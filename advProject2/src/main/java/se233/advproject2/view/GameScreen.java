package se233.advproject2.view;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import se233.advproject2.objects.Bullet;
import se233.advproject2.objects.Player;

import java.util.ArrayList;
import java.util.List;

public class GameScreen extends Pane {
    public static final int WIDTH = 600;
    public static final int HEIGHT = 700;
    private Canvas canvas;
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
        //
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
    public void renderText(String Text, double x, double y) {
        gc.setFill(Color.BLACK);
        gc.fillText(Text, x , y);
    }
    public void renderHP(int hp , double x, double y)
            throws NullPointerException,IndexOutOfBoundsException {
        gc.setFill(Color.GREEN);
        for (int i = 0; i < hp; i++) {
            gc.fillRect(x-5, y + (i*11), 5, 10);
        }
    }
    public void renderSpecialAmmo(int ammo , double x, double y)
            throws NullPointerException,IndexOutOfBoundsException {
        gc.setFill(Color.BLUE);
        for (int i = 0; i < ammo; i++) {
            gc.fillRect(x+32, y + (i*7), 5, 5);
        }
    }

    /// getting keys pressed
    private List<KeyCode> key = new ArrayList<>();
    public List<KeyCode> getKeys() { return key; }

    public void pressKey(KeyCode key) {
        if (!this.key.contains(key)) { // don't add if already have
            this.key.add(key);
        }
    }

    public void releaseKey(KeyCode key) {
        this.key.remove(key);
    }
    public void reset(){
        Platform.runLater(()->{
            this.getChildren().clear();
            canvas = new Canvas(WIDTH, HEIGHT);
            this.getChildren().add(canvas);
            try {
                gc = canvas.getGraphicsContext2D();
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        });
    }
}
