package se233.advproject2.objects;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Boss extends Enemy {
    enum bossType {
        scatter, tracker, fast
    }
    private bossType type;
    int moveCD = 180;
    int moveCD_count = 0;
    int moveCD_reduction = 0;
    int attack = 0;
    int atkCD = 360;
    int atkCD_count = 0;
    int atkCD_reduction = 0;
    double xto, yto;
    int shotsAmount;
    // constructor
    public Boss (double x, double y, int size, int lvl){
        super(x, y, size, lvl, "assets/bossSprite-Sheet.png");
        xto = x;yto = y;
        this.y = -100;
        this.moveCD_reduction += Math.min(lvl, 35);
        // random type
        switch ((int)Math.floor((Math.random()*2.99999999))){
//        switch (2){ // for force test
            default -> {
                hp = 90;
                name = "scatter";
                type = bossType.scatter;
                this.shootCD_reduction = 150;
                this.shotsAmount = 8+lvl;
            }
            case 1 -> {
                hp = 120;
                name = "tracker";
                type = bossType.tracker;
                this.shootCD_reduction = 120;
                this.shotsAmount += 20+lvl;
            }
            case 2 -> {
                hp = 60;
                name = "fast";
                type = bossType.fast;
                this.shootCD_reduction = 285;
                this.moveCD_reduction = 90;
                this.shotsAmount = 1;
                updateSprite("assets/cirnoBoss-Sheet.png");
            }
        }
        logger.info("boss created : {}",name);
    }
    // state machine
    public void step(){ // base step
        switch (type){
            case scatter -> scatterStep();
            case tracker -> trackerStep();
            case fast -> fastStep();
        }
        // randomly move
        if(moveCD_count > moveCD){
            move();
        } else {
            moveCD_count++;
        }
//        // change attack sometimes
//        if(atkCD_count > atkCD){
//            changeAtk();
//        } else {
//            atkCD_count++;
//        }
        // if move to changed, lerp x and y to that move to
        if(xto != Math.round(x) || yto != Math.round(y)){
            x += game.lerp(x, xto, 0.05);
            y += game.lerp(y, yto, 0.05);
        }
    }
    ///// STEPS //// FUNCTIONS ////
    private void scatterStep(){
        if(shootCD_Counter > shootCD){
            if (attack == 1) {
            } else {// create bullet
                int dir = (int)targetPlayerDir();
                dir += 10 * shotsAmount / 2; // buffer the direction to cover the player equally left right
                for (int i = 0; i < shotsAmount; i++) {
                    dir -= 10;
                    Bullet b = new Bullet(getX() + (getSize() / 2), getY() + getSize() + 5, dir, 3, Player.class);
                }
            }
            cdReset();
        } else {
            shootCD_Counter++;
        }
    }
    private void trackerStep(){
        // shoot
        if(shootCD_Counter > shootCD){
            if (attack == 1) {
            } else {// create bullet
                int dir = -90;
                for (int i = 0; i < shotsAmount; i++) {
                    dir -= (360 / shotsAmount);
                    Bullet b = new Bullet(getX() + (getSize() / 2), getY() + getSize() + 5,
                            dir, 4, Player.class);
                    b.setTargetting();
                }
            }
            cdReset();
        } else {
            shootCD_Counter++;
        }
    }
    private void fastStep(){
        if(shootCD_Counter > shootCD){
            if (attack == 1) {
            } else {
                // shoot faster and aim at player with small spreading
                for (int i = 0; i < shotsAmount; i++) {
                    Bullet b = new Bullet(getX() + (getSize() / 2), getY() + getSize() + 5,
                            targetPlayerDir()+(Math.random()*40)-20, 5, Player.class);
                }
            }
            cdReset();
        } else {
            shootCD_Counter++;
        }
    }

    //// MISC

    void moveCDReset(){//randomness + reduction
        moveCD_count = (int)(Math.random()*((moveCD-moveCD_reduction)/2)) + (moveCD_reduction);
    }
    public void move(){
        xto = 50 + (Math.random()*500);
        yto = 100 + (Math.random()*100);
        moveCDReset();
    }
    void atkCDReset(){//randomness + reduction
        atkCD_count = (int)(Math.random()*((atkCD-atkCD_reduction)/2)) + (atkCD_reduction);
    }
    public void changeAtk(){
        if (attack == 0){
            attack = 1;
        }else {
            attack = 0;
        }
        atkCDReset();
    }
    public void hurt(int dmg){
        hp -= dmg;
        if(hp <= 0){
            // drop chance
            dropChance(1);
            logger.info("boss killed");
            // Use Platform.runLater to update rendering entity immediately upon death
            Platform.runLater(() -> {
                // Remove the entity to the platform's children
                platform.getChildren().remove(this);
                game.getEntities().remove(this);
                // counting enemies
                enemyCount();
                if(game.getEnemyCount() == 0) {
                    // starting new wave
                    game.setCreepsWave();
                    game.spawnWaveInit(3);
                }
            });
            game.level++; // next level
            game.setScore(game.getScore()+50); // score add
            // reset phase
            game.setCreationPhase(true);
            System.out.println(name + " is dead");
        }else System.out.println(name + " now has " + hp + " hp");
    }
    // logger //
    private static final Logger logger = LogManager.getLogger(Character.class);
}
