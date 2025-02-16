---
id: registration
title: Register Accounting Service
sidebar_position: 1
---

# Register Accounting Service

## Steps to Register into Accounting Service

There are two ways to register yourself into Accounting Service:

### The first way

---

- **Authenticate yourself**  
  First, you need to authenticate yourself to EOSC Core Infrastructure Proxy.
  Instructions are provided [here](https://argoeu.github.io/argo-accounting/docs/authentication/authenticating_clients)
. Once you gain an access token, copy it.

- **Register yourself**  
  Then, you need to apply a request to the Accounting Service API in order to
  register yourself. For more details on how to syntax the request, see [here](https://argoeu.github.io/argo-accounting/docs/api/client#post---client-registration)
  .

### The second way

---
Just log in to [Accounting User Interface](https://accounting.eosc-portal.eu/).

## Upon successful Registration

Once you register for the Accounting Service, you can perform the following actions:

### READ Metric Definitions

---

- **User Interface**

  View Metric Definitions on [website](https://accounting.eosc-portal.eu/metrics-definitions).

- **HTTP Request**

  All users with roles can display all the Metric Definitions existing in the
  Accounting Service. Apply a request to the API.  
  > 📝 For more details on how to syntax the request, see [here](https://argoeu.github.io/argo-accounting/docs/api/metric_definition#get----fetch-all-metric-definitions).

### READ Providers

---

- **User Interface**

  View Providers on [website](https://accounting.eosc-portal.eu/providers).

- **HTTP Request**

  You can read the Providers available on the EOSC Resource Catalogue and those
  registered through the Accounting Service API. Apply a request to the API.  
  > 📝 For more details on how to syntax the request, see [here](https://argoeu.github.io/argo-accounting/docs/api/provider#get---fetch-all-registered-providers).

### READ Unit Types

---

- **User Interface**

  View Unit Types on [website](https://accounting.eosc-portal.eu/unit-types).

- **HTTP Request**

  You can read all the Unit Types registered to the Accounting Service.
  Apply a request to the API.  
  >📝 For more details on how to syntax the request, see [here](https://argoeu.github.io/argo-accounting/docs/api/unit_type#get----fetch-all-the-unit-types).

### READ Metric Types

---

- **User Interface**

  View Metric Types on [website](https://accounting.eosc-portal.eu/metric-types).

- **HTTP Request**

  You can read all the Metric Types registered to the Accounting Service.
  Apply a request to the API.  
  > 📝 For more details on how to syntax the request, see [here](https://argoeu.github.io/argo-accounting/docs/api/metric_type#get----fetch-all-the-metric-types).

### READ all Clients

---

- **User Interface**

  It's currently under development.

- **HTTP Request**

  You can read all the Clients registered to the Accounting Service.
  Apply a request to the API.  
  > 📝 For more details on how to syntax the request, see [here](https://argoeu.github.io/argo-accounting/docs/api/client#get---read-the-registered-clients).

---
