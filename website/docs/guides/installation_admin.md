---
id: installation_admin
title: Installation Admin
sidebar_position: 6
---

# Installation Admin

![Logo](./img/installation.png)

## Before you start

You can manage a Provider assigned to a specific Project.

**1.** [Register](/docs/guides/register.md) to Accounting Service.

**2.** [Contact](mailto:argo@grnet.gr) the administrator of the Project or the administrator of the
Project's Provider, that this Installation is associated with, to assign you
the `Installation Admin` [role](/docs/authorization/accounting_system_roles.md) upon the installation you want.

In the Accounting Service, the `Installation Admin` role is the main role
for managing an Installation. This role permits the user to perform any
operation on a specific Installation.

Below we describe the actions an `Installation Admin` can either perform
through the Accounting User Interface or a simple HTTP request.

## Actions

### View all the Installations you have access to

- **User Interface**
  To perform this action via the website, please click [here](https://ui.acc.argo.grnet.gr/installations).


### Update the Installations you have access to

- **User Interface**
  To perform this action via the website, please click [here](https://ui.acc.argo.grnet.gr/installations)
and follow the provided [instructions](../guides/ui_actions/installation#update-an-existing-installation).

- **HTTP Request**
  To syntax the HTTP request, please visit the corresponding [document](../api/installation#patch---update-an-existing-installation).

### Delete the Installations you have access to

- **User Interface**
  To perform this action via the website, please click [here](https://ui.acc.argo.grnet.gr/installations)
and follow the provided [instructions](../guides/ui_actions/installation#delete-an-existing-installation).

- **HTTP Request**
  To syntax the HTTP request, please visit the corresponding [document](../api/installation#delete---delete-an-existing-installation).

### Collect Metrics from a specific Installation

- **User Interface**
  To perform this action via the website, please click [here](https://ui.acc.argo.grnet.gr/installations)
and follow the provided [instructions](../guides/ui_actions/installation#collect-metrics-from-specific-installation).

- **HTTP Request**
  To syntax the HTTP request, please visit the corresponding [document](../api/collect_metrics#get---collecting-metrics-from-specific-installation).

### Add a new Metric to a specific Installation

You can add Metrics to all the Installations you have been granted as
installation admin.

- **User Interface**
  To perform this action via the website, please click [here](https://ui.acc.argo.grnet.gr/installations)
and follow the provided [instructions](../guides/ui_actions/installation#add-a-new-metric).

- **HTTP Request**
  To syntax the HTTP request, please visit the corresponding [document](../api/metric#post---create-a-new-metric).

### Update a Metric belonging to a specific Installation

You can edit all Metrics belonging to the Installation you have been
granted as installation admin.

- **User Interface**
  To perform this action via the website, please click [here](https://ui.acc.argo.grnet.gr/installations)
and follow the provided [instructions](../guides/ui_actions/installation#update-an-existing-metric).

- **HTTP Request**
  To syntax the HTTP request, please visit the corresponding [document](../api/metric#patch---update-an-existing-metric).

### Delete a Metric belonging to a specific Installation

You can delete all Metrics belonging to the Installation you have been granted
as installation admin.

- **User Interface**
  To perform this action via the website, please click [here](https://ui.acc.argo.grnet.gr/installations)
and follow the provided [instructions](../guides/ui_actions/installation#delete-an-existing-metric).

- **HTTP Request**
  To syntax the HTTP request, please visit the corresponding [document](../api/metric#delete---delete-an-existing-metric).

### Manage Metric Definitions, Providers, Unit Types, and Metric Types

As an `Installation Admin`, you can create, update, and delete:

- **Metric Definitions**: [Manage Metric Definitions](https://ui.acc.argo.grnet.gr/metrics-definitions)
- **Providers**: [Manage Providers](https://ui.acc.argo.grnet.gr/providers)
- **Unit Types**: [Manage Unit Types](https://ui.acc.argo.grnet.gr/unit-types)
- **Metric Types**: [Manage Metric Types](https://ui.acc.argo.grnet.gr/metric-types)

Each of these actions has a corresponding [UI](../category/ui-actions/)
and [API documentation](../category/api-actions/).

---

## Note

The `role=viewer` grants **read-only access permissions**. Users with this role can view information but **cannot perform any create, update, or delete operations**.
