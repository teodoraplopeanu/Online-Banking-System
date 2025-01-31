package org.poo.main.commands.reports;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.BusinessAccount;
import org.poo.main.statistics.CommerciantStatistics;
import org.poo.main.EBank;
import org.poo.main.User;
import org.poo.main.commands.Command;

import java.util.Collections;
import java.util.Map;

public final class BusinessReport extends Command {
    public BusinessReport() { }

    /**
     * Performs a business report for the account received in the input
     * The report has two types:
     *      * transaction (shows how much every associate spent or deposited)
     *      * commerciant (statistics of the amounts spent to every commerciant)
     * */
    @Override
    public ObjectNode execute(final CommandInput input) {
        BusinessAccount account = EBank.getInstance().getBusinessAccountsMap()
                .get(input.getAccount());

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode result = mapper.createObjectNode();
        result.put("command", input.getCommand());

        ObjectNode output = mapper.createObjectNode();
        output.put("IBAN", input.getAccount());
        output.put("balance", account.getBalance());
        output.put("currency", account.getCurrency());
        output.put("spending limit", account.getSpendingLimit());
        output.put("deposit limit", account.getDepositLimit());
        output.put("statistics type", input.getType());

        // Complete the report with the specific data based on
        // the report type
        if (input.getType().equals("transaction")) {
            return transactionReport(output, result, account, input);
        } else {
            return commerciantReport(output, result, account, input);
        }
    }

    /**
     * Performs the transaction report
     * */
    public ObjectNode transactionReport(final ObjectNode output, final ObjectNode result,
                                        final BusinessAccount account, final CommandInput input) {
        ObjectMapper mapper = new ObjectMapper();
        double totalSpent = 0;
        double totalDeposited = 0;

        ArrayNode managers = mapper.createArrayNode();
        for (Map.Entry<User, String> entry : account.getPeople().entrySet()) {
            if (entry.getValue().equals("manager")) {
                ObjectNode manager = mapper.createObjectNode();
                manager.put("username", entry.getKey().getLastName() + " "
                        + entry.getKey().getFirstName());
                manager.put("spent", account.getSpent().get(entry.getKey()));
                totalSpent += account.getSpent().get(entry.getKey());
                manager.put("deposited", account.getDeposited().get(entry.getKey()));
                totalDeposited += account.getDeposited().get(entry.getKey());
                managers.add(manager);
            }
        }

        output.set("managers", managers);

        ArrayNode employees = mapper.createArrayNode();
        for (Map.Entry<User, String> entry : account.getPeople().entrySet()) {
            if (entry.getValue().equals("employee")) {
                ObjectNode employee = mapper.createObjectNode();
                employee.put("username", entry.getKey().getLastName() + " "
                        + entry.getKey().getFirstName());
                employee.put("spent", account.getSpent().get(entry.getKey()));
                totalSpent += account.getSpent().get(entry.getKey());
                employee.put("deposited", account.getDeposited().get(entry.getKey()));
                totalDeposited += account.getDeposited().get(entry.getKey());
                employees.add(employee);
            }
        }

        output.set("employees", employees);

        output.put("total spent", totalSpent);
        output.put("total deposited", totalDeposited);

        result.set("output", output);
        result.put("timestamp", input.getTimestamp());
        return result;
    }

    /**
     * Performs the commerciant report
     * */
    public ObjectNode commerciantReport(final ObjectNode output, final ObjectNode result,
                                        final BusinessAccount account, final CommandInput input) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode commerciants = mapper.createArrayNode();
        for (Map.Entry<String, CommerciantStatistics> entry : account.getCommerciantStatistics()
                .entrySet()) {
            ObjectNode commerciant = mapper.createObjectNode();
            commerciant.put("commerciant", entry.getKey());
            CommerciantStatistics statistics = entry.getValue();
            commerciant.put("total received", statistics.getTotalReceived());

            Collections.sort(statistics.getManagers());
            Collections.sort(statistics.getEmployees());

            ArrayNode managers = mapper.createArrayNode();
            for (String manager : statistics.getManagers()) {
                managers.add(manager);
            }
            commerciant.put("managers", managers);
            ArrayNode employees = mapper.createArrayNode();
            for (String employee : statistics.getEmployees()) {
                employees.add(employee);
            }
            commerciant.put("employees", employees);
            commerciants.add(commerciant);
        }

        output.set("commerciants", commerciants);
        result.set("output", output);
        result.put("timestamp", input.getTimestamp());
        return result;
    }
}
