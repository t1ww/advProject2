////             ////
// singleton class //
////             ////
package se233.advproject2.controller;

import se233.advproject2.view.GameScreen;

public class GameLoop implements Runnable {
    // variables
    private GameScreen platform;

    private boolean running;
    // constructor
    public GameLoop(GameScreen p) {
        this.platform = p;
        running = true;
    }
    // states


    @Override
    public void run() {
        // run

    }
    // start
    public static void Start(){

    }
    // end
    public static void End(){

    }

}
