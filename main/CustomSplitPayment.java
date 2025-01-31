package org.poo.main;

import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;
import org.poo.main.exceptions.InsufficientFundsException;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public class CustomSplitPayment {
    private List<Account> accountsInvolved;
    private List<Account> accountsAccepted;
    private List<Double> amountForUsers;
    private List<String> involvedAccountsIbans;
    private HashMap<Account, Double> amountPerAccount;
    private String currency;
    private int timestamp;
    private double totalAmount;
    private String splitPaymentType;

    public CustomSplitPayment(final CommandInput input) {
        involvedAccountsIbans = input.getAccounts();
        this.totalAmount = input.getAmount();
        this.currency = input.getCurrency();
        this.timestamp = input.getTimestamp();
        this.amountPerAccount = new HashMap<>();
        this.accountsInvolved = new LinkedList<>();
        this.accountsAccepted = new ArrayList<>();
        this.splitPaymentType = input.getSplitPaymentType();

        if (splitPaymentType.equals("custom")) {
            amountForUsers = input.getAmountForUsers();
        } else {
            amountForUsers = new ArrayList<>();
            double eqAmount = totalAmount / involvedAccountsIbans.size();
            for (int i = 0; i < involvedAccountsIbans.size(); i++) {
                amountForUsers.add(eqAmount);
            }
        }

        for (int idx = 0; idx < involvedAccountsIbans.size(); idx++) {
            String accountIban = involvedAccountsIbans.get(idx);
            Double amount = amountForUsers.get(idx);

            Account account = EBank.getInstance().getAccountsMap().get(accountIban);
            if (account == null) {
                return;
            }
            double exchangeRate = EBank.getInstance().findExchange(this.currency,
                    account.getCurrency());

            amountPerAccount.put(account, amount * exchangeRate);
            accountsInvolved.add(account);
        }
    }

    /**
     * Begins the execution of the split payment,
     * when all the accounts have accepted
     * */
    public void execute() {
        Account insuffiecientFundsAccount = null;
        try {
            for (Account account : accountsInvolved) {
                if (account.getBalance() < amountPerAccount.get(account)) {
                    insuffiecientFundsAccount = account;
                    throw new InsufficientFundsException();
                }
            }

            DecimalFormat numberFormat = new DecimalFormat("#.00");
            Transaction transaction = new Transaction.TransactionBuilder(this.timestamp,
                    "Split payment of " + numberFormat.format(totalAmount) + " " + currency)
                    .setCurrency(currency)
                    .setAmountForUsers(amountForUsers)
                    .setSplitPaymentType(splitPaymentType)
                    .setInvolvedAccounts(involvedAccountsIbans)
                    .build();

            for (Account account : accountsInvolved) {
                double amount = amountPerAccount.get(account);
                account.pay(amount, account.getCurrency());
                account.getTransactionsSet().add(transaction);
                account.getOwner().getTransactionsSet().add(transaction);
            }
        } catch (InsufficientFundsException e) {
            DecimalFormat numberFormat = new DecimalFormat("#.00");
            Transaction transaction = new Transaction.TransactionBuilder(timestamp,
                    "Split payment of " + numberFormat.format(totalAmount)
                            + " " + currency)
                    .setCurrency(currency)
                    .setAmountForUsers(amountForUsers)
                    .setSplitPaymentType(splitPaymentType)
                    .setInvolvedAccounts(involvedAccountsIbans)
                    .setError("Account " + insuffiecientFundsAccount.getIban()
                            + " has insufficient funds for a split payment.").build();

            for (Account account : accountsInvolved) {
                account.getTransactionsSet().add(transaction);
                account.getOwner().getTransactionsSet().add(transaction);
            }
        }
    }
}
