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
    private static final Rate RATE_PLN = Rate.builder().currency("złotówki polskie").code("PLN").mid(1.00).build();
    private static final ApiRepository<RateTable> rates = new ApiRepository<>(RateTable.class);
    private Map<LocalDate,RateTable> cache = new HashMap<>();

    @Override
    public List<Rate> findByTableAndDate(Table table, LocalDate date) throws IOException, InterruptedException {
        if(cache.containsKey(date)){
            return cache.get(date).getRates();
        }else{
            final RateTable rateTable = this.rates.getList(URIGenerator.currentTableJson(table)).get(0);
            final String strDate = rateTable.getEffectiveDate();
            final LocalDate dateTable = LocalDate.parse(strDate);
            rateTable.getRates().add(RATE_PLN);
            cache.put(dateTable,rateTable);
            return rateTable.getRates();
        }
    }
}
