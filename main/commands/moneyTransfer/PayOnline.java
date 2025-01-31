package org.poo.main.commands.moneyTransfer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;

import org.poo.main.Account;
import org.poo.main.Card;
import org.poo.main.EBank;
import org.poo.main.User;
import org.poo.main.BusinessAccount;
import org.poo.main.Transaction;
import org.poo.main.Commerciant;
import org.poo.main.statistics.NrOfTransactionsStatistics;
import org.poo.main.statistics.CommerciantStatistics;
import org.poo.main.commands.Command;
import org.poo.main.commission.Gold;
import org.poo.main.exceptions.CardNotFoundException;
import org.poo.main.exceptions.FrozenCardException;
import org.poo.main.exceptions.InsufficientFundsException;
import org.poo.main.exceptions.InvalidUserException;
import org.poo.main.exceptions.ThresholdException;
import org.poo.utils.Utils;

import static org.poo.utils.Utils.*;


public final class PayOnline extends Command {

    public PayOnline() { }

    /**
     * Executes payOnline command
     * */
    public ObjectNode execute(final CommandInput input) throws CardNotFoundException,
            InsufficientFundsException, FrozenCardException, InvalidUserException {
        String cardNumber = input.getCardNumber();
        User owner = EBank.getInstance().getUsersMap().get(input.getEmail());

        if (input.getAmount() == 0) {
            return null;
        }

        try {
            Card card = null;
            Account account = null;
            BusinessAccount businessAccount = null;

            for (Account accountIt : owner.getAccounts()) {
                for (Card cardIt : accountIt.getCards()) {
                    if (cardIt.getCardNumber().equals(cardNumber)) {
                        card = cardIt;
                        account = accountIt;
                        break;
                    }
                }
            }

            for (BusinessAccount sharedAccount : owner.getSharedAccounts()) {
                for (Card cardIt : sharedAccount.getCards()) {
                    if (cardIt.getCardNumber().equals(cardNumber)) {
                        card = cardIt;
                        businessAccount = sharedAccount;
                        break;
                    }
                }
            }

            if (card == null || card.getStatus().equals("deleted")) {
                throw new CardNotFoundException();
            }

            if (card.getStatus().equals("frozen")) {
                throw new FrozenCardException();
            }

            // Execute business
            if (businessAccount != null) {
                return executeBusiness(input, businessAccount);
            }

            if (input.getEmail() == null) {
                throw new InvalidUserException();
            }

            // Found the card
            double amount = input.getAmount();
            String currency = input.getCurrency();

            double exchangeRate = EBank.getInstance().findExchange(currency,
                    account.getCurrency());

            amount = amount * exchangeRate;

            Transaction transaction = new Transaction.TransactionBuilder(
                    input.getTimestamp(), "Card payment")
                    .setAmount(amount)
                    .setCommerciant(input.getCommerciant())
                    .build();
            double commission = owner.getServicePlan().getCommission(amount, account);

            // Insufficient funds
            if (amount + commission > account.getBalance()) {
                throw new InsufficientFundsException();
            }
            account.pay(amount + commission, account.getCurrency());

            // Add pay to commenrciant
            if (account.getCommerciantSpending().containsKey(input.getCommerciant())) {
                account.getCommerciantSpending().put(input.getCommerciant(),
                        account.getCommerciantSpending().get(input.getCommerciant()) + amount);
            } else {
                account.getCommerciantSpending().put(input.getCommerciant(), amount);
                account.getCommerciantStartTimestamp().put(input.getCommerciant(),
                        input.getTimestamp());
            }

            // Add transaction
            owner.getTransactionsSet().add(transaction);
            account.getTransactionsSet().add(transaction);

            // Commerciant statistics
            Commerciant commerciant = EBank.getInstance().getCommerciantsMap().get(
                    input.getCommerciant());

            if (account.getTransactionsCount().containsKey(input.getCommerciant())) {
                account.getTransactionsCount().put(input.getCommerciant(),
                        account.getTransactionsCount().get(input.getCommerciant()) + 1);
            } else {
                account.getTransactionsCount().put(input.getCommerciant(), 1);
            }

            double exchangeRate2 = EBank.getInstance().findExchange(account.getCurrency(), "RON");
            double amountInRon = amount * exchangeRate2;
            if (commerciant.getCashbackName().equals("spendingThreshold")) {
                account.setTotalSpendingThreshold(account.getTotalSpendingThreshold()
                        + amountInRon);
            }

            // Give nr of transactions cashback
            // if previously one threshold has been achieved
            NrOfTransactionsStatistics statistics = account.getNrOfTransactionsStatistics();
            if (statistics.isAchievedTwo()) {
                if (!statistics.isReceivedTwo() && commerciant.getType().equals("Food")) {
                    // Cashback
                    account.setBalance(account.getBalance() + PERCENTAGE_FOOD * amount);
                    statistics.setReceivedTwo(true);
                }
            }

            if (statistics.isAchievedFive()) {
                if (!statistics.isReceivedFive() && commerciant.getType().equals("Clothes")) {
                    account.setBalance(account.getBalance() + PERCENTAGE_CLOTHES * amount);
                    statistics.setReceivedFive(true);
                }
            }

            if (statistics.isAchievedTen()) {
                if (!statistics.isReceivedTen() && commerciant.getType().equals("Tech")) {
                    account.setBalance(account.getBalance() + PERCENTAGE_TECH * amount);
                    statistics.setReceivedTen(true);
                }
            }

            // Cashback
            commerciant.getCashbackStrategy().giveCashback(commerciant, amount, account);

            if (amountInRon >= AMOUNT_THRESHOLD
                    && owner.getServicePlan().getType().equals("silver")) {
                owner.setPaysUntilGold(owner.getPaysUntilGold() + 1);
            }
            if (owner.getPaysUntilGold() >= COUNT_THRESHOLD
                    && !owner.getServicePlan().getType().equals("gold")) {
                owner.setServicePlan(new Gold());
                // Add transaction
                Transaction transactionGold = new Transaction.TransactionBuilder(
                        input.getTimestamp(),
                        "Upgrade plan")
                        .setAccountIbanV2(account.getIban())
                        .setNewPlanType("gold")
                        .build();
                owner.getTransactionsSet().add(transactionGold);
                account.getTransactionsSet().add(transactionGold);
            }

            // Replace card, in case it is OneTimePay
            if (card.getCardType().equals("OneTimeCard")) {
                Transaction destroyTransaction = new Transaction.TransactionBuilder(
                        input.getTimestamp(), "The card has been destroyed")
                        .setCard(card.getCardNumber())
                        .setCardHolder(account.getOwner().getEmail())
                        .setAccountIban(account.getIban())
                        .build();

                account.getTransactionsSet().add(destroyTransaction);
                account.getOwner().getTransactionsSet().add(destroyTransaction);

                account.getCards().remove(card);

                Card newCard = new Card(Utils.generateCardNumber(), "active", owner);
                newCard.setCardType("OneTimeCard");
                account.getCards().add(newCard);

                // Add transaction for replacement
                Transaction newCardTransaction = new Transaction.TransactionBuilder(
                        input.getTimestamp(), "New card created")
                        .setCard(newCard.getCardNumber())
                        .setCardHolder(account.getOwner().getEmail())
                        .setAccountIban(account.getIban())
                        .build();

                account.getTransactionsSet().add(newCardTransaction);
                account.getOwner().getTransactionsSet().add(newCardTransaction);
            }

        } catch (CardNotFoundException e) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();
            node.put("command", "payOnline");
            ObjectNode output = mapper.createObjectNode();
            output.put("timestamp", input.getTimestamp());
            output.put("description", "Card not found");
            node.set("output", output);
            node.put("timestamp", input.getTimestamp());
            return node;
        } catch (InsufficientFundsException e) {
            // Insufficient funds
            Transaction transaction = new Transaction.TransactionBuilder(
                    input.getTimestamp(), "Insufficient funds").build();
            owner.getTransactionsSet().add(transaction);
        } catch (FrozenCardException e) {
            // Frozen card
            Transaction transaction = new Transaction.TransactionBuilder(
                    input.getTimestamp(), "The card is frozen").build();
            owner.getTransactionsSet().add(transaction);
        } catch (InvalidUserException ignored) { }

