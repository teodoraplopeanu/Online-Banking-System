package org.poo.main.commands.bankingData.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.BusinessAccount;
import org.poo.main.EBank;
import org.poo.main.User;
import org.poo.main.commands.Command;
import org.poo.main.exceptions.InvalidUserException;
import org.poo.main.exceptions.NotBusinessAccException;

public final class ChangeSpendingLimit extends Command {
    public ChangeSpendingLimit() { }

    /**
     * Changes the spending limit of a business account
     * if action is done by the owner of the account
     * */
    @Override
    public ObjectNode execute(final CommandInput input) throws InvalidUserException {
        BusinessAccount businessAccount = EBank.getInstance().getBusinessAccountsMap().
                get(input.getAccount());
        User user = EBank.getInstance().getUsersMap().get(input.getEmail());

        try {
            // Not business account
            if (businessAccount == null) {
                throw new NotBusinessAccException();
            }

            if (!businessAccount.getOwner().getEmail().equals(user.getEmail())) {
                throw new InvalidUserException();
            }
            double amount = input.getAmount();
            businessAccount.setSpendingLimit(amount);

        } catch (InvalidUserException e) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode result = mapper.createObjectNode();
            result.put("command", input.getCommand());
            ObjectNode output = mapper.createObjectNode();
            output.put("timestamp", input.getTimestamp());
            output.put("description", "You must be owner in order to change spending limit.");
            result.set("output", output);
            result.put("timestamp", input.getTimestamp());
            return result;
        } catch (NotBusinessAccException e) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode result = mapper.createObjectNode();
            result.put("command", input.getCommand());
            ObjectNode output = mapper.createObjectNode();
            output.put("description", "This is not a business account");
            output.put("timestamp", input.getTimestamp());
            result.set("output", output);
            result.put("timestamp", input.getTimestamp());
            return result;
        }

        return null;
    }
}
