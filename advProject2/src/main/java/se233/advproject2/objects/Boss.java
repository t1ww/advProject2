package se233.advproject2.objects;

import se233.advproject2.controller.GameLoop;

public class Boss extends Entity {
    enum bossType {
        shooter,
        tank,
        fast
    }
    private bossType type;
    // constructor
    public Boss (double x, double y, int size, bossType bt){
        super(x, y, size);
        this.type = bt;
    }

    // state machine
    // pattern 1, 2, 3
}
