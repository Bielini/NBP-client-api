package repository;

import nbpapi.Rate;
import nbpapi.RateTable;
import nbpapi.Table;
import nbpapi.URIGenerator;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class RateRepositoryNBP implements RateRepository {
    private static final ApiRepository<RateTable> rates = new ApiRepository<>(RateTable.class);
    @Override
    public List<Rate> findByTableAndDate(Table table, LocalDate date) throws IOException, InterruptedException {
        return rates.getList(URIGenerator.currentTableJson(table)).get(0).getRates();
    }
}
