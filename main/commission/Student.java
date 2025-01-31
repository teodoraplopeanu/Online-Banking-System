package org.poo.main.commission;

import lombok.Getter;
import lombok.Setter;
import org.poo.main.Account;

@Getter
@Setter
public final class Student extends CommissionStrategy {
    public Student() {
        this.setType("student");
    }

    /**
     * Computes the commission for the current transaction,
     * based on the service plan type
     * */
    @Override
    public double getCommission(final double amountPayed, final Account account) {
        return 0;
    }
}
