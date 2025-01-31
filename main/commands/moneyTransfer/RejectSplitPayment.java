package org.poo.main.commands.moneyTransfer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;

import org.poo.main.CustomSplitPayment;
import org.poo.main.EBank;
import org.poo.main.User;
import org.poo.main.Account;
import org.poo.main.Transaction;
import org.poo.main.commands.Command;
import org.poo.main.exceptions.InvalidUserException;

import java.text.DecimalFormat;

public class RejectSplitPayment extends Command {
    public RejectSplitPayment() { }

    /**
     * Rejects the current split payment
     * */
    public ObjectNode execute(final CommandInput input) {
        User user = EBank.getInstance().getUsersMap().get(input.getEmail());

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
        String splitType = input.getSplitPaymentType();

        for (CustomSplitPayment splitPaymentIt : EBank.getInstance().getCurrentSplits()) {
            for (Account accountIt : splitPaymentIt.getAccountsInvolved()) {
                if (accountIt.getOwner().getEmail().equals(user.getEmail())
                        && splitPaymentIt.getSplitPaymentType().equals(splitType)) {
                    splitPayment = splitPaymentIt;
                    break;
                }
            }
        }

        if (splitPayment == null) {
            return null;
        }

        DecimalFormat numberFormat = new DecimalFormat("#.00");
        Transaction transaction = new Transaction.TransactionBuilder(splitPayment.getTimestamp(),
                "Split payment of " + numberFormat.format(splitPayment.getTotalAmount())
                        + " " + splitPayment.getCurrency())
                .setError("One user rejected the payment.")
                .setInvolvedAccounts(splitPayment.getInvolvedAccountsIbans())
                .setCurrency(splitPayment.getCurrency())
                .setAmountForUsers(splitPayment.getAmountForUsers())
                .setSplitPaymentType(splitType)
                .build();

        for (Account accountIt : splitPayment.getAccountsInvolved()) {
            accountIt.getTransactionsSet().add(transaction);
            accountIt.getOwner().getTransactionsSet().add(transaction);
        }

        EBank.getInstance().getCurrentSplits().remove(splitPayment);

        return null;
    }
}
