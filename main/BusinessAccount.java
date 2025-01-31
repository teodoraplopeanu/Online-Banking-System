package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.main.statistics.CommerciantStatistics;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;

@Getter
@Setter
public final class BusinessAccount extends Account {
    // associate - role
    private LinkedHashMap<User, String> people;
    private HashMap<User, Double> spent;
    private HashMap<User, Double> deposited;
    private TreeMap<String, CommerciantStatistics> commerciantStatistics;
    private double spendingLimit;
    private double depositLimit;
    static final int INITIAL_LIMIT = 500;

    public BusinessAccount(final String currency, final String type, final User owner,
                           final double interestRate) {
        super(currency, type, owner, interestRate);
        people = new LinkedHashMap<>();
        commerciantStatistics = new TreeMap<>();
        people.put(owner, "owner");
        spent = new HashMap<>();
        deposited = new HashMap<>();
        spent.put(owner, 0.0);
        deposited.put(owner, 0.0);

        double exchangeRate = EBank.getInstance().findExchange("RON", this.getCurrency());
        double limit = INITIAL_LIMIT * exchangeRate;
        spendingLimit = limit;
        depositLimit = limit;
    }

    /**
     * Builds a Json node with account data
     * */
    public ObjectNode toJson() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("IBAN", this.getIban());
        node.put("balance", this.getBalance());
        node.put("currency", this.getCurrency());
        node.put("type", this.getType());
        ArrayNode cardsNode = mapper.createArrayNode();
        for (Card card : this.getCards()) {
            if (!card.getStatus().equals("deleted")) {
                cardsNode.add(card.toJson());
            }
        }
        node.set("cards", cardsNode);
        return node;
    }
}
