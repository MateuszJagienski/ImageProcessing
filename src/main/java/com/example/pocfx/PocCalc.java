package com.example.pocfx;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class PocCalc {

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
}
