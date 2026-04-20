---
title: Inactivity Fee
nav_order: 3
parent: Tax Types
description: "Configuration for EzTax inactivity fee"
---

# Inactivity Fee
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---

## Overview

The inactivity fee charges players who have not logged in for a configurable number of days.
Choose a flat fixed amount, a percentage of the player's balance, or combine both.

---

## Configuration

### Flat fee

```yaml
inactivity-fee:
  enabled: true
  days-inactive: 30
  flat-fee: 500.0
  percentage: 0.0
```

### Percentage-based

```yaml
inactivity-fee:
  enabled: true
  days-inactive: 60
  flat-fee: 0.0
  percentage: 0.5    # 0.5% of balance charged each run
```

---

## Options

| Key | Type | Default | Description |
|---|---|---|---|
| `enabled` | boolean | `false` | Enable or disable the inactivity fee |
| `days-inactive` | int | `30` | Days of inactivity before the fee is applied |
| `flat-fee` | double | `500.0` | Fixed amount charged when inactive (used when `percentage` is `0`) |
| `percentage` | double | `0.0` | Percentage of balance charged (takes precedence over `flat-fee` when `> 0`) |

---

## Tips

- Use `flat-fee` for simplicity; use `percentage` to scale with the player's wealth.
- Set `days-inactive` generously (e.g. 60–90 days) to avoid discouraging returning players.
- Announce your inactivity policy in server rules to avoid confusion.
- Players with `eztax.exempt` are never charged.

