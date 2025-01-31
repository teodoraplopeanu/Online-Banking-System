package org.poo.main.commands.bankingData.account;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.BusinessAccount;
import org.poo.main.EBank;
import org.poo.main.Transaction;
import org.poo.main.User;
import org.poo.main.commands.Command;

public final class AddBusinessAccount extends Command {
    public AddBusinessAccount() { }

    /**
     * Adds a new business account to the user
     * */
    @Override
    public ObjectNode execute(final CommandInput input) {
        User owner = EBank.getInstance().getUsersMap().get(input.getEmail());

        BusinessAccount businessAccount = new BusinessAccount(input.getCurrency(),
                input.getAccountType(), owner, 0);

        owner.getSharedAccounts().add(businessAccount);
        owner.getSharedAccountsMap().put(businessAccount.getIban(), businessAccount);
        EBank.getInstance().getBusinessAccountsMap().put(businessAccount.getIban(),
                businessAccount);

        Transaction transaction = new Transaction.TransactionBuilder(input.getTimestamp(),
                "New account created").build();
        owner.getTransactionsSet().add(transaction);
        businessAccount.getTransactionsSet().add(transaction);

        owner.getAccounts().add(businessAccount);
        return null;
    }
}
