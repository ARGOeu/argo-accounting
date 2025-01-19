---
id: project
title: Manage Projects
sidebar_position: 2
---


This is a guide that refers to a Project.
A Project is the main resource of the Accounting System.
If you are permitted to act on one or more Projects, via this guide you can see all the options you have .

### Before you start

**1.** [Register](/docs/guides/register.md) to Accounting Service.<br/>
**2.** [Contact](/docs/authorization/assigning_roles.md) the system administrator, to assign you one or more roles on the Project.

**ΝΟΤΕ** <br/>
In the Accounting Service, the **_project_admin_** role is the main role for managing a Project.This role permits the user to perform any operation,on a specific Project.
In case the user is assigned with any other role,he can operate according to the role's permissions.

## OPERATIONS

---

### GET Project's Hierarchy details

<details>
You can get the detais of the Project's structuure,to retrieve the Providers and Installations associated with the Project.Apply a request to the Accounting Service API.
<b> For more details,how to syntax the request,see <a href="https://argoeu.github.io/argo-accounting/docs/api/project#get---project-hierarchical-structure">here</a></b>
</details>

### ASSOCIATE one or more Providers with a specific Project

<details>
You can associate one or more Providers with a specific Project.This is a required action,in order,to be able to assign Installations and Metrics.Apply a request to the Accounting Service API.
<b> For more details,how to syntax the request,see <a href="https://argoeu.github.io/argo-accounting/docs/api/project#post---associate-providers-with-a-specific-project">here.</a></b>
</details>

### DISSOCIATE Provider from a specific Project

<details>
If a Provider is associated with a specific Project, you can dessociate it.Provider can be dissociated only if no installations are assigned.Apply a request to the Accounting Service API.
<b> For more details,how to syntax the request,see <a href="https://argoeu.github.io/argo-accounting/docs/api/project#post---dissociate-providers-from-a-project">here.</a></b>
</details>

### FETCH Project's hierarchical structure

<details>
You can fetch the hierarchy of the Project (all Providers and Installations that are assigned to the Project). Apply a request to the Accounting Service API.
<b> For more details,how to syntax the request,see <a href="https://argoeu.github.io/argo-accounting/docs/api/project#get---project-hierarchical-structure">here.</a></b>
</details>

### FETCH all Projects

<details>
You can fetch all the Projects that are assigned to you.Apply a request to the Accounting Service API.
<b> For more details,how to syntax the request,see <a href="https://argoeu.github.io/argo-accounting/docs/api/project#get---fetch-all-projects">here.</a></b>
</details>

### Provide access roles on the Project

<details>
You can provide users with access roles on the Project.<br/>

**1.** Read registered clients ( see <a href="https://argoeu.github.io/argo-accounting/docs/api/client#get---read-the-registered-clients)">here</a>) and retrieve client's id.<br/>
**2.** Decide one or more roles,that this user will be assigned with,on the Project and apply a request to the Accounting Service API.
<b> For more details,how to syntax the request,see <a href="https://argoeu.github.io/argo-accounting/docs/api/project#post---access-control-entry-for-a-particular-project">here.</a></b>
</details>

### SEARCH Projects

<details>
If you are assigned with many Projects, you can search for specific Project/Projects,that match one or more criteria.You can define search criteria, on each field of the <b><a href="https://argoeu.github.io/argo-accounting/docs/api/project"> Project Collection</a></b> or a combination of search criteria on more than one fields.You can search by Project's acronym, title, period, call identifier or a combination of them.
Apply a request to the Accounting Service API.You need to provide the search criteria in a specific <b><a href="https://argoeu.github.io/argo-accounting/docs/guides/search-filter"> syntax</a></b>.
<b> For more details,how to syntax the request,see <a href="https://argoeu.github.io/argo-accounting/docs/api/project#post---search-for-projects">here.</a></b>
</details>

In the case the role assigned to you,on the Project,is administrative, you can perform all the actions on the Providers,described at this <b><a href="https://argoeu.github.io/argo-accounting/docs/guides/api_actions/provider">section</a></b>,as well as all the actions on Installations, described at this <b><a href="https://argoeu.github.io/argo-accounting/docs/guides/api_actions/installation">section</a></b>.

---
