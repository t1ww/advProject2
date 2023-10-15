package se233.advproject2.objects;

import se233.advproject2.controller.GameLoop;
import se233.advproject2.view.GameScreen;

public class Entity {
    GameLoop game = GameLoop.Instance;
    GameScreen platform = GameLoop.Instance.platform;
    String name;
    // init variables
    public boolean dead = false;
    public int hp;
    private double x, y;
    private int size;
    public Entity(double x, double y, int size){
        this.x = x;
        this.y = y;
        this.size = size;
        this.name = "entity";
        this.hp = 1;
    }
    public void step(){}
    public double getX() { return x; }
    public double getY() { return y; }
    public int getSize() { return size;}
    public void setX(double _x) { x = _x; }
    public void setY(double _y) { y = _y; }
    public void hurt(int dmg){
        hp -= dmg;
        if(hp <= 0){
            dead = true;
            if(getClass() == Enemy.class) game.enemycount--;
            System.out.println(name + " is dead");
        }else System.out.println(name + " now has " + hp + " hp");
    }
}
