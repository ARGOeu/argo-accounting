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
     * Balance as of a single point in time 'at' (defaults to now if null,
     * in which case the result is not treated as a snapshot).
     *
     * The basis is always the most recently started allocation whose
     * valid_from is <= at ("the wallet resets with each new policy"), found
     * by looking backwards regardless of whether that allocation's own
     * valid_to has already passed. Consumption is summed from that
     * allocation's valid_from up to 'at'; if no such allocation exists at
     * all, consumption is summed from the beginning of recorded history.
     */
    public CreditBalanceResponseDto getBalance(String projectId, String groupId, LocalDate atDate) {

        if (!externalEntityLookupRepository.projectExists(projectId)) {
            throw new NotFoundException("Project not found: " + projectId);
        }

        var isSnapshot = atDate != null;

        var resolvedAtDate = atDate != null ? atDate : LocalDate.now(ZoneOffset.UTC);
        var effectiveAt = resolvedAtDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        var basisAllocation = creditAllocationRepository
                .findLatestStartingOnOrBefore(projectId, groupId, effectiveAt)
                .orElse(null);

        Instant windowStart;
        double allocatedCredits;
        BasisAllocationDto basisDto = null;

        if (basisAllocation != null) {

            windowStart = basisAllocation.getValidFrom();
            allocatedCredits = basisAllocation.getTotalCredits();

            basisDto = new BasisAllocationDto();
            basisDto.allocationId = basisAllocation.getId();
            basisDto.validFrom = basisAllocation.getValidFrom();
            basisDto.validTo = basisAllocation.getValidTo();

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

            var policyCurrentlyInEffect = basisAllocation != null
                    && !effectiveAt.isBefore(basisAllocation.getValidFrom())
                    && effectiveAt.isBefore(basisAllocation.getValidTo());

            reason = policyCurrentlyInEffect ? "allocated credits exhausted" : "no allocation policy in effect";
        }

        var response = new CreditBalanceResponseDto();
        response.projectId = projectId;
        response.groupId = groupId;
        response.at = effectiveAt;
        response.isSnapshot = isSnapshot;
        response.warning = isSnapshot
                ? "This balance reflects a snapshot as of the specified point in time and says nothing about the current (true) balance."
                : null;
        response.basisAllocation = basisDto;
        response.allocatedCredits = allocatedCredits;
        response.consumedCredits = consumedCredits;
        response.balance = balance;
        response.reason = reason;
        response.installations = usageReport.installations;

        return response;
    }
}