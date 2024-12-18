package ca.abdullahs.gui_game;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import java.util.Random;

/**
 * An enemy ship object
 */
public class EnemyShip extends Ship {
    private static final double BASE_SPEED = 1.0;
    private static final double ANGLE_VARIANCE = 30;
    private static final Random random = new Random();

    private static Image[] sprites;

    // Select a random colour sprite for each enemy ship
    static {
        sprites = new Image[3];
        sprites[0] = new Image("/red.png");
        sprites[1] = new Image("/green.png");
        sprites[2] = new Image("/yellow.png");
    }

    private boolean isShooting;
    private long lastShotTime;

    public EnemyShip(double x, double y, double gameTimeMultiplier, boolean movingRight) {
        super(x, y, BASE_SPEED,
                sprites[random.nextInt(sprites.length)]); // Random sprite selection

        speed = BASE_SPEED * (1 + gameTimeMultiplier); // Speed increases with time
        health = 1;
        isShooting = random.nextBoolean(); // 50% chance of being a shooting ship

        // Select a random movement angle between ±60 deg +/- 30 deg
        double baseAngle = movingRight ? -60 : 60;
        double angle = baseAngle + (random.nextDouble() * 2 - 1) * ANGLE_VARIANCE;
        rotation = angle;

        // Convert angle to velocity vector
        double radians = Math.toRadians(angle);
        velocity = new Point2D(Math.sin(radians) * speed, Math.cos(radians) * speed);
    }

    // Whether the ship is a shooting ship and it has been long enough since the last shot
    public boolean canShoot() {
        return isShooting && (System.currentTimeMillis() - lastShotTime) > 1000; // 1 second cooldown
    }

    // Record the time of the last shot
    public void recordShot() {
        lastShotTime = System.currentTimeMillis();
    }

    // Bounce off the wall if the ship has hit the edge of the screen
    public void bounceOffWall(double width) {
        if (position.getX() <= 0 || position.getX() >= width) {
            velocity = new Point2D(-velocity.getX(), velocity.getY());
        }
    }
}