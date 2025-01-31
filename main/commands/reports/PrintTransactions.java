package org.poo.main.commands.reports;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.EBank;
import org.poo.main.Transaction;
import org.poo.main.User;
import org.poo.main.commands.Command;

import java.util.Iterator;
import java.util.List;

public final class PrintTransactions extends Command {
    public PrintTransactions() { }

    /**
     * Executes printTransactions command
     * */
    public ObjectNode execute(final CommandInput input) {
        User user = EBank.getInstance().getUsersMap().get(input.getEmail());
        ObjectNode node = user.getMapper().createObjectNode();

        // Get only transactions made until the time of printing
        ArrayNode newArrayNode = user.getMapper().createArrayNode();
        List<Transaction> transactions = user.getTransactionsSet();
        transactions.sort((o1, o2) -> {
            return  Integer.compare(o1.getTimestamp(),
                o2.getTimestamp());
        });
        Iterator<Transaction> it = user.getTransactionsSet().iterator();
        while (it.hasNext()) {
            ObjectNode transaction = it.next().toJson();
            newArrayNode.add(transaction);
        }

        node.put("command", input.getCommand());
        node.set("output", newArrayNode);
        node.put("timestamp", input.getTimestamp());

        return node;
    }
}
