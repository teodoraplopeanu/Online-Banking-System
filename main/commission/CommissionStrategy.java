package org.poo.main.commission;

import lombok.Getter;
import lombok.Setter;
import org.poo.main.Account;

@Getter
@Setter
public abstract class CommissionStrategy {
    private String type;

    /**
     * Computes the commission for the current transaction,
     * based on the service plan type
     * */
    public abstract double getCommission(double amountPayed, Account account);
}
