---
id: provider_admin
title: Provider Admin
sidebar_position: 5
---

# Provider Admin

![Logo](./img/provider.png)

## Before you start

You can manage a Provider assigned to a specific Project.

**1.** [Register](/docs/guides/register.md) to Accounting Service.

**2.** [Contact](mailto:argo@grnet.gr) the administrator of the Project, that this Provider is
associated with, to assign you the `Provider Admin` [role](/docs/authorization/accounting_system_roles.md) upon the provider
you want.

In the Accounting Service, the `Provider Admin` role is the main role
for managing a Provider belonging to a Project. This role permits the user
to perform any operation on a specific Provider.

Below we describe the actions a `Provider Admin` can either perform
through the Accounting User Interface or a simple HTTP request.

## Actions

### View all the Providers you have access to

- **User Interface:**
  To perform this action via the website, please click [here](https://ui.acc.argo.grnet.gr/myProviders).

### Create a new Installation on a specific Provider

- **User Interface:**
  To perform this action via the website, please click [here](https://ui.acc.argo.grnet.gr/installations)
and follow the provided [instructions](../guides/ui_actions/installation#create-a-new-installation).

- **HTTP Request:**
  To syntax the HTTP request, please visit the corresponding [document](../api/installation#post---create-a-new-installation).

### Update the Installations belonging to a specific Provider

- **User Interface:**
  To perform this action via the website, please click [here](https://ui.acc.argo.grnet.gr/installations)
and follow the provided [instructions](../guides/ui_actions/installation#update-an-existing-installation).

- **HTTP Request:**
  To syntax the HTTP request, please visit the corresponding [document](../api/installation#patch---update-an-existing-installation).

### Delete the Installations belonging to a specific Provider

- **User Interface:**
  To perform this action via the website, please click [here](https://ui.acc.argo.grnet.gr/installations)
and follow the provided [instructions](../guides/ui_actions/installation#delete-an-existing-installation).

- **HTTP Request:**
  To syntax the HTTP request, please visit the corresponding [document](../api/installation#delete---delete-an-existing-installation).

### Collect Metrics from a specific Provider

- **User Interface:**
  To perform this action via the website, please click [here](https://ui.acc.argo.grnet.gr/myProviders)
and follow the provided [instructions](../guides/ui_actions/my_providers#collect-metrics-from-specific-provider).

- **HTTP Request:**
  To syntax the HTTP request, please visit the corresponding [document](../api/collect_metrics#get---collecting-metrics-from-specific-provider).

### Add a new Metric to a specific Provider

- **Info:**
  You can add Metrics to all the Installations belonging to the Provider you
have been granted as provider admin.

- **User Interface:**
  To perform this action via the website, please click [here](https://ui.acc.argo.grnet.gr/myProviders)
and follow the provided [instructions](../guides/ui_actions/my_providers#add-a-new-metric).

- **HTTP Request:**
  To syntax the HTTP request, please visit the corresponding [document](../api/metric#post---create-a-new-metric).

### Update a Metric belonging to a specific Provider

- **Info:**
  You can edit all Metrics belonging to the Provider you have been granted
as provider admin.

- **User Interface:**
  To perform this action via the website, please click [here](https://ui.acc.argo.grnet.gr/myProviders)
and follow the provided [instructions](../guides/ui_actions/my_providers#update-an-existing-metric).

- **HTTP Request:**
  To syntax the HTTP request, please visit the corresponding [document](../api/metric#patch---update-an-existing-metric).

### Delete a Metric belonging to a specific Provider

- **Info:**
  You can delete all Metrics belonging to the Provider you have been granted
as project admin.

- **User Interface:**
  To perform this action via the website, please click [here](https://ui.acc.argo.grnet.gr/myProviders)
and follow the provided [instructions](../guides/ui_actions/my_providers#delete-an-existing-metric).

- **HTTP Request:**
  To syntax the HTTP request, please visit the corresponding [document](../api/metric#delete---delete-an-existing-metric).

Please note that you can perform all the actions on [Installations](/docs/guides/installation_admin.md)
belonging to the Provider you have been granted as a `Provider Admin`.

### Manage Metric Definitions, Providers, Unit Types, and Metric Types

As a `Provider Admin`, you can create, update, and delete:

- **Metric Definitions**: [Manage Metric Definitions](https://ui.acc.argo.grnet.gr/metrics-definitions)
- **Providers**: [Manage Providers](https://ui.acc.argo.grnet.gr/providers)
- **Unit Types**: [Manage Unit Types](https://ui.acc.argo.grnet.gr/unit-types)
- **Metric Types**: [Manage Metric Types](https://ui.acc.argo.grnet.gr/metric-types)

Each of these actions has a corresponding [UI](../category/ui-actions/)
and [API documentation](../category/api-actions/).

Please note that you can perform all the actions on [Installations](../guides/installation_admin.md) belonging to the
Provider you have been granted as a `Provider Admin`.

---

## Note

The `role=viewer` grants **read-only access permissions**. Users with this role can view information but **cannot perform any create, update, or delete operations**.
