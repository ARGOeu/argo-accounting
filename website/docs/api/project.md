---
id: project
title: Project
sidebar_position: 3
---

A Project is the main resource of the Accounting System. The first step a user must follow is to create a project under which the metric data will belong.

Currently, we support only projects that are available in the EU database.  Basically, we register information about a particular Project in our system using the unique ID that every European Project has for its identification.

### [POST] - Associate Providers with a specific Project

To be able to register Metrics in an installation, first you must associate a Project with one or more  Providers. The following action is responsible for generating a hierarchical relationship between a Project and one or more Providers:

```
POST /accounting-system/projects/{project_id}/associate

Content-Type: application-json
Authorization: Bearer {token}

{
   "providers":[
      "bioexcel",
      "osmooc",
      "grnet",
      "sites"
   ]
}
```

The response is:

Success Response `200 OK`

```
{
   "code": 200,
   "message": "The following providers [bioexcel, osmooc, grnet, sites] have been associated with Project {project_id}"
}
```

### [POST] - Dissociate Providers from a Project

You can also dissociate one or more Providers from a Project:

```
POST /accounting-system/projects/{project_id}/dissociate

Content-Type: application-json
Authorization: Bearer {token}

{
   "providers":[
      "grnet"
   ]
}
```

The response is :

Success Response `200 OK`

```
{
   "code": 200,
   "message": "The following providers [grnet] have been dissociated from Project {project_id}"
}
```

If there are Installations registered to Provider, the dissociation is not allowed:

Error Response `409 CONFLICT`

```
{
   "code": 409,
   "message": "Dissociation is not allowed. There are registered Installations to {project_id, provider_id}"
}
```

### [GET] - Project Hierarchical Structure

You can retrieve the Providers and Installations associated with a specific project:

```
GET /accounting-system/projects/{project_id}

Authorization: Bearer {token}
```

Basically, the hierarchical structure of a Project is returned:

Success Response `200 OK`
```
{
    "id": "725025",
    "acronym": "AgeConsolidate",
    "title": "The Missing Link of Episodic Memory Decline in Aging: The Role of Inefficient Systems Consolidation",
    "start_date": "2017-05-01",
    "end_date": "2022-10-31",
    "call_identifier": "ERC-2016-COG",
    "providers": [
        {
            "id": "sites",
            "name": "Swedish Infrastructure for Ecosystem Science",
            "website": "https://www.fieldsites.se/en-GB",
            "abbreviation": "SITES",
            "logo": "https://dst15js82dk7j.cloudfront.net/231546/95187636-P5q11.png",
            "installations": [
                {
                    "id": "6350f13072dda00a3ce5f0cb",
                    "infrastructure": "infra-grnet-test",
                    "installation": "installation-grnet",
                    "unit_of_access": "6350f12772dda00a3ce5f0ca"
                }
            ]
        },
        {
            "id": "carlzeissm",
            "name": "Carl Zeiss Microscopy",
            "website": "https://www.zeiss.com/",
            "abbreviation": "Carl Zeiss",
            "logo": "https://images.zeiss.com/corporate-new/about-zeiss/history/images/logo/logo_heute.ts-1537187631211.jpg?auto=compress%2Cformat&fm=png&ixlib=java-1.1.11&w=640&s=982838e7e4ea9f38fde897b6a61a544b",
            "installations": []
        }
    ]
}
```


### [GET] - Fetch all Projects

You can retrieve all Projects assigned to you by executing the following GET request:

```
GET /accounting-system/projects

Authorization: Bearer {token}
```

By default, the first page of 10 Projects will be returned. You can tune the default values by using the query parameters page and size as shown in the example below.

```
GET /accounting-system/projects?page=2&size=5

Authorization: Bearer {token}
```

Success Response `200 OK`

