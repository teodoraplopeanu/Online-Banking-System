package org.poo.main.commands.bankingData.card;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;

import org.poo.main.Account;
import org.poo.main.BusinessAccount;
import org.poo.main.Card;
import org.poo.main.EBank;
import org.poo.main.Transaction;
import org.poo.main.commands.Command;
import org.poo.main.exceptions.CardNotFoundException;

public final class CheckCardStatus extends Command {
    public CheckCardStatus() { }

    /**
     * Executes checkCardStatus command
     * */
    public ObjectNode execute(final CommandInput input) {
        try {
            String cardNumber = input.getCardNumber();
            Account account = null;
            BusinessAccount businessAccount = null;
            Card card = null;

            // Search for the card
            for (String ibanIt : EBank.getInstance().getAccountsMap().keySet()) {
                Account accountIt = EBank.getInstance().getAccountsMap().get(ibanIt);
                for (Card cardIt : accountIt.getCards()) {
                    if (cardIt.getCardNumber().equals(cardNumber)) {
                        card = cardIt;
                        account = accountIt;
                        break;
                    }
                }
            }

            for (String ibanIt : EBank.getInstance().getBusinessAccountsMap().keySet()) {
                BusinessAccount accountIt = EBank.getInstance().getBusinessAccountsMap()
                        .get(ibanIt);
                for (Card cardIt : accountIt.getCards()) {
                    if (cardIt.getCardNumber().equals(cardNumber)) {
                        card = cardIt;
                        businessAccount = accountIt;
                        break;
                    }
                }
            }

            if (card == null) {
                throw new CardNotFoundException();
            }

            // Added business account card handling
            if (businessAccount != null) {
                if (businessAccount.getBalance() <= businessAccount.getMinBalance()) {
                    card.setStatus("frozen");
                    Transaction transaction = new Transaction.TransactionBuilder(
                            input.getTimestamp(),
                            "You have reached the minimum amount of funds, the card will be frozen")
                            .build();
                    businessAccount.getTransactionsSet().add(transaction);
                } else {
                    card.setStatus("active");
                }
                return null;
            }

           // Update / Check card status
            if (account.getBalance() <= account.getMinBalance()) {
                card.setStatus("frozen");
                // Add transaction
                Transaction transaction = new Transaction.TransactionBuilder(input.getTimestamp(),
                        "You have reached the minimum amount of funds, the card will be frozen")
                        .build();
                account.getOwner().getTransactionsSet().add(transaction);
                account.getTransactionsSet().add(transaction);
            } else {
                card.setStatus("active");
            }

        } catch (CardNotFoundException e) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();
            node.put("command", input.getCommand());
            ObjectNode output = mapper.createObjectNode();
            output.put("timestamp", input.getTimestamp());
            output.put("description", "Card not found");
            node.set("output", output);
            node.put("timestamp", input.getTimestamp());
            return node;
        }

        return null;
    }
}
