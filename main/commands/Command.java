package org.poo.main.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;

@Setter
@Getter
public abstract class Command {
    /**
     * Executes the specific command
     * */
    public abstract ObjectNode execute(CommandInput input) throws Exception;
}
