---
id: project_admin
title: Project Admin
sidebar_position: 3
---

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

### Before you start

**1.** [Register](/docs/guides/register.md) to Accounting Service.<br/>
**2.** [Contact](/docs/authorization/assigning_roles.md) the system administrator, to assign you the Project Admin role upon the project you want.

In the Accounting Service, the **_project_admin_** role is the main role for managing a Project. This role permits the client to perform any operation, on a specific Project.

Below we describe the actions a **_project_admin_** can either perform through the Accounting User Interface or a simple HTTP request.

## Actions

---

:::info VIEW all Projects that are assigned to you

<Tabs>
  <TabItem value="ui" label="User Interface">To perform this action via the website, please click <a href="https://accounting.eosc-portal.eu/projects">here</a>.</TabItem>
  <TabItem value="http" label="HTTP Request">To syntax the HTTP request, please visit the corresponding <a href="https://argoeu.github.io/argo-accounting/docs/api/project#get---fetch-all-projects">document.</a>.</TabItem>
</Tabs>

:::

:::info ASSOCIATE one or more Providers with a specific Project

<Tabs>
  <TabItem value="ui" label="User Interface">To perform this action via the website, please click <a href="https://accounting.eosc-portal.eu/projects">here</a> and follow the provided <a href="https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/project#associate-providers-with-a-specific-project">instructions</a>.</TabItem>
  <TabItem value="http" label="HTTP Request">To syntax the HTTP request, please visit the corresponding <a href="https://argoeu.github.io/argo-accounting/docs/api/project#post---associate-providers-with-a-specific-project">document</a>.</TabItem>
</Tabs>

:::

:::info DISSOCIATE Provider from a specific Project

<Tabs>
  <TabItem value="ui" label="User Interface">To perform this action via the website, please click <a href="https://accounting.eosc-portal.eu/projects">here</a> and follow the provided <a href="https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/project/#dissociate-providers-from-a-specific-project">instructions</a>.</TabItem>
  <TabItem value="http" label="HTTP Request">To syntax the HTTP request, please visit the corresponding <a href="https://argoeu.github.io/argo-accounting/docs/api/project#post---dissociate-providers-from-a-project">document</a>.</TabItem>
</Tabs>

:::

:::info Create a new Installation on a specific Project

<Tabs>
  <TabItem value="ui" label="User Interface">To perform this action via the website, please click <a href="https://accounting.eosc-portal.eu/installations">here</a> and follow the provided <a href="https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/installation#create-a-new-installation">instructions</a>.</TabItem>
  <TabItem value="http" label="HTTP Request">To syntax the HTTP request, please visit the corresponding <a href="https://argoeu.github.io/argo-accounting/docs/api/installation#post---create-a-new-installation">document</a>.</TabItem>
</Tabs>

:::

:::info Update the Installations belonging to a specific Project

<Tabs>
  <TabItem value="ui" label="User Interface">To perform this action via the website, please click <a href="https://accounting.eosc-portal.eu/installations">here</a> and follow the provided <a href="https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/installation#update-an-existing-installation">instructions</a>.</TabItem>
  <TabItem value="http" label="HTTP Request">To syntax the HTTP request, please visit the corresponding <a href="https://argoeu.github.io/argo-accounting/docs/api/installation#patch---update-an-existing-installation">document</a>.</TabItem>
</Tabs>

:::

:::info Delete the Installations belonging to a specific Project

<Tabs>
  <TabItem value="ui" label="User Interface">To perform this action via the website, please click <a href="https://accounting.eosc-portal.eu/installations">here</a> and follow the provided <a href="https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/installation#delete-an-existing-installation">instructions</a>.</TabItem>
  <TabItem value="http" label="HTTP Request">To syntax the HTTP request, please visit the corresponding <a href="https://argoeu.github.io/argo-accounting/docs/api/installation#delete---delete-an-existing-installation">document</a>.</TabItem>
</Tabs>

:::

:::info Collect Metrics from a specific Project

<Tabs>
  <TabItem value="ui" label="User Interface">To perform this action via the website, please click <a href="https://accounting.eosc-portal.eu/projects">here</a> and follow the provided <a href="https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/project#collect-metrics-from-specific-project">instructions</a>.</TabItem>
  <TabItem value="http" label="HTTP Request">To syntax the HTTP request, please visit the corresponding <a href="https://argoeu.github.io/argo-accounting/docs/api/collect_metrics#get---collecting-metrics-from-specific-project">document</a>.</TabItem>
</Tabs>

:::

:::info Add a new Metric to a specific Project

