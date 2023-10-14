package se233.advproject2.objects;

import se233.advproject2.controller.GameLoop;

public class Entity {
    GameLoop game = GameLoop.Instance;
    public Entity(double x, double y, int size){
        this.x = x;
        this.y = y;
        this.size = size;
    }
    // init variables
    private double x, y;
    private int size;
    public double getX() { return x; }
    public double getY() { return y; }
    public int getSize() { return size;}
    public void setX(double _x) { x = _x; }
    public void sety(double _y) { y = _y; }
}
