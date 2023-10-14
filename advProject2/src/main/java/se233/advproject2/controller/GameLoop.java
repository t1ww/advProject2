////             ////
// singleton class //
////             ////
package se233.advproject2.controller;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import se233.advproject2.example.SpaceInvaderApp;
import se233.advproject2.objects.Enemy;
import se233.advproject2.objects.Entity;
import se233.advproject2.objects.Player;
import se233.advproject2.view.GameScreen;

import java.util.ArrayList;
import java.util.List;

public class GameLoop implements Runnable {
    public static GameLoop Instance;
    // variables
    public GameScreen platform;
    private Player player;
    private boolean running;
    private List<Entity> entities = new ArrayList<Entity>();
    public int level;
    public int score;
    private float fps = 1000.0f / 60;

    // constructor
    public GameLoop(GameScreen p) {
        this.platform = p;
        running = true;
    }
    // states
    private enum STATE {
        PreStart,
        Running,
        End
    }
    /// running
    @Override
    public void run() {
        while (running){
            step();
            draw();
            try {
                Thread.sleep((long)fps);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    // step
    private void step(){
        // run
        player.move(platform);
    }
    // draw
    private void draw(){
        platform.render(entities, score);
    }
    ///
    // start
    public void Start(){
        level = 0; // reset level
        score = 0;
        // create player
        player = new Player(300, 600, 40);
        entities.add(player);
        // create enemies
        for (int i = 0; i < 5; i++) {
            entities.add(new Enemy(90 + i*100, 150, 40));
        }
        System.out.println("game start");
    }
    // end
    public void End(){
        entities.clear();
    }
}
