package se233.advproject2.model;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

//Imports are omitted
public class AnimatedSprite extends ImageView {
    int count, columns, rows, offsetX, offsetY, width, height, curIndex,
            curColumnIndex = 0, curRowIndex = 0;
    private final Timeline animationTimeline;
    public AnimatedSprite(Image image, int count, int columns, int rows, int
            offsetX, int offsetY, int width, int height, int ms) {
        this.setImage(image);
        this.count = count;
        this.columns = columns;
        this.rows = rows;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.width = width;
        this.height = height;
        this.setViewport(new Rectangle2D(offsetX, offsetY, width, height));
        // Initialize and set up the Timeline to run tick() at 2fps
        animationTimeline = new Timeline(new KeyFrame(Duration.millis(ms), e -> tick()));
        animationTimeline.setCycleCount(Timeline.INDEFINITE);
        startAnimation();
    }
    public void tick() {
        curColumnIndex = curIndex % columns;
        curRowIndex = curIndex / columns;
        curIndex = (curIndex+1) % (columns * rows);
        interpolate();
    }
    protected void interpolate() {
        final int x = curColumnIndex * width + offsetX;
        final int y = curRowIndex * height + offsetY;
        this.setViewport(new Rectangle2D(x, y, width, height));
    }
    public void startAnimation() {
        animationTimeline.play();
    }

    public void stopAnimation() {
        animationTimeline.stop();
    }

    public int getIndex() {
        return curIndex;
    }
}
