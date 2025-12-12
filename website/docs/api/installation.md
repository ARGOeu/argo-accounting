---
id: installation
title: Installation
sidebar_position: 5
---

# Installation

We use the term installation as it is defined in the Virtual Access
documentation to refer to a specific instance or part of a resource/service
that is allocated to a specific Project by one Provider.

An Installation can only be generated through the endpoint we are going to
describe below.

The Installation collection has the following structure:

| Field                | Description         |
|----------------------|---------------------------------|
| [project](./project.md) | Points to an already registered Project* ID. |
| [organisation](./provider.md) |Points to an already registered Provider* ID.|
|[resource](./resource.md)|Points to a Resource*. _(O)_|
| infrastructure       | Short name of infrastructure. |
| installation         | Short name of installation.   |
|[unit_of_access](./metric_definition.md)| Points to Metric Definition*. _(O)_|

\* _The entity must be previously registered through the Accounting System API._

_O_: Optional

## [POST] - Create a new Installation

You can submit a new Installation by executing the following POST request:

```
POST /accounting-system/installations

Content-type: application/json
Authorization: Bearer {token}

{
  "project" : "101017567t",
  "organisation" : "grnet",
  "infrastructure" : "okeanos-knossos",
  "installation" : "GRNET-KNS",
  "unit_of_access" : "62973fea0f41a20c683e9014"
}
```

Upon inserting the record into the database, the API returns the Installation
enhanced with the generated installation ID:

Success Response `201 CREATED`

```
{
  "id" : "61dc142f6a278e43e8d6b3be",
  "project" : "101017567t",
  "organisation" : "grnet",
  "infrastructure" : "okeanos-knossos",
  "installation" : "GRNET-KNS",
  "unit_of_access": {
        "metric_definition_id": "62973fea0f41a20c683e9014",
        "metric_name": "lalala",
        "metric_description": "Number of users",
        "unit_type": "#",
        "metric_type": "aggregated",
        "creator_id": "115143399384cc3177df5377691ccdbb284cb245fad1c@aai.eosc-portal.eu"
    }
}
```

## [DELETE] - Delete an existing Installation

You can also delete an existing Installation by executing the following request:

```
DELETE /accounting-system/installations/{installation_id}

Authorization: Bearer {token}
```

If the deletion is successful the following response is returned:

Success Response `200 OK`

```
{
   "code": 200,
   "message": "Installation has been deleted successfully."
}
```

## [PATCH] - Update an existing Installation

You can update an existing Installation by executing the following request:

```
PATCH /accounting-system/installations/{installation_id}

Content-type: application/json
Authorization: Bearer {token}

{
  "organisation" : "organisation to be updated",
  "infrastructure" : "infrastructure to be updated",
  "installation" : "installation to be updated",
  "unit_of_access" : "unit_of_access to be updated"
}
```

The body of the request must contain an updated representation of Installation.
You can update a part or all attributes of the Installation. The empty or null
values are ignored.

The response will be the updated entity :

Success Response `200 OK`

```
{
   "updated_entity"
}
```

## [GET] - Fetch an existing Installation

You can fetch a created Installation by executing the following GET HTTP request:

```
GET /accounting-system/installations/{installation_id}

Authorization: Bearer {token}
```

The response is as follows:

Success Response `200 OK`

```
{
    "id": "6350f13072dda00a3ce5f0cb",
    "project": "725025",
    "organisation": "sites",
    "infrastructure": "infra-grnet-test",
    "installation": "installation-grnet",
    "unit_of_access": {
        "metric_definition_id": "6350f12772dda00a3ce5f0ca",
        "metric_name": "lalala",
        "metric_description": "Number of users",
        "unit_type": "#",
        "metric_type": "aggregated"
    }
}
```

## [GET] Fetch all Project Installations

Essentially, the following endpoint returns all Installations available
in a specific Project. By default, the first page of 10 Installations will
be returned.

```
GET /accounting-system/projects/{project_id}/installations

Authorization: Bearer {token}
```

You can tune the default values by using the query parameters page and size as
shown in the example below.

```
GET /accounting-system/projects/{project_id}/installations?page=2&size=15

Authorization: Bearer {token}
```

The above request returns the second page which contains 15 Installations:

Success Response `200 OK`

```
{
   "size_of_page": 15,
   "number_of_page": 2,
   "total_elements": 237,
   "total_pages": 16,
   "content": [
   {
   "id": "62986c61683f693f470bb67c",
   "organisation": "grnet",
   "infrastructure": "okeanos-knossos",
   "installation": "GRNET-KNS",
   "unit_of_access": {
       "metric_definition_id": "62986c4e683f693f470bb67b",
       "metric_name": "number_of_users",
       "metric_description": "Number of users",
       "unit_type": "#",
       "metric_type": "aggregated"
   }
   }
   ],
   "links": [
       {
           "href": "https://acc.devel.argo.grnet.gr/accounting-system/installations?page=1&size=15",
           "rel": "first"
       },
       {
           "href": "https://acc.devel.argo.grnet.gr/accounting-system/installations?page=16&size=15",
           "rel": "last"
       },
       {
           "href": "https://acc.devel.argo.grnet.gr/accounting-system/installations?page=2&size=15",
           "rel": "self"
       },
       {
           "href": "https://acc.devel.argo.grnet.gr/accounting-system/installations?page=1&size=15",
           "rel": "prev"
       },
       {
           "href": "https://acc.devel.argo.grnet.gr/accounting-system/installations?page=3&size=15",
           "rel": "next"
       }
   ]
}
```

