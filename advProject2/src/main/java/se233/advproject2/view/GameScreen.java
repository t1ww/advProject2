package se233.advproject2.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import se233.advproject2.objects.Bullet;
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
    GraphicsContext gc;
    public GameScreen() {
        this.setHeight(HEIGHT);
        this.setWidth(WIDTH);
        canvas = new Canvas(WIDTH, HEIGHT);
        this.getChildren().add(canvas);
        try {
            gc = canvas.getGraphicsContext2D();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
    public void renderReset() throws NullPointerException, IndexOutOfBoundsException {
        gc.clearRect(0,0,WIDTH,HEIGHT);
    }
    public void renderBullets(List<Bullet> ent) // render bullet list
            throws NullPointerException, IndexOutOfBoundsException {
        for (Bullet e : ent) {
            gc.setFill(Color.BLACK);
            gc.fillRect(e.getX()-2 , e.getY()-2 , 5, 5);
        }
    }
    public void render(Color color, double x, double y, int s) // render one ent
            throws NullPointerException, IndexOutOfBoundsException {
        // will be changed to sprite rendering
        gc.setFill(color);
        gc.fillRect(x, y, s, s);
    }
    public void renderText(String Text, int x, int y) {
        gc.setFill(Color.BLACK);
        gc.fillText(Text, x , y);
    }
    public void renderHP(int hp)
            throws NullPointerException,IndexOutOfBoundsException {
        gc.setFill(Color.BLACK);
        gc.fillText("HP : " + hp + "/3", 20 ,50);
        gc.setFill(Color.GREEN);
        for (int i = 0; i < hp; i++) {
            gc.fillRect(50 + (25*i), 50, 20, 10);
        }

    }
    public List<KeyCode> getKeys() { return key; }
    KeyCode keyPressed;
    public KeyCode getKeyPressed() throws NullPointerException {
        if(!key.isEmpty() && keyPressed != key.get(key.size()-1)) {
            keyPressed = key.get(key.size()-1);
            return keyPressed; // return this if haven't returned this keycode
        }else return null; // else don't return
        // reset on release
    }

    public void pressKey(KeyCode key) {
        if (!this.key.contains(key)) { // don't add if already have
            this.key.add(key);
        }
    }

    public void releaseKey(KeyCode key) {
        this.key.remove(key);
        keyPressed = null;
    }
}
