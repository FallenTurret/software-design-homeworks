package ru.itmo.sd.drawing;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class JavaFxDrawer extends Application implements DrawingApi {
    private static JavaFxDrawer instance = null;
    private String title;
    private Canvas canvas;

    @Override
    public void init() throws Exception {
        super.init();
        canvas = new Canvas(1200, 700);
        title = getParameters().getUnnamed().get(0);
        instance = this;
    }

    public static JavaFxDrawer getInstance(String title) throws InterruptedException {
        if (instance == null) {
            new Thread(() -> Application.launch(JavaFxDrawer.class, title)).start();
        }
        while (true) {
            if (instance != null) {
                return instance;
            }
            Thread.sleep(100);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle(title);
        Group root = new Group();
        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    @Override
    public long getDrawingAreaWidth() {
        return (long)canvas.getWidth();
    }

    @Override
    public long getDrawingAreaHeight() {
        return (long)canvas.getHeight();
    }

    @Override
    public void drawCircle(double x, double y, double r) {
        var gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.GREEN);
        gc.fillOval(x - r, y - r, 2 * r, 2 * r);
    }

    @Override
    public void drawLine(double x1, double y1, double x2, double y2) {
        var gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.BLACK);
        gc.strokeLine(x1, y1, x2, y2);
    }
}