## [GET] Fetch all Provider Installations

Essentially, the following endpoint returns all Installations available in a
Provider belonging to a specific Project. By default, the first page of 10
Installations will be returned.

```
GET /accounting-system/projects/{project_id}/providers/{provider_id}/installations

Authorization: Bearer {token}
```

You can tune the default values by using the query parameters page and size as
shown in the example below.

```
GET /accounting-system/projects/{project_id}/providers/{provider_id}/installations?page=2&size=15

Authorization: Bearer {token}
```

The above request returns the second page which contains 15 Installations:

Success Response `200 OK`

```
{
   "size_of_page": 15,
   "number_of_page": 2,
   "total_elements": 237,
   "total_pages": 16,
   "content": [
        {
            "id": "6350f13072dda00a3ce5f0cb",
            "project": "725025",
            "organisation": "sites",
            "infrastructure": "infra-grnet-test",
            "installation": "installation-grnet",
            "unit_of_access": {
                "metric_definition_id": "6350f12772dda00a3ce5f0ca",
                "metric_name": "lalala",
                "metric_description": "Number of users",
                "unit_type": "#",
                "metric_type": "aggregated"
            }
        }
    ],
   "links": [
       {
           "href": "https://acc.devel.argo.grnet.gr/accounting-system/installations?page=1&size=15",
           "rel": "first"
       },
       {
           "href": "https://acc.devel.argo.grnet.gr/accounting-system/installations?page=16&size=15",
           "rel": "last"
       },
       {
           "href": "https://acc.devel.argo.grnet.gr/accounting-system/installations?page=2&size=15",
           "rel": "self"
       },
       {
           "href": "https://acc.devel.argo.grnet.gr/accounting-system/installations?page=1&size=15",
           "rel": "prev"
       },
       {
           "href": "https://acc.devel.argo.grnet.gr/accounting-system/installations?page=3&size=15",
           "rel": "next"
       }
   ]
}
```

## Capacities

Capacities represent the allocated quantities for an Installation (e.g., number of VMs, storage volume, etc.). They are linked to specific Metric Definitions and allow tracking of resource allocations.

### [POST] - Register a new Capacity for an Installation

You can register a new capacity for a specific Installation by executing the following POST request:
```
POST /accounting-system/installations/{installation_id}/capacities

Content-Type: application/json
Authorization: Bearer {token}

{
"metric_definition_id": "62973fea0f41a20c683e9014",
"value": 100.0,
"registered_on": "2025-12-15T10:00:00Z"
}
```

where `{installation_id}` denotes the registered Installation, `{metric_definition_id}` denotes the registered Metric Definition, and `{registered_on}` specifies the start of the period to which the corresponding `{value}` applies. If `registered_on` is empty, the current time is assigned.

Success Response `200 OK`
```json
{
  "code": 200,
  "message": "Capacity has been successfully registered."
}
```

### [PATCH] - Update an existing Capacity of an Installation
You can update an existing capacity by executing the following request:
```
PATCH /accounting-system/installations/{installation_id}/capacities/{capacity_id}
Content-Type: application/json
Authorization: Bearer {token}
{
  "value": 150.0,
  "registered_on": "2026-12-15T10:00:00Z"
}
```

Only the provided fields will be updated. Empty or null values are ignored.

Success Response `200 OK`
```json
{
  "code": 200,
  "message": "Capacity was updated successfully."
}
```

### [GET] - Fetch all the Capacities of an Installation
You can retrieve all capacities associated with a specific Installation.  By default, the first page of 10
Installations will be returned.
```
GET /accounting-system/installations/{installation_id}/capacities
Authorization: Bearer {token}
```

Success Response `200 OK`

```json
[
  {
    "id": "5f2a9c1e8b4d6a7c903a1123",
    "installation_id": "6a3b8d2f9c4e7b1a552c3344",
    "metric_definition_id": "7c9e1a4b6d8f2a3e99887766",
    "value": 742,
    "registered_on": "2024-10-02T14:21:09.482"
  },
  {
    "id": "5e8d3a9f2c4b7a1d66554433",
    "installation_id": "6f1c9b8a3d2e4a7c11223344",
    "metric_definition_id": "7a6d4c9e2b1f8d3a44556677",
    "value": 1589,
    "registered_on": "2024-11-18T07:55:31.907"
  }
]
```

## Access Control Entry for a specific Installation

### [POST] – Creation of a new entitlement

