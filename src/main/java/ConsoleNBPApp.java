import controller.ConsoleController;
import controller.Menu;
import controller.MenuItem;
import nbpapi.Rate;
import nbpapi.Table;
import repository.RateRepository;
import repository.RateRepositoryNBP;
import service.ServiceNBP;
import service.ServiceNBPApi;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class ConsoleNBPApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final RateRepository rates = new RateRepositoryNBP();
    private static final ServiceNBP service = new ServiceNBPApi(rates);

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
            printTable(service.findAll(table, LocalDate.now()), table);

        } catch (Exception e) {
            System.err.println("Connection Error!\n " + e.getMessage());
        }


    }
//TODO input validation and errors interpretation


    private static void exchange() {
        //TODO print code list
//        printCurrencyCodes();
        System.out.println("Type amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.println("Type code currency:");
        String sourceCode = scanner.nextLine();
        System.out.println("Type currency target code:");
        String targetCode = scanner.nextLine();
        try {
            double result = service.calc(amount, sourceCode, targetCode);
            System.out.printf("result = %.2f\n",result);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    //TODO repair that
    private static void printCurrencyCodes(){
        try {
            List<String> allCodes = service.findAllCodes(Table.TABLE_A);
            for (String allCode : allCodes) {
                System.out.println(allCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
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
                                .label("Currency change")
                                .action(ConsoleNBPApp::exchange)
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
