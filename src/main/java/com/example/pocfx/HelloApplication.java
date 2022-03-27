package com.example.pocfx;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
    private TextField min;
    private TextField max;
    private ImageView imageView;
    private Image image;

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent p = create();

        Scene scene = new Scene(p);

        primaryStage.setTitle("POC1");
        primaryStage.setWidth(499);
        primaryStage.setHeight(499);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Parent create() {
        VBox vbox = new VBox();
        vbox.setPadding( new Insets(10) );
        vbox.setSpacing( 10 );

        loadImageBtn = new Button("Load image");
        doSomethingBtn = new Button("Change color");
        greyBtn = new Button("Grey");
        loadImageBtn.setOnAction(actionEvent -> {
            try {
                loadImage(actionEvent);
            } catch (URISyntaxException | FileNotFoundException e) {
                e.printStackTrace();
            }
        });
        doSomethingBtn.setOnAction(this::changeColor);
        greyBtn.setOnAction(this::changeGrey);
        imageView = new ImageView();

        buttonHbox = new HBox();
        buttonHbox.setSpacing(5);
        buttonHbox.getChildren().addAll(loadImageBtn, doSomethingBtn, greyBtn);

        vbox.getChildren().addAll(buttonHbox, imageView);


        return vbox;

    }

    private void changeGrey(ActionEvent actionEvent) {

    }

    private void changeColor(ActionEvent actionEvent) throws NullPointerException {
        PixelReader pix = image.getPixelReader();
        Image copyImage = image;
        WritableImage writableImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        int width = (int) copyImage.getWidth();
        int height = (int) copyImage.getHeight();
        int pixel, R,G,B;
        Color color;
        Color color1;
        for(int i=0; i<width; i++) {
            for (int j = 0; j < height; j++) {
                color = copyImage.getPixelReader().getColor(i,j);
                double r = color.getRed();
                double g = color.getGreen();
                double b = color.getBlue();
                R = (int) (r * 255);
                G = (int) (g * 255);
                B = (int) (b * 255);

                color1  = Color.rgb(G, G, 0);

                pixelWriter.setColor(i, j, color1);
            }
        }
        imageView.setImage(writableImage);
    }

    private void loadImage(ActionEvent actionEvent) throws URISyntaxException, FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("src/main/resources"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("jpg file", "*.jpg"),
                new FileChooser.ExtensionFilter("png file", "*.png"));
        File f = fileChooser.showOpenDialog(null);
        if (f != null) {
            System.out.println(f);
            image = new Image(new FileInputStream(f));
            imageView.setImage(image);
            imageView.setPreserveRatio(true);
            imageView.setFitHeight(200);
            imageView.setFitWidth(200);
        }

    }

    public static void main(String[] args) {
        launch();
    }
}