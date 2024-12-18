package ca.abdullahs.gui_game;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.text.Text;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Main class for the Space Invaders game
 */
public class SpaceInvaders extends Application {
    /// Constants
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    private static final double INITIAL_SPAWN_INTERVAL = 2.0; // seconds
    private static final Image GAME_OVER_SPRITE = new Image("/game_over.png");
    private static final Random random = new Random();

    /// Game UI
    private Pane root;
    private Button restartButton;
    private PlayerShip player;
    private List<EnemyShip> enemies;
    private List<Projectile> projectiles;
    private List<Text> healthDisplay;

    /// Game state
    // The time when the game started
    private long startTime;
    // The time when the last enemy was spawned
    private long lastSpawnTime;
    // Whether the next enemy should spawn on the left or right
    private boolean enemySpawnOnLeft = true;
    // Whether the game is over
    private boolean gameOver = false;
    // Whether the player is rotating clockwise
    private boolean rotatingClockwise = false;
    // Whether the player is rotating counter-clockwise
    private boolean rotatingCounterClockwise = false;
    // Whether the player is moving left
    private boolean movingLeft = false;
    // Whether the player is moving right
    private boolean movingRight = false;

    // Start the game
    @Override public void start(Stage stage) {
        root = new Pane();
        Scene scene = new Scene(root, WIDTH, HEIGHT, Color.BLACK);
        stage.setScene(scene);
        stage.setTitle("Space Invaders");

        initGame();
        setupInputHandling(scene);
        startGameLoop();

        stage.show();
    }

    // Called whenever a new game is started
    private void initGame() {
        root.setStyle("-fx-background-color: black;");

        enemies = new ArrayList<>();
        projectiles = new ArrayList<>();
        healthDisplay = new ArrayList<>();
        startTime = System.nanoTime();
        gameOver = false;

        // Remove old game objects if they exist
        if (player != null) {
            root.getChildren().remove(player.getImageView());
        }
        if (restartButton != null) {
            root.getChildren().remove(restartButton);
        }

        // Initialize player
        player = new PlayerShip(WIDTH / 2, HEIGHT - 50);
        root.getChildren().add(player.getImageView());

        updateHealthDisplay();
    }

    // Returns a multiplier based on how long the current game has been going
    private double getGameTimeMultiplier() {
        return (System.nanoTime() - startTime) / 1e11; // Increases by 1 every 10 seconds
    }

    // Returns the interval after which the next enemy can spawn
    private double getCurrentSpawnInterval() {
        // Spawn interval decreases over time (enemies spawn faster)
        return INITIAL_SPAWN_INTERVAL - Math.pow(1 + getGameTimeMultiplier(), 1.2);
    }

    // Renders the health display based on the player's current health
    private void updateHealthDisplay() {
        root.getChildren().removeAll(healthDisplay);
        healthDisplay.clear();

        for (int i = 0; i < player.health; i++) {
            Text heart = new Text(10 + i * 20, 20, "♥");
            heart.setFill(Color.RED);
            healthDisplay.add(heart);
            root.getChildren().add(heart);
        }
    }

    // Handles player arrow key & spacebar input
    private void setupInputHandling(Scene scene) {
        scene.setOnKeyPressed(e -> {
            if (gameOver) return;

            switch (e.getCode()) {
                case LEFT: movingLeft = true; break;
                case RIGHT: movingRight = true; break;
                case UP: rotatingClockwise = true; break;
                case DOWN: rotatingCounterClockwise = true; break;
                case SPACE: playerShoot(); break;
            }
        });

        scene.setOnKeyReleased(e -> {
            switch (e.getCode()) {
                case LEFT: movingLeft = false; break;
                case RIGHT: movingRight = false; break;
                case UP: rotatingClockwise = false; break;
                case DOWN: rotatingCounterClockwise = false; break;
            }
        });
    }

    // Called when the player presses spacebar to shoot
    private void playerShoot() {
        Projectile projectile = new Projectile(
                player.getPosition().getX(),
                player.getPosition().getY(),
                player.getRotation(),
                5,
                true
        );
        projectiles.add(projectile);
        root.getChildren().add(projectile.getImageView());
    }

