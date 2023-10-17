package se233.advproject2.objects;

import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import se233.advproject2.Launcher;

import java.util.Objects;

public class Particle extends Pane {

    private final ImageView imageView;
    private final Image particleImage;
    private final double speedX, speedY;
    private final boolean isAnimated;
    private int animationFrames;

    public Particle(double x, double y, String spritePath, double speedX, double speedY, boolean isAnimated, int animationFrames) {
        this.particleImage = new Image(Objects.requireNonNull(Launcher.class.getResourceAsStream(spritePath)));
        this.imageView = new ImageView(particleImage);
        this.imageView.setFitWidth(32);
        this.imageView.setFitHeight(32);
        this.getChildren().addAll(this.imageView);

        setTranslateX(x);
        setTranslateY(y);

        this.speedX = speedX;
        this.speedY = speedY;
        this.isAnimated = isAnimated;
        this.animationFrames = animationFrames;
    }

    public void move() {
        if (isAnimated && animationFrames <= 0) {
            this.setVisible(false);
            return;
        }

        setTranslateX(getTranslateX() + speedX);
        setTranslateY(getTranslateY() + speedY);

        if (!isOnScreen()) {
            this.setVisible(false);
        }

        if (isAnimated) {
            animationFrames--;
        }
    }

    private boolean isOnScreen() {
        Bounds bounds = this.getBoundsInParent();
        return bounds.getMinX() < 800 && bounds.getMaxX() > 0 && bounds.getMinY() < 600 && bounds.getMaxY() > 0;
    }
}