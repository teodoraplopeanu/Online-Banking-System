package org.poo.main.reset;

public interface Visitable {
    /**
     * Method that accepts the visit of the Visitor
     * */
    void accept(Visitor visitor);
}
