# Inactivity Fee

Summary
- Charges players after a configurable period of inactivity. Choose between a flat charge or a percentage-based fee.

Quick config (flat)
```yaml
inactivity-fee:
  enabled: true
  days-inactive: 30
  flat-fee: 50.0
  percentage: 0.0
```

Quick config (percentage)
```yaml
inactivity-fee:
  enabled: true
  days-inactive: 60
  flat-fee: 0.0
  percentage: 0.5    # 0.5% of balance when charged
```

Options
- `enabled` (boolean): enable/disable inactivity fee.
- `days-inactive` (int): days of inactivity required before charging.
- `flat-fee` (double): fixed amount charged when inactive.
- `percentage` (double): percent of balance charged as alternative.

Reporting
- Inactivity fees are added to the `INACTIVITY` sink and persisted by storage providers.

Practical tips
- Consider excluding new players or very low balances to avoid discouraging returning users.
- Announce inactivity policies clearly in server rules to avoid confusion.
