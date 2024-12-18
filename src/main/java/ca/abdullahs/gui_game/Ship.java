package ca.abdullahs.gui_game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Abstract class for the player and enemy ships
 */
public abstract class Ship extends GameObject {
    protected int health;
    protected double speed;

    public Ship(double x, double y, double speed, Image sprite) {
        super(x, y);
        this.speed = speed;
        imageView = new ImageView(sprite);
        width = sprite.getWidth();
        height = sprite.getHeight();
        imageView.setPreserveRatio(true);

        // Center the sprite's pivot point
        imageView.setTranslateX(x - width/2);
        imageView.setTranslateY(y - height/2);
    }
}