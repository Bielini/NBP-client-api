import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import nbpapi.Rate;
import nbpapi.Table;
import repository.RateRepository;
import repository.RateRepositoryNBPCached;
import service.ServiceNBP;
import service.ServiceNBPApi;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class FXNBPApp extends Application {

    private final VBox root = new VBox();
    private final HBox comboboxiesRow = new HBox();
    private final HBox resultsRow = new HBox();
    private final Scene scene = new Scene(root, 600, 400);
    private final Label amountLabel = new Label("Amount to exchange");
    private final TextField amount = new TextField();
    private final ComboBox<Rate> sourceCode = new ComboBox<>();
    private final ComboBox<Rate> targetCode = new ComboBox<>();
    private final ComboBox<Table> tables = new ComboBox<>();
    private final Button resultButton = new Button("Calculate");
    private final Button tableChange = new Button("Change Table");
    private final Label result = new Label("0,0");

    private RateRepository repository = new RateRepositoryNBPCached();
    private ServiceNBP service = new ServiceNBPApi(repository);

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        builtGUI(stage);
    }


    private void builtGUI(Stage stage) {

        comboboxiesRow.getChildren().addAll(tables, sourceCode, targetCode, tableChange);
        comboboxiesRow.setAlignment(Pos.TOP_CENTER);

        tables.getSelectionModel().selectFirst();

        resultsRow.getChildren().addAll(resultButton, result);
        resultsRow.setAlignment(Pos.TOP_CENTER);

        resultButton.setOnAction(actionEvent -> calcExchangeResult());
        tableChange.setOnAction(actionEvent -> tableChangeExecute());
        root.setSpacing(10);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(amountLabel, amount, comboboxiesRow, resultsRow);
        root.setAlignment(Pos.TOP_CENTER);

        try {
            sourceCode.getItems().addAll(service.findAll(Table.TABLE_A, LocalDate.now()));
            targetCode.getItems().addAll(service.findAll(Table.TABLE_A, LocalDate.now()));
            tables.getItems().addAll(Table.values());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        stage.setScene(scene);
        stage.setTitle("NBP currencies");

        stage.show();
    }

    private void calcExchangeResult() {

        double input = Double.parseDouble(amount.getText());
        double source = sourceCode.getSelectionModel().getSelectedItem().getMid();
        double target = targetCode.getSelectionModel().getSelectedItem().getMid();

        double output = input * source / target;

        result.setText(String.format("%6.2f", output));
    }



    private void tableChangeExecute() {

        Table table = tables.getValue();
        sourceCode.getItems().clear();
        targetCode.getItems().clear();
        try {

            sourceCode.getItems().addAll(service.findAll(table));
            targetCode.getItems().addAll(service.findAll(table));


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
