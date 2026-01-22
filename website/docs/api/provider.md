---
id: provider
title: Provider
sidebar_position: 4
---

# Provider

As Provider, we refer to the Organisation that offers at least one installation
to a specific project.

You can create a new Provider by using the endpoints the
Accounting Service provides.

## Registering Provider through Accounting System API

If the Provider is not registered in the EOSC Resource Catalogue, the API
offers
the functionality to create a new one. Below, we will describe the available
operations regarding the Provider that you can interact with.

## [POST] - Create a new Provider

You can submit a new Provider by executing the following POST request:

```
POST /accounting-system/providers

Content-type: application/json
Authorization: Bearer {token}

{
  "id" : "grnet",
  "name" : "National Infrastructures for Research and Technology",
  "website" : "https://www.grnet.gr/",
  "abbreviation" : "GRNET",
  "logo": : "https://grnet.gr/wp-content/uploads/sites/13/2016/04/GRNET_Logo_Transparent-e1431694322566-1.png"
}
```

The response is the stored Provider:

Success Response `201 OK`

```
{
  "id" : "grnet",
  "name" : "National Infrastructures for Research and Technology",
  "website" : "https://www.grnet.gr/",
  "abbreviation" : "GRNET",
  "logo": : "https://grnet.gr/wp-content/uploads/sites/13/2016/04/GRNET_Logo_Transparent-e1431694322566-1.png",
  "creator_id": "115143399384cc3177df5377691ccdbb284cb245fad1c@aai.eosc-portal.eu"
}
```

## [DELETE] - Delete an existing Provider

You can also delete an existing Provider registered through the Accounting
System API by executing the following request:

```
DELETE /accounting-system/providers/{provider_id}

Authorization: Bearer {token}

```

If the deletion is successful the following response is returned:

Success Response `200 OK`

```
{
   "code": 200,
   "message": "Provider has been deleted successfully."
}
```

## [PATCH] - Update an existing Provider

You can update an existing Provider registered through the Accounting System
API by executing the following request:

```
PATCH /accounting-system/providers/{provider_id}

Content-type: application/json
Authorization: Bearer {token}

{
  "id" : "id to be updated",
  "name" : "name to be updated",
  "website" : "website to be updated",
  "abbreviation" : "abbreviation to be updated",
  "logo" : "logo to be updated"
}

```

The body of the request must contain an updated representation of Provider.
You can update a part or all attributes of the Provider. The empty or null
values are ignored.

The response will be the updated entity :

Success Response `200 OK`

```
{
   "updated_entity"
}
```

**Finally, it should be noted that updating Providers which derive from the
EOSC Resource Catalogue is not allowed.**

## [GET] - Fetch a registered Provider

You can either fetch a Provider registered through the Accounting System
API or EOSC Resource Catalogue by executing the following GET HTTP request:

```
GET /accounting-system/providers/{provider_id}

Authorization: Bearer {token}
```

The response is as follows:

Success Response `200 OK`

```
{
   "id": "osmooc",
   "name": "Open Science MOOC",
   "website": "https://opensciencemooc.eu/",
   "abbreviation": "OSMOOC",
   "logo": "https://opensciencemooc.eu/assets/img/osm/osm-logo.png",
   "creator_id": "115143399384cc3177df5377691ccdbb284cb245fad1c@aai.eosc-portal.eu"
}
```

## [GET] - Fetch all registered Providers

Essentially, the following endpoint returns all Providers available on the EOSC
Resource Catalogue as well as all Providers registered through the Accounting
System API. By default, the first page of 10 Providers will be returned.

```
GET /accounting-system/providers

Authorization: Bearer {token}
```

You can tune the default values by using the query parameters page and size as
shown in the example below.

```
GET /accounting-system/providers?page=2&size=15

Authorization: Bearer {token}
```

The above request returns the second page which contains 15 providers:

Success Response `200 OK`

