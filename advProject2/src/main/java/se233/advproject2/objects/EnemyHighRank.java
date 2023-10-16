package se233.advproject2.objects;

import javafx.scene.paint.Color;

public class EnemyHighRank extends Enemy {
    /// constructor
    public EnemyHighRank(double x, double y, int size, int lvl){
        super(x,y,size,lvl);
        this.sprite = Color.DARKRED;
        this.hp += Math.ceilDiv(this.hp,2);
    }
}
