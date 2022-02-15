package controller;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class MenuItem {
    private String label;
    private Runnable action;
}
