---
id: project
title: Project
sidebar_position: 3
---

# Project

A Project is the main resource of the Accounting System. The first step a user
must follow is to create a project under which the metric data will belong.

In the context of the Accounting System, creating a Node requires the use of the Project entity as well.

## [POST] - Associate Providers with a specific Project

To be able to register Metrics in an installation, first you must associate a Project
with one or more  Providers. The following action is responsible for gener
ating a hierarchical relationship between a Project and one or more Providers:

```
POST /accounting-system/projects/{project_id}/associate/provider/{provider_id}

Content-Type: application/json
Authorization: Bearer {token}
```

The response is:

Success Response `200 OK`

```
Provider has been successfully associated with the Project.
```

## [POST] - Dissociate Providers from a Project

You can also dissociate one or more Providers from a Project:

```
POST /accounting-system/projects/{project_id}/dissociate/provider/{provider_id}

Content-Type: application/json
Authorization: Bearer {token}
```

The response is :

Success Response `200 OK`

```
Provider has been successfully dissociated from the Project.
```

If there are Installations registered to Provider, the dissociation is not allowed:

Error Response `409 CONFLICT`

```
{
   "code": 409,
   "message": "Dissociation is not allowed. There are registered Installations to
{project_id, provider_id}"
}
```

## [GET] - Project Hierarchical Structure

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
    "title": "The Missing Link of Episodic Memory Decline in Aging: The Role of
             Inefficient Systems Consolidation",
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

## [GET] - Fetch all Projects

You can retrieve all Projects assigned to you by executing the following GET request:

```
GET /accounting-system/projects

Authorization: Bearer {token}
```

