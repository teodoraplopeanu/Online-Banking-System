package org.poo.main.commands.bankingData.card;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.Account;
import org.poo.main.Card;
import org.poo.main.EBank;
import org.poo.main.User;
import org.poo.main.Transaction;
import org.poo.main.commands.Command;
import org.poo.main.exceptions.InvalidUserException;

import java.util.LinkedList;

public final class DeleteCard extends Command {
    public DeleteCard() { }

    /**
     * Executes deleteCard command
     * */
    public ObjectNode execute(final CommandInput input) throws InvalidUserException {
        String cardNumber = input.getCardNumber();
        User inputOwner = EBank.getInstance().getUsersMap().get(input.getEmail());
        Account accountFound = null;
        Card cardToDelete = null;
        try {
            LinkedList<Account> userAccounts = inputOwner.getAccounts();
            for (Account account : userAccounts) {
                for (Card card : account.getCards()) {
                    if (card.getCardNumber().equals(cardNumber)) {
                        accountFound = account;
                        cardToDelete = card;
                        break;
                    }
                }
            }

            if (cardToDelete == null) {
                throw new InvalidUserException();
            }

            // New condition - don't delete card if account still has balance
            if (accountFound.getBalance() > 0) {
                return null;
            }

            cardToDelete.setStatus("deleted");

            // Add transaction
            Transaction transaction = new Transaction.TransactionBuilder(input.getTimestamp(),
                    "The card has been destroyed")
                    .setCard(cardNumber)
                            .setCardHolder(inputOwner.getEmail())
                                    .setAccountIban(accountFound.getIban()).build();
            inputOwner.getTransactionsSet().add(transaction);

        } catch (InvalidUserException ignored) { }
        return null;
    }
}
