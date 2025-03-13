---
id: project_admin
title: Project Admin
sidebar_position: 3
---

# Project Admin

## Before you start

**1.** [Register](/docs/guides/register.md) to Accounting Service.

**2.** [Contact](/docs/authorization/assigning_roles.md) the system
administrator, to assign you the Project Admin role upon the project
you want.

In the Accounting Service, the **_project_admin_** role is the main role
for managing a Project. This role permits the client to perform any operation,
on a specific Project.

Below we describe the actions a **_project_admin_** can either perform through
the Accounting User Interface or a simple HTTP request.

## Actions

### VIEW all Projects that are assigned to you

---

- **User Interface**
  To perform this action via the website, please click [here](https://accounting.eosc-portal.eu/projects).

- **HTTP Request**
  To syntax the HTTP request, please visit the corresponding [document](https://argoeu.github.io/argo-accounting/docs/api/project#get---fetch-all-projects).

### ASSOCIATE one or more Providers with a specific Project

---

- **User Interface**
  To perform this action via the website, please click [here](https://accounting.eosc-portal.eu/projects)
and follow the provided [instructions](https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/project#associate-providers-with-a-specific-project).

- **HTTP Request**
  To syntax the HTTP request, please visit the corresponding [document](https://argoeu.github.io/argo-accounting/docs/api/project#post---associate-providers-with-a-specific-project).

### DISSOCIATE Provider from a specific Project

---

- **User Interface**
  To perform this action via the website, please click [here](https://accounting.eosc-portal.eu/projects)
and follow the provided [instructions](https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/project/#dissociate-providers-from-a-specific-project).

- **HTTP Request**
  To syntax the HTTP request, please visit the corresponding [document](https://argoeu.github.io/argo-accounting/docs/api/project#post---dissociate-providers-from-a-project).

### Create a new Installation on a specific Project

---

- **User Interface**
  To perform this action via the website, please click [here](https://accounting.eosc-portal.eu/installations)
and follow the provided [instructions](https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/installation#create-a-new-installation).

- **HTTP Request**
  To syntax the HTTP request, please visit the corresponding [document](https://argoeu.github.io/argo-accounting/docs/api/installation#post---create-a-new-installation).

### Update the Installations belonging to a specific Project

---

- **User Interface**
  To perform this action via the website, please click [here](https://accounting.eosc-portal.eu/installations)
and follow the provided [instructions](https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/installation#update-an-existing-installation).

- **HTTP Request**
  To syntax the HTTP request, please visit the corresponding [document](https://argoeu.github.io/argo-accounting/docs/api/installation#patch---update-an-existing-installation).

### Delete the Installations belonging to a specific Project

---

- **User Interface**
  To perform this action via the website, please click [here](https://accounting.eosc-portal.eu/installations)
and follow the provided [instructions](https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/installation#delete-an-existing-installation).

- **HTTP Request**
  To syntax the HTTP request, please visit the corresponding [document](https://argoeu.github.io/argo-accounting/docs/api/installation#delete---delete-an-existing-installation).

### Collect Metrics from a specific Project

---

- **User Interface**
  To perform this action via the website, please click [here](https://accounting.eosc-portal.eu/projects)
and follow the provided [instructions](https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/project#collect-metrics-from-specific-project).

- **HTTP Request**
  To syntax the HTTP request, please visit the corresponding [document](https://argoeu.github.io/argo-accounting/docs/api/collect_metrics#get---collecting-metrics-from-specific-project).

### Manage Metric Definitions, Providers, Unit Types, and Metric Types

---

As a **_project_admin_**, you can create, update, and delete:

- **Metric Definitions**: [Manage Metric Definitions](https://accounting.eosc-portal.eu/metrics-definitions)
- **Providers**: [Manage Providers](https://accounting.eosc-portal.eu/providers)
- **Unit Types**: [Manage Unit Types](https://accounting.eosc-portal.eu/unit-types)
- **Metric Types**: [Manage Metric Types](https://accounting.eosc-portal.eu/metric-types)

Each of these actions has a corresponding [guide](https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/)
and [API documentation](https://argoeu.github.io/argo-accounting/docs/api/).

Please note that you can perform all the actions on [Providers](/docs/guides/provider_admin.md)
and [Installations](/docs/guides/installation_admin.md) belonging to the
Project you have been granted as a **_project_admin_**.

---
