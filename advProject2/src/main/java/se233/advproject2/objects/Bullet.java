package se233.advproject2.objects;

import se233.advproject2.controller.GameLoop;

import java.util.ConcurrentModificationException;
import java.util.List;

public class Bullet {
    public boolean dead = false;
    private double x, y, direction, speed;
    private int damage = 1;
    public double getX() { return x; }
    public double getY() { return y; }
    public void setY(double y) {
        this.y = y;
    }
    public double getDirection() {
        return direction;
    }
    public void setDirection(double direction) {
        this.direction = direction;
    }
    public double getSpeed() {
        return speed;
    }
    public void setSpeed(double speed) {
        this.speed = speed;
    }
    private boolean hit = false;
    private Class checkFor;
    private int changeDirTime = 45; // 1.30s
    private boolean homing;
    enum type {
        normal,piercing,homing,targetting
    }
    type bulletType = type.normal;
    public void setTargetting(){bulletType = type.targetting;}
    public Bullet(double x, double y, double direction, double speed, Class checkFor){
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.speed = speed;
        this.checkFor = checkFor;
        this.homing = false;
    }
    public Bullet(double x, double y, double direction, double speed, Class checkFor, int damage){
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.speed = speed;
        this.checkFor = checkFor;
        this.damage = damage;
        this.homing = false;
    }
    public void bulletCollision(List<Entity> entityList) throws ConcurrentModificationException {
        if(!hit) for (Entity ent: entityList) {
            if(checkFor.isAssignableFrom(ent.getClass())) { // check for targeted entity and it's subclasses
                int buffer = 15;
                if(checkFor == Player.class){ buffer = -5; }
                boolean checkinX = (this.x > ent.getX() - buffer && this.x < ent.getX() + ent.getSize() + buffer);
                boolean checkinY = (this.y > ent.getY() && this.y < ent.getY() + ent.getSize());
                if (checkinX && checkinY) {
                    ent.hurt(damage);
                    hit = true; // set hit so no more damaging
                    // add score
                    if (ent.getClass() == Enemy.class) {
                        GameLoop.Instance.score++;
                    }else if (ent.getClass() == EnemyHighRank.class){
                        GameLoop.Instance.score += 2;
                    }
                    System.out.println("Collided with " + ent.name);
                    return;
                }
            }
        }
        if(hit && bulletType != type.piercing) dead = true;
    }
    // move
    public void move() throws ConcurrentModificationException {
        switch (bulletType){
            case targetting -> {
                // change direction
                if (changeDirTime > 0) changeDirTime--;
                if (changeDirTime == 0) {
                    // change direction to towards player
                    targetPlayer();
                    // prevent redo
                    changeDirTime--;
                    // no reset
                }
            }
        }
        // handling
        double angleRad = Math.toRadians(direction);
        double hsp = Math.cos(angleRad) * speed;
        double vsp = Math.sin(angleRad) * speed;
        // update pos
        x += hsp;
        y -= vsp;
    }
    Player player = GameLoop.Instance.player;
    private void targetPlayer(){
        double x1 = x, y1 = y;
        double x2 = player.x, y2 = player.y - 100;
        // Calculate the angle between the two points
        double angle = Math.toDegrees(Math.atan2(y2 - y1, x2 - x1));
        direction = -angle;
    }
}
