---
id: metric_definition
title: Metric Definition
sidebar_position: 2
---

# Metric Definition

Metrics are measures of quantitative assessment commonly used for assessing,
comparing, and tracking usage or performance of a service. They are the main
indicators.

A metric definition is the way to represent and describe the type of the
metrics. A Metric Definition consists of the metadata describing a Metric.
The client can interact through several operations with the API in order to
create, update, delete or fetch a Metric Definition. The aforementioned
operations are described below.

The Metric Definition can be expressed by the following structure:

| Field           | Description                          |
|------------------ |---------------------------------------- |
| id              | A unique identifier.             |
| metric_name       | Name to be used for describing a Metric. |
| metric_description       | Short Description of a Metric Definition. |
| [unit_type](./unit_type.md)| Quantifies metrics across various entities*.|
| [metric_type](./metric_type.md)| How metrics have been collected. |

\* _entities can be: infrastructures, service providers, and projects_

## [POST] - Create a Metric Definition

Upon creating a new Metric Definition, the client should be able to forward
a Metric to the Accounting System API.
The client can submit a new Metric Definition by executing the following POST
request:

```
POST /accounting-system/metric-definitions

Content-type: application/json
Authorization: Bearer {token}

{
    "metric_name" : "number_of_users",
    "metric_description" : "Number of users",
    "unit_type" : "#",
    "metric_type" : "aggregated"
}
```

Upon inserting the record into the database, the API returns the Metric
Definition enhanced with the metric_definition_id:

Success Response `201 CREATED`

```
{
    "metric_definition_id": "61dc142f6a278e43e8d6b3be",
    "metric_name" : "number_of_users",
    "metric_description" : "Number of users",
    "unit_type" : "#",
    "metric_type" : "aggregated",
    "creator_id": "115143399384cc3177df5377691ccdbb284cb245fad1c@aai.eosc-portal.eu"
}
```

## [GET] - Fetch a Metric Definition

The client should be able to fetch an already created Metric Definition. Having
the id of a Metric Definition, the client can request the relevant to that id
Metric Definition by executing the following request:

```
GET /accounting-system/metric-definitions/{metric_definition_id}

Authorization: Bearer {token}
```

The response returned to the client is the following:

Success Response `200 OK`

```
{
    "id" : "61dff744ba5b5f60791bd09d",
    "metric_name" : "number_of_users",
    "metric_description" : "Number of users",
    "unit_type" : "#",
    "metric_type" : "aggregated"
}
```

## [PATCH] - Update a Metric Definition

The client can update a Metric Definition using the following PATCH request.
The URL should be filled in with the metric definition id to be updated.

Bear in mind that you cannot update an existing Metric Definition if there
are Metrics assigned to it.

```
PATCH /accounting-system/metric-definitions/{metric_definition_id}

Content-type: application/json
Authorization: Bearer {token}

{
    attributes_to_be_updated
}
```

The response will be the updated entity :

Success Response `200 OK`

```
{
    "updated_entity"
}
```

In order to update the resource properties, the body of the request must
contain an updated representation of Metric Definition. For example, to
edit the metric type, send the following request:

```
PATCH /accounting-system/metric-definitions/{metric_definition_id}

Content-type: application/json
Authorization: Bearer {token}

{  
    "metric_type": "metric_type_to_be_updated"
}
```

You can update a part or all attributes of the Metric Definition. The
empty or null values are ignored.

## [DELETE]  - Delete a Metric Definition

You can only delete a Metric Definition that does not have any Metrics
assigned to it. If the Metric Definition has no Metrics, you can safely
delete it.

Metric Definition can be deleted by executing the following request:

```
DELETE /accounting-system/metric-definitions/{metric_definition_id}

Authorization: Bearer {token}
```

If the operation is successful, you get the following response:

Success Response `200 OK`

```
{
   "code": 200,
   "message": "The Metric Definition has been deleted successfully."
}
```

## [GET]  - Fetch all the Metric Definitions

You can also fetch all the Metric Definitions that exist to the
accounting system.

```
GET /accounting-system/metric-definitions

Authorization: Bearer {token}
```

If the operation is successful, you get the following response:

```
{
  "size_of_page": 3,
  "number_of_page": 1,
  "total_elements": 3,
  "total_pages": 1,
  "content": [
    {
      "metric_definition_id": "6360c5d63b4ae429c92409d7",
      "metric_name": "number_of_active_users",
      "metric_description": "Number of active users",
      "unit_type": "#",
      "metric_type": "aggregated"
    },
    {
      "metric_definition_id": "6360c7ad3b4ae429c92409dd",
      "metric_name": "number_of_inactive_users",
      "metric_description": "Number of inactive users",
      "unit_type": "#",
      "metric_type": "aggregated"
    },
    {
      "metric_definition_id": "6360c88c3b4ae429c92409de",
      "metric_name": "number_of_users_deleted",
      "metric_description": "Number of deleted users",
      "unit_type": "s",
      "metric_type": "count"
    }
  ],
  "links": []
}
```

## [POST] - Search for Metric Definitions

You can search on Installations, to find the ones corresponding to the given
search criteria. Metric Definitions can be searched by executing the following
request:

```
POST accounting-system/metric-definition/search
Content-Type: application/json
```

### Example 1

```
{
  "type": "query",
  "field": "metric_type",
  "values": "count",
  "operand": "eq"
}
```

### Example 2

```
{
  "type": "filter",
  "operator": "OR",
  "criteria": [
    {
      "type": "query",
      "field": "metric_name",
      "values": "mdname1",
      "operand": "eq"
    },
    {
      "type": "filter",
      "operator": "AND",
      "criteria": [
        {
          "type": "query",
          "field": "metric_type",
          "values": "count",
          "operand": "eq"
        },
        {
          "type": "query",
          "field": "unit_type",
          "values": "#",
          "operand": "eq"
        }
      ]
    }

```

The context of the request should be a JSON object. The syntax of the JSON
object is described [**here**](https://argoeu.github.io/argo-accounting/docs/guides/search-filter).
If the operation is successful, you get a list of metrics, for example:

```
{
    "size_of_page": 1,
    "number_of_page": 1,
    "total_elements": 1,
    "total_pages": 1,
    "content": [
        {
            "metric_definition_id": "6360c7ad3b4ae429c92409dd",
            "metric_name": "cpu",
            "metric_description": "CPU metric definition",
            "unit_type": "#",
            "metric_type": "aggregated"
        }
    ],
    "links": []
}
```

## Errors

Please refer to section [Errors](./api_errors) to see all possible Errors.
