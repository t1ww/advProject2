package se233.advproject2.objects;

import javafx.scene.layout.Pane;

public class SpecialBullet extends Bullet {
    private String sprPath;
    public SpecialBullet(String sprPath, double x, double y, double direction, double speed, Class checkFor) {
        super(x, y, direction, speed, checkFor);
    }

    public SpecialBullet(String sprPath, double x, double y, double direction, double speed, Class checkFor, int damage) {
        super(x, y, direction, speed, checkFor, damage);
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
                        // create explosion effect
                        // new Particle(-100,-100,"",800,700); // create the sprite that covers whole screen
                        // stun the enemies
                        for (Entity ent: game.getEntities()) {
                            if(isAssociateWith(Enemy)){
                                ent.stun();
                            }
                        }
                        // clear the bullets
                        game.bulletList.clear(); // use platform.runlater iteration to clear platform children and then clear the list
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
}
