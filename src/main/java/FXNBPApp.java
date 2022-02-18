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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FXNBPApp extends Application {

    private final VBox root = new VBox();
    private final HBox comboboxiesRow = new HBox();
    private final HBox resultsRow = new HBox();
    private final Scene scene = new Scene(root, 600, 400);
    private final Label amountLabel = new Label("Amount to exchange");
    private final Label titleLabel = new Label("NBP Currency converter ");
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
        stage.setScene(scene);
        stage.setTitle("NBP currencies");
        componentsAdd();


        resultButton.setDisable(true);
        amount.textProperty().addListener((observable, oldValue, newValue) -> {
            resultButton.setDisable(
                    !isValidInput(newValue)  ||
                    sourceCode.getSelectionModel().getSelectedItem() == null ||
                    targetCode.getSelectionModel().getSelectedItem() == null);
        });
//
        sourceCode.setOnAction(actionEvent -> buttonUnlock());
        targetCode.setOnAction(actionEvent -> buttonUnlock());

        resultButton.setOnAction(actionEvent -> calcExchangeResult());
        tableChange.setOnAction(actionEvent -> setTable());



        tables.getSelectionModel().select(0);
        setTable();
        sourceCode.getSelectionModel().select(0);
        targetCode.getSelectionModel().select(0);
        stage.show();
    }

    private void componentsAdd() {
        tables.getItems().addAll(Table.values());

        comboboxiesRow.getChildren().addAll(tables, sourceCode, targetCode, tableChange);
        comboboxiesRow.setAlignment(Pos.TOP_CENTER);


        resultsRow.getChildren().addAll(resultButton, result);
        resultsRow.setAlignment(Pos.TOP_CENTER);

        root.setSpacing(10);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(amountLabel, amount, comboboxiesRow, resultsRow);
        root.setAlignment(Pos.TOP_CENTER);

    }

    private void buttonUnlock() {

        resultButton.setDisable(true);
        if (sourceCode.getSelectionModel().getSelectedItem() != null &&
                targetCode.getSelectionModel().getSelectedItem() != null &&
                isValidInput(amount.getText())
        ) {
            if(!"".equals(amount.getText()))
            resultButton.setDisable(false);
        }
    }

    private boolean isValidInput(String amount) {
        if("".equals(amount)){
            return false;
        }
        Pattern pattern = Pattern.compile("^[0-9]{0,13}\\.?[0-9]{0,2}$");
        Matcher matcher = pattern.matcher(amount);
        return matcher.matches();
    }

    private void calcExchangeResult() {

        double input = Double.parseDouble(amount.getText());
        double source = sourceCode.getSelectionModel().getSelectedItem().getMid();
        double target = targetCode.getSelectionModel().getSelectedItem().getMid();

        double output = input * source / target;

        result.setText(String.format("%6.2f", output));
    }


    private void setTable() {


        Table table = tables.getValue();

        sourceCode.getItems().clear();
        targetCode.getItems().clear();
        resultButton.setDisable(true);
        result.setText("0,0");
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
