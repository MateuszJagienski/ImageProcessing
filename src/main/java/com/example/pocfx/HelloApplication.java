package com.example.pocfx;

import com.example.pocfx.color.ColorRGB;
import com.example.pocfx.color.ColorYUV;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.opencv.core.Core;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

public class HelloApplication extends Application {

    private HBox buttonHbox;
    private Button loadImageBtn;
    private Button doSomethingBtn;
    private Button greyBtn;
    private Button greyBtn1;
    private Button calcHistBtn;
    private Button saveBtn;
    private Button thresholdingBtn;
    private TextField min;
    private TextField max;
    private ImageView imageView;
    private Image image;
    private Image copyImage;
    private PixelReader pixelReader;
    private PixelWriter pixelWriter;
    private WritableImage writableImage;
    private int imageWidth;
    private int imageHeight;
    private String imageFilename;

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent p = create();

        Scene scene = new Scene(p);

        primaryStage.setTitle("POC1");
        primaryStage.setWidth(1000);
        primaryStage.setHeight(1000);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Parent create() {
        VBox vbox = new VBox();
        vbox.setPadding( new Insets(10) );
        vbox.setSpacing( 10 );

        loadImageBtn = new Button("Load image");
        doSomethingBtn = new Button("Change color");
        greyBtn = new Button("GreyRGB");
        greyBtn1 = new Button("GreyYUV");
        calcHistBtn = new Button("Cals hist");
        saveBtn = new Button("Save");
        thresholdingBtn = new Button("Thresholding");
        min = new TextField();
        max = new TextField();
        min.setMaxWidth(50);
        max.setMaxWidth(50);
        min.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    min.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        max.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    max.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        loadImageBtn.setOnAction(actionEvent -> {
            try {
                loadImage(actionEvent);
            } catch (URISyntaxException | FileNotFoundException e) {
                e.printStackTrace();
            }
        });
        doSomethingBtn.setOnAction(this::changeColor);
        greyBtn.setOnAction(this::changeGrey);
        greyBtn1.setOnAction(this::changeGreyYUV);
        calcHistBtn.setOnAction(this::calcHist);
        saveBtn.setOnAction(this::saveImage);
        thresholdingBtn.setOnAction(this::thresholding);
        imageView = new ImageView();

        buttonHbox = new HBox();
        buttonHbox.setSpacing(5);
        buttonHbox.getChildren().addAll(loadImageBtn, doSomethingBtn, greyBtn, greyBtn1, calcHistBtn, saveBtn, thresholdingBtn, min, max);

        vbox.getChildren().addAll(buttonHbox, imageView);


        return vbox;

    }

    private void thresholding(ActionEvent actionEvent) {

//        if (!min.getText().isBlank()) {
//            if (Integer.parseInt(max.getText()) > 255) a = 255;
//        }
//        if (!max.getText().isBlank()) {
//            if (Integer.parseInt(max.getText()) > 255) a = 255;
//        }
        int minimum = Integer.parseInt(min.getText());
        int maximum = Integer.parseInt(max.getText());
        int R,G,B;
        int avg;
        Color color;
        Color color1;
        int tresh = 150;
        for(int i=0; i<imageWidth; i++) {
            for (int j = 0; j < imageHeight; j++) {
                color = copyImage.getPixelReader().getColor(i,j);
                double r = color.getRed();
                double g = color.getGreen();
                double b = color.getBlue();
                R = (int) (r * 255);
                G = (int) (g * 255);
                B = (int) (b * 255);
                avg = (R + G + B) / 3;
                if (avg > minimum && avg < maximum) {
                    color1  = Color.rgb(0, 0, 0);
                } else {
                    color1  = color;
                }
                pixelWriter.setColor(i, j, color1);
            }
        }
        imageView.setImage(writableImage);
    }

