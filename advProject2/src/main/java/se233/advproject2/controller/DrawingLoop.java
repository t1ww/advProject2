package se233.advproject2.controller;


import se233.advproject2.objects.Entity;
import se233.advproject2.view.GameScreen;

public class DrawingLoop implements Runnable {
    private GameScreen platform;
    private int frameRate;
    private float interval;
    private boolean running;
    public DrawingLoop(GameScreen platform) {
        this.platform = platform;
        frameRate = 60;
        interval = 1000.0f / frameRate; // 1000 ms = 1 second
        running = true;
    }
    private void paint(Entity e) {
        e.repaint();
    }
    @Override
    public void run() {
        GameLoop game = GameLoop.Instance;
        while (running) {
            float time = System.currentTimeMillis();
            try {
                game.entities.forEach(entity -> {
                    paint(entity);
                });
                time = System.currentTimeMillis() - time;
            }catch (Exception e){}
            if (time < interval) {
                try {
                    Thread.sleep((long) (interval - time));
                } catch (InterruptedException e) {
                }
            } else {
                try {
                    Thread.sleep((long) (interval - (interval % time)));
                } catch (InterruptedException e) {
                }
            }
        }
    }
}
