---
id: resource
title: Resource
sidebar_position: 9
---

The Accounting System communicates with EOSC Resource Catalogue to retrieve the available Resources. From the response we receive we keep specific information, which expresses a Resource. The Resource for the Accounting Service has the following structure:


| Field          	| Description   	                      | 
|------------------	|---------------------------------------- |
| id             	| A unique Resource identifier            |
| name            	| The name of Resource                    |

### [GET] - Fetch the available EOSC Resources

The client can retrieve the Resources that are available in the EOSC Resource Catalogue by executing the following request:

```
GET /accounting-system/resources

Authorization: Bearer {token}
```

The response returned to the client is the following:

Success Response `200 OK`

```
{
  "size_of_page": 10,
  "number_of_page": 1,
  "total_elements": 15,
  "total_pages": 2,
  "links": [
    {
      "href": "http://localhost:8080/accounting-system/metric-definitions/61eeab7bb3b68f5c3f8c4c24/metrics?page=1&size=10",
      "rel": "first"
    }
  ],
  "content": [
    {
      "id": "openaire.european_marine_science_openaire_dashboard",
      "name": "European Marine Science OpenAIRE Community Gateway"
    }
  ]
}
```

### Errors

Please refer to section [Errors](./api_errors) to see all possible Errors.