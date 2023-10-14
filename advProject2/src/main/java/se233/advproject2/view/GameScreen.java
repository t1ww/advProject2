package se233.advproject2.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import se233.advproject2.objects.Enemy;
import se233.advproject2.objects.Entity;
import se233.advproject2.objects.Player;

import java.util.ArrayList;
import java.util.List;

public class GameScreen extends Pane {
    public static final int WIDTH = 600;
    public static final int HEIGHT = 700;
    private Canvas canvas;
    private List<KeyCode> key = new ArrayList<>();
    public GameScreen() {
        this.setHeight(HEIGHT);
        this.setWidth(WIDTH);
        canvas = new Canvas(WIDTH, HEIGHT);
        this.getChildren().add(canvas);

    }
    public void render(List<Entity> ent, int score) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0,0,WIDTH,HEIGHT);
        for (Entity e : ent) {
            if (e.getClass().equals(Player.class)) {
                gc.setFill(Color.BLUE);
            }else if(e.getClass().equals(Enemy.class)){
                gc.setFill(Color.RED);
            }
            gc.fillRect(e.getX() , e.getY() , e.getSize(), e.getSize());
        }
        gc.fillText("score : " + score , 10 , 10);
    }
    public List<KeyCode> getKeys() { return key; }
    public void pressKey(KeyCode key) {
        if (!this.key.contains(key)) { // don't add if already have
            this.key.add(key);
        }
    }
    public void releaseKey(KeyCode key) { this.key.remove(key); }
}
