---
id: provider
title: Manage Providers
sidebar_position: 2
---

This is a guide that refers to a Provider.
A Provider is the Organization that operates one or more Installations in one or more Projects.
If you are permitted to act on one or more Providers, via this guide you can see all the options you have .

### Before you start

You can manage a Provider assigned to a specific Project.<br/>

**1.** Register to the Accounting Service.<br/>
**2.** Contact the administrator of the Project,that this Provider is associated with,to assign you one or more roles on the Provider. 

**ΝΟΤΕ** <br/>
In the Accounting Service,the **_provider_admin_** role is the main role for managing a Provider.This role permits the user to perform any operation,on a specific Provider.
In case the user is assigned with any other role, he can operate according to the role's permissions.


## OPERATIONS 

--- 

### GET Provider's details
<details>
You can get the details of the Provider.Apply a request to the Accounting Service API. 
<b> For more details,how to syntax the request,see <a href="https://argoeu.github.io/argo-accounting/docs/api/provider#get---fetch-an-existing-provider">here</a></b>
</details>

### Provide access roles on the Provider
<details>
You can provide users with access roles on the Provider.<br/>

**1.** Read registered clients ( see <a href="https://argoeu.github.io/argo-accounting/docs/api/client#get---read-the-registered-clients)">here</a>) and retrieve client's id. <br/>
**2.** Decide one or more roles,that this user will be assigned with,on the Provider and apply a request to the Accounting Service API.
<b> For more details,how to syntax the request,see <a href="https://argoeu.github.io/argo-accounting/docs/api/provider#post---access-control-entry-for-a-particular-provider-of-a-specific-project">here.</a></b>
</details>

### ASSIGN Installations to the Provider
<details>
You can assign one or more Installations to the Provider.Apply a request to the Accounting Service API.
<b> For more details,how to syntax the request,see <a href="https://argoeu.github.io/argo-accounting/docs/api/installation#post---create-a-new-installation">here.</a></b>
</details>

### FETCH all Installations assigned to the provider
<details>
You can fetch all Installations,assigned to the Provider.Apply a request to the Accounting Service API.
<b> For more details,how to syntax the request,see <a href="https://argoeu.github.io/argo-accounting/docs/api/installation#get-fetch-all-provider-installations">here.</a></b>
</details>

### SEARCH installations, assigned to the provider
<details>
You can search for specific Installation/Installations,assigned to the Provider,that matches one or more criteria.You can define search criteria on each field of the <b><a href="https://argoeu.github.io/argo-accounting/docs/api/installation"> Installation Collection</a></b> or a combination of search criteria on more than one fields.You can search for Installations by Project, Provider, infrastracture, Installation's name, Metric Definition id or a combination of them. 
Apply a request to the Accounting Service API.You need to provide the search criteria in a specific <b><a href="https://argoeu.github.io/argo-accounting/docs/guides/search-filter"> syntax</a></b>. <b> For more details,how to syntax the request,see <a href="https://argoeu.github.io/argo-accounting/docs/api/installation#post---search-for-installations">here</a></b>
</details>

Also,if the role, assigned to you,is administrative,you are permitted to perform all the actions described at this <b><a href="https://argoeu.github.io/argo-accounting/docs/guides/installation">section</a></b>,on the Installations assigned to the Provider.

---
