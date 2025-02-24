---
id: project
title: Project
sidebar_position: 3
---

# Project

A Project is the main resource of the Accounting System. The first step a user m
ust follow is to create a project under which the metric data will belong.

Currently, we support only projects that are available in the EU database.  Basi
cally, we register information about a particular Project in our system using th
e unique ID that every European Project has for its identification.

## [POST] - Associate Providers with a specific Project

To be able to register Metrics in an installation, first you must associate a Pr
oject with one or more  Providers. The following action is responsible for gener
ating a hierarchical relationship between a Project and one or more Providers:

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
   "message": "The following providers [bioexcel, osmooc, grnet, sites] have bee
n associated with Project {project_id}"
}
```

## [POST] - Dissociate Providers from a Project

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
   "message": "The following providers [grnet] have been dissociated from Projec
t {project_id}"
}
```

If there are Installations registered to Provider, the dissociation is not allow
ed:

Error Response `409 CONFLICT`

```
{
   "code": 409,
   "message": "Dissociation is not allowed. There are registered Installations t
o {project_id, provider_id}"
}
```

## [GET] - Project Hierarchical Structure

You can retrieve the Providers and Installations associated with a specific proj
ect:

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

You can retrieve all Projects assigned to you by executing the following GET req
uest:

```
GET /accounting-system/projects

Authorization: Bearer {token}
```

By default, the first page of 10 Projects will be returned. You can tune the def
ault values by using the query parameters page and size as shown in the example
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

## [POST] - Access Control Entry for a specific Project {#post-access-control}

The general endpoint that is responsible for creating an Access Control entry fo
r a Project is as follows:

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

where `{who}` is the client ID in which the roles will be assigned.

The response is :

Success Response `200 OK`

```
{
   "code": 200,
   "message": "Project Access Control was successfully created."
}
```

One client can have different roles at different Projects. For instance, in one
Project can be an admin executing all the Project operations while in another it
 can only read the Project Metrics.
Consequently, any client can have different responsibilities at different Projec
ts. The actions the client can perform at each Project are determined by the rol
e, and the permissions it has.

**Keep in mind that** to execute the above operation, you must have been assigne
d a role containing the Project Acl permission.

## [POST] - Search for Projects

You can search on Projects, to find the ones corresponding to the given search c
riteria. Projects  can be searched by executing the following request:

```
POST accounting-system/projects/search
Content-Type: application/json


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
object is described [**here**](https://argoeu.github.io/argo-accounting/docs/guides/search-filter).
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

## Errors

Please refer to section [Errors](./api_errors) to see all possible Errors.
