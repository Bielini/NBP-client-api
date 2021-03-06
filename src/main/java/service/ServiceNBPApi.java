package service;

import nbpapi.Rate;
import nbpapi.Table;
import repository.RateRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ServiceNBPApi implements ServiceNBP {
    private final RateRepository rates;

    public ServiceNBPApi(RateRepository rates) {
        this.rates = rates;
    }

    @Override
    public BigDecimal calc(BigDecimal amount, String source, String target) throws IOException, InterruptedException {
        final List<Rate> rates = this.rates.findByTableLast(Table.TABLE_A);
        Optional<Rate> sourceRate = rates.stream()
                .filter(rate -> rate.getCode().equals(source))
                .findFirst();

        Optional<Rate> targetRate = rates.stream()
                .filter(rate -> rate.getCode().equals(target))
                .findFirst();

        if (sourceRate.isPresent() && targetRate.isPresent()) {
            return amount.multiply(sourceRate.get().getMid().divide(targetRate.get().getMid()));
        } else {
            throw new InvalidParameterException("Missing code");
        }
    }

    @Override
    public List<Rate> findAll(Table table) throws IOException, InterruptedException {
        return rates.findByTableLast(table);
    }

    @Override
    public List<Rate> findAll(Table table, LocalDate date) throws IOException, InterruptedException {
        return rates.findByTableAndDate(table, date);
    }

    @Override
    public List<String> findAllCodes(Table table) throws IOException, InterruptedException {
        return findAll(table, LocalDate.now()).stream()
                .map(Rate::getCode)
                .collect(Collectors.toList());
    }

}
