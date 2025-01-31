package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public final class Transaction {
    private int timestamp;
    private String description;
    private String senderIban;
    private String receiverIban;
    private String amountString;
    private double amount;
    private String accountIbanv2;
    private String accountIban;
    private String commerciant;
    private String card;
    private String cardHolder;
    private String transferType;
    private String newPlanType;
    private String currency;
    private String splitPaymentType;
    private List<Double> amountForUsers;
    private List<String> involvedAccounts;
    private String error;
    private String classicAccountIban;
    private String savingsAccountIban;

    private ObjectMapper mapper;
    private ObjectNode json;

    /**
     * Transfers class members
     * */
    public Transaction(final TransactionBuilder builder) {
        this.timestamp = builder.timestamp;
        this.description = builder.description;
        this.senderIban = builder.senderIban;
        this.receiverIban = builder.receiverIban;
        this.amountString = builder.amountString;
        this.amount = builder.amount;
        this.accountIban = builder.accountIban;
        this.commerciant = builder.commerciant;
        this.card = builder.card;
        this.cardHolder = builder.cardHolder;
        this.transferType = builder.transferType;
        this.accountIbanv2 = builder.accountIbanv2;
        this.newPlanType = builder.newPlanType;
        this.mapper = new ObjectMapper();
        this.currency = builder.currency;
        this.splitPaymentType = builder.splitPaymentType;
        this.amountForUsers = builder.amountForUsers;
        this.involvedAccounts = builder.involvedAccounts;
        this.error = builder.error;
        this.classicAccountIban = builder.classicAccountIban;
        this.savingsAccountIban = builder.savingsAccountIban;
    }


    public static final class TransactionBuilder {
        // Required parameters
        private final int timestamp;
        private final String description;

        // Optional parameters
        private String senderIban;
        private String receiverIban;
        private String amountString;
        private double amount;
        private String accountIbanv2;
        private String accountIban;
        private String commerciant;
        private String card;
        private String cardHolder;
        private String transferType;
        private String newPlanType;
        private String currency;
        private String splitPaymentType;
        private List<Double> amountForUsers;
        private List<String> involvedAccounts;
        private String error;
        private String classicAccountIban;
        private String savingsAccountIban;

        public TransactionBuilder(final int timestamp, final String description) {
            this.timestamp = timestamp;
            this.description = description;
        }

        /**
         * Builder method
         * */
        public TransactionBuilder setSplitPaymentType(final String splitPaymentTypeB) {
            this.splitPaymentType = splitPaymentTypeB;
            return this;
        }

        /**
         * Builder method
         * */
        public TransactionBuilder setError(final String errorB) {
            this.error = errorB;
            return this;
        }

        /**
         * Builder method
         * */
        public TransactionBuilder setAmountForUsers(final List<Double> amountForUsersB) {
            this.amountForUsers = amountForUsersB;
            return this;
        }

        /**
         * Builder method
         * */
        public TransactionBuilder setInvolvedAccounts(final List<String> involvedAccountsB) {
            this.involvedAccounts = involvedAccountsB;
            return this;
        }

        /**
         * Builder method
         * */
        public TransactionBuilder setSenderIban(final String senderIbanB) {
            this.senderIban = senderIbanB;
            return this;
        }

        /**
         * Builder method
         * */
        public TransactionBuilder setReceiverIban(final String receiverIbanB) {
            this.receiverIban = receiverIbanB;
            return this;
        }

        /**
         * Builder method
         * */
        public TransactionBuilder setAmount(final double amountB) {
            this.amount = amountB;
            return this;
        }

        /**
         * Builder method
         * */
        public TransactionBuilder setAmountString(final String amountStringB) {
            this.amountString = amountStringB;
            String[] tokens = amountStringB.split(" ");
            this.amount = Double.parseDouble(tokens[0]);
            return this;
        }

        /**
         * Builder method
         * */
        public TransactionBuilder setAccountIbanV2(final String accountB) {
            this.accountIbanv2 = accountB;
            return this;
        }

        /**
         * Builder method
         * */
        public TransactionBuilder setNewPlanType(final String newPlanTypeB) {
            this.newPlanType = newPlanTypeB;
            return this;
        }

        /**
         * Builder method
         * */
        public TransactionBuilder setAccountIban(final String accountIbanB) {
            this.accountIban = accountIbanB;
            return this;
        }

        /**
         * Builder method
         * */
        public TransactionBuilder setCommerciant(final String commerciantB) {
            this.commerciant = commerciantB;
            return this;
        }

        /**
         * Builder method
         * */
        public TransactionBuilder setCardHolder(final String cardHolderB) {
            this.cardHolder = cardHolderB;
            return this;
        }

        /**
         * Builder method
         * */
        public TransactionBuilder setCard(final String cardB) {
            this.card = cardB;
            return this;
        }

        /**
         * Builder method
         * */
        public TransactionBuilder setTransferType(final String transferTypeB) {
            this.transferType = transferTypeB;
            return this;
        }

        /**
         * Builder method
         * */
        public TransactionBuilder setCurrency(final String currencyB) {
            this.currency = currencyB;
            return this;
        }

        /**
         * Builder method
         * */
        public TransactionBuilder setClassicAccountIban(final String classicAccountIbanB) {
            this.classicAccountIban = classicAccountIbanB;
            return this;
        }

        /**
         * Builder method
         * */
        public TransactionBuilder setSavingsAccountIban(final String savingsAccountIbanB) {
            this.savingsAccountIban = savingsAccountIbanB;
            return this;
        }

        /**
         * Final build
         * */
        public Transaction build() {
            return new Transaction(this);
        }
    }

    /**
     * Builds an ObjectNode with Transaction data
     * */
    public ObjectNode toJson() {
        if (json != null) {
            return json;
        }

        json = mapper.createObjectNode();
        json.put("timestamp", timestamp);
        json.put("description", description);

        if (senderIban != null) {
            json.put("senderIBAN", senderIban);
        }

        if (receiverIban != null) {
            json.put("receiverIBAN", receiverIban);
        }

        if (amountString != null) {
            json.put("amount", amountString);
        } else if (amount != 0) {
            json.put("amount", amount);
        }

        if (savingsAccountIban != null) {
            json.put("savingsAccountIBAN", savingsAccountIban);
        }

        if (classicAccountIban != null) {
            json.put("classicAccountIBAN", classicAccountIban);
        }

        if (splitPaymentType != null) {
            json.put("splitPaymentType", splitPaymentType);
        }

        if (currency != null) {
            json.put("currency", currency);
        }

        if (amountForUsers != null) {
            if (splitPaymentType.equals("equal")) {
                json.put("amount", amountForUsers.getFirst());
            } else {
                ArrayNode arrayNode = new ObjectMapper().createArrayNode();
                for (double amountIt : amountForUsers) {
                    arrayNode.add(amountIt);
                }
                json.set("amountForUsers", arrayNode);
            }
        }

        if (involvedAccounts != null) {
            ArrayNode arrayNode = new ObjectMapper().createArrayNode();
            for (String involvedAccount : involvedAccounts) {
                arrayNode.add(involvedAccount);
            }
            json.set("involvedAccounts", arrayNode);
        }

        if (error != null) {
            json.put("error", error);
        }

        if (transferType != null) {
            json.put("transferType", transferType);
        }

        if (card != null) {
            json.put("card", card);
        }

        if (cardHolder != null) {
            json.put("cardHolder", cardHolder);
        }

        if (accountIban != null) {
            json.put("account", accountIban);
        }

        if (accountIbanv2 != null) {
            json.put("accountIBAN", accountIbanv2);
        }

        if (commerciant != null) {
            json.put("commerciant", commerciant);
        }

        if (newPlanType != null) {
            json.put("newPlanType", newPlanType);
        }

        return json;
    }

}
