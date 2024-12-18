---
id: provider_admin
title: Provider Admin
sidebar_position: 4
---

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

### Before you start

You can manage a Provider assigned to a specific Project.<br/>

**1.** [Register](/docs/guides/register.md) to Accounting Service.<br/>
**2.** Contact the administrator of the Project, that this Provider is associated with, to assign you the Provider Admin role upon the provider you want.

In the Accounting Service,the **_provider_admin_** role is the main role for managing a Provider belonging to a Project. This role permits the user to perform any operation, on a specific Provider.

Below we describe the actions a **_provider_admin_** can either perform through the Accounting User Interface or a simple HTTP request.

## Actions

---

:::info View all the Providers you have access to

<Tabs>
  <TabItem value="ui" label="User Interface">To perform this action via the website, please click <a href="https://accounting.eosc-portal.eu/myProviders">here</a>.</TabItem>
  <TabItem value="http" label="HTTP Request">It's currently under development.</TabItem>
</Tabs>

:::

:::info Create a new Installation on a specific Provider

<Tabs>
  <TabItem value="ui" label="User Interface">To perform this action via the website, please click <a href="https://accounting.eosc-portal.eu/installations">here</a> and follow the provided <a href="https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/installation#create-a-new-installation">instructions</a>.</TabItem>
  <TabItem value="http" label="HTTP Request">To syntax the HTTP request, please visit the corresponding <a href="https://argoeu.github.io/argo-accounting/docs/api/installation#post---create-a-new-installation">document</a>.</TabItem>
</Tabs>

:::

:::info Update the Installations belonging to a specific Provider

<Tabs>
  <TabItem value="ui" label="User Interface">To perform this action via the website, please click <a href="https://accounting.eosc-portal.eu/installations">here</a> and follow the provided <a href="https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/installation#update-an-existing-installation">instructions</a>.</TabItem>
  <TabItem value="http" label="HTTP Request">To syntax the HTTP request, please visit the corresponding <a href="https://argoeu.github.io/argo-accounting/docs/api/installation#patch---update-an-existing-installation">document</a>.</TabItem>
</Tabs>

:::

:::info Delete the Installations belonging to a specific Provider

<Tabs>
  <TabItem value="ui" label="User Interface">To perform this action via the website, please click <a href="https://accounting.eosc-portal.eu/installations">here</a> and follow the provided <a href="https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/installation#delete-an-existing-installation">instructions</a>.</TabItem>
  <TabItem value="http" label="HTTP Request">To syntax the HTTP request, please visit the corresponding <a href="https://argoeu.github.io/argo-accounting/docs/api/installation#delete---delete-an-existing-installation">document</a>.</TabItem>
</Tabs>

:::

:::info Collect Metrics from a specific Provider

<Tabs>
  <TabItem value="ui" label="User Interface">To perform this action via the website, please click <a href="https://accounting.eosc-portal.eu/myProviders">here</a> and follow the provided <a href="https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/my_providers#collect-metrics-from-specific-provider">instructions</a>.</TabItem>
  <TabItem value="http" label="HTTP Request">To syntax the HTTP request, please visit the corresponding <a href="https://argoeu.github.io/argo-accounting/docs/api/collect_metrics#get---collecting-metrics-from-specific-provider">document</a>.</TabItem>
</Tabs>

:::

:::info Add a new Metric to a specific Provider

<Tabs>
  <TabItem value="info" label="Info">You can add Metrics to all the Installations belonging to the Provider you have been granted as provider admin.</TabItem>
  <TabItem value="ui" label="User Interface">To perform this action via the website, please click <a href="https://accounting.eosc-portal.eu/myProviders">here</a> and follow the provided <a href="https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/my_providers#add-a-new-metric">instructions</a>.</TabItem>
  <TabItem value="http" label="HTTP Request">To syntax the HTTP request, please visit the corresponding <a href="https://argoeu.github.io/argo-accounting/docs/api/metric#post---create-a-new-metric">document</a>.</TabItem>
</Tabs>

:::

:::info Update a Metric belonging to a specific Provider

<Tabs>
  <TabItem value="info" label="Info">You can edit all Metrics belonging to the Provider you have been granted as provider admin.</TabItem>
  <TabItem value="ui" label="User Interface">To perform this action via the website, please click <a href="https://accounting.eosc-portal.eu/myProviders">here</a> and follow the provided <a href="https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/my_providers#update-an-existing-metric">instructions</a>.</TabItem>
  <TabItem value="http" label="HTTP Request">To syntax the HTTP request, please visit the corresponding <a href="https://argoeu.github.io/argo-accounting/docs/api/metric#patch---update-an-existing-metric">document</a>.</TabItem>
</Tabs>

:::

:::info Delete a Metric belonging to a specific Provider

<Tabs>
  <TabItem value="info" label="Info">You can delete all Metrics belonging to the Provider you have been granted as project admin.</TabItem>
  <TabItem value="ui" label="User Interface">To perform this action via the website, please click <a href="https://accounting.eosc-portal.eu/myProviders">here</a> and follow the provided <a href="https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/my_providers#delete-an-existing-metric">instructions</a>.</TabItem>
  <TabItem value="http" label="HTTP Request">To syntax the HTTP request, please visit the corresponding <a href="https://argoeu.github.io/argo-accounting/docs/api/metric#delete---delete-an-existing-metric">document</a>.</TabItem>
</Tabs>

:::

Please note that you can perform all the actions on [Installations](/docs/guides/installation_admin.md) belonging to the Provider you have been granted as a **_provider_admin_**.

---
