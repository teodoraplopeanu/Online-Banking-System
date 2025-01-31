package org.poo.main.commands.bankingData.account;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.EBank;
import org.poo.main.User;
import org.poo.main.commands.Command;

public final class SetAlias extends Command {
    public SetAlias() { }

    /**
     * Executes setAlias command
     * */
    public ObjectNode execute(final CommandInput input) {
        String alias = input.getAlias();
        String iban = input.getAccount();
        User user = EBank.getInstance().getUsersMap().get(input.getEmail());

        user.getAliases().put(alias, iban);

        return null;
    }
}
