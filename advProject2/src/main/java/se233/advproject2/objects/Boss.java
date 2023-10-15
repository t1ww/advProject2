package se233.advproject2.objects;

import javafx.scene.paint.Color;

public class Boss extends Enemy {
    enum bossType {
        scatter, laser, fast
    }
    private bossType type;
    // constructor
    public Boss (double x, double y, int size, int level){
        super(x, y, size, level);
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
    public void hurt(int dmg){
        hp -= dmg;
        if(hp <= 0){
            dead = true;
            game.enemycount--; // removed from list
            game.level++; // next level
            System.out.println(name + " is dead");
        }else System.out.println(name + " now has " + hp + " hp");
    }
}
