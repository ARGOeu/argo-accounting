---
id: metric_definition
title: Manage Metric Definitions
sidebar_position: 6
---

# Manage Metric Definitions

This is a guide that refers to a Metric Definition.
A Metric Definition is the way to represent and describe the type of the
metrics. A Metric Definition consists of the metadata describing a Metric.
If you are registered on Accounting System, via this guide you can see all
the options you have to act on Metric Definitions.

## Before you start

You can manage Metric Definitions.

**1.** Register to Accounting Service.

## OPERATIONS

---

### FETCH all Metric Definitions, existing in Accounting Service

Any user, registered to Accounting System, can access the Metric Definitions
that exist in Accounting Service. Apply a request to the API.

> 📝 **For more details on how to format the request, see** [here](https://argoeu.github.io/argo-accounting/docs/api/metric_definition/#get----fetch-all-metric-definitions).

### GET Metric Definitions' details

Any user, registered to Accounting System, can get the details of a specific
Metric Definition, existing in the accounting system. Apply a request to the API.

> 📝 **For more details on how to format the request, see** [here](https://argoeu.github.io/argo-accounting/docs/api/metric_definition#get---fetch-a-metric-definition).

### CREATE a Metric Definition

If a **project_admin** role is assigned to you, you can create a Metric
Definition. Apply a request to the API.

> 📝 **For more details on how to format the request, see** [here](https://argoeu.github.io/argo-accounting/docs/api/metric_definition#post---create-a-metric-definition).

### UPDATE a Metric Definition

If a **project_admin** role is assigned to you and you are the creator
of the Metric Definition, you can update it. Apply a request to the API.

> 📝 **For more details on how to format the request, see** [here](https://argoeu.github.io/argo-accounting/docs/api/metric_definition#patch---update-a-metric-definition).

### DELETE a Metric Definition

If a **project_admin** role is assigned to you and you are the creator
of the Metric Definition, you can delete it. Apply a request to the API.

> 📝 **For more details on how to format the request, see** [here](https://argoeu.github.io/argo-accounting/docs/api/metric_definition/#delete----delete-a-metric-definition).

### SEARCH Metric Definitions

Any user, registered to Accounting System, can search for specific Metric
Definitions that match one or more criteria. You can define search criteria
for each field of the **[Metric Definition Collection](https://argoeu.github.io/argo-accounting/docs/api/metric_definition)**
or a combination of criteria across multiple fields. You can search by metric
definition's unit type, metric type, metric name, metric description, or a
combination of them.
Apply a request to the Accounting Service API. You need to provide the search
criteria in a specific **[syntax](https://argoeu.github.io/argo-accounting/docs/guides/search-filter)**.

> 📝 **For more details on how to format the request, see** [here](https://argoeu.github.io/argo-accounting/docs/api/metric_definition#post---search-for-metric-definitions).

---
