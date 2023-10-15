package se233.advproject2.objects;

import se233.advproject2.controller.GameLoop;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;

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

    public Bullet(double x, double y, double direction, double speed, Class checkFor){
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.speed = speed;
        this.checkFor = checkFor;
    }
    public Bullet(double x, double y, double direction, double speed, int damage){
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.speed = speed;
        this.damage = damage;
    }
    public boolean bulletCollision(List<Entity> entityList) throws ConcurrentModificationException {
        if(!hit)
        for (Entity ent: entityList) {
            if(checkFor == ent.getClass()) { // check for targeted entity
                int buffer = 15;
                boolean checkinX = (this.x > ent.getX() - buffer && this.x < ent.getX() + ent.getSize() + buffer);
                boolean checkinY = (this.y > ent.getY() && this.y < ent.getY() + ent.getSize());
                if (checkinX && checkinY) {
                    String collided = "noone";
                    if (ent.getClass() == Player.class) {
                        collided = "player";
                    } else if (ent.getClass() == Enemy.class) {
                        collided = "enemy";
                    }
                    ent.hurt(damage);
                    hit = true; // set hit so no more damaging
                    // add score
                    if (ent.getClass() != Player.class) GameLoop.Instance.score++;
                    System.out.println("Collided with " + collided);
                    return true;
                }
            }
        }
        if(hit) dead = true;
        return false;
    }
    // move
    public void move() throws ConcurrentModificationException {
        double angleRad = Math.toRadians(direction);
        double hsp = Math.cos(angleRad) * speed;
        double vsp = Math.sin(angleRad) * speed;
        // update pos
        x += hsp;
        y -= vsp;
    }
    // hit
}
