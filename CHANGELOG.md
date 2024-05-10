# Changelog

---

All notable changes to this project will be documented in this file.

According to [Keep a Changelog](https://keepachangelog.com/en/1.0.0/) , the `Unreleased` section serves the following purposes:

-   People can see what changes they might expect in upcoming releases.
-   At release time, you can move the `Unreleased` section changes into a new release version section.

## Types of changes

---

-   `Added` for new features.
-   `Changed` for changes in existing functionality.
-   `Removed` for now removed features.
-   `Fixed` for any bug fixes.
-   `Security` in case of vulnerabilities.
-   `Deprecated` for soon-to-be removed features.

## Unreleased

---

## 1.2.0 - 2024-05-10

---

### Added

-   [#164](https://github.com/ARGOeu/argo-accounting/pull/164) - Upgrade quarkus version to 3.6.0.

## 1.1.0 - 2023-05-10

---

### Added

-   [#153](https://github.com/ARGOeu/argo-accounting/pull/153) - Add Resource field in Metric entity.
-   [#152](https://github.com/ARGOeu/argo-accounting/pull/152) - Add support for Resource.
-   [#143](https://github.com/ARGOeu/argo-accounting/pull/143) - Add CITATION.cff, CODE_OF_CODUCT.md, CONTRIBUTING.md files.
-   [#142](https://github.com/ARGOeu/argo-accounting/pull/142) - Enhance Metric Response.
-   [#141](https://github.com/ARGOeu/argo-accounting/pull/141) - Accounting Service health checks.
-   [#138](https://github.com/ARGOeu/argo-accounting/pull/138) - Docusaurus Guideline for managing Metric Definitions via UI.

### Changed

-   [#139](https://github.com/ARGOeu/argo-accounting/pull/139) - Use another endpoint for retrieving the EOSC Providers.

### Removed

-   [#144](https://github.com/ARGOeu/argo-accounting/pull/144) - Remove redundant endpoints.


## 1.0.0 - 2023-01-31

---

### Added

-   [#2](https://github.com/ARGOeu/argo-accounting/pull/2)     - Support of Metric Definitions.
-   [#10](https://github.com/ARGOeu/argo-accounting/pull/10)   - Support of Metrics.
-   [#38](https://github.com/ARGOeu/argo-accounting/pull/38)   - Support of Projects.
-   [#39](https://github.com/ARGOeu/argo-accounting/pull/39)   - Support of Providers.
-   [#43](https://github.com/ARGOeu/argo-accounting/pull/43)   - Support of Installations.
-   [#52](https://github.com/ARGOeu/argo-accounting/pull/52)   - Authentication in Accounting Service.
-   [#57](https://github.com/ARGOeu/argo-accounting/pull/57)   - Authorization in Accounting Service.
-   [#96](https://github.com/ARGOeu/argo-accounting/pull/96)   - Support Search on Metrics.     
-   [#123](https://github.com/ARGOeu/argo-accounting/pull/123) - Client's general permissions.
-   [#129](https://github.com/ARGOeu/argo-accounting/pull/129) - Docusaurus Guideline of Setting up a Project.
-   [#131](https://github.com/ARGOeu/argo-accounting/pull/131) - Add value's unit type info in Metric.
-   [#132](https://github.com/ARGOeu/argo-accounting/pull/132) - Manage Unit Types through Accounting Service API.
-   [#133](https://github.com/ARGOeu/argo-accounting/pull/133) - Manage Metric Types through Accounting Service API.
  
### Changed

-   [#118](https://github.com/ARGOeu/argo-accounting/pull/118) - Change Infrastructure in Installation to optional.
  
### Deprecated

-   [#132](https://github.com/ARGOeu/argo-accounting/pull/132) & [#133](https://github.com/ARGOeu/argo-accounting/pull/133) - The "unit-types" and "metric-types" actions, which belong to the Metric Definition endpoint, have been deprecated.