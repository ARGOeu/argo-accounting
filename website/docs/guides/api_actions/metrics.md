---
id: metric
title: Manage Metrics
sidebar_position: 4
---


This is a guide that refers to Metrics. Metrics are measures of quantitative assessment, commonly used for assessing, comparing, and tracking usage or performance of a service. They are the main indicators.<br/>

If you are permitted to act on one or more Installations (see <a href="https://argoeu.github.io/argo-accounting/docs/api/installation#before-you-start">here</a>), you can also act on the Metrics.Via this guide you can see all the options you have.

### ASSIGN Metrics,to the Installation
<details>
You can assign one or more Metrics to the Installation.Apply a request to the Accounting Service API.In order to successfully assign a Metric you need to provide a MetricDefinition id,a start and an end period timestamp and a value.
<b> For more details,how to syntax the request,see <a href="https://argoeu.github.io/argo-accounting/docs/api/metric#post---create-a-new-metric">here</a></b>
</details>

### GET details of specific Metric,assigned to the Installation
<details>
You can get the details of a specific Metric,assigned to the Installation.Apply a request to the Accounting Service API.
<b> For more details,how to syntax the request,see <a href="https://argoeu.github.io/argo-accounting/docs/api/metric#get---fetch-an-existing-metric">here</a></b>
</details>

### UPDATE a specific Metric,assigned to the Installation
<details>
You can update the details of a Metrics, assigned to the Installation. Apply a request to the Accounting Service API.In order to successfully update a specific Metric you need to provide the new properties' values of the Metric.
<b> For more details,how to syntax the request,see <a href="https://argoeu.github.io/argo-accounting/docs/api/metric#patch---update-an-existing-metric">here</a></b>
</details>

### DELETE a specific Metric,assigned to the Installation
<details>
You can delete a Metric,assigned to the Installation. Apply a request to the Accounting Service API.
<b> For more details,how to syntax the request,see <a href="https://argoeu.github.io/argo-accounting/docs/api/metric#delete---delete-an-existing-metric">here</a></b>
</details>

### FETCH all Metrics,assigned to the Installation
<details>
You can fetch all Metrics,assigned to the Installation.Apply a request to the Accounting Service API.
<b> For more details,how to syntax the request,see <a href="https://argoeu.github.io/argo-accounting/docs/api/collect_metrics#get---collecting-metrics-from-specific-installation">here</a></b>
</details>

### SEARCH Metrics,assigned to the Installation
<details>
You can search for specific Metric/Metrics assigned to the Installation,that matches one or more criteria.You can define search criteria on each field of the <b><a href="https://argoeu.github.io/argo-accounting/docs/api/metric">Metrics Collection</a></b> or a combination of search criteria on more than one fields. You can search for Metrics by Metric Definition id, value, period or a combination of them. 
Apply a request to the Accounting Service API.You need to provide the search criteria in a specific
<b><a href="https://argoeu.github.io/argo-accounting/docs/guides/search-filter">  syntax</a></b> . 
<b> For more details,how to syntax the request,see <a href="https://argoeu.github.io/argo-accounting/docs/api/metric#post---search-for-metrics">here</a></b>
</details>

