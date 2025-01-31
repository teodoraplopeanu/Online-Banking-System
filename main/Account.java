package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.main.statistics.NrOfTransactionsStatistics;
import org.poo.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

@Getter
@Setter
public class Account {
    private String iban;
    private double balance;
    private final String currency;
    private final String type;
    private List<Card> cards;
    private final User owner;
    private double minBalance;
    private TreeMap<String, Double> commerciantSpending;
    private HashMap<String, Integer> commerciantStartTimestamp;
    private List<Transaction> transactionsSet;
    private double interestRate;
    // commerciant name - nr of transactions
    private HashMap<String, Integer> transactionsCount;
    // commerciant name - total amount paid
    private double totalSpendingThreshold;
    private NrOfTransactionsStatistics nrOfTransactionsStatistics;

    public Account(final String currency, final String type, final User owner,
                   final double interestRate) {
        this.iban = Utils.generateIBAN();
        this.balance = 0;
        this.currency = currency;
        this.type = type;
        this.cards = new ArrayList<>();
        this.owner = owner;
        this.minBalance = 0;
        this.commerciantSpending = new TreeMap<>();
        this.commerciantStartTimestamp = new HashMap<>();
        this.interestRate = interestRate;
        this.transactionsCount = new HashMap<>();
        this.transactionsSet = new ArrayList<>();
        this.totalSpendingThreshold = 0;
        this.nrOfTransactionsStatistics = new NrOfTransactionsStatistics();
    }

    /**
     * Subtracts an amount of money from the account
     * */
    public void pay(final double amount, final String payCurrency) {
        double amountToPay = amount * EBank.getInstance().findExchange(payCurrency, currency);
        setBalance(balance - amountToPay);
    }

    /**
     * Builds a Json node with account data
     * */
    public ObjectNode toJson() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("IBAN", iban);
        node.put("balance", balance);
        node.put("currency", currency);
        node.put("type", type);
        ArrayNode cardsNode = mapper.createArrayNode();
        for (Card card : cards) {
            if (!card.getStatus().equals("deleted")) {
                cardsNode.add(card.toJson());
            }
        }
        node.set("cards", cardsNode);
        return node;
    }
}
