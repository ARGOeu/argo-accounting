---
id: registration
title: Register into the Accounting Service
sidebar_position: 1
---

# Register into the Accounting Service


## Steps to Register into the Accounting Service

There are two ways to register yourself into Accounting Service:

### The first way

- **Authenticate yourself**

  First, you need to authenticate yourself to EOSC Core Infrastructure Proxy.
  Instructions are provided [here](../authentication/authenticating_clients). Once you gain an access token, copy it.

- **Register yourself**

  Then, you need to apply a request to the Accounting Service API in order to
  register yourself. For more details on how to syntax the request, see [here](../api/client#post---client-registration).

### The second way

Just log in to [Accounting User Interface](https://ui.acc.argo.grnet.gr).

## Upon successful Registration

After registering to the Accounting Service, you should notify the Accounting Service support team so that a role can be assigned to you.

See [Mapping of Roles to Entitlements](../authorization/accounting_system_roles#mapping-of-roles-to-entitlements) for assigning a role to a client/actor.

The most basic entitlement that can be granted to you is:
```
{
  "name": "{namespace}:group:accounting:operations:resources:role=viewer"
}
```

where you will be able to perform the following actions:

### READ Providers

---

- **User Interface**

  View Providers on [website](https://ui.acc.argo.grnet.gr/providers).

- **HTTP Request**

  You can read the Providers available on the EOSC Resource Catalogue and those
  registered through the Accounting Service API. Apply a request to the API.

  > 📝 For more details on how to syntax the request, see [here](../api/provider#get---fetch-all-registered-providers).


### READ Metric Definitions

---

- **User Interface**

  View Metric Definitions on [website](https://ui.acc.argo.grnet.gr/metrics-definitions).

- **HTTP Request**

  All users with roles can display all the Metric Definitions existing in the
  Accounting Service. Apply a request to the API.

  > 📝 For more details on how to syntax the request, see [here](../api/metric_definition#get----fetch-all-metric-definitions).


### READ Unit Types

---

- **User Interface**

  View Unit Types on [website](https://ui.acc.argo.grnet.gr/unit-types).

- **HTTP Request**

  You can read all the Unit Types registered to the Accounting Service.
  Apply a request to the API.

  > 📝 For more details on how to syntax the request, see [here](../api/unit_type#get----fetch-all-the-unit-types).

### READ Metric Types

---

- **User Interface**

  View Metric Types on [website](https://ui.acc.argo.grnet.gr/metric-types).

- **HTTP Request**

  You can read all the Metric Types registered to the Accounting Service.
  Apply a request to the API.

  > 📝 For more details on how to syntax the request, see [here](../api/metric_type#get----fetch-all-the-metric-types).

### READ Installations' Resources

---

- **HTTP Request**

  You can read all the Resources registered to the Accounting Service.
  Apply a request to the API.

  > 📝 For more details on how to syntax the request, see [here](../api/resource#get---fetch-resources).

---
