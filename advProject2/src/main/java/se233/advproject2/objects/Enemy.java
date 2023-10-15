package se233.advproject2.objects;

import javafx.scene.paint.Color;

public class Enemy extends Entity {
    // var init
    int shootCD = 50;
    int shootCD_Counter = 0;
    int shootCD_reduction = 0;
    int Level;
    // constructor
    public Enemy(double x, double y, int size, int lvl) {
        super(x,y,size);
        this.name = "small enemy";
        this.Level = lvl;
        this.hp += lvl;
        this.sprite = Color.RED;
        this.shootCD_reduction += Math.min(lvl, 35);
        cdReset();
    }
    // state machine
    // alive
    // shoot
    public void step(){
        if(shootCD_Counter > shootCD){
            shoot();
        }else {
            shootCD_Counter++;
        }
    }
    public void shoot(){
        // create bullet
        Bullet b = new Bullet(getX() + (getSize()/2), getY() + getSize() + 5, -90, 10, Player.class);
        game.bulletList.add(b);
        cdReset();
    }
    void cdReset(){//randomness + reduction
        shootCD_Counter = (int)(Math.random()*((shootCD-shootCD_reduction)/2)) + (shootCD_reduction);
    }
    // dead
    public void hurt(int dmg){
        hp -= dmg;
        if(hp <= 0){
            dead = true;
            game.enemycount--;
            System.out.println(name + " is dead");
        }else System.out.println(name + " now has " + hp + " hp");
    }
}