        return null;
    }

    /**
     * Executes the same command, but for a business account
     * */
    public ObjectNode executeBusiness(final CommandInput input, final BusinessAccount account) {
        User user = EBank.getInstance().getUsersMap().get(input.getEmail());
        double amount = input.getAmount();
        String currency = input.getCurrency();
        double exchangeRate = EBank.getInstance().findExchange(currency, account.getCurrency());
        amount = amount * exchangeRate;

        try {
            // Unauthorized transaction
            if (!account.getPeople().containsKey(user)) {
                return null;
            }

            if (account.getPeople().get(user).equals("employee")
                    && amount > account.getSpendingLimit()) {
                throw new ThresholdException();
            }

            // Pay amount
            User owner = account.getOwner();
            double commission = owner.getServicePlan().getCommission(amount, account);

            if (account.getBalance() < amount + commission) {
                throw new InsufficientFundsException();
            }

            account.pay(amount + commission, account.getCurrency());
            account.getSpent().put(user, account.getSpent().get(user) + amount);

            // Add pay to commerciant
            if (account.getCommerciantSpending().containsKey(input.getCommerciant())) {
                account.getCommerciantSpending().put(input.getCommerciant(),
                        account.getCommerciantSpending().get(input.getCommerciant()) + amount);
            } else {
                account.getCommerciantSpending().put(input.getCommerciant(), amount);
                account.getCommerciantStartTimestamp().put(input.getCommerciant(),
                        input.getTimestamp());
            }

            // Commerciant statistics
            Commerciant commerciant = EBank.getInstance().getCommerciantsMap().get(
                    input.getCommerciant());

            // Transactions count for nr of transactions
            if (account.getTransactionsCount().containsKey(input.getCommerciant())) {
                account.getTransactionsCount().put(input.getCommerciant(),
                        account.getTransactionsCount().get(input.getCommerciant()) + 1);
            } else {
                account.getTransactionsCount().put(input.getCommerciant(), 1);
            }

            // Commerciant report
            String commerciantName = input.getCommerciant();
            account.getCommerciantStatistics().putIfAbsent(commerciantName,
                    new CommerciantStatistics(commerciantName));
            CommerciantStatistics commerciantStatistics = account
                    .getCommerciantStatistics().get(commerciantName);

            if (account.getPeople().get(user).equals("employee")) {
                commerciantStatistics.setTotalReceived(commerciantStatistics.getTotalReceived()
                        + amount);
                String name = user.getLastName() + " " + user.getFirstName();
                commerciantStatistics.getEmployees().add(name);
            } else if (account.getPeople().get(user).equals("manager")) {
                commerciantStatistics.setTotalReceived(commerciantStatistics.getTotalReceived()
                        + amount);
                String name = user.getLastName() + " " + user.getFirstName();
                commerciantStatistics.getManagers().add(name);
            }

            // Add to spending threshold total
            double exchangeRate2 = EBank.getInstance().findExchange(account.getCurrency(), "RON");
            double amountInRon = amount * exchangeRate2;
            if (commerciant.getCashbackName().equals("spendingThreshold")) {
                account.setTotalSpendingThreshold(account.getTotalSpendingThreshold()
                        + amountInRon);
            }

            // Give nr of transactions cashback
            // if previously one threshold has been achieved
            NrOfTransactionsStatistics statistics = account.getNrOfTransactionsStatistics();
            if (statistics.isAchievedTwo()) {
                if (!statistics.isReceivedTwo() && commerciant.getType().equals("Food")) {
                    // Cashback
                    account.setBalance(account.getBalance() + PERCENTAGE_FOOD * amount);
                    statistics.setReceivedTwo(true);
                }
            }

            if (statistics.isAchievedFive()) {
                if (!statistics.isReceivedFive() && commerciant.getType().equals("Clothes")) {
                    account.setBalance(account.getBalance() + PERCENTAGE_CLOTHES * amount);
                    statistics.setReceivedFive(true);
                }
            }

            if (statistics.isAchievedTen()) {
                if (!statistics.isReceivedTen() && commerciant.getType().equals("Tech")) {
                    account.setBalance(account.getBalance() + PERCENTAGE_TECH * amount);
                    statistics.setReceivedTen(true);
                }
            }

            // Cashback
            commerciant.getCashbackStrategy().giveCashback(commerciant, amount, account);

            // Automatic upgrade to gold
            if (amountInRon >= AMOUNT_THRESHOLD
                    && owner.getServicePlan().getType().equals("silver")) {
                owner.setPaysUntilGold(owner.getPaysUntilGold() + 1);
            }
            if (owner.getPaysUntilGold() >= COUNT_THRESHOLD
                    && !owner.getServicePlan().getType().equals("gold")) {
                owner.setServicePlan(new Gold());
                // Add transaction
                Transaction transactionGold = new Transaction.TransactionBuilder(
                        input.getTimestamp(),
                        "Upgrade plan")
                        .setAccountIbanV2(account.getIban())
                        .setNewPlanType("gold")
                        .build();
                owner.getTransactionsSet().add(transactionGold);
                account.getTransactionsSet().add(transactionGold);
            }

        } catch (ThresholdException | InsufficientFundsException ignored) {
        }

        return null;
    }
}
