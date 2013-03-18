/*
 * Candlestick Pattern
 */
package com.nnstockpredict.data.candlestick;

/**
 *
 * @author ajaybhaga
 */
public class CandlestickPattern {

    private int id;
    private String name;
    private String rule;
    private double probabilityScore;

    public CandlestickPattern(int id, String name, String rule, double probabilityScore) {
        this.id = id;
        this.name = name;
        this.rule = rule;
        this.probabilityScore = probabilityScore;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRule() {
        return rule;
    }

    public double getProbabilityScore() {
        return probabilityScore;
    }
}
