---
id: provider
title: Manage Providers
sidebar_position: 3
---

# Manage Providers

This is a guide that refers to a Provider. A Provider is the Organization that
operates one or more Installations in one or more Projects. If you are
permitted to act on one or more Providers, via this guide you can see all the
options you have.

## Before you start

You can manage a Provider assigned to a specific Project.

**1.** [Register](/docs/guides/register.md) to Accounting Service.

**2.** [Contact](/docs/authorization/assigning_roles.md) the administrator of
the Project that this Provider is
associated with to assign you one or more roles on the Provider.

### NOTE

In the Accounting Service, the **_provider_admin_** role is the main role for
managing a Provider. This role permits the user to perform any operation on a
specific Provider. In case the user is assigned any other role, they can
operate according to the role's permissions.

## OPERATIONS

---

### GET Provider's details

You can get the details of the Provider. Apply a request to the
Accounting Service API.

> 📝 **For more details on how to syntax the request, see** [here](https://argoeu.github.io/argo-accounting/docs/api/provider#get---fetch-an-existing-provider).

### Provide access roles on the Provider

You can provide users with access roles on the Provider.

**1.** Read registered clients (see
[here](https://argoeu.github.io/argo-accounting/docs/api/client#get---read-the-registered-clients)
) and retrieve the client's id.

**2.** Decide on one or more roles that this user will be assigned with on
the Provider and apply a request to the Accounting Service API.

> 📝 **For more details on how to syntax the request, see**
[here](https://argoeu.github.io/argo-accounting/docs/api/provider#post---access-control-entry-for-a-particular-provider-of-a-specific-project)
.

### ASSIGN Installations to the Provider

You can assign one or more Installations to the Provider.
Apply a request to the Accounting Service API.

> 📝 **For more details on how to syntax the request, see**
[here](https://argoeu.github.io/argo-accounting/docs/api/installation#post---create-a-new-installation)
.

### FETCH all Installations assigned to the provider

You can fetch all Installations assigned to the Provider.
Apply a request to the Accounting Service API.

> 📝 **For more details on how to syntax the request, see**
[here](https://argoeu.github.io/argo-accounting/docs/api/installation#get-fetch-all-provider-installations)
.

### SEARCH Installations assigned to the provider

You can search for specific Installation/Installations assigned to the
Provider that match one or more criteria. You can define search criteria
on each field of the **[Installation Collection](https://argoeu.github.io/argo-accounting/docs/api/installation)**
or a combination of search criteria on more than one field. You can search
for Installations by Project, Provider, infrastructure, Installation's name,
Metric Definition id, or a combination of them.

Apply a request to the Accounting Service API. You need to provide the search
criteria in a specific
**[syntax](https://argoeu.github.io/argo-accounting/docs/guides/search-filter)**
.

> 📝 **For more details on how to syntax the request, see**
[here](https://argoeu.github.io/argo-accounting/docs/api/installation#post---search-for-installations)
.

Also, if the role assigned to you is administrative, you are permitted to
perform all the actions described at this **[section](https://argoeu.github.io/argo-accounting/docs/guides/api_actions/installation)**
on the Installations assigned to the Provider.

### GET Provider Report

You can get a report for a specific Provider in a defined time period.  
The report contains aggregated metric values grouped by metric definitions for all Installations that belong to the Provider.

Apply a request to the Accounting Service API by providing the Project ID, the Provider ID, and the desired time range (`start`, `end`).

> 📝 **For more details on how to structure the request, see** [here](https://argoeu.github.io/argo-accounting/docs/api/provider#get---get-provider-report).


### GET Global Provider Report

You can get a global report of a Provider by applying a request to the Accounting
Service API. The report contains aggregated metric values for all Installations for a specific time period, independently of a Project.

> 📝 For more details on how to structure the request, see [here](https://argoeu.github.io/argo-accounting/docs/api/provider#get---get-global-provider-report).

---
