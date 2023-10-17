# advProject2
 2nd semester project for advance programming class y2 2023


from chat gpt
 > how to make particle
Of course! Below is a basic implementation of a Particle class that has two types:

A particle that moves in a given direction and gets removed when out of screen.
A particle that gets removed once its animation ends.
For simplicity, I'll assume that the size of the scene or screen is 800x600 pixels. You can adjust these values as needed.

java
Copy code
package se233.advproject2.view;

import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

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
Usage:

For a moving particle that disappears off screen:

java
Copy code
Particle movingParticle = new Particle(100, 100, "path/to/particle.png", 5, 0, false, 0);
For an animated particle that disappears after a certain number of frames:

java
Copy code
Particle animatedParticle = new Particle(200, 200, "path/to/animatedParticle.png", 0, 0, true, 60);
Remember to call the move() method of each Particle instance regularly (e.g., within a game loop or timeline) to see the movement and disappearance behavior.

You might want to enhance the implementation further based on your specific needs.
