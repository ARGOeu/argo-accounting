---
id: registration
title: Register Accounting Service
sidebar_position: 1
---
import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';


### Steps to Register into Accounting Service

There are two ways to register yourself into Accounting Service :

### The first way
---

- **_Authenticate yourself_**<br/>
First you need to authenticate yourself to EOSC Core Infrastructure Proxy. Instructions are provided <b><a href="https://argoeu.github.io/argo-accounting/docs/authentication/authenticating_clients">here</a></b>. Once you gain an access token, copy it.<br/>
- **_Register yourself_**<br/>
Then you need to apply a request to the Accounting Service API, in order to register yourself. <b> For more details, how to syntax the request, see <a href="https://argoeu.github.io/argo-accounting/docs/api/client#post---client-registration">here</a></b>.<br/>

### The second way
---
Just log in to [Accounting User Interface](https://accounting.eosc-portal.eu/).


### Upon successful Registration

Once you register the Accounting Service, you can perform the following actions:


### READ Metric Definitions
:::info Choose your preferred way

<Tabs>
  <TabItem value="ui" label="User Interface">View Metric Definitions on <a href="https://accounting.eosc-portal.eu/metrics-definitions">website</a>.</TabItem>
  <TabItem value="http" label="HTTP Request">All users, with roles, can display all the Metric Definitions, existing in the Accounting Service. Apply a request to the api.
<b> For more details, how to syntax the request, see <a href="https://argoeu.github.io/argo-accounting/docs/api/metric_definition#get----fetch-all-metric-definitions">here</a>.</b></TabItem>
</Tabs>

:::


### READ Providers
:::info Choose your preferred way

<Tabs>
  <TabItem value="ui" label="User Interface">View Providers on <a href="https://accounting.eosc-portal.eu/providers">website</a>.</TabItem>
  <TabItem value="http" label="HTTP Request">You can read the Providers available on the EOSC Resource Catalogue and the ones registered through the Accounting Service API. Apply a request to the api. <b> For more details, how to syntax the request, see  <a href="https://argoeu.github.io/argo-accounting/docs/api/provider#get---fetch-all-registered-providers">here</a>.</b></TabItem>
</Tabs>

:::

### READ Unit Types
:::info Choose your preferred way

<Tabs>
  <TabItem value="ui" label="User Interface">View Unit Types on <a href="https://accounting.eosc-portal.eu/unit-types">website</a>.</TabItem>
  <TabItem value="http" label="HTTP Request">You can read all the Unit Types registered to Accounting Service. Apply a request to the api. <b> For more details, how to syntax the request, see  <a href="https://argoeu.github.io/argo-accounting/docs/api/unit_type#get----fetch-all-the-unit-types">here</a>.</b></TabItem>
</Tabs>

:::

### READ Metric Types
:::info Choose your preferred way

<Tabs>
  <TabItem value="ui" label="User Interface">View Metric Types on <a href="https://accounting.eosc-portal.eu/metric-types">website</a>.</TabItem>
  <TabItem value="http" label="HTTP Request">You can read all the Metric Types registered to Accounting Service. Apply a request to the api. <b> For more details, how to syntax the request, see  <a href="https://argoeu.github.io/argo-accounting/docs/api/metric_type#get----fetch-all-the-metric-types">here</a>.</b></TabItem>
</Tabs>

:::

### READ all Clients
:::info Choose your preferred way

<Tabs>
  <TabItem value="ui" label="User Interface">It's currently under development.</TabItem>
  <TabItem value="http" label="HTTP Request">You can read all the Clients registered to Accounting Service. Apply a request to the api. <b> For more details, how to syntax the request, see  <a href="https://argoeu.github.io/argo-accounting/docs/api/client#get---read-the-registered-clients">here</a>.</b></TabItem>
</Tabs>

:::


















