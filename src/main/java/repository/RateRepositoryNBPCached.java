package repository;

import nbpapi.Rate;
import nbpapi.RateTable;
import nbpapi.Table;
import nbpapi.URIGenerator;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RateRepositoryNBPCached implements RateRepository{
    private static final Rate RATE_PLN = Rate.builder().code("PLN").mid(1).currency("złoty").build();
    private ApiRepository<RateTable> rates = new ApiRepository<>(RateTable.class);
    private Map<LocalDate, RateTable> cacheA = new HashMap<>();
    private Map<LocalDate, RateTable> cacheB = new HashMap<>();

    @Override
    public List<Rate> findByTableAndDate(Table table, LocalDate date) throws IOException, InterruptedException {
        if (table == Table.TABLE_A) {
            if (cacheA.containsKey(date)) {
                return cacheA.get(date).getRates();
            } else {
                //TODO poprawić generowanie URI dla konkretnej daty tabeli
                final List<RateTable> list = this.rates.getList(URIGenerator.tableByDateJson(table, date));
                if (!list.isEmpty()) {
                    RateTable rateTable = list.get(0);
                    final String strDate = rateTable.getEffectiveDate();
                    final LocalDate dateTable = LocalDate.parse(strDate);
                    rateTable.getRates().add(RATE_PLN);
                    cacheA.put(dateTable, rateTable);
                    return rateTable.getRates();
                }
            }
        }
        if (table == Table.TABLE_B) {
            if (cacheB.containsKey(date)) {
                return cacheB.get(date).getRates();
            } else {
                //TODO poprawić generowanie URI dla konkretnej daty tabeli
                final RateTable rateTable = this.rates.getList(URIGenerator.tableByDateJson(table, date)).get(0);
                final String strDate = rateTable.getEffectiveDate();
                final LocalDate dateTable = LocalDate.parse(strDate);
                rateTable.getRates().add(RATE_PLN);
                cacheB.put(dateTable, rateTable);
                return rateTable.getRates();
            }
        }
        return this.rates.getList(URIGenerator.currentTableJson(table)).get(0).getRates();
    }
}
