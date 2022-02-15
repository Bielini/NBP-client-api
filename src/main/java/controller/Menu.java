package controller;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class Menu {

    private List<MenuItem> items;

    public void print() {
        for (int i = 0; i < items.size(); i++) {
            System.out.println((i + 1) + ". " + items.get(i).getLabel());
        }
    }

    public void run(int option) {
        items.get(option - 1).getAction().run();
    }

    public boolean isValidOption(int option) {
        return option <= items.size() && option >0;
    }
}
