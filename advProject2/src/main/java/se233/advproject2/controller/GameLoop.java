////             ////
// singleton class //
////             ////
package se233.advproject2.controller;

import javafx.scene.input.KeyCode;
import se233.advproject2.objects.*;
import se233.advproject2.view.GameScreen;

import java.util.ArrayList;
import java.util.List;

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
    public int enemyCount = -1;
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
    double xStart = -200,xto = 200,_x = xStart;
    double xStart2 = -300,xto2 = 200,_x2 = xStart;
    boolean pause = false;
    @Override
    public void run() {
        while (true){
            if(platform.getKeyPressed().equals(KeyCode.BACK_SPACE)){
                gameRestart();
            }
            if(pause)continue; // simple pause
            switch (gameState){
                case PreStart -> {
                    _x += lerp(_x, xto, .05);
                    platform.renderReset();
                    platform.renderText("press Space to start game", (int)_x, 300);
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
                    _x += lerp(_x, xto, .05);
                    _x2 += lerp(_x2, xto2, .04);
                    platform.renderReset();
                    platform.renderText("YOU LASTED : " + time, (int) _x2, 300);
                    platform.renderText("Game OVER, press enter to restart", (int) _x, 330);
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
        if (enemyCount == 0 && alarm == null)  { // only when haven't init (alarm was null)
            // change next wave
            gameWave = (WAVE.Creeps == gameWave)? WAVE.Boss : WAVE.Creeps;
            spawnWaveInit(3);
            enemyCount--;
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
            String time = getTime(runtime);
            platform.renderText("Time : " + time, 100, 10);
            platform.renderText("Enemy count : " + enemyCount, 100, 30);
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
        // move old enemies
        enemyList.forEach(enemy -> enemy.move());
        enemyCount = enemyList.size();
        if(gameWave == WAVE.Creeps) { // wave check
            // small wave
            for (int i = 0; i < 5; i++) {
                entities.add(new Enemy(90 + i * 100, 150, 32, level));
                enemyCount++;
            }
            waveCD_count = 0; // reset counter
        } else {
            // boss wave
            entities.add(new Boss((platform.WIDTH / 2) , 150, 64, level));
            enemyCount++;
        }
    }
    // game setup methods
    public void Start(){
        /// clean up
        clear(); // clear lists
        // reset variables
        runtime = 0;level = 0;score = 0;
        waveCD_count = 0;
        enemyCount = -1;
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
        xStart = -200;xto = 200;_x = xStart;
        xStart2 = -400;xto2 = 200;_x2 = xStart;
    }
    public void clear(){
        // clear lists
        entities.clear();
        bulletList.clear();
        player = null;
    }
    public void gameRestart(){
        End();
        clear();
        Start();
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
    public double lerp(double v1, double v2, double amount){
        return (v2-v1)*amount;
    }
}
