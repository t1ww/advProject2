package se233.advproject2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import se233.advproject2.controller.GameLoop;
import se233.advproject2.view.GameScreen;

public class Launcher extends Application {
    @Override
    public void start(Stage stage) {
        GameScreen p = new GameScreen();
        GameLoop gameLoop = new GameLoop(p);
        Scene scene = new Scene(p, GameScreen.WIDTH* GameScreen.TILE_SIZE, GameScreen.HEIGHT * GameScreen.TILE_SIZE);
        scene.setOnKeyPressed(event-> p.setKey(event.getCode()));
        scene.setOnKeyReleased(event -> p.setKey(null));
        stage.setTitle("Shooter game");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        (new Thread(gameLoop)).start();
    }

    public static void main(String[] args) {
        launch();
    }
}
