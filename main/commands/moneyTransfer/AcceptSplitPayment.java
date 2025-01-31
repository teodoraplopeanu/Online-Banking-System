package org.poo.main.commands.moneyTransfer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.Account;
import org.poo.main.CustomSplitPayment;
import org.poo.main.EBank;
import org.poo.main.User;
import org.poo.main.commands.Command;
import org.poo.main.exceptions.InvalidUserException;

public final class AcceptSplitPayment extends Command {
    public AcceptSplitPayment() { }

    /**
     * Accepts the current split payment
     * */
    @Override
    public ObjectNode execute(final CommandInput input) {
        User user = EBank.getInstance().getUsersMap().get(input.getEmail());
        String splitType = input.getSplitPaymentType();

        try {
            if (user == null) {
                throw new InvalidUserException();
            }
        } catch (InvalidUserException e) {
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

        CustomSplitPayment splitPayment = null;
        Account account = null;

        for (CustomSplitPayment splitPaymentIt : EBank.getInstance().getCurrentSplits()) {
            for (Account accountIt : splitPaymentIt.getAccountsInvolved()) {
                if (accountIt.getOwner().getEmail().equals(user.getEmail())
                    && splitPaymentIt.getSplitPaymentType().equals(splitType)
                    && !splitPaymentIt.getAccountsAccepted().contains(accountIt)) {
                    account = accountIt;
                    splitPayment = splitPaymentIt;
                    break;
                }
            }
        }

        if (splitPayment == null) {
            return null;
        }

        splitPayment.getAccountsAccepted().add(account);

        if (splitPayment.getAccountsAccepted().size()
                == splitPayment.getAccountsInvolved().size()) {
            splitPayment.execute();
            EBank.getInstance().getCurrentSplits().remove(splitPayment);
        }

        return null;
    }
}
