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
            GameLoop game = GameLoop.Instance;
            game.enemyMove();
            // run the method
            if(game.creationPhase){
                // create new friends
                for (int i = 0; i < 5; i++) {
                    game.entities.add(new Enemy(90 + i * 100, 150, 32, game.level));
                    game.enemyCount++;
                }
                // started the game
                game.creationPhase = false;
            }
            countdown--;
        }
    }

}
