import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import nbpapi.Rate;
import nbpapi.Table;
import repository.RateRepository;
import repository.RateRepositoryNBPCached;
import service.ServiceNBP;
import service.ServiceNBPApi;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FXNBPApp extends Application {

    private final VBox root = new VBox();
    private final Scene scene = new Scene(root, 600, 400);

    private final HBox tablesRow = new HBox(30);
    private final HBox resultsRow = new HBox();
    private final HBox currenciesLabelRow = new HBox(80);
    private final HBox currenciesChooseRow = new HBox(80);

    private final Label amountLabel = new Label("Amount to exchange");
    private final Label titleLabel = new Label("NBP Currency converter ");
    private final Label sourceLabel = new Label("Source currency ");
    private final Label targetLabel = new Label("Target currency ");
    private final Label result = new Label("Result value is: ");

    private final DatePicker datePicker = new DatePicker();
    private final TextField amount = new TextField();
    private final ComboBox<Rate> sourceCode = new ComboBox<>();
    private final ComboBox<Rate> targetCode = new ComboBox<>();
    private final ComboBox<Table> tables = new ComboBox<>();
    private final Button resultButton = buttonCreation();


    private final RateRepository repository = new RateRepositoryNBPCached();
    private final ServiceNBP service = new ServiceNBPApi(repository);

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
        stage.setResizable(false);

        componentsAdd();
        setActions();
        prepareTables();

        stage.show();
    }

    private void componentsAdd() {

        tables.getItems().addAll(Table.values());

        amountLabel.setFont(new Font("Arial",15));
        titleLabel.setFont(new Font("Arial", 24));
        resultButton.setMaxSize(25,25);


        currenciesLabelRow.getChildren().addAll(sourceLabel,targetLabel);
        currenciesLabelRow.setAlignment(Pos.TOP_CENTER);

        currenciesChooseRow.getChildren().addAll(sourceCode,targetCode);
        currenciesChooseRow.setAlignment(Pos.TOP_CENTER);

        tablesRow.getChildren().addAll(tables,datePicker );
        tablesRow.setAlignment(Pos.TOP_CENTER);


        resultsRow.getChildren().addAll(resultButton, result);
        resultsRow.setAlignment(Pos.TOP_CENTER);

        root.setSpacing(10);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(
                titleLabel,
                amountLabel,
                amount,
                tablesRow,
                currenciesLabelRow,
                currenciesChooseRow,
                resultsRow);
        root.setAlignment(Pos.TOP_CENTER);

        resultButton.setDisable(true);

    }
    //TODO reapair this
        private Button buttonCreation(){

        FileInputStream input = null;
        try {
            input = new FileInputStream("resources/calculator.png");
        } catch (FileNotFoundException e) {
            System.err.println("Invalid button image path!");
        }
        Image image = new Image(input);
        ImageView imageView = new ImageView(image);
        return new Button("Calculate ",imageView);

    }
    private void setActions() {
        amount.textProperty().addListener((observable, oldValue, newValue) -> {
            resultButton.setDisable(
                    !isValidInput(newValue) ||
                            sourceCode.getSelectionModel().getSelectedItem() == null ||
                            targetCode.getSelectionModel().getSelectedItem() == null);
        });

        sourceCode.setOnAction(actionEvent -> buttonUnlock());
        targetCode.setOnAction(actionEvent -> buttonUnlock());

        resultButton.setOnAction(actionEvent -> calcExchangeResult());
        tables.setOnAction(actionEvent -> setTable());
    }

    private boolean isValidInput(String amount) {
        if ("".equals(amount)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[0-9]{0,13}\\.?[0-9]{0,10}$");
        Matcher matcher = pattern.matcher(amount);
        return matcher.matches();
    }

    private void buttonUnlock() {

        resultButton.setDisable(true);
        if (sourceCode.getSelectionModel().getSelectedItem() != null &&
                targetCode.getSelectionModel().getSelectedItem() != null &&
                isValidInput(amount.getText())
        ) {
            if (!"".equals(amount.getText()))
                resultButton.setDisable(false);
        }
    }

    private void calcExchangeResult() {

        double input = Double.parseDouble(amount.getText());


        if (tables.getSelectionModel().getSelectedItem().equals(Table.TABLE_C)) {
            double source = sourceCode.getSelectionModel().getSelectedItem().getAsk();
            double target = targetCode.getSelectionModel().getSelectedItem().getAsk();
            double ask = input * source / target;

            source = sourceCode.getSelectionModel().getSelectedItem().getBid();
            target = targetCode.getSelectionModel().getSelectedItem().getBid();

            double bid = input * source / target;

            result.setText(String.format("Sell value: %6.2f\n Buy value: %6.2f", ask, bid));
        } else {
            double source = sourceCode.getSelectionModel().getSelectedItem().getMid();
            double target = targetCode.getSelectionModel().getSelectedItem().getMid();
            double output = input * source / target;
            result.setText(String.format("Middle value: %6.2f", output));
        }
    }

    private void prepareTables() {
        tables.getSelectionModel().select(0);
        setTable();


        sourceCode.getSelectionModel().select(0);
        targetCode.getSelectionModel().select(0);
    }

    private void setTable() {

        Table table = tables.getValue();

        sourceCode.getItems().clear();
        targetCode.getItems().clear();
        resultButton.setDisable(true);
        result.setText("0,0");

        try {

            List<Rate> ratesSorted = service.findAll(table).stream()
                    .sorted(Comparator.comparing(Rate::getCode))
                    .toList();

            sourceCode.getItems().addAll(ratesSorted);
            targetCode.getItems().addAll(ratesSorted);

            sourceCode.getSelectionModel().select(0);
            targetCode.getSelectionModel().select(0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
