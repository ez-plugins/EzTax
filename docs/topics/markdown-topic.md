## **EzTax**
##### Modern, configurable tax system for Vault-based Minecraft economies
###### Paper 26.1+ • Java 25+ • Vault required • Optional: [EzEconomy](https://modrinth.com/plugin/ezeconomy), EzSeasons

**Why EzTax?**

* **Transaction Taxes** – Deducts a configurable percentage or flat fee when a player sends money
* **Wealth Tax** – Periodically taxes players above a configurable balance threshold
* **Progressive Tax** – Realistic, tiered tax brackets (like real-world tax systems)
* **Group-Based Taxation** – Apply different tax rates based on player groups or permissions
* **Player Exemptions** – Exempt specific players from taxation
* **Inactivity Fees** – Tax players who have not been online for a set number of days
* **Death Fees** – Charge a percentage of balance on player death (configurable worlds/exemptions)
* **Tax Sinks** – Route collected taxes to a server account or remove them from the economy
* **Tax History** – Paginated, per-player transaction history via `/tax history`
* **Stats & Sinks** – View total tax collected and breakdown by type with `/tax stats` and `/tax sinks`
* **In-Game Config GUI** – Edit tax settings live from an inventory GUI
* **Reloadable Config** – Apply config changes instantly with `/tax reload`
* **EzSeasons Integration** – Logs a season-reset summary of dates and totals collected
* **Full Vault Compatibility** – Works with any Vault-based economy; [EzEconomy](https://modrinth.com/plugin/ezeconomy) recommended
* **MySQL Support** – Store statistics and tax history in MySQL with automatic schema migration



**Requirements**

* Java 25 or newer
* Paper 26.1+ (Minecraft 26.1)
* Vault
* A Vault-compatible economy plugin ([EzEconomy](https://modrinth.com/plugin/ezeconomy) recommended)


**Commands**

* **/tax check** – Preview your next tax amounts and outstanding fines
* **/tax pay** – Pay all outstanding taxes immediately
* **/tax payfine** – Pay all outstanding fines immediately
* **/tax show** – Detailed breakdown of your current taxes
* **/tax fines** – Detailed breakdown of your outstanding fines
* **/tax stats** – View server-wide tax statistics
* **/tax history [player]** – View paginated tax transaction history
* **/tax reload** – Reload config (admin)
* **/tax sinks** – View configured tax sink destinations (admin)
* **/tax runwealthtax** – Manually trigger a wealth tax cycle (admin/testing)
* **/tax runtaxpayment** – Manually trigger a tax payment run (admin/testing)
* **/tax exempt/unexempt/exemptions** – Manage player exemptions (admin)
* **/tax settaxrate** – Set global or group tax rates (admin)
* **/tax transactiontax** – View or manage transaction tax settings (admin)


**Permissions**

* eztax.command.use – Use the /tax command (default: everyone)
* eztax.command.check – Preview next tax amounts (default: everyone)
* eztax.command.paytax – Pay outstanding taxes (default: everyone)
* eztax.command.payfine – Pay outstanding fines (default: everyone)
* eztax.command.showtax – View detailed tax breakdown (default: everyone)
* eztax.command.showfines – View detailed fines breakdown (default: everyone)
* eztax.command.stats – View server-wide stats (default: op)
* eztax.command.history – View own tax history (default: everyone)
* eztax.command.history.others – View any player's tax history (default: op)
* eztax.command.reload – Reload config (default: op)
* eztax.command.sinks – View tax sinks (default: op)
* eztax.command.runwealthtax – Trigger wealth tax run (default: op)
* eztax.command.runtaxpayment – Trigger tax payment run (default: op)
* eztax.command.settaxrate – Set tax rates (default: op)
* eztax.command.transactiontax.view – View transaction tax config (default: op)
* eztax.command.transactiontax.manage – Modify transaction tax config (default: op)
* eztax.command.exempt – Exempt a player from taxes (default: op)
* eztax.command.unexempt – Remove player exemption (default: op)
* eztax.command.exemptions – List exempt players (default: op)
* eztax.command.config – Open admin configuration GUI (default: op)
* eztax.exempt – Permanent exemption from all taxes (default: none)


**Configuration**

* Enable/disable each tax type independently
* Flat or progressive brackets for transaction tax and wealth tax
* Group-based rates and per-player exemptions
* Wealth tax threshold, percentage, and schedule interval
* Inactivity fee: days threshold, flat fee, percentage
* Death fee: percentage, maximum loss, world exemptions
* Tax sink: route to a named player account or remove from economy
* MySQL storage: host, port, database, table prefix
* Debug mode for troubleshooting


**Integration**

* **EzEconomy** – [Modern Vault Economy Plugin](https://modrinth.com/plugin/ezeconomy)
* **EzSeasons** – Season-reset summary logged automatically (soft-depend)
* Works with any Vault provider



**FAQ**

* **Q: Does EzTax require EzEconomy?**

* A: No, but EzEconomy is recommended for best compatibility and features.

* **Q: Can I disable certain taxes?**

* A: Yes, each tax type is independently configurable in config.yml.

* **Q: Where do taxes go?**

* A: Taxes can be routed to a server account or removed from the economy (sink).

* **Q: Does it support MySQL?**

* A: Yes, EzTax can store statistics and tax history in MySQL with automatic schema migration.

* **Q: What changed in 2.0.0?**

* A: Requires Minecraft 26.1 and Java 25. Adds Jaloquent ORM storage, tax history, EzSeasons integration, and an in-game config GUI.



**Related Plugins**

* [**EzEconomy – Modern Vault Economy Plugin**](https://modrinth.com/plugin/ezeconomy)
* [**EzAuction – Buy Orders, Advanced GUI, Shop Price**](https://modrinth.com/plugin/ezauction)
* [**EzShops – Dynamic Shops, Player Shops, Sell Hand/Inv**](https://modrinth.com/plugin/ezshops)


Developed by Shadow48402 | EzPlugins
Discord: [https://discord.gg/yWP95XfmBS](https://discord.gg/yWP95XfmBS)
