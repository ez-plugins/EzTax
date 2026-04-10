package com.skyblockexp.eztax.feature;

import com.skyblockexp.eztax.test.AbstractEzTaxTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionTaxRoundingTest extends AbstractEzTaxTest {

    @Test
    public void transactionTax_rounds_half_up() {
        // ensure minimum fee does not interfere
        plugin.getConfig().set("transaction-tax.percentage", 100.0);
        plugin.getConfig().set("transaction-tax.minimum-fee", 0.0);
        plugin.saveConfig();
        plugin.reloadEzTax();

        // percent=100% of 1.235 -> 1.235 -> should round HALF_UP to 1.24
        double tax = plugin.getTaxEngine().calculateTransactionTax(1.235);
        assertEquals(1.24, tax, 0.001);
    }

    @Test
    public void transactionTax_handles_precise_decimal_and_rounding() {
        plugin.getConfig().set("transaction-tax.percentage", 1.333);
        plugin.getConfig().set("transaction-tax.minimum-fee", 0.0);
        plugin.saveConfig();
        plugin.reloadEzTax();

        // 123.45 * 1.333% = 1.6454685 -> should round to 1.65
        double tax = plugin.getTaxEngine().calculateTransactionTax(123.45);
        assertEquals(1.65, tax, 0.001);
    }
}
