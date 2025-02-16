---
id: api_errors
title: API Errors
sidebar_position: 11
---

# Errors

In case of Error during handling user’s request the API responds using the
following schema:

```json
{
  "code": 500,
  "message": "Internal Server Error"
}
```

## Error Codes

The following error codes are the possible errors of all methods

| Error          | Code | Status           | Related Requests        |
|------------|------|-------------|---------------|
| Bad Request         | 400  | BAD_REQUEST      | All POST, PATCH, and PUT     |
| Unauthorized        | 401  | UNAUTHORIZED          | All*          |
| Forbidden Access to Resource   | 403  | FORBIDDEN        | All**         |
| Entity Not Found          | 404  | NOT_FOUND        | All GET            |
| Entity already exists     | 409  | CONFLICT    | All POST, PATCH, and PUT     |
| Cannot consume content type |415|UNSUPPORTED_MEDIA_TYPE|All POST, PATCH, PUT|
| Internal Server Error     | 500  | INTERNAL_SERVER_ERROR      | All           |

\* _if a user is not authenticated_

\** _if a user is forbidden to access the resource_
