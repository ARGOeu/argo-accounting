# ARGO-accounting
ARGO Accounting

This project uses Quarkus, the Supersonic Subatomic Java Framework.

## Prerequisites

-   Java 11+
-   Apache Maven 3.8.1+
-   Docker (for dev mode)

## Dev Services

Quarkus supports the automatic provisioning of unconfigured services in development and test mode. 
They refer to this capability as Dev Services. 
From a developer’s perspective this means that if you include an extension and don’t configure it then Quarkus will automatically start the relevant service (usually using Testcontainers behind the scenes) 
and wire up your application to use this service.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
mvn clean compile quarkus:dev
```

## Packaging and running the application

The application can be packaged using:
```shell script
mvn clean package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
mvn clean package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:
```shell script
mvn clean package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:
```shell script
mvn clean package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/rest-json-quickstart-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Mongo Database

When running the production version of the application, the MongoDB connection needs to be configured as normal by setting the connection string in `quarkus.mongodb.connection-string`.
If you want to continue use Dev Services we recommend that you use the `%prod.` profile to define your MongoDB settings. What that means practically, is that Quarkus
will automatically start a MongoDB container when running tests or dev-mode, and automatically configure the connection.

## Unit and Metric type

The possible values of `unit_type` and `metric_type` are defined in two separate files, `unit_type.conf` and `metric_type.conf` respectively.
The application reads those files and offers to the clients the available values by the respective GET requests.
You can set the path to those files by filling in the following attributes in the `application.properties`:
-   `unit.types.file`
-   `metric.types.file`

Finally, under the folder `/files`, you can find samples of those files.

## Authentication

To access Accounting System API resources, you have to be authenticated by GRNET's keycloak.
This keycloak offers various Identity Providers where the authentication process can be performed.

There is an ancillary web page at `{accounting_system_host}` where you can identify yourself.
Once you log in, you obtain an access token. Using this token, you can access the available API operations.
These operations are protected and can only be accessed if a client is sending a bearer token along with the request,
which must be valid and trusted by the Accounting System API.

When passing in the access token in an HTTP header, you should make a request like the following:

```bash
curl http://localhost:8080/accounting-system/metric-definition
   -H "Authorization: Bearer {token}"
```
### Authentication with TestContainer in Dev mode

Quarkus starts a Keycloak container for both the dev and/or test modes and initializes them by registering the existing Keycloak realm or 
creating a new realm with the client and users for you to start developing the Accounting System application secured by Keycloak immediately. 
More details about how to obtain access tokens in dev mode you can find [here](https://quarkus.io/guides/security-openid-connect-dev-services).

### Authentication with External keycloak in Dev mode

If you want to integrate with external keycloak, you have to remove the prefix `%prod.` from `%prod.quarkus.oidc.auth-server-url`. 
Then you have to configure the following variables in `application.properties`:
-   `quarkus.oidc.auth-server-url={The base URL of the Keycloak server. The base URL will be in the following format: https://host:port/auth/realms/{realm}.`
-   `quarkus.oidc.client-id={Specifies an alpha-numeric string that will be used as the client identifier for OIDC requests}`
-   `quarkus.oidc.credentials.secret={Client secret which is used for a client_secret_basic authentication method}`

To use a UI Page to obtain an access token from the external keycloak, which will be accessible at {accounting_system_host}, you have to configure the following variables in `application.properties`:

-   `keycloak.server.url={The base URL of Keycloak server. Make sure the base URL is in the following format: https://host:port/auth}`
-   `keycloak.server.realm={The name of the Keycloak realm}`
-   `keycloak.server.client.id={Specifies an alpha-numeric string that will be used as the client identifier for OIDC requests}`

The `keycloak.server.url`, `keycloak.server.realm` and `keycloak.server.client.id` are used in order to feed the [keycloak.html](src/main/resources/templates/keycloak.html) template.
There also is an [endpoint](src/main/java/org/accounting/system/templates/KeycloakClientTemplate.java) which is responsible for rendering that html page. 

The keycloak.html is responsible for :
-   redirecting a user to Keycloak's login page in order to be authenticated
-   displaying the obtained token

Typically, the keycloak.html is a client-side JavaScript library that can be used to secure HTML5/JavaScript applications. More details you can find [here](https://github.com/keycloak/keycloak-documentation/blob/main/securing_apps/topics/oidc/javascript-adapter.adoc).

## Deploying the Accounting System API

To deploy the API in a machine :

-   First, you have to set up a mongo database (We are currently using `4.0.28`, but other versions may be compatible)
-   Second, you have to put the `unit_type.conf` and `metric_type.conf` in the machine filesystem
-   Third, there must be an _über-jar_ version of the API on this machine

Before running the _über-jar_, you have to export the following variables:
```bash
- export QUARKUS_MONGODB_CONNECTION_STRING=mongodb://{host}:{port}
- export UNIT_TYPES_FILE={filesystem path to unit_type.conf}
- export METRIC_TYPES_FILE={filesystem path to metric_type.conf}
- export SERVER_URL={The proxy server URL that acts on behalf of the Accounting System}
- export QUARKUS_OIDC_AUTH_SERVER_URL={The base URL of the OpenID Connect (OIDC) server. Note if you work with Keycloak OIDC server, make sure the base URL is in the following format: https://host:port/auth/realms/{realm} where {realm} has to be replaced by the name of the Keycloak realm}
- export QUARKUS_OIDC_CLIENT_ID={Specifies an alpha-numeric string that will be used as the client identifier for OIDC requests}
- export QUARKUS_OIDC_CREDENTIALS_SECRET={Client secret which is used for a client_secret_basic authentication method}
- export KEYCLOAK_SERVER_URL={The base URL of Keycloak server. Make sure the base URL is in the following format: https://host:port/auth}
- export KEYCLOAK_SERVER_REALM={The name of the Keycloak realm}
- export KEYCLOAK_SERVER_CLIENT_ID={Specifies an alpha-numeric string that will be used as the client identifier for OIDC requests}
```

Once the variables above have been exported, you should execute the following command:
`java -jar *-runner.jar`

## Expose OpenAPI Specifications

Once the application is started, you can make a request to the `/open-api` endpoint:

`curl {accounting_system_host}/open-api`

## Swagger UI

Swagger UI is accessible at `/swagger-ui` endpoint:

`{accounting_system_host}/swagger-ui`