package org.poo.main.reset;

import org.poo.main.EBank;

public interface Visitor {
    /**
     * Method that visits an EBank (for resetting its fields)
     * */
    void visit(EBank eBank);
}
