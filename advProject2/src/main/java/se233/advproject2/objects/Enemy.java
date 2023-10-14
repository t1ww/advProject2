package se233.advproject2.objects;

public class Enemy extends Entity {
    // var init
    // constructor
    public Enemy(double x, double y, int size) {
        super(x,y,size);
        this.name = "small enemy";
        this.hp = 2;
        cdReset();
    }
    // state machine
    // alive
    // shoot
    int shootCD;
    public void step(){
        if(shootCD < 0){
            shoot();
        }else {
            shootCD--;
        }
    }
    public void shoot(){
        // create bullet
        Bullet b = new Bullet(getX() + (getSize()/2), getY() + getSize() + 5, -90, 10);
        game.bulletList.add(b);
        cdReset();
    }
    private void cdReset(){
        shootCD = (int)(Math.random()*100) + 30;
    }
    // dead
}
