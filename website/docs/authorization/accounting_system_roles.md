---
id: accounting_system_roles
title: Accounting Roles
sidebar_position: 1
---

# Accounting Roles

The Accounting Service is shifting to using AAI groups and entitlements for centralized, secure, and streamlined access control. This simplifies the codebase, speeds up onboarding, and enables flexible role management.

## What It Offers

- **Centralized access control** through AAI groups and entitlements, ensuring consistent permissions across services.
- **Simplified user onboarding**: new users gain access automatically via group membership without manual API calls.
- **Flexible role management**: easily integrate custom roles such as *Project Viewer* or *Installation Admin*.
- **Enhanced security** with unified, policy-driven authorization and auditing capabilities.
- **Simplified OIDC multitenancy support** through standardized entitlement management.
- **Reduced service complexity** by offloading authorization logic to AAI, keeping the Accounting Service codebase clean and maintainable.

---

## Mapping of Roles to Entitlements

This section defines how system roles are linked to AAI entitlements (roles, groups, and permissions) that grant specific access rights.

## Project Viewer/Admin

**Entitlements:**

- `<NAMESPACE>:group:accounting:myproject:role=viewer/admin`
- `<NAMESPACE>:group:accounting:operations:resources:role=viewer/admin`

**Description:**

- Grants project-level administration for `myproject`, and its underneath sub-groups in view/admin mode.
- Allows creation of providers, metric definitions, metric types, unit types, and installation's resources.

For the viewer role (`role=viewer`), access is read-only. For the admin role (`role=admin`), full create/update/delete permissions are granted where applicable.

**Permissions on Assigned Project and Related Entities:**

| Collection   | Create | Update | Delete | Read   |
|--------------|--------|--------|--------|--------|
| Provider     | Always (admin only) | Entity* (admin only) | Entity* (admin only) | Always |
| Installation | Always (admin only) | Always (admin only) | Always (admin only) | Always |
| Metric       | Always (admin only) | Always (admin only) | Always (admin only) | Always |

* Cannot update or delete if associated with other entities.

![Project Viewer/Admin](./assets/project-viewer-admin.png)

## Project Provider Viewer/Admin

**Entitlements:**

- `<NAMESPACE>:group:accounting:myproject:GRNET:role=viewer/admin`
- `<NAMESPACE>:group:accounting:operations:resources:role=viewer/admin`

**Description:**

- Grants administration for a specific provider (`GRNET`) within project `myproject`, and its underneath sub-groups, in view/admin mode.
- Allows creation of providers, metric definitions, metric types, unit types, and installation's resources.

For the viewer role (`role=viewer`), access is read-only. For the admin role (`role=admin`), full create/update/delete permissions are granted where applicable.

**Permissions on Assigned Provider and Related Entities:**

| Collection   | Create | Update | Delete | Read   |
|--------------|--------|--------|--------|--------|
| Installation | Always (admin only) | Always (admin only) | Always (admin only) | Always |
| Metric       | Always (admin only) | Always (admin only) | Always (admin only) | Always |

![Project Provider Viewer/Admin](./assets/project-provider-viewer-admin.png)

## Installation Viewer/Admin

**Entitlements:**

- `<NAMESPACE>:group:accounting:myproject:GRNET:GRNET-notebook:role=viewer/admin`
- `<NAMESPACE>:group:accounting:operations:resources:role=viewer/admin`

**Description:**

- Grants administration for installation `GRNET-notebook` under provider `GRNET` in project `myproject`, and its underneath sub-groups, in view/admin mode.
- Allows creation of providers, metric definitions, metric types, unit types, and installation's resources.

For the viewer role (`role=viewer`), access is read-only. For the admin role (`role=admin`), full create/update/delete permissions are granted where applicable.

**Permissions on Assigned Installation and Related Entities:**

