package se233.advproject2.objects;

import javafx.scene.paint.Color;

public class Enemy extends Entity {
    // var init
    int shootCD = 50;
    int shootCD_Counter = 0;
    int shootCD_reduction = 0;
    int Level;
    private int movestreak = 0;
    double xto, yto;
    enum MOVE_ROTAION{
        left,right,down
    }
    public MOVE_ROTAION moveDir = MOVE_ROTAION.left;
    // constructor
    public Enemy(double x, double y, int size, int lvl) {
        super(x,y,size);
        xto = x;yto = y;
        this.x = Math.random()*800;
        this.y = -60;
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
        // if move to changed, lerp x and y to that move to
        if(xto != Math.round(x) || yto != Math.round(y)){
            x += game.lerp(x, xto, 0.05);
            y += game.lerp(y, yto, 0.05);
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
            game.enemyCount--;
            System.out.println(name + " is dead");
        }else System.out.println(name + " now has " + hp + " hp");
    }

    public void move() { // MOVE IS STARTED AT LEFT SO NOBODY SPAWNS
        switch (moveDir){
            case left -> {
                xto -= 10;
                moveDir = MOVE_ROTAION.right;
            }
            case right -> {
                xto += 10;
                moveDir = MOVE_ROTAION.down;
            }
            case down -> {
                movestreak++;
                if(movestreak == 10){
                    game.End();
                }
                yto += 50;
                // create new friends
                for (int i = 0; i < 5; i++) {
                    game.entities.add(new Enemy(90 + i * 100, 150, 32, game.level));
                    game.enemyCount++;
                }
                moveDir = MOVE_ROTAION.left;
            }
        }
    }
}
