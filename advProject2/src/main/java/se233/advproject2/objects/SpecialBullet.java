package se233.advproject2.objects;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import se233.advproject2.Launcher;
import se233.advproject2.controller.GameLoop;
import se233.advproject2.model.AnimatedSprite;
import se233.advproject2.view.GameScreen;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Objects;

public class SpecialBullet extends Bullet {
    int size = 32;
    public SpecialBullet(String sprPath, double x, double y, double direction, double speed, Class checkFor) {
        super(x, y, direction, speed, checkFor);
    }
    public SpecialBullet(String sprPath, double x, double y, double direction, double speed, Class checkFor, int damage) {
        super(x, y, direction, speed, checkFor, damage);
        this.spritePath = sprPath;
        this.characterImg = new Image(Objects.requireNonNull(Launcher.class.getResourceAsStream(this.spritePath)));
        this.imageView = new AnimatedSprite(characterImg,2,2,1,0,0,this.size,this.size,500);
        this.imageView.setFitWidth(this.size);
        this.imageView.setFitHeight(this.size);

        this.getChildren().addAll(this.imageView);
        // Use Platform.runLater to update rendering entity immediately upon creation
        Platform.runLater(() -> {
            // Add the entity to the platform's children
            platform.getChildren().addAll(this);
        });
        // set rendering
        setTranslateX(x-16);
        setTranslateY(y-16);
    }

    // methods
    public void step(){
        move();
        bulletCollision(game.getEntities());
    }
    public void move(){
        ///handling
        double angleRad = Math.toRadians(direction);
        double hsp = Math.cos(angleRad) * speed;
        double vsp = Math.sin(angleRad) * speed;
        // slow down the speed
        speed += game.lerp(speed, 0, .1);
        // update pos
        x += hsp;
        y -= vsp;
        int buffer = -16;
        Platform.runLater(() -> {
            setTranslateX(x+buffer);
            setTranslateY(y+buffer);
        });
    }
    boolean check;
    int timer = 30;
    public void bulletCollision(List<Entity> entityList) throws ConcurrentModificationException {
        if (timer <= 0) {
            // stun the wave spawn
            game.stun();
            // stun the enemies
            synchronized (game.getEntities()) {
                List<Enemy> enemyList = game.getEntities().stream()
                        .filter(ent -> ent instanceof Enemy)
                        .map(ent -> (Enemy) ent)
                        .toList();
                for (Enemy e : enemyList) {
                    e.stun();
                }
            }
            // remove special bullets render
            Platform.runLater(() -> {
                for (Bullet b : game.bulletList) {
                    if (b.getClass() == SpecialBullet.class) {
                        platform.getChildren().remove(b);
                    }
                }
                // clear the bullets
                game.bulletList.clear();
            });
            // add score
            game.setScore(game.getScore() + 10); // score add
            // create stun explosion effect
            new Particle(getX() - 32, getY() - 32, "assets/stunFlashSprite-Sheet-export.png",
                    64, 64, 0, 0, true, 4);
            // create debris particle
            for (int i = 0; i < 6; i++) {
                new Particle(getX() - 32, getY() - 32, "assets/debris.png",
                        3, 3, 10 + (Math.random() * 10), 10, Math.random() * 360, false, 1);
            }
        }


        timer--;
    }
}
