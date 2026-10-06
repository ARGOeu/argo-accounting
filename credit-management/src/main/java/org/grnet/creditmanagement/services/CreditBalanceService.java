package org.grnet.creditmanagement.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.grnet.creditmanagement.dtos.BasisAllocationDto;
import org.grnet.creditmanagement.dtos.CreditBalanceResponseDto;
import org.grnet.creditmanagement.repositories.CreditAllocationRepository;
import org.grnet.creditmanagement.repositories.ExternalEntityLookupRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

@ApplicationScoped
public class CreditBalanceService {

    @Inject
    ExternalEntityLookupRepository externalEntityLookupRepository;

    @Inject
    CreditAllocationRepository creditAllocationRepository;

    @Inject
    CreditUsageReportService creditUsageReportService;

    /**
     * Balance as of the end of a calendar date 'atDate' (defaults to today
     * if null, in which case the result is not treated as a snapshot).
     * 'atDate' is resolved to the start of the day AFTER it (00:00:00 UTC),
     * so the entire 'atDate' day is included in the window.
     *
     * The basis is the most recently started allocation whose valid_from is
     * <= the resolved instant, found by looking backwards.
     * - If it is still in effect (resolved instant < valid_to): budget is
     *   its total_credits, consumption is counted from its valid_from.
     * - If it has expired: budget is 0 and only consumption after its
     *   valid_to counts (consumption during its period used the budget
     *   that was valid then; the wallet closes at valid_to).
     * - If there is none: budget is 0 and all recorded consumption counts.
     */
    public CreditBalanceResponseDto getBalance(String projectId, String groupId, LocalDate atDate) {

        var isSnapshot = atDate != null;

        var resolvedAtDate = atDate != null ? atDate : LocalDate.now(ZoneOffset.UTC);
        var effectiveAt = resolvedAtDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        if (!externalEntityLookupRepository.projectExists(projectId)) {
            throw new NotFoundException("Project not found: " + projectId);
        }

        var basisAllocation = creditAllocationRepository
                .findLatestStartingOnOrBefore(projectId, groupId, effectiveAt)
                .orElse(null);

        var allocationInEffect = basisAllocation != null
                && effectiveAt.isBefore(basisAllocation.getValidTo());

        Instant windowStart;
        double allocatedCredits;
        BasisAllocationDto basisDto = null;

        if (basisAllocation != null) {

            basisDto = new BasisAllocationDto();
            basisDto.allocationId = basisAllocation.getId();
            basisDto.validFrom = basisAllocation.getValidFrom();
            basisDto.validTo = basisAllocation.getValidTo();

            if (allocationInEffect) {
                windowStart = basisAllocation.getValidFrom();
                allocatedCredits = basisAllocation.getTotalCredits();
            } else {
                windowStart = basisAllocation.getValidTo();
                allocatedCredits = 0.0;
            }

        } else {
            windowStart = Instant.EPOCH;
            allocatedCredits = 0.0;
        }

        var usageReport = creditUsageReportService.generateReport(
                projectId, windowStart, effectiveAt, null, null, null, groupId);

        var consumedCredits = usageReport.installations.stream()
                .flatMap(installation -> installation.metrics.stream())
                .mapToDouble(metric -> metric.totalCredits)
                .sum();

        var balance = allocatedCredits - consumedCredits;

        String reason = null;

        if (balance < 0) {
            reason = allocationInEffect ? "allocated credits exhausted" : "no allocation policy in effect";
        }

        var response = new CreditBalanceResponseDto();
        response.projectId = projectId;
        response.groupId = groupId;
        response.at = effectiveAt;
        response.isSnapshot = isSnapshot;
        response.warning = isSnapshot
                ? "This balance reflects a snapshot as of the specified point in time and says nothing about the current (true) balance."
                : null;
        response.allocationInEffect = allocationInEffect;
        response.basisAllocation = basisDto;
        response.allocatedCredits = allocatedCredits;
        response.consumedCredits = consumedCredits;
        response.balance = balance;
        response.reason = reason;
        response.installations = usageReport.installations;

        return response;
    }
}