////             ////
// singleton class //
////             ////
package se233.advproject2.controller;

import javafx.scene.input.KeyCode;
import se233.advproject2.example.SpaceInvaderApp;
import se233.advproject2.objects.*;
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
    public int level = 0;
    public int score;
    private float fps = 1000.0f / 60;
    private int runtime;
    public int enemycount = -1;
    Alarm alarm;
    // states
    private enum STATE {
        PreStart, Running, End
    } public STATE gameState = STATE.PreStart;
    // constructor
    public GameLoop(GameScreen p) {
        this.platform = p;
        this.runtime = 0;
    }
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
            // if esc leave game
            if(platform.getKeys().contains(KeyCode.ESCAPE)){
                System.exit(0);
            }
        }
    }
    // step
    private void step() {
        // step events
        if(alarm != null)alarm.step();
        for (Bullet b : bulletList) {
            b.move();
            b.bulletCollision(entities);
        } // bullet steps
        for (Entity ent : entities) {
            ent.step();
        } // entities steps
        // cleanups
        if(alarm.countdown < 0) alarm = null;
        bulletList.removeIf(n -> n.dead); // remove on hit
        bulletList.removeIf(n -> n.getY() > platform.WIDTH + 50 || n.getY() < -50); // remove when out of bound
        entities.removeIf(n -> n.dead); // remove on dead
        // spawn next wave on cleared
        if (enemycount == 0){
            spawnWaveInit(3);
            enemycount--;
        }
        // runtime counting
        runtime++;
        if(runtime == 600){ // next wave every 10 sec
            spawnWaveInit(3);
        }
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
            if(alarm != null)platform.renderText("Enemy spawn in " + alarm.countdown, 300,300);
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
        alarm = new Alarm(5);
        gameState = STATE.Running;
        System.out.println("game start");
    }
    public void spawnWaveInit(int count){
        if(alarm == null) alarm = new Alarm(count);
    }
    public void spawnEnemyWave(){
        level++; // next level
        runtime = 0; // reset runtime
        System.out.println("spawning new enemy wave");
        enemyList = entities.stream()
                .filter(ent -> ent instanceof Enemy)
                .map(ent -> (Enemy) ent)
                .toList();
        enemyList.forEach(enemy -> enemy.setY(enemy.getY() + 50));
        enemycount = enemyList.size();
        for (int i = 0; i < 5; i++) {
            Enemy e = new Enemy(90 + i*100, 150, 40, level);
            entities.add(e);
            enemycount++;
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
