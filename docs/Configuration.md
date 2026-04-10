# Configuration

EzTax is highly configurable. Below are the main configuration options and their purposes. Edit `config.yml` in the plugin's resources folder to adjust these settings.

## Main Options
- **tax-intervals**: Define how often taxes are collected (e.g., daily, weekly).
- **tax-rates**: Set the percentage or flat amount for each tax type (wealth, income, death fee).
- **fine-settings**: Configure fine amounts, triggers, and payment options.
- **sinks**: List and configure economic sinks (where collected taxes/fines go).
- **language**: Set the default language for messages (see `messages/` folder for available translations).
- **metrics**: Enable or disable plugin metrics/statistics.

## Example `config.yml`
```yaml
tax-intervals:
  wealth: weekly
  income: daily
  death: on-death

tax-rates:
  wealth: 2.5
  income: 1.0
  death: 100

fine-settings:
  late-payment: 10
  max-fines: 5

sinks:
  - server
  - charity

language: en
metrics: true
```

> **Tip:** After editing `config.yml`, use `/tax reload` to apply changes without restarting the server.

## Tax Types

The plugin implements several tax types; detailed documentation for each is available in the `docs/type` folder:

- [docs/type/transaction-tax.md](docs/type/transaction-tax.md) — Transaction tax (applies to transfers and intercepted commands).
- [docs/type/wealth-tax.md](docs/type/wealth-tax.md) — Wealth tax (periodic taxation based on player balance, supports brackets).
- [docs/type/inactivity-fee.md](docs/type/inactivity-fee.md) — Inactivity fee (charges players after configured inactivity period).
- [docs/type/death-tax.md](docs/type/death-tax.md) — Death fee (charges on player death, with world exclusions and caps).

Refer to those pages for configuration examples and behaviour notes for each tax type.
