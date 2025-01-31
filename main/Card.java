package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public final class Card {
    private final String cardNumber;
    private String status;
    private String cardType;
    private User creator;

    public Card(final String cardNumber, final String status, final User user) {
        this.cardNumber = cardNumber;
        this.status = status;
        this.creator = user;
    }

    /**
     * Builds a Json node with Card data
     * */
    public ObjectNode toJson() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("cardNumber", cardNumber);
        node.put("status", status);

        return node;
    }
}
