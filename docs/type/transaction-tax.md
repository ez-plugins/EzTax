# Transaction Tax

Summary
- Small fee applied to player transfers and optional economy commands. Use as an anti-inflation sink or gameplay friction.

Quick config
```yaml
transaction-tax:
  enabled: true
  percent: 0.5        # 0.5% per transfer
  minimum-fee: 0.10   # minimum flat fee
  capture-commands: true
  commands:
    - pay
    - withdraw
  apply-on-withdraw: true
  apply-on-deposit: false
```

Options
- `enabled` (boolean): enable/disable transaction tax.
- `percent` (double): percent of the transferred amount taken as tax.
- `minimum-fee` (double): minimum flat fee applied to avoid tiny tax values.
- `capture-commands` (boolean): intercept configured commands instead of wrapping Vault.
- `commands` (list): command names (no leading `/`) to capture when `capture-commands` is true.
- `apply-on-withdraw` / `apply-on-deposit` (boolean): control whether to tax the withdrawer, the recipient, or both.

Commands & permissions
- `/eztax settaxrate <rate> [group]` — set tax rates (permission: `eztax.command.settaxrate`).
- Transaction administration commands are grouped under the main `/eztax` command.

Tips for server owners
- Set a sensible `minimum-fee` to avoid rounding and micro-fee noise on very small transfers.
- If you have custom economy commands, add them to `commands` and test with `capture-commands` enabled.
- Monitor the `TRANSACTION` sink in stats to confirm expected behaviour after changes.

Where it's recorded
- Tax amounts increase the `TRANSACTION` sink and are persisted by storage providers (YAML/MySQL/etc.).
