package se233.advproject2.objects;

import javafx.scene.paint.Color;
import se233.advproject2.controller.GameLoop;

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
    // constructor
    public Boss (double x, double y, int size, int lvl){
        super(x, y, size, lvl);
        xto = x;yto = y;
        this.y = -100;
        this.moveCD_reduction += Math.min(lvl, 35);
        // random type
//        switch ((int) (Math.random()*3)){
        switch (1){ // for force test
            default -> {
                hp = 60;
                name = "scatter";
                type = bossType.scatter;
                sprite = Color.PINK;
                this.shootCD_reduction = 150;
            }
            case 1 -> {
                hp = 80;
                name = "tracker";
                type = bossType.tracker;
                sprite = Color.PURPLE;
                this.shootCD_reduction = 120;
                this.shotsAmount += lvl;
            }
            case 2 -> {
                hp = 40;
                name = "fast";
                type = bossType.fast;
                sprite = Color.GREEN;
                this.shootCD_reduction = 260;
                this.moveCD_reduction = 90;
            }
        }
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
    private void scatterStep(){
        if(shootCD_Counter > shootCD){
            if (attack == 1) {
            } else {// create bullet
                int dir = -(int) (Math.random() * 90);
                for (int i = 0; i < 9; i++) {
                    dir -= 10;
                    Bullet b = new Bullet(getX() + (getSize() / 2), getY() + getSize() + 5, dir, 3, Player.class);
                    game.bulletList.add(b);
                }
            }
            cdReset();
        } else {
            shootCD_Counter++;
        }
    }
    // amount of shots for tracker
    int shotsAmount = 20;
    private void trackerStep(){
        // shoot
        if(shootCD_Counter > shootCD){
            if (attack == 1) {
            } else {// create bullet
                int dir = -90;
                for (int i = 0; i < shotsAmount; i++) {
                    dir -= (360 / shotsAmount);
                    Bullet b = new Bullet(getX() + (getSize() / 2), getY() + getSize() + 5,
                            dir, 4, Player.class, 1, true);
                    game.bulletList.add(b);
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
            }
            cdReset();
        } else {
            shootCD_Counter++;
        }
    }
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
            dead = true;
            game.enemyCount--; // removed from list
            if(game.enemyCount == 0) {
                game.setCreepsWave();
                game.spawnWaveInit(5);
            }
            game.level++; // next level
            GameLoop.Instance.score += 50; // score add
            // reset phase
            game.creationPhase = true;
            System.out.println(name + " is dead");
        }else System.out.println(name + " now has " + hp + " hp");
    }
}
