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

**2.** [Contact](mailto:argo@grnet.gr) the system
administrator to assign you one or more [roles](/docs/authorization/accounting_system_roles.md) on the Installation that is associated with the particular Provider and Project.

### NOTE

In the Accounting Service, the `Installation Admin` role is the main role
for managing an Installation. This role permits the user to perform any
operation on a specific Installation. If the user is assigned any other role,
they can operate according to the role's permissions.

## OPERATIONS

---

### GET Installation's details

You can get the details of the Installation by applying a request to the Accounting
Service API.

> 📝 **For more details on how to syntax the request, see** [here](../../api/installation#get---fetch-an-existing-installation).

### UPDATE the Installation

You can update the Installation by applying a request to the Accounting Service
API, providing the new values of the Installation's properties.

> 📝 **For more details on how to syntax the request, see** [here](../../api/installation#patch---update-an-existing-installation).

### DELETE the Installation

You can delete the Installation by applying a request to the Accounting Service
API. If Metrics are assigned to the Installation, no DELETE action can take
place. In this case, you need to delete all the assigned Metrics first.

> 📝 **For more details on how to syntax the request, see** [here](../../api/installation#delete---delete-an-existing-installation).

### REGISTER a new Capacity for a specific Installation

You can register capacities for the installations where you have been granted installation admin rights, with each capacity linked to a specific Metric Definition.

> 📝 **For more details on how to syntax the request, see** [here](../../api/installation#post---register-a-new-capacity-for-an-installation).

### UPDATE a Capacity belonging to a specific Installation

You can update capacities for the installations where you have been granted installation admin rights, with each capacity linked to a specific Metric Definition.

> 📝 **For more details on how to syntax the request, see** [here](../../api/installation#patch---update-an-existing-capacity-of-an-installation).

### GET Capacities for a specific Installation

You can fetch all capacities belonging to the Installations you have been granted as installation admin.

> 📝 **For more details on how to syntax the request, see** [here](../../api/installation#get---fetch-all-the-capacities-of-an-installation).

### GET Installation Report

You can get a report of an Installation by applying a request to the Accounting
Service API. The report contains aggregated metric values grouped by metric definition for a specific time period. When capacities are defined for the selected time period, the report also includes usage percentages broken down by the capacity-defined time periods.
The report can be obtained using the external installation id as a query parameter.

> 📝 **For more details on how to syntax the request, see** [here](../../api/installation#get---get-installation-report).

---
