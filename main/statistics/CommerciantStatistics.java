package org.poo.main.statistics;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CommerciantStatistics {
    private String commerciant;
    private double totalReceived;
    private List<String> managers;
    private List<String> employees;

    public CommerciantStatistics(final String commerciant) {
        this.commerciant = commerciant;
        this.totalReceived = 0;
        this.managers = new ArrayList<>();
        this.employees = new ArrayList<>();
    }
}
