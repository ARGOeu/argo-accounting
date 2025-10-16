---
id: test_prod_environment
title: Development and Production Environment
sidebar_position: 2
slug: /
---

# Development and Production Environment

## Introduction

Our development and production environments are designed to facilitate the
development, testing, and deployment of our software applications in a
secure and stable environment. This document provides an overview of our
development and production environments.

## Purpose

The purpose of our development and production environments are as follows:

- **Development Environment:** To provide a secure and stable environment
  for software development and testing activities. This environment is isolated
  from our production environment, ensuring that any issues or bugs discovered
  during development and testing do not affect our production systems.

- **Production Environment:** To provide a secure and stable environment for
  hosting and running our software applications in a live production environment.

## Components

Our development and production environments consist of the following components:

- **Accounting System API:** The REST API is the core component of our software
  applications. It receives input data from external systems and stores it in the
  database. The API is responsible for aggregating and processing the data, making
  it available for querying through the user interface.

- **User Interface:** The user interface is the primary point of interaction for
  clients to access the Accounting Service. It provides a graphical interface that
  allows users to view and interact with accounting data.

## Development Environment Location

Our development environment consists of two components: the REST API and User
Interface.
The REST API is accessible via the development API URL, which is currently
[https://api.devel.acc.argo.grnet.gr](https://api.devel.acc.argo.grnet.gr).

This environment is dedicated to development and testing activities and is
isolated from the production environment. Access to the development environment
is restricted to authorized personnel only.
If you require access to our development environment via the API, please
identify yourself at the [demo AAI Proxy](https://api.devel.acc.argo.grnet.gr/accounting-system/oidc-client).
Otherwise, just log in to development Accounting User Interface.

## Production Environment Location

Our production environment consists of two components: the REST API and
User Interface.
The REST API is accessible via the production API URL, which is currently
[https://api.acc.argo.grnet.gr](https://api.acc.argo.grnet.gr).
The Graphical User Interface is accessible via the production URL, which is currently
[https://ui.acc.argo.grnet.gr](https://ui.acc.argo.grnet.gr).

Access to the production environment is restricted to authorized personnel
only.
If you require access to our production environment via the API, please
identify yourself at the [production AAI Proxy](https://api.acc.argo.grnet.gr/accounting-system/oidc-client).
Otherwise, just log in to production Accounting User Interface.
