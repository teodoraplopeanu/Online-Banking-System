package org.poo.main.statistics;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NrOfTransactionsStatistics {
    private boolean achievedTwo;
    private boolean receivedTwo;
    private boolean achievedFive;
    private boolean receivedFive;
    private boolean achievedTen;
    private boolean receivedTen;

    public NrOfTransactionsStatistics() {
        achievedTwo = false;
        receivedTwo = false;
        achievedFive = false;
        receivedFive = false;
        achievedTen = false;
        receivedTen = false;
    }
}