```
{
   "size_of_page": 15,
   "number_of_page": 2,
   "total_elements": 237,
   "total_pages": 16,
   "content": [
       {
           "id": "ubora",
           "name": "Open Biomedical Engineering e-platform for Innovation
                   through Education",
           "website": "http://ubora-biomedical.org/",
           "abbreviation": "UBORA",
           "logo": "http://ubora-biomedical.org/wp-content/uploads/2017/01/UBORA-Logo-Final-JPEGb.jpg",
           "creator_id": ""
       },
       {
           "id": "bioexcel",
           "name": "BioExcel Centre of Excellence",
           "website": "https://bioexcel.eu/",
           "abbreviation": "BIOEXCEL",
           "logo": "https://bioexcel.eu/wp-content/uploads/2017/02/Bioexcell_logo_payoff_1080px_transp.png",
           "creator_id": ""
       },
       {
           "id": "surf-nl",
           "name": "SURF",
           "website": "https://www.surf.nl/",
           "abbreviation": "SURF",
           "logo": "https://www.surf.nl/themes/surf/logo.svg",
           "creator_id": ""
       },
       {
           "id": "emso_eric",
           "name": "European Multidisciplinary Seafloor and water column Observatory",
           "website": "http://emso.eu",
           "abbreviation": "EMSO ERIC",
           "logo": "http://emso.eu/wp-content/uploads/2018/03/logo-w-300.png",
           "creator_id": ""
       },
       {
           "id": "elixir-belgium",
           "name": "ELIXIR Belgium",
           "website": "https://www.elixir-belgium.org/",
           "abbreviation": "ELIXIR Belgium",
           "logo": "https://www.elixir-belgium.org/sites/default/files/logo.jpg",
           "creator_id": ""
       },
           "id": "cyi",
           "name": "The Cyprus Institute",
           "website": "https://www.cyi.ac.cy/",
           "abbreviation": "CyI",
           "logo": "https://numismatics-medieval.dioptra.cyi.ac.cy/sites/default/files/CyI.png",
           "creator_id": ""
       },
       {
           "id": "uni-freiburg",
           "name": "University of Freiburg",
           "website": "https://www.uni-freiburg.de",
           "abbreviation": "UNI FREIBURG",
           "logo": "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSH9gnjhQ5503SSs1_6E8Rr7UITWeacwmm3u3rK1eSdLQiKdywpqQ&s",
           "creator_id": ""
       },
       {
           "id": "dcc-uk",
           "name": "Digital Curation Centre",
           "website": "https://www.dcc.ac.uk/",
           "abbreviation": "DMPonline",
           "logo": "https://arts.unimelb.edu.au/__data/assets/image/0009/2913867/DDC2019.jpg",
           "creator_id": ""
       },
       {
           "id": "cloudferro",
           "name": "CloudFerro",
           "website": "https://cloudferro.com/",
           "abbreviation": "CloudFerro",
           "logo": "https://cf2.cloudferro.com:8080/94d2acacf10346a18c289981f4d0bd33:cloudferro_cms/static/img/cloudferro-logo.svg",
           "creator_id": ""
       },
       {
           "id": "psnc",
           "name": "Poznan Supercomputing and Networking Center",
           "website": "https://www.psnc.pl/",
           "abbreviation": "PSNC",
           "logo": "https://www.psnc.pl/files/PSNC_logo_.svg",
           "creator_id": ""
       },
       {
           "id": "forschungsdaten",
           "name": "Forschungsdaten.info",
           "website": "https://www.forschungsdaten.info/praxis-kompakt/english-pages/",
           "abbreviation": "forschungsdaten",
           "logo": "https://www.forschungsdaten.info/typo3temp/secure_downloads/99679/0/ba8ae03722cb4bfb8baa63cfb1ab045bb7a03c78/csm_logo_long_seeblau100_af47d6577b.png",
           "creator_id": ""
       },
       {
           "id": "umg-br",
           "name": "University of Minas Gerais",
           "website": "https://ufmg.br/",
           "abbreviation": "UMG",
           "logo": "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2d/Symbolfumg.jpg/375px-Symbolfumg.jpg",
           "creator_id": ""
       },
       {
           "id": "coard",
           "name": "Collaborative Open Access Research and Development",
           "website": "https://coard.community/",
           "abbreviation": "COARD",
           "logo": "https://images.squarespace-cdn.com/content/v1/5f44f92bd523407260108ba6/1598444273981-CRFZ1SC35B9VTRNT0QUS/Coard+logo%402x.png",
           "creator_id": ""
       },
       {
           "id": "erasmusmc",
           "name": "Erasmus Medical Center",
           "website": "https://www.erasmusmc.nl/",
           "abbreviation": "Erasmus MC",
           "logo": "https://seeklogo.com/images/E/Erasmus_MC-logo-B857CA0725-seeklogo.com.png",
           "creator_id": ""
       },
       {
           "id": "osmooc",
           "name": "Open Science MOOC",
           "website": "https://opensciencemooc.eu/",
           "abbreviation": "OSMOOC",
           "logo": "https://opensciencemooc.eu/assets/img/osm/osm-logo.png",
           "creator_id": ""
       }
   ],
   "links": [
       {
           "href": "https://acc.devel.argo.grnet.gr/accounting-system/providers?page=1&size=15",
           "rel": "first"
       },
       {
           "href": "https://acc.devel.argo.grnet.gr/accounting-system/providers?page=16&size=15",
           "rel": "last"
       },
       {
           "href": "https://acc.devel.argo.grnet.gr/accounting-system/providers?page=2&size=15",
           "rel": "self"
       },
       {
           "href": "https://acc.devel.argo.grnet.gr/accounting-system/providers?page=1&size=15",
           "rel": "prev"
       },
       {
           "href": "https://acc.devel.argo.grnet.gr/accounting-system/providers?page=3&size=15",
           "rel": "next"
       }
   ]
}
```

