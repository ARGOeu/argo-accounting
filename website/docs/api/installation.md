---
id: installation
title: Installation
sidebar_position: 5
---

We use the term installation as it is defined in the Virtual Access documentation to refer to a specific instance or part of a resource/service that is allocated to a specific Project by one Provider.

An Installation can only be generated through the endpoint we are going to describe below.

The Installation collection has the following structure:

| Field          	| Description   	                      | 
|------------------	|---------------------------------------- |
| project             	| It must point to a Project ID that has already been registered             |
| organisation       	| It must point to a Provider ID that has been either registered through the EOSC Resource Catalogue or Accounting System API |
| infrastructure      	    | Short name of infrastructure |
| installation      	| Short name of installation |
| unit_of_access      	| It must point to an existing Metric Definition. Obviously, you can add different Metrics to an Installation, but this attribute expresses the primary Unit of Access |

### [POST] - Create a new Installation

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

Upon inserting the record into the database, the API returns the Installation enhanced with the generated installation ID :

Success Response `201 CREATED`

```
{
  "id" : "61dc142f6a278e43e8d6b3be",
  "project" : "101017567t",
  "organisation" : "grnet",
  "infrastructure" : "okeanos-knossos",
  "installation" : "GRNET-KNS",
  "unit_of_access" : "62973fea0f41a20c683e9014"
}
```

### [DELETE] - Delete an existing Installation

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

### [PATCH] - Update an existing Installation

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

The body of the request must contain an updated representation of Installation. You can update a part or all attributes of the Installation. The empty or null values are ignored.

The response will be the updated entity :

Success Response `200 OK`

```
{
   "updated_entity"
}
```

### [GET] - Fetch an existing Installation

You can fetch a created Installation by executing the following GET HTTP request:

```
GET /accounting-system/installations/{installation_id}

Authorization: Bearer {token}
```

The response is as follows:

Success Response `200 OK`

```
{
   "installation_id": "62986c61683f693f470bb67c",
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
```

### [GET] Fetch all Project Installations

Essentially, the following endpoint returns all Installations available in a specific Project. By default, the first page of 10 Installations will be returned.

```
GET /accounting-system/projects/{project_id}/installations

Authorization: Bearer {token}
```

You can tune the default values by using the query parameters page and size as shown in the example below.

```
GET /accounting-system/projects/{project_id}/installations?page=2&size=15

Authorization: Bearer {token}
```

The above request returns the second page which contains 15 Installations:

Success Response 200 OK

```
{
   "size_of_page": 15,
   "number_of_page": 2,
   "total_elements": 237,
   "total_pages": 16,
   "content": [
   {
   "installation_id": "62986c61683f693f470bb67c",
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

### [GET] Fetch all Provider Installations

Essentially, the following endpoint returns all Installations available in a Provider belonging to a specific Project. By default, the first page of 10 Installations will be returned.

```
GET /accounting-system/projects/{project_id}/providers/{provider_id}/installations

Authorization: Bearer {token}
```

You can tune the default values by using the query parameters page and size as shown in the example below.

```
GET /accounting-system/projects/{project_id}/providers/{provider_id}/installations?page=2&size=15

Authorization: Bearer {token}
```

The above request returns the second page which contains 15 Installations:

Success Response 200 OK

```
{
   "size_of_page": 15,
   "number_of_page": 2,
   "total_elements": 237,
   "total_pages": 16,
   "content": [
   {
   "installation_id": "62986c61683f693f470bb67c",
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

### [POST] - Access Control Entry for a particular Installation

The same goes for the Installations. Any client can have different responsibilities at different Installations. The actions the client can perform at each Installation are determined by the role, and the permissions it has.

To grant a role to a client on a specific Installation, you have to execute the following request:

```
POST /accounting-system/installations/{installation_id}/acl/{who}

Content-Type: application/json
Authorization: Bearer {token}
 
{
  "roles":[
     {role_name}
  ]
}
```

where {who} is the client ID in which the roles will be assigned.

The response is :

Success Response `200 OK`

```
{
   "code": 200,
   "message": "Access Control entry has been created successfully."
}
```

**Keep in mind that** to execute the above operation, you must have been assigned a role containing the Installation Acl permission.

### [POST] - Search for Installations
 
You can search on Installations, to find the ones corresponding to the given search criteria. Installations  can be searched by executing the following request:
 
``` 
POST accounting-system/installations/search
Content-Type: application/json
```
#### Example 1: 
```
{
           "type":"query",
           "field": "installation",
           "values": "GRNET-KNS-1",
           "operand": "eq"         

}
```

#### Example 2: 

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
 
The context of the request can be a json object of type ‘query’ or ‘filter’. 
‘query’ defines a criterion in a specific field of the installation. 
 
‘query’ can be expressed as a json object :
```
{
  "type":string,
  "field": string ,
  "values":primitive,
  "operand": string  
}
```
 
In the ‘query’ element we need to define the following properties: 
 
| Field          	| Description   	                      | 
|------------------	|---------------------------------------- |
| type | The type of the search and it’s value is ‘query’ |
| field | The field of the collection on which we search |
| values | The value of the equation , and it can be of any type depending on the type of the field we search |
| operand |The equation we want to apply on the field in order to search results. it’s value can be {eq, neq, lt, lte, gt, gte} |

 
__Example 1__ defines a search on field title. The ‘query’ searches for installations  that have installation="GRNET-KNS-1" or organisation="grnet"
‘filter’ defines multiple criteria and the way they are combined . A filter can include criteria of ‘filter’ or ‘query’ types.
‘filter’ can be expressed as a json object :
```
{
  "type":string,
  "operator": string ,
  criteria:array of ‘query’ or ‘filter’ elements
}
```
 
In the ‘query’ element we need to define the following properties: 
 
| Field          	| Description   	                   | 
|------------------	|---------------------------------------- |
|type                  |The type of the search and it’s value is ‘filter’ |
|operator              |The operation on which the elements in the criteria will be combined. it’s values is AND or OR |
|criteria |The specific subqueries that will be matched by the operator. criteria is an array of objects of ‘query’ or ‘filter’ type |

 
__Example 2__ defines a ‘filter’ containing criteria both of filter and query type. The ‘filter’ searches for  installations   that have installation="GRNET-KNS-1"  OR organisation=’grnet’
 
If the operation is successful, you get a list of installations
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

### Errors

Please refer to section [Errors](./api_errors) to see all possible Errors.
