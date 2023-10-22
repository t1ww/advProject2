package se233.advproject2.objects;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import se233.advproject2.Launcher;
import se233.advproject2.controller.GameLoop;
import se233.advproject2.view.AnimatedSprite;
import se233.advproject2.view.GameScreen;

import java.util.Objects;

public class Particle extends Pane {
    GameLoop game = GameLoop.Instance;
    GameScreen platform = game.platform;

    private final ImageView imageView;
    private final Image particleImage;
    private final double speedX, speedY;
    private final boolean isAnimated;
    private int animationFrames;

    public Particle(double x, double y, String spritePath, int width, int height, double speedX, double speedY, boolean isAnimated, int animationFrames) {
        setTranslateX(x);
        setTranslateY(y);

        this.speedX = speedX;
        this.speedY = speedY;
        this.isAnimated = isAnimated;
        this.animationFrames = animationFrames;

        this.particleImage = new Image(Objects.requireNonNull(Launcher.class.getResourceAsStream(spritePath)));
        this.imageView = new AnimatedSprite(particleImage,animationFrames,animationFrames,1,0,0,width,height);
        this.imageView.setFitWidth(width);
        this.imageView.setFitHeight(height);
        this.getChildren().addAll(this.imageView);
        System.out.println("particle create : " + spritePath);
        // setup
        game.particleList.add(this);
        Platform.runLater(() -> {
            // Add the particle to the platform's children
            platform.getChildren().addAll(this);
        });
    }

    public void move() {
        if (isAnimated && animationFrames <= 0) {
            removeSelf();
            return;
        }

        Platform.runLater(() -> {
            setTranslateX(getTranslateX() + speedX);
            setTranslateY(getTranslateY() + speedY);
        });

        if (!isOnScreen()) {
            removeSelf();
        }

        if (isAnimated) {
            animationFrames--;
        }
    }
    public void removeSelf(){
        // remove from update list
        game.particleList.remove(this);
        Platform.runLater(() -> {
            // Remove the particle to the platform's children
            platform.getChildren().remove(this);
        });
    }
    private boolean isOnScreen() {
        Bounds bounds = this.getBoundsInParent();
        return bounds.getMinX() < 800 && bounds.getMaxX() > 0 && bounds.getMinY() < 600 && bounds.getMaxY() > 0;
    }
}