## Access Control Entry for a specific Provider

### [POST] – Creation of a new entitlement

The general endpoint responsible for creating an Access Control entry for a Provider, via the “Create a new entitlement” operation, is as follows:

```
POST /accounting-system/admin/entitlements

Authorization: Bearer {token}

{
  "name": "{namespace}:group:accounting:{project_id}:{provider_id}:role=admin"
}
```

where `{project_id}` and `{provider_id}` refers to the previously registered project and provider respectively as the the provided `{namespace}` as well, e.g. `urn:geant:sandbox.eosc-beyond.eu:core:integration`.

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
A client can have different roles across different Projects. The actions a client can perform for each Provider are determined by the assigned role and its associated permissions.

### Note

See [Mapping of Roles to Entitlements](../authorization/accounting_system_roles#mapping-of-roles-to-entitlements) for assigning a role to a client/actor.


## [POST] - Search for Providers

You can search on Providers, to find the ones corresponding to the given search
criteria. Providers  can be searched by executing the following request:

```
POST accounting-system/providers/search
Content-Type: application/json
Authorization: Bearer {token}
```

### Example 1

```
{

           "type":"query",
           "field": "name",
           "values":"European Space Agency",
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

           "type":"query",
           "field": "name",
           "values":"European Space Agency",
           "operand": "eq"
   },
    {

           "type":"query",
           "field": "abbreviation",
           "values":"SITES",
           "operand": "eq"
   }
  ]
}
```

The context of the request should be a JSON object. The syntax of the JSON
object is described [**here**](../guides/api_actions/search-filter).
If the operation is successful, you get a list of providers.

```
{
   "size_of_page": 2,
   "number_of_page": 1,
   "total_elements": 2,
   "total_pages": 1,
   "content": [
       {
           "id": "esa-int",
           "name": "European Space Agency",
           "website": "https://www.esa.int/",
           "abbreviation": "ESA",
           "logo": "https://www.esa.int/var/esa/storage/images/esa_multimedia/images/2014/03/b_esa_b/14337112-1-eng-GB/b_ESA_b_pillars.png"
       },
       {
           "id": "sites",
           "name": "Swedish Infrastructure for Ecosystem Science",
           "website": "https://www.fieldsites.se/en-GB",
           "abbreviation": "SITES",
           "logo": "https://dst15js82dk7j.cloudfront.net/231546/95187636-P5q11.png"
       }
   ],
   "links": []

```

Otherwise, an empty response will be returned.
**Keep in mind that** to execute the above operation, you must have been
assigned a role containing the Provider Acl permission.

## [GET] - Get Provider Report

The report includes aggregated metric values grouped by metric definitions for all Installations belonging
to a specific Provider.

You can retrieve a report for a specific **Provider** within a defined time period. When capacities are defined for the selected time period, the report also includes usage percentages broken down by the capacity-defined time periods.

```
GET /accounting-system/projects/{project_id}/providers/{provider_id}/report

Authorization: Bearer {token}
```

or by using the `externalId` instead.

```
GET /accounting-system/providers/external/report

Authorization: Bearer {token}
```

**Parameters**

- `project_id` *(required, path)* – The Project ID.  
  Example: `704029`  
- `provider_id` *(required, path)* – The Provider ID.  
  Example: `grnet`  
- `start` *(required, query)* – Start date in `yyyy-MM-dd` format.  
  Example: `2024-01-01`  
- `end` *(required, query)* – End date in `yyyy-MM-dd` format.  
  Example: `2024-12-31`  

**Success Response `200 OK`**

```json
  {
    "provider_id": "grnet",
    "name": "National Infrastructures for Research and Technology",
    "website": "https://www.grnet.gr",
    "abbreviation": "GRNET",
    "logo": "https://grnet.gr/wp-content/uploads/2023/01/01_EDYTE_LOGO_COLOR-1.png",
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
  }
```

## [GET] - Get Global Provider Report

The report includes aggregated metric values grouped by metric definitions for all Installations belonging
to a specific Provider, independently of a Project.

You can retrieve a report for a specific **Provider** within a defined time period. When capacities are defined for the selected time period, the report also includes usage percentages broken down by the capacity-defined time periods.

```
GET /accounting-system/providers/{provider_id}/report

Authorization: Bearer {token}
```

**Parameters**

- `provider_id` *(required, path)* – The Provider ID.  
  Example: `grnet`  
- `start` *(required, query)* – Start date in `yyyy-MM-dd` format.  
  Example: `2024-01-01`  
- `end` *(required, query)* – End date in `yyyy-MM-dd` format.  
  Example: `2024-12-31`  

**Success Response `200 OK`**

```json
{
  "reports": [
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
          "total_value": 2469.12
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
        },
        {
          "installation_id": "507f1f77bcf86cd799439012",
          "project": "447536",
          "provider": "grnet",
          "installation": "GRNET-KNS",
          "infrastructure": "okeanos-knossos",
          "resource": "cloud",
          "external_id": "installation-45584",
          "data": [
            {
              "metric_definition_id": "507f1f77bcf86cd799439011",
              "metric_name": "storage",
              "metric_description": "The storage of the facility",
              "unit_type": "TB hours",
              "metric_type": "aggregated",
              "periods": [
                {
                  "from": "2022-02-01T00:00:00Z",
                  "to": "2022-02-28T23:59:59Z",
                  "total_value": 1234.56,
                  "capacity_value": 1000,
                  "usage_percentage": 80
                }
              ]
            }
          ]
        }
      ]
    }
  ],
  "aggregated_metrics": [
    {
      "metric_definition_id": "507f1f77bcf86cd799439011",
      "metric_name": "storage",
      "metric_description": "The storage of the facility",
      "unit_type": "TB hours",
      "metric_type": "aggregated",
      "total_value": 2469.12
    }
  ]
}
```

## Errors

Please refer to section [Errors](./api_errors) to see all possible Errors.

## Note

Instead of the internal ID, you can use the external ID in the corresponding API calls.
