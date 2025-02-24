---
id: provider_admin
title: Provider Admin
sidebar_position: 4
---

# Provider Admin

## Before you start

You can manage a Provider assigned to a specific Project.

**1.** [Register](/docs/guides/register.md) to Accounting Service.  
**2.** Contact the administrator of the Project, that this Provider is
associated with, to assign you the Provider Admin role upon the provider
you want.

In the Accounting Service, the **_provider_admin_** role is the main role
for managing a Provider belonging to a Project. This role permits the user
to perform any operation on a specific Provider.

Below we describe the actions a **_provider_admin_** can either perform
through the Accounting User Interface or a simple HTTP request.

## Actions

### View all the Providers you have access to

---

- **User Interface:**
  To perform this action via the website, please click [here](https://accounting.eosc-portal.eu/myProviders).

- **HTTP Request:**
  It's currently under development.

### Create a new Installation on a specific Provider

---

- **User Interface:**
  To perform this action via the website, please click [here](https://accounting.eosc-portal.eu/installations)
and follow the provided [instructions](https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/installation#create-a-new-installation).

- **HTTP Request:**
  To syntax the HTTP request, please visit the corresponding [document](https://argoeu.github.io/argo-accounting/docs/api/installation#post---create-a-new-installation).

### Update the Installations belonging to a specific Provider

---

- **User Interface:**
  To perform this action via the website, please click [here](https://accounting.eosc-portal.eu/installations)
and follow the provided [instructions](https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/installation#update-an-existing-installation).

- **HTTP Request:**
  To syntax the HTTP request, please visit the corresponding [document](https://argoeu.github.io/argo-accounting/docs/api/installation#patch---update-an-existing-installation).

### Delete the Installations belonging to a specific Provider

---

- **User Interface:**
  To perform this action via the website, please click [here](https://accounting.eosc-portal.eu/installations)
and follow the provided [instructions](https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/installation#delete-an-existing-installation).

- **HTTP Request:**
  To syntax the HTTP request, please visit the corresponding [document](https://argoeu.github.io/argo-accounting/docs/api/installation#delete---delete-an-existing-installation).

### Collect Metrics from a specific Provider

---

- **User Interface:**
  To perform this action via the website, please click [here](https://accounting.eosc-portal.eu/myProviders)
and follow the provided [instructions](https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/my_providers#collect-metrics-from-specific-provider).

- **HTTP Request:**
  To syntax the HTTP request, please visit the corresponding [document](https://argoeu.github.io/argo-accounting/docs/api/collect_metrics#get---collecting-metrics-from-specific-provider).

### Add a new Metric to a specific Provider

---

- **Info:**
  You can add Metrics to all the Installations belonging to the Provider you
have been granted as provider admin.

- **User Interface:**
  To perform this action via the website, please click [here](https://accounting.eosc-portal.eu/myProviders)
and follow the provided [instructions](https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/my_providers#add-a-new-metric).

- **HTTP Request:**
  To syntax the HTTP request, please visit the corresponding [document](https://argoeu.github.io/argo-accounting/docs/api/metric#post---create-a-new-metric).

### Update a Metric belonging to a specific Provider

---

- **Info:**
  You can edit all Metrics belonging to the Provider you have been granted
as provider admin.

- **User Interface:**
  To perform this action via the website, please click [here](https://accounting.eosc-portal.eu/myProviders)
and follow the provided [instructions](https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/my_providers#update-an-existing-metric).

- **HTTP Request:**
  To syntax the HTTP request, please visit the corresponding [document](https://argoeu.github.io/argo-accounting/docs/api/metric#patch---update-an-existing-metric).

### Delete a Metric belonging to a specific Provider

---

- **Info:**
  You can delete all Metrics belonging to the Provider you have been granted
as project admin.

- **User Interface:**
  To perform this action via the website, please click [here](https://accounting.eosc-portal.eu/myProviders)
and follow the provided [instructions](https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/my_providers#delete-an-existing-metric).

- **HTTP Request:**
  To syntax the HTTP request, please visit the corresponding [document](https://argoeu.github.io/argo-accounting/docs/api/metric#delete---delete-an-existing-metric).

Please note that you can perform all the actions on [Installations](/docs/guides/installation_admin.md)
belonging to the Provider you have been granted as a **_provider_admin_**.

---
