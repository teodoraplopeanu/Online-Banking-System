package org.poo.main.commands.bankingData.account;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.Account;
import org.poo.main.EBank;
import org.poo.main.Transaction;
import org.poo.main.User;
import org.poo.main.commands.Command;

public final class AddAccount extends Command {
    public AddAccount() { }

    /**
     * Executes addAccount command
     * */
    public ObjectNode execute(final CommandInput input) {
        User user = EBank.getInstance().getUsersMap().get(input.getEmail());

        if (input.getAccountType().equals("business")) {
            return new AddBusinessAccount().execute(input);
        }

        double interestRate = 0;
        if (input.getAccountType().equals("savings")) {
            interestRate = input.getInterestRate();
        }

        // Create the new account
        Account newAccount = new Account(input.getCurrency(), input.getAccountType(),
                user, interestRate);
        user.getAccounts().add(newAccount);
        user.getAccountsMap().put(newAccount.getIban(), newAccount);
        EBank.getInstance().getAccountsMap().put(newAccount.getIban(), newAccount);

        // Add the transaction
        Transaction transaction = new Transaction.TransactionBuilder(input.getTimestamp(),
                "New account created").build();
        user.getTransactionsSet().add(transaction);
        newAccount.getTransactionsSet().add(transaction);
        return null;
    }
}
