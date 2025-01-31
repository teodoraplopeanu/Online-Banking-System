package org.poo.main.commands.moneyTransfer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.*;
import org.poo.main.commands.Command;
import org.poo.main.exceptions.AccountNotFoundException;
import org.poo.main.exceptions.InsufficientFundsException;
import org.poo.main.exceptions.InvalidUserException;
import org.poo.main.statistics.NrOfTransactionsStatistics;

import java.util.Map;

import static org.poo.utils.Utils.*;


public final class SendMoney extends Command {
    public SendMoney() { }

    /**
     * Executes sendMoney command
     * */
    public ObjectNode execute(final CommandInput input) {
        try {
            String ibanSender = input.getAccount();
            String ibanReceiver = input.getReceiver();

            // ibanSender = alias -> invalid
            if (!ibanSender.startsWith("RO")) {
                return null;
            }
            Account senderAccount = EBank.getInstance().getAccountsMap().get(ibanSender);
            if (senderAccount == null) {
                senderAccount = EBank.getInstance().getBusinessAccountsMap().get(ibanSender);
            }

            // ibanReceiver = alias
            if (!ibanReceiver.startsWith("RO")) {
                ibanReceiver = senderAccount.getOwner().getAliases().get(ibanReceiver);
            }

            Account receiverAccount = EBank.getInstance().getAccountsMap().get(ibanReceiver);
            if (receiverAccount == null) {
                receiverAccount = EBank.getInstance().getBusinessAccountsMap().get(ibanReceiver);

                if (receiverAccount == null) {
                    // Send money to commerciant
                    if (EBank.getInstance().getCommerciantAccounts().contains(ibanReceiver)) {
                        Commerciant commerciant = null;
                        for (Map.Entry<String, Commerciant> entry
                                : EBank.getInstance().getCommerciantsMap().entrySet()) {
                            Commerciant c = entry.getValue();
                            if (c.getAccount().equals(ibanReceiver)) {
                                commerciant = c;
                                break;
                            }
                        }

                        double amount = input.getAmount();
                        Transaction transactionSender = new Transaction.TransactionBuilder(
                                input.getTimestamp(), input.getDescription())
                                .setSenderIban(ibanSender)
                                .setReceiverIban(ibanReceiver)
                                .setAmountString(input.getAmount() + " "
                                        + senderAccount.getCurrency())
                                .setTransferType("sent")
                                .build();
                        double commission = senderAccount.getOwner().getServicePlan().getCommission(
                                amount, senderAccount);
                        if (amount + commission > senderAccount.getBalance()) {
                            throw new InsufficientFundsException();
                        }

                        // Send the money
                        senderAccount.setBalance(senderAccount.getBalance() - amount - commission);

                        if (commerciant.getCashbackName().equals("spendingThreshold")) {
                            double exchangeRate2 = EBank.getInstance().
                                    findExchange(senderAccount.getCurrency(), "RON");
                            double amountForCashback = amount * exchangeRate2;
                            senderAccount.setTotalSpendingThreshold(senderAccount.
                                    getTotalSpendingThreshold() + amountForCashback);
                        } else {
                            if (senderAccount.getTransactionsCount().containsKey(
                                    input.getCommerciant())) {
                                senderAccount.getTransactionsCount().put(input.getCommerciant(),
                                        senderAccount.getTransactionsCount().get(
                                                input.getCommerciant()) + 1);
                            } else {
                                senderAccount.getTransactionsCount().put(
                                        input.getCommerciant(), 1);
                            }
                        }

                        // Give nr of transactions cashback
                        // if previously one threshold has been achieved
                        NrOfTransactionsStatistics statistics = senderAccount.
                                getNrOfTransactionsStatistics();
                        if (statistics.isAchievedTwo()) {
                            if (!statistics.isReceivedTwo() && commerciant.getType().
                                    equals("Food")) {
                                // Cashback
                                senderAccount.setBalance(senderAccount.getBalance()
                                        + PERCENTAGE_FOOD * amount);
                                statistics.setReceivedTwo(true);
                            }
                        }

                        if (statistics.isAchievedFive()) {
                            if (!statistics.isReceivedFive() && commerciant.getType()
                                    .equals("Clothes")) {
                                senderAccount.setBalance(senderAccount.getBalance()
                                        + PERCENTAGE_CLOTHES * amount);
                                statistics.setReceivedFive(true);
                            }
                        }

                        if (statistics.isAchievedTen()) {
                            if (!statistics.isReceivedTen() && commerciant.getType()
                                    .equals("Tech")) {
                                senderAccount.setBalance(senderAccount.getBalance()
                                        + PERCENTAGE_TECH * amount);
                                statistics.setReceivedTen(true);
                            }
                        }

                        // Give cashback
                        commerciant.getCashbackStrategy().giveCashback(commerciant,
                                amount, senderAccount);

                        // Add transaction
                        senderAccount.getTransactionsSet().add(transactionSender);
                        senderAccount.getOwner().getTransactionsSet().add(transactionSender);

                        return null;
                    }
                }
            }

            if (senderAccount == null || receiverAccount == null) {
                throw new AccountNotFoundException();
            }

            if (!senderAccount.getOwner().equals(EBank.getInstance().getUsersMap().
                    get(input.getEmail()))) {
                throw new InvalidUserException();
            }

            // Compute amount
            double amount = input.getAmount();
            Transaction transactionSender = new Transaction.TransactionBuilder(
                    input.getTimestamp(), input.getDescription())
                    .setSenderIban(ibanSender)
                    .setReceiverIban(ibanReceiver)
                    .setAmountString(input.getAmount() + " " + senderAccount.getCurrency())
                    .setTransferType("sent")
                    .build();
            double commission = senderAccount.getOwner().getServicePlan().getCommission(
                    amount, senderAccount);
            if (amount + commission > senderAccount.getBalance()) {
                throw new InsufficientFundsException();
            }

            // Send the money
            senderAccount.setBalance(senderAccount.getBalance() - amount - commission);

            double exchangeRate = EBank.getInstance().findExchange(senderAccount.getCurrency(),
                    receiverAccount.getCurrency());
            amount = amount * exchangeRate;
            receiverAccount.setBalance(receiverAccount.getBalance() + amount);

            User senderUser = senderAccount.getOwner();
            User receiverUser = receiverAccount.getOwner();

            // Add transaction to sender
            senderUser.getTransactionsSet().add(transactionSender);
            senderAccount.getTransactionsSet().add(transactionSender);

            // Add transaction to receiver
            Transaction transactionReceiver = new Transaction.TransactionBuilder(
                    input.getTimestamp(), input.getDescription())
                    .setSenderIban(ibanSender)
                    .setReceiverIban(ibanReceiver)
                    .setAmountString(amount + " " + receiverAccount.getCurrency())
                    .setTransferType("received")
                    .build();
            receiverUser.getTransactionsSet().add(transactionReceiver);
            receiverAccount.getTransactionsSet().add(transactionReceiver);

        } catch (InsufficientFundsException e) {
            // Insufficient funds
            Account senderAccount = EBank.getInstance().getAccountsMap().get(input.getAccount());
            if (senderAccount == null) {
                senderAccount = EBank.getInstance().getBusinessAccountsMap().get(
                        input.getAccount());
            }
            User sender = senderAccount.getOwner();
            Transaction transaction = new Transaction.TransactionBuilder(input.getTimestamp(),
                    "Insufficient funds").build();
            sender.getTransactionsSet().add(transaction);
            senderAccount.getTransactionsSet().add(transaction);
        } catch (AccountNotFoundException e) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode result = mapper.createObjectNode();
            result.put("command", input.getCommand());
            ObjectNode output = mapper.createObjectNode();
            output.put("timestamp", input.getTimestamp());
            output.put("description", "User not found");
            result.set("output", output);
            result.put("timestamp", input.getTimestamp());
            return result;
        } catch (InvalidUserException e) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode result = mapper.createObjectNode();
            result.put("command", input.getCommand());
            ObjectNode output = mapper.createObjectNode();
            output.put("timestamp", input.getTimestamp());
            output.put("description", "User not found");
            result.set("output", output);
            result.put("timestamp", input.getTimestamp());
            return result;
        }

        return null;
    }
}
