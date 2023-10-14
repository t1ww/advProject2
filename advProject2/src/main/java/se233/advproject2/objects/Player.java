package se233.advproject2.objects;

import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import se233.advproject2.controller.GameLoop;
import se233.advproject2.view.GameScreen;

public class Player extends Entity {
    // init variables
    int startX = 300, startY = 750;
    int speed = 10;
    boolean keyLeft = false;
    boolean keyRight = false;
    boolean keyShoot = false;
    public Player(double x, double y, int size) {
        super(x,y,size);
    }
    // key setup
    // move
    public void move(GameScreen p) {
        resetKeys();
        if(!p.getKeys().isEmpty())
            for (KeyCode cur_key : p.getKeys()) {
                switch (cur_key) {
                    case A -> keyLeft = true;
                    case D -> keyRight = true;
                }
            }
        int hsp;
        hsp = 0;
        if (keyLeft) hsp -= speed;
        if (keyRight) hsp += speed;

        setX(getX() + hsp);
    }
    private void resetKeys(){
        keyLeft = false;
        keyRight = false;
        keyShoot = false;
    }
    // shoot
    // state machine
}
