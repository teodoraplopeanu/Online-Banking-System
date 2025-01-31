package org.poo.main.cashback;

public final class CashbackFactory {
    private CashbackFactory() { }

    /**
     * Returns the corresponding cashback class,
     * according to the type given as a string
     * */
    public static CashbackStrategy getCashbackStrategy(final String strategy) {
        return switch (strategy) {
            case "nrOfTransactions" -> new NrOfTransactions();
            case "spendingThreshold" -> new SpendingThreshold();
            default -> null;
        };
    }
}
