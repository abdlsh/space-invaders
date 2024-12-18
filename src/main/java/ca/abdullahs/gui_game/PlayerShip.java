package ca.abdullahs.gui_game;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;

/**
 * The player ship object
 */
public class PlayerShip extends Ship {
    private static final double PLAYER_SPEED = 5.0;
    private static final double ROTATION_SPEED = 4.0;

    public PlayerShip(double x, double y) {
        super(x, y, PLAYER_SPEED,
                new Image("/player.png"));
        health = 5;
    }

    /// Movement handling
    public void moveLeft() {
        velocity = new Point2D(-speed, 0);
    }

    public void moveRight() {
        velocity = new Point2D(speed, 0);
    }

    public void rotate(boolean clockwise) {
        rotation += clockwise ? ROTATION_SPEED : -ROTATION_SPEED;
        setRotation(rotation);
    }

    // Stop the ship when the game ends
    public void stop() {
        velocity = new Point2D(0, 0);
    }

    @Override public void update() {
        Point2D newPosition = position.add(velocity);

        // Constrain the ship's position to the screen bounds
        double newX = Math.min(Math.max(newPosition.getX(), width/2), SpaceInvaders.WIDTH - width/2);
        double newY = Math.min(Math.max(newPosition.getY(), height/2), SpaceInvaders.HEIGHT - height/2);

        // Set the constrained position
        setPosition(new Point2D(newX, newY));
    }
}