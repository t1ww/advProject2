package se233.advproject2.objects;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import se233.advproject2.Launcher;
import se233.advproject2.view.AnimatedSprite;
import se233.advproject2.view.GameScreen;

import java.util.ConcurrentModificationException;
import java.util.Objects;

public class Player extends Entity {
    // init variables
    boolean starting = true;
    int speed = 3;
    int fireRate = 16;
    int fireDelay = 0;
    public int hp = 3;
    boolean keyLeft = false,keyRight = false, keySprint = false;
    boolean keyShoot = false,trigger = false, auto = true;
    boolean keySpecial = false, specialTrigger = false;
    double startX;
    double startY;
    GameScreen p = platform;
    public Player(double x, double y, int size) {
        super(x,y,size, "assets/playerSprite-straight.png");
        this.name = "player";
        this.startX = x;
        this.startY = y;
        this.x = Math.random()*800;
        this.y = 750;
    }
    // step
    public void step() throws ConcurrentModificationException {
        if(starting){
            // ease in player when starting
            x += game.lerp(x, startX,0.1);
            y += game.lerp(y, startY, 0.1);
            if(Math.round(x) == startX && Math.round(y) == startY) {
                System.out.println("player ready, game started");
                starting = false;
            }
        }else {// allow inputs when started
            shoot();
            move();
        }
    }

    // shoot
    public void shoot(){
        resetKeys();
        if(!p.getKeys().isEmpty()) {
            for (KeyCode cur_key : p.getKeys()) {
                switch (cur_key) {
                    // key setup
                    case SPACE -> keyShoot = true;
                    case G -> keySpecial = true;
                }
            }
        }
/////////////
        // normal attack
        if(keyShoot){
            if(auto){
                if(fireDelay <= 0){
                    // shot fired
                    createBullet();
                    // delay the shot
                    fireDelay = fireRate;
                }
            }else
            if (!trigger){// semi auto
                createBullet();
            }
        }else{
            // reset trigger
            trigger = false;
            fireDelay = 0;
        }
        if(fireDelay > 0) fireDelay--;
/////////////
        // special attack
        if(keySpecial){
            if (!specialTrigger){// semi auto
                createSpecialBullet();
            }
        }else{
            // reset trigger
            specialTrigger = false;
        }
    }
    private void createBullet(){
        // create bullet
        Bullet b = new Bullet(getX() + (getSize()/2), getY()- 5, 90, 8, Enemy.class);
        game.bulletList.add(b);
        trigger = true;
    }
    private void createSpecialBullet(){
        System.out.println("player special fired");
        // create bullet
        //Bullet b = new Bullet(getX() + (getSize()/2), getY()- 5, 90, 8, Enemy.class);
        //game.bulletList.add(b);
        specialTrigger = true;
    }
    // move
    public void move() {
        GameScreen p = platform;
        resetKeys(); // reset
        // check keys
        if(!p.getKeys().isEmpty()) {
            for (KeyCode cur_key : p.getKeys()) {
                switch (cur_key) {
                    // key setup
                    case A, LEFT -> keyLeft = true;
                    case D, RIGHT -> keyRight = true;
                    case SHIFT -> keySprint = true;
                }
            }
        }
        // apply key
        int hsp;
        hsp = 0;
        if (keyLeft) hsp -= speed;
        if (keyRight) hsp += speed;
        if (keySprint) hsp *=2;
        hsp = playerCollision(hsp);
        // update pos
        setX(getX() + hsp);
    }
    private int playerCollision(int hsp){
        if(getX() + hsp < 0
        || getX() + hsp > (GameScreen.WIDTH - getSize())){
            // if next move hit edge, don't move
            hsp = 0;
        }
        return hsp;
    }
    private void resetKeys(){
        keyLeft = false;
        keyRight = false;
        keyShoot = false;
        keySprint = false;
        keySpecial = false;
    }
    public void hurt(int dmg){
        hp -= dmg;
        if(hp <= 0){ // player dead
            dead = true;
            // Use Platform.runLater to update rendering entity immediately upon death
            Platform.runLater(() -> {
                // Remove the entity to the platform's children
                platform.getChildren().remove(this);
            });
            game.End();
            System.out.println(name + " is dead");
        }else System.out.println(name + " now has " + hp + " hp");
    }
    // logger // import later
//    private static final Logger logger = LogManager.getLogger(Character.class);
//    public void trace() {
//        logger.info("x:{} y:{} vx:{} vy:{}",x,y,xVelocity,yVelocity);
//    }

}
