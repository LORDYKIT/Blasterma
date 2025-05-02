package com.example.javafxdemo;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;


public class test extends Application {

    Pane pane= new Pane();
    double sceneHeight= 600;
    double sceneWidth= 600;
    Circle circle= new Circle(20);

    Scene scene;
    int dx= 1;
    int dy= 1;

    double newCenterX;
    private static final String[] COLOR_NAMES =
            {"RED", "LIGHTGREEN", "LIMEGREEN", "FUCHSIA", "LIGHTPINK"};
    Color backgroundColor;
    Timeline animation;
    Timeline bulletAnimation;

    int score= 0;
    int level = 1;
    boolean gameLost = false;
    boolean gameStart= false;
    private Scene mainMenuScene;


    @Override
    public void start(Stage stage) throws IOException, URISyntaxException {

        Button startButton = new Button("Start");

        startButton.setStyle(
                "-fx-background-color: #800080; " +
                        "-fx-text-fill: #00FF00; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-family: 'Algerian';"
        );

        String videoFile = "/mainVid.mp4";
        URL p = getClass().getResource(videoFile);

        Media m= new Media(p.toString());
        MediaPlayer mp= new MediaPlayer(m);


        MediaView mediaView = new MediaView(mp);

        mediaView.setFitWidth(20000);
        mediaView.setFitHeight(600);

        mp.setCycleCount(MediaPlayer.INDEFINITE);
        mp.play();

        Pane mainMenuLayout = new Pane();

        mainMenuScene = new Scene(mainMenuLayout, sceneWidth, sceneHeight);

        stage.setResizable(false);

        Label lblGameTitle = new Label("Blasterma");
        lblGameTitle.setFont(Font.font("Goudy Stout", FontWeight.BOLD, 45)); // Set font size, weight, and type
        lblGameTitle.setTextFill(Color.LIMEGREEN);


        mainMenuLayout.getChildren().add(mediaView);
        mainMenuLayout.getChildren().add(lblGameTitle);
        mainMenuLayout.getChildren().add(startButton);


        mediaView.layoutXProperty().bind(mainMenuScene.widthProperty().subtract(1050).divide(2));
        mediaView.layoutYProperty().bind(mainMenuScene.heightProperty().subtract(600).divide(2));


        lblGameTitle.layoutXProperty().bind(mainMenuLayout.widthProperty().subtract(lblGameTitle.widthProperty()).divide(2));
        lblGameTitle.layoutYProperty().bind(mainMenuLayout.heightProperty().subtract(lblGameTitle.heightProperty()).divide(2));

        startButton.layoutXProperty().bind(mainMenuLayout.widthProperty().subtract(startButton.widthProperty()).divide(2));
        startButton.layoutYProperty().bind(mainMenuLayout.heightProperty().subtract(startButton.heightProperty()).divide(1.2));


        stage.setScene(mainMenuScene);
        stage.setTitle("Main Menu Game");



        pane.setBackground(new Background(new BackgroundFill(Color.CYAN, CornerRadii.EMPTY, Insets.EMPTY)));

        circle.setCenterX(circle.getRadius() + 1);
        circle.setCenterY(circle.getRadius() + 1);


        Rectangle player= new Rectangle(200, 10);
        player.setFill(Color.PURPLE);
        player.setX(sceneWidth / 2);
        player.setY(sceneHeight - player.getHeight());



        Label lblScore= new Label("Score: "+score);
        lblScore.setFont(Font.font("Copperplate Gothic Bold", FontWeight.BOLD, 22));
        lblScore.setTextFill(Color.BLACK);

        Label lblLevel = new Label("Level: " + level);
        lblLevel.setFont(Font.font("Copperplate Gothic Bold", FontWeight.BOLD, 28));
        lblLevel.setTextFill(Color.BLACK);

        Label lblGameOver = new Label("Game Over");
        lblGameOver.setFont(Font.font("Algerian", FontWeight.BOLD, 70));
        lblGameOver.setTextFill(Color.DARKRED);


        VBox vbox = new VBox(lblLevel, lblScore);


        pane.getChildren().add(player);
        pane.getChildren().add(circle);
        pane.getChildren().add(vbox);


        scene= new Scene(pane, sceneWidth, sceneHeight);



        Button restartButton = new Button("Restart?");
        restartButton.setStyle(
                "-fx-background-color: #800080; " + // Purple background color
                        "-fx-text-fill: #00FF00; " + // Green text color
                        "-fx-font-size: 16px; " +
                        "-fx-font-family: 'Algerian';" // padding
        );


        MediaPlayer bgSound = new MediaPlayer(new Media(getClass().getResource("/arcade.mp3").toURI().toString()));

        MediaPlayer shootingSound = new MediaPlayer(new Media(getClass().getResource("/9mm.mp3").toURI().toString()));

        MediaPlayer explosionSound = new MediaPlayer(new Media(getClass().getResource("/ex.mp3").toURI().toString()));

        MediaPlayer GOSound = new MediaPlayer(new Media(getClass().getResource("/GO.mp3").toURI().toString()));

        GOSound.setOnEndOfMedia(() -> {
            GOSound.seek(Duration.ZERO);
            GOSound.stop();
        });

        shootingSound.setOnEndOfMedia(() -> {
            shootingSound.seek(Duration.ZERO);
            shootingSound.stop();
        });

        explosionSound.setOnEndOfMedia(() -> {
            explosionSound.seek(Duration.ZERO);
            explosionSound.stop();
        });

        bgSound.setVolume(1.0);
        shootingSound.setVolume(0.1);
        explosionSound.setVolume(0.1);


        scene.setOnMouseClicked(e ->
        {
            if (gameStart) {
                shootingSound.seek(Duration.ZERO);
                shootingSound.stop();
                shootingSound.play();

                Image bulletImage = new Image(Objects.requireNonNull(getClass().getResource("/bullet.png")).toExternalForm());

                ImageView bullet = new ImageView(bulletImage);
                bullet.setFitWidth(20);
                bullet.setFitHeight(30);
                bullet.setX(player.getX() + player.getWidth() / 2);
                bullet.setY(player.getY() - player.getHeight());
                pane.getChildren().add(bullet);




                bulletAnimation = new Timeline(new KeyFrame(Duration.millis(7), ee ->
                {
                    if (bullet.getY() > 0)
                        bullet.setY(bullet.getY() - 10);
                    else {
                        bullet.setY(-900);
                        pane.getChildren().remove(bullet);
                    }

                    if (bullet.getBoundsInLocal().intersects(circle.getBoundsInLocal())) {
                        explosionSound.seek(Duration.ZERO);
                        explosionSound.stop();
                        explosionSound.play();
                        score++;

                        lblScore.setText("Score: " + score);

                        pane.getChildren().remove(bullet);
                        pane.getChildren().remove(circle);

                        if (score % 10 == 0)
                        {
                            level++;
                            if (circle.getRadius() > 6)//jad changed here
                                circle.setRadius(circle.getRadius() - 2);
                            animation.setRate(1.1);
                            pane.getChildren().add(circle);
                            int randomIndex = (int) (Math.random() * COLOR_NAMES.length);

                            lblLevel.setText("Level: " + level);

                            backgroundColor = Color.valueOf(COLOR_NAMES[randomIndex]);
                            BackgroundFill backgroundFill = new BackgroundFill(backgroundColor, CornerRadii.EMPTY, Insets.EMPTY);
                            pane.setBackground(new Background(backgroundFill));
                        }
                    }

                }));
                bulletAnimation.setCycleCount(Timeline.INDEFINITE);
                bulletAnimation.play();
            }
        });
        pane.setOnMouseMoved(e ->
        {
            if(gameStart){
                stage.setResizable(true);
            //bgSound.play();
            if(!gameLost) {
                if (e.getX() > 0 &&
                        e.getX() < scene.getWidth() - player.getWidth())

                    player.setX(e.getX());
            }
            }
        });


        animation= new Timeline(new KeyFrame(Duration.millis(4), e->
        {

            bgSound.setStartTime(Duration.seconds(1));
            bgSound.play();
            bgSound.setVolume(0.2);
            if(gameStart) {
                if (pane.getChildren().contains(circle)) {
                    sceneHeight = scene.getHeight();
                    sceneWidth = scene.getWidth();

                    if (circle.getCenterY() >= sceneHeight - circle.getRadius() || circle.getCenterY() <= circle.getRadius()) {
                        dy *= -1;
                    }

                    if (circle.getCenterX() >= sceneWidth - circle.getRadius() || circle.getCenterX() <= circle.getRadius()) {
                        dx *= -1;
                    }

                    if (circle.getBoundsInLocal().intersects(player.getBoundsInLocal()) || circle.getCenterY() >= scene.getHeight() - circle.getRadius()) {
                        System.out.println("Game Over!");

                        pane.getChildren().remove(circle);

                        gameLost = true;

                        animation.pause();

                        GOSound.play();


                        pane.getChildren().add(lblGameOver);
                        pane.getChildren().add(restartButton);

                        lblGameOver.layoutXProperty().bind(scene.widthProperty().subtract(lblGameOver.widthProperty()).divide(2));
                        lblGameOver.layoutYProperty().bind(scene.heightProperty().subtract(lblGameOver.heightProperty()).divide(2));

                        restartButton.layoutXProperty().bind(scene.widthProperty().subtract(restartButton.widthProperty()).divide(2));
                        restartButton.layoutYProperty().bind(scene.heightProperty().subtract(restartButton.heightProperty()).divide(1.6));
                    }

                    circle.setCenterY(circle.getCenterY() + (dy));
                    circle.setCenterX(circle.getCenterX() + (dx));
                } else {
                    circle = new Circle(circle.getRadius());
                    do {
                        newCenterX = Math.random() * scene.getWidth();
                        circle.setCenterX(newCenterX);
                    } while (newCenterX < circle.getRadius() || newCenterX > scene.getWidth() - circle.getRadius());
                    circle.setCenterY(circle.getRadius() + 1);
                    pane.getChildren().add(circle);
                }
            }
        }));

        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();


        restartButton.setOnAction(e -> {

            animation.stop();

            circle.setRadius(20);

            score = 0;
            level = 1;

            gameLost = false;


            lblScore.setText("Score: " + score);
            lblLevel.setText("Level: " + level);

            pane.getChildren().remove(lblGameOver);
            pane.getChildren().remove(restartButton);

            pane.setBackground(new Background(new BackgroundFill(Color.CYAN, CornerRadii.EMPTY, Insets.EMPTY)));

            animation.playFromStart();
        });

        startButton.setOnAction(e -> {
            gameStart = true;
            stage.setScene(scene);

        });




        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
