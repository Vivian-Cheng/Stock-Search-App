package com.example.app.Section;

public class Stock {
    public final String ticker;
    public final String company;
    public final Double currPrice;
    public final Double change;
    public final Double changePercent;
    public Stock(String ticker, String company, Double currPrice, Double change, Double changePercent) {
        this.ticker = ticker;
        this.company = company;
        this.currPrice = currPrice;
        this.change = change;
        this.changePercent = changePercent;
    }
}
