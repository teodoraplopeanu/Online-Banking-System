package org.poo.main;

import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommerciantInput;
import org.poo.main.cashback.CashbackFactory;
import org.poo.main.cashback.CashbackStrategy;

@Getter
@Setter
public final class Commerciant {
    private String commerciant;
    private int id;
    private String account;
    private String type;
    private String cashbackName;
    private CashbackStrategy cashbackStrategy;

    public Commerciant(final CommerciantInput input) {
        this.commerciant = input.getCommerciant();
        this.id = input.getId();
        this.account = input.getAccount();
        this.type = input.getType();
        this.cashbackName = input.getCashbackStrategy();
        this.cashbackStrategy = CashbackFactory.getCashbackStrategy(cashbackName);
    }
}
