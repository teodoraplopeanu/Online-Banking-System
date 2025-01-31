package org.poo.main.cashback;

import org.poo.main.Account;
import org.poo.main.Commerciant;

public interface CashbackStrategy {
    /**
     * Executes the corresponding actions
     * to the type of cashback one commerciant has
     * */
    void giveCashback(Commerciant commerciant, double amount, Account account);
}
