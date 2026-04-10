# Wealth Tax

Overview
- Wealth tax is applied periodically to players based on their balance/wealth. It can be a flat percentage or configured with progressive brackets.

Configuration
- `wealth-tax.enabled` (boolean)
- `wealth-tax.threshold` (double) - minimum wealth before tax applies
- `wealth-tax.percent` (double) - default percentage applied when brackets are not used
- `wealth-tax.interval` (string) - frequency of wealth tax runs (e.g., `DAILY`, `WEEKLY` depending on implementation)
- `wealth-tax.brackets` (list) - optional progressive brackets: entries with `limit` and `percent`
- `wealth-tax.flat-fee` (double) - optional flat fee amount (if configured)

Example (flat)
```yaml
wealth-tax:
	enabled: true
	threshold: 1000.0
	percent: 1.0        # 1% of balance above threshold
	# Wealth Tax

	Summary
	- Periodic tax on player balances. Supports a simple flat percent model or progressive brackets for tiered taxation.

	Quick config (flat)
	```yaml
	wealth-tax:
	  enabled: true
	  threshold: 1000.0
	  percent: 1.0        # 1% of balance above threshold
	  interval: weekly
	  flat-fee: 0.0
	```

	Quick config (progressive)
	```yaml
	wealth-tax:
	  enabled: true
	  interval: weekly
	  brackets:
	    - limit: 10000.0
	      percent: 0.5
	    - limit: 50000.0
	      percent: 1.0
	    - limit: 999999999.0
	      percent: 2.0
	```

	Options
	- `enabled` (boolean): enable periodic wealth tax.
	- `threshold` (double): minimum balance before taxation applies (flat model).
	- `percent` (double): flat percentage applied to taxable balance.
	- `interval` (string): schedule for runs (see plugin scheduling options).
	- `brackets` (list): ordered brackets with `limit` and `percent` for progressive taxation.
	- `flat-fee` (double): optional fixed amount charged instead or in addition.

	Commands
	- `/eztax runwealthtax` — manually trigger the wealth tax run (admin).

	Tips
	- Use brackets for fine-grained control of high-balance players.
	- Test on a staging server to confirm bracket math and thresholds before enabling on live.

	Where it's recorded
	- Totals are recorded to the `WEALTH` sink for reporting and persisted by storage providers.
