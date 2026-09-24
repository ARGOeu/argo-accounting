package org.grnet.creditmanagement.services;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.grnet.creditmanagement.dtos.CreditUsageReportResponseDto;
import org.grnet.creditmanagement.dtos.InstallationReportDto;
import org.grnet.creditmanagement.dtos.MetricReportDto;
import org.grnet.creditmanagement.dtos.ReportFiltersDto;
import org.grnet.creditmanagement.dtos.SegmentDto;
import org.grnet.creditmanagement.entities.RatingPolicyEntity;
import org.grnet.creditmanagement.repositories.ExternalEntityLookupRepository;
import org.grnet.creditmanagement.repositories.ExternalMetricRepository;
import org.grnet.creditmanagement.repositories.RatingPolicyRepository;
import org.grnet.creditmanagement.usage.MetricEvent;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

@ApplicationScoped
public class CreditUsageReportService {

    @Inject
    ExternalEntityLookupRepository externalEntityLookupRepository;

    @Inject
    ExternalMetricRepository metricRepository;

    @Inject
    RatingPolicyRepository ratingPolicyRepository;

    /**
     * Calendar-date entry point (used directly by the credit usage report
     * endpoint): from/to are both-inclusive calendar dates, converted to a
     * half-open Instant window and delegated to the Instant-based variant.
     */
    public CreditUsageReportResponseDto generateReport(String projectId,
                                                       LocalDate fromDate,
                                                       LocalDate toDate,
                                                       String installationIdFilter,
                                                       String metricDefinitionIdFilter,
                                                       String userId,
                                                       String groupId) {

        if (fromDate == null || toDate == null) {
            throw new BadRequestException("Both 'from' and 'to' query parameters are required.");
        }

        if (fromDate.isAfter(toDate)) {
            throw new BadRequestException("'from' must not be after 'to'.");
        }

        var from = fromDate.atStartOfDay(ZoneOffset.UTC).toInstant();
        var to = toDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        return generateReport(projectId, from, to, installationIdFilter, metricDefinitionIdFilter, userId, groupId);
    }

    /**
     * Instant-windowed entry point (used by the credit balance endpoint,
     * whose window is anchored to an allocation's valid_from and an
     * arbitrary point-in-time 'at', not calendar dates). from == to is
     * allowed (an empty window, e.g. querying exactly at an allocation's
     * own start); from > to is rejected.
     */
    public CreditUsageReportResponseDto generateReport(String projectId,
                                                       Instant from,
                                                       Instant to,
                                                       String installationIdFilter,
                                                       String metricDefinitionIdFilter,
                                                       String userId,
                                                       String groupId) {

        if (from == null || to == null) {
            throw new BadRequestException("Both 'from' and 'to' are required.");
        }

        if (from.isAfter(to)) {
            throw new BadRequestException("'from' must not be after 'to'.");
        }

        if (!externalEntityLookupRepository.projectExists(projectId)) {
            throw new NotFoundException("Project not found: " + projectId);
        }

        if (metricDefinitionIdFilter != null && !externalEntityLookupRepository.metricDefinitionExists(metricDefinitionIdFilter)) {
            throw new NotFoundException("Metric Definition not found: " + metricDefinitionIdFilter);
        }

        var installationRefs = externalEntityLookupRepository.listInstallationsUnderProject(projectId, installationIdFilter);

        if (installationIdFilter != null && installationRefs.isEmpty()) {
            throw new NotFoundException("There is no Installation with the following id: " + installationIdFilter
                    + " under project " + projectId);
        }

        var installationReports = new ArrayList<InstallationReportDto>();

        for (var ref : installationRefs) {

            var metricDefinitionIds = resolveMetricDefinitionIds(
                    ref.installationId(), from, to, metricDefinitionIdFilter, userId, groupId);

            if (metricDefinitionIds.isEmpty()) {
                continue;
            }

            var metricReports = metricDefinitionIds.stream()
                    .map(metricDefinitionId -> buildMetricReport(ref.installationId(), metricDefinitionId, from, to, userId, groupId))
                    .toList();

            var installationDto = new InstallationReportDto();
            installationDto.installationId = ref.installationId();
            installationDto.providerId = ref.providerId();
            installationDto.metrics = metricReports;

            installationReports.add(installationDto);
        }

        var response = new CreditUsageReportResponseDto();
        response.projectId = projectId;
        response.from = from;
        response.to = to;

        response.filters = new ReportFiltersDto();
        response.filters.installationId = installationIdFilter;
        response.filters.metricDefinitionId = metricDefinitionIdFilter;
        response.filters.userId = userId;
        response.filters.groupId = groupId;

        response.installations = installationReports;

        return response;
    }

