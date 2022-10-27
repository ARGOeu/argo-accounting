---
id: installation
title: Manage Installations
sidebar_position: 1
---
### Before you start. 


You can manage an installation assigned to a specific project and provider.
Firstly, you need to be registered to the accounting-system and then you need to contact the administrator of the project or the administrator of the project's provider, that this installation is associated with, to assign you one or more roles on the installation

**NOTE** In accounting sytem, already exists the **_installation_admin_** role , that permits the user , assigned with this role, to perform any operation on a specific Installation. In case, the user is assigned to another role, he can act according to the role's permissions.

## OPERATIONS 

--- 

### READ the installation 
<details>
You can display the installation's details. Apply a request to the api. 
<b> For more details ,how to syntax the request, see <a href="https://argoeu.github.io/argo-accounting/docs/api/installation#get---fetch-an-existing-installation">here</a></b>
</details>


### UPDATE the installation
<details>
You can update the installation's details. Apply a request to the api, providing the new values of the installation's properties
<b> For more details ,how to syntax the request, see <a href="https://argoeu.github.io/argo-accounting/docs/api/installation#patch---update-an-existing-installation">here</a></b>
</details>

### DELETE the installation
<details>
You can delete the installation.Apply a request to the api. If metrics are assigned to the installation , no DELETE action can take place. In this case, you need to delete all the assigned metrics. 
<b> For more details, how to syntax the request, see <a href="https://argoeu.github.io/argo-accounting/docs/api/installation#delete---delete-an-existing-installation">here</a></b>
</details>

### ASSIGN metrics to the installation
<details>
You can assign one or more metrics to the installation. Apply a request to the api. In order to successfully assign a metric you need to provide a metric definition id, a start and an end period timestamp and a value.
<b> For more details , how to syntax the request, see <a href="https://argoeu.github.io/argo-accounting/docs/api/metric#post---create-a-new-metric">here</a></b>
</details>

### READ a specific metric assigned to the installation
<details>
You can display the details of a metric assigned to the installation. Apply a request to the api
<b> For more details ,how to syntax the request, see <a href="https://argoeu.github.io/argo-accounting/docs/api/metric#get---fetch-an-existing-metric">here</a></b>
</details>

### UPDATE a specific metric belonging to the installation
<details>
You can update an assigned metrics's details. Apply a request to the api. In order to successfully update a specific metric you need to provide the new properties' values of the metric.
<b> For more details ,how to syntax the request, see <a href="https://argoeu.github.io/argo-accounting/docs/api/metric#patch---update-an-existing-metric">here</a></b>
</details>

### DELETE a specific metric belonging to the installation
<details>
You can delete an assigned metric. Apply a request to the api.
<b> For more details ,how to syntax the request, see <a href="https://argoeu.github.io/argo-accounting/docs/api/metric#delete---delete-an-existing-metric">here</a></b>
</details>

### COLLECT metrics assigned to an installation
<details>
You can collect and read  all the metrics that are assigned to the installation. Apply a request to the api.
<b> For more details ,how to syntax the request, see <a href="https://argoeu.github.io/argo-accounting/docs/api/collect_metrics#get---collecting-metrics-from-specific-installation">here</a></b>
</details>

### SEARCH metrics assigned to an installation
<details>
You can search for specific metric/metrics assigned to an installation, that matches one or more criteria. Apply a request to the api. You need to provide the search criteria in a 
<b><a href="https://argoeu.github.io/argo-accounting/docs/guides/search-filter"> specific syntax</a></b> . 
<b> For more details ,how to syntax the request, see <a href="">here</a></b>
</details>

---
