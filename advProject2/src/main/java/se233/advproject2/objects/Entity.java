package se233.advproject2.objects;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import se233.advproject2.Launcher;
import se233.advproject2.controller.GameLoop;
import se233.advproject2.view.AnimatedSprite;
import se233.advproject2.view.GameScreen;

import java.util.Objects;

public class Entity extends Pane {
    GameLoop game = GameLoop.Instance;
    GameScreen platform = GameLoop.Instance.platform;
    String name;
    // init variables
    public boolean dead = false;
    public int hp;
    double x, y;
    int size;
    Color sprite = Color.BLACK;
    Image characterImg;
    AnimatedSprite imageView;
    // default
    String spritePath = "assets/prefabSprite.png";
    public Entity(double x, double y, int size, String spritePath){
        this.x = x - ((double) size /2);
        this.y = y;
        this.size = size;
        this.name = "entity";
        this.hp = 1;
        this.spritePath = spritePath;
        this.characterImg = new Image(Objects.requireNonNull(Launcher.class.getResourceAsStream(this.spritePath)));
        this.imageView = new AnimatedSprite(characterImg,2,2,1,0,0,this.size,this.size);
        this.imageView.setFitWidth(this.size);
        this.imageView.setFitHeight(this.size);
        this.getChildren().addAll(this.imageView);
        // Use Platform.runLater to update rendering entity immediately upon creation
        Platform.runLater(() -> {
            // Add the entity to the platform's children
            platform.getChildren().addAll(this);
        });
    }


    public void step(){}
    public double getX() { return x; }
    public double getY() { return y; }
    public int getSize() { return size;}

    public void setX(double _x) { x = _x; }
    public void setY(double _y) { y = _y; }
    public void repaint(){
        Platform.runLater(() -> {
            setTranslateX(x);
            setTranslateY(y);
        });
    }
    public void hurt(int dmg){
        hp -= dmg;
        if(hp <= 0){
            dead = true;
            System.out.println(name + " is dead");
        }else System.out.println(name + " now has " + hp + " hp");
    }
}
