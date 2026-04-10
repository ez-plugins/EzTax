package com.skyblockexp.eztax.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaxConfig {
    private final JavaPlugin plugin;

    private boolean debug;
    
    // Group-based taxes
    private boolean groupTaxesEnabled;
    private double groupTaxesFallbackRate;
    private Map<String, GroupTaxRates> groupTaxRates;

    private boolean transactionTaxEnabled;
    private boolean transactionTaxApplyOnDeposit;
    private boolean transactionTaxApplyOnWithdraw;
    private double transactionTaxPercent;
    private double transactionTaxMinimumFee;
    private java.util.List<String> transactionChatCommands;
    private boolean transactionTaxCaptureCommands;

    private boolean wealthTaxEnabled;
    private double wealthTaxThreshold;
    private double wealthTaxPercent;
    private TaxInterval wealthTaxInterval;
    private java.util.List<WealthBracket> wealthTaxBrackets;

    private boolean inactivityFeeEnabled;
    private int inactivityDays;
    private double inactivityFlatFee;
    private double inactivityPercentage;

    private boolean deathFeeEnabled;
    private double deathFeePercent;
    private double deathFeeMaxLoss;
    private List<String> deathFeeDisabledWorlds;

    private boolean treasuryEnabled;

    // Storage configuration
    private String storageType; // yml | mysql
    private String storageFile; // filename for YML provider

    // MySQL specific
    private String mysqlHost;
    private int mysqlPort;
    private String mysqlDatabase;
    private String mysqlUser;
    private String mysqlPassword;
    private String mysqlTable;

    public TaxConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        FileConfiguration config = plugin.getConfig();
        this.debug = config.getBoolean("debug", false);
        
        // Load group-based taxes
        this.groupTaxesEnabled = config.getBoolean("group-taxes.enabled", false);
        // Use a sentinel default so callers can fall back to global tax percents when not set
        this.groupTaxesFallbackRate = config.getDouble("group-taxes.fallback-rate", -1.0);
        this.groupTaxRates = new HashMap<>();
        
        ConfigurationSection groupsSection = config.getConfigurationSection("group-taxes.groups");
        if (groupsSection != null) {
            for (String groupName : groupsSection.getKeys(false)) {
                double transactionTax = groupsSection.getDouble(groupName + ".transaction-tax", -1.0);
                double wealthTax = groupsSection.getDouble(groupName + ".wealth-tax", -1.0);
                groupTaxRates.put(groupName.toLowerCase(), new GroupTaxRates(transactionTax, wealthTax));
            }
        }

        this.transactionTaxEnabled = config.getBoolean("transaction-tax.enabled", true);
        this.transactionTaxApplyOnDeposit = config.getBoolean("transaction-tax.apply-on-deposit", false);
        this.transactionTaxApplyOnWithdraw = config.getBoolean("transaction-tax.apply-on-withdraw", true);
        this.transactionTaxPercent = config.getDouble("transaction-tax.percentage", 0.0D);
        this.transactionTaxMinimumFee = config.getDouble("transaction-tax.minimum-fee", 0.0D);
        this.transactionChatCommands = config.getStringList("transaction-tax.commands");
        if (this.transactionChatCommands == null || this.transactionChatCommands.isEmpty()) {
            this.transactionChatCommands = java.util.List.of("pay");
        }
        this.transactionTaxCaptureCommands = config.getBoolean("transaction-tax.capture-commands", false);

        this.wealthTaxEnabled = config.getBoolean("wealth-tax.enabled", false);
        this.wealthTaxThreshold = config.getDouble("wealth-tax.threshold", 0.0D);
        this.wealthTaxPercent = config.getDouble("wealth-tax.percentage", 0.0D);
        this.wealthTaxInterval = TaxInterval.fromString(config.getString("wealth-tax.interval", "DAILY"));
        this.wealthTaxBrackets = new java.util.ArrayList<>();
        ConfigurationSection bracketsSection = config.getConfigurationSection("wealth-tax.brackets");
        if (bracketsSection != null) {
            for (String key : bracketsSection.getKeys(false)) {
                ConfigurationSection b = bracketsSection.getConfigurationSection(key);
                if (b == null) continue;
                double limit = b.getDouble("limit", -1.0);
                double percent = b.getDouble("percent", -1.0);
                if (limit >= 0 && percent >= 0) {
                    wealthTaxBrackets.add(new WealthBracket(limit, percent));
                }
            }
            // ensure sorted by limit asc
            wealthTaxBrackets.sort((x, y) -> Double.compare(x.limit, y.limit));
        }

        this.inactivityFeeEnabled = config.getBoolean("inactivity-fee.enabled", false);
        this.inactivityDays = config.getInt("inactivity-fee.days-inactive", 30);
        this.inactivityFlatFee = config.getDouble("inactivity-fee.flat-fee", 0.0D);
        this.inactivityPercentage = config.getDouble("inactivity-fee.percentage", 0.0D);

        this.deathFeeEnabled = config.getBoolean("death-fee.enabled", false);
        this.deathFeePercent = config.getDouble("death-fee.percentage", 0.0D);
        this.deathFeeMaxLoss = config.getDouble("death-fee.max-loss", 0.0D);
        this.deathFeeDisabledWorlds = config.getStringList("death-fee.disabled-worlds");
        if (this.deathFeeDisabledWorlds == null) {
            this.deathFeeDisabledWorlds = Collections.emptyList();
        }

        this.treasuryEnabled = config.getBoolean("server-treasury.enabled", true);

        // Storage configuration
        this.storageType = config.getString("storage.type", "yml").toLowerCase();
        this.storageFile = config.getString("storage.file", "stats.yml");
        this.mysqlHost = config.getString("storage.mysql.host", "localhost");
        this.mysqlPort = config.getInt("storage.mysql.port", 3306);
        this.mysqlDatabase = config.getString("storage.mysql.database", "eztax");
        this.mysqlUser = config.getString("storage.mysql.user", "root");
        this.mysqlPassword = config.getString("storage.mysql.password", "");
        this.mysqlTable = config.getString("storage.mysql.table", "eztax_stats");
    }
    
    public JavaPlugin getPlugin() {
        return plugin;
    }

    public boolean isDebug() {
        return debug;
    }
    
    public boolean isGroupTaxesEnabled() {
        return groupTaxesEnabled;
    }
    
    public double getGroupTaxesFallbackRate() {
        return groupTaxesFallbackRate;
    }
    
    public GroupTaxRates getGroupTaxRates(String group) {
        if (group == null) {
            return null;
        }
        return groupTaxRates.get(group.toLowerCase());
    }
    
    public double getTransactionTaxPercentForGroup(String group) {
        if (!groupTaxesEnabled || group == null) {
            return transactionTaxPercent;
        }
        GroupTaxRates rates = getGroupTaxRates(group);
        if (rates != null && rates.transactionTax >= 0) {
            return rates.transactionTax;
        }
        // If a group fallback is explicitly configured and non-negative, use it; otherwise use the global transaction percent
        return groupTaxesFallbackRate >= 0 ? groupTaxesFallbackRate : transactionTaxPercent;
    }
    
    public double getWealthTaxPercentForGroup(String group) {
        if (!groupTaxesEnabled || group == null) {
            return wealthTaxPercent;
        }
        GroupTaxRates rates = getGroupTaxRates(group);
        if (rates != null && rates.wealthTax >= 0) {
            return rates.wealthTax;
        }
        return groupTaxesFallbackRate >= 0 ? groupTaxesFallbackRate : wealthTaxPercent;
    }

    public boolean isTransactionTaxEnabled() {
        return transactionTaxEnabled;
    }

    public boolean isTransactionTaxApplyOnDeposit() {
        return transactionTaxApplyOnDeposit;
    }

    public boolean isTransactionTaxApplyOnWithdraw() {
        return transactionTaxApplyOnWithdraw;
    }

    public double getTransactionTaxPercent() {
        return transactionTaxPercent;
    }

    public double getTransactionTaxMinimumFee() {
        return transactionTaxMinimumFee;
    }

    /**
     * Commands to intercept from chat for applying transaction tax. These are command names without leading '/'.
     */
    public java.util.List<String> getTransactionChatCommands() {
        return new java.util.ArrayList<>(transactionChatCommands);
    }

    /**
     * If true, EzTax will intercept commands (chat) listed in `transaction-tax.commands`
     * and apply transaction tax there instead of registering the taxed Vault economy wrapper.
     */
    public boolean isTransactionTaxCaptureCommands() {
        return transactionTaxCaptureCommands;
    }

    public boolean isWealthTaxEnabled() {
        return wealthTaxEnabled;
    }

    public double getWealthTaxThreshold() {
        return wealthTaxThreshold;
    }

    public double getWealthTaxPercent() {
        return wealthTaxPercent;
    }

    public TaxInterval getWealthTaxInterval() {
        return wealthTaxInterval;
    }

    /**
     * Returns wealth tax brackets. Each bracket defines an upper limit and the percent
     * applied to amounts within that bracket. If empty, flat percent is used.
     */
    public java.util.List<Object> getWealthTaxBrackets() {
        return new java.util.ArrayList<>(wealthTaxBrackets);
    }

    public boolean isInactivityFeeEnabled() {
        return inactivityFeeEnabled;
    }

    public int getInactivityDays() {
        return inactivityDays;
    }

    public double getInactivityFlatFee() {
        return inactivityFlatFee;
    }

    public double getInactivityPercentage() {
        return inactivityPercentage;
    }

    public boolean isDeathFeeEnabled() {
        return deathFeeEnabled;
    }

    public double getDeathFeePercent() {
        return deathFeePercent;
    }

    public double getDeathFeeMaxLoss() {
        return deathFeeMaxLoss;
    }

    public List<String> getDeathFeeDisabledWorlds() {
        return deathFeeDisabledWorlds;
    }

    public boolean isTreasuryEnabled() {
        return treasuryEnabled;
    }

    // --- storage getters ---
    public String getStorageType() {
        return storageType == null ? "yml" : storageType;
    }

    public String getStorageFile() {
        return storageFile == null || storageFile.isEmpty() ? "stats.yml" : storageFile;
    }

    public String getMysqlHost() { return mysqlHost; }
    public int getMysqlPort() { return mysqlPort; }
    public String getMysqlDatabase() { return mysqlDatabase; }
    public String getMysqlUser() { return mysqlUser; }
    public String getMysqlPassword() { return mysqlPassword; }
    public String getMysqlTable() { return mysqlTable; }

    public static class GroupTaxRates {
        private final double transactionTax;
        private final double wealthTax;
        
        public GroupTaxRates(double transactionTax, double wealthTax) {
            this.transactionTax = transactionTax;
            this.wealthTax = wealthTax;
        }
        
        public double getTransactionTax() {
            return transactionTax;
        }
        
        public double getWealthTax() {
            return wealthTax;
        }
    }
    public static class WealthBracket {
        private final double limit;
        private final double percent;

        public WealthBracket(double limit, double percent) {
            this.limit = limit;
            this.percent = percent;
        }

        public double getLimit() {
            return limit;
        }

        public double getPercent() {
            return percent;
        }
    }
}
