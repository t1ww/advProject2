package se233.advproject2.objects;

import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import se233.advproject2.controller.GameLoop;
import se233.advproject2.view.GameScreen;

import java.util.ConcurrentModificationException;

public class Player extends Entity {
    // init variables
    int startX = 300, startY = 750;
    int speed = 5;
    boolean keyLeft = false,keyRight = false, keySprint = false;
    boolean keyShoot = false,trigger = false, auto = false;
    public Player(double x, double y, int size) {
        super(x,y,size);
        this.name = "player";
    }
    // step
    public void step(){
        shoot();
        move();
    }
    // shoot
    public void shoot(){
        resetKeys();
        GameScreen p = platform;
        if(!p.getKeys().isEmpty())
            for (KeyCode cur_key : p.getKeys()) {
                switch (cur_key) {
                    // key setup
                    case SPACE -> keyShoot = true;
                }
            }
        if(keyShoot){
            if (!trigger){// semi auto
                System.out.println("player shot bullet");
                // create bullet
                Bullet b = new Bullet(getX() + (getSize()/2), getY()- 5, 90, 10);
                game.bulletList.add(b);
                trigger = true;
            }
        }else{
            // reset trigger
            trigger = false;
        }
    }
    // move
    public void move() {
        GameScreen p = platform;
        resetKeys();
        if(!p.getKeys().isEmpty())
            for (KeyCode cur_key : p.getKeys()) {
                switch (cur_key) {
                    // key setup
                    case A -> keyLeft = true;
                    case D -> keyRight = true;
                    case SHIFT -> keySprint = true;
                }
            }
        int hsp;
        hsp = 0;
        if (keyLeft) hsp -= speed;
        if (keyRight) hsp += speed;
        if (keySprint) hsp *=2;
        hsp = playerCollision(hsp);
        setX(getX() + hsp);
    }
    private int playerCollision(int hsp){
        if(getX() + hsp < 0
        || getX() + hsp > (GameScreen.WIDTH - getSize())){
            hsp = 0;
        }
        return hsp;
    }
    private void resetKeys(){
        keyLeft = false;
        keyRight = false;
        keyShoot = false;
        keySprint = false;
    }
    public void hurt(int dmg){
        hp -= dmg;
        if(hp <= 0){ // player dead
            dead = true;
            game.End();
            System.out.println(name + " is dead");
        }else System.out.println(name + " now has " + hp + " hp");
    }
    // state machine

}
