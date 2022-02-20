import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import nbpapi.Rate;
import nbpapi.Table;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import repository.RateRepository;
import repository.RateRepositoryNBPCached;
import service.ServiceNBP;
import service.ServiceNBPApi;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FXNBPApp extends Application {

    private final VBox root = new VBox();
    private final Scene scene = new Scene(root, 600, 400);

    private final HBox tablesRow = new HBox(30);
    private final HBox resultsRow = new HBox(40);
    private final HBox currenciesLabelRow = new HBox(80);
    private final HBox currenciesChooseRow = new HBox(80);

    private final Label amountLabel = new Label("Amount to exchange");
    private final Label titleLabel = new Label("NBP Currency converter ");
    private final Label sourceLabel = new Label("Source currency ");
    private final Label targetLabel = new Label("Target currency ");
    private final Label result = new Label();

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

        amountLabel.setFont(new Font("Arial", 15));
        result.setFont(new Font("Arial", 15));

        titleLabel.setFont(new Font("Arial", 24));
        amount.setFont(Font.font("Verdana", FontWeight.BOLD, 20));

        currenciesLabelRow.getChildren().addAll(sourceLabel, targetLabel);
        currenciesLabelRow.setAlignment(Pos.TOP_CENTER);

        currenciesChooseRow.getChildren().addAll(sourceCode, targetCode);
        currenciesChooseRow.setAlignment(Pos.TOP_CENTER);

        tablesRow.getChildren().addAll(tables, datePicker);
        tablesRow.setAlignment(Pos.TOP_CENTER);


        resultsRow.getChildren().addAll(resultButton, result);
        resultsRow.setAlignment(Pos.TOP_CENTER);

        root.setSpacing(10);
        root.setPadding(new Insets(50));
        root.getChildren().addAll(
                titleLabel,
                amountLabel,
                amount,
                tablesRow,
                currenciesLabelRow,
                currenciesChooseRow,
                resultsRow);
        root.setAlignment(Pos.TOP_CENTER);
        setBackground();
        resultButton.setPrefSize(150, 70);
        result.setPrefWidth(220);
        resultButton.setDisable(true);

    }

    private void setBackground(){

        FileInputStream input = null;
        try {
            input = new FileInputStream("resources/background.png");
        } catch (FileNotFoundException e) {
            System.err.println("Invalid background image path!");
        }
        assert input != null;

        Image img = new Image(input);
        BackgroundImage bImg = new BackgroundImage(img,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        Background bGround = new Background(bImg);
        root.setBackground(bGround);
    }

    private Button buttonCreation() {
        FileInputStream input = null;
        try {
            input = new FileInputStream("resources/calculator.png");
        } catch (FileNotFoundException e) {
            System.err.println("Invalid button image path!");
        }
        assert input != null;
        Image image = new Image(input);
        ImageView imageView = new ImageView(image);
        return new Button("Calculate ", imageView);

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

        BigDecimal input = BigDecimal.valueOf(Double.parseDouble(amount.getText()));


        if (tables.getSelectionModel().getSelectedItem().equals(Table.TABLE_C)) {
            BigDecimal source = sourceCode.getSelectionModel().getSelectedItem().getAsk();
            BigDecimal target = targetCode.getSelectionModel().getSelectedItem().getAsk();
            BigDecimal ask = input.multiply(source.divide(target,RoundingMode.HALF_EVEN));

            source = sourceCode.getSelectionModel().getSelectedItem().getBid();
            target = targetCode.getSelectionModel().getSelectedItem().getBid();

            BigDecimal bid = input.multiply(source.divide(target,RoundingMode.HALF_EVEN));

            result.setText(String.format("Sell value: \n%6.2f\nBuy value: \n%6.2f", ask, bid));
        } else {
            BigDecimal source = sourceCode.getSelectionModel().getSelectedItem().getMid();
            BigDecimal target = targetCode.getSelectionModel().getSelectedItem().getMid();
            BigDecimal mid = input.multiply(source.divide(target,RoundingMode.HALF_EVEN));
            result.setText(String.format("Middle value:\n%6.2f", mid));
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

        if(Table.TABLE_C.equals(table)){
            result.setText("Sell value: \n\nBuy value: ");
        }else {
            result.setText("Middle value: ");
        }
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