    // funkcja zapisujaca aktualnie przegladane zdjecie do pliku, plik widoczny w folderze po zamknieciu programu
    private void saveImage(ActionEvent actionEvent) {
        saveImageToJpg(writableImage);
    }

    private void calcHist(ActionEvent actionEvent) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        CalcHistDemo calcHistDemo = new CalcHistDemo();
        calcHistDemo.run(imageFilename);
    }

    private void changeGreyYUV(ActionEvent actionEvent) {
        int R,G,B;
        int avg;
        Color color;
        Color color1;
        for(int i=0; i<imageWidth; i++) {
            for (int j = 0; j < imageHeight; j++) {
                color = copyImage.getPixelReader().getColor(i,j);
                double r = color.getRed();
                double g = color.getGreen();
                double b = color.getBlue();
                R = (int) (r * 255);
                G = (int) (g * 255);
                B = (int) (b * 255);

                avg = (int) (0.299 * R + 0.587 * G + 0.114 * B);


                color1  = Color.rgb(avg, avg, avg);
                pixelWriter.setColor(i, j, color1);
            }
        }
        imageView.setImage(writableImage);
    }



    private void changeGrey(ActionEvent actionEvent) {
        int R,G,B;
        int avg;
        Color color;
        Color color1;
        for(int i=0; i < imageWidth; i++) {
            for (int j = 0; j < imageHeight; j++) {
                color = copyImage.getPixelReader().getColor(i,j);
                double r = color.getRed();
                double g = color.getGreen();
                double b = color.getBlue();
                R = (int) (r * 255);
                G = (int) (g * 255);
                B = (int) (b * 255);
                avg = (R + G + B) / 3;

                color1  = Color.rgb(avg, avg, avg);

                pixelWriter.setColor(i, j, color1);
            }
        }
        imageView.setImage(writableImage);
    }

    private void changeColor(ActionEvent actionEvent) throws NullPointerException {
        int R,G,B;
        Color color;
        Color color1;
        for(int i=0; i < imageWidth; i++) {
            for (int j = 0; j < imageHeight; j++) {
                color = copyImage.getPixelReader().getColor(i,j);
                double r = color.getRed();
                double g = color.getGreen();
                double b = color.getBlue();
                R = (int) (r * 255);
                G = (int) (g * 255);
                B = (int) (b * 255);

                color1  = Color.rgb(G, G, G);

                pixelWriter.setColor(i, j, color1);
            }
        }
        imageView.setImage(writableImage);
        saveImageToJpg(writableImage);
    }

    private void loadImage(ActionEvent actionEvent) throws URISyntaxException, FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("src/main/resources"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("jpg file", "*.jpg"),
                new FileChooser.ExtensionFilter("png file", "*.png"));
        File f = fileChooser.showOpenDialog(null);
        if (f != null) {
            imageFilename = f.toString();
            image = new Image(new FileInputStream(imageFilename));
            imageView.setImage(image);
            imageView.setPreserveRatio(true);
            imageView.setFitHeight(200);
            imageView.setFitWidth(200);
            writableImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
            pixelWriter = writableImage.getPixelWriter();
            copyImage = image;
            imageWidth = (int) image.getWidth();
            imageHeight = (int) image.getHeight();
        }

    }

    // funkacja zapisujaca do pliku
    private void saveImageToJpg(Image imageFx) {
        BufferedImage image = SwingFXUtils.fromFXImage(imageFx, null);

// Remove alpha-channel from buffered image:
        BufferedImage imageRGB = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.OPAQUE);

        Graphics2D graphics = imageRGB.createGraphics();

        graphics.drawImage(image, 0, 0, null);
        String f = writableImage.toString();

        File file = new File("H:\\Mateusz\\demo (1)\\pocFx\\src\\main\\java\\com\\example\\pocfx\\jpg\\" + f.substring(32,39) + ".jpg");

        try {
            ImageIO.write(imageRGB, "jpg", file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        graphics.dispose();
    }

    public static void main(String[] args) {
        launch();
    }
}