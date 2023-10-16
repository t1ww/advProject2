package se233.advproject2.objects;

import javafx.scene.paint.Color;

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
    double xto, yto;

    public void moveDown() {
        yto += 50;
        if(movestreak++ >= 10) game.End();
    }

    enum MOVE_ROTAION{
        left,right
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
        this.shootCD_reduction += Math.min(lvl, 60);
        this.moveCD_reduction += Math.min(lvl, 60);
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
        if(moveCD_Counter > moveCD){
            move();
        }else {
            moveCD_Counter++;
        }
        // if move to changed, lerp x and y to that move to
        if(xto != Math.round(x) || yto != Math.round(y)){
            x += game.lerp(x, xto, 0.05);
            y += game.lerp(y, yto, 0.05);
        }
    }
    public void shoot(){
        // create bullet
        Bullet b = new Bullet(getX() + (getSize()/2), getY() + getSize() + 5, -90, 8, Player.class);
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
            if(game.enemyCount == 0) {
                game.setBossWave();
                game.spawnWaveInit(5);
            }
            System.out.println(name + " is dead");
        }else System.out.println(name + " now has " + hp + " hp");
    }
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
}
