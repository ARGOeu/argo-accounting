---
id: installation
title: Manage Installations
sidebar_position: 2
---

This is a guide that refers to an Installation. An Installation refers to a specific instance or part of a resource/service that is allocated to a specific Project by one Provider. If you are permitted to act on one or ore Installations, via this guide you can see all the options you have .

### Before you start

You can manage an Installation assigned to a specific Project and Provider.<br/>
**1.** Register to the Accounting Service.
**2.** Contact the administrator of the Project or the administrator of the Project's Provider,that this Installation is associated with,to assign you one or more roles on the Installation.

**ΝΟΤΕ** <br/>
In the Accounting Service, the **_installation_admin_** role is the main role for managing an Installation.This role permits the user to perform any operation,on a specific Installation.
In case the user is assigned with any other role, he can operate according to the role's permissions.

## OPERATIONS 

--- 

### GET Installation's details 
<details>
You can get Installation's details.Apply a request to the Accounting Service API.
<b> For more details,how to syntax the request,see <a href="https://argoeu.github.io/argo-accounting/docs/api/installation#get---fetch-an-existing-installation">here</a></b>
</details>


### UPDATE the Installation
<details>
You can update the Installation.Apply a request to the Accounting Service API,providing the new values of the Installation's properties.
<b> For more details,how to syntax the request,see <a href="https://argoeu.github.io/argo-accounting/docs/api/installation#patch---update-an-existing-installation">here</a></b>
</details>

### DELETE the Installation
<details>
You can delete the Installation.Apply a request to the Accounting Service API.If Metrics are assigned to the Installation,no DELETE action can take place.In this case,you need to delete all the assigned Metrics.
<b> For more details,how to syntax the request,see <a href="https://argoeu.github.io/argo-accounting/docs/api/installation#delete---delete-an-existing-installation">here</a></b>
</details>

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

---

