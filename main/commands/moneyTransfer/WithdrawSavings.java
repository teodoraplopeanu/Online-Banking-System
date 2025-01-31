package org.poo.main.commands.moneyTransfer;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.jdi.InvalidTypeException;
import org.poo.fileio.CommandInput;
import org.poo.main.Account;
import org.poo.main.EBank;
import org.poo.main.Transaction;
import org.poo.main.User;
import org.poo.main.commands.Command;
import org.poo.main.exceptions.InsufficientFundsException;
import org.poo.main.exceptions.MinAgeException;
import org.poo.main.exceptions.NoClassicAccException;


public final class WithdrawSavings extends Command {
    static final int MIN_AGE = 21;

    /**
     * Returns the classic account which must receive the withdrawal
     * If it returns null, then the user doesn't have any classic account
     * and the error is handled properly in the execute method
     * */
    private Account hasClassicAccount(final User user, final String currency) {
        for (Account account : user.getAccounts()) {
            if (account.getType().equals("classic") && account.getCurrency().equals(currency)) {
                return account;
            }
        }
        return null;
    };

    /**
     * Withdraws money form a savings account
     * and adds the amount to a classic one (of the same owner)
     * */
    public ObjectNode execute(final CommandInput input) {
        Account account = EBank.getInstance().getAccountsMap().get(input.getAccount());
        User owner = account.getOwner();
        double amount = input.getAmount();
        double exchangeRate = EBank.getInstance().findExchange(input.getCurrency(),
                account.getCurrency());
        amount = amount * exchangeRate;

        try {
            if (owner.getAge() < MIN_AGE) {
                throw new MinAgeException();
            }

            Account classicAccount = hasClassicAccount(owner, input.getCurrency());
            if (classicAccount == null) {
                throw new NoClassicAccException();
            }

            if (!account.getType().equals("savings")) {
                throw new InvalidTypeException();
            }

            if (account.getBalance() < amount) {
                throw new InsufficientFundsException();
            }

            // Execute withdrawal of savings
            account.setBalance(account.getBalance() - amount);
            exchangeRate = EBank.getInstance().findExchange(account.getCurrency(),
                    classicAccount.getCurrency());
            amount = amount * exchangeRate;
            classicAccount.setBalance(classicAccount.getBalance() + amount);

            // Add transactions
            Transaction transactionClassic = new Transaction.TransactionBuilder(
                    input.getTimestamp(),
                    "Savings withdrawal")
                    .setClassicAccountIban(classicAccount.getIban())
                    .setSavingsAccountIban(account.getIban())
                    .setAmount(input.getAmount())
                    .build();
            classicAccount.getTransactionsSet().add(transactionClassic);
            owner.getTransactionsSet().add(transactionClassic);

            Transaction transactionSavings = new Transaction.TransactionBuilder(
                    input.getTimestamp(),
                    "Savings withdrawal")
                    .setClassicAccountIban(classicAccount.getIban())
                    .setSavingsAccountIban(account.getIban())
                    .setAmount(input.getAmount())
                    .build();
            account.getTransactionsSet().add(transactionSavings);
            owner.getTransactionsSet().add(transactionSavings);
        } catch (MinAgeException e) {

            Transaction transaction = new Transaction.TransactionBuilder(input.getTimestamp(),
                    "You don't have the minimum age required.")
                    .build();
            account.getTransactionsSet().add(transaction);
            owner.getTransactionsSet().add(transaction);

        }  catch (NoClassicAccException e) {

            Transaction transaction = new Transaction.TransactionBuilder(input.getTimestamp(),
                    "You do not have a classic account.").build();
            account.getTransactionsSet().add(transaction);
            owner.getTransactionsSet().add(transaction);

        } catch (InvalidTypeException e) {
            Transaction transaction = new Transaction.TransactionBuilder(input.getTimestamp(),
                    "Account is not of type savings.")
                    .build();
            account.getTransactionsSet().add(transaction);
            owner.getTransactionsSet().add(transaction);
        } catch (InsufficientFundsException e) {
            Transaction transaction = new Transaction.TransactionBuilder(input.getTimestamp(),
                    "Insufficient funds")
                    .build();
            account.getTransactionsSet().add(transaction);
            owner.getTransactionsSet().add(transaction);
        }

        return null;
    }
}
