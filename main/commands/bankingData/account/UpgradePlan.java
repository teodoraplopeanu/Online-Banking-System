package org.poo.main.commands.bankingData.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.Account;
import org.poo.main.EBank;
import org.poo.main.Transaction;
import org.poo.main.User;
import org.poo.main.commands.Command;
import org.poo.main.commission.ServicePlanFactory;
import org.poo.main.exceptions.AccountNotFoundException;
import org.poo.main.exceptions.InsufficientFundsException;
import org.poo.main.exceptions.NoDowngradeException;
import org.poo.main.exceptions.SamePlanException;

import java.util.LinkedList;
import java.util.List;

import static org.poo.utils.Utils.*;

public final class UpgradePlan extends Command {
    public UpgradePlan() { }

    /**
     * Upgrades the current service plan of the user to a higher one
     * */
    @Override
    public ObjectNode execute(final CommandInput input) throws Exception {
        List<String> planTypeIerarchy = new LinkedList<>();
        planTypeIerarchy.add("standard");
        planTypeIerarchy.add("student");
        planTypeIerarchy.add("silver");
        planTypeIerarchy.add("gold");

        String newPlanType = input.getNewPlanType();
        Account account = EBank.getInstance().getAccountsMap().get(input.getAccount());
        if (account == null) {
            account = EBank.getInstance().getBusinessAccountsMap().get(input.getAccount());
        }
        try {
            if (account == null) {
                throw new AccountNotFoundException();
            }
        } catch (AccountNotFoundException e) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode result = mapper.createObjectNode();
            result.put("command", input.getCommand());
            ObjectNode output = mapper.createObjectNode();
            output.put("description", "Account not found");
            output.put("timestamp", input.getTimestamp());
            result.set("output", output);
            result.put("timestamp", input.getTimestamp());
            return result;
        }

        User owner = account.getOwner();

        try {
            if (account.getOwner().getServicePlan().getType().equals(newPlanType)) {
                throw new SamePlanException();
            }

            if (planTypeIerarchy.indexOf(owner.getServicePlan().getType())
                    > planTypeIerarchy.indexOf(newPlanType)) {
                throw new NoDowngradeException();
            }

            String currentPlanType = owner.getServicePlan().getType();

            double fee = 0;
            if (currentPlanType.equals("student") || currentPlanType.equals("standard")) {
                if (newPlanType.equals("silver")) {
                    fee = ONE_HUNDRED;
                } else if (newPlanType.equals("gold")) {
                    fee = THREE_HUNDRED_FIFTY;
                }
            } else if (currentPlanType.equals("silver") && newPlanType.equals("gold")) {
                fee = TWO_HUNDRED_FIFTY;
            }

            double exchangeRate = EBank.getInstance().findExchange("RON", account.getCurrency());
            fee = fee * exchangeRate;

            if (account.getBalance() < fee) {
                throw new InsufficientFundsException();
            }

            account.setBalance(account.getBalance() - fee);
            owner.setServicePlan(ServicePlanFactory.chooseServicePlan(newPlanType));

            Transaction transaction = new Transaction.TransactionBuilder(input.getTimestamp(),
                    "Upgrade plan")
                    .setAccountIbanV2(account.getIban())
                    .setNewPlanType(newPlanType)
                    .build();
            account.getTransactionsSet().add(transaction);
            owner.getTransactionsSet().add(transaction);

        } catch (SamePlanException e) {
            Transaction transaction = new Transaction.TransactionBuilder(input.getTimestamp(),
                    "The user already has the " + newPlanType + " plan.").build();
            account.getTransactionsSet().add(transaction);
            owner.getTransactionsSet().add(transaction);
        } catch (NoDowngradeException e) {
            Transaction transaction = new Transaction.TransactionBuilder(input.getTimestamp(),
                    "You cannot downgrade your plan.").build();
            account.getTransactionsSet().add(transaction);
            owner.getTransactionsSet().add(transaction);
        } catch (InsufficientFundsException e) {
            Transaction transaction = new Transaction.TransactionBuilder(input.getTimestamp(),
                    "Insufficient funds").build();
            account.getTransactionsSet().add(transaction);
            owner.getTransactionsSet().add(transaction);
        }

        return null;
    }
}