<Tabs>
  <TabItem value="info" label="Info">You can add Metrics to all the Installations belonging to the Project you have been granted as project admin.</TabItem>
  <TabItem value="ui" label="User Interface">To perform this action via the website, please click <a href="https://accounting.eosc-portal.eu/projects">here</a> and follow the provided <a href="https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/project/#add-a-new-metric">instructions</a>.</TabItem>
  <TabItem value="http" label="HTTP Request">To syntax the HTTP request, please visit the corresponding <a href="https://argoeu.github.io/argo-accounting/docs/api/metric#post---create-a-new-metric">document</a>.</TabItem>
</Tabs>

:::

:::info Update a Metric belonging to a specific Project

<Tabs>
  <TabItem value="info" label="Info">You can edit all Metrics belonging to the Project you have been granted as project admin.</TabItem>
  <TabItem value="ui" label="User Interface">To perform this action via the website, please click <a href="https://accounting.eosc-portal.eu/projects">here</a> and follow the provided <a href="https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/project/#update-an-existing-metric">instructions</a>.</TabItem>
  <TabItem value="http" label="HTTP Request">To syntax the HTTP request, please visit the corresponding <a href="https://argoeu.github.io/argo-accounting/docs/api/metric#patch---update-an-existing-metric">document</a>.</TabItem>
</Tabs>

:::

:::info Delete a Metric belonging to a specific Project

<Tabs>
  <TabItem value="info" label="Info">You can delete all Metrics belonging to the Project you have been granted as project admin.</TabItem>
  <TabItem value="ui" label="User Interface">To perform this action via the website, please click <a href="https://accounting.eosc-portal.eu/projects">here</a> and follow the provided <a href="https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/project/#delete-an-existing-metric">instructions</a>.</TabItem>
  <TabItem value="http" label="HTTP Request">To syntax the HTTP request, please visit the corresponding <a href="https://argoeu.github.io/argo-accounting/docs/api/metric#delete---delete-an-existing-metric">document</a>.</TabItem>
</Tabs>

:::

:::info Manage Metric Definitions

<Tabs>
  <TabItem value="info" label="Info">As a project admin, you can create new Metric Definitions and delete/update your created ones.</TabItem>
  <TabItem value="ui" label="User Interface">To manage them via the website, please click <a href="https://accounting.eosc-portal.eu/metrics-definitions">here</a> and follow the provided <a href="https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/metric_definition">instructions</a>.</TabItem>
  <TabItem value="http" label="HTTP Request">To manage them via the Accounting Service, please visit the corresponding <a href="https://argoeu.github.io/argo-accounting/docs/api/metric_definition">document</a>.</TabItem>
</Tabs>

:::

:::info Manage Providers

<Tabs>
  <TabItem value="info" label="Info">As a project admin, you can create new Providers and delete/update your created ones.</TabItem>
  <TabItem value="ui" label="User Interface">To manage them via the website, please click <a href="https://accounting.eosc-portal.eu/providers">here</a> and follow the provided <a href="https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/provider">instructions</a>.</TabItem>
  <TabItem value="http" label="HTTP Request">To manage them via the Accounting Service, please visit the corresponding <a href="https://argoeu.github.io/argo-accounting/docs/api/provider">document</a>.</TabItem>
</Tabs>

:::

:::info Manage Unit Types

<Tabs>
  <TabItem value="info" label="Info">As a project admin, you can create new Unit Types and delete/update your created ones.</TabItem>
  <TabItem value="ui" label="User Interface">To manage them via the website, please click <a href="https://accounting.eosc-portal.eu/unit-types">here</a> and follow the provided <a href="https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/unit_type">instructions</a>.</TabItem>
  <TabItem value="http" label="HTTP Request">To manage them via the Accounting Service, please visit the corresponding <a href="https://argoeu.github.io/argo-accounting/docs/api/unit_type">document</a>.</TabItem>
</Tabs>

:::

:::info Manage Metric Types

<Tabs>
  <TabItem value="info" label="Info">As a project admin, you can create new Metric Types and delete/update your created ones.</TabItem>
  <TabItem value="ui" label="User Interface">To manage them via the website, please click <a href="https://accounting.eosc-portal.eu/metric-types">here</a> and follow the provided <a href="https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/metric_type">instructions</a>.</TabItem>
  <TabItem value="http" label="HTTP Request">To manage them via the Accounting Service, please visit the corresponding <a href="https://argoeu.github.io/argo-accounting/docs/api/metric_type">document</a>.</TabItem>
</Tabs>

:::

Please note that you can perform all the actions on [Providers](/docs/guides/provider_admin.md) and [Installations](/docs/guides/installation_admin.md) belonging to the Project you have been granted as a **_project_admin_**.

---
