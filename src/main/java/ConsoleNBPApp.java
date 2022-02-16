import controller.ConsoleController;
import controller.Menu;
import controller.MenuItem;
import nbpapi.Rate;
import nbpapi.RateTable;
import nbpapi.Table;
import nbpapi.URIGenerator;
import repository.ApiRepository;
import repository.RateRepository;
import repository.RateRepositoryNBP;
import repository.RateRepositoryNBPApi;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class ConsoleNBPApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final RateRepository rates = new RateRepositoryNBP();


    private static void printTable(List<Rate> list, Table table) {
        System.out.printf("%-35s %5s %5s%n", "Currency name", "Code ISO(4217)", "Mid");
        for (Rate rate : list) {
            switch (table) {
                case TABLE_A, TABLE_B -> System.out.printf("%-35s %5s %15.4f%n", rate.getCurrency(), rate.getCode(), rate.getMid());
                case TABLE_C -> System.out.printf("%-35s %5s %15.4f%n", rate.getCurrency(), rate.getCode(), rate.getBid());
            }
        }
        System.out.println();
    }

    private static void handleOptionTable(Table table) {
        try {
            printTable(rates.findByTableAndDate(table, LocalDate.now()), table);

        } catch (Exception e) {
            System.err.println("Connection Error!\n " + e.getMessage());
        }


    }

    public static void main(String[] args) {

        Menu menu = Menu.builder()
                .items(List.of(
                        MenuItem.builder()
                                .label("Download table A")
                                .action(() -> {
                                    handleOptionTable(Table.TABLE_A);
                                })
                                .build(),
                        MenuItem.builder()
                                .label("Download table B")
                                .action(() ->
                                    handleOptionTable(Table.TABLE_B)
                                )
                                .build(),
                        MenuItem.builder()
                                .label("Download table C")
                                .action(() -> {
                                    handleOptionTable(Table.TABLE_C);
                                })
                                .build(),
                        MenuItem.builder()
                                .label("Exit")
                                .action(() -> {
                                    System.exit(0);
                                })
                                .build()
                ))
                .build();
        ConsoleController controller = new ConsoleController(scanner, menu);
        controller.process();

    }
}
