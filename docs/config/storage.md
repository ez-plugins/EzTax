---
title: Storage
nav_order: 2
parent: Config Reference
description: "YAML and MySQL storage configuration for EzTax statistics"
---

# Storage
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---

## Overview

EzTax persists aggregated tax statistics (totals, treasury balance, daily/weekly resets) via
[Jaloquent](https://github.com/EzFramework/Jaloquent). Two backends are supported:

| Backend | Best for |
|---|---|
| `yml` | Single-server setups, easy backup, no database required |
| `mysql` | Multi-server/shared-database setups, production environments |

---

## YAML storage _(default)_

Statistics are stored in `plugins/EzTax/stats.yml`.

```yaml
storage:
  type: yml
  file: stats.yml
```

| Key | Type | Default | Description |
|---|---|---|---|
| `type` | string | `yml` | Set to `yml` to use YAML storage |
| `file` | string | `stats.yml` | Filename in the plugin data folder |

No additional setup is required. The file is created automatically on first run.

---

## MySQL storage

```yaml
storage:
  type: mysql
  mysql:
    host: localhost
    port: 3306
    database: eztax
    user: root
    password: ""
    table: eztax_stats
```

| Key | Type | Default | Description |
|---|---|---|---|
| `type` | string | `yml` | Set to `mysql` to use MySQL |
| `mysql.host` | string | `localhost` | MySQL server hostname or IP |
| `mysql.port` | int | `3306` | MySQL port |
| `mysql.database` | string | `eztax` | Target database name |
| `mysql.user` | string | `root` | MySQL username |
| `mysql.password` | string | `""` | MySQL password |
| `mysql.table` | string | `eztax_stats` | Table name for statistics |

EzTax will create the table automatically on first connection if it does not already exist.

If the MySQL connection fails at startup, EzTax automatically falls back to YAML storage and
logs a warning so the server can still start.

---

## Jaloquent

Under the hood, EzTax uses the [Jaloquent](https://github.com/EzFramework/Jaloquent) ORM library
developed by the EzFramework project. Jaloquent provides a clean, Eloquent-style active-record
model layer on top of JDBC or flat-map stores.

- For YAML storage a `BukkitYamlDataStore` adapter is used.
- For MySQL a `DataSourceJdbcStore` from Jaloquent wraps the JDBC `DataSource`.
- Stats are modelled as a single `StatsModel` instance (ID `"stats"`) stored by a
  `JaloquentStatsRepository`.

See the [Developer Guide](../developer-guide.md) for more detail on the storage architecture.

---

## Migration

Switching from YAML to MySQL (or vice-versa) requires a manual one-time import:

1. Note your current stats values from `stats.yml` (or the existing MySQL table).
2. Update `storage.type` in `config.yml`.
3. Start the server — a fresh record will be created.
4. Restore the values via the new backend.

Automated migration tooling is planned for a future release.
