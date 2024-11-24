package se233.advproject2.objects;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import se233.advproject2.Launcher;
import se233.advproject2.controller.GameLoop;
import se233.advproject2.model.AnimatedSprite;
import se233.advproject2.view.GameScreen;

import java.util.ConcurrentModificationException;
import java.util.Objects;

public class Collectibles extends Pane {
    // controller access
    GameLoop game = GameLoop.getInstance();
    GameScreen platform = game.getPlatform();
    // initialise variables
    Image characterImg;
    AnimatedSprite imageView;
    String spritePath;
    double x, y, direction, speed;
    // enum
    enum TYPE {
        health, scatterGun, homingGun
    }
    TYPE type;
    /// constructor //
    public Collectibles(double x, double y, double direction, double speed){
        this.x = x;
        this.y = y;
        Platform.runLater(() -> {
            // set rendering
            setTranslateX(x - 16);
            setTranslateY(y - 16);
        });
        // setting up (randomly)
        String spritePath;
        switch ((int)Math.floor(Math.random()*3)){
            default -> {
                type = TYPE.health;
                // sprite
                spritePath = "assets/Collectible_heal-Sheet.png";
            }
            case 1 -> {
                type = TYPE.scatterGun;
                // sprite
                spritePath = "assets/Collectible_scatter-Sheet.png";
            }
            case 2 -> {
                type = TYPE.homingGun;
                // sprite
                spritePath = "assets/Collectible_homing-Sheet.png";
            }
        }
        this.speed = speed;
        this.direction = direction;
        this.characterImg = new Image(Objects.requireNonNull(Launcher.class.getResourceAsStream(spritePath)));
        int size = 32;
        this.imageView = new AnimatedSprite(characterImg,2,2,1,0,0,size,size,500);
        this.imageView.setFitWidth(size);
        this.imageView.setFitHeight(size);
        this.getChildren().addAll(this.imageView);
        // Use Platform.runLater to update rendering entity immediately upon creation
        Platform.runLater(() -> {
            // Add the entity to the platform's children
            platform.getChildren().addAll(this);
            game.getCollectiblesList().add(this);
        });
    }

    /// methods //

    public void step(){
        move();
        collision();
    }
    private void move() throws ConcurrentModificationException {
        // handling
        double angleRad = Math.toRadians(direction);
        double horizontalSpeed = Math.cos(angleRad) * speed;
        double verticalSpeed = Math.sin(angleRad) * speed;
        // update pos
        x += horizontalSpeed;
        y -= verticalSpeed;
        Platform.runLater(() -> {
            // set rendering
            setTranslateX(x - 16);
            setTranslateY(y - 16);
        });
    }
    private void collision() throws ConcurrentModificationException {
        Player ent = game.player;
        int buffer = 10;
        boolean checkinX = (this.x > ent.getX() - buffer && this.x < ent.getX() + ent.getSize() + buffer);
        boolean checkinY = (this.y > ent.getY() && this.y < ent.getY() + ent.getSize());
        if(checkinX && checkinY){
            // give effect to player
            switch (type){
                case health -> game.player.heal();
                case scatterGun -> game.player.setScatterShot();
                case homingGun -> game.player.setHomingShot();
            }
            // instance destroy
            removeSelf();
        }
    }
    private void removeSelf(){
        // remove from update list
        game.getCollectiblesList().remove(this);
        Platform.runLater(() -> {
            // Remove from the platform's children (drawing)
            platform.getChildren().remove(this);
        });
    }
}
