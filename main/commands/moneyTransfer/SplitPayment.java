package org.poo.main.commands.moneyTransfer;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.CustomSplitPayment;
import org.poo.main.EBank;
import org.poo.main.commands.Command;

public final class SplitPayment extends Command {
    public SplitPayment() { }

    /**
     * Executes splitPayment command
     * */
    public ObjectNode execute(final CommandInput input) {
        CustomSplitPayment splitPayment = new CustomSplitPayment(input);
        EBank.getInstance().getCurrentSplits().add(splitPayment);

        return null;
    }
}
