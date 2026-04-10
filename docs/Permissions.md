# Permissions

EzTax uses a set of permission nodes to control access to its features. Assign these permissions to players or groups using your permissions plugin (e.g., LuckPerms).

| Permission Node                | Description                                 | Default |
|-------------------------------|---------------------------------------------|---------|
| eztax.command.check           | Use /tax check and view own taxes/fines     | true    |
| eztax.command.paytax          | Pay your taxes using /tax pay               | true    |
| eztax.command.payfine         | Pay your fines using /tax payfine           | true    |
| eztax.command.showtax         | View tax details using /tax show            | true    |
| eztax.command.showfines       | View fines using /tax fines                 | true    |
| eztax.command.stats           | View tax statistics using /tax stats        | true    |
| eztax.command.reload          | Reload plugin configuration                 | op      |
| eztax.command.sinks           | View economic sinks                         | op      |
| eztax.command.runtaxpayment   | Force tax payment run                       | op      |
| eztax.command.runwealthtax    | Force wealth tax run                        | op      |

> **Note:** Some commands may require multiple permissions for full access.
