---
id: test_prod_environment
title: Development and Production Environment
sidebar_position: 1
slug: /
---

Our development and production environments are designed to facilitate the development, testing, and deployment of our software applications in a secure and stable environment. This document provides an overview of our development and production environments.

The purpose of our development and production environments are as follows:

- **Development Environment:** To provide a secure and stable environment for software development and testing activities. This environment is isolated from our production environment, ensuring that any issues or bugs discovered during development and testing do not affect our production systems.

- **Production Environment:** To provide a secure and stable environment for hosting and running our software applications in a live production environment.

Our development and production environments consist of the following components:

- **Accounting System API:** The REST API is the core component of our software applications. It receives input data from external systems and stores it in the database. The API is responsible for aggregating and processing the data, making it available for querying through the user interface. 

- **User Interface:** The user interface is the primary point of interaction for clients to access the Accounting Service. It provides a graphical interface that allows users to view and interact with accounting data.

### Development Environment Location

Our development environment consists of two components: the REST API and User Interface. 
The REST API is accessible via the development API URL, which is currently https://acc.devel.argo.grnet.gr/. The User Interface is accessible via the development UI URL, which is currently https://accounting-eosc-dev.apps.wok.in2p3.fr/. 

This environment is dedicated to development and testing activities and is isolated from the production environment. Access to the development environment is restricted to authorized personnel only. 
If you require access to our development environment via the API, please identify yourself at the [demo AAI Proxy](https://acc.devel.argo.grnet.gr/oidc-client/index.php). Otherwise, just log in to development Accounting User Interface.

### Production Environment Location

Our production environment consists of two components: the REST API and User Interface.
The REST API is accessible via the production API URL, which is currently https://api.accounting.argo.grnet.gr/. The User Interface is accessible via the production UI URL, which is currently https://accounting.eosc-portal.eu/.

Access to the production environment is restricted to authorized personnel only.
If you require access to our production environment via the API, please identify yourself at the [production AAI Proxy](https://api.accounting.argo.grnet.gr/oidc-client/index.php). Otherwise, just log in to production Accounting User Interface.

