////             ////
// singleton class //
////             ////
package se233.advproject2.controller;

import javafx.scene.input.KeyCode;
import se233.advproject2.example.SpaceInvaderApp;
import se233.advproject2.objects.Bullet;
import se233.advproject2.objects.Enemy;
import se233.advproject2.objects.Entity;
import se233.advproject2.objects.Player;
import se233.advproject2.view.GameScreen;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.stream.Collectors;

public class GameLoop implements Runnable {
    public static GameLoop Instance;
    public void setInstance(GameLoop inst){
        Instance = inst;
    };
    // variables
    public GameScreen platform;
    private Player player;
    private List<Entity> entities = new ArrayList<Entity>();
    private List<Enemy> enemyList = new ArrayList<Enemy>();
    public List<Bullet> bulletList = new ArrayList<Bullet>();
    public int level;
    public int score;
    private float fps = 1000.0f / 60;
    private int runtime;
    private int spawning = 0;

    // constructor
    public GameLoop(GameScreen p) {
        this.platform = p;
        this.runtime = 0;
    }
    // states
    private enum STATE {
        PreStart, Running, End
    }
    public STATE gameState = STATE.PreStart;
    /// running
    @Override
    public void run() {
        while (true){
            switch (gameState){
                case PreStart -> {
                    platform.renderText("press Space to start game", 200, 300);
                    if(platform.getKeys().contains(KeyCode.SPACE)){
                        Start();
                    }
                    try {
                        Thread.sleep((long)fps);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                case Running -> {
                    step();
                    draw();
                    try {
                        Thread.sleep((long)fps);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                case End -> {
                    platform.renderText("Game over, press enter to restart", platform.WIDTH/2, 50);
                    if(platform.getKeys().contains(KeyCode.ENTER)){
                        Start();
                    }
                    try {
                        Thread.sleep((long)fps);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    // step
    private void step() {
        bulletList.removeIf(n -> n.dead);
        // clear bullets when out of bound
        bulletList.removeIf(n -> n.getY() > platform.WIDTH + 50 || n.getY() < -50);
        for (Bullet b : bulletList) {
            b.move();
            b.bulletCollision(entities);
        }
        for (Entity ent : entities) {
            ent.step();
        }
        entities.removeIf(n -> n.dead); // cleanup on dead
        // runtime counting
        runtime++;
    }
    // draw
    ///
    private void draw(){
        // handled rendering exceptions
        try {
            // render
            platform.renderReset();
            platform.render(player);
            platform.render(entities);
            platform.renderBullets(bulletList);
            platform.renderHP(player.hp);
            platform.renderScore(score);
        } catch (NullPointerException | IndexOutOfBoundsException e){
            e.printStackTrace();
        }
    }
    // start
    public void Start(){
        /// clean up
        clear(); // clear lists
        // resets
        runtime = 0;
        level = 0;
        score = 0;
        platform.renderReset();
        /// creating
        // create player
        player = new Player(300, 600, 40);
        entities.add(player);
        // create enemies
        spawnEnemyWave();
        gameState = STATE.Running;
        System.out.println("game start");
    }
    public void spawnEnemyWave(){
        System.out.println("spawning new enemy wave");
        enemyList = entities.stream()
                .filter(ent -> ent instanceof Enemy)
                .map(ent -> (Enemy) ent)
                .toList();
        enemyList.forEach(enemy -> enemy.setY(enemy.getY() + 50));
        for (int i = 0; i < 5; i++) {
            Enemy e = new Enemy(90 + i*100, 150, 40);
            entities.add(e);
        }
    }
    // end
    public void End(){
        gameState = STATE.End;
    }
    public void clear(){
        // clear lists
        entities.clear();
        bulletList.clear();
        player = null;
    }
}
