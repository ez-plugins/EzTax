# EzTax

> A professional, highly configurable tax and fine management plugin for Paper 1.21+ servers.

[![Paper](https://img.shields.io/badge/Paper-1.21.1%2B-brightgreen)](https://papermc.io)
[![Java](https://img.shields.io/badge/Java-21%2B-blue)](https://adoptium.net)
[![Vault](https://img.shields.io/badge/Requires-Vault-orange)](https://www.spigotmc.org/resources/vault.34315/)

---

## Features

| Feature | Description |
|---|---|
| Transaction tax | Percentage cut applied to every `/pay` transfer |
| Wealth tax | Periodic bracket-based tax on player balances |
| Inactivity fee | Flat or percentage fee charged to offline players |
| Death fee | Flat or percentage deduction on player death |
| Tax sink | Route collected tax to `burn`, a specific `player`, an online player `pool`, or a custom `command` |
| Jaloquent storage | YAML and MySQL backends through a unified repository layer |
| Multi-language | Bundled translations: EN, DE, ES, FR, JA, NL, RU, ZH |
| bStats | Built-in anonymous metrics |

---

## Requirements

| Dependency | Version |
|---|---|
| [Paper](https://papermc.io) | 1.21.1+ |
| Java | 17+ |
| [Vault](https://www.spigotmc.org/resources/vault.34315/) | Any |
| Economy plugin | Any Vault-compatible plugin |

---

## Installation

1. Download `EzTax.jar` and place it in your server's `plugins/` folder.
2. Install [Vault](https://www.spigotmc.org/resources/vault.34315/) and a compatible economy plugin.
3. Start the server — `plugins/EzTax/config.yml` is generated automatically.
4. Edit `config.yml` to configure tax rates, intervals, and sink destination.
5. Run `/tax reload` to apply changes without a restart.

---

## Quick start

```yaml
# config.yml — minimal example
global-tax-enabled: true

transaction-tax:
  enabled: true
  rate: 5.0          # 5% on every /pay

tax-sink:
  destination: burn  # burn | player | pool | command
```

---

## Documentation

| Page | Description |
|---|---|
| [Getting Started](docs/getting-started.md) | Installation and first-run setup |
| [Commands](docs/Commands.md) | Full command reference |
| [Permissions](docs/Permissions.md) | Permission node reference |
| [Configuration](docs/Configuration.md) | All config keys explained |
| [Tax Sink](docs/config/tax-sink.md) | Sink destination options |
| [Storage](docs/config/storage.md) | YAML and MySQL storage setup |
| [Tax Types](docs/type/transaction-tax.md) | In-depth per-type documentation |
| [Developer Guide](docs/developer-guide.md) | Architecture and contribution notes |

---

## Building from source

```bash
# Install parent POM (first time only)
cd ../skyblock-experience && mvn install -N

# Build shaded JAR
cd ../EzTax && mvn package -DskipTests
# Output: target/EzTax-<version>.jar
```

---

## License

See [LICENSE](LICENSE) for details.
