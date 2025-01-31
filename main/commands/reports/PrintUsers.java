package org.poo.main.commands.reports;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.EBank;
import org.poo.main.User;
import org.poo.main.commands.Command;

public final class PrintUsers extends Command {
    public PrintUsers() { }

    /**
     * Executes printUsers command
     * */
    public ObjectNode execute(final CommandInput input) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode ret = mapper.createObjectNode();

        ret.put("command", input.getCommand());

        ArrayNode usersNode = mapper.createArrayNode();
        for (User user : EBank.getInstance().getUsers()) {
            usersNode.add(user.toJson());
        }
        ret.set("output", usersNode);

        ret.put("timestamp", input.getTimestamp());

        return ret;
    }
}
