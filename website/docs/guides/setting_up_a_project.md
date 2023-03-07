---
id: setting_up_a_project
title: Setting up a Project
sidebar_position: 4
---

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';


To be able to set up a particular Project, you must be the `Project Admin` of that Project.
See [here](/docs/guides/api_actions/projects.md) how you can obtain that role.

Consider that you can execute the following actions either through User Interface or directly to the Accounting Service by a plain HTTP request.

### Step 1: Associate Providers with the Project

:::note
You can either use the Providers <a href="https://argoeu.github.io/argo-accounting/docs/api/provider#registering-provider-by-following-the-eosc-onboarding-process-at-httpsproviderseosc-portaleubecomeaprovider">registered in the EOSC Resource Catalogue</a> or create a new one. 
:::

:::info View all the Providers

<Tabs>
  <TabItem value="ui" label="User Interface">View Providers on <a href="https://accounting.eosc-portal.eu/providers">website</a>.</TabItem>
  <TabItem value="http" label="HTTP Request">To syntax the HTTP request, please visit the corresponding <a href="https://argoeu.github.io/argo-accounting/docs/api/provider#get---fetch-all-registered-providers">document</a>.</TabItem>
</Tabs>

:::

:::info Create a new Provider

<Tabs>
  <TabItem value="ui" label="User Interface">It's currently under development.</TabItem>
  <TabItem value="http" label="HTTP Request">To syntax the HTTP request, please visit the corresponding <a href="https://argoeu.github.io/argo-accounting/docs/api/provider#post---create-a-new-provider">document</a>.</TabItem>
</Tabs>

:::

:::info Associate Providers with the Project

<Tabs>
  <TabItem value="ui" label="User Interface">To perform this action via the website, please click <a href="https://accounting.eosc-portal.eu/projects">here</a> and follow the provided <a href="https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/project#associate-providers-with-a-specific-project">instructions</a>.</TabItem>
  <TabItem value="http" label="HTTP Request">To syntax the HTTP request, please visit the corresponding <a href="https://argoeu.github.io/argo-accounting/docs/api/project#post---associate-providers-with-a-specific-project">document</a>.</TabItem>
</Tabs>

:::

### Step 2: Create a new Installation and assign it to the associated Providers

:::note
As indicated in the Installation <a href="https://argoeu.github.io/argo-accounting/docs/api/installation">documentation</a>, the "unit_of_access" Installation property must point to an existing Metric Definition.
Therefore, you can either create a new Metric Definition or use an existing one.
:::

:::info View all the Metric Definitions

<Tabs>
  <TabItem value="ui" label="User Interface">View Metric Definitions on <a href="https://accounting.eosc-portal.eu/metrics-definitions">website</a>.</TabItem>
  <TabItem value="http" label="HTTP Request">To syntax the HTTP request, please visit the corresponding <a href="https://argoeu.github.io/argo-accounting/docs/api/metric_definition#get----fetch-all-metric-definitions">document</a>.</TabItem>
</Tabs>

:::

:::info Create a new Metric Definition

<Tabs>
  <TabItem value="ui" label="User Interface">To perform this action via the website, please click <a href="https://accounting.eosc-portal.eu/metrics-definitions">here</a> and follow the provided <a href="https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/metric_definition#create-a-metric-definition">instructions</a>.</TabItem>
  <TabItem value="http" label="HTTP Request">To syntax the HTTP request, please visit the corresponding <a href="https://argoeu.github.io/argo-accounting/docs/api/metric_definition#post---create-a-metric-definition">document</a>.</TabItem>
</Tabs>

:::

:::info Create a new Installation

<Tabs>
  <TabItem value="ui" label="User Interface">It's currently under development.</TabItem>
  <TabItem value="http" label="HTTP Request">To syntax the HTTP request, please visit the corresponding <a href="https://argoeu.github.io/argo-accounting/docs/api/installation#post---create-a-new-installation">document</a>.</TabItem>
</Tabs>

:::

### Step 3: Add Metrics

:::info Add Metrics to a particular Project

<Tabs>
  <TabItem value="ui" label="User Interface">To perform this action via the website, please follow the provided <a href="https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/project#manage-project-metrics">instructions</a>.</TabItem>
  <TabItem value="http" label="HTTP Request">To syntax the HTTP request, please visit the corresponding <a href="https://argoeu.github.io/argo-accounting/docs/api/metric#post---create-a-new-metric">document</a>.</TabItem>
</Tabs>

:::