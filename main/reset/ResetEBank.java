package org.poo.main.reset;

import org.poo.main.EBank;
import org.poo.utils.Utils;

public final class ResetEBank implements Visitor {
    @Override
    public void visit(final EBank eBank) {
        EBank.getInstance().getUsers().clear();
        EBank.getInstance().getUsersMap().clear();
        EBank.getInstance().getCommerciantsMap().clear();
        EBank.getInstance().getAccountsMap().clear();
        EBank.getInstance().getBusinessAccountsMap().clear();
        EBank.getInstance().getExchangeRates().clear();
        EBank.getInstance().resetMatrix();
        Utils.resetRandom();
    }
}
