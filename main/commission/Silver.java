package org.poo.main.commission;

import lombok.Getter;
import lombok.Setter;
import org.poo.main.Account;
import org.poo.main.EBank;

import static org.poo.utils.Utils.FIVE_HUNDRED;

@Getter
@Setter
public final class Silver extends CommissionStrategy {
    public Silver() {
        this.setType("silver");
    }

    /**
     * Computes the commission for the current transaction,
     * based on the service plan type
     * */
    @Override
    public double getCommission(final double amountPayed, final Account account) {
        double exchangeRate = EBank.getInstance().findExchange(account.getCurrency(), "RON");
        double amountInRon = amountPayed * exchangeRate;

        if (amountInRon <= FIVE_HUNDRED) {
            return 0;
        }

        return 0.001 * amountPayed;
    }
}
