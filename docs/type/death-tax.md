# Death Fee

Overview
- Death fees charge a percentage of a player's balance on death. Useful for adding risk and a sink tied to PvE/PvP events.

Configuration
- `death-fee.enabled` (boolean)
- `death-fee.percentage` (double) - percentage taken on death
- `death-fee.max-loss` (double) - cap on the maximum amount a player can lose on death
- `death-fee.disabled-worlds` (list) - worlds where the death fee is not applied

Example
```yaml
death-fee:
	enabled: true
	percentage: 1.0    # 1% of balance on death
	max-loss: 1000.0   # never lose more than this amount
	disabled-worlds:
		- hub
		- lobby
# Death Fee

Summary
- Charges a percentage of a player's balance when they die. Use to add risk or as an economic sink tied to deaths.

Quick config
```yaml
death-fee:
  enabled: true
  percentage: 1.0    # 1% of balance on death
  max-loss: 1000.0   # never lose more than this amount
  disabled-worlds:
    - hub
    - lobby
```

Options
- `enabled` (boolean): toggle death fee.
- `percentage` (double): percent of balance taken on death.
- `max-loss` (double): cap the absolute loss to prevent extreme penalties.
- `disabled-worlds` (list): world names where fee is ignored.

Behavior
- Registered listener applies fee on player death events; worlds in `disabled-worlds` are ignored by the listener.
- Amounts are recorded to the `DEATH` sink for stats and persistence.

Tips
- Use `max-loss` to prevent grief from large balance losses.
- Consider disabling in safe hubs or lobby worlds in `disabled-worlds`.
