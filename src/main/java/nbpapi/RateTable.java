package nbpapi;

import java.util.List;

public class RateTable {

    private String table;
    private String no;
    private String effectiveDate;
    private String tradingDate;
    private List<Rate> rates;


    public String getTable() {
        return table;
    }

    public String getNo() {
        return no;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public List<Rate> getRates() {
        return rates;
    }

    public String getTradingDate() {
        return tradingDate;
    }

    @Override
    public String toString() {
        return "RateTable{" +
                "table='" + table + '\'' +
                ", no='" + no + '\'' +
                ", effectiveDate='" + effectiveDate + '\'' +
                ", tradingDate='" + tradingDate + '\'' +
                ", rates=" + rates +
                '}';
    }
}

