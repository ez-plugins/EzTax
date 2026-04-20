---
title: Transaction Tax
nav_order: 1
parent: Tax Types
description: "Configuration for EzTax transaction tax"
---

# Transaction Tax
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---

## Overview

Transaction tax is a small fee applied whenever money changes hands — either through Vault economy
operations or intercepted chat commands (e.g. `/pay`). It acts as an anti-inflation sink that slows
down wealth accumulation without punishing balances directly.

---

## Configuration

```yaml
transaction-tax:
  enabled: true
  apply-on-withdraw: true   # tax the sender
  apply-on-deposit: false   # tax the recipient
  percentage: 1.0           # % of the transferred amount
  minimum-fee: 1.0          # minimum fee to prevent micro-tax noise
  commands:
    - pay                   # command names to intercept (no leading '/')
  capture-commands: false   # true = intercept commands, false = wrap Vault
```

---

## Options

| Key | Type | Default | Description |
|---|---|---|---|
| `enabled` | boolean | `true` | Enable or disable transaction tax |
| `apply-on-withdraw` | boolean | `true` | Tax the player sending the money |
| `apply-on-deposit` | boolean | `false` | Tax the player receiving the money |
| `percentage` | double | `1.0` | Percentage taken from the transferred amount |
| `minimum-fee` | double | `1.0` | Minimum flat fee (avoids tiny taxes on small transfers) |
| `commands` | list | `[pay]` | Chat command names to intercept when `capture-commands` is `true` |
| `capture-commands` | boolean | `false` | When `true`, intercept listed chat commands instead of wrapping Vault |

---

## Group overrides

When `group-taxes.enabled: true`, the `transaction-tax` rate for a group takes precedence over
the global `percentage`:

```yaml
group-taxes:
  groups:
    vip:
      transaction-tax: 0.5   # VIP players pay only 0.5%
```

---

## Tips

- Set a sensible `minimum-fee` (e.g. `1.0`) to prevent rounding issues on very small transfers.
- If your server uses custom `/pay` variants (e.g. `/pay <player> <amount>`), add each command
  name to the `commands` list.
- Check the `TRANSACTION` sink in `/tax stats` to confirm expected collection amounts.

