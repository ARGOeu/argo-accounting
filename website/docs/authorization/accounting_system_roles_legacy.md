---
id: accounting_system_roles_legacy
title: Accounting Roles (legacy)
sidebar_position: 2
---

# Accounting Roles (Legacy)

## Default Actions

When clients register to the system, they can perform the following actions:

- Read all the metric definitions

| Collection       | Read   |
|------------------|--------|
| MetricDefinition | Always |

- Read all the providers

| Collection | Read   |
|-------------|--------|
| Provider    | Always |

- Read all the Unit Types

| Collection | Read   |
|-------------|--------|
| UnitType    | Always |

- Read all the Metric Types

| Collection | Read   |
|-------------|--------|
| MetricType  | Always |

- Read all the clients

| Collection | Read   |
|-------------|--------|
| Client      | Always |

By default, in the accounting system, the following roles exist and can be assigned to the registered client.

---

## Roles

### Project Admin

Project admin role has full access on the projects it is assigned and can perform all actions on the projects as well as the providers, installations, and metrics that are assigned to these projects.

**On an assigned project and the providers/installation/metrics of the project, the project admin role can:**

- Read the project
- Associate and dissociate providers on the project
- Grant access to a client with a specific role on a project

| Collection | Associate | Dissociate | Read    | Acl     |
|-------------|------------|-------------|----------|----------|
| Project     | Always *   | Always *    | Always * | Always * |

- Create/Update/Delete/Read Installations on a project
- Grant access to a client with a specific role on an installation

| Collection    | Create  | Update  | Delete  | Read    | Acl     |
|----------------|---------|---------|---------|---------|----------|
| Installation   | Always * | Always * | Always * | Always * | Always * |

- Create/Update/Delete/Read Metrics on a project

| Collection | Create  | Update  | Delete  | Read    |
|-------------|---------|---------|---------|---------|
| Metric      | Always * | Always * | Always * | Always * |

- Grant access to a client with a specific role on a provider

| Collection | Acl     |
|-------------|---------|
| Provider    | Always * |

\* You can always perform that action on a particular Project.

**They can also:**

- Create providers
- Read all the providers
- Update/Delete their created providers

| Collection | Create  | Update  | Delete  | Read    |
|-------------|---------|---------|---------|---------|
| Provider    | Always  | Entity * | Entity * | Always  |

\* You cannot update or delete an existing Provider if it belongs to a Project.

- Create metric definitions
- Read all the metric definitions
- Update/Delete their created metric definitions

| Collection        | Create  | Update  | Delete  | Read    |
|------------------|---------|---------|---------|---------|
| MetricDefinition | Always  | Entity ** | Entity ** | Always  |

\** You cannot update or delete an existing Metric Definition if there are Metrics assigned to it.

- Create Unit Types
- Read all the Unit Types
- Update/Delete their created Unit Types

| Collection | Create  | Update  | Delete  | Read    |
|-------------|---------|---------|---------|---------|
| UnitType    | Always  | Entity *** | Entity *** | Always  |

\*** You cannot update or delete a Unit Type registered by Accounting Service or a Unit Type used in an existing Metric Definition.

- Create Metric Types
- Read all the Metric Types
- Update/Delete their created Metric Types

| Collection  | Create  | Update  | Delete  | Read    |
|--------------|---------|---------|---------|---------|
| MetricType   | Always  | Entity **** | Entity **** | Always  |

\**** You cannot update or delete a Metric Type registered by Accounting Service or a Metric Type used in an existing Metric Definition.

---

### Provider Admin

A provider admin role has full access on the providers it is assigned and can perform all actions on the installations as well as the metrics that are assigned to the specific providers.

**On an assigned provider and the installations/metrics of the provider, the provider admin role can:**

- Create/Update/Delete/Read Installations on a specific Provider
- Grant access to a client with a specific role on an installation

| Collection    | Create  | Update  | Delete  | Read    | Acl     |
|----------------|---------|---------|---------|---------|----------|
| Installation   | Always * | Always * | Always * | Always * | Always * |

- Create/Update/Delete/Read Metrics on an Installation

| Collection | Create  | Update  | Delete  | Read    |
|-------------|---------|---------|---------|---------|
| Metric      | Always * | Always * | Always * | Always * |

- Grant access to a client with a specific role on a provider

| Collection | Acl     |
|-------------|---------|
| Provider    | Always * |

\* You can always perform that action on a particular Provider.

---

### Installation Admin

An installation admin role has full access on the installations it is assigned and can perform all actions on the metrics that are assigned to the specific installations.

**On an assigned installation and the metrics of the installation, the installation admin role can:**

- Read/Update/Delete installation
- Grant access to a client with a specific role on an installation

| Collection    | Update  | Delete  | Read    | Acl     |
|----------------|---------|---------|---------|----------|
| Installation   | Always * | Always * | Always * | Always * |

- Create/Read/Update/Delete metrics on an installation

| Collection | Create  | Update  | Delete  | Read    |
|-------------|---------|---------|---------|---------|
| Metric      | Always * | Always * | Always * | Always * |

\* You can always perform that action on a particular Installation.

---

## Assigning Roles to a Client using the Legacy Mechanism

As we have already mentioned, a client can interact with a Project-Provider-Installation relationship only if it has been assigned one of the following roles:

- [Project Admin](#project-admin)
- [Provider Admin](#provider-admin)
- [Installation Admin](#installation-admin)

Initially, no client has a role. The Accounting System Administrators can initially turn any client into a Project Admin.

| ![](assets/info.png) | Contact accounting@einfra.grnet.gr for Project Admin. |
|-----------------------|------------------------------------------------------|

Once you have been assigned the Project Admin role, you can in turn grant roles to other clients.  
Generally, any of the above roles can assign roles to other clients at the level of the Project-Provider-Installation relationship they manage.

---

### Assigning Roles to a Client as a Project Admin

As a Project Admin, you can grant the above roles to other clients for a specific Project:

- [You can turn a client into a Project Admin for the Project you manage.](/docs/api/project#post-access-control)
- [You can turn a client into a Provider Admin for the Providers belonging to the Project you manage.](/docs/api/provider#post---access-control-entry-for-a-particular-provider-of-a-specific-project)
- [You can turn a client into an Installation Admin for the Installations belonging to the Project you manage.](/docs/api/installation#post---access-control-entry-for-a-particular-installation)

---

### Assigning Roles to a Client as a Provider Admin

As a Provider Admin, you can grant the Provider Admin and Installation Admin roles to other clients for a particular Provider:

- [You can turn a client into a Provider Admin for the Provider you manage.](/docs/api/provider#post---access-control-entry-for-a-particular-provider-of-a-specific-project)
- [You can turn a client into an Installation Admin for the Installations belonging to the Provider you manage.](/docs/api/installation#post---access-control-entry-for-a-particular-installation)

---

### Assigning Roles to a Client as an Installation Admin

As an Installation Admin, you can grant the Installation Admin role to other clients for a particular Installation:

- [You can turn a client into an Installation Admin for the Installation you manage.](/docs/api/installation#post---access-control-entry-for-a-particular-installation)

