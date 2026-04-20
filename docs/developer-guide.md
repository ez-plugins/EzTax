---
title: Developer Guide
nav_order: 6
description: "Architecture overview, Jaloquent integration, and contribution guidelines for EzTax"
---

# Developer Guide
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---

## Architecture overview

EzTax is structured around a bootstrap-component pattern:

```
EzTaxPlugin
└── PluginBootstrap
    ├── ConfigComponent       ← loads TaxConfig from config.yml
    ├── MetricsComponent      ← registers bStats
    ├── StatsComponent        ← wires Jaloquent storage + StatsService
    ├── ExemptionService      ← permission-based exemptions
    ├── EconomyComponent      ← hooks Vault + registers TaxedEconomy wrapper
    ├── TaxEngineComponent    ← builds TaxEngine
    ├── ListenerComponent     ← registers death/command listeners
    ├── SchedulerComponent    ← schedules wealth/inactivity cycles
    └── CommandComponent      ← registers /tax subcommands
```

Each component implements `Component` (`start()`, `stop()`, `reload()`).

---

## Key classes

| Class | Package | Purpose |
|---|---|---|
| `TaxEngine` | `service` | Applies wealth, inactivity, death, and transaction taxes |
| `StatsService` | `service` | In-memory stats with periodic save via repository |
| `TaxConfig` | `config` | Typed wrapper over Bukkit `FileConfiguration` |
| `SinkDestination` | `service` | Enum for tax routing: `BURN`, `PLAYER`, `POOL`, `COMMAND` |
| `VaultHook` | `economy` | Vault economy/permissions bridge |
| `TaxedEconomy` | `economy` | Vault `Economy` proxy that intercepts withdraw/deposit |
| `StatsModel` | `storage` | Jaloquent `Model` representing persisted stats |
| `BukkitYamlDataStore` | `storage` | Jaloquent `DataStore` adapter for Bukkit YAML files |
| `JaloquentStatsRepository` | `repository` | `StatsRepository` backed by `ModelRepository<StatsModel>` |

---

## Jaloquent integration

EzTax uses [Jaloquent](https://github.com/EzFramework/Jaloquent) (`com.github.EzFramework:jaloquent:1.1.0`)
as its persistence layer. Jaloquent is shaded and relocated into `com.skyblockexp.eztax.libs.jaloquent`.

### Data model

`StatsModel extends Model` holds all aggregated statistics as named attributes:

```java
model.setTotalRemoved(1234.56);
model.setSinkTotal(TaxSink.WEALTH, 500.0);
model.getDailyResetDate(); // ISO date string
```

The model is always stored with ID `"stats"` (singleton pattern).

### YAML store

`BukkitYamlDataStore implements DataStore` wraps a Bukkit YAML file. Jaloquent paths are
converted to YAML keys by replacing `/` with `.`:

```
path:    eztax_stats/stats
yaml key: eztax_stats.stats.total_removed
```

### MySQL store

When `storage.type: mysql` is configured:

1. `TableRegistry.register("eztax_stats", tableName, StatsModel.COLUMN_DEFS)` registers the schema.
2. `DataSourceJdbcStore(dataSource)` is passed to `JaloquentStatsRepository`.
3. `ModelRepository` detects the `TableRegistry` entry and routes all operations through SQL.

### Repository wiring

`StatsComponent.start()` selects the appropriate `DataStore` and constructs the repository:

```java
DataStore store = "mysql".equals(type)
    ? new DataSourceJdbcStore(ds)     // with TableRegistry.register(...)
    : new BukkitYamlDataStore(file);
StatsRepository repo = new JaloquentStatsRepository(store, "eztax_stats");
```

---

## Tax sink routing

After a successful `economy.withdrawPlayer()` call, `TaxEngine.distributeTax(amount)` routes
money to the configured `tax-sink.destination`:

| Destination | Behaviour |
|---|---|
| `burn` | Nothing — money is already removed from the economy |
| `player` | `economy.depositPlayer(target, amount)` |
| `pool` | Split `amount / onlinePlayers.size()` and deposit to each |
| `command` | `Bukkit.dispatchCommand(console, cmd.replace("%amount%", ...))` |

---

## Building

```bash
# Compile
mvn compile

# Run tests
mvn test

# Package (shaded JAR)
mvn package -DskipTests
```

The parent POM (`com.skyblockexp:skyblockexp-parent`) must be installed in the local Maven
repository. Clone the `skyblock-experience` monorepo and run `mvn install -N` in its root.

---

## Adding a new tax type

1. Add a value to `TaxSink` enum.
2. Add `COLUMN_DEFS` entries in `StatsModel` for the new sink key.
3. Implement the tax logic in `TaxEngine` and call `withdrawSafely(player, amount, TaxSink.YOUR_SINK)`.
4. Add config keys to `TaxConfig` and `config.yml`.
5. Register the listener or scheduler in the appropriate bootstrap component.

---

## Contribution

- Follow existing code style (no Lombok, minimal dependencies).
- Write JUnit 5 tests for new service logic under `src/test/`.
- Format: 4-space indentation, Allman braces for new classes.
- Open a pull request against the `main` branch with a clear description.
