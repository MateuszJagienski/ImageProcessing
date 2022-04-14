package com.example.pocfx;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.opencv.core.Core;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;

public class ImageProcessingApp extends Application {

    private HBox buttonHbox;
    private Button loadImageBtn;
    private Button loadImageBtn1;
    private Button doSomethingBtn;
    private Button greyBtn;
    private Button greyBtn1;
    private Button calcHistBtn;
    private Button saveBtn;
    private Button thresholdingBtn;
    private Button selectImageBtn;
    private Button selectImageBtn1;
    private Button imageSubtractionBtn;
    private Button imageFilterBtn;


    private TextField min;
    private TextField max;
    private TextField maskFilter;

    private ImageView imageView;
    private ImageView imageView1;
    private ImageView imageViewResult;
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
        primaryStage.setHeight(800);
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // funkcja tworzaca nowe okno
    private Parent create() throws IOException {

        loadImageBtn = new Button("Load image");
        doSomethingBtn = new Button("Change color");
        greyBtn = new Button("GreyRGB");
        greyBtn1 = new Button("GreyYUV");
        calcHistBtn = new Button("Cals hist");
        saveBtn = new Button("Save");
        thresholdingBtn = new Button("Thresholding");
        loadImageBtn1 = new Button("Load image");
        selectImageBtn = new Button("Select");
        selectImageBtn1 = new Button("Select");
        imageSubtractionBtn = new Button("Subtract images");
        imageFilterBtn = new Button("Filter");
        min = new TextField();
        max = new TextField();
        maskFilter = new TextField();
        maskFilter.setPromptText("np. 1;2;3;1;2;3;1;2;3");
        max.setTextFormatter(new TextFormatter<>(c -> {
            if (!c.getText().matches("\\d")) {
                c.setText("");
            }
            return c;
        }));
        maskFilter.setTextFormatter(new TextFormatter<>(c -> {
            if (!c.getText().matches("\\d") && !c.getText().equals(";") && !c.getText().equals("-")) {
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
                uploadImage(actionEvent, imageView);
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
            }
        });

        loadImageBtn1.setOnAction(actionEvent -> {
            try {
                uploadImage(actionEvent, imageView1);
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
            }
        });
        doSomethingBtn.setOnAction(this::changeColor);
        greyBtn.setOnAction(this::changeGreyRGB);
        greyBtn1.setOnAction(this::changeGreyYUV);
        calcHistBtn.setOnAction(actionEvent -> {
            try {
                calcHist(actionEvent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        saveBtn.setOnAction(this::saveImage);
        thresholdingBtn.setOnAction(this::thresholding);
        imageSubtractionBtn.setOnAction(actionEvent1 -> {
            try {
                imageSubtraction(actionEvent1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        calcHistDemo = new CalcHistDemo();
        imageView = new ImageView();
        imageView1 = new ImageView();
        loadImage(imageView, "src/main/resources/Images/jp2.jpg");
        loadImage(imageView1, "src/main/resources/Images/jp2.jpg");

        selectImageBtn.setOnAction(actionEvent -> {
            try {
                selectImage(actionEvent, imageView.getImage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        selectImageBtn1.setOnAction(actionEvent -> {
            try {
                selectImage(actionEvent, imageView1.getImage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        imageFilterBtn.setOnAction(this::filterImage);

        imageViewResult = new ImageView();
        imageViewResult.setFitWidth(350);
        imageViewResult.setFitHeight(350);
        imageViewResult.setPreserveRatio(true);
        VBox vbox = organizeNodes();

        return vbox;

    }

    private VBox organizeNodes() {
        VBox vbox = new VBox();
        vbox.setPadding( new Insets(10) );
        vbox.setSpacing( 10 );

        buttonHbox = new HBox();
        buttonHbox.setSpacing(5);
        HBox imageButtonHbox = new HBox();
        HBox imageButtonHbox1 = new HBox();
        imageButtonHbox.getChildren().addAll(loadImageBtn, selectImageBtn);
        imageButtonHbox1.getChildren().addAll(loadImageBtn1, selectImageBtn1);

        VBox imageViewVbox = new VBox(imageView, imageButtonHbox);
        VBox imageViewVbox1 = new VBox(imageView1, imageButtonHbox1);
        HBox imageHbox = new HBox(imageViewVbox, imageViewVbox1);
        buttonHbox.getChildren().addAll(doSomethingBtn, greyBtn, greyBtn1, calcHistBtn, saveBtn,
                thresholdingBtn, min, max, imageSubtractionBtn, imageFilterBtn, maskFilter);

        vbox.getChildren().addAll(buttonHbox, imageHbox, imageViewResult);
        return vbox;
    }

    private void filterImage(ActionEvent actionEvent) {
        imageViewResult.setImage(calcHistDemo.imageFilter(maskFilter.getText()));
    }

    private void imageSubtraction(ActionEvent actionEvent) throws IOException {
        imageViewResult.setImage(calcHistDemo.imageSubtraction());
    }

    /**Progowanie (ang. thresholding) – metoda uzyskiwania obrazu binarnego (posiadającego tylko kolor
     biały i czarny) na podstawie obrazu kolorowego lub w odcieniach szarości. Polega na wyznaczeniu dla
     danego obrazu progu jasności, a następnie piksele jaśniejsze od wyznaczonego progu otrzymują jedną
     wartość, a ciemniejsze drugą. Częstym zastosowaniem progowania jest oddzielenie obiektów
     pierwszoplanowych od tła.**/
    // funkcja wykorzystuje podany przez uzytkownika prog lub, jesli nie jest podany uzyta zostanie wartosc wyliczona z histogramu
    private void thresholding(ActionEvent actionEvent) {
        if (min.getText().isBlank() || max.getText().isBlank()) {
            imageViewResult.setImage(calcHistDemo.calcThreshold());
        } else {
            int minimum = Integer.parseInt(min.getText());
            int maximum = Integer.parseInt(max.getText());
            imageViewResult.setImage(calcHistDemo.calcThresholdWithUserInput(minimum, maximum));
        }
    }

    // funkcja zapisujaca aktualnie przegladane zdjecie do pliku, plik jest widoczny w folderze po zamknieciu programu
    private void saveImage(ActionEvent actionEvent) {
        if (imageViewResult.getImage() == null) {
            System.out.println("Brak zdjecia do zapisania!");
        } else {
            saveImageToJpg(imageViewResult.getImage());
        }
    }

    // funkcja wywoujaca clase od histogramu
    private void calcHist(ActionEvent actionEvent) throws IOException {
        imageViewResult.setImage(calcHistDemo.calcHistogram(copyImage));
    }

    // zmienia obraz na odcienie szarosci przez zmiane warotsci RGB ale srednia wylicza z YUV
    private void changeGreyYUV(ActionEvent actionEvent) {
        imageViewResult.setImage(calcHistDemo.greyScaleYUV());
    }


    // funkcja zmienia kolor obrazu przez przypisanie wartoscia R G B wartosci sredniej, obraz zostaje zmieniony w odcienie szarosci
    private void changeGreyRGB(ActionEvent actionEvent) {
        imageViewResult.setImage(calcHistDemo.greyScaleRGB());
    }

    // funkcja zmienia kolor obrazu w zaleznosci od podanych wartosci R, G, B
    private void changeColor(ActionEvent actionEvent) throws NullPointerException {
        imageViewResult.setImage(calcHistDemo.changeColor());
    }

    // funkcja ładująca zdjecia
    private void uploadImage(ActionEvent actionEvent, ImageView imageView) throws URISyntaxException, IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("src/main/resources"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("jpg file", "*.jpg"),
                new FileChooser.ExtensionFilter("png file", "*.png"));
        File f = fileChooser.showOpenDialog(null);
        if (f != null) {
            imageFilename = f.toString();
            loadImage(imageView, imageFilename);
        }
    }

    private void loadImage(ImageView imageView, String imageFilename) throws IOException {
        image = new Image(new FileInputStream(imageFilename));
        imageView.setImage(image);
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(350);
        imageView.setFitWidth(350);
        selectImage(null, image);
    }

    private void selectImage(ActionEvent actionEvent, Image image) throws IOException {
        writableImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
        pixelWriter = writableImage.getPixelWriter();
        copyImage = image;
        imageWidth = (int) image.getWidth();
        imageHeight = (int) image.getHeight();

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        calcHistDemo.setWritableImage(writableImage);
        calcHistDemo.setPixelWriter(pixelWriter);
        calcHistDemo.setFirstImage(imageView.getImage());
        calcHistDemo.setSecondImage(imageView1.getImage());
        calcHistDemo.setSelectedImage(image);
        calcHistDemo.run();
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

        File file = new File("src/main/resources/Images/" + f.substring(32,39) + ".jpg");

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