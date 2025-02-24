---
id: installation_admin
title: Installation Admin
sidebar_position: 5
---

# Installation Admin

## Before you start

You can manage a Provider assigned to a specific Project.

**1.** [Register](/docs/guides/register.md) to Accounting Service.  
**2.** Contact the administrator of the Project or the administrator of the
Project's Provider, that this Installation is associated with, to assign you
the Installation Admin role upon the installation you want.

In the Accounting Service, the **_installation_admin_** role is the main role
for managing an Installation. This role permits the user to perform any
operation on a specific Installation.

Below we describe the actions an **_installation_admin_** can either perform
through the Accounting User Interface or a simple HTTP request.

## Actions

### View all the Installations you have access to

---

- **User Interface**
  To perform this action via the website, please click [here](https://accounting.eosc-portal.eu/installations).

- **HTTP Request**
  It's currently under development.

### Update the Installations you have access to

---

- **User Interface**
  To perform this action via the website, please click [here](https://accounting.eosc-portal.eu/installations)
and follow the provided [instructions](https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/installation#update-an-existing-installation).

- **HTTP Request**
  To syntax the HTTP request, please visit the corresponding [document](https://argoeu.github.io/argo-accounting/docs/api/installation#patch---update-an-existing-installation).

### Delete the Installations you have access to

---

- **User Interface**
  To perform this action via the website, please click [here](https://accounting.eosc-portal.eu/installations)
and follow the provided [instructions](https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/installation#delete-an-existing-installation).

- **HTTP Request**
  To syntax the HTTP request, please visit the corresponding [document](https://argoeu.github.io/argo-accounting/docs/api/installation#delete---delete-an-existing-installation).

### Collect Metrics from a specific Installation

---

- **User Interface**
  To perform this action via the website, please click [here](https://accounting.eosc-portal.eu/installations)
and follow the provided [instructions](https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/installation#collect-metrics-from-specific-installation).

- **HTTP Request**
  To syntax the HTTP request, please visit the corresponding [document](https://argoeu.github.io/argo-accounting/docs/api/collect_metrics#get---collecting-metrics-from-specific-installation).

### Add a new Metric to a specific Installation

---

You can add Metrics to all the Installations you have been granted as
installation admin.

- **User Interface**
  To perform this action via the website, please click [here](https://accounting.eosc-portal.eu/installations)
and follow the provided [instructions](https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/installation#add-a-new-metric).

- **HTTP Request**
  To syntax the HTTP request, please visit the corresponding [document](https://argoeu.github.io/argo-accounting/docs/api/metric#post---create-a-new-metric).

### Update a Metric belonging to a specific Installation

---

You can edit all Metrics belonging to the Installation you have been
granted as installation admin.

- **User Interface**
  To perform this action via the website, please click [here](https://accounting.eosc-portal.eu/installations)
and follow the provided [instructions](https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/installation#update-an-existing-metric).

- **HTTP Request**
  To syntax the HTTP request, please visit the corresponding [document](https://argoeu.github.io/argo-accounting/docs/api/metric#patch---update-an-existing-metric).

### Delete a Metric belonging to a specific Installation

---

You can delete all Metrics belonging to the Installation you have been granted
as installation admin.

- **User Interface**
  To perform this action via the website, please click [here](https://accounting.eosc-portal.eu/installations)
and follow the provided [instructions](https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/installation#delete-an-existing-metric).

- **HTTP Request**
  To syntax the HTTP request, please visit the corresponding [document](https://argoeu.github.io/argo-accounting/docs/api/metric#delete---delete-an-existing-metric).

---
