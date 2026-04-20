---
title: Getting Started
nav_order: 2
description: "Installation and first-run setup for EzTax"
---

# Getting Started
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---

## Requirements

| Requirement | Version |
|---|---|
| Paper (or fork) | 1.21+ |
| Java | 17+ |
| Vault | Latest |
| Economy plugin | Any Vault-compatible (EssentialsX, CMI, etc.) |

---

## Installation

1. Download the latest `EzTax-x.x.x.jar` from the releases page.
2. Place the JAR in your server's `plugins/` folder.
3. Start (or restart) the server.
4. Edit `plugins/EzTax/config.yml` to configure taxes as needed.
5. Run `/tax reload` to apply your changes.

---

## First-run checklist

After the first start, work through these steps:

1. **Confirm Vault is hooked** — check the console for `[EzTax] Vault economy hooked`.
2. **Enable the tax types you want** — each tax type defaults to either `true` or `false`;
   check [Configuration](Configuration.md) for defaults.
3. **Set rates** — configure `percentage` / `flat-fee` values for each enabled tax type.
4. **Choose a sink destination** — decide where collected money goes:
   `burn` (destroy), `player`, `pool`, or `command`. See [Tax Sink](config/tax-sink.md).
5. **Configure storage** — `yml` (default) requires no setup; `mysql` needs host/database/credentials.
   See [Storage](config/storage.md).
6. **Test** — use `/tax runtaxpayment` and `/tax runwealthtax` on a test account to verify
   rates before going live.

---

## Building from source

```bash
git clone <repository-url>
cd EzTax
mvn package -DskipTests
# Output: target/EzTax-x.x.x.jar
```

Dependencies are downloaded automatically via Maven/JitPack.

---

## Updating

1. Stop the server.
2. Replace the old JAR with the new one.
3. Start the server.

Your existing `config.yml` and `stats.yml` are preserved. Check the release notes for any new
config keys to add manually.
