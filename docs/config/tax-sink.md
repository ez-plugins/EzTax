---
title: Tax Sink Destination
nav_order: 1
parent: Config Reference
description: "Configure where collected tax money is routed"
---

# Tax Sink Destination
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---

## Overview

By default, when EzTax collects a tax it simply removes the money from the player's account and
records the amount in statistics — the money is **burned** (removed from the economy).

The `tax-sink` configuration section lets you route collected money to a different destination
instead. This applies to all tax types: transaction, wealth, inactivity, and death.

---

## Configuration

```yaml
tax-sink:
  destination: burn   # burn | player | pool | command
  target-player: ""   # player name (used when destination=player)
  command: ""         # console command (used when destination=command)
```

---

## Destinations

### `burn` _(default)_

Money is removed from the economy and destroyed. This is the standard anti-inflation
sink — collected amounts are tracked in statistics but no one receives the funds.

```yaml
tax-sink:
  destination: burn
```

---

### `player`

Collected tax is deposited directly into a named player's account after being withdrawn
from the taxpayer. Useful for routing tax revenue to a town leader, admin, or server fund.

```yaml
tax-sink:
  destination: player
  target-player: "ServerAdmin"
```

> **Note:** The `target-player` must be a valid player name. Offline players are supported
> (their balance is updated even when not online).

---

### `pool`

Collected tax is split equally among all **currently online** players and deposited into
their accounts. Useful for redistribution mechanics where the community benefits from taxation.

```yaml
tax-sink:
  destination: pool
```

> **Note:** If no players are online when a tax is collected, the money is effectively burned
> for that event.

---

### `command`

A console command is executed after each tax collection. Use `%amount%` as a placeholder for
the collected amount (formatted as a decimal with two places).

```yaml
tax-sink:
  destination: command
  command: "eco give BankVault %amount%"
```

Example commands:

| Use case | Command |
|---|---|
| Give to a Vault account | `eco give ServerVault %amount%` |
| Broadcast collection | `broadcast Tax collected: %amount%` |
| Run a script | `script tax_collected %amount%` |

---

## Tips

- The `burn` destination is best for controlling inflation by permanently removing money.
- Use `pool` for a fun redistribution mechanic that rewards active players.
- The `command` destination is the most flexible and allows integration with any plugin that
  accepts console commands.
