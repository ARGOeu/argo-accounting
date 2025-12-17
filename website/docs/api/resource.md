---
id: resource
title: Resource of an Installation
sidebar_position: 9
---

# Resource of an Installation

You can register your own Resource that you can link to an installation, which follows the structure outlined below:

| Field           | Description                          |
|------------------ |---------------------------------------- |
| id              | A unique Resource identifier            |
| name             | The name of Resource                    |

## [POST] - Create an Installation Resource

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

## [GET] - Fetch Resources

Returns all the Resources available on the Accounting Service.

Essentially, this operation retrieves the list of Resources registered in the Accounting Service.
```
GET /accounting-system/resources

Content-Type: application/json
Authorization: Bearer {token}
```
The response returned to the client is the following:

Success Response `200 OK`
### Success Response `200 OK`

```json
{
  "size_of_page": 10,
  "number_of_page": 1,
  "total_elements": 15,
  "total_pages": 2,
  "links": [
    {
      "href": "http://localhost:8080/accounting-system/resources?page=1&size=10",
      "rel": "first"
    }
  ],
  "content": [
    {
      "id": "openaire.european_marine_science_openaire_dashboard",
      "name": "European Marine Science OpenAIRE Community Gateway"
    },
    {
      "id": "egi.data_node",
      "name": "EGI Data Node"
    },
    {
      "id": "cern.open_data_portal",
      "name": "CERN Open Data Portal"
    }
  ]
}
```

## Errors

Please refer to section [Errors](./api_errors) to see all possible Errors.
