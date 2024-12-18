package ca.abdullahs.gui_game;

import javafx.geometry.Point2D;
import javafx.scene.image.ImageView;

/**
 * The base class for all game objects
 */
public abstract class GameObject {
    protected Point2D position;
    protected Point2D velocity;
    protected double rotation;
    protected ImageView imageView;
    protected boolean alive = true;
    protected double width;
    protected double height;

    public GameObject(double x, double y) {
        position = new Point2D(x, y);
        velocity = new Point2D(0, 0);
        rotation = 0;
    }

    // Update position based on velocity each frame
    public void update() {
        setPosition(position.add(velocity));
    }

    // Checks if the object is colliding with another object
    public boolean isColliding(GameObject other) {
        return imageView.getBoundsInParent().intersects(
                other.getImageView().getBoundsInParent());
    }

    // Handles a collision between this object and another object
    // where the two objects should bounce off each other
    public void handleCollision(GameObject other) {
        // Calculate the velocity along the normal vector
        Point2D normal = other.getPosition().subtract(position).normalize();
        Point2D relativeVelocity = other.getVelocity().subtract(velocity);
        double velocityAlongNormal = relativeVelocity.dotProduct(normal);

        // If the object is moving away from the other object, there is no need to bounce
        if (velocityAlongNormal > 0) return;

        // Calculate the impulse to apply to the other object
        // based on reducing the velocity of this object along the normal vector
        double multiplier = 0.5;
        double j = -(1 + multiplier) * velocityAlongNormal;
        Point2D impulse = normal.multiply(j);

        // Determine the new velocity for both objects
        Point2D newVelocity = velocity.subtract(impulse);
        Point2D otherNewVelocity = other.getVelocity().add(impulse);

        // Limit the maximum speed of the objects
        double maxSpeed = 3.0;
        newVelocity = limitVelocity(newVelocity, maxSpeed);
        otherNewVelocity = limitVelocity(otherNewVelocity, maxSpeed);

        // Set the new velocities
        velocity = newVelocity;
        other.setVelocity(otherNewVelocity);
    }

    // Limits a velocity vector to a maximum speed
    private Point2D limitVelocity(Point2D vel, double maxSpeed) {
        double speed = vel.magnitude();
        if (speed > maxSpeed) {
            return vel.multiply(maxSpeed / speed);
        }
        return vel;
    }

    // Getters and setters
    public Point2D getPosition() { return position; }
    public void setPosition(Point2D position) {
        this.position = position;
        imageView.setTranslateX(position.getX() - width/2);
        imageView.setTranslateY(position.getY() - height/2);
    }

    public Point2D getVelocity() { return velocity; }
    public void setVelocity(Point2D velocity) { this.velocity = velocity; }

    public double getRotation() { return rotation; }
    public void setRotation(double rotation) {
        this.rotation = rotation;
        imageView.setRotate(rotation);
    }

    public ImageView getImageView() { return imageView; }

    public boolean isAlive() { return alive; }
    public void setAlive(boolean alive) { this.alive = alive; }
}