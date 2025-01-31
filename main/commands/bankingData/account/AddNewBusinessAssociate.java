package org.poo.main.commands.bankingData.account;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.BusinessAccount;
import org.poo.main.EBank;
import org.poo.main.User;
import org.poo.main.commands.Command;

public final class AddNewBusinessAssociate extends Command {
    public AddNewBusinessAssociate() { }

    /**
     * Adds a new business associate to the account
     * (with a specific role)
     * */
    @Override
    public ObjectNode execute(final CommandInput input) {
        User user = EBank.getInstance().getUsersMap().get(input.getEmail());
        String role = input.getRole();
        BusinessAccount businessAccount = EBank.getInstance().getBusinessAccountsMap().
                get(input.getAccount());

        // Not to add same person twice
        if (!businessAccount.getPeople().containsKey(user)) {
            businessAccount.getPeople().put(user, role);
            user.getSharedAccounts().add(businessAccount);
            user.getSharedAccountsMap().put(businessAccount.getIban(), businessAccount);
            businessAccount.getSpent().put(user, 0.0);
            businessAccount.getDeposited().put(user, 0.0);
        }

        return null;
    }
}