| Collection   | Create | Update | Delete | Read   |
|--------------|--------|--------|--------|--------|
| Installation | -      | Always (admin only) | Always (admin only) | Always |
| Metric       | Always (admin only) | Always (admin only) | Always (admin only) | Always |

![Installation Viewer/Admin](./assets/installation-viewer-admin.png)

## System Viewer/Admin

**Entitlements:**

- `<NAMESPACE>:group:accounting:role=viewer/admin`

**Description:**

- System-wide role: full access to view/manage providers, projects, installations, and resources (metric definitions, metric types, unit types, and installation's resources).

For the viewer role (`role=viewer`), access is read-only across the system. For the admin role (`role=admin`), full create/update/delete permissions are granted system-wide.

**System-Wide Permissions:**

| Collection       | Create | Update | Delete | Read   |
|------------------|--------|--------|--------|--------|
| Provider         | Always (admin only) | Entity* (admin only) | Entity* (admin only) | Always |
| Project          | Always (admin only) | Always (admin only) | Always (admin only) | Always |
| Installation     | Always (admin only) | Always (admin only) | Always (admin only) | Always |
| Metric           | Always (admin only) | Always (admin only) | Always (admin only) | Always |
| MetricDefinition | Always (admin only) | Entity** (admin only) | Entity** (admin only) | Always |
| UnitType         | Always (admin only) | Entity*** (admin only) | Entity*** (admin only) | Always |
| MetricType       | Always (admin only) | Entity**** (admin only) | Entity**** (admin only) | Always |

* Cannot update or delete if belongs to a project.  
** Cannot update or delete if metrics are assigned.  
*** Cannot update or delete if registered by service or used in metric definition.  
**** Cannot update or delete if registered by service or used in metric definition.

![System Viewer/Admin](./assets/system-viewer-admin.png)

## Provider Representative Viewer/Admin

**Entitlements:**

- `<NAMESPACE>:group:accounting:roles:provider:GRNET:role=viewer/admin`
- `<NAMESPACE>:group:accounting:operations:resources:role=viewer/admin`

**Description:**

- Provider Representative can view/admin provider `GRNET` in all projects where `GRNET` is associated with.
- Allows creation of providers, metric definitions, metric types, unit types, and installation's resources.

For the viewer role (`role=viewer`), access is read-only across associated projects. For the admin role (`role=admin`), full create/update/delete permissions are granted where applicable.

**Permissions on Associated Providers and Related Entities:**

| Collection   | Create | Update | Delete | Read   |
|--------------|--------|--------|--------|--------|
| Installation | Always (admin only) | Always (admin only) | Always (admin only) | Always |
| Metric       | Always (admin only) | Always (admin only) | Always (admin only) | Always |

![Provider Representative Viewer/Admin](./assets/provider-representative-viewer-admin.png)

## Custom Roles: Project Provider Reader and Installation Admin

**Entitlements:**

- `<NAMESPACE>:group:accounting:myproject:GRNET:role=viewer`
- `<NAMESPACE>:group:accounting:myproject:GRNET:GRNET-HPC:role=admin`
- `<NAMESPACE>:group:accounting:operations:resources:role=viewer`

**Description:**

- Grants administration for a specific provider (`GRNET`) within project `myproject`, and its underneath sub-groups, in view mode.
- Grants administration for installation `GRNET-HPC` under provider `GRNET` in project `myproject`, and its underneath sub-groups in admin mode.
- Allows only to view resources: providers, metric definitions, metric types, unit types, and installation's resources.

**Permissions:**

| Collection   | Create | Update | Delete | Read   |
|--------------|--------|--------|--------|--------|
| Provider     | -      | -      | -      | Always |
| Installation | -      | Always (on GRNET-HPC) | Always (on GRNET-HPC) | Always |
| Metric       | -      | Always (on GRNET-HPC) | Always (on GRNET-HPC) | Always |

![Custom Roles](./assets/custom-roles.png)

## Note

Contact Accounting Service support for guidance on how to set up entitlements for your account.
