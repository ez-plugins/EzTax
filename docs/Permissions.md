---
title: Permissions
nav_order: 4
description: "Reference for all EzTax permission nodes"
---

# EzTax Permissions
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---

## Overview

EzTax uses granular permission nodes. Assign them to players and groups using your
permissions plugin (e.g. [LuckPerms](https://luckperms.net)).

---

## Player Permissions

Granted to regular players by default (`default: true`).

| Permission | Description |
|---|---|
| `eztax.command.check` | Use `/tax check` — view own taxes and fines |
| `eztax.command.paytax` | Use `/tax pay` — pay outstanding taxes |
| `eztax.command.payfine` | Use `/tax payfine` — pay outstanding fines |
| `eztax.command.showtax` | Use `/tax show` — detailed tax breakdown |
| `eztax.command.showfines` | Use `/tax fines` — detailed fines breakdown |
| `eztax.command.stats` | Use `/tax stats` — server tax statistics |

---

## Admin Permissions

Restricted to operators (`default: op`).

| Permission | Description |
|---|---|
| `eztax.command.reload` | Use `/tax reload` — reload configuration |
| `eztax.command.sinks` | Use `/tax sinks` — view sink destinations |
| `eztax.command.runtaxpayment` | Use `/tax runtaxpayment` — force a tax run |
| `eztax.command.runwealthtax` | Use `/tax runwealthtax` — force a wealth tax cycle |

---

## Exemption

Players with the `eztax.exempt` permission are excluded from all taxes and fees.

| Permission | Description |
|---|---|
| `eztax.exempt` | Exempt this player from all EzTax taxes and fees |

---

## Tips

- Grant `eztax.*` to admins for full access.
- To exempt a rank from specific taxes only, customise the config group rates to `0` instead of
  using the exemption permission.

