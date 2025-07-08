package se233.advproject2;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import se233.advproject2.controller.DrawingLoop;
import se233.advproject2.controller.GameLoop;
import se233.advproject2.view.GameScreen;

public class Launcher extends Application {
    @Override
    public void start(Stage stage) {
        // Initialize Log4j 3 using the configuration file
        System.setProperty("log4j.configurationFile", "log4j2.yaml");
        // create game controllers  
        GameScreen p = new GameScreen();
        GameLoop gameLoop = new GameLoop(p);
        gameLoop.setInstance(gameLoop);
        DrawingLoop drawingLoop = new DrawingLoop(gameLoop);
        // scene
        Scene scene = new Scene(p, GameScreen.WIDTH, GameScreen.HEIGHT);
        scene.setOnKeyPressed(event-> p.pressKey(event.getCode()));
        scene.setOnKeyReleased(event -> p.releaseKey(event.getCode()));
        // stage
        stage.setTitle("Shooter game");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setOnCloseRequest(e -> {
            Platform.runLater(stage::close);
        });
        stage.show();
        // thread
        (new Thread(gameLoop)).start();
        (new Thread(drawingLoop)).start();
    }

    public static void main(String[] args) {
        launch();
    }
}
