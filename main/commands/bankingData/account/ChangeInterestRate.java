package org.poo.main.commands.bankingData.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.Account;
import org.poo.main.EBank;
import org.poo.main.Transaction;
import org.poo.main.commands.Command;
import org.poo.main.exceptions.NotSavingsAccountException;

public final class ChangeInterestRate extends Command {
    public ChangeInterestRate() { }

    /**
     * Executes changeInterestRate command
     * */
    public ObjectNode execute(final CommandInput input) throws NotSavingsAccountException {
        try {
            Account account = EBank.getInstance().getAccountsMap().get(input.getAccount());
            if (!account.getType().equals("savings")) {
                throw new NotSavingsAccountException();
            }
            double interestRate = input.getInterestRate();
            account.setInterestRate(interestRate);

            // Add transaction
            Transaction transaction = new Transaction.TransactionBuilder(input.getTimestamp(),
                    "Interest rate of the account changed to "
                    + interestRate).build();
            account.getTransactionsSet().add(transaction);
            account.getOwner().getTransactionsSet().add(transaction);

            return null;
        } catch (NotSavingsAccountException e) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode result = mapper.createObjectNode();
            result.put("command", input.getCommand());
            ObjectNode output = mapper.createObjectNode();
            output.put("timestamp", input.getTimestamp());
            output.put("description", "This is not a savings account");
            result.set("output", output);
            result.put("timestamp", input.getTimestamp());
            return result;
        }
    }
}
