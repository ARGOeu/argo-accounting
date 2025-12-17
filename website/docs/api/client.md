---
id: client
title: Client
sidebar_position: 1
---

# Client

As a client/actor we define either a user or a service that communicates and
interacts with the Accounting System API.
In order for a client to be able to interact with the API, first it has to
register itself into the Accounting System. Based on the
Client's OIDC ID and issuer the Accounting System API assigns
different Roles to different Clients.
Consequently, a client cannot get a Role unless registration has been
completed
first.

## [POST] - Client Registration

One client/actor can register itself into the Accounting System by
executing the following request:

```
POST /accounting-system/clients

Authorization: Bearer {token}
```

Once the above request is executed, we extract the following
information from the token:

- name
- email
- OIDC ID
- AAI issuer

Then we store it into the database collection Client/Actor:

| Field            | Description                          |
|------------------|---------------------------------------- |
|id|An identifier for the client that is globally unique and not reassignable.|
|oidc_id |The OIDC subject identifier associated with the client.|
| name             | The client's full name. |
| email            | The client’s email. |
|registered_on | The timestamp when the client registered.|
|issuer | The issuer of the authentication token.|

## [GET] - Read the registered Clients

A system Admin can read the registered clients/actors by executing the following request:

```
GET /accounting-system/admin/actors

Authorization: Bearer {token}
```

## Errors

Please refer to section [Errors](./api_errors) to see all possible Errors.

## Note

See [Mapping of Roles to Entitlements](../authorization/accounting_system_roles#mapping-of-roles-to-entitlements) for assigning a role to a client/actor.
