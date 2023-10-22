////             ////
// singleton class //
////             ////
package se233.advproject2.controller;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import se233.advproject2.objects.*;
import se233.advproject2.view.GameScreen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameLoop implements Runnable {
    public static GameLoop Instance;
    public void setInstance(GameLoop inst){
        Instance = inst;
    };
    // variables
    public GameScreen platform;
    public DrawingLoop drawingLoop;
    public Player player;
    private final List<Entity> entities = Collections.synchronizedList(new ArrayList<>());
    private List<Enemy> enemyList = new CopyOnWriteArrayList<Enemy>();
    public List<Bullet> bulletList = new CopyOnWriteArrayList<Bullet>();
    public List<Particle> particleList = new CopyOnWriteArrayList<Particle>();
    private float interval = 1000.0f / 60;
    private int waveCD = 1200;
    public int waveCD_count = 0;
    public int level = 0;
    private int score;
    public int runtime;
    public int enemyCount = -1;
    public Alarm alarm;
    public boolean creationPhase;
    // getter setter
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        if (this.score != score) {
            this.score = score;
            logScoreChange();
        }
    }

    public Player getPlayer() {
        return player;
    }

    // states
    private enum STATE {
        PreStart, Running, End
    } public STATE gameState = STATE.PreStart;
    private enum WAVE {
        Creeps, Boss
    } public WAVE gameWave = WAVE.Creeps;
    public void setCreepsWave(){
        gameWave = WAVE.Creeps;
    }
    public void setBossWave(){
        gameWave = WAVE.Boss;
    }
    // constructor
    public GameLoop(GameScreen p) {
        this.platform = p;
        this.runtime = 1;
    }

    /// // METHODS // ///
    // misc vars
    double xStart = -200,xto = 200,_x = xStart;
    double xStart2 = -300,xto2 = 200,_x2 = xStart;
    /// running
    @Override
    public void run() {
        while (true){
            float time = System.currentTimeMillis();
            switch (gameState){
                case PreStart -> {
                    _x += lerp(_x, xto, .07);
                    _x2 += lerp(_x2, xto2, .05);
                    platform.renderReset();
                    // key instruction
                    String str = "A, D or <-arrow-> to move" +
                            "\nPRESS SPACE TO SHOOT" +
                            "\nPRESS LSHIFT TO SPRINT";
                    platform.renderText(str, (int)_x, 300);
                    platform.renderText("press Space to start game", (int)_x2, 400);
                    if(platform.getKeys().contains(KeyCode.SPACE)){
                        Start();
                    }
                }
                case Running -> {
                    try {
                        step();
                        Platform.runLater(() -> {
                            try {
                                draw(); // ui
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });
                    } catch (Exception e){
                        logger.error("Exception caught during game loop:", e);
                    }
                }
                case End -> {
                    String gametime = getTime(runtime);
                    _x += lerp(_x, xto, .07);
                    _x2 += lerp(_x2, xto2, .05);
                    Platform.runLater(() -> {
                        platform.renderReset();
                        platform.renderText("YOU LASTED : " + gametime, (int) _x, 300);
                        platform.renderText("Game OVER, press enter to restart", (int) _x2, 330);
                    });
                    if(platform.getKeys().contains(KeyCode.ENTER)){
                        Start();
                    }
                }
            }
            // if esc leave game
            if(platform.getKeys().contains(KeyCode.ESCAPE)){
                System.exit(0);
            }
            time = System.currentTimeMillis() - time;
            /// delay a bit based on fps
            if (time < interval) {
                try {
                    Thread.sleep((long) (interval - time));
                } catch (InterruptedException e) {logger.error("Interruption caught during game loop:", e);
                }
            } else {
                try {
                    Thread.sleep((long) (interval - (interval % time)));
                } catch (InterruptedException e) {
                }
            }
        }
    }

    /// // METHODS // ///
    private void step() throws Exception {
        // step events
        if (alarm != null) alarm.step();

        // Move and check bullet collisions
        List<Bullet> bulletsToRemove = new ArrayList<>();
        for (Bullet b : bulletList) {
            b.move();
            b.bulletCollision(entities);
            if (b.dead || b.getY() > platform.WIDTH + 50 || b.getY() < -50) {
                bulletsToRemove.add(b);
            }
        }
        bulletList.removeAll(bulletsToRemove);

        // Update entities
        for (Entity ent : entities) {
            ent.step();
        }

        // Remove dead entities
        entities.removeIf(ent -> ent.dead);
//        Iterator<Entity> entityIterator = entities.iterator();
//        while (entityIterator.hasNext()) {
//            Entity ent = entityIterator.next();
//            if (ent.dead) {
//                entityIterator.remove();
//            }
//        }

        /// handling particles
        synchronized(particleList) {
            for (Particle p : particleList) {
                p.move();
            }
        }

        /// only during creeps wave
        if (gameWave.equals(WAVE.Creeps)) {
            waveCD_count++;
            // next wave every given time
            if (waveCD_count == waveCD) {
                spawnWaveInit(3);
            }
        }

        // runtime counting
        runtime++;
        // cleanups
        if (Alarm.countdown < 0) alarm = null;
    }

    /// draw
    private void draw() throws Exception {
        /// render setup
        platform.renderReset();
        platform.renderBullets(bulletList);
        // draw ui
        platform.renderHP(player.hp);
        platform.renderText("Score : " + score, 10, 10);
        platform.renderText("Level : " + level, 10, 30);
        String time = getTime(runtime);
        platform.renderText("Time : " + time, 100, 10);
        platform.renderText("Enemy count : " + enemyCount, 100, 30);
        String spawningText = (gameWave == WAVE.Creeps) ? "A Wave of Enemy Creeps spawning in " : "Boss spawning in ";
        if (alarm != null) platform.renderText(spawningText + alarm.countdown, 150, 100);
    }
    //// enemies handling
    public void spawnWaveInit(int count){
        alarm = new Alarm(count);
    }
    public void enemiesSpawn(){ // put this in alarm
        enemyList = entities.stream()
                .filter(ent -> ent instanceof Enemy)
                .map(ent -> (Enemy) ent)
                .toList();
        enemyCount = enemyList.size(); // recheck size
        if(gameWave == WAVE.Creeps) { // wave check
            /// create a frontline wave of enemies
            if(creationPhase){
                System.out.println("creating frontline");
                for (int i = 0; i < 9; i++) {
                    entities.add(new Enemy(200 + (i * 34), 150, 32, level));
                    enemyCount++;
                }
                // started the game
                creationPhase = false;
            }
            // move all previous enemies down
            enemiesMoveDown();
            /// create a wave of enemies
            System.out.println("spawning creep wave");
            // left wall
            for (int i = 0; i < 3; i++) {
                entities.add(new Enemy(200 + (i * 34), 150, 32, level));
                enemyCount++;
            }
            // second tier //
            for (int i = 0; i < 3; i++) {
                entities.add(new EnemyHighRank(302 + (i * 34), 150, 32, level));
                enemyCount++;
            }
            // right wall
            for (int i = 0; i < 3; i++) {
                entities.add(new Enemy(404 + (i * 34), 150, 32, level));
                enemyCount++;
            }
            waveCD_count = 0; // reset counter
        } else { // boss wave
            // boss creation
            entities.add(new Boss((platform.WIDTH / 2) , 150, 64, level));
            enemyCount++;
        }
        waveCD_count = 0; // reset spawn wave
    } 
    public void enemiesMoveDown(){
        System.out.println("moving down");
        enemyList = entities.stream()
                .filter(ent -> ent instanceof Enemy)
                .map(ent -> (Enemy) ent)
                .toList();
        enemyCount = enemyList.size(); // recheck size
        Iterator<Enemy> enemyIterator = enemyList.iterator();
        while(enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            enemy.moveDown();
        }
    }
    // game setup methods
    public void Start(){
        // start drawing loop
        drawingLoop.running = true;
    /// / clean up
        clear(); // clear lists
        // reset variables
        creationPhase = true;
        runtime = 0;level = 0;score = 0;
        waveCD_count = 0;
        enemyCount = -1;
        platform.renderReset();
    /// / creating
        // create player
        player = new Player(300, 600, 32);
        entities.add(player);
        // create enemies
        alarm = new Alarm(1);
        gameState = STATE.Running;
        gameWave = WAVE.Creeps;
//        gameWave = WAVE.Boss; // forcing boss for testing
    /// / set pos to ease in from
        xStart = -200;xto = 200;_x = xStart;
        xStart2 = -400;xto2 = 200;_x2 = xStart;
    /// / log start
        logger.info("Game started.");
    }
    public void End(){
        // stop drawing loop
        drawingLoop.running = false;
        Platform.runLater(()->{
            entities.forEach(ent ->{
                platform.getChildren().remove(ent);
            }); // clear render
        });
        gameState = STATE.End;
        // set pos to ease in from
        xStart = -200;xto = 200;_x = xStart;
        xStart2 = -400;xto2 = 200;_x2 = xStart;
    /// / log end
        logger.info("Game ended. Lasted: " + getTime(runtime));
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
    public double lerp(double v1, double v2, double amount){
        return (v2-v1)*amount;
    }
    public void gameRestart(){
        End();
        clear();
        Start();
    }
    public List<Entity> getEntities() {
        return this.entities;
    }
    // logger //
    private static final Logger logger = LogManager.getLogger(GameLoop.class);
    private void logScoreChange() {
        logger.info("Score has been updated to: " + score);
    }
}
