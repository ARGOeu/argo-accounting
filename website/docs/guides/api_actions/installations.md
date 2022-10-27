---
id: installation
title: Manage Installations
sidebar_position: 3
---

This is a guide that refers to an Installation. An Installation refers to a specific instance or part of a resource/service that is allocated to a specific Project by one Provider. If you are permitted to act on one or ore Installations, via this guide you can see all the options you have .

### Before you start

You can manage an Installation assigned to a specific Project and Provider.<br/>
**1.** Register to the Accounting Service.<br/>
**2.** Contact the administrator of the Project or the administrator of the Project's Provider,that this Installation is associated with,to assign you one or more roles on the Installation.

**ΝΟΤΕ** <br/>
In the Accounting Service, the **_installation_admin_** role is the main role for managing an Installation.This role permits the user to perform any operation,on a specific Installation.
In case the user is assigned with any other role, he can operate according to the role's permissions.

## OPERATIONS 

--- 

### GET Installation's details 
<details>
You can get the details of the Installation.Apply a request to the Accounting Service API.
<b> For more details,how to syntax the request,see <a href="https://argoeu.github.io/argo-accounting/docs/api/installation#get---fetch-an-existing-installation">here</a></b>
</details>


### UPDATE the Installation
<details>
You can update the Installation.Apply a request to the Accounting Service API,providing the new values of the Installation's properties.
<b> For more details,how to syntax the request,see <a href="https://argoeu.github.io/argo-accounting/docs/api/installation#patch---update-an-existing-installation">here.</a></b>
</details>

### DELETE the Installation
<details>
You can delete the Installation.Apply a request to the Accounting Service API.If Metrics are assigned to the Installation,no DELETE action can take place.In this case,you need to delete all the assigned Metrics.
<b> For more details,how to syntax the request,see <a href="https://argoeu.github.io/argo-accounting/docs/api/installation#delete---delete-an-existing-installation">here.</a></b>
</details>

---

