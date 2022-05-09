package com.example.pocfx;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.io.FileInputStream;

public class PocCalc extends Application {

    private PixelWriter pixelWriter;
    private WritableImage writableImage;

    public Image threshold(Image image, int histTh) {
//        int minimum = 100;
//        int maximum = 150;
//        if (!min.getText().isBlank()) {
//            minimum  = Integer.parseInt(min.getText());
//        }
//        if (!max.getText().isBlank()) {
//            maximum = Integer.parseInt(max.getText());
//        }
//        if (maximum > 255) {
//            maximum = 255;
//        }
//        if (minimum > 255) {
//            minimum = 255;
//        }
        int R,G,B;
        int avg;
        Color color;
        Color color1;
        for(int i=0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                color = image.getPixelReader().getColor(i,j);
                double r = color.getRed();
                double g = color.getGreen();
                double b = color.getBlue();
                R = (int) (r * 255);
                G = (int) (g * 255);
                B = (int) (b * 255);
                avg = (R + G + B) / 3;
                if (avg > histTh) {
                    color1  = Color.rgb(0, 0, 0);
                } else {
                    color1  = Color.rgb(255, 255, 255);
                }
                pixelWriter.setColor(i, j, color1);
            }
        }
        return writableImage;
    }

    public Image treshold(Image image) {
        return writableImage;
    }


    @Override
    public void start(Stage stage) throws Exception {
        ImageView imageView = new ImageView();
        imageView.setImage(new Image(new FileInputStream("src/main/resources/Images/jp2.jpg")));
        ImageView imageView1 = new ImageView();
        Transform transform = new Translate();


        Parent p = new VBox();
        Scene scene = new Scene(p);
        stage.setScene(scene);
        stage.setTitle("POC1");
        stage.setWidth(1000);
        stage.setHeight(800);
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
    }
}
