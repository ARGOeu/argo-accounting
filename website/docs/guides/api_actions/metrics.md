---
id: metric
title: Manage Metrics
sidebar_position: 5
---

# Manage Metrics

This is a guide that refers to Metrics. Metrics are measures of quantitative
assessment, commonly used for assessing, comparing, and tracking usage or
performance of a service. They are the main indicators.

If you are permitted to act on one or more Installations (see [here](../../api/installation#before-you-start)),
you can also act on the Metrics. Via this guide, you can see all the
options you have.

## Before you start

You can manage Installation's metrics under a specific Provider and Project.

**1.** [Register](/docs/guides/register.md) to Accounting Service.

**2.** [Contact](mailto:argo@grnet.gr) the system administrator to assign you one or more [roles](/docs/authorization/accounting_system_roles.md) on the Project, or Provider, or Installation.

## ASSIGN Metrics to the Installation

> You can assign one or more Metrics to the Installation. Apply a request to
the Accounting Service API. In order to successfully assign a Metric, you
need to provide a MetricDefinition id, a start and an end period timestamp,
and a value.

📝 **For more details on how to syntax the request, see** [here](../../api/metric#post---create-a-new-metric).

## GET details of a specific Metric assigned to the Installation

> You can get the details of a specific Metric, assigned to the
Installation. Apply a request to the Accounting Service API.

📝 **For more details on how to syntax the request, see** [here](../../api/metric#get---fetch-an-existing-metric).

## UPDATE a specific Metric assigned to the Installation

> You can update the details of a Metric assigned to the Installation.
Apply a request to the Accounting Service API. In order to successfully
update a specific Metric, you need to provide the new properties' values
of the Metric.

📝 **For more details on how to syntax the request, see** [here](../../api/metric#patch---update-an-existing-metric).

## DELETE a specific Metric assigned to the Installation

> You can delete a Metric assigned to the Installation. Apply a request to
the Accounting Service API.

📝 **For more details on how to syntax the request, see** [here](../../api/metric#delete---delete-an-existing-metric).

## FETCH all Metrics assigned to the Installation

> You can fetch all Metrics assigned to the Installation. Apply a request to
the Accounting Service API.

📝 **For more details on how to syntax the request, see** [here](../../api/collect_metrics#get---collecting-metrics-from-specific-installation).

## SEARCH Metrics assigned to the Installation

> You can search for specific Metric/Metrics assigned to the Installation
that match one or more criteria. You can define search criteria on each field
of the **[Metrics Collection](../../api/metric)**
or a combination of search criteria on more than one field. You can search
for Metrics by Metric Definition id, value, period, or a combination of them.

Apply a request to the Accounting Service API. You need to provide the search
criteria in a specific
**[syntax](./search-filter)**.

📝 **For more details on how to syntax the request, see** [here](../../api/metric#post---search-for-metrics).
