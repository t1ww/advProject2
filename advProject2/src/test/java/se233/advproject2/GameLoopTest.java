package se233.advproject2;


import javafx.scene.input.KeyCode;
import org.junit.Before;
import org.junit.Test;
import se233.advproject2.controller.GameLoop;
import se233.advproject2.objects.Player;
import se233.advproject2.view.GameScreen;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameLoopTest {
    private GameLoop gameLoopUnderTest;
    private GameScreen gameScreenUnderTest;
    private Method step;
    @Before
    public void init() throws NoSuchMethodException {
        gameScreenUnderTest = new GameScreen();
        gameLoopUnderTest = new GameLoop(gameScreenUnderTest);
        step = GameLoop.class.getDeclaredMethod("step");
        step.setAccessible(true);
    }
    private void clockTickHelper() throws InvocationTargetException,
            IllegalAccessException {
        step.invoke(gameLoopUnderTest);
    }
    private void clockTickHelper(int c) throws InvocationTargetException,
            IllegalAccessException {
        for (int i = 0; i < c; i++) {
            step.invoke(gameLoopUnderTest);
        }
    }
    @Test
    public void testClockTick() throws InvocationTargetException,
            IllegalAccessException {
        gameLoopUnderTest = new GameLoop(new GameScreen());
        clockTickHelper();
        assertNotEquals(gameLoopUnderTest.runtime, 0);
    }
    @Test
    public void testKeyPressHandling() throws InvocationTargetException, IllegalAccessException {
        // start anew
        gameLoopUnderTest.setInstance(gameLoopUnderTest);
        gameLoopUnderTest.Start();
        // Run the game loop's step method to process the keypress
        clockTickHelper();
        // getting the player
        Player player = gameLoopUnderTest.getPlayer();
        double startX = player.getX(); // get start
        // Simulate a key press
        gameScreenUnderTest.pressKey(KeyCode.LEFT);

        // Run the game loop's step method to process the keypress
        clockTickHelper();
        // check if the player's position has updated as expected.
        assertTrue(player.getX() < startX);

        // Simulate key release
        gameScreenUnderTest.releaseKey(KeyCode.LEFT);
    }
}