```
{
    "size_of_page": 10,
    "number_of_page": 1,
    "total_elements": 15,
    "total_pages": 2,
    "content": [
        {
            "id": "888743",
            "acronym": "DABAT",
            "title": "DNA-sensing by AIM2 in activated B cells: novel targets to improve allogeneic haematopoietic stem cell transplantation",
            "start_date": "2020-10-01",
            "end_date": "2023-09-30",
            "call_identifier": "H2020-MSCA-IF-2019",
            "providers": [
                {
                    "id": "sites",
                    "name": "Swedish Infrastructure for Ecosystem Science",
                    "website": "https://www.fieldsites.se/en-GB",
                    "abbreviation": "SITES",
                    "logo": "https://dst15js82dk7j.cloudfront.net/231546/95187636-P5q11.png",
                    "installations": [
                        {
                            "id": "6351002527962d7967919fa7",
                            "infrastructure": "infra-grnet-test",
                            "installation": "installation-grnet",
                            "unit_of_access": "6351002027962d7967919fa6"
                        }
                    ]
                },
                {
                    "id": "carlzeissm",
                    "name": "Carl Zeiss Microscopy",
                    "website": "https://www.zeiss.com/",
                    "abbreviation": "Carl Zeiss",
                    "logo": "https://images.zeiss.com/corporate-new/about-zeiss/history/images/logo/logo_heute.ts-1537187631211.jpg?auto=compress%2Cformat&fm=png&ixlib=java-1.1.11&w=640&s=982838e7e4ea9f38fde897b6a61a544b",
                    "installations": []
                }
            ]
        },
        {
            "id": "709328",
            "acronym": "IN VIVO MOSSY",
            "title": "Is the hippocampal mossy fiber synapse a detonator in vivo?",
            "start_date": "2016-04-01",
            "end_date": "2018-03-31",
            "call_identifier": "H2020-MSCA-IF-2015",
            "providers": []
        },
        {
            "id": "895916",
            "acronym": "MXTRONICS",
            "title": "MXene Nanosheets For Future Optoelectronic Devices",
            "start_date": "2020-11-01",
            "end_date": "2022-10-31",
            "call_identifier": "H2020-MSCA-IF-2019",
            "providers": [
                {
                    "id": "ubora",
                    "name": "Open Biomedical Engineering e-platform for Innovation through Education",
                    "website": "http://ubora-biomedical.org/",
                    "abbreviation": "UBORA",
                    "logo": "http://ubora-biomedical.org/wp-content/uploads/2017/01/UBORA-Logo-Final-JPEGb.jpg",
                    "installations": []
                }
            ]
        },
        {
            "id": "887530",
            "acronym": "NONORMOPERA",
            "title": "Sexual and Gender Non-Normativity in Opera after the Second World War",
            "start_date": "2020-11-01",
            "end_date": "2024-05-02",
            "call_identifier": "H2020-MSCA-IF-2019",
            "providers": []
        },
        {
            "id": "843702",
            "acronym": "LCxLCProt",
            "title": "Comprehensive two-dimensional liquid chromatography for the characterization of protein biopharmaceuticals at the protein level",
            "start_date": "2020-01-01",
            "end_date": "2020-12-31",
            "call_identifier": "H2020-MSCA-IF-2018",
            "providers": []
        },
        {
            "id": "894897",
            "acronym": "DEFORM",
            "title": "Dead or Alive: Finding the Origin of Caldera Unrest using Magma Reservoir Models",
            "start_date": "2020-11-01",
            "end_date": "2022-10-31",
            "call_identifier": "H2020-MSCA-IF-2019",
            "providers": []
        },
        {
            "id": "895478",
            "acronym": "ANACLETO",
            "title": "Noise and drag reduction by riblets.",
            "start_date": "2020-07-01",
            "end_date": "2022-06-30",
            "call_identifier": "H2020-MSCA-IF-2019",
            "providers": []
        },
        {
            "id": "894921",
            "acronym": "PlaGE",
            "title": "Playing at the Gateways of Europe: theatrical languages and performatives practices in the Migrants' Reception Centres of the Mediterranean Area",
            "start_date": "2020-10-01",
            "end_date": "2023-09-30",
            "call_identifier": "H2020-MSCA-IF-2019",
            "providers": []
        },
        {
            "id": "654496",
            "acronym": "NanoCytox",
            "title": "Development of Novel Analytical Methods to assess Nanoparticle cytotoxicity",
            "start_date": "2015-11-01",
            "end_date": "2017-10-31",
            "call_identifier": "H2020-MSCA-IF-2014",
            "providers": []
        },
        {
            "id": "701538",
            "acronym": "TransIt",
            "title": "Translating science in the long Italian Eighteenth-Century. The role of translators and publishers as “cultural mediators” (1760-1790s)",
            "start_date": "2017-02-01",
            "end_date": "2019-01-31",
            "call_identifier": "H2020-MSCA-IF-2015",
            "providers": []
        }
    ],
    "links": [
        {
            "href": "http://localhost:8080/accounting-system/projects?page=1&size=10",
            "rel": "first"
        },
        {
            "href": "http://localhost:8080/accounting-system/projects?page=2&size=10",
            "rel": "last"
        },
        {
            "href": "http://localhost:8080/accounting-system/projects?page=1&size=10",
            "rel": "self"
        },
        {
            "href": "http://localhost:8080/accounting-system/projects?page=2&size=10",
            "rel": "next"
        }
    ]
}
```