    // Starts the game loop
    private void startGameLoop() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        }.start();
    }

    // Called every frame
    private void update() {
        if (gameOver) return;

        // Handle continuous input
        if (movingLeft) player.moveLeft();
        if (movingRight) player.moveRight();
        if (rotatingClockwise) player.rotate(true);
        if (rotatingCounterClockwise) player.rotate(false);
        if (!movingLeft && !movingRight) player.stop();

        // Spawn enemies if the spawn interval has elapsed
        if ((System.nanoTime() - lastSpawnTime) / 1e9 > getCurrentSpawnInterval()) {
            spawnEnemy();
            lastSpawnTime = System.nanoTime();
        }

        // Update all game objects
        player.update();
        updateEnemies();
        updateProjectiles();

        // Check for collisions
        checkCollisions();

        // Remove dead objects
        cleanupObjects();
    }

    // Spawns a new enemy
    private void spawnEnemy() {
        double x = enemySpawnOnLeft
                ? random.nextDouble() * (WIDTH/2 - 50) // Left half, with some randomness
                : WIDTH/2 + random.nextDouble() * (WIDTH/2 - 50); // Right half, with some randomness

        EnemyShip enemy = new EnemyShip(x, 0, getGameTimeMultiplier(), enemySpawnOnLeft);
        enemies.add(enemy);
        root.getChildren().add(enemy.getImageView());

        // The next enemy should spawn on the opposite side
        enemySpawnOnLeft = !enemySpawnOnLeft;
    }

    // Called every frame to update the enemy's position and whether they're shooting
    private void updateEnemies() {
        for (EnemyShip enemy : enemies) {
            enemy.update();
            enemy.bounceOffWall(WIDTH);

            // Enemy shooting
            if (enemy.canShoot()) {
                Projectile projectile = new Projectile(
                        enemy.getPosition().getX(),
                        enemy.getPosition().getY(),
                        180 + enemy.getRotation(),
                        3.5,
                        false
                );
                projectiles.add(projectile);
                root.getChildren().add(projectile.getImageView());
                enemy.recordShot();
            }
        }
    }

    // Update the position of each projectile every frame
    private void updateProjectiles() {
        projectiles.forEach(Projectile::update);
    }

    // Check for collisions between ships and projectiles
    private void checkCollisions() {
        // Check enemy-enemy collisions
        for (int i = 0; i < enemies.size(); i++) {
            for (int j = i + 1; j < enemies.size(); j++) {
                EnemyShip e1 = enemies.get(i);
                EnemyShip e2 = enemies.get(j);
                if (e1.isColliding(e2)) {
                    e1.handleCollision(e2);
                }
            }
        }

        // Check projectile-ship collisions
        for (Projectile projectile : projectiles) {
            // Check if the player was hit by an enemy projectile
            if (projectile.getVelocity().getY() > 0 &&
                    !projectile.isPlayerProjectile() &&
                    projectile.isColliding(player)) {
                // Player loses 1 health, game ends if player is out of health
                player.health--;
                updateHealthDisplay();
                projectile.setAlive(false);

                if (player.health <= 0) {
                    gameOver();
                }
            }

            // Check if the enemy was hit by a player projectile
            for (EnemyShip enemy : enemies) {
                if (projectile.getVelocity().getY() < 0 &&
                        projectile.isPlayerProjectile() &&
                        projectile.isColliding(enemy)) {
                    new Explosion( // create an explosion if an enemy was hit
                            enemy.getPosition().getX(),
                            enemy.getPosition().getY(),
                            root
                    );
                    enemy.setAlive(false);
                    projectile.setAlive(false);
                }
            }
        }

        // Check player-enemy collisions
        for (EnemyShip enemy : enemies) {
            if (enemy.isColliding(player)) {
                new Explosion(
                        enemy.getPosition().getX(),
                        enemy.getPosition().getY(),
                        root
                );

                // Player loses 1 health, game ends if player is out of health
                enemy.setAlive(false);
                player.health--;
                updateHealthDisplay();

                if (player.health <= 0) {
                    gameOver();
                }
            }
        }
    }

    // Called every frame to remove any unneeded objects
    private void cleanupObjects() {
        // Remove dead enemies
        enemies.removeIf(enemy -> {
            if (!enemy.isAlive()) {
                root.getChildren().remove(enemy.getImageView());
                return true;
            }
            // Remove enemy and reduce player health if they reached bottom of screen
            if (enemy.getPosition().getY() > HEIGHT) {
                root.getChildren().remove(enemy.getImageView());
                player.health -= 1;
                updateHealthDisplay();
                if (player.health <= 0) {
                    gameOver();
                }
                return true;
            }
            return false;
        });

        // Remove dead projectiles
        projectiles.removeIf(projectile -> {
            if (!projectile.isAlive() ||
                    projectile.getPosition().getY() < 0 ||
                    projectile.getPosition().getY() > HEIGHT) {
                root.getChildren().remove(projectile.getImageView());
                return true;
            }
            return false;
        });
    }

    // Called when the game ends
    private void gameOver() {
        gameOver = true;

        ImageView gameOverImage = new ImageView(GAME_OVER_SPRITE);
        gameOverImage.setX(WIDTH/2.0 - GAME_OVER_SPRITE.getWidth()/2);  // Center horizontally
        gameOverImage.setY(HEIGHT/2.0 - GAME_OVER_SPRITE.getHeight()/2); // Center vertically
        root.getChildren().add(gameOverImage);

        // Create restart button
        restartButton = new Button("Restart Game");
        restartButton.setLayoutX(WIDTH/2.0 - 50);
        restartButton.setLayoutY(HEIGHT/2.0 + GAME_OVER_SPRITE.getHeight()); // Position below GAME OVER text
        restartButton.setStyle("-fx-background-color: #4a4a4a; -fx-text-fill: white; -fx-font-size: 14px;");

        // Highlight button on hover
        restartButton.setOnMouseEntered(e ->
                restartButton.setStyle("-fx-background-color: #787878; -fx-text-fill: white; -fx-font-size: 14px;"));
        restartButton.setOnMouseExited(e ->
                        restartButton.setStyle("-fx-background-color: #4a4a4a; -fx-text-fill: white; -fx-font-size: 14px;"));

        // Restart the game when button is clicked
        restartButton.setOnAction(e -> {
            root.getChildren().remove(gameOverImage);
            root.getChildren().remove(restartButton);
            initGame();
        });
        root.getChildren().add(restartButton);

        // Clear projectiles and enemies
        projectiles.forEach(projectile -> {
            root.getChildren().remove(projectile.getImageView());
        });
        projectiles.clear();

        enemies.forEach(enemy -> {
            root.getChildren().remove(enemy.getImageView());
        });
        enemies.clear();

        // Stop the player
        player.stop();
    }

    // Starts the game
    public static void main(String[] args) {
        launch(args);
    }
}