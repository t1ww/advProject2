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
    public GameScreen() {
        this.setHeight(HEIGHT);
        this.setWidth(WIDTH);
        canvas = new Canvas(WIDTH, HEIGHT);
        this.getChildren().add(canvas);

    }
    public void renderReset(){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0,0,WIDTH,HEIGHT);
    }
    public void renderBullets(List<Bullet> ent) // render bullet list
            throws NullPointerException,IndexOutOfBoundsException {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        for (Bullet e : ent) {
            gc.setFill(Color.BLACK);
            gc.fillRect(e.getX()-2 , e.getY() , 5, 20);
        }
    }
    public void render(Entity ent) // render one ent
            throws NullPointerException,IndexOutOfBoundsException {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        if (ent.getClass().equals(Player.class)) {
            gc.setFill(Color.BLUE);
        } else if (ent.getClass().equals(Enemy.class)) {
            gc.setFill(Color.RED);
        }
        gc.fillRect(ent.getX(), ent.getY(), ent.getSize(), ent.getSize());
    }
    public void render(List<Entity> ent) // render ent list
            throws NullPointerException,IndexOutOfBoundsException{
        GraphicsContext gc = canvas.getGraphicsContext2D();
        for (Entity e : ent) {
            if (e.getClass().equals(Player.class)) {
                gc.setFill(Color.BLUE);
            }else if(e.getClass().equals(Enemy.class)){
                gc.setFill(Color.RED);
            }
            gc.fillRect(e.getX() , e.getY() , e.getSize(), e.getSize());
        }
    }
    public void renderScore(int score){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillText("score : " + score , 10 , 10);
    }
    public void renderText(String Text, int x, int y) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillText(Text, x , y);
    }
    public List<KeyCode> getKeys() { return key; }
    public void pressKey(KeyCode key) {
        if (!this.key.contains(key)) { // don't add if already have
            this.key.add(key);
        }
    }

    public void releaseKey(KeyCode key) { this.key.remove(key); }

    public void renderHP(int hp)
            throws NullPointerException,IndexOutOfBoundsException {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.GREEN);
        int size = 32;
        for (int i = 0; i < hp; i++) {
            gc.fillRect(50 + (50*i), 50, size, size);
        }

    }
}
