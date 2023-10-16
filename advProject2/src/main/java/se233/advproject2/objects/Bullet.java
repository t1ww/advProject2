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
    private int changeDirTime = 90; // 1.30s
    private boolean homing;

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
    public Bullet(double x, double y, double direction, double speed, Class checkFor, int damage, boolean homing){
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.speed = speed;
        this.checkFor = checkFor;
        this.damage = damage;
        this.homing = homing;
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
        if(hit) dead = true;
    }
    // move
    public void move() throws ConcurrentModificationException {
        if(homing) {
            // change direction
            if (changeDirTime > 0) changeDirTime--;
            if (changeDirTime == 0) {
                // change direction to towards player

                // prevent redo
                changeDirTime--;
                // no reset
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
}
