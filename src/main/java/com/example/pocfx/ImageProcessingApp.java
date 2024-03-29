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
    private Button rotateBtn;

    private Slider slider11;
    private Slider slider12;
    private Slider slider21;
    private Slider slider22;
    private Slider slidertx;
    private Slider sliderty;


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
    private CalcDemo calcDemo;

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
        rotateBtn = new Button("Rotate");
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
            if (!c.getText().matches("(-*(\\d+)?;?)+")) {
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
        rotateBtn.setOnAction(this::rotateImage);
        calcDemo = new CalcDemo();
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
        imageViewResult.setFitWidth(500);
        imageViewResult.setFitHeight(500);
        imageViewResult.setPreserveRatio(true);

        organizeSliders();
        VBox vbox = organizeNodes();

        return vbox;

    }

    private void organizeSliders() {
        slider11 = new Slider(-2, 2, 1);
        slider11.setShowTickMarks(true);
        slider11.setShowTickLabels(true);
        slider11.setMajorTickUnit(0.25f);
        slider11.setBlockIncrement(0.1f);
        slider11.valueProperty().addListener((p) -> rotateBtn.fire());


        slider12 = new Slider(-2, 2, 0);
        slider12.setShowTickMarks(true);
        slider12.setShowTickLabels(true);
        slider12.setMajorTickUnit(0.25f);
        slider12.setBlockIncrement(0.1f);
        slider12.valueProperty().addListener((p) -> rotateBtn.fire());


        slider21 = new Slider(-2, 2, 0);
        slider21.setShowTickMarks(true);
        slider21.setShowTickLabels(true);
        slider21.setMajorTickUnit(0.25f);
        slider21.setBlockIncrement(0.1f);
        slider21.valueProperty().addListener((p) -> rotateBtn.fire());

        slider22 = new Slider(-2, 2, 1);
        slider22.setShowTickMarks(true);
        slider22.setShowTickLabels(true);
        slider22.setMajorTickUnit(0.25f);
        slider22.setBlockIncrement(0.1f);
        slider22.valueProperty().addListener((p) -> rotateBtn.fire());

        slidertx = new Slider(-1000, 1000, 0);
        slidertx.setShowTickMarks(true);
        slidertx.setShowTickLabels(true);
        slidertx.setMajorTickUnit(50);
        slidertx.setBlockIncrement(25);
        slidertx.valueProperty().addListener((p) -> rotateBtn.fire());

        sliderty = new Slider(-1000, 1000, 0);
        sliderty.setShowTickMarks(true);
        sliderty.setShowTickLabels(true);
        sliderty.setMajorTickUnit(50);
        sliderty.setBlockIncrement(25);
        sliderty.valueProperty().addListener((p) -> rotateBtn.fire());
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
        VBox imageViewSliders = new VBox(imageViewResult, slider11, slider12, slider21, slider22, slidertx, sliderty);
        HBox imageHbox = new HBox(imageViewVbox, imageViewVbox1, imageViewSliders);
        buttonHbox.getChildren().addAll(doSomethingBtn, greyBtn, greyBtn1, calcHistBtn, saveBtn,
                thresholdingBtn, min, max, imageSubtractionBtn, imageFilterBtn, maskFilter, rotateBtn);

        vbox.getChildren().addAll(buttonHbox, imageHbox);

        return vbox;
    }

    private void filterImage(ActionEvent actionEvent) {
        imageViewResult.setImage(calcDemo.imageFilter(maskFilter.getText()));
    }

    private void imageSubtraction(ActionEvent actionEvent) throws IOException {
        imageViewResult.setImage(calcDemo.imageSubtraction());
    }

    /**Progowanie (ang. thresholding) – metoda uzyskiwania obrazu binarnego (posiadającego tylko kolor
     biały i czarny) na podstawie obrazu kolorowego lub w odcieniach szarości. Polega na wyznaczeniu dla
     danego obrazu progu jasności, a następnie piksele jaśniejsze od wyznaczonego progu otrzymują jedną
     wartość, a ciemniejsze drugą. Częstym zastosowaniem progowania jest oddzielenie obiektów
     pierwszoplanowych od tła.**/
    // funkcja wykorzystuje podany przez uzytkownika prog lub, jesli nie jest podany uzyta zostanie wartosc wyliczona z histogramu
    private void thresholding(ActionEvent actionEvent) {
        if (min.getText().isBlank() || max.getText().isBlank()) {
            imageViewResult.setImage(calcDemo.calcThreshold());
        } else {
            int minimum = Integer.parseInt(min.getText());
            int maximum = Integer.parseInt(max.getText());
            imageViewResult.setImage(calcDemo.calcThresholdWithUserInput(minimum, maximum));
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
        imageViewResult.setImage(calcDemo.calcHistogram(copyImage));
    }
    // zmienia obraz na odcienie szarosci przez zmiane warotsci RGB ale srednia wylicza z YUV

    private void changeGreyYUV(ActionEvent actionEvent) {
        imageViewResult.setImage(calcDemo.greyScaleYUV());
    }

    // funkcja zmienia kolor obrazu przez przypisanie wartoscia R G B wartosci sredniej, obraz zostaje zmieniony w odcienie szarosci

    private void changeGreyRGB(ActionEvent actionEvent) {
        imageViewResult.setImage(calcDemo.greyScaleRGB());
    }
    // funkcja zmienia kolor obrazu w zaleznosci od podanych wartosci R, G, B

    private void changeColor(ActionEvent actionEvent) throws NullPointerException {
        imageViewResult.setImage(calcDemo.changeColor());
    }

    private void rotateImage(ActionEvent actionEvent) {
        double s11 = slider11.getValue();
        double s12 = slider12.getValue();
        double s21 = slider21.getValue();
        double s22 = slider22.getValue();
        double stx = slidertx.getValue();
        double sty = sliderty.getValue();
        Image im = null;
        try {
            im = calcDemo.rotateImage(new double[6], s11, s12, s21, s22, stx, sty);
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageViewResult.setImage(im);
    }

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
        imageView.setFitHeight(500);
        imageView.setFitWidth(500);
        selectImage(null, image);
    }

    private void selectImage(ActionEvent actionEvent, Image image) throws IOException {
        writableImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
        pixelWriter = writableImage.getPixelWriter();
        copyImage = image;
        imageWidth = (int) image.getWidth();
        imageHeight = (int) image.getHeight();

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        calcDemo.setWritableImage(writableImage);
        calcDemo.setPixelWriter(pixelWriter);
        calcDemo.setFirstImage(imageView.getImage());
        calcDemo.setSecondImage(imageView1.getImage());
        calcDemo.setSelectedImage(image);
        calcDemo.run();
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