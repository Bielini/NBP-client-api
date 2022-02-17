package repository;

import jdk.jshell.spi.ExecutionControl;
import nbpapi.Rate;
import nbpapi.Table;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface RateRepository {
    List<Rate> findByTableAndDate(Table table, LocalDate date) throws IOException, InterruptedException;
    List<Rate> findByTableLast(Table table) throws IOException, InterruptedException;
}
