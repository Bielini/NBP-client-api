package service;

import nbpapi.Rate;
import nbpapi.Table;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface ServiceNBP {

    double calc(double amount, String source ,String target) throws IOException, InterruptedException;
    List<Rate> findAll(Table table, LocalDate date) throws IOException, InterruptedException;
    List<String> findAllCodes(Table table) throws IOException, InterruptedException;
}
