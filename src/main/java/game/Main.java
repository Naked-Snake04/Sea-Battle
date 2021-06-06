package game;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;


import java.util.Optional;
import java.util.Random;

public class Main extends Application {
    private boolean running = false;
    private Field enemyField, playerField;
    private int shipsToPlace = 10;
    private int type = 0;
    private boolean enemyTurn;

    private Random random = new Random();

    Label label;

    private Parent createContent(){ //создаём К О Н Т Е Н Т
        BorderPane root = new BorderPane();
        label = new Label("Player");
        label.setFont(Font.font("Tahoma", FontWeight.NORMAL,20));
        Label labelcom = new Label("Computer");
        labelcom.setFont(Font.font("Tahoma", FontWeight.NORMAL,20));
        root.setPrefSize(1024,768);
        //создаём MenuBar
        MenuBar menuBar = new MenuBar();

        //Создаём меню
        Menu fileMenu = new Menu("File");
        Menu helpMenu = new Menu("Help");

        //Создаём кнопки для MenuItems
        //Для File
        MenuItem changeNickname = new MenuItem("Change Nickname");
        MenuItem exit = new MenuItem("Exit");
        //Для Help
        MenuItem rules = new MenuItem("Rules");
        MenuItem controls = new MenuItem("Controls");

        //Добавляем все MenuItem в нужные отделы Menu
        fileMenu.getItems().addAll(changeNickname,new SeparatorMenuItem(),exit);
        helpMenu.getItems().addAll(rules,new SeparatorMenuItem(),controls);

        //Добавляем Menu в MenuBar
        menuBar.getMenus().addAll(fileMenu,helpMenu);

        //Добавляем функционал кнопок меню
        //Поменять Ник
        changeNickname.setAccelerator(KeyCombination.keyCombination("N"));
        changeNickname.setOnAction((ActionEvent e)->{
            textInputDialog();
        });
        //Выход из игры
        exit.setAccelerator(KeyCombination.keyCombination("ESC"));
        exit.setOnAction((ActionEvent e)->{
            Platform.exit();
        });
        //Правила игры
        rules.setAccelerator(KeyCombination.keyCombination("F1"));
        rules.setOnAction((ActionEvent e)->{
            rulesGame();
        });
        //Управление
        controls.setAccelerator(KeyCombination.keyCombination("H"));
        controls.setOnAction((ActionEvent e)->{
            controlInfo();
        });
        enemyField = new Field (true, event -> { //создаём вражеское поле
            if (!running)
                return;

            Cell cell = (Cell) event.getSource();
            if (cell.wasShot)
                return;

            enemyTurn = !cell.shoot();

            if (enemyField.ships==0){
                resultWin();
            }

            if (enemyTurn)
                enemyMove();
        });

        playerField = new Field(false, event -> { //создаём поле игрока
            if (running)
                return;
            Cell cell = (Cell) event.getSource();
            if (playerField.placeShip(new Ship(findType(shipsToPlace), event.getButton() == MouseButton.PRIMARY), cell.x, cell.y)){
                if (--shipsToPlace == 0){
                    shipsToPlace = 10; //чтобы у компа тоже были корабли
                    startGame();
                }
            }
        });

        HBox hBox = new HBox(50, label, playerField,enemyField, labelcom);
        hBox.setAlignment(Pos.CENTER);
        root.setTop(menuBar);
        root.setCenter(hBox);
        return root;
    }

    public int findType(int i){ //определяет, какой тип корабля ставить
        int type = 0;
        switch (i){
            case 10: type = 4;
            break;
            case 9: case 8:
                type = 3;
                break;
            case 7: case 6: case 5:
                type = 2;
                break;
            case 4: case 3: case 2: case 1:
                type = 1;
                break;
        }
        return type;
    }

    private void enemyMove(){ //ход врага - реализован на рандоме
        while (enemyTurn){
            int x = random.nextInt(10);
            int y = random.nextInt(10);

            Cell cell = playerField.getCell(x, y);
            if (cell.wasShot)
                continue;

            enemyTurn = cell.shoot();

            if (playerField.ships==0){
                resultLose();
            }
        }
    }

    private void startGame(){ //расстановка кораблей компьютера и начало игры
        while (shipsToPlace > 0){
            int x = random.nextInt(10);
            int y = random.nextInt(10);

            if (enemyField.placeShip(new Ship(findType(shipsToPlace), Math.random() < 0.5), x, y)){
                shipsToPlace--;
            }
        }
        running = true;
    }

   private void textInputDialog(){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Change Nickname");
        dialog.setContentText("Please enter your Nickname:");
        dialog.getDialogPane().setMinSize(500,200);
        dialog.setHeaderText(null);
        dialog.setGraphic(null);
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent())
            label.setText(result.get());
        else
            label.setText("Player");
    }

    private void rulesGame(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Rules of Game");
        alert.setGraphic(null);
        alert.setHeaderText("Rules of Game");
        alert.setContentText("The game is played on four grids, two for each player. The grids are typically square and the individual squares in the grid are identified by letter and number. In this application not released letters and numbers. On one grid the player arranges ships and records the shots by the opponent. On the other grid the player records their own shots.\n" +
                "\n" +
                "Before play begins, each player secretly arranges their ships on their primary grid. Each ship occupies a number of consecutive squares on the grid, arranged either horizontally or vertically. The number of squares for each ship is determined by the type of the ship. The ships cannot overlap (i.e., only one ship can occupy any given square in the grid). The types and numbers of ships allowed are the same for each player. These may vary depending on the rules.\n" +
                "\n" +
                "There's 10 ships. Types: 4 with 1 health; 3 with 2 health; 2 with 3 health; 1 with 4 health. You play versus computer.");
        alert.showAndWait();
    }

    private void controlInfo() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Control of Game");
        alert.setHeaderText("Help with Control");
        alert.setContentText("Press the LEFT MOUSE BUTTON to position the ship vertically.\n" +
                "Press the RIGHT MOUSE BUTTON to position the ship horizontally.\n"+
                "\n"+
                "When you have placed all your ships, press the LEFT MOUSE BUTTON on the enemy field to find enemy ships.\n"+
                "\n"+
                "You can change your Nickname in File -> Change Nickname(or N).\n"+
                "\n"+
                "You can exit in File -> Exit (or ESC).\n"+
                "\n"+
                "Press F1 to read the rules.");
        alert.showAndWait();
    }
    private void resultWin(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Game Result");
        alert.setGraphic(null);
        alert.setHeaderText("YOU WON!");
        alert.setContentText("Press OK, to exit.\nPress Cancel, to cancel this window");
        alert.getDialogPane().setMinSize(600,200);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK)
            Platform.exit();
    }
    private void resultLose(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Game Result");
        alert.setGraphic(null);
        alert.setHeaderText("YOU LOSE!");
        alert.setContentText("Press OK, to exit.\nPress Cancel, to cancel this window");
        alert.getDialogPane().setMinSize(600,200);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK)
            Platform.exit();
    }

    @Override
    public void start(Stage primaryStage) {

        Scene scene = new Scene(createContent());
        primaryStage.setTitle("Sea Battle");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main (String[] args){
        launch(args);
    }
}
