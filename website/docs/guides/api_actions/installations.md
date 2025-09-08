---
id: installation
title: Manage Installations
sidebar_position: 4
---

# Manage Installations

This is a guide that refers to an Installation. An Installation refers to a
specific instance or part of a resource/service that is allocated to a
specific Project by one Provider. If you are permitted to act on one or more
Installations, this guide outlines all the options available to you.

## Before you start

You can manage an Installation assigned to a specific Project and Provider.

**1.** [Register](/docs/guides/register.md) to Accounting Service.

**2.** [Contact](/docs/authorization/assigning_roles.md) the administrator of
   the Project or the administrator of the
   Project's Provider that this Installation is associated with to assign you
   one or more roles on the Installation.

### NOTE

In the Accounting Service, the **installation_admin** role is the main role
for managing an Installation. This role permits the user to perform any
operation on a specific Installation. If the user is assigned any other role,
they can operate according to the role's permissions.

## OPERATIONS

---

### GET Installation's details

You can get the details of the Installation by applying a request to the Accounting
Service API.

> 📝 For more details on how to structure the request, see [here](https://argoeu.github.io/argo-accounting/docs/api/installation#get---fetch-an-existing-installation).

### UPDATE the Installation

You can update the Installation by applying a request to the Accounting Service
API, providing the new values of the Installation's properties.

> 📝 For more details on how to structure the request, see [here](https://argoeu.github.io/argo-accounting/docs/api/installation#patch---update-an-existing-installation).

### DELETE the Installation

You can delete the Installation by applying a request to the Accounting Service
API. If Metrics are assigned to the Installation, no DELETE action can take
place. In this case, you need to delete all the assigned Metrics first.

> 📝 For more details on how to structure the request, see [here](https://argoeu.github.io/argo-accounting/docs/api/installation#delete---delete-an-existing-installation).

### GET Installation Report

You can get a report of an Installation by applying a request to the Accounting
Service API. The report contains aggregated metric values grouped by metric definition for a specific time period.
The report can be obtained using the external installation id as a query parameter.

> 📝 For more details on how to structure the request, see [here](https://argoeu.github.io/argo-accounting/docs/api/installation#get---get-installation-report).

---
