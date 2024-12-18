package ca.abdullahs.gui_game;

import javafx.animation.PauseTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * An explosion animation played when an enemy is destroyed
 */
public class Explosion {
    private ImageView explosionView;
    private Image EXPLOSION_GIF = new Image("/explosion.gif");

    public Explosion(double x, double y, Pane root) {
        explosionView = new ImageView(EXPLOSION_GIF);

        // Center the explosion at the enemy's position
        explosionView.setX(x - EXPLOSION_GIF.getWidth()/2);
        explosionView.setY(y - EXPLOSION_GIF.getHeight()/2);

        // Add explosion to the root pane
        root.getChildren().add(explosionView);
        explosionView.toBack();

        // Remove explosion after animation completes
        PauseTransition delay = new PauseTransition(Duration.seconds(1.25));
        delay.setOnFinished(event -> root.getChildren().remove(explosionView));
        delay.play();
    }
}