The general endpoint responsible for creating an Access Control entry for a specific Installation, via the “Create a new entitlement” operation, is as follows:
```
POST /accounting-system/admin/entitlements

Content-Type: application/json  
Authorization: Bearer {token}

{
  "name": "{namespace}:group:accounting:{project_id}:{provider_id}:{installation_id}:role=admin"
}
```
where `{project_id}`, `{provider_id}`, and `{installation_id}` refer to the previously registered Project, Provider, and Installation respectively, along with the provided `{namespace}`, e.g. `urn:geant:sandbox.eosc-beyond.eu:core:integration`.

The response is:

Success Response `201 OK`
```
{
   "code": 201,
   "message": "Entitlement created successfully."
}
```
### [POST] – Association of an entitlement with a client/actor

The general endpoint responsible for the association of an entitlement with a client/actor is as follows:
```
POST /accounting-system/admin/entitlements/{id}/assign/{actor_id}

Content-Type: application/json
Authorization: Bearer {token}
```
where `{id}` refers to the previously registered entitlement, and `{actor_id}` refers to the previously registered client/actor.

The response is:

Success Response `200 OK`
```
{
   "code": 200,
   "message": "Entitlement was assigned successfully."
}
```
A client can have different roles across different Projects, Providers, and Installations. The actions a client can perform for each Installation are determined by the assigned role and its associated permissions.

### Note

See [Mapping of Roles to Entitlements](../authorization/accounting_system_roles#mapping-of-roles-to-entitlements) for assigning a role to a client/actor.

## [POST] - Search for Installations

You can search on Installations, to find the ones corresponding to the given
search criteria. Installations  can be searched by executing the following
request:

```
POST accounting-system/installations/search

Content-Type: application/json
Authorization: Bearer {token}
```

### Example 1

```
{
           "type":"query",
           "field": "installation",
           "values": "GRNET-KNS-1",
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
     "type": "filter",
     "operator": "OR",
     "criteria": [{
           "type":"query",
           "field": "installation",
           "values": "GRNET-KNS-1",
           "operand": "eq"

},{
           "type":"query",
           "field": "organisation",
           "values": "grnet",
           "operand": "eq"

}]

   }]}
```

The context of the request should be a JSON object. The syntax of the JSON
object is described [**here**](../guides/api_actions/search-filter).
If the operation is successful, you get a list of installations.

```
{
   "size_of_page": 2,
   "number_of_page": 1,
   "total_elements": 2,
   "total_pages": 1,
   "content": [
       {
           "installation_id": "62de52a3be6b3a161e01c75b",
           "project": "750802",
           "organisation": "sites",
           "infrastructure": "okeanos-knossos-1",
           "installation": "GRNET-KNS-1",
           "unit_of_access": {
               "metric_definition_id": "62de528dbe6b3a161e01c75a",
               "metric_name": "number_of_active_users",
               "metric_description": "Number of active users",
               "unit_type": "#",
               "metric_type": "aggregated"
           }
       },
       {
           "installation_id": "62de532cbe6b3a161e01c75d",
           "project": "750802",
           "organisation": "grnet",
           "infrastructure": "okeanos-knossos-2",
           "installation": "GRNET-KNS-2",
           "unit_of_access": {
               "metric_definition_id": "62de531cbe6b3a161e01c75c",
               "metric_name": "number_of_users_deleted",
               "metric_description": "Number of deleted users",
               "unit_type": "#",
               "metric_type": "aggregated"
           }
       }
   ],
   "links": []
}
```
## [GET] - Get Installation Report

The report includes aggregated metric values grouped by metric definitions.

You can retrieve a report for a specific **installation** within a defined time period. When capacities are defined for the selected time period, the report also includes usage percentages broken down by the capacity-defined time periods.



```
GET /accounting-system/installations/{installation_id}/report

Content-Type: application/json
Authorization: Bearer {token}
```

or by using the `externalId` instead.

```
GET /accounting-system/installations/external/report

Content-Type: application/json
Authorization: Bearer {token}
```


**Parameters**

- *`externalId` *(required, query)* – The external installation ID.  
  Example: `installation-446655440000`*
- `start` *(required, query)* – Start date in `yyyy-MM-dd` format.  
  Example: `2024-01-01`  
- `end` *(required, query)* – End date in `yyyy-MM-dd` format.  
  Example: `2024-12-31`  

**Success Response `200 OK`**

```json
{
  "installation_id": "507f1f77bcf86cd799439011",
  "project": "447535",
  "provider": "grnet",
  "installation": "GRNET-KNS",
  "infrastructure": "okeanos-knossos",
  "resource": "cloud",
  "external_id": "installation-45583",
  "data": [
    {
      "metric_definition_id": "507f1f77bcf86cd799439011",
      "metric_name": "storage",
      "metric_description": "The storage of the facility",
      "unit_type": "TB hours",
      "metric_type": "aggregated",
      "periods": [
        {
          "from": "2022-01-05T09:13:07Z",
          "to": "2022-01-05T09:13:07Z",
          "total_value": 1234.56,
          "capacity_value": 1000,
          "usage_percentage": 75
        }
      ]
    }
  ]
}
```

## Errors

Please refer to section [Errors](./api_errors) to see all possible Errors.

## Note

Instead of the internal ID, you can use the external ID in the corresponding API calls.
