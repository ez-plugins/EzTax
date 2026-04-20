---
title: Configuration
nav_order: 5
description: "Complete configuration reference for EzTax"
---

# Configuration
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---

## Overview

EzTax is configured through `config.yml` in the plugin data folder
(`plugins/EzTax/config.yml`). Run `/tax reload` after any change to apply it
without restarting the server.

---

## File layout

```
plugins/EzTax/
├── config.yml          ← main configuration
├── messages.yml        ← message overrides
├── messages/           ← per-locale message files
│   ├── messages_en.yml
│   └── ...
├── gui.yml             ← GUI item configuration
└── stats.yml           ← persisted statistics (YAML storage)
```

---

## Global options

| Key | Type | Default | Description |
|---|---|---|---|
| `debug` | boolean | `false` | Log extra debug output to console |

---

## Group taxes

Override per-rank tax rates when using a Vault permissions plugin.

| Key | Type | Default | Description |
|---|---|---|---|
| `group-taxes.enabled` | boolean | `false` | Enable group-based rate overrides |
| `group-taxes.fallback-rate` | double | `1.0` | Rate for players not matched by any group (`-1` = use global rate) |
| `group-taxes.groups.<name>.transaction-tax` | double | — | Transaction tax rate for this group |
| `group-taxes.groups.<name>.wealth-tax` | double | — | Wealth tax rate for this group |

---

## Tax types

Detailed configuration for each tax type is documented separately:

- [Transaction Tax](type/transaction-tax.md) — applied to transfers and commands
- [Wealth Tax](type/wealth-tax.md) — periodic balance-based tax, supports brackets
- [Inactivity Fee](type/inactivity-fee.md) — charges inactive players
- [Death Fee](type/death-tax.md) — charges on player death

---

## Server treasury

| Key | Type | Default | Description |
|---|---|---|---|
| `server-treasury.enabled` | boolean | `true` | Track cumulative collected amounts in stats |

---

## Tax sink destination

Configure where collected money actually goes after being withdrawn.
See [docs/config/tax-sink.md](config/tax-sink.md) for full documentation.

| Key | Type | Default | Description |
|---|---|---|---|
| `tax-sink.destination` | string | `burn` | `burn` / `player` / `pool` / `command` |
| `tax-sink.target-player` | string | `""` | Player name — used with `destination: player` |
| `tax-sink.command` | string | `""` | Console command with `%amount%` placeholder — used with `destination: command` |

---

## Storage

See [docs/config/storage.md](config/storage.md) for full documentation.

| Key | Type | Default | Description |
|---|---|---|---|
| `storage.type` | string | `yml` | `yml` or `mysql` |
| `storage.file` | string | `stats.yml` | Filename for YAML storage |
| `storage.mysql.host` | string | `localhost` | MySQL host |
| `storage.mysql.port` | int | `3306` | MySQL port |
| `storage.mysql.database` | string | `eztax` | MySQL database name |
| `storage.mysql.user` | string | `root` | MySQL username |
| `storage.mysql.password` | string | `""` | MySQL password |
| `storage.mysql.table` | string | `eztax_stats` | Stats table name |

