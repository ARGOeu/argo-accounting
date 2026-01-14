---
id: oidc_providers
title: OIDC Providers
sidebar_position: 1
---

# OpenID Connect (OIDC)

## What is OIDC?

**OpenID Connect (OIDC)** is an identity layer built on top of the OAuth 2.0 protocol. It enables applications (clients) to verify the identity of end users or services based on authentication performed by an **Identity Provider (IdP)** and to obtain basic profile information in a secure, standardized way.

In practice, OIDC is used to:

- Authenticate users and services
- Obtain **access tokens** for calling protected APIs
- Federate identities across different organizations and infrastructures

OIDC is widely used in EOSC, AAI federations, and modern cloud-native services.

---

## How does OIDC work (high level)?

1. A **client** (user application or service) redirects or authenticates against an **OIDC Provider**.
2. The OIDC Provider authenticates the user or service.
3. The provider issues one or more tokens:
   - **ID Token** – identity information (who you are)
   - **Access Token** – authorization to access APIs
   - (optionally) **Refresh Token** – obtain new access tokens
4. The client sends the **access token** to a protected API (e.g. the Accounting Service).
5. The API validates the token using the issuer’s public keys and claims.

---

## How to get an OIDC token

The exact steps depend on the **grant type** used. The most common cases are described below.

### 1. Interactive user authentication

Used by web portals and CLI tools that authenticate a human user.

Typical steps:

1. Redirect the user to the OIDC Provider’s `/authorize` endpoint
2. User logs in via the IdP
3. The client exchanges the authorization code at the `/token` endpoint
4. The client receives an **access token**

This flow is suitable for:

- Web applications
- Command-line tools using device or browser-based login

---

### 2. Non-interactive authentication

Used by **services**, **daemons**, or **automated workflows**.

In this flow:

1. A client authenticates directly to the `/token` endpoint
2. Authentication is done using a **client ID** and **client secret** (or certificate)
3. The OIDC Provider returns an **access token**

This is the recommended approach for **service-to-service communication**.

---

## How to Have a Service Account

A **service account** represents a non-human identity that can authenticate to an OIDC provider using the **Client Credentials** grant. This is the recommended approach for backend services, automated workflows, pipelines, cron jobs, and other machine-to-machine communication.

For detailed, authoritative instructions, users **must follow the official AAI guides**:

- [EOSC Core – Registering services with the Core Infrastructure Proxy](https://docs.sandbox.eosc-beyond.eu/Service%20Portfolio/AAI/registering-services-with-the-core-infrastructure-proxy)

- [EU-Node AAI Service Registration Guide](https://docs.google.com/document/d/1n5fHjStY4t62Mqh4T9bvh7Kq7TMmIypKMLPzbhCvsjQ/edit?tab=t.0#heading=h.c8qb9dhlereg)

- [ENVRI-ID - Registering Services with the ENVRI-ID Infrastructure Proxy](https://docs.google.com/document/d/19cCcJ8FrgoYYk-_ZjKasUvwu5uXlMZfyxHKYgPShj7E/edit?tab=t.0#heading=h.c8qb9dhlereg)

## OIDC Providers registered in the Accounting Service

The Accounting Service supports multiple OIDC Providers depending on the deployment environment and logical node.

| Accounting Service instance | Node | OIDC Provider issuer |
|-----------------------------|------|----------------------|
| https://api.devel.acc.argo.grnet.gr/ | default | https://core-proxy.staging.sandbox.eosc-beyond.eu/auth/realms/core |
| https://api.devel.acc.argo.grnet.gr/ | envri_hub | https://login.staging.envri.eu/auth/realms/envri |
| https://api.acc.argo.grnet.gr | default | https://core-proxy.sandbox.eosc-beyond.eu/auth/realms/core |
| https://api.acc.argo.grnet.gr | envri_hub | https://login.envri.eu/auth/realms/envri |
| https://api.acc.staging.eosc.grnet.gr/ | default | https://core-infra-proxy.staging.eosc.grnet.gr/auth/realms/core <br />(for service accounts: https://proxy.staging.eosc-federation.eu)|
| https://api.acc.eosc.grnet.gr <br />(alias: https://acc.open-science-cloud.ec.europa.eu) | default | https://core-infra-proxy.aai.open-science-cloud.ec.europa.eu/auth/realms/core <br /> (for service accounts: https://proxy.aai.open-science-cloud.ec.europa.eu)|

---

## Notes

- The **issuer** value must exactly match the `iss` claim in the access token
- Tokens issued by non-registered issuers will be rejected
