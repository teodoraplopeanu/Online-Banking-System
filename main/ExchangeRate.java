package org.poo.main;

import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.ExchangeInput;

@Getter
@Setter
public class ExchangeRate {
    private String from;
    private String to;
    private double rate;

    public ExchangeRate(final ExchangeInput input) {
        this.from = input.getFrom();
        this.to = input.getTo();
        this.rate = input.getRate();
    }
}
