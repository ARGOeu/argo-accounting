---
id: accounting-library
title: Accounting Library
sidebar_position: 9
---

# Accounting Library

The ARGO Accounting Library is a lightweight Python library (adapter) that provides a convenient interface for interacting with the ARGO Accounting Service REST API. It is designed to be used by infrastructure providers, service operators, and service components that need to retrieve or push metrics or information related to installations, providers, and projects.

The library's source code and examples are available on [GitHub](https://github.com/ARGOeu/argo-acc-library).

## Overview

The Accounting System is responsible for collecting, aggregating, and exchanging accounting metrics across different infrastructures and stakeholders. The Accounting Library acts as a client-side adapter that:

- Authenticates against the ARGO Accounting Service
- Retrieves registered entities (installations, projects, providers)
- Fetches accounting metrics
- Pushes new metric values to the service

The library wraps the Accounting Service REST API and abstracts HTTP calls, authentication headers, and response handling.

## Supported Environments

The library has been tested with:

- **Python**: 3.9, 3.11, 3.12
- **Operating Systems**: Rocky Linux 9 (RHEL-compatible systems)

## Installation

### From PyPI

To install the latest version of **argo-acc-library** from PyPI, run:

```bash
pip install argo-acc-library
```

### From Source

Clone the repository and install using `pip`:

```bash
git clone https://github.com/ARGOeu/argo-acc-library.git
cd argo-acc-library
pip install .
```

Clone the repository and install using `setuptools`:

```bash
python3 ./setup.py build && sudo python3 ./setup.py install
```

### RPM-based Installation (RHEL / Rocky / Alma)

You can also build an RPM package:

```bash
python3 ./setup.py build && sudo python3 ./setup.py bdist_rpm
```

Install the generated RPM (located under `dist/`):

```bash
sudo dnf install ./dist/argo-acc-library-<version>.noarch.rpm
```

## Authentication

Before you can use the library or make any API calls, you must first obtain a JSON Web Token (JWT). This token acts as a temporary access key that proves your identity. Each token is valid for one hour, after which you must request a new one.

To obtain a JWT, follow the steps described in the Authenticating Clients section. Once you have the token, include it with every request to the Accounting Library until it expires.

Follow the instructions in the [Authenticating Clients](../category/authenticating-clients) section.

Once a token is available, initialize the Accounting Service client:

