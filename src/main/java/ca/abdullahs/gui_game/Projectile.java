package ca.abdullahs.gui_game;

import javafx.geometry.Point2D;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

/**
 * A projectile from either the player or an enemy
 */
public class Projectile extends GameObject {
    private static final double PROJECTILE_SPEED = 7.0;
    private static final Image PROJECTILE_SPRITE = new Image("/projectile.png");

    private boolean isPlayerProjectile;

    public Projectile(double x, double y, double angle, double size, boolean isPlayer) {
        super(x, y);

        isPlayerProjectile = isPlayer;

        // Set up the ImageView with the sprite
        imageView = new ImageView(PROJECTILE_SPRITE);

        // Scale the image based on the size parameter
        double scale = size / (PROJECTILE_SPRITE.getWidth() / 2);
        imageView.setFitWidth(PROJECTILE_SPRITE.getWidth() * scale);
        imageView.setFitHeight(PROJECTILE_SPRITE.getHeight() * scale);
        imageView.setPreserveRatio(true);

        // Store scaled dimensions for collision detection
        width = imageView.getFitWidth();
        height = imageView.getFitHeight();

        // Center the projectile
        imageView.setTranslateX(x - width/2);
        imageView.setTranslateY(y - height/2);

        rotation = angle;
        imageView.setRotate(rotation);

        // Convert angle to velocity vector
        double radians = Math.toRadians(angle);
        velocity = new Point2D(Math.sin(radians) * PROJECTILE_SPEED,
                -Math.cos(radians) * PROJECTILE_SPEED);
    }

    public boolean isPlayerProjectile() {
        return isPlayerProjectile;
    }
}