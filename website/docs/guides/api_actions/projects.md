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
**2.** [Contact](/docs/authorization/assigning_roles.md) the system
administrator to assign you one or more roles on the Project.

**NOTE**  
In the Accounting Service, the **_project_admin_** role is the main role
for managing a Project. This role permits the user to perform any operation
on a specific Project. In case the user is assigned any other role, they can
operate according to the role's permissions.

## OPERATIONS

---

### GET Project's Hierarchy details

You can get the details of the Project's structure to retrieve the Providers
and Installations associated with the Project. Apply a request to the
Accounting Service API.

> 📝 **For more details on how to syntax the request, see** [here](https://argoeu.github.io/argo-accounting/docs/api/project#get---project-hierarchical-structure).

### ASSOCIATE one or more Providers with a specific Project

You can associate one or more Providers with a specific Project. This is a
required action in order to be able to assign Installations and Metrics. Apply
a request to the Accounting Service API.

> 📝 **For more details on how to syntax the request, see** [here](https://argoeu.github.io/argo-accounting/docs/api/project#post---associate-providers-with-a-specific-project).

### DISSOCIATE Provider from a specific Project

If a Provider is associated with a specific Project, you can dissociate it.
A Provider can be dissociated only if no installations are assigned. Apply a
request to the Accounting Service API.

> 📝 **For more details on how to syntax the request, see** [here](https://argoeu.github.io/argo-accounting/docs/api/project#post---dissociate-providers-from-a-project).

### FETCH Project's hierarchical structure

You can fetch the hierarchy of the Project (all Providers and Installations
that are assigned to the Project). Apply a request to the Accounting Service
API.

> 📝 **For more details on how to syntax the request, see** [here](https://argoeu.github.io/argo-accounting/docs/api/project#get---project-hierarchical-structure).

### FETCH all Projects

You can fetch all the Projects that are assigned to you. Apply a request to
the Accounting Service API.

> 📝 **For more details on how to syntax the request, see** [here](https://argoeu.github.io/argo-accounting/docs/api/project#get---fetch-all-projects).

### Provide access roles on the Project

You can provide users with access roles on the Project.

**1.** Read registered clients (see
[here](https://argoeu.github.io/argo-accounting/docs/api/client#get---read-the-registered-clients)
) and retrieve the client's id.
**2.** Decide on one or more roles that this user will be assigned with
on the Project and apply a request to the Accounting Service API.

> 📝 **For more details on how to syntax the request, see**
[here](https://argoeu.github.io/argo-accounting/docs/api/project#post---access-control-entry-for-a-particular-project)
.

### SEARCH Projects

If you are assigned many Projects, you can search for specific
Project/Projects that match one or more criteria. You can define
search criteria on each field of the
**[Project Collection](https://argoeu.github.io/argo-accounting/docs/api/project)**
or a combination of search criteria on more than one field. You can search
by Project's acronym, title, period, call identifier, or a combination of
them.  
Apply a request to the Accounting Service API. You need to provide the search
criteria in a specific
**[syntax](https://argoeu.github.io/argo-accounting/docs/guides/search-filter)**
.

> 📝 **For more details on how to syntax the request, see** [here](https://argoeu.github.io/argo-accounting/docs/api/project#post---search-for-projects).

In the case the role assigned to you on the Project is administrative,
you can perform all the actions on the Providers described at this
**[section](https://argoeu.github.io/argo-accounting/docs/guides/api_actions/provider)**
, as well as all the actions on Installations, described at this
**[section](https://argoeu.github.io/argo-accounting/docs/guides/api_actions/installation)**.

---