```python
from argo_acc_library import ArgoAccountingService

acc = ArgoAccountingService(
    endpoint="ACCOUNTING_SERVICE_INSTANCE",
    token="<your_jwt>"
)
```
Replace the `ACCOUNTING_SERVICE_INSTANCE` with the appropriate environment found in [table](../authentication/oidc_providers#oidc-providers-registered-in-the-accounting-service).

## Authorization

Access to the Accounting Service is controlled via roles and entitlements. Each system role is mapped to specific permissions that define what actions a user or service can perform. To use the Accounting Library, you must have an appropriate role assigned.

For detailed mappings of system roles to entitlements and their associated permissions, see the [Mapping of Roles to Entitlements](../authorization/accounting_system_roles) section.  

Ensure that your JWT token includes the necessary entitlements; otherwise, API calls may be rejected due to insufficient permissions.

## Usage Examples

The repository's [examples](https://github.com/ARGOeu/argo-acc-library/tree/main/examples) directory contains practical, ready-to-run scripts that showcase common use cases. These scripts leverage the library's Python code to perform various actions.

All examples support the `--help` flag for detailed usage instructions.

### Prerequisites

* Save your valid JWT token in a file, e.g., `~/acc.jwt`.
* Replace the `ACCOUNTING_SERVICE_INSTANCE` with the appropriate environment found in [table](../authentication/oidc_providers#oidc-providers-registered-in-the-accounting-service).

### 1. List Registered Projects

```python
from argo_acc_library import ArgoAccountingService

with open("~/acc.jwt") as f:
    token = f.read()

acc = ArgoAccountingService("ACCOUNTING_SERVICE_INSTANCE", token)

for project in acc.projects:
    print(f"{project.title} (ID: {project.id})")
```

**Explanation**
- Initializes the Accounting Service client
- Iterates over the `projects` collection
- Each `Project` object exposes fields such as `id` and `title`

#### Bash wrapper

This example ([get_projects.py](https://github.com/ARGOeu/argo-acc-library/blob/main/examples/get_projects.py)) retrieves the list of all registered Projects and iterates over them, printing each project’s identifier and title.

```bash
python3 examples/get_projects.py \
  --host ACCOUNTING_SERVICE_INSTANCE \
  --token ~/acc.jwt -f
```

### 2. List Registered Providers
```python
from argo_acc_library import ArgoAccountingService

acc = ArgoAccountingService("ACCOUNTING_SERVICE_INSTANCE", open("~/acc.jwt").read())

for provider in acc.providers:
    print(f"{provider.title} (ID: {provider.id})")
```

**Explanation**
- Initializes the Accounting Service client
- Iterates over the `providers` collection
- Each `Provider` object exposes fields such as `id` and `name`

#### Bash wrapper

This example ([get_providers.py](https://github.com/ARGOeu/argo-acc-library/blob/main/examples/get_providers.py)) queries the Accounting Service for all registered Providers and iterates over them to print their identifiers and names.

```bash
python3 examples/get_providers.py \
  --host ACCOUNTING_SERVICE_INSTANCE \
  --token ~/acc.jwt -f
```

### 3. List Registered Installations

```python
from argo_acc_library import ArgoAccountingService

acc = ArgoAccountingService("ACCOUNTING_SERVICE_INSTANCE", open("~/acc.jwt").read())

for installation in acc.installations:
    print(installation)  # JSON representation
```

**Explanation**
- Initializes the Accounting Service client
- Iterates over the `installations` collection
- Each `Installation` serialize itself as JSON string

#### Bash wrapper

This example ([get_installations.py](https://github.com/ARGOeu/argo-acc-library/blob/main/examples/get_installations.py)) retrieves all registered Installations from the Accounting Service and iterates over the result set, printing the installation attributes as JSON string.

```bash
python3 examples/get_installations.py \
  --host ACCOUNTING_SERVICE_INSTANCE \
  --token ~/acc.jwt -f
```

### 4. Retrieve Metrics for a Project

```python
from argo_acc_library import ArgoAccountingService

acc = ArgoAccountingService("ACCOUNTING_SERVICE_INSTANCE", open("~/acc.jwt").read())

project = acc.projects["<PROJECT_ID>"]

for metric in project.metrics:
    print(metric.value, metric.metric_definition.metric_type)
```

**Explanation**
- Initializes the Accounting Service client
- Iterates over the `metrics` collection of a `Project`
- Each `Metric` object exposes fields such as `value` and the `metric_type` of the associated `metric_definition`
- `PROJECT_ID` must be set accordingly.

#### Bash wrapper

This example ([get_project_metrics.py](https://github.com/ARGOeu/argo-acc-library/blob/main/examples/get_project_metrics.py)) fetches all metrics associated with a specific Project and loops over the results to inspect metric definitions and reported values.

```bash
python3 examples/get_project_metrics.py \
  --host ACCOUNTING_SERVICE_INSTANCE \
  --token ~/acc.jwt -f \
  --project <PROJECT_ID>
```

### 5. Retrieve Metrics for a Provider under a specific Project
```python
from argo_acc_library import ArgoAccountingService

acc = ArgoAccountingService("ACCOUNTING_SERVICE_INSTANCE", open("~/acc.jwt").read())

provider = acc.projects["<PROJECT_ID>"].providers["<PROVIDER_ID>"]

for metric in provider.metrics:
    print(metric.value, metric.metric_definition.metric_type)
```

**Explanation**
- Initializes the Accounting Service client
- Providers are accessed in the context of a project
- Metrics are automatically scoped to the project/provider combination
- `PROJECT_ID` and `PROVIDER_ID` must be set accordingly.

#### Bash wrapper

This example ([get_provider_metrics.py](https://github.com/ARGOeu/argo-acc-library/blob/main/examples/get_provider_metrics.py)) fetches all metrics associated with a specific Provider under a specific Project and loops over the results to inspect metric definitions and reported values.

```bash
python3 examples/get_provider_metrics.py \
  --host ACCOUNTING_SERVICE_INSTANCE \
  --token ~/acc.jwt -f \
  --project <PROJECT_ID> \
  --provider <PROVIDER_ID>
```

### 6. Retrieve Metrics for an Installation

```python
from argo_acc_library import ArgoAccountingService

acc = ArgoAccountingService("ACCOUNTING_SERVICE_INSTANCE", open("~/acc.jwt").read())

installation = acc.installations["<INSTALLATION_ID>"]

for metric in installation.metrics:
    print(metric.value, metric.metric_definition.metric_type)
```

**Explanation**
- Initializes the Accounting Service client
- Installations expose their associated metrics
- Each `Metric` object exposes fields such as `value` and the `metric_type` of the associated `metric_definition`
- `INSTALLATION_ID` must be set accordingly.

#### Bash wrapper

This example ([get_installation_metrics.py](https://github.com/ARGOeu/argo-acc-library/blob/main/examples/get_installation_metrics.py)) queries all accounting metrics associated with a specific Installation and loops over them to display the metric definition identifiers and their corresponding values.

```bash
python3 examples/get_installation_metrics.py \
  --host ACCOUNTING_SERVICE_INSTANCE \
  --token ~/acc.jwt -f \
  --installation <INSTALLATION_ID>
```

### 7. Push Installation Metrics

```python
from argo_acc_library import ArgoAccountingService

acc = ArgoAccountingService("ACCOUNTING_SERVICE_INSTANCE", open("~/acc.jwt").read())

acc.installations["<INSTALLATION_ID>"].metrics.add({
    "metric_definition_id": "<METRICDEF_ID>",
    "time_period_start": "2025-01-01T00:00:00Z",
    "time_period_end": "2025-01-31T23:59:59Z",
    "value": 100,
    "group_id": "group-01",
    "user_id": "user-01",
})
```

**Explanation**
- Initializes the Accounting Service client
- Uses `metrics.add()` to POST a new metric entry
- `INSTALLATION_ID` and `METRIC_DEFINITION_ID` must be set accordingly.
- The metric value will be associated with this particular installation and metric definition

#### Bash wrapper

This example ([add_installation_metric.py](https://github.com/ARGOeu/argo-acc-library/blob/main/examples/add_installation_metric.py)) demonstrates how to submit (push) a new metric entry for a given Installation by providing the metric definition, time window, value, and user/group context.

```bash
python3 examples/add_installation_metric.py \
  --host ACCOUNTING_SERVICE_INSTANCE \
  --token ~/acc.jwt -f \
  --installation <INSTALLATION_ID> \
  --metricdefid <METRICDEF_ID> \
  --tstart <TSTART> \
  --tend <TEND> \
  --value <VALUE> \
  --gid <GID> \
  --uid <UID>
```

## Environment Variables

The Accounting Library supports the following environment variables:

| Variable | Description |
|--|-|
| `DEBUG` | Set to any truthy value to enable verbose debug output |

Example:

```bash
export DEBUG=true
```

## Error Handling

The library propagates HTTP and validation errors returned by the Accounting Service. Users are encouraged to:

- Validate entity IDs before metric submission
- Handle empty or partial responses
- Enable `DEBUG` mode during development

# Python API

## Core Service & HTTP Layer

| Class | Purpose | Attributes | Methods / Properties |
|------|--------|------------|----------------------|
| `ArgoAccountingService` | Entry point for accessing the Accounting REST API | `_endpoint`, `_conn`, `_installations`, `_projects`, `_providers` | `installations`, `projects`, `providers`, `connection`, `endpoint` |
| `HttpRequests` | Low-level HTTP request handler | `token`, `routes` | `make_request()`, `_error_dict()` |

## Exceptions

| Class | Purpose | Attributes | Methods |
|------|--------|------------|--------|
| `AccException` | Base exception for accounting errors | — | `__init__()` |
| `AccServiceException` | API-level errors | `msg`, `code` | `__init__()` |
| `AccTimeoutException` | Timeout-specific API errors | — | `__init__()` |
| `AccConnectionException` | Network/connection errors | `msg` | `__init__()` |

## REST Resource Base Classes

| Class | Purpose | Attributes | Methods |
|------|--------|------------|--------|
| `RestResource` | Abstract base for all REST resources | `_parent` | `endpoint`, `connection`, `id_name`, `data_root` |
| `RestResourceItem` | Represents a single REST resource | `id` (dynamic fields via JSON) | `_fetch()`, `_fetch_route()`, `_fetch_args()`, `__str__()` |
| `RestResourceList` | Represents paginated REST collections | `_parent`, `_page_size`, `_page_count`, `_current_page`, `_cache` | `_fetch()`, `_fetch_route()`, `_fetch_args()`, `_create_child()`, `refresh()`, `add()`, `__iter__()`, `__getitem__()`, `get()` |

## Installation Domain

| Class | Purpose | Attributes | Methods / Properties |
|------|--------|------------|----------------------|
| `InstallationBase` | Base class for installations | `id`, `project`, `organisation`, `infrastructure`, `installation`, `resource`, `unit_of_access` | — |
| `Installation` | Single installation | (inherits base fields) | `_fetch_route()`, `_fetch_args()`, `metrics` |
| `Installations` | Collection of installations | — | `_fetch_route()`, `_fetch_args()`, `_create_child()` |
| `ProjectInstallation` | Installation under a project | (inherits base fields) | `metrics` |
| `ProjectInstallations` | Project installation collection | — | `_fetch_route()`, `_fetch_args()`, `_create_child()` |
| `ProjectProviderInstallation` | Installation under project & provider | (inherits base fields) | `metrics` |
| `ProjectProviderInstallations` | Provider-scoped installations | — | `_fetch_route()`, `_fetch_args()`, `_create_child()` |

## Metrics Domain

| Class | Purpose | Attributes | Methods |
|------|--------|------------|--------|
| `MetricBase` | Base class for metrics | `id`, `time_period_start`, `time_period_end`, `value`, `project`, `provider`, `installation_id`, `project_id`, `resource`, `group_id`, `user_id`, `metric_definition_id`, `metric_definition` | — |
| `MetricDefinition` | Metric definition metadata | `metric_definition_id`, `metric_name`, `metric_description`, `unit_type`, `metric_type`, `creator_id` | `_fetch_route()`, `_fetch_args()` |
| `Metrics` | Base metrics collection | — | — |
| `InstallationMetric` | Metric for an installation | — | `_fetch_route()`, `_fetch_args()` |
| `InstallationMetrics` | Installation metric collection | — | `_fetch_route()`, `_fetch_args()`, `_add_route()`, `_add_args()`, `_create_child()` |
| `ProjectMetric` | Metric for a project | — | — |
| `ProjectMetrics` | Project metric collection | — | `_fetch_route()`, `_fetch_args()`, `_create_child()` |
| `ProviderMetric` | Metric for a provider | — | — |
| `ProviderMetrics` | Provider metric collection | — | `_fetch_route()`, `_fetch_args()`, `_create_child()` |

## Projects Domain

| Class | Purpose | Attributes | Methods / Properties |
|------|--------|------------|----------------------|
| `Project` | Single project | `id`, `acronym`, `title`, `start_date`, `end_date`, `call_identifier`, `_providers` | `_fetch_route()`, `_fetch_args()`, `providers`, `metrics`, `installations` |
| `Projects` | Project collection | — | `_fetch_route()`, `_fetch_args()`, `_create_child()` |

## Providers Domain

| Class | Purpose | Attributes | Methods / Properties |
|------|--------|------------|----------------------|
| `Provider` | Single provider | `id`, `acronym`, `title`, `start_date`, `end_date`, `call_identifier`, `providers` | `_fetch_route()`, `_fetch_args()`, `metrics` |
| `Providers` | Provider collection | — | `_fetch_route()`, `_fetch_args()`, `_create_child()` |
| `ProjectProvider` | Provider within a project context | — | `metrics`, `installations` |
| `ProjectProviders` | Project-scoped provider collection | — | `__getitem__()` |

