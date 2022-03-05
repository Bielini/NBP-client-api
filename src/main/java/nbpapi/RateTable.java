package nbpapi;

import lombok.Data;

import java.util.List;


@Data
public class RateTable {

    private String table;
    private String no;
    private String effectiveDate;
    private String tradingDate;
    private List<Rate> rates;



}

