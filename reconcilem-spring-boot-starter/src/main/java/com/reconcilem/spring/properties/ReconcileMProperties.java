package com.reconcilem.spring.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "reconcilem")
public class ReconcileMProperties {

    private Thresholds thresholds = new Thresholds();
    private Rules rules = new Rules();

    public Thresholds getThresholds() {
        return thresholds;
    }

    public void setThresholds(Thresholds thresholds) {
        this.thresholds = thresholds;
    }

    public Rules getRules() {
        return rules;
    }

    public void setRules(Rules rules) {
        this.rules = rules;
    }

    public static class Thresholds {

        private int matchedScore = 80;
        private int possibleMatchScore = 50;

        public int getMatchedScore() {
            return matchedScore;
        }

        public void setMatchedScore(int matchedScore) {
            this.matchedScore = matchedScore;
        }

        public int getPossibleMatchScore() {
            return possibleMatchScore;
        }

        public void setPossibleMatchScore(int possibleMatchScore) {
            this.possibleMatchScore = possibleMatchScore;
        }
    }

    public static class Rules {

        private BigDecimal amountTolerance = new BigDecimal("1000.00");
        private int amountScore = 40;
        private int currencyScore = 10;
        private int dateToleranceDays = 3;
        private int dateScore = 15;
        private int referenceScore = 20;
        private int counterpartyScore = 10;

        public BigDecimal getAmountTolerance() {
            return amountTolerance;
        }

        public void setAmountTolerance(BigDecimal amountTolerance) {
            this.amountTolerance = amountTolerance;
        }

        public int getAmountScore() {
            return amountScore;
        }

        public void setAmountScore(int amountScore) {
            this.amountScore = amountScore;
        }

        public int getCurrencyScore() {
            return currencyScore;
        }

        public void setCurrencyScore(int currencyScore) {
            this.currencyScore = currencyScore;
        }

        public int getDateToleranceDays() {
            return dateToleranceDays;
        }

        public void setDateToleranceDays(int dateToleranceDays) {
            this.dateToleranceDays = dateToleranceDays;
        }

        public int getDateScore() {
            return dateScore;
        }

        public void setDateScore(int dateScore) {
            this.dateScore = dateScore;
        }

        public int getReferenceScore() {
            return referenceScore;
        }

        public void setReferenceScore(int referenceScore) {
            this.referenceScore = referenceScore;
        }

        public int getCounterpartyScore() {
            return counterpartyScore;
        }

        public void setCounterpartyScore(int counterpartyScore) {
            this.counterpartyScore = counterpartyScore;
        }
    }
}