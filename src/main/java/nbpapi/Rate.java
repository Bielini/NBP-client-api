package nbpapi;

import java.math.BigDecimal;

public class Rate {
    private String currency;
    private String code;
    private String ask;
    private double bid;
    private double mid;


    public String getCurrency() {
        return currency;
    }

    public String getCode() {
        return code;
    }

    public String getAsk() {
        return ask;
    }

    public double getBid() {
        return bid;
    }

    public double getMid() {
        return mid;
    }

    @Override
    public String toString() {
        return "Rate{" +
                "currency='" + currency + '\'' +
                ", code='" + code + '\'' +
                ", ask='" + ask + '\'' +
                ", bid=" + bid +
                ", mid=" + mid +
                '}';
    }
}
