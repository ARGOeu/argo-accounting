---
id: project_admin
title: Project Admin
sidebar_position: 4
---

# Project Admin

A `Project Admin` is authorized to manage either a dedicated project or an associated Node within the system. In this context, the terms `Project` and `Node` are used interchangeably, depending on the operational scope.

## Administering at the Project Scope
![Logo](./img/project.png)

## Administering at the Node Scope
![Logo](./img/node.png)

## Before you start

**1.** [Register](/docs/guides/register.md) to Accounting Service.

**2.** [Contact](mailto:argo@grnet.gr) the system
administrator, to assign you the `Project Admin` [role](/docs/authorization/accounting_system_roles.md)
upon the project you want.


In the Accounting Service, the `Project Admin` role is the primary role for managing a Project. Since Projects and Nodes are handled in a uniform manner, a client who needs to act as a Node Admin must also be assigned the project_admin role, which allows performing all operations on the associated Project/Node.

Below we describe the actions a `Project Admin` can either perform through
the Accounting User Interface or a simple HTTP request.

## Actions

### VIEW all Projects that are assigned to you

- **User Interface**
  To perform this action via the website, please click [here](https://ui.acc.argo.grnet.gr/projects).

- **HTTP Request**
  To syntax the HTTP request, please visit the corresponding [document](../api/project#get---fetch-all-projects).

### ASSOCIATE one or more Providers with a specific Project

- **User Interface**
  To perform this action via the website, please click [here](https://ui.acc.argo.grnet.gr/projects)
and follow the provided [instructions](../guides/ui_actions/project#associate-providers-with-a-specific-project).

- **HTTP Request**
  To syntax the HTTP request, please visit the corresponding [document](../api/project#post---associate-providers-with-a-specific-project).

### DISSOCIATE Provider from a specific Project

- **User Interface**
  To perform this action via the website, please click [here](https://ui.acc.argo.grnet.gr/projects)
and follow the provided [instructions](../guides/ui_actions/project/#dissociate-providers-from-a-specific-project).

- **HTTP Request**
  To syntax the HTTP request, please visit the corresponding [document](../api/project#post---dissociate-providers-from-a-project).

### Create a new Installation on a specific Project

- **User Interface**
  To perform this action via the website, please click [here](https://ui.acc.argo.grnet.gr/installations)
and follow the provided [instructions](../guides/ui_actions/installation#create-a-new-installation).

- **HTTP Request**
  To syntax the HTTP request, please visit the corresponding [document](../api/installation#post---create-a-new-installation).

### Update the Installations belonging to a specific Project

- **User Interface**
  To perform this action via the website, please click [here](https://ui.acc.argo.grnet.gr/installations)
and follow the provided [instructions](../guides/ui_actions/installation#update-an-existing-installation).

- **HTTP Request**
  To syntax the HTTP request, please visit the corresponding [document](../api/installation#patch---update-an-existing-installation).

### Delete the Installations belonging to a specific Project

- **User Interface**
  To perform this action via the website, please click [here](https://ui.acc.argo.grnet.gr/installations)
and follow the provided [instructions](../guides/ui_actions/installation#delete-an-existing-installation).

- **HTTP Request**
  To syntax the HTTP request, please visit the corresponding [document](../api/installation#delete---delete-an-existing-installation).

### Collect Metrics from a specific Project

- **User Interface**
  To perform this action via the website, please click [here](https://ui.acc.argo.grnet.gr/projects)
and follow the provided [instructions](../guides/ui_actions/project#collect-metrics-from-specific-project).

- **HTTP Request**
  To syntax the HTTP request, please visit the corresponding [document](../api/collect_metrics#get---collecting-metrics-from-specific-project).

### Manage Metric Definitions, Providers, Unit Types, and Metric Types

As a `Project Admin`, you can create, update, and delete:

- **Metric Definitions**: [Manage Metric Definitions](https://ui.acc.argo.grnet.gr/metrics-definitions)
- **Providers**: [Manage Providers](https://ui.acc.argo.grnet.gr/providers)
- **Unit Types**: [Manage Unit Types](https://ui.acc.argo.grnet.gr/unit-types)
- **Metric Types**: [Manage Metric Types](https://ui.acc.argo.grnet.gr/metric-types)

Each of these actions has a corresponding [UI](../category/ui-actions/)
and [API documentation](../category/api-actions/).

Please note that you can perform all the actions on [Providers](../guides/provider_admin.md)
and [Installations](../guides/installation_admin.md) belonging to the
Project you have been granted as a `Project Admin`.

---

## Note

The `role=viewer` grants **read-only access permissions**. Users with this role can view information but **cannot perform any create, update, or delete operations**.