    private List<String> resolveMetricDefinitionIds(String installationId,
                                                    Instant from,
                                                    Instant to,
                                                    String metricDefinitionIdFilter,
                                                    String userId,
                                                    String groupId) {

        var fromEvents = metricRepository.findDistinctMetricDefinitionIds(
                installationId, from, to, metricDefinitionIdFilter, userId, groupId);

        var fromPolicies = ratingPolicyRepository.findDistinctMetricDefinitionIds(
                installationId, metricDefinitionIdFilter);

        var combined = new LinkedHashSet<String>();
        combined.addAll(fromEvents);
        combined.addAll(fromPolicies);

        return combined.stream().toList();
    }

    private MetricReportDto buildMetricReport(String installationId,
                                              String metricDefinitionId,
                                              Instant from,
                                              Instant to,
                                              String userId,
                                              String groupId) {

        var policies = ratingPolicyRepository.findAllOrderedByValidFrom(installationId, metricDefinitionId);

        var breakpoints = policies.stream()
                .map(RatingPolicyEntity::getValidFrom)
                .filter(validFrom -> validFrom.isAfter(from) && validFrom.isBefore(to))
                .distinct()
                .sorted()
                .toList();

        var boundaries = new ArrayList<Instant>();
        boundaries.add(from);
        boundaries.addAll(breakpoints);
        boundaries.add(to);

        var events = metricRepository.findOverlappingEvents(installationId, metricDefinitionId, from, to, userId, groupId);

        var segments = new ArrayList<SegmentDto>();
        double totalValue = 0;
        double totalCredits = 0;

        for (int i = 0; i < boundaries.size() - 1; i++) {

            var segmentStart = boundaries.get(i);
            var segmentEnd = boundaries.get(i + 1);

            if (segmentStart.equals(segmentEnd)) {
                continue;
            }

            var applicablePolicy = policies.stream()
                    .filter(policy -> !policy.getValidFrom().isAfter(segmentStart))
                    .max(Comparator.comparing(RatingPolicyEntity::getValidFrom))
                    .orElse(null);

            var segmentValue = prorateEventsForSegment(events, segmentStart, segmentEnd);

            var segmentCredits = applicablePolicy != null ? segmentValue * applicablePolicy.getRate() : 0.0;

            var segment = new SegmentDto();
            segment.from = segmentStart;
            segment.to = segmentEnd;
            segment.ratingPolicyId = applicablePolicy != null ? applicablePolicy.getId() : null;
            segment.rate = applicablePolicy != null ? applicablePolicy.getRate() : null;
            segment.totalValue = segmentValue;
            segment.credits = segmentCredits;

            segments.add(segment);

            totalValue += segmentValue;
            totalCredits += segmentCredits;
        }

        var metricDto = new MetricReportDto();
        metricDto.metricDefinitionId = metricDefinitionId;
        metricDto.segments = segments;
        metricDto.totalValue = totalValue;
        metricDto.totalCredits = totalCredits;

        return metricDto;
    }

    private double prorateEventsForSegment(List<MetricEvent> events, Instant segmentStart, Instant segmentEnd) {

        double sum = 0;

        for (var event : events) {

            var overlapStart = event.start().isAfter(segmentStart) ? event.start() : segmentStart;
            var overlapEnd = event.end().isBefore(segmentEnd) ? event.end() : segmentEnd;

            if (overlapStart.isBefore(overlapEnd)) {

                var eventDurationMillis = event.end().toEpochMilli() - event.start().toEpochMilli();

                if (eventDurationMillis <= 0) {
                    continue;
                }

                var overlapMillis = overlapEnd.toEpochMilli() - overlapStart.toEpochMilli();
                var fraction = (double) overlapMillis / eventDurationMillis;

                sum += event.value() * fraction;
            }
        }

        return sum;
    }
}
