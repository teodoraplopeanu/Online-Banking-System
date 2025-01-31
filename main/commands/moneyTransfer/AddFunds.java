package org.poo.main.commands.moneyTransfer;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.Account;
import org.poo.main.BusinessAccount;
import org.poo.main.EBank;
import org.poo.main.User;
import org.poo.main.commands.Command;

public final class AddFunds extends Command {
    public AddFunds() { }

    /**
     * Executes addFunds command
     * */
    public ObjectNode execute(final CommandInput input) {
        if (EBank.getInstance().getBusinessAccountsMap().get(input.getAccount()) != null) {
            return executeBusiness(input);
        }

        Account account = EBank.getInstance().getAccountsMap().get(input.getAccount());

        if (account == null) {
            return null;
        }

        account.setBalance(account.getBalance() + input.getAmount());

        return null;
    }

    /**
     * Executes the same command, but for a business account
     * */
    public ObjectNode executeBusiness(final CommandInput input) {
        BusinessAccount businessAccount = EBank.getInstance().getBusinessAccountsMap().
                get(input.getAccount());
        if (businessAccount == null) {
            return null;
        }

        User user = EBank.getInstance().getUsersMap().get(input.getEmail());
        if (businessAccount.getPeople().containsKey(user)
                && businessAccount.getPeople().get(user).equals("employee")
                && businessAccount.getDepositLimit() < input.getAmount()) {
            return null;
        }

        businessAccount.setBalance(businessAccount.getBalance() + input.getAmount());
        if (businessAccount.getPeople().containsKey(user)) {
            businessAccount.getDeposited().put(user, businessAccount.getDeposited().get(user)
                    + input.getAmount());
        }

        return null;
    }
}
