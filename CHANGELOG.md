# Changelog

---

All notable changes to this project will be documented in this file.

According to [Keep a Changelog](https://keepachangelog.com/en/1.0.0/) , the
`Unreleased` section serves the following purposes:

- People can see what changes they might expect in upcoming releases.
- At release time, you can move the `Unreleased` section changes into a new
  release version section.

## Types of changes

---

- `Added` for new features.
- `Changed` for changes in existing functionality.
- `Removed` for now removed features.
- `Fixed` for any bug fixes.
- `Security` in case of vulnerabilities.
- `Deprecated` for soon-to-be removed features.

## Unreleased

---

## Unreleased - Added

- [#219](https://github.com/ARGOeu/argo-accounting/pull/219) - ACC-345 Sys Admin 
  should be able to manage all projects.
- [#221](https://github.com/ARGOeu/argo-accounting/pull/221) - ACC-355 Implement multitenancy 
  via supporting different OIDC providers.
- [#223](https://github.com/ARGOeu/argo-accounting/pull/223) - ACC-360 Installation
  Report.
- [#224](https://github.com/ARGOeu/argo-accounting/pull/224) - ACC-361 Provider
  Report.
- [#225](https://github.com/ARGOeu/argo-accounting/pull/225) - ACC-362 Project
  Report.
- [#226](https://github.com/ARGOeu/argo-accounting/pull/226) - ACC-369 Aggregated 
  Provider report.
- [#227](https://github.com/ARGOeu/argo-accounting/pull/227) - ACC-366 Add issuer 
  in the client's id.

## 1.6.1 - 2025-03-21

---

### 1.6.1 - Added

- [#211](https://github.com/ARGOeu/argo-accounting/pull/211) - ACC-319 Create 
  an admin-only endpoint to update user information.
- [#213](https://github.com/ARGOeu/argo-accounting/pull/213) - ACC-322 Fix Project 
  and Provider not to accept full stop in id when are generated.


## 1.6.0 - 2025-03-13

---

### 1.6.0 - Added

- [#190](https://github.com/ARGOeu/argo-accounting/pull/190) - ACC-256 Add
  start/end field in metrics retrieval API calls for user and group ids.
- [#192](https://github.com/ARGOeu/argo-accounting/pull/192) - ACC-250
  Additional Information for the Clients.
- [#197](https://github.com/ARGOeu/argo-accounting/pull/197) - ACC-287 Add
  version in API.
- [#200](https://github.com/ARGOeu/argo-accounting/pull/200) - ACC-245 Make
  field unit_of_access OPTIONAL.
- [#202](https://github.com/ARGOeu/argo-accounting/pull/202) - ACC-297 Add API
  call to delete a project.
- [#203](https://github.com/ARGOeu/argo-accounting/pull/203) - ACC-299 Add API
  call to delete a resource.
- [#206](https://github.com/ARGOeu/argo-accounting/pull/206) - ACC-317 API 
  to fetch Document Counts by Time Period in MongoDB.
- 
### 1.6.0 - Changed

- [#191](https://github.com/ARGOeu/argo-accounting/pull/191) - ACC-252
  Registered clients should not be visible to everyone.

### 1.6.0 - Fixed

- [#193](https://github.com/ARGOeu/argo-accounting/pull/193) - ACC-274 Update
  Group ID Path Name to Resolve Conflict with User ID Path.
- [#194](https://github.com/ARGOeu/argo-accounting/pull/194) - ACC-275 Fix
  Search of Metrics by Start or End Date.
- [#195](https://github.com/ARGOeu/argo-accounting/pull/195) - ACC-286 Project
  entry "lost" when updated.

## 1.5.1 - 2024-07-24

---

### 1.5.1 - Added

- [#187](https://github.com/ARGOeu/argo-accounting/pull/187) - ACC-248 Extract
  'sub' or 'client-id' from Token Introspection.

## 1.5.0 - 2024-07-15

---

### 1.5.0 - Added

- [#184](https://github.com/ARGOeu/argo-accounting/pull/184) - ACC-243 Add
  support for service account AAI.

## 1.4.0 - 2024-07-08

---

### 1.4.0 - Added

- [#178](https://github.com/ARGOeu/argo-accounting/pull/178) - ACC-237 Add API
  call to modify Project.
- [#179](https://github.com/ARGOeu/argo-accounting/pull/179) - ACC-239 Create a
  new API call that returns all metrics under a specific group id.
- [#180](https://github.com/ARGOeu/argo-accounting/pull/180) - ACC-242 New API
  call that returns all metrics under a specific user id.

## 1.3.0 - 2024-05-11

---

### 1.3.0 - Added

- [#173](https://github.com/ARGOeu/argo-accounting/pull/173) - ACC-228 Adapt
  Metric schema to add support for Groups and Users.
- [#174](https://github.com/ARGOeu/argo-accounting/pull/174) - ACC-235 Create
  New Endpoint for Project Creation.

## 1.2.1 - 2024-05-22

---

### 1.2.1 - Changed

- [#169](https://github.com/ARGOeu/argo-accounting/pull/169) - EOSC Resource Not
  Supported.

## 1.2.0 - 2024-05-10

---

### 1.2.0 - Added

- [#164](https://github.com/ARGOeu/argo-accounting/pull/164) - Upgrade quarkus
  version to 3.6.0.

## 1.1.0 - 2023-05-10

---

### 1.1.0 - Added

- [#153](https://github.com/ARGOeu/argo-accounting/pull/153) - Add Resource
  field in Metric entity.
- [#152](https://github.com/ARGOeu/argo-accounting/pull/152) - Add support for
  Resource.
- [#143](https://github.com/ARGOeu/argo-accounting/pull/143) - Add CITATION.cff,
  CODE_OF_CODUCT.md, CONTRIBUTING.md files.
- [#142](https://github.com/ARGOeu/argo-accounting/pull/142) - Enhance Metric
  Response.
- [#141](https://github.com/ARGOeu/argo-accounting/pull/141) - Accounting
  Service health checks.
- [#138](https://github.com/ARGOeu/argo-accounting/pull/138) - Docusaurus
  Guideline for managing Metric Definitions via UI.

### 1.1.0 - Changed

- [#139](https://github.com/ARGOeu/argo-accounting/pull/139) - Use another
  endpoint for retrieving the EOSC Providers.

### 1.1.0 - Removed

- [#144](https://github.com/ARGOeu/argo-accounting/pull/144) - Remove redundant
  endpoints.

## 1.0.0 - 2023-01-31

---

### 1.0.0 - Added

- [#2](https://github.com/ARGOeu/argo-accounting/pull/2) - Support of Metric
  Definitions.
- [#10](https://github.com/ARGOeu/argo-accounting/pull/10) - Support of Metrics.
- [#38](https://github.com/ARGOeu/argo-accounting/pull/38) - Support of
  Projects.
- [#39](https://github.com/ARGOeu/argo-accounting/pull/39) - Support of
  Providers.
- [#43](https://github.com/ARGOeu/argo-accounting/pull/43) - Support of
  Installations.
- [#52](https://github.com/ARGOeu/argo-accounting/pull/52) - Authentication in
  Accounting Service.
- [#57](https://github.com/ARGOeu/argo-accounting/pull/57) - Authorization in
  Accounting Service.
- [#96](https://github.com/ARGOeu/argo-accounting/pull/96) - Support Search on
  Metrics.
- [#123](https://github.com/ARGOeu/argo-accounting/pull/123) - Client's general
  permissions.
- [#129](https://github.com/ARGOeu/argo-accounting/pull/129) - Docusaurus
  Guideline of Setting up a Project.
- [#131](https://github.com/ARGOeu/argo-accounting/pull/131) - Add value's unit
  type info in Metric.
- [#132](https://github.com/ARGOeu/argo-accounting/pull/132) - Manage Unit Types
  through Accounting Service API.
- [#133](https://github.com/ARGOeu/argo-accounting/pull/133) - Manage Metric
  Types through Accounting Service API.

### 1.0.0 - Changed

- [#118](https://github.com/ARGOeu/argo-accounting/pull/118) - Change
  Infrastructure in Installation to optional.

### 1.0.0 - Deprecated

- [#132](https://github.com/ARGOeu/argo-accounting/pull/132) &
  [#133](https://github.com/ARGOeu/argo-accounting/pull/133) - The "unit-types"
  and "metric-types" actions, which belong to the Metric Definition endpoint,
  have been deprecated.
