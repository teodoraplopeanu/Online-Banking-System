package org.poo.main.commands.moneyTransfer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.Account;
import org.poo.main.Card;
import org.poo.main.EBank;
import org.poo.main.User;
import org.poo.main.Transaction;
import org.poo.main.commands.Command;
import org.poo.main.exceptions.CardNotFoundException;
import org.poo.main.exceptions.InsufficientFundsException;

import java.text.DecimalFormat;


public final class CashWithdrawal extends Command {
    public CashWithdrawal() { }

    /**
     * Withdraws an amount of cash from a specific account
     * */
    public ObjectNode execute(final CommandInput input) {
        String cardNumber = input.getCardNumber();
        User user = EBank.getInstance().getUsersMap().get(input.getEmail());

        if (user == null) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode result = mapper.createObjectNode();
            result.put("command", input.getCommand());
            ObjectNode output = mapper.createObjectNode();
            output.put("description", "User not found");
            output.put("timestamp", input.getTimestamp());
            result.set("output", output);
            result.put("timestamp", input.getTimestamp());
            return result;
        }

        // Search for the account
        Account account = null;
        for (Account accountIt : user.getAccounts()) {
            for (Card cardIt : accountIt.getCards()) {
                if (cardIt.getCardNumber().equals(cardNumber)) {
                    account = accountIt;
                    break;
                }
            }
        }

        try {

            if (account == null) {
                throw new CardNotFoundException();
            }

            // Compute the amount
            double amount = input.getAmount();
            double exchangeRate = EBank.getInstance().findExchange("RON", account.getCurrency());
            amount = amount * exchangeRate;

            DecimalFormat numberFormat = new DecimalFormat("#.0#");
            Transaction transaction = new Transaction.TransactionBuilder(input.getTimestamp(),
                    "Cash withdrawal of " + numberFormat.format(input.getAmount()))
                    .setAmount(Double.parseDouble(numberFormat.format(input.getAmount())))
                    .build();

            // Check if user can pay amount + commission
            double commission = user.getServicePlan().getCommission(amount, account);

            if (amount + commission > account.getBalance()) {
                throw new InsufficientFundsException();
            }

            // Subtract the money
            account.pay(amount + commission, account.getCurrency());

            account.getTransactionsSet().add(transaction);
            user.getTransactionsSet().add(transaction);

        } catch (InsufficientFundsException e) {
            Transaction transaction = new Transaction.TransactionBuilder(input.getTimestamp(),
                    "Insufficient funds").build();
            user.getTransactionsSet().add(transaction);
            account.getTransactionsSet().add(transaction);

        } catch (CardNotFoundException e) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode result = mapper.createObjectNode();
            result.put("command", input.getCommand());
            ObjectNode output = mapper.createObjectNode();
            output.put("timestamp", input.getTimestamp());
            output.put("description", "Card not found");
            result.set("output", output);
            result.put("timestamp", input.getTimestamp());
            return result;
        }

        return null;
    }
}
