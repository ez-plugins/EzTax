---
title: Death Fee
nav_order: 4
parent: Tax Types
description: "Configuration for EzTax death fee"
---

# Death Fee
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---

## Overview

The death fee charges a percentage of a player's balance when they die. Use it to add
economic risk to PvE and PvP events, or simply as a sink tied to death events.
`max-loss` caps the absolute amount so one unlucky death never wipes a player's entire
bank account.

---

## Configuration

```yaml
death-fee:
  enabled: true
  percentage: 0.5     # 0.5% of balance on death
  max-loss: 5000.0    # never lose more than 5000 in one death
  disabled-worlds:
    - hub
    - lobby
    - arena
```

---

## Options

| Key | Type | Default | Description |
|---|---|---|---|
| `enabled` | boolean | `false` | Enable or disable the death fee |
| `percentage` | double | `0.5` | Percentage of current balance charged on death |
| `max-loss` | double | `5000` | Hard cap on loss per death (set to `0` to disable cap) |
| `disabled-worlds` | list | `[]` | World names where the death fee is never applied |

---

## Tips

- Always set a `max-loss` to prevent extreme penalties in high-risk areas.
- List safe worlds like `hub` and `lobby` in `disabled-worlds`.
- Players with `eztax.exempt` are immune to the death fee.
- Combine with the `burn` [sink destination](../config/tax-sink.md) to create a true death-as-loss-of-wealth mechanic.

