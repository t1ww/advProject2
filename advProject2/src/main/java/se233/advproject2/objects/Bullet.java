package se233.advproject2.objects;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import se233.advproject2.controller.GameLoop;
import se233.advproject2.model.AnimatedSprite;
import se233.advproject2.view.GameScreen;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.stream.Collectors;

public class Bullet extends Pane {
    GameLoop game = GameLoop.Instance;
    GameScreen platform = GameLoop.Instance.platform;
    Image characterImg;
    AnimatedSprite imageView;
    // default
    String spritePath;
    public boolean dead = false;
    double x, y, direction, speed;
    int damage = 1;
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
    List<Entity> hit = new ArrayList<>();
    Class checkFor;
    private int changeDirTime = 45; // 1.30s
    private boolean homing;
    enum type {
        normal,piercing,homing,targetting
    }
    type bulletType = type.normal;
    Entity target;

    public void setPiercing(){bulletType = type.piercing;}
    public void setTargetting(){bulletType = type.targetting;}
    public void setHoming(){bulletType = type.homing;}
    public Bullet(double x, double y, double direction, double speed, Class checkFor){
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.speed = speed;
        this.checkFor = checkFor;
        this.homing = false;
        game.bulletList.add(this);
    }
    public Bullet(double x, double y, double direction, double speed, Class checkFor, int damage){
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.speed = speed;
        this.checkFor = checkFor;
        this.damage = damage;
        this.homing = false;
        game.bulletList.add(this);
    }
    boolean check;
    public void bulletCollision(List<Entity> entityList) throws ConcurrentModificationException {
        synchronized(game.getEntities()) {
            for (Entity ent: game.getEntities()) {
                if(bulletType == type.piercing){
                    check = !hit.contains(ent);
                }else check = hit.isEmpty();
                if(check && checkFor.isAssignableFrom(ent.getClass())) { // check for targeted entity and it's subclasses
                    int buffer = 15;
                    if(checkFor == Player.class){ buffer = -5; }
                    boolean checkinX = (this.x > ent.getX() - buffer && this.x < ent.getX() + ent.getSize() + buffer);
                    boolean checkinY = (this.y > ent.getY() && this.y < ent.getY() + ent.getSize());
                    if (checkinX && checkinY) {
                        ent.hurt(damage);
                        hit.add(ent); // set hit so no more damaging
                        // add score
                        if (ent.getClass() == Enemy.class) {
                            game.setScore(game.getScore()+1); // score add
                        }else if (ent.getClass() == EnemyHighRank.class){
                            game.setScore(game.getScore()+2); // score add
                        }
                        // create explosion effect
                        new Particle(getX()-32,getY()-32,"assets/bulletFlashSprite-Sheet.png",
                                64,64,0,0,true,4);
                        // create debris particle
                        for (int i = 0; i < 6; i++) {
                            new Particle(getX()-32,getY()-32,"assets/debris.png",
                                    3,3,10+(Math.random()*10), 10, Math.random()*360,false,1);
                        }
                        System.out.println("Collided with " + ent.name);
                        return;
                    }
                }
            }
        }
        if(!hit.isEmpty() && bulletType != type.piercing) dead = true;
    }
    public void step(){
        move();
        bulletCollision(game.getEntities());
    }
    // move
    public void move() throws ConcurrentModificationException {
        switch (bulletType){
            case homing -> {
                if (target == null) {
                    List<Entity> nonPlayerEntities = game.getEntities().stream()
                            .filter(e -> !(e instanceof Player))
                            .toList();
                    if (!nonPlayerEntities.isEmpty()) {
                        target = nonPlayerEntities.get((int) (Math.random() * nonPlayerEntities.size()));
                    }else target = null;
                } else {
                    // check if target is still exist
                    List<Entity> nonPlayerEntities = game.getEntities().stream()
                            .filter(e -> !(e instanceof Player))
                            .toList();
                    if(!nonPlayerEntities.contains(target)){target = null;}
                    //
                    double x1 = x, y1 = y;
                    double x2 = target.x, y2 = target.y;
                    // Calculate the angle between the two points
                    double angle = Math.toDegrees(Math.atan2(y2 - y1, x2 - x1));
                    direction = -angle;
                }
            }
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
