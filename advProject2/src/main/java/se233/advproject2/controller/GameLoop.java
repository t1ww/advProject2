////             ////
// singleton class //
////             ////
package se233.advproject2.controller;

import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import se233.advproject2.objects.*;
import se233.advproject2.view.GameScreen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameLoop implements Runnable {
    private static GameLoop instance = null;
    public static synchronized GameLoop getInstance()
    {
        return instance;
    }
    // Constructor
    public GameLoop(GameScreen p) {
        instance = this;
        this.platform = p;
        this.runtime = 1;
    }

    // Variables
    private final GameScreen platform;
    public Player player;
    private final List<Entity> entities = Collections.synchronizedList(new ArrayList<>());
    private List<Enemy> enemyList = new CopyOnWriteArrayList<>();
    private final List<Bullet> bulletList = new CopyOnWriteArrayList<>();
    private final List<Particle> particleList = new CopyOnWriteArrayList<>();
    private final List<Collectibles> collectiblesList = new CopyOnWriteArrayList<>();
    private int waveCD_count = 0;
    private int score;
    private int runtime;
    private int enemyCount = -1;
    private Alarm alarm;
    private boolean creationPhase;
    public int level = 0;

    // Getter
    public Alarm getAlarm() {
        return alarm;
    }

    public boolean isCreationPhase() {
        return creationPhase;
    }

    public int getRuntime() {
        return runtime;
    }

    public int getScore() {
        return score;
    }

    public int getEnemyCount() {
        return enemyCount;
    }

    public GameScreen getPlatform() {
        return platform;
    }

    public List<Bullet> getBulletList() {
        return bulletList;
    }

    public List<Collectibles> getCollectiblesList() {
        return collectiblesList;
    }

    public List<Particle> getParticleList() {
        return particleList;
    }

    public Player getPlayer() {
        return player;
    }
    // Setter
    public void setAlarm(Alarm alarm) {
        this.alarm = alarm;
    }

    public void setCreationPhase(boolean creationPhase) {
        this.creationPhase = creationPhase;
    }

    public void setEnemyCount(int enemyCount) {
        this.enemyCount = enemyCount;
    }

    public void setScore(int score) {
        if (this.score != score) {
            this.score = score;
            logScoreChange();
        }
    }

    // States
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
                    _x += linearInterpolation(_x, xto, .07);
                    _x2 += linearInterpolation(_x2, xto2, .05);
                    platform.renderReset();
                    // key instruction
                    String str = """
                            A, D or <-arrow-> to move
                            PRESS SPACE TO SHOOT
                            PRESS G TO USE BOMB (STUN AND CLEAR BULLETS)
                            PRESS LSHIFT TO SPRINT""";
                    platform.renderText(str, (int)_x, 300);
                    platform.renderText("press Space to start game", (int)_x2, 400);
                    if(platform.getKeys().contains(KeyCode.SPACE)){
                        Start();
                    }
                }
                case Running -> {
                    try {
                        Platform.runLater(() -> {
                            try {
                                step();
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
                    String gameTime = getTime(runtime);
                    _x += linearInterpolation(_x, xto, .07);
                    _x2 += linearInterpolation(_x2, xto2, .05);
                    Platform.runLater(() -> {
                        platform.renderReset();
                        platform.renderText("YOU LASTED : " + gameTime, (int) _x, 300);
                        platform.renderText("Game OVER, press enter to restart", (int) _x2, 330);
                        platform.renderText("SCORE : " + score, 200,360);
                    });
                    if(platform.getKeys().contains(KeyCode.ENTER)){
                        Start();
                    }
                }
            }
            // if esc leave game
            if(platform.getKeys().contains(KeyCode.ESCAPE)){
                Platform.runLater(() -> {
                    Stage stage = (Stage) platform.getScene().getWindow();
                    stage.close();
                });
            }
            time = System.currentTimeMillis() - time;
            // Delay a bit based on delta-time
            float interval = 1000.0f / 60;
            if (time < interval) {
                try {
                    Thread.sleep((long) (interval - time));
                } catch (InterruptedException e) {
                    logger.error("Interruption caught during game loop:", e);
                }
            } else {
                try {
                    Thread.sleep((long) (interval - (interval % time)));
                } catch (InterruptedException e) {
                    logger.error("Interruption caught during game loop:", e);
                }
            }
        }
    }

    /// // METHODS // ///
    private void step() throws Exception {
        // Small chance
        if((int)(Math.random()*30) == 1){
            // create stars (decor)
            String sprPath = "assets/blackDot.png";
            new Particle(Math.random()*800 -100,-100, sprPath, 64,64,1, 1,-85 + (Math.random()*10),false,1);
        }

        // Move and check bullet collisions
        List<Bullet> bulletsToRemove = new ArrayList<>();
        for (Bullet b : bulletList) {
            b.step();
            if (b.dead || b.getY() > GameScreen.WIDTH + 50 || b.getY() < -50) {
                bulletsToRemove.add(b);
            }
        }
        bulletList.removeAll(bulletsToRemove);

        // Update entities
        for (Entity ent : entities) {
            ent.step();
        }

        // Handling particles
        synchronized(particleList) {
            for (Particle p : particleList) {
                p.step();
            }
        }
        // Handling Collectibles
        synchronized(collectiblesList) {
            for (Collectibles c : collectiblesList) {
                c.step();
            }
        }

        if(stun) { // Wave stop stun
            // Count then check
            if(--stunTimer < 0){
                stun = false;
            }
        } else {
            // runs while not stun
            if (gameWave.equals(WAVE.Creeps)) {
                waveCD_count++;
                // next wave every given time
                int waveCD = 1200;
                if (waveCD_count == waveCD) {
                    spawnWaveInit(3);
                }
                // alarm events
                if (alarm != null) alarm.step();
            }else if (alarm != null) alarm.step();
        }

        // runtime counting
        runtime++;
        // cleanups
        if (Alarm.countdown < 0) alarm = null;
    }

    // DRAW
    private void draw() throws Exception {
        // Render reset
        platform.renderReset();

        // Draw the finish line for enemy
        platform.renderDeadLine();
        platform.renderBullets(bulletList);

        // Draw ui
        if(player != null) {
            double px = player.getX();
            double py = player.getY();
            platform.renderHP(player.hp, px, py);
            platform.renderSpecialAmmo(player.getSpecialAmmo(), px,py);
            if(!player.isNormal())platform.renderText("Ammo : " + player.getAmmo(), px + 32, py-10);
        }

        platform.renderText("Score : " + score, 10, 10);
        platform.renderText("Level : " + level, 10, 30);
        String time = getTime(runtime);
        platform.renderText("Time : " + time, 100, 10);
        platform.renderText("Enemy count : " + enemyCount, 100, 30);
        String spawningText = (gameWave == WAVE.Creeps) ? "A Wave of Enemy Creeps spawning in " : "Boss spawning in ";
        if (alarm != null) platform.renderText(spawningText + alarm.countdown, 150, 100);
    }

    // Enemies handling
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

            // Create a wave of enemies
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
            entities.add(new Boss(((double) GameScreen.WIDTH / 2) , 150, 64, level));
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
        for (Enemy enemy : enemyList) {
            enemy.moveDown();
        }
    }

    // Stun feature
    boolean stun = false;
    int stunTimer = 0;

    // Only during creeps waves
    public void stun(){
        if(gameWave == WAVE.Creeps) {
            stun = true;
            stunTimer = 60;
        }
    }

    // Game setup methods
    public void Start(){
        // Start drawing loop
        DrawingLoop.getInstance().running = true;
        // Reset variables
        creationPhase = true;
        runtime = 0;level = 0;score = 0;
        waveCD_count = 0;
        enemyCount = -1;
        platform.renderReset();

        // Create player
        player = new Player(300, 600, 32);
        entities.add(player);
        // Create enemies
        alarm = new Alarm(1);
        gameState = STATE.Running;
        gameWave = WAVE.Creeps;
        // Set position coords to ease-in from
        xStart = -200;xto = 200;_x = xStart;
        xStart2 = -400;xto2 = 200;_x2 = xStart;
        // Log "start game"
        logger.info("Game started.");
    }
    public void End(){
        // Clean up
        player = null;
        clear_list();

        // Stop drawing loop
        DrawingLoop.getInstance().running = false;
        gameState = STATE.End;

        // Set position coords to ease-in from
        xStart = -200;xto = 200;_x = xStart;
        xStart2 = -400;xto2 = 200;_x2 = xStart;
        // Log end
        logger.info("Game ended. Lasted: " + getTime(runtime));
    }
    public void clear_list(){
        Platform.runLater(()->{
            entities.clear();
            particleList.clear();
            bulletList.clear();
            platform.reset();
        });
    }
    // Misc methods
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

    // Linear interpolation
    // : returns the number between 2 values (range 0 - 1)
    public double linearInterpolation(double v1, double v2, double amount){
        return (v2-v1)*amount;
    }

    public List<Entity> getEntities() {
        return this.entities;
    }

    // logger //
    public static final Logger logger = LogManager.getLogger(GameLoop.class);
    private void logScoreChange() {
        logger.info("Score has been updated to: " + score);
    }
}
