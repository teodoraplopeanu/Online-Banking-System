package org.poo.main;

import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommerciantInput;
import org.poo.fileio.ExchangeInput;
import org.poo.fileio.ObjectInput;
import org.poo.fileio.UserInput;
import org.poo.main.reset.Visitable;
import org.poo.main.reset.Visitor;

import java.util.*;

@Getter
@Setter
public final class EBank implements Visitable {
    private static EBank instance = null;
    private final List<User> users;
    // email - user
    private final HashMap<String, User> usersMap;
    // commerciant name - commerciant ibject
    private final HashMap<String, Commerciant> commerciantsMap;
    // iban - account (for commerciants)
    private final List<String> commerciantAccounts;
    // IBAN - account
    // contains only normal accounts
    private final HashMap<String, Account> accountsMap;
    // IBAN - business account
    // in order to differentiate approach at certain commands
    private final HashMap<String, BusinessAccount> businessAccountsMap;
    private final List<ExchangeRate> exchangeRates;
    private final HashMap<String, HashMap<String, Double>> exchangeMatrix;
    private Map<String, Map<String, Double>> shortestPaths;
    private LinkedList<CustomSplitPayment> currentSplits;

    private EBank() {
        users = new ArrayList<User>();
        usersMap = new HashMap<>();
        accountsMap = new HashMap<>();
        businessAccountsMap = new HashMap<>();
        commerciantAccounts = new ArrayList<>();
        exchangeRates = new ArrayList<>();
        exchangeMatrix = new HashMap<>();
        shortestPaths = new HashMap<>();
        commerciantsMap = new HashMap<>();
        currentSplits = new LinkedList<>();
    }

    /**
     * Instance getter for Singleton pattern
     * */
    public static EBank getInstance() {
        if (instance == null) {
            instance = new EBank();
        }
        return instance;
    }

    /**
     * Get the users from the input
     * */
    public void readUsers(final ObjectInput inputData) {
        UserInput[] userInput = inputData.getUsers();

        for (UserInput input : userInput) {
            User user = new User(input);
            users.add(user);
            usersMap.put(user.getEmail(), user);
        }
    }

    /**
     * Get the commerciants from the input
     * */
    public void readCommerciants(final ObjectInput inputData) {
        CommerciantInput[] commerciantInput = inputData.getCommerciants();

        for (CommerciantInput input : commerciantInput) {
            Commerciant commerciant = new Commerciant(input);
            commerciantsMap.put(commerciant.getCommerciant(), commerciant);
            commerciantAccounts.add(commerciant.getAccount());
        }
    }

    /**
     * Get the exchange rates from the input
     * */
    public void readExchangeRates(final ObjectInput inputData) {
        ExchangeInput[] exchangeInput = inputData.getExchangeRates();
        for (ExchangeInput input : exchangeInput) {
            ExchangeRate exchangeRate = new ExchangeRate(input);
            exchangeRates.add(exchangeRate);

            // Build the map of exchanges
            exchangeMatrix.putIfAbsent(exchangeRate.getFrom(), new HashMap<>());
            exchangeMatrix.putIfAbsent(exchangeRate.getTo(), new HashMap<>());

            exchangeMatrix.get(exchangeRate.getFrom()).put(exchangeRate.getTo(),
                    exchangeRate.getRate());
            exchangeMatrix.get(exchangeRate.getTo()).put(exchangeRate.getFrom(),
                    1.0 / exchangeRate.getRate());
        }

        // Set the diagonal to 1
        for (String currency : exchangeMatrix.keySet()) {
            exchangeMatrix.get(currency).put(currency, 1.0);
        }

        Set<String> currencies = exchangeMatrix.keySet();
        shortestPaths = floydWarshall(currencies);
    }

    /**
     * Apply the Floyd-Warshall algorithm to find the shortest path currency exchange rate
     * */
    private Map<String, Map<String, Double>> floydWarshall(final Set<String> currencies) {
        Map<String, Map<String, Double>> distances = new HashMap<>();

        // Initialize distances with -log(rate)
        // (because we want to find the maximum product, not sum)
        for (String from : currencies) {
            distances.put(from, new HashMap<>());
            for (String to : currencies) {
                if (from.equals(to)) {
                    // log(1) = 0
                    distances.get(from).put(to, 0.0);
                } else if (exchangeMatrix.containsKey(from)
                        && exchangeMatrix.get(from).containsKey(to)) {
                    distances.get(from).put(to, -Math.log(exchangeMatrix.get(from).get(to)));
                } else {
                    distances.get(from).put(to, Double.POSITIVE_INFINITY);
                }
            }
        }

        // Floyd-Warshall Algorithm
        for (String i : currencies) {
            for (String j : currencies) {
                for (String k : currencies) {
                    if (distances.get(i).get(k) + distances.get(k).get(j)
                            < distances.get(i).get(j)) {
                        distances.get(i).put(j, distances.get(i).get(k)
                                + distances.get(k).get(j));
                    }
                }
            }
        }

        return distances;
    }

    /**
     * Find the appropriate exchange rate between two currnecies
     * @param from fromExchangeRate
     * @param to toExchangeRate
     * */
    public double findExchange(final String from, final String to) {
        if (from.equals(to)) {
            return 1.0;
        }

        if (!shortestPaths.containsKey(from) || !shortestPaths.get(from).containsKey(to)) {
            return 1.0;
        }

        return Math.exp(-shortestPaths.get(from).get(to));
    }

    /**
     * Resets the exchangeMatrix (for a new test)
     * */
    public void resetMatrix() {
        exchangeMatrix.clear();
    }

    @Override
    public void accept(final Visitor visitor) {
        visitor.visit(this);
    }
}