### [POST] - Access Control Entry for a particular Project

The general endpoint that is responsible for creating an Access Control entry for a Project is as follows:

```
POST /accounting-system/projects/{project_id}/acl/{who}

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
   "message": "Project Access Control was successfully created."
}
```

One client can have different roles at different Projects. For instance, in one Project can be an admin executing all the Project operations while in another it can only read the Project Metrics.
Consequently, any client can have different responsibilities at different Projects. The actions the client can perform at each Project are determined by the role, and the permissions it has.

**Keep in mind that** to execute the above operation, you must have been assigned a role containing the Project Acl permission.

### [POST] - Search for Projects
 
You can search on Projects, to find the ones corresponding to the given search criteria. Projects  can be searched by executing the following request:
```
POST accounting-system/projects/search
Content-Type: application/json

```
#### Example 1: 
```
 {
      "type": "query",
      "field": "title",
      "values": "Functional and Molecular Characterisation of Breast Cancer Stem Cells",
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
      "type": "query",
      "field": "title",
      "values": "Functional and Molecular Characterisation of Breast Cancer Stem Cells",
      "operand": "eq"
    },
    {
      "type": "query",
      "field": "acronym",
      "values": "El_CapiTun",
      "operand": "eq"
    }
  ]
}
```



 
The context of the request can be a json object of type ‘query’ or ‘filter’. 
‘query’ defines a criterion in a specific field of the project. 
 
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
| type                  | The type of the search and it’s value is ‘query’ |
| field | The field of the collection on which we search |
| values | The value of the equation , and it can be of any type depending on the type of the field we search |
| operand | The equation we want to apply on the field in order to search results. it’s value can be {eq, neq, lt, lte, gt, gte} |

 
__Example 1__ defines a search on field title. The ‘query’ searches for projects that have title="Functional and Molecular Characterisation of Breast Cancer Stem Cells"
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

| Field          	| Description   	                      | 
|------------------	|---------------------------------------- |
| type | The type of the search and it’s value is ‘filter’ |
| operator | The operation on which the elements in the criteria will be combined. it’s values is AND or OR  |
| criteria | The specific subqueries that will be matched by the operator. criteria is an array of objects of ‘query’ or ‘filter’ type |

 
__Example 2__ defines a ‘filter’ containing criteria both of filter and query type. The ‘filter’ searches for  projects  that have 
title="TRAJECTORY MODIFICATION IN CHRONIC STROKE PATIENTS"
 OR acronym=’El_CapiTun’
 
If the operation is successful, you get a list of projects

```
{
   "size_of_page": 2,
   "number_of_page": 1,
   "total_elements": 2,
   "total_pages": 1,
   "content": [
       {
           "project_id": "5R44NS032194-03",
           "acronym": "",
           "title": "TRAJECTORY MODIFICATION IN CHRONIC STROKE PATIENTS",
           "providers": []
       },
       {
           "project_id": "750802",
           "acronym": "El_CapiTun",
           "title": "An elastocapillary-enabled self-tunable microfluidic chip",
           "providers": [
               {
                   "provider_id": "grnet",
                   "name": "National Infrastructures for Research and Technology",
                   "installations": []
               },
               {
                   "provider_id": "osmooc",
                   "name": "Open Science MOOC",
                   "installations": []
               },
               {
                   "provider_id": "sites",
                   "name": "Swedish Infrastructure for Ecosystem Science",
                   "installations": [
                       {
                           "installation_id": "62da5f68d3d2e80761293830",
                           "installation": "GRNET-KNS-1",
                           "infrastructure": "okeanos-knossos-1"
                       }
                   ]
               }
           ]
       }
   ],
   "links": []

```


Otherwise, an empty response will be returned.




### Errors

Please refer to section [Errors](./api_errors) to see all possible Errors.
