---
id: project
title: Manage Projects
sidebar_position: 2
---

# Manage Projects

This is a guide that refers to a Project. A Project is the main resource of
the Accounting System. If you are permitted to act on one or more Projects,
via this guide you can see all the options you have.

## Before you start

**1.** [Register](/docs/guides/register.md) to Accounting Service.

**2.** [Contact](mailto:argo@grnet.gr) the system
administrator to assign you the `Project Admin` [role](/docs/authorization/accounting_system_roles.md)
upon the project you want.

### NOTE

In the Accounting Service, the `Project Admin` role is the main role
for managing a Project. This role permits the user to perform any operation
on a specific Project. In case the user is assigned any other role, they can
operate according to the role's permissions.

## OPERATIONS

---

### GET Project's Hierarchy details

You can get the details of the Project's structure to retrieve the Providers
and Installations associated with the Project. Apply a request to the
Accounting Service API.

> 📝 **For more details on how to syntax the request, see** [here](../../api/project#get---project-hierarchical-structure).

### ASSOCIATE one or more Providers with a specific Project

You can associate one or more Providers with a specific Project. This is a
required action in order to be able to assign Installations and Metrics. Apply
a request to the Accounting Service API.

> 📝 **For more details on how to syntax the request, see** [here](../../api/project#post---associate-providers-with-a-specific-project).

### DISSOCIATE Provider from a specific Project

If a Provider is associated with a specific Project, you can dissociate it.
A Provider can be dissociated only if no installations are assigned. Apply a
request to the Accounting Service API.

> 📝 **For more details on how to syntax the request, see** [here](../../api/project#post---dissociate-providers-from-a-project).


### SEARCH Projects

If you are assigned many Projects, you can search for specific
Project/Projects that match one or more criteria. You can define
search criteria on each field of the
**[Project Collection](../../api/project)**
or a combination of search criteria on more than one field. You can search
by Project's acronym, title, period, call identifier, or a combination of
them.

Apply a request to the Accounting Service API. You need to provide the search
criteria in a specific
**[syntax](./search-filter)**.

> 📝 **For more details on how to syntax the request, see** [here](../../api/project#post---search-for-projects).

In the case the role assigned to you on the Project is administrative,
you can perform all the actions on the Providers described at this
**[section](../../guides/api_actions/provider)**, as well as all the actions on Installations, described at this
**[section](../../guides/api_actions/installation)**.

### GET Project Report

You can get a report for a specific Project in a defined time period.  
The report contains aggregated metric values grouped by metric definitions for all Providers and Installations that belong to the Project. The report contains aggregated metric values grouped by metric definition for a specific time period. When capacities are defined for the selected time period, the report also includes usage percentages broken down by the capacity-defined time periods.

> 📝 **For more details on how to structure the request, see** [here](../../api/project#get---get-project-report).

---
