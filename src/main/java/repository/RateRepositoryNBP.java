package repository;

import nbpapi.Rate;
import nbpapi.RateTable;
import nbpapi.Table;
import nbpapi.URIGenerator;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class RateRepositoryNBP implements RateRepository {
    private static final ApiRepository<RateTable> rates = new ApiRepository<>(RateTable.class);
    private static final Rate RATE_PLN = Rate.builder()
            .currency("złotówki polskie")
            .code("PLN")
            .mid(BigDecimal.valueOf(1.00))
            .build();

    @Override
    public List<Rate> findByTableLast(Table table) throws IOException, InterruptedException {
        List<Rate> rates = this.rates.getList(URIGenerator.currentTableJson(table)).get(0).getRates();
        rates.add(RATE_PLN);
        return rates;
    }

    @Override
    public List<Rate> findByTableAndDate(Table table, LocalDate date) throws IOException, InterruptedException {
        List<Rate> rates = RateRepositoryNBP.rates.getList(URIGenerator.currentTableJson(table)).get(0).getRates();
        rates.add(RATE_PLN);
        return rates;
    }
}
