---
id: installation_admin
title: Installation Admin
sidebar_position: 5
---

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

### Before you start 

You can manage a Provider assigned to a specific Project.<br/>

**1.** [Register](/docs/guides/register.md) to Accounting Service.<br/>
**2.** Contact the administrator of the Project or the administrator of the Project's Provider, that this Installation is associated with, to assign you the Installation Admin role upon the installation you want.


In the Accounting Service, the **_installation_admin_** role is the main role for managing an Installation. This role permits the user to perform any operation, on a specific Installation.

Below we describe the actions an **_installation_admin_** can either perform through the Accounting User Interface or a simple HTTP request.

## Actions 

---

:::info View all the Installations you have access to

<Tabs>
  <TabItem value="ui" label="User Interface">To perform this action via the website, please click <a href="https://accounting.eosc-portal.eu/installations">here</a>.</TabItem>
  <TabItem value="http" label="HTTP Request">It's currently under development.</TabItem>
</Tabs>

:::

:::info Update the Installations you have access to

<Tabs>
  <TabItem value="ui" label="User Interface">To perform this action via the website, please click <a href="https://accounting.eosc-portal.eu/installations">here</a> and follow the provided <a href="https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/installation#update-an-existing-installation">instructions</a>.</TabItem>
  <TabItem value="http" label="HTTP Request">To syntax the HTTP request, please visit the corresponding <a href="https://argoeu.github.io/argo-accounting/docs/api/installation#patch---update-an-existing-installation">document</a>.</TabItem>
</Tabs>

:::

:::info Delete the Installations you have access to

<Tabs>
  <TabItem value="ui" label="User Interface">To perform this action via the website, please click <a href="https://accounting.eosc-portal.eu/installations">here</a> and follow the provided <a href="https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/installation#delete-an-existing-installation">instructions</a>.</TabItem>
  <TabItem value="http" label="HTTP Request">To syntax the HTTP request, please visit the corresponding <a href="https://argoeu.github.io/argo-accounting/docs/api/installation#delete---delete-an-existing-installation">document</a>.</TabItem>
</Tabs>

:::

:::info Collect Metrics from a specific Installation

<Tabs>
  <TabItem value="ui" label="User Interface">To perform this action via the website, please click <a href="https://accounting.eosc-portal.eu/installations">here</a> and follow the provided <a href="https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/installation#collect-metrics-from-specific-installation">instructions</a>.</TabItem>
  <TabItem value="http" label="HTTP Request">To syntax the HTTP request, please visit the corresponding <a href="https://argoeu.github.io/argo-accounting/docs/api/collect_metrics#get---collecting-metrics-from-specific-installation">document</a>.</TabItem>
</Tabs>

:::

:::info Add a new Metric to a specific Installation

<Tabs>
  <TabItem value="info" label="Info">You can add Metrics to all the Installations you have been granted as installation admin.</TabItem>
  <TabItem value="ui" label="User Interface">To perform this action via the website, please click <a href="https://accounting.eosc-portal.eu/installations">here</a> and follow the provided <a href="https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/installation#add-a-new-metric">instructions</a>.</TabItem>
  <TabItem value="http" label="HTTP Request">To syntax the HTTP request, please visit the corresponding <a href="https://argoeu.github.io/argo-accounting/docs/api/metric#post---create-a-new-metric">document</a>.</TabItem>
</Tabs>

:::

:::info Update a Metric belonging to a specific Installation

<Tabs>
  <TabItem value="info" label="Info">You can edit all Metrics belonging to the Installation you have been granted as installation admin.</TabItem>
  <TabItem value="ui" label="User Interface">To perform this action via the website, please click <a href="https://accounting.eosc-portal.eu/installations">here</a> and follow the provided <a href="https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/installation#update-an-existing-metric">instructions</a>.</TabItem>
  <TabItem value="http" label="HTTP Request">To syntax the HTTP request, please visit the corresponding <a href="https://argoeu.github.io/argo-accounting/docs/api/metric#patch---update-an-existing-metric">document</a>.</TabItem>
</Tabs>

:::

:::info Delete a Metric belonging to a specific Installation

<Tabs>
  <TabItem value="info" label="Info">You can delete all Metrics belonging to the Installation you have been granted as installation admin.</TabItem>
  <TabItem value="ui" label="User Interface">To perform this action via the website, please click <a href="https://accounting.eosc-portal.eu/installations">here</a> and follow the provided <a href="https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/installation#delete-an-existing-metric">instructions</a>.</TabItem>
  <TabItem value="http" label="HTTP Request">To syntax the HTTP request, please visit the corresponding <a href="https://argoeu.github.io/argo-accounting/docs/api/metric#delete---delete-an-existing-metric">document</a>.</TabItem>
</Tabs>

:::

---