By default, the first page of 10 Projects will be returned. You can tune the default 
values by using the query parameters page and size as shown in the example
below.

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
            "title": "DNA-sensing by AIM2 in activated B cells: novel targets to
                     improve allogeneic haematopoietic stem cell
                     transplantation",
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
            "title": "Is the hippocampal mossy fiber synapse a detonator in
                     vivo?",
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
                    "name": "Open Biomedical Engineering e-platform for
                            Innovation through Education",
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
            "title": "Sexual and Gender Non-Normativity in Opera after the
                     Second World War",
            "start_date": "2020-11-01",
            "end_date": "2024-05-02",
            "call_identifier": "H2020-MSCA-IF-2019",
            "providers": []
        },
        {
            "id": "843702",
            "acronym": "LCxLCProt",
            "title": "Comprehensive two-dimensional liquid chromatography for
                     the characterization of protein biopharmaceuticals at
                     the protein level",
            "start_date": "2020-01-01",
            "end_date": "2020-12-31",
            "call_identifier": "H2020-MSCA-IF-2018",
            "providers": []
        },
        {
            "id": "894897",
            "acronym": "DEFORM",
            "title": "Dead or Alive: Finding the Origin of Caldera Unrest
                     using Magma Reservoir Models",
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
            "title": "Playing at the Gateways of Europe: theatrical languages
                     and performatives practices in the Migrants' Reception
                     Centres of the Mediterranean Area",
            "start_date": "2020-10-01",
            "end_date": "2023-09-30",
            "call_identifier": "H2020-MSCA-IF-2019",
            "providers": []
        },
        {
            "id": "654496",
            "acronym": "NanoCytox",
            "title": "Development of Novel Analytical Methods to assess
                     Nanoparticle cytotoxicity",
            "start_date": "2015-11-01",
            "end_date": "2017-10-31",
            "call_identifier": "H2020-MSCA-IF-2014",
            "providers": []
        },
        {
            "id": "701538",
            "acronym": "TransIt",
            "title": "Translating science in the long Italian
                     Eighteenth-Century. The role of translators and publishers
                     as “cultural mediators” (1760-1790s)",
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

## Access Control Entry for a specific Project

### [POST] - Creation of a new entitlement

The general endpoint responsible for creating an Access Control entry for a Project, via the “Create a new entitlement” operation, is as follows:

```
POST /accounting-system/admin/entitlements

Authorization: Bearer {token}

{
  "name": "{namespace}:group:accounting:{project_id}:role=admin"
}
```
where `{project_id}` refers to the previously registered project and the provided `{namespace}` e.g. urn:geant:sandbox.eosc-beyond.eu:core:integration.

The response is :

Success Response `201 OK`

```
{
   "code": 201,
   "message": "Entitlement created successfully."
}
```

### [POST] - Association of an entitlement with a client/actor

The general endpoint responsible for the association of an entitlement with a client/actor.

```
POST /accounting-system/admin/entitlements/{id}/assign/{actor_id}

Authorization: Bearer {token}

```

where `{id}` refers to the previously registered entitlement, and `{actor_id}` refers to the previously registered client/actor.

The response is :

Success Response `200 OK`

```
{
   "code": 200,
   "message": "Entitlement was assigned successfully."
}
```

One client can have different roles at different Projects. For instance, in one
Project can be an admin executing all the Project operations while in another it
 can only read the Project Metrics.
Consequently, any client can have different responsibilities at different Projects. 
The actions the client can perform at each Project are determined by the rol
e, and the permissions it has.

### Note

See [Mapping of Roles to Entitlements](../authorization/accounting_system_roles#mapping-of-roles-to-entitlements) for assigning a role to a client/actor.

## [POST] - Search for Projects

You can search on Projects, to find the ones corresponding to the given search 
criteria. Projects  can be searched by executing the following request:

```
POST accounting-system/projects/search
Content-Type: application/json
Authorization: Bearer {token}

```

### Example 1

```
 {
      "type": "query",
      "field": "title",
      "values": "Functional and Molecular Characterisation of Breast Cancer Stem
                Cells",
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
      "field": "title",
      "values": "Functional and Molecular Characterisation of Breast Cancer Stem
                Cells",
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

The context of the request should be a JSON object. The syntax of the JSON
object is described [**here**](../guides/api_actions/search-filter).
If the operation is successful, you get a list of projects

```
{
    "size_of_page": 1,
    "number_of_page": 1,
    "total_elements": 1,
    "total_pages": 1,
    "content": [
        {
            "id": "655710",
            "acronym": "CONRICONF",
            "title": "Contentious Rights: A Comparative Study of International
                      Human Rights Norms and their Effects on Domestic Social
                      Conflict",
            "start_date": "2016-01-17",
            "end_date": "2020-02-08",
            "call_identifier": "H2020-MSCA-IF-2014",
            "providers": [
                {
                    "id": "msw",
                    "name": "MyScienceWork",
                    "website": "https://www.mysciencework.com",
                    "abbreviation": "MSW",
                    "logo": "https://www.mysciencework.com/bundles/home/images/logo-secondary-medium.png?202012111405-56",
                    "installations": [
                        {
                            "id": "635111fcbc140d44766be86b",
                            "infrastructure": "okeanos-knossos-5",
                            "installation": "GRNET-KNS-5",
                            "unit_of_access": "635111dabc140d44766be86a"
                        }
                    ]
                },
                {
                    "id": "meeo",
                    "name": "Meteorological Environmental Earth Observation",
                    "website": "https://www.meeo.it/",
                    "abbreviation": "MEEO",
                    "logo": "https://www.meeo.it/wp-content/uploads/2020/09/logo-meeo-dark2.svg",
                    "installations": []
                }
            ]
        }
    ],
    "links": []
}

```

Otherwise, an empty response will be returned.

## [GET] - Get Project Report

The report includes aggregated metric values grouped by metric definitions for all Providers and Installations belonging to a specific Project.

You can retrieve a report for a specific **Project** within a defined time period. When capacities are defined for the selected time period, the report also includes usage percentages broken down by the capacity-defined time periods.

```
GET /accounting-system/projects/{project_id}/report

Authorization: Bearer {token}
```


**Parameters**

- `project_id` *(required, path)* – The Project ID.  
  Example: `704029`  
- `start` *(required, query)* – Start date in `yyyy-MM-dd` format.  
  Example: `2024-01-01`  
- `end` *(required, query)* – End date in `yyyy-MM-dd` format.  
  Example: `2024-12-31`  

**Success Response `200 OK`**

```json
{
  "id": "447535",
  "acronym": "GRNET-447535",
  "title": "Project 447535",
  "start_date": "2022-01-01",
  "end_date": "2022-12-31",
  "call_identifier": null,
  "aggregated_metrics": [
    {
      "metric_definition_id": "507f1f77bcf86cd799439011",
      "metric_name": "storage",
      "metric_description": "The storage of the facility",
      "unit_type": "TB hours",
      "metric_type": "aggregated",
      "total_value": 2234.56
    }
  ],
  "data": [
    {
      "provider_id": "grnet",
      "name": "National Infrastructures for Research and Technology",
      "website": "https://www.grnet.gr",
      "abbreviation": "GRNET",
      "logo": "https://grnet.gr/wp-content/uploads/2023/01/01_EDYTE_LOGO_COLOR-1.png",
      "external_id": null,
      "aggregated_metrics": [
        {
          "metric_definition_id": "507f1f77bcf86cd799439011",
          "metric_name": "storage",
          "metric_description": "The storage of the facility",
          "unit_type": "TB hours",
          "metric_type": "aggregated",
          "total_value": 1234.56
        }
      ],
      "data": [
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
      ]
    },
    {
      "provider_id": "egi",
      "name": "European Grid Infrastructure",
      "website": "https://www.egi.eu",
      "abbreviation": "EGI",
      "logo": "https://cdn.egi.eu/app/uploads/2021/11/egi-logo.svg",
      "external_id": null,
      "aggregated_metrics": [
        {
          "metric_definition_id": "68c9199ec0b09a61b2778a5b",
          "metric_name": "storage",
          "metric_description": "Storage provided by EGI",
          "unit_type": "TB hours",
          "metric_type": "aggregated",
          "total_value": 1000
        }
      ],
      "data": [
        {
          "installation_id": "68c9199ec0b09a61b2778a60",
          "project": "447535",
          "provider": "egi",
          "installation": "EGI-Cloud1",
          "infrastructure": "egi-infra",
          "resource": "cloud",
          "external_id": "installation-55555",
          "data": [
            {
              "metric_definition_id": "68c9199ec0b09a61b2778a5b",
              "metric_name": "storage",
              "metric_description": "Storage provided by EGI",
              "unit_type": "TB hours",
              "metric_type": "aggregated",
              "periods": [
                {
                  "from": "2022-01-01T00:00:00Z",
                  "to": "2022-12-31T23:59:59Z",
                  "total_value": 1000,
                  "capacity_value": 1200,
                  "usage_percentage": 83
                }
              ]
            }
          ]
        }
      ]
    }
  ]
}
```

## Errors

Please refer to section [Errors](./api_errors) to see all possible Errors.
