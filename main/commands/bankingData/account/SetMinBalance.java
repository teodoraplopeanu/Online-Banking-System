package org.poo.main.commands.bankingData.account;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.Account;
import org.poo.main.BusinessAccount;
import org.poo.main.EBank;
import org.poo.main.User;
import org.poo.main.commands.Command;
import org.poo.main.exceptions.InvalidUserException;
import org.poo.main.exceptions.NotOwnerException;

public final class SetMinBalance extends Command {
    public SetMinBalance() { }

    /**
     * Executes setMinBalance command
     * */
    public ObjectNode execute(final CommandInput input) throws InvalidUserException,
            NotOwnerException {
        try {
            double amount = input.getAmount();
            User crtUser = EBank.getInstance().getUsersMap().get(input.getEmail());

            if (crtUser == null) {
                throw new InvalidUserException();
            }

            Account account = EBank.getInstance().getAccountsMap().get(input.getAccount());
            BusinessAccount businessAccount = EBank.getInstance().getBusinessAccountsMap().get(
                    input.getAccount());

            if (account != null && !account.getOwner().equals(crtUser)) {
                throw new NotOwnerException();
            }

            if (businessAccount != null) {
                businessAccount.setMinBalance(amount);
                return null;
            }

            account.setMinBalance(amount);

        } catch (InvalidUserException | NotOwnerException ignored) { }

        return null;
    }
}
