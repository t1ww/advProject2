package se233.advproject2.objects;

import javafx.application.Platform;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Enemy extends Entity {
    // var init
    int shootCD = 300;
    int shootCD_Counter = 0;
    int shootCD_reduction = 0;
    int moveCD = 60;
    int moveCD_Counter = 0;
    int moveCD_reduction = 0;
    int Level;
    private int movestreak = 0;
    public double xto, yto;

    public void moveDown() {
        yto += 50;
        if(movestreak++ > 5) game.End();
    }

    enum MOVE_ROTAION{
        left,right
    }
    boolean stun = false;
    int stunTimer = 0;
    public MOVE_ROTAION moveDir = MOVE_ROTAION.left;
    // constructor
    public Enemy(double x, double y, int size, int lvl) {
        super(x,y,size, "assets/enemySprite-Sheet.png");
        xto = x;yto = y;
        this.x = Math.random()*800;
        this.y = -60;
        this.name = "small enemy";
        this.Level = lvl;
        this.hp += lvl;
        this.shootCD_reduction += Math.min(lvl, 60);
        this.moveCD_reduction += Math.min(lvl, 60);
        cdReset();
    }
    public Enemy(double x, double y, int size, int lvl, String sprite) {
        super(x,y,size, sprite);
        xto = x;yto = y;
        this.x = Math.random()*800;
        this.y = -60;
        this.name = "small enemy";
        this.Level = lvl;
        this.hp += lvl;
        this.shootCD_reduction += Math.min(lvl, 60);
        this.moveCD_reduction += Math.min(lvl, 60);
        cdReset();
    }
    // state machine
    // alive
    // shoot
    public void step(){
        // stop doing everything while stun
        if(stun) {
            // count the stun
            if(--stunTimer < 0){
                stun = false;
            }
        }else {
            if (shootCD_Counter > shootCD) {
                shoot();
            } else {
                shootCD_Counter++;
            }
            if (moveCD_Counter > moveCD) {
                move();
            } else {
                moveCD_Counter++;
            }
            // if move to changed, lerp x and y to that move to
            if (xto != Math.round(x) || yto != Math.round(y)) {
                x += game.lerp(x, xto, 0.05);
                y += game.lerp(y, yto, 0.05);
            }
        }
    }
    public void shoot(){
        // create bullet
        Bullet b = new Bullet(getX() + (getSize()/2), getY() + getSize() + 5, -90, 7, Player.class);
        cdReset();
    }
    void cdReset(){//randomness + reduction
        shootCD_Counter = (int)(Math.random()*((shootCD-shootCD_reduction)/2)) + (shootCD_reduction);
    }
    // dead
    int move_count = 10;
    int move_counter = move_count;
    public void move() {
        switch (moveDir){
            case left -> {
                xto -= 10;
                if(move_counter <= 0) {
                    moveDir = MOVE_ROTAION.right;
                    move_counter = move_count;
                }
            }
            case right -> {
                xto += 10;
                if(move_counter <= 0) {
                    moveDir = MOVE_ROTAION.left;
                    move_counter = move_count;
                }
            }
        }
        move_counter--;
        // reset move
        moveCD_Counter = moveCD_reduction;
    }

    double targetPlayerDir(){
        Player player = game.player;
        double x1 = x, y1 = y;
        double x2 = player.x, y2 = player.y;
        // Calculate the angle between the two points
        double angle = Math.toDegrees(Math.atan2(y2 - y1, x2 - x1));
        return -angle;
    }
    public void hurt(int dmg){
        hp -= dmg;
        if(hp <= 0){
            Platform.runLater(() -> {
                // Remove the entity to the platform's children
                platform.getChildren().remove(this);
                game.getEntities().remove(this);
                // counting enemies
                enemyCount();
                if(game.enemyCount == 0) {
                    // start new wave
                    game.setBossWave();
                    game.spawnWaveInit(5);
                }
            });
            System.out.println(name + " is dead");
        }else System.out.println(name + " now has " + hp + " hp");
    }
    public void stun(){
        stun = true;
        stunTimer = 60;
    }

    public void enemyCount(){
        // counting enemies
        List<Enemy> enemyList = game.getEntities().stream()
                .filter(ent -> ent instanceof Enemy)
                .map(ent -> (Enemy) ent)
                .toList();
        game.enemyCount = enemyList.size(); // recheck size
    }
}
