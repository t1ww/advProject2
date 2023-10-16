package se233.advproject2.objects;

import se233.advproject2.controller.GameLoop;

import java.lang.reflect.Method;

public class Alarm {
    public static int countdown;
    private int counter = 0;
    public Alarm(int countdown){
        this.countdown = countdown;
    }
    public Alarm(int countdown, Method method){
        this.countdown = countdown;
    }
    public void step(){
        counter++;
        int second = 60;
        if (counter >= second){
            countdown--; // count alarm
            counter = 0; // reset counter
        }
        if (countdown == 0){
            // run the method
            if(game.creationPhase){ // frontline creation
                /// create a wave of enemies 
                // left wall
                for (int i = 0; i < 3; i++) {
                    game.entities.add(new Enemy(90 + (i * 34), 150, 32, game.level));
                    game.enemyCount++;
                }
                // started the game
                game.creationPhase = false;
            }
            // move all previous enemies down
                game.enemiesMoveDown();
            /// create a wave of enemies 
                // left wall
                for (int i = 0; i < 3; i++) {
                    game.entities.add(new Enemy(90 + (i * 34), 150, 32, game.level));
                    game.enemyCount++;
                }
                // second tier
                for (int i = 0; i < 3; i++) {
                    game.entities.add(new Enemy(90 + (i * 34), 150, 32, game.level));
                    game.enemyCount++;
                }
                // right wall
                for (int i = 0; i < 3; i++) {
                    game.entities.add(new Enemy(90 + (i * 34), 150, 32, game.level));
                    game.enemyCount++;
                }
            countdown--;
        }
    }

}
