package org.poo.main.cashback;

import org.poo.main.Account;
import org.poo.main.Commerciant;

import static org.poo.utils.Utils.*;

public final class NrOfTransactions implements CashbackStrategy {
    /**
     * Update the nrOfTransactions statistics,
     * according to the new count (after the current transaction)
     * In other words, it sets the achievedX parameter to true,
     * which means that, at the next transaction,
     * the account will receive cashback according to the achieved threshold
     * */
    @Override
    public void giveCashback(final Commerciant commerciant, final double amount,
                             final Account account) {
        int count;
        try {
            count = account.getTransactionsCount().get(commerciant.getCommerciant());
        } catch (NullPointerException e) {
            System.out.println(commerciant.getCommerciant() + " not found");
            return;
        }

        if (count >= TWO) {
            account.getNrOfTransactionsStatistics().setAchievedTwo(true);

            if (count >= FIVE) {
                account.getNrOfTransactionsStatistics().setAchievedFive(true);

                if (count >= TEN) {
                    account.getNrOfTransactionsStatistics().setAchievedTen(true);
                }
            }
        }
    }
}
