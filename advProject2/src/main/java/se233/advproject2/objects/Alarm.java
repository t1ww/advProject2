package se233.advproject2.objects;

import se233.advproject2.controller.GameLoop;

import java.lang.reflect.Method;

public class Alarm {
    GameLoop game = GameLoop.Instance;
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
            game.enemiesSpawn();
            countdown--;
        }
    }

}
