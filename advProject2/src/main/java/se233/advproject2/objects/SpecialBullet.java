package se233.advproject2.objects;

import javafx.scene.layout.Pane;

public class SpecialBullet extends Bullet {
    private String sprPath;
    public SpecialBullet(String sprPath, double x, double y, double direction, double speed, Class checkFor) {
        super(x, y, direction, speed, checkFor);
    }

    public SpecialBullet(String sprPath, double x, double y, double direction, double speed, Class checkFor, int damage) {
        super(x, y, direction, speed, checkFor, damage);
    }
}
