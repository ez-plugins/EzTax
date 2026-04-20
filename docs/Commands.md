---
title: Commands
nav_order: 3
description: "Reference for all EzTax commands and subcommands"
---

# EzTax Commands
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---

## Overview

All EzTax features are accessible through the `/tax` root command. Player-facing
subcommands require only basic permissions, while admin subcommands are restricted
to operators by default.

---

## Player Commands

These commands are available to regular players by default.

| Command | Description | Permission |
|---|---|---|
| `/tax check` | Show your current due taxes and outstanding fines | `eztax.command.check` |
| `/tax pay` | Pay all outstanding taxes immediately | `eztax.command.paytax` |
| `/tax payfine` | Pay all outstanding fines immediately | `eztax.command.payfine` |
| `/tax show` | Display a detailed breakdown of your taxes | `eztax.command.showtax` |
| `/tax fines` | Display a detailed breakdown of your fines | `eztax.command.showfines` |
| `/tax stats` | View server-wide tax statistics | `eztax.command.stats` |

---

## Admin Commands

These commands require operator permissions or explicit permission nodes.

| Command | Description | Permission |
|---|---|---|
| `/tax reload` | Reload the plugin configuration without restarting | `eztax.command.reload` |
| `/tax sinks` | Display the configured economic sink destinations | `eztax.command.sinks` |
| `/tax runtaxpayment` | Manually trigger an immediate tax payment run | `eztax.command.runtaxpayment` |
| `/tax runwealthtax` | Manually trigger an immediate wealth tax cycle | `eztax.command.runwealthtax` |

---

## Tips

- Use `/tax help` in-game for a quick command summary.
- After changing `config.yml`, run `/tax reload` instead of restarting the server.
- `/tax runtaxpayment` and `/tax runwealthtax` are useful for testing your configuration before the
  scheduled cycle fires.

