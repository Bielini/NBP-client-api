package exercises;

import menu_controller.ConsoleController;
import menu_controller.Menu;
import menu_controller.MenuItem;

import java.util.List;
import java.util.Scanner;

public class ConsoleAppCalc {

    private static final Scanner scanner = new Scanner(System.in);


    public static void main(String[] args) {

        Menu menu = Menu.builder()
                .items(List.of(
                        MenuItem.builder()
                                .label("Summary")
                                .action(() -> {
                                    System.out.println("123 + 456 = " + (123 + 456));
                                })
                                .build(),
                        MenuItem.builder()
                                .label("Multiplication")
                                .action(() -> {
                                    System.out.println("23 * 67 = " + 23 * 67);
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
