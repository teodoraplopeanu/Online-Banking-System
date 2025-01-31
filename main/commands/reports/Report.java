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
import org.poo.main.exceptions.AccountNotFoundException;

public final class Report extends Command {
    public Report() { }

    /**
     * Executes report command
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

            // Gather transactions
            ArrayNode arrayNode = owner.getMapper().createArrayNode();
            for (Transaction transaction : account.getTransactionsSet()) {
                int timestamp = transaction.getTimestamp();
                if (timestamp >= startTimestamp && timestamp <= endTimestamp) {
                    arrayNode.add(transaction.toJson());
                }
            }

            ObjectNode result = owner.getMapper().createObjectNode();
            result.put("command", input.getCommand());
            ObjectNode output = owner.getMapper().createObjectNode();
            output.put("IBAN", input.getAccount());
            output.put("balance", account.getBalance());
            output.put("currency", account.getCurrency());
            output.set("transactions", arrayNode);
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
