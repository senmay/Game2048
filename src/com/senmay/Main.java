package com.senmay;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class Main extends Application {

    private static final int CELL_SIZE = 64;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage myStage) throws Exception {
        myStage.setTitle("Game 2048");

        FlowPane rootNode = new FlowPane();

        myStage.setResizable(false);

        Logic logic = new Logic();
        Scene myScene = new Scene(rootNode, logic.getWidth(), logic.getHeight());
        myStage.setScene(myScene);

        myScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                logic.resetGame();
            }

            if (!logic.canMove() || (!logic.win && !logic.canMove())) {
                logic.lose = true;
            }

            if (!logic.win && !logic.lose) {
                switch (event.getCode()) {
                    case LEFT:
                        logic.left();
                        break;
                    case RIGHT:
                        logic.right();
                        break;
                    case DOWN:
                        logic.down();
                        break;
                    case UP:
                        logic.up();
                        break;
                }
            }
            logic.relocate(330, 390);
        });

        rootNode.getChildren().add(logic);
        myStage.show();

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                GraphicsContext gc = logic.getGraphicsContext2D();
                gc.setFill(Color.rgb(187, 103, 130, 0.5));
                gc.fillRect(0, 0, logic.getWidth(), logic.getHeight());

                for (int y = 0; y < 4; y++) {
                    for (int x = 0; x < 4; x++) {
                        Cell cell = logic.getCells()[x + y * 4];
                        int value = cell.number;
                        int xOffset = offsetCoors(x);
                        int yOffset = offsetCoors(y);

                        gc.setFill(cell.getBackground());
                        gc.fillRect(xOffset, yOffset, CELL_SIZE, CELL_SIZE);
                        gc.setFill(cell.getForeground());
                        gc.setTextAlign(TextAlignment.CENTER);

                        String s = String.valueOf(value);

                        if (value != 0)
                            gc.fillText(s, xOffset + CELL_SIZE / 2, yOffset + CELL_SIZE / 2 - 2);
                        if (logic.win || logic.lose) {
                            gc.setFill(Color.rgb(255, 255, 255));
                            gc.fillRect(0, 0, logic.getWidth(), logic.getHeight());
                            gc.setFill(Color.rgb(78, 139, 202));
                            if (logic.win) {
                                gc.fillText("You win", 95, 150);
                            }
                            if (logic.lose) {
                                gc.fillText("You lose", 160, 200);
                            }
                            if (logic.win || logic.lose) {
                                gc.setFill(Color.rgb(128, 128, 128));
                                gc.fillText("Press ESC to play again", 110, 270);
                            }
                        }
                        gc.fillText("Score: " + logic.score, 200, 350);
                    }
                }
            }
        }.start();
    }

    private static int offsetCoors(int arg) {
        return arg * (16 + 64) + 16;
    }

}