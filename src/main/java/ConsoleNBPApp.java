import controller.ConsoleController;
import controller.Menu;
import controller.MenuItem;
import nbpapi.Rate;
import nbpapi.Table;
import repository.RateRepository;
import repository.RateRepositoryNBP;
import repository.RateRepositoryNBPApi;
import repository.RateRepositoryNBPCached;
import service.ServiceNBP;
import service.ServiceNBPApi;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ConsoleNBPApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final RateRepository rates = new RateRepositoryNBPApi();
    private static final ServiceNBP service = new ServiceNBPApi(rates);


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
        System.out.println();
        controller.process();

    }

//    private static void handleOptionTable(Table table) {
//        System.out.println("Type a date: ");
//        String date = scanner.nextLine();
//        try {
//            printTable(service.findAll(table, LocalDate.parse(date)), table);
//        } catch (Exception e) {
//            System.err.println("Connection Error!\n " + e.getMessage());
//        }
//    }
    private static void handleOptionTable(Table table) {
        try {
            printTable(service.findAll(table, LocalDate.now()), table);
        } catch (Exception e) {
            System.err.println("Connection Error!\n " + e.getMessage());
        }
    }

    private static void printTable(List<Rate> list, Table table) {
        System.out.printf("%-45s %5s %5s%n", "Currency name", "Code ISO(4217)", "Mid");
        for (Rate rate : list) {
            switch (table) {
                case TABLE_A, TABLE_B -> System.out.printf("%-45s %5s %15.4f%n", rate.getCurrency(), rate.getCode(), rate.getMid());
                case TABLE_C -> System.out.printf("%-45s %5s %15.4f%n", rate.getCurrency(), rate.getCode(), rate.getBid());
            }
        }
    }


    private static void exchange() {
        printCurrencyCodes();

        System.out.println("Type amount: ");
        double amount = amountValidation();
        scanner.nextLine();

        System.out.println("Type source currency code :");
        String sourceCode = codeValidation();

        System.out.println("Type target currency code:");
        String targetCode = codeValidation();

        try {
            double result = service.calc(amount, sourceCode, targetCode);
            System.out.printf("Result = %.2f\n", result);
        } catch (IOException e) {
            System.err.println("Connection issue");
        } catch (InterruptedException e) {
            System.err.println("Interrupted !");
        }
    }


    private static double amountValidation() {
        double amount;
        boolean flag = true;
        do {
            while (!scanner.hasNextDouble()) {
                System.out.println("Put correct value type!");
                scanner.next();
            }
            amount = scanner.nextDouble();
            if (amount <= 0) {
                System.out.println("Value must be greater then 0!");
            } else {
                flag = false;
            }
        } while (flag);
        return amount;
    }

    private static String codeValidation() {

        try {
            List<String> allCodes = service.findAllCodes(Table.TABLE_A);

            String inputCode = scanner.nextLine();
            Pattern pattern = Pattern.compile("^[A-Z][A-Z][A-Z]$");

            while (!Pattern.matches(pattern.toString(), inputCode) || !allCodes.contains(inputCode)) {
                System.out.println("Choose code from list above.");
                inputCode = scanner.nextLine();
            }
            return inputCode;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("");
        }

    }

    private static void printCurrencyCodes() {
        try {
            List<String> allCodes = service.findAllCodes(Table.TABLE_A);
            System.out.print("Available currencies codes: \n");
            allCodes.stream().sorted().forEach(code-> System.out.print(code+", "));

            System.out.println();
        } catch (IOException e) {
            System.err.println("Empty list of codes problem " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Connection problem: " + e.getMessage());
        }
    }
}
