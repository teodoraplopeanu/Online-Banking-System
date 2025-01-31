package org.poo.main.commands.reports;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.Account;
import org.poo.main.EBank;
import org.poo.main.Transaction;
import org.poo.main.User;
import org.poo.main.commands.Command;

import javax.security.auth.login.AccountNotFoundException;
import java.util.Map;

public final class SpendingReport extends Command {
    public SpendingReport() { }

    /**
     * Executes spendingsReport command
     * */
    public ObjectNode execute(final CommandInput input) throws AccountNotFoundException {
        try {
            Account account = EBank.getInstance().getAccountsMap().get(input.getAccount());
            if (account == null) {
                throw new AccountNotFoundException();
            }
            int startTimestamp = input.getStartTimestamp();
            int endTimestamp = input.getEndTimestamp();
            User owner = account.getOwner();

            // Savings account - not supported
            if (account.getType().equals("savings")) {
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode result = mapper.createObjectNode();
                result.put("command", input.getCommand());
                ObjectNode error = mapper.createObjectNode();
                error.put("error", "This kind of report is not supported for a saving account");
                result.set("output", error);
                result.put("timestamp", input.getTimestamp());
                return result;
            }

            // Collect all transactions in timestamp interval
            ArrayNode arrayNode = owner.getMapper().createArrayNode();
            for (Transaction transaction : account.getTransactionsSet()) {
                int timestamp = transaction.getTimestamp();
                String description = transaction.getDescription();
                if (timestamp >= startTimestamp && timestamp <= endTimestamp
                && description.equals("Card payment")) {
                    arrayNode.add(transaction.toJson());
                }
            }

            // Gather commerciants by timestamp
            ArrayNode commerciants = owner.getMapper().createArrayNode();
            for (Map.Entry<String, Double> entry : account.getCommerciantSpending().entrySet()) {
                if (account.getCommerciantStartTimestamp().get(entry.getKey()) >= startTimestamp
                    && account.getCommerciantStartTimestamp().get(entry.getKey()) <= endTimestamp) {
                    ObjectNode commerciantNode = owner.getMapper().createObjectNode();
                    commerciantNode.put("commerciant", entry.getKey());

                    double commerciantAmount = entry.getValue();

                    commerciantNode.put("total", commerciantAmount);
                    commerciants.add(commerciantNode);
                }
            }

            ObjectNode result = owner.getMapper().createObjectNode();
            result.put("command", input.getCommand());
            ObjectNode output = owner.getMapper().createObjectNode();
            output.put("IBAN", input.getAccount());
            output.put("balance", account.getBalance());
            output.put("currency", account.getCurrency());
            output.set("transactions", arrayNode);
            output.set("commerciants", commerciants);
            result.set("output", output);
            result.put("timestamp", input.getTimestamp());
            return result;
        } catch (AccountNotFoundException e) {
            // Account not found
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode result = mapper.createObjectNode();
            result.put("command", input.getCommand());
            ObjectNode output = mapper.createObjectNode();
            output.put("timestamp", input.getTimestamp());
            output.put("description", "Account not found");
            result.set("output", output);
            result.put("timestamp", input.getTimestamp());
            return result;
        }
        }
}
