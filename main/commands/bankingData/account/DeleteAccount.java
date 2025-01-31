package org.poo.main.commands.bankingData.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.Account;
import org.poo.main.EBank;
import org.poo.main.Transaction;
import org.poo.main.User;
import org.poo.main.commands.Command;
import org.poo.main.exceptions.InvalidUserException;

public final class DeleteAccount extends Command {
    public DeleteAccount() { }

    /**
     * Executes deleteAccount command
     * */
    public ObjectNode execute(final CommandInput input) throws InvalidUserException {
        try {
            Account account = EBank.getInstance().getAccountsMap().get(input.getAccount());
            if (account == null) {
                account = EBank.getInstance().getBusinessAccountsMap().get(input.getAccount());
            }

            String email = input.getEmail();
            User inputOwner = EBank.getInstance().getUsersMap().get(email);
            User accountOwner = account.getOwner();

            if (!accountOwner.equals(inputOwner)) {
                throw new InvalidUserException();
            }

            if (account.getBalance() > 0) {
                // Add transaction
                ObjectMapper mapper = new ObjectMapper();
                Transaction transaction = new Transaction.TransactionBuilder(input.getTimestamp(),
                        "Account couldn't be deleted - there are funds remaining")
                        .build();

                account.getTransactionsSet().add(transaction);
                accountOwner.getTransactionsSet().add(transaction);

                // Return error
                ObjectNode node = mapper.createObjectNode();
                node.put("command", input.getCommand());
                ObjectNode output = mapper.createObjectNode();
                output.put("error",
                        "Account couldn't be deleted - see org.poo.transactions for details");
                output.put("timestamp", input.getTimestamp());
                node.set("output", output);
                node.put("timestamp", input.getTimestamp());
                return node;
            }

            accountOwner.getAccounts().remove(account);

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();
            node.put("command", input.getCommand());

            ObjectNode output = mapper.createObjectNode();
            output.put("success", "Account deleted");
            output.put("timestamp", input.getTimestamp());

            node.set("output", output);
            node.put("timestamp", input.getTimestamp());

            return node;
        } catch (InvalidUserException ignored) { }
        return null;
    }
}
