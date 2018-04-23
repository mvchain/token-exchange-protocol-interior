package com.mvc.sell.console.util;

import java.math.BigDecimal;

/**
 * @author ethands
 */
public final class Convert {
    private Convert() {
    }

    public static BigDecimal fromWei(String number, Convert.Unit unit) {
        return fromWei(new BigDecimal(number), unit);
    }

    public static BigDecimal fromWei(BigDecimal number, Convert.Unit unit) {
        return number.divide(unit.getWeiFactor());
    }

    public static BigDecimal toWei(String number, Convert.Unit unit) {
        return toWei(new BigDecimal(number), unit);
    }

    public static BigDecimal toWei(BigDecimal number, Convert.Unit unit) {
        return number.multiply(unit.getWeiFactor());
    }

    public static enum Unit {
        WEI("wei", 0),
        WEI1("wei1", 1),
        WEI2("wei2", 2),
        KWEI("kwei", 3),
        KWEI1("kwei1", 4),
        KWEI2("kwei2", 5),
        MWEI("mwei", 6),
        MWEI1("mwei1", 7),
        MWEI2("mwei2", 8),
        GWEI("gwei", 9),
        GWEI1("gwei1", 10),
        GWEI2("gwei2", 11),
        SZABO("szabo", 12),
        SZABO1("szabo1", 13),
        SZABO2("szabo2", 14),
        FINNEY("finney", 15),
        FINNEY1("finney1", 16),
        FINNEY2("finney2", 17),
        ETHER("ether", 18),
        ETHER1("ether1", 19),
        ETHER2("ether2", 20),
        KETHER("kether", 21),
        KETHER1("kether1", 22),
        KETHER2("kether2", 23),
        METHER("mether", 24),
        METHER1("mether1", 25),
        METHER2("mether2", 26),
        GETHER("gether", 27);

        private String name;
        private BigDecimal weiFactor;

        private Unit(String name, int factor) {
            this.name = name;
            this.weiFactor = BigDecimal.TEN.pow(factor);
        }

        public BigDecimal getWeiFactor() {
            return this.weiFactor;
        }

        @Override
        public String toString() {
            return this.name;
        }

        public static Convert.Unit fromString(String name) {
            if (name != null) {
                Convert.Unit[] var1 = values();
                int var2 = var1.length;

                for (int var3 = 0; var3 < var2; ++var3) {
                    Convert.Unit unit = var1[var3];
                    if (name.equalsIgnoreCase(unit.name)) {
                        return unit;
                    }
                }
            }

            return valueOf(name);
        }
    }
}