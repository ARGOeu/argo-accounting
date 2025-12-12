---
id: accounting_system_api
title: Accounting System API
sidebar_position: 1
---

The Accounting System is a platform responsible for collecting, aggregating, and exchanging metrics between different infrastructures, providers, and projects.

The platform’s main functions are exposed through a REST API, which provides the following capabilities:

- Accept input from multiple sources.
- Store incoming data in a database.
- Aggregate the collected data.
- Provide all available metrics to various clients.
- Query accounting data for specific time periods.
- Report aggregated information grouped for more precise resource consumption analysis.
- Assign capacities, enabling usage percentages reports.

All API resources are restricted to authenticated clients. Every client requesting access must be properly authenticated to interact with the API.

---

## Event-Driven Capabilities

The Accounting Service can also listen to messages from the [Argo Messaging Service](https://grnet.gr/en/services/cloud-services/argo-messaging-service/) and automatically perform actions based on these events. This enables real-time interaction and automated workflows, extending the API’s functionality beyond standard request/response operations.
