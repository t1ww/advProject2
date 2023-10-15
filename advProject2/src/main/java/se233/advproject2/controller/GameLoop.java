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
    private float fps = 1000.0f / 60;
    private int waveCD = 800;
    int waveCD_count = 0;
    public int level = 0;
    public int score;
    private int runtime;
    public int enemycount = -1;
    Alarm alarm;
    // states
    private enum STATE {
        PreStart, Running, End
    } public STATE gameState = STATE.PreStart;
    private enum WAVE {
        Creeps, Boss
    } public WAVE gameWave = WAVE.Creeps;
    // constructor
    public GameLoop(GameScreen p) {
        this.platform = p;
        this.runtime = 1;
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
                    String time = getTime(runtime);
                    platform.renderText("YOU LASTED : " + time, 200, 300);
                    platform.renderText("Game OVER, press enter to restart", 200, 330);
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
    /// step
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
        if (enemycount == 0 && alarm == null)  { // only when haven't init (alarm was null)
            // change next wave
            gameWave = (WAVE.Creeps == gameWave)? WAVE.Boss : WAVE.Creeps;
            spawnWaveInit(3);
            enemycount--;
        }
        // runtime counting
        runtime++;
        //,/ only on creeps wave
        if(gameWave.equals(WAVE.Creeps)) {
            waveCD_count++;
            // next wave every given time
            if (waveCD_count == waveCD) {
                spawnWaveInit(3);
            }
        }
    }
    /// draw
    private void draw(){
        // handled rendering exceptions
        try {
            // render
            platform.renderReset();
            platform.render(player);
            platform.render(entities);
            platform.renderBullets(bulletList);
            platform.renderHP(player.hp);
            platform.renderText("Score : " + score , 10, 10);
            platform.renderText("Level : " + level , 10, 30);
            platform.renderText("Enemy count : " + enemycount, 100, 10);
            String spawning = (gameWave == WAVE.Creeps) ? "Enemy Creeps" : "Boss" ;
            if(alarm != null)platform.renderText(spawning + " spawn in " + alarm.countdown, 300,300);
        } catch (NullPointerException | IndexOutOfBoundsException e){
            e.printStackTrace();
        }
    }
    public void spawnWaveInit(int count){
        alarm = new Alarm(count);
    }
    public void spawnEnemyWave(){
        System.out.println("spawning new enemy wave");
        enemyList = entities.stream()
                .filter(ent -> ent instanceof Enemy)
                .map(ent -> (Enemy) ent)
                .toList();
        enemyList.forEach(enemy -> enemy.setY(enemy.getY() + 50));
        enemycount = enemyList.size();
        if(gameWave == WAVE.Creeps) { // wave check
            // small wave
            for (int i = 0; i < 5; i++) {
                entities.add(new Enemy(90 + i * 100, 150, 32, level));
                enemycount++;
            }
            waveCD_count = 0; // reset counter
        } else {
            // boss wave
            entities.add(new Boss((platform.WIDTH / 2) , 150, 64, level));
            enemycount++;
        }
    }
    // game setup methods
    public void Start(){
        /// clean up
        clear(); // clear lists
        // reset variables
        runtime = 0;level = 0;score = 0;
        waveCD_count = 0;enemycount = -1;
        platform.renderReset();
        /// creating
        // create player
        player = new Player(300, 600, 32);
        entities.add(player);
        // create enemies
        alarm = new Alarm(5);
        gameState = STATE.Running;
        System.out.println("game start");
    }
    public void End(){
        gameState = STATE.End;
    }
    public void clear(){
        // clear lists
        entities.clear();
        bulletList.clear();
        player = null;
    }

    /// misc methods
    public String getTime(int time){
        String str = "";
        int hours = time/216000;
        time -= hours * 216000;
        int minutes = time/3600;
        time -= minutes * 3600;
        int seconds = time/60;
        str = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        return str;
    }
}
