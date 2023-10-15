package se233.advproject2.objects;

public class Enemy extends Entity {
    // var init
    int shootCD;
    int Level;
    // constructor
    public Enemy(double x, double y, int size, int lvl) {
        super(x,y,size);
        this.name = "small enemy";
        this.Level = lvl;
        this.hp += lvl;
        cdReset();
    }
    // state machine
    // alive
    // shoot
    public void step(){
        if(shootCD < 0){
            shoot();
        }else {
            shootCD--;
        }
    }
    public void shoot(){
        // create bullet
        Bullet b = new Bullet(getX() + (getSize()/2), getY() + getSize() + 5, -90, 10, Player.class);
        game.bulletList.add(b);
        cdReset();
    }
    private void cdReset(){
        shootCD = (int)(Math.random()*100) + 30 - (Level * 20);
    }
    // dead
}
