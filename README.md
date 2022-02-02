# ARGO-accounting
ARGO Accounting

This project uses Quarkus, the Supersonic Subatomic Java Framework.

## Prerequisites
- Java 11+
- Apache Maven 3.8.1+
- Docker (for dev mode)

## Dev Services

Quarkus supports a feature called Dev Services that allows you to create various datasources without any config. 
In the case of MongoDB this support extends to the default MongoDB connection. 
What that means practically, is that if you have not configured `quarkus.mongodb.connection-string` Quarkus 
will automatically start a MongoDB container when running tests or dev-mode, and automatically configure the connection.

When running the production version of the application, the MongoDB connection need to be configured as normal, so if you want to include a production database config in your `application.properties` and continue to use Dev Services we recommend that you use the `%prod.` profile to define your MongoDB settings.

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

## Unit and Metric type

The possible values of `unit_type` and `metric_type` are defined in two separate files, `unit_type.conf` and `metric_type.conf` respectively.
The application reads those files and offers to the clients the available values by the respective GET requests.
You can set the path to those files by filling in the following attributes in the `application.properties`:
- `unit.types.file`
- `metric.types.file`

Finally, under the folder `/files`, you can find samples of those files.

## Deploying the Accounting System API

To deploy the API in a machine :

- First, you have to set up a mongo database (We are currently using `4.0.28`, but other versions may be compatible)
- Second, you have to put the `unit_type.conf` and `metric_type.conf` in the machine filesystem
- Third, there must be an _über-jar_ version of the API on this machine

Before running the _über-jar_, you have to export the following variables:

- export `QUARKUS_MONGODB_CONNECTION_STRING=mongodb://{host}:{port}`
- export `UNIT_TYPES_FILE={filesystem path to unit_type.conf}`
- export `METRIC_TYPES_FILE={filesystem path to metric_type.conf}`

Once the variables above have been exported, you should execute the following command:
`java -jar *-runner.jar`

## Expose OpenAPI Specifications

Once the application is started, you can make a request to the `/open-api` endpoint:

`curl http://localhost:8080/open-api`

## Swagger UI
Swagger UI is accessible at `/swagger-ui` endpoint:

`http://localhost:8080/swagger-ui`

