package org.poo.main.commands.bankingData.card;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.Account;
import org.poo.main.Card;
import org.poo.main.EBank;
import org.poo.main.User;
import org.poo.main.Transaction;
import org.poo.main.BusinessAccount;
import org.poo.main.commands.Command;
import org.poo.utils.Utils;

public final class CreateCard extends Command {
    public CreateCard() { }

    /**
     * Executes createCard command
     * */
    public ObjectNode execute(final CommandInput input) {
        User user = EBank.getInstance().getUsersMap().get(input.getEmail());

        if (user == null) {
            return null;
        }

        if (user.getSharedAccountsMap().get(input.getAccount()) != null) {
            return executeBusiness(input);
        }

        Account account = user.getAccountsMap().get(input.getAccount());

        // The user does not owe the desired account
        if (account == null) {
            return null;
        }

        Card newCard = new Card(Utils.generateCardNumber(), "active", user);

        if (input.getCommand().equals("createOneTimeCard")) {
            newCard.setCardType("OneTimeCard");
        } else {
            newCard.setCardType("NormalCard");
        }

        account.getCards().add(newCard);

        // Add transaction
        Transaction transaction = new Transaction.TransactionBuilder(
                input.getTimestamp(), "New card created")
                .setCard(newCard.getCardNumber())
                .setCardHolder(user.getEmail())
                .setAccountIban(account.getIban())
                .build();

        user.getTransactionsSet().add(transaction);
        account.getTransactionsSet().add(transaction);

        return null;
    }

    /**
     * Executes the same command, but for a business account
     * */
    public ObjectNode executeBusiness(final CommandInput input) {
        User user = EBank.getInstance().getUsersMap().get(input.getEmail());
        BusinessAccount account = user.getSharedAccountsMap().get(input.getAccount());
        Card newCard = new Card(Utils.generateCardNumber(), "active", user);
        if (input.getCommand().equals("createOneTimeCard")) {
            newCard.setCardType("OneTimeCard");
        } else {
            newCard.setCardType("NormalCard");
        }

        account.getCards().add(newCard);
        return null;
    }
}
