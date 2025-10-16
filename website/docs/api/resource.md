---
id: resource
title: Resource of an Installation
sidebar_position: 9
---

# Resource of an Installation

The Accounting System communicates with EOSC Resource Catalogue to retrieve the
available Resources. From the response we receive we keep specific information,
which expresses a Resource. The Resource for the Accounting Service has the
following structure:

| Field           | Description                          |
|------------------ |---------------------------------------- |
| id              | A unique Resource identifier            |
| name             | The name of Resource                    |

## [GET] - Create an Installation Resource

The client can retrieve the Resources that are available in the EOSC Resource
Catalogue by executing the following request:

```
POST /accounting-system/resources

Content-Type: application/json
Authorization: Bearer {token}
```

The response returned to the client is the following:

Success Response `201 OK`

```
{
  "id": "openaire.european_marine_science_openaire_dashboard",
  "name": "European Marine Science OpenAIRE Community Gateway"
}
```

## Errors

Please refer to section [Errors](./api_errors) to see all possible Errors.
