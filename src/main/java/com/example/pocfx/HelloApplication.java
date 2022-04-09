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
import javafx.scene.control.TextFormatter;
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
    private CalcHistDemo calcHistDemo;

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

    // funkcja tworzaca nowe okno, inicjalizujaca obiekty
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
        max.setTextFormatter(new TextFormatter<>(c -> {
            if (!c.getText().matches("\\d")) {
                c.setText("");
            }
            return c;
        }));
        min.setMaxWidth(50);
        max.setMaxWidth(50);
        min.setPromptText("min");
        max.setPromptText("max");
        min.setTextFormatter(new TextFormatter<>(c -> {
            if (!c.getText().matches("\\d")) {
                c.setText("");
            }
            return c;
        }));
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

    /**Progowanie (ang. thresholding) – metoda uzyskiwania obrazu binarnego (posiadającego tylko kolor
     biały i czarny) na podstawie obrazu kolorowego lub w odcieniach szarości. Polega na wyznaczeniu dla
     danego obrazu progu jasności, a następnie piksele jaśniejsze od wyznaczonego progu otrzymują jedną
     wartość, a ciemniejsze drugą. Częstym zastosowaniem progowania jest oddzielenie obiektów
     pierwszoplanowych od tła.**/
    // funkcja wykorzystuje podany przez uzytkownika prog lub, jesli nie jest podany uzyta zostanie wartosc wyliczona z histogramu
    private void thresholding(ActionEvent actionEvent) {
        int minimum = 100;
        int maximum = 150;
        if (!min.getText().isBlank()) {
           minimum  = Integer.parseInt(min.getText());
        }
        if (!max.getText().isBlank()) {
            maximum = Integer.parseInt(max.getText());
        }
        if (maximum > 255) {
            maximum = 255;
        }
        if (minimum > 255) {
            minimum = 255;
        }
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
                if (avg > calcHistDemo.getPeakHist()) {
                    color1  = Color.rgb(0, 0, 0);
                } else {
                    color1  = Color.rgb(255, 255, 255);
                }
                pixelWriter.setColor(i, j, color1);
            }
        }
        imageView.setImage(writableImage);
    }

    // funkcja zapisujaca aktualnie przegladane zdjecie do pliku, plik jest widoczny w folderze po zamknieciu programu
    private void saveImage(ActionEvent actionEvent) {
        saveImageToJpg(writableImage);
    }

    // funkcja wywoujaca clase od histogramu
    private void calcHist(ActionEvent actionEvent) {
        calcHistDemo.showHistogram();
    }

    // zmienia obraz na odcienie szarosci przez zmiane warotsci RGB ale srednia wylicza z YUV
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


    // funkcja zmienia kolor obrazu przez przypisanie wartoscia R G B wartosci sredniej, obraz zostaje zmieniony w odcienie szarosci
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

    // funkcja zmienia kolor obrazu w zaleznosci od podanych wartosci R, G, B
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

    // funkcja ładująca zdjecia
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
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        calcHistDemo = new CalcHistDemo();
        calcHistDemo.run(imageFilename);
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