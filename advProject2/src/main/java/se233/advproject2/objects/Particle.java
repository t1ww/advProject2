package se233.advproject2.objects;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import se233.advproject2.Launcher;
import se233.advproject2.controller.GameLoop;
import se233.advproject2.model.AnimatedSprite;
import se233.advproject2.view.GameScreen;

import java.util.Objects;

public class Particle extends Pane {
    GameLoop game = GameLoop.getInstance();
    GameScreen platform = game.getPlatform();

    private final AnimatedSprite imageView;
    private final Image particleImage;
    private double x, y, direction, speed, speedMin = 1;
    private final boolean isAnimated;
    private int animationFrames;
    private String spritePath;

    public Particle(double x, double y, String spritePath, int width, int height, double speed, double direction, boolean isAnimated, int animationFrames) {
        this.x = x;
        this.y = y;

        this.spritePath = spritePath;

        this.speed = speed;
        this.direction = direction;
        this.isAnimated = isAnimated;
        this.animationFrames = animationFrames;

        this.particleImage = new Image(Objects.requireNonNull(Launcher.class.getResourceAsStream(spritePath)));
        this.imageView = new AnimatedSprite(particleImage,animationFrames,animationFrames,1,0,0,width,height, 50);
        this.imageView.setFitWidth(width);
        this.imageView.setFitHeight(height);
        this.getChildren().addAll(this.imageView);
        // setup
        game.getParticleList().add(this);
        Platform.runLater(() -> {
            // Add the particle to the platform's children
            platform.getChildren().addAll(this);
            setTranslateX(this.x);
            setTranslateY(this.y);
        });

        this.animationFrames *= 3;
    }
    public Particle(double x, double y, String spritePath, int width, int height, double speed, double speedMin, double direction, boolean isAnimated, int animationFrames) {
        this.x = x;
        this.y = y;
        setTranslateX(this.x);
        setTranslateY(this.y);
        this.spritePath = spritePath;

        this.speed = speed;
        this.speedMin = speedMin;
        this.direction = direction;
        this.isAnimated = isAnimated;
        this.animationFrames = animationFrames;

        this.particleImage = new Image(Objects.requireNonNull(Launcher.class.getResourceAsStream(spritePath)));
        this.imageView = new AnimatedSprite(particleImage,animationFrames,animationFrames,1,0,0,width,height, 50);
        this.imageView.setFitWidth(width);
        this.imageView.setFitHeight(height);
        this.getChildren().addAll(this.imageView);
        // setup
        game.getParticleList().add(this);
        Platform.runLater(() -> {
            // Add the particle to the platform's children
            platform.getChildren().addAll(this);
        });

        this.animationFrames *= 3;
    }
    public void step() {
        if(isAnimated) {
            animationFrames--;
            if (animationFrames < 0) {
                removeSelf();
                return;
            }
        }
        if (!isOnScreen()) {
            removeSelf();
        }
        move();
    }
    public void move() {
        ///handling
        double angleRad = Math.toRadians(direction);
        double hsp = Math.cos(angleRad) * speed;
        double vsp = Math.sin(angleRad) * speed;
        // slow down the speed
        if(!isAnimated)speed += game.lerp(speed, speedMin, .1);
        // update pos
        x += hsp;
        y -= vsp;

        Platform.runLater(() -> {
            setTranslateX(x);
            setTranslateY(y);
        });
    }
    public void removeSelf(){
        logger.info("Removing particle [ Path : {}", spritePath);
        // remove from update list
        game.getParticleList().remove(this);
        Platform.runLater(() -> {
            // Remove the particle to the platform's children
            platform.getChildren().remove(this);
        });
    }
    private boolean isOnScreen() {
        return  getTranslateX() > -200 &&  getTranslateX() < GameScreen.WIDTH +200 &&
                getTranslateY() > -200 &&  getTranslateY() < GameScreen.HEIGHT +200;
    }
    // logger //
    private static final Logger logger = GameLoop.logger;

}