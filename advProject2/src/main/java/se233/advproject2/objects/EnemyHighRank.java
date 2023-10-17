package se233.advproject2.objects;

import javafx.scene.paint.Color;

public class EnemyHighRank extends Enemy {
    /// constructor
    public EnemyHighRank(double x, double y, int size, int lvl){
        super(x,y,size,lvl, "assets/enemyHighRankSprite-Sheet.png");
        this.sprite = Color.DARKRED;
        this.hp += Math.ceilDiv(this.hp,2);
    }
    public void shoot(){ // override the shoot to target player
        // create bullet
        Bullet b = new Bullet(getX() + (getSize()/2), getY() + getSize() + 5, targetPlayerDir(), 4, Player.class);
        game.bulletList.add(b);
        cdReset();
    }
}
