## **EzTax**
##### Modern, configurable tax system for Vault-based Minecraft economies
###### Bukkit, Spigot, Paper, or Purpur 1.7–1.21.x • Java 8+ • Vault required • Integrates with [EzEconomy](https://modrinth.com/plugin/ezeconomy) and any Vault provider

**Why EzTax?**

* **Transaction Taxes** – Deducts a configurable percentage or flat fee from deposits, withdrawals, and payments
* **Wealth Tax** – Periodically taxes players above a configurable balance threshold
* **Progressive Tax** – Realistic, tiered tax brackets (like real-world tax systems)
* **Group-Based Taxation** – Apply different tax rates based on player groups or permissions
* **Player Exemptions** – Exempt specific players from taxation
* **Inactivity Fees** – Optionally tax players who are inactive for a set number of days
* **Death Fees** – Charge a percentage of balance on player death (configurable worlds/exemptions)
* **Treasury Support** – Route collected taxes to a server account or sink
* **Stats & Sinks** – View total tax collected and breakdown by type with /eztax stats and /eztax sinks
* **Reloadable Config** – Change tax settings and reload instantly with /eztax reload
* **Full Vault Compatibility** – Works with any Vault-based economy, but [EzEconomy](https://modrinth.com/plugin/ezeconomy) is recommended for best results



**Requirements**

* Java 8 or newer
* Bukkit, Spigot, Paper, or Purpur server (Minecraft 1.7–1.21.x)
* Vault
* A Vault-compatible economy plugin ([EzEconomy](https://modrinth.com/plugin/ezeconomy) recommended)


**Commands**

* **/eztax stats** – View total tax collected
* **/eztax sinks** – View breakdown by tax type
* **/eztax reload** – Reload config (admin)
* **/eztax check** – Preview your next tax amount (player)


**Testing & Admin Features**

* **/eztax runwealthtax** – Manually trigger a wealth tax run for all players (admin only, for testing)


**Permissions**

* eztax.stats – View tax stats and sinks
* eztax.command.reload – Reload config and run admin commands
* eztax.command.runwealthtax – Manually trigger wealth tax runs
* eztax.command.runtaxpayment – Run pending tax payments
* eztax.command.settaxrate – Set global or group tax rates
* eztax.command.transactiontax.view – View transaction tax status
* eztax.command.transactiontax.manage – Manage transaction tax settings
* eztax.command.exempt – Exempt a player from taxes
* eztax.command.unexempt – Remove player exemption
* eztax.command.exemptions – List exempt players


**Configuration**

* Enable/disable each tax type
* Set transaction tax percent/minimum
* Configure wealth tax threshold, percent, interval, or progressive brackets
* Set group-based tax rates and player exemptions
* Set inactivity fee days, flat fee, and percent
* Death fee percent, max loss, and world exemptions
* Treasury account for tax collection
* Debug mode for troubleshooting


**Integration**

* **EzEconomy** – [Modern Vault Economy Plugin](https://modrinth.com/plugin/ezeconomy)
* Works with any Vault provider
* PlaceholderAPI support via your economy plugin (e.g. [EzEconomy](https://modrinth.com/plugin/ezeconomy))


**Support**

* Post in the discussion for help or feature requests
* Report bugs via SpigotMC or GitHub


**FAQ**

* **Q: Does EzTax require EzEconomy?**

* A: No, but EzEconomy is recommended for best compatibility and features.

* **Q: Can I disable certain taxes?**

* A: Yes, all tax types are configurable in the config file.

* **Q: Where do taxes go?**

* A: Taxes can be routed to a server account or simply removed from the economy (sink).

* **Q: Does it support PlaceholderAPI?**

* A: Yes, via your Vault economy plugin (EzEconomy provides placeholders).



**Related Plugin**

* [**EzEconomy – Modern Vault Economy Plugin**](https://modrinth.com/plugin/ezeconomy)
* [**EzAuction – Buy Orders, Advanced GUI, Shop Price**](https://modrinth.com/plugin/ezauction)
* [**EzShops – Dynamic Shops, Player Shops, Sell Hand/Inv**](https://modrinth.com/plugin/ezshops)


Developed by Shadow48402 | EzPlugins
Discord: [https://discord.gg/yWP95XfmBS](https://discord.gg/yWP95XfmBS)