package se233.advproject2.controller;


import se233.advproject2.objects.Entity;
import se233.advproject2.view.GameScreen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DrawingLoop implements Runnable {
    private static final int DEFAULT_FRAME_RATE = 60;
    private static final float MS_IN_SECOND = 1000.0f;
    private final int frameRate;
    private final float interval;
    private final GameLoop gameLoop;
    public DrawingLoop(GameLoop gameLoop) {
        this.gameLoop = gameLoop;
        this.frameRate = DEFAULT_FRAME_RATE;
        this.interval = MS_IN_SECOND / frameRate;
    }
    private void paint(Entity e) {
        e.repaint();
    }
    @Override
    public void run() {
        GameLoop game = GameLoop.Instance;
        while (true) {
            float time = System.currentTimeMillis();
            List<Entity> entities = Collections.synchronizedList(new ArrayList<>());
            synchronized(entities) {
                for (Entity e : entities) {
                    paint(e);
                }
            }
            time = System.currentTimeMillis() - time;
            if (time < interval) {
                safeSleep((long) (interval - time));
            } else {
                safeSleep((long) (interval - (interval % time)));
            }
        }
    }
    private void safeSleep(long duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore the interrupted status
        }
    }
}
