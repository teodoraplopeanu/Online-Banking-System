package org.poo.main.cashback;


import org.poo.main.Account;
import org.poo.main.Commerciant;

import static org.poo.utils.Utils.*;

public class SpendingThreshold implements CashbackStrategy {
    /**
     * Gives spendingThreshold cashback as an amount of the current transaction
     * The rate by which the amount is multiplied depends on the type
     * of the account owner's service plan
     *
     * @param commerciant the commerciant to which the current transaction has been executed
     * @param transactionAmount the amount paid in the current transaction
     * @param account the account who made the payment
     * */
    @Override
    public void giveCashback(final Commerciant commerciant, final double transactionAmount,
                             final Account account) {
        double totalAmount = account.getTotalSpendingThreshold();

        double rate500 = 0, rate300 = 0, rate100 = 0;

        switch (account.getOwner().getServicePlan().getType()) {
            case "standard", "student":
                rate500 = 0.0025;
                rate300 = 0.002;
                rate100 = 0.001;
                break;
            case "silver":
                rate500 = 0.005;
                rate300 = 0.004;
                rate100 = 0.003;
                break;
            case "gold":
                rate500 = 0.007;
                rate300 = 0.0055;
                rate100 = 0.005;
                break;
            default:
                break;
        }

        if (totalAmount >= FIVE_HUNDRED) {
            account.setBalance(account.getBalance() + rate500 * transactionAmount);
            return;
        }
        if (totalAmount >= THREE_HUNDRED) {
            account.setBalance(account.getBalance() + rate300 * transactionAmount);
            return;
        }
        if (totalAmount >= ONE_HUNDRED) {
            account.setBalance(account.getBalance() + rate100 * transactionAmount);
        }
    }
}
