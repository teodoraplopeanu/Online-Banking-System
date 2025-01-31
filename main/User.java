package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.UserInput;
import org.poo.main.commission.CommissionStrategy;
import org.poo.main.commission.Standard;
import org.poo.main.commission.Student;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Setter
@Getter
public final class User {
    // Personal data
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate birthDate;
    private int age;
    private String occupation;
    private CommissionStrategy servicePlan;
    private List<Transaction> transactionsSet;
    // Banking data
    private LinkedList<String> ibans;
    private LinkedList<Account> accounts;
    // business acc to which I am the owner
    private LinkedList<BusinessAccount> businessAccounts;
    // business acc from other owners
    private LinkedList<BusinessAccount> sharedAccounts;
    // IBAN - account
    private HashMap<String, Account> accountsMap;
    private HashMap<String, BusinessAccount> sharedAccountsMap;
    // alias - IBAN
    private HashMap<String, String> aliases;
    private ObjectMapper mapper = new ObjectMapper();
    private int paysUntilGold;

    public User(final UserInput input) {
        this.firstName = input.getFirstName();
        this.lastName = input.getLastName();
        this.email = input.getEmail();
        this.birthDate = dateParse(input.getBirthDate());
        this.age = computeAge(birthDate);
        this.occupation = input.getOccupation();

        if (occupation.equals("student")) {
            servicePlan = new Student();
        } else {
            servicePlan = new Standard();
        }

        this.accounts = new LinkedList<>();
        this.businessAccounts = new LinkedList<>();
        this.accountsMap = new HashMap<>();
        this.sharedAccounts = new LinkedList<>();
        this.sharedAccountsMap = new HashMap<>();
        this.aliases = new HashMap<>();
        this.transactionsSet = new ArrayList<>();
        this.paysUntilGold = 0;
    }

    /**
     * Parse a string that represents a date
     * to a LocalDate object
     * */
    private LocalDate dateParse(final String date) {
        String[] tokens = date.split("-");
        return LocalDate.of(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]),
                Integer.parseInt(tokens[2]));
    }

    /**
     * Compute the age of the user
     * */
    private int computeAge(final LocalDate birthDateInput) {
        Period diff = Period.between(birthDateInput, LocalDate.now());
        return diff.getYears();
    }

    /**
     * Creates a Json node with user data
     * */
    public ObjectNode toJson() {
        ObjectNode objectNode = mapper.createObjectNode();

        objectNode.put("firstName", firstName);
        objectNode.put("lastName", lastName);
        objectNode.put("email", email);
        ArrayNode accountsNode = mapper.createArrayNode();
        for (Account account : accounts) {
            accountsNode.add(account.toJson());
        }
        objectNode.set("accounts", accountsNode);
        return objectNode;
    }
}
