package se233.advproject2.controller;

import se233.advproject2.objects.Entity;

import java.util.List;

public class DrawingLoop implements Runnable {
    private static final DrawingLoop instance = new DrawingLoop();
    public static synchronized DrawingLoop getInstance()
    {
        return instance;
    }

    private static final int DEFAULT_FRAME_RATE = 60;
    private static final float MS_IN_SECOND = 1000.0f;

    private final int frameRate;
    private final float interval;
    private final GameLoop game;
    private final List<Entity> entities;

    public boolean running;

    public DrawingLoop() {
        this.game = GameLoop.getInstance();
        this.frameRate = DEFAULT_FRAME_RATE;
        this.interval = MS_IN_SECOND / frameRate;
        this.running = false;
        this.entities = this.game.getEntities();
    }

    private void paint(Entity e) {
        e.repaint();
    }

    @Override
    public void run() {
        while (true) {
            float time = System.currentTimeMillis();
            if (running) { // <- running is supposed to be true
                synchronized (entities) {
                    for (Entity e : entities) {
                        paint(e);
                    }
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
