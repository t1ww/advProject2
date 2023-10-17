package se233.advproject2;


import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import se233.advproject2.controller.GameLoop;
import se233.advproject2.objects.Enemy;
import se233.advproject2.objects.Player;
import se233.advproject2.view.GameScreen;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameLoopTest {
    private GameLoop gameLoopUnderTest;
    private GameScreen gameScreenUnderTest;
    private Method step;
    @BeforeClass
    public static void initJFX() throws InterruptedException {
        Thread t = new Thread("JavaFX Init Thread") {
            public void run() {
                javafx.application.Application.launch(DummyApplication.class);
            }
        };
        t.setDaemon(true);
        t.start();

        synchronized (DummyApplication.class) {
            DummyApplication.class.wait(); // Wait until notified from DummyApplication::start()
        }
    }
    @Before
    public void init() throws NoSuchMethodException {
        gameScreenUnderTest = new GameScreen();
        gameLoopUnderTest = new GameLoop(gameScreenUnderTest);
        step = GameLoop.class.getDeclaredMethod("step");
        step.setAccessible(true);
    }
    @Test
    public void testClockTick() throws InvocationTargetException,
            IllegalAccessException {
        gameLoopUnderTest = new GameLoop(new GameScreen());
        gameLoopUnderTest.setInstance(gameLoopUnderTest);
        clockTickHelper();
        assertNotEquals(gameLoopUnderTest.runtime, 0);
    }
    @Test
    public void testKeyPressHandling() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                // start anew
                gameLoopUnderTest.setInstance(gameLoopUnderTest);
                gameLoopUnderTest.Start();
                // Run the game loop's step method to proceed
                clockTickHelper();
                // getting the player
                Player player = gameLoopUnderTest.getPlayer();
                // Run the game loop's step method until player's in position to start
                while (player.starting){
                    clockTickHelper();
                }
                double startX = player.getX(); // get start
                // Simulate a key press
                gameScreenUnderTest.pressKey(KeyCode.LEFT);
                clockTickHelper(); // step
                // check if the player's position has updated as expected.
                assertTrue("Expected player's x position to decrease. Initial: "
                        + startX + ", Current: " + player.getX(), player.getX() < startX);
                // Simulate key release
                gameScreenUnderTest.releaseKey(KeyCode.LEFT);
            } catch (Exception e) {
                fail("Failed within Platform.runLater(): " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });

        latch.await();  // Wait until the operations within Platform.runLater() are done
    }
    @Test
    public void testShootingMechanism() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                // Initialize the game loop and start it
                gameLoopUnderTest.setInstance(gameLoopUnderTest);
                gameLoopUnderTest.Start();
                clockTickHelper();
                Player player = gameLoopUnderTest.getPlayer();
                while (player.starting){
                    clockTickHelper();
                }
                player = gameLoopUnderTest.player;
                double cur_x = player.getX(), cur_y = player.getY();
                gameLoopUnderTest.enemyCount = 0; // reset the count
                gameLoopUnderTest.setScore(0); // reset the score
                gameLoopUnderTest.alarm = null; // dont spawn wave
                Enemy etest = new Enemy(cur_x, cur_y-100,32,0);
                etest.setX(cur_x);
                etest.setY(cur_y);
                gameLoopUnderTest.getEntities().add(etest);
                Assert.assertEquals("Expect to have an enemy in the list",2, gameLoopUnderTest.getEntities().size());
                // Simulate shooting key press
                gameScreenUnderTest.pressKey(KeyCode.SPACE);
                clockTickHelper();
                gameScreenUnderTest.releaseKey(KeyCode.SPACE);
                // Check if bullet was added to the bullet list
                Assert.assertEquals("Expected bullet to be added to bullet list.", 1, gameLoopUnderTest.bulletList.size());
                // steps until the bullet is gone (bullet should hit the enemy etest)
                while (gameLoopUnderTest.bulletList.size() != 0){
                    clockTickHelper();
                }
                // Check if bullet was destroyed
                Assert.assertEquals("Expected bullet to be removed from bullet list.", 0, gameLoopUnderTest.bulletList.size());
                Assert.assertEquals("Expected no more enemy.", 0, gameLoopUnderTest.enemyCount);
                Assert.assertNotEquals("Expected score to not be 0",0, gameLoopUnderTest.getScore()); // score should be added

            } catch (Exception e) {
                fail("Failed within Platform.runLater(): " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });

        latch.await();
    }

    @Test
    public void testSpecialAttackMechanism() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                gameLoopUnderTest.setInstance(gameLoopUnderTest);
                gameLoopUnderTest.Start();
                clockTickHelper();
                Player player = gameLoopUnderTest.getPlayer();
                while (player.starting){
                    clockTickHelper();
                }

                // Simulate special attack key press
                gameScreenUnderTest.pressKey(KeyCode.G);
                clockTickHelper();

                // Check if special bullet was added to the bullet list
                Assert.assertEquals("Expected special bullet to be added to bullet list.", 1, gameLoopUnderTest.bulletList.size());

                gameScreenUnderTest.releaseKey(KeyCode.G);
            } catch (Exception e) {
                fail("Failed within Platform.runLater(): " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });

        latch.await();
    }
    // helper methods
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
    // dummy app for testing
    public static class DummyApplication extends javafx.application.Application {
        @Override
        public void start(javafx.stage.Stage primaryStage) {
            Platform.runLater(() -> {
                synchronized (DummyApplication.class) {
                    DummyApplication.class.notifyAll(); // Notify that JavaFX is initialized
                }
            });
        }
    }
}