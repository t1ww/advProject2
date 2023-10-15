package se233.advproject2.objects;

import javafx.scene.paint.Color;

public class Boss extends Enemy {
    enum bossType {
        scatter, laser, fast
    }
    private bossType type;
    int moveCD = 180;
    int moveCD_count = 0;
    int moveCD_reduction = 0;
    double xto, yto;
    // constructor
    public Boss (double x, double y, int size, int lvl){
        super(x, y, size, lvl);
        xto = x;yto = y;
        this.y = -100;
        this.moveCD_reduction += Math.min(lvl, 35);
        // random type
        switch ((int) (Math.random()*3)){
            default -> {
                hp = 60;
                name = "scatter";
                type = bossType.scatter;
                sprite = Color.PINK;
            }
            case 1 -> {
                hp = 100;
                name = "laser";
                type = bossType.laser;
                sprite = Color.PURPLE;
            }
            case 2 -> {
                hp = 40;
                name = "fast";
                type = bossType.fast;
                sprite = Color.GREEN;
                this.size = 48;
                this.shootCD_reduction = 20;
            }
        }
    }
    // state machine
    public void step(){ // base step
        switch (type){
            case scatter -> scatterStep();
            case laser -> laserStep();
            case fast -> fastStep();
        }
        // randomly move
        if(moveCD_count > moveCD){
            move();
        } else {
            moveCD_count++;
        }
        // if move to changed, lerp x and y to that move to
        if(xto != x || yto != y){
            x += game.lerp(x, xto, 0.05);
            y += game.lerp(y, yto, 0.05);
        }
    }
    private void scatterStep(){
        if(shootCD_Counter > shootCD){
            // create bullet
            for (int i = 0; i < 5; i++) {
                int dir = -70 - (10*i);
                Bullet b = new Bullet(getX() + (getSize()/2), getY() + getSize() + 5, dir, 10, Player.class);
                game.bulletList.add(b);
            }
            cdReset();
        } else {
            shootCD_Counter++;
        }
    }
    private void laserStep(){
        // shoot
        if(shootCD_Counter > shootCD){
            shoot();
        } else {
            shootCD_Counter++;
        }
    }
    private void fastStep(){
        if(shootCD_Counter > shootCD){
            shoot();
        } else {
            shootCD_Counter++;
        }
    }
    void moveCDReset(){//randomness + reduction
        moveCD_count = (int)(Math.random()*((moveCD-moveCD_reduction)/2)) + (moveCD_reduction);
    }
    public void move(){
        xto = 50 + (Math.random()*500);
        yto = 100 + (Math.random()*200);
        moveCDReset();
    }
    public void hurt(int dmg){
        hp -= dmg;
        if(hp <= 0){
            dead = true;
            game.enemyCount--; // removed from list
            game.level++; // next level
            System.out.println(name + " is dead");
        }else System.out.println(name + " now has " + hp + " hp");
    }
}
