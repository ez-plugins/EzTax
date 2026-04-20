---
title: Wealth Tax
nav_order: 2
parent: Tax Types
description: "Configuration for EzTax wealth tax"
---

# Wealth Tax
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---

## Overview

Wealth tax is applied periodically to all online players whose balance exceeds a configured
threshold. Supports both a simple flat-percentage model and progressive tax brackets for
tiered taxation.

The scheduled run frequency is controlled by `interval` (`DAILY` or `WEEKLY`).

---

## Configuration

### Flat rate

```yaml
wealth-tax:
  enabled: true
  threshold: 250000   # minimum balance before tax applies
  percentage: 2.0     # 2% of taxable balance
  interval: DAILY
```

### Progressive brackets

```yaml
wealth-tax:
  enabled: true
  threshold: 250000
  percentage: 3.0     # applied to amounts above the highest bracket
  interval: DAILY
  brackets:
    low:
      limit: 500000   # applies to taxable amounts up to 500 000
      percent: 1.0
    mid:
      limit: 1000000  # applies to taxable amounts 500 000–1 000 000
      percent: 2.0
    # amounts above 1 000 000 are taxed at the global percentage (3.0%)
```

---

## Options

| Key | Type | Default | Description |
|---|---|---|---|
| `enabled` | boolean | `false` | Enable periodic wealth tax |
| `threshold` | double | `250000` | Minimum balance before the tax kicks in |
| `percentage` | double | `2.0` | Flat percentage applied to taxable balance (amount above threshold) |
| `interval` | string | `DAILY` | Schedule: `DAILY` or `WEEKLY` |
| `brackets.<name>.limit` | double | — | Upper bound for this bracket |
| `brackets.<name>.percent` | double | — | Rate applied to amounts within this bracket |

---

## Group overrides

```yaml
group-taxes:
  groups:
    vip:
      wealth-tax: 1.0   # VIP players pay only 1%
```

---

## Tips

- Brackets are sorted by `limit` ascending at startup; order in the YAML does not matter.
- To test bracket math without waiting for the schedule, use `/tax runwealthtax`.
- Set `threshold` high enough that new players are never charged on their starter balance.

