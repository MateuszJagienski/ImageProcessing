package com.example.pocfx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

public class CalcDemo {

    private Mat src;
    private Mat histImage;
    private Mat dst;
    private float bMax;
    private float gMax;
    private float rMax;
    private double thres;
    private WritableImage writableImage;
    private PixelWriter pixelWriter;
    private Image firstImage;
    private Image secondImage;
    private Image selectedImage;

    public void run() throws IOException {
        src = ImageConverter.convertImageFxToMat(selectedImage);
        if (src.empty()) {
            System.err.println("Cannot read image: " + firstImage);
            System.exit(0);
        }
    }

    public Image calcHistogram(Image image) throws IOException {
        src = ImageConverter.convertImageFxToMat(image);
        List<Mat> bgrPlanes = new ArrayList<>();
        Core.split(src, bgrPlanes);
        int histSize = 256;
        float[] range = {0, 256}; //the upper boundary is exclusive
        MatOfFloat histRange = new MatOfFloat(range);
        boolean accumulate = false;
        Mat bHist = new Mat(), gHist = new Mat(), rHist = new Mat();
        Imgproc.calcHist(bgrPlanes, new MatOfInt(0), new Mat(), bHist, new MatOfInt(histSize), histRange, accumulate);
        Imgproc.calcHist(bgrPlanes, new MatOfInt(1), new Mat(), gHist, new MatOfInt(histSize), histRange, accumulate);
        Imgproc.calcHist(bgrPlanes, new MatOfInt(2), new Mat(), rHist, new MatOfInt(histSize), histRange, accumulate);
        int histW = 512, histH = 400;
        int binW = (int) Math.round((double) histW / histSize);
        histImage = new Mat( histH, histW, CvType.CV_8UC3, new Scalar( 0,0,0) );
        Core.normalize(bHist, bHist, 0, histImage.rows(), Core.NORM_MINMAX);
        Core.normalize(gHist, gHist, 0, histImage.rows(), Core.NORM_MINMAX);
        Core.normalize(rHist, rHist, 0, histImage.rows(), Core.NORM_MINMAX);
        float[] bHistData = new float[(int) (bHist.total() * bHist.channels())];
        bHist.get(0, 0, bHistData);
        float[] gHistData = new float[(int) (gHist.total() * gHist.channels())];
        gHist.get(0, 0, gHistData);
        float[] rHistData = new float[(int) (rHist.total() * rHist.channels())];
        rHist.get(0, 0, rHistData);
        for( int i = 1; i < histSize; i++ ) {
            Imgproc.line(histImage, new Point(binW * (i - 1), histH - Math.round(bHistData[i - 1])),
                    new Point(binW * (i), histH - Math.round(bHistData[i])), new Scalar(255, 0, 0), 2);
            Imgproc.line(histImage, new Point(binW * (i - 1), histH - Math.round(gHistData[i - 1])),
                    new Point(binW * (i), histH - Math.round(gHistData[i])), new Scalar(0, 255, 0), 2);
            Imgproc.line(histImage, new Point(binW * (i - 1), histH - Math.round(rHistData[i - 1])),
                    new Point(binW * (i), histH - Math.round(rHistData[i])), new Scalar(0, 0, 255), 2);
        }

        return ImageConverter.convertImageMatToFx(histImage);
    }

    public Image calcThreshold() {
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY);
        Imgproc.adaptiveThreshold(src, src, 255.0,
                Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                Imgproc.THRESH_BINARY, 11, 2);
        return ImageConverter.convertImageMatToFx(src);
    }

    public Image calcThresholdWithUserInput(int minimum, int maximum) {
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
        for(int i = 0; i< firstImage.getWidth(); i++) {
            for (int j = 0; j < firstImage.getHeight(); j++) {
                color = firstImage.getPixelReader().getColor(i,j);
                double r = color.getRed();
                double g = color.getGreen();
                double b = color.getBlue();
                R = (int) (r * 255);
                G = (int) (g * 255);
                B = (int) (b * 255);
                avg = (R + G + B) / 3;
                if (avg > minimum && avg < maximum) {
                    color1  = Color.rgb(255, 255, 255);
                } else {
                    color1  = Color.rgb(0, 0, 0);
                }
                pixelWriter.setColor(i, j, color1);
            }
        }
        return writableImage;
    }

    public Image greyScaleYUV() {
        int R,G,B;
        int avg;
        Color color;
        Color color1;
        for(int i = 0; i< selectedImage.getWidth(); i++) {
            for (int j = 0; j < selectedImage.getHeight(); j++) {
                color = selectedImage.getPixelReader().getColor(i,j);
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
        return writableImage;
    }

    public Image greyScaleRGB() {
        int R,G,B;
        int avg;
        Color color;
        Color color1;
        for(int i = 0; i < selectedImage.getWidth(); i++) {
            for (int j = 0; j < selectedImage.getHeight(); j++) {
                color = selectedImage.getPixelReader().getColor(i,j);
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
        return writableImage;

    }

    public Image changeColor() {
        int R,G,B;
        Color color;
        Color color1;
        for(int i = 0; i < selectedImage.getWidth(); i++) {
            for (int j = 0; j < selectedImage.getHeight(); j++) {
                color = selectedImage.getPixelReader().getColor(i,j);
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
        return writableImage;
    }

    public Image imageSubtraction() throws IOException {
        if (firstImage.getWidth() != secondImage.getWidth() || firstImage.getHeight() != secondImage.getHeight()) {
            System.out.println("Rozmiar zdjec nie zgadza sie!");
            return null;
        }
        Mat mat = new Mat();
        dst = ImageConverter.convertImageFxToMat(secondImage);
        Color color;
        Color color1;
        int R, G, B, avg, avg1, kol;
        for(int i = 0; i < firstImage.getWidth(); i++) {
            for (int j = 0; j < firstImage.getHeight(); j++) {
                color = firstImage.getPixelReader().getColor(i, j);
                color1 = secondImage.getPixelReader().getColor(i, j);

                double r = color.getRed();
                double g = color.getGreen();
                double b = color.getBlue();
                R = (int) (r * 255);
                G = (int) (g * 255);
                B = (int) (b * 255);
                avg = (R + G + B) / 3;

                double r1 = color1.getRed();
                double g1 = color1.getGreen();
                double b1 = color1.getBlue();
                R = (int) (r1 * 255);
                G = (int) (g1 * 255);
                B = (int) (b1 * 255);
                avg1 = (R + G + B) / 3;
                if (avg - avg1 < 0) {

                    kol = 0;
                } else {
                    kol = avg - avg1;
                }

                color1  = Color.rgb(kol, kol, kol);

                pixelWriter.setColor(i, j, color1);
            }
        }
        return writableImage;
    }

    public Image imageFilter(String mask) {
        Color color, color1, filterColor;
        int R, G, B, avg, avg1, kol;
        int[][] maska;
        maska = decode(mask);
        int K = 0;
        System.out.println(Arrays.deepToString(maska));
        for (int[] c: maska) {
            for (int i : c) {
                K += i;
            }
        }
        if (K == 0) K = 1;
        System.out.println(maska.length);
        int edge = maska.length;
        for(int i = 0; i < selectedImage.getWidth(); i++) {
            for (int j = 0; j < selectedImage.getHeight(); j++) {
                if (i < edge || j < edge || (edge + i) >= selectedImage.getWidth() || (edge + j) >= selectedImage.getHeight()) {
                    pixelWriter.setColor(i, j, Color.BLACK);
                } else {
                    int R1 = 0;
                    int G1 = 0;
                    int B1 = 0;

                    for (int i1 = i - 1; i1 <= i + 1; i1++) {
                        for (int j1 = j - 1; j1 <= j + 1; j1++) {
                            color = selectedImage.getPixelReader().getColor(i1, j1);
                            double r = color.getRed();
                            double g = color.getGreen();
                            double b = color.getBlue();
                            R = (int) (r * 255);
                            G = (int) (g * 255);
                            B = (int) (b * 255);
                            R1 += (R * maska[i - i1 + 1][j - j1 + 1]);
                            G1 += (G * maska[i - i1 + 1][j - j1 + 1]);
                            B1 += (B * maska[i - i1 + 1][j - j1 + 1]);
                        }
                    }

                    R1 = R1 / K;
                    G1 = G1 / K;
                    B1 = B1 / K;
                    if (R1 > 255) {
                        R1 = 255;
                    }
                    if (G1 > 255) {
                        G1 = 255;
                    }
                    if (B1 > 255) {
                        B1 = 255;
                    }
                    if (R1 < 0) {
                        R1 = 0;
                    }
                    if (G1 < 0) {
                        G1 = 0;
                    }
                    if (B1 < 0) {
                        B1 = 0;
                    }

                    filterColor = Color.rgb(R1, G1, B1);

                    pixelWriter.setColor(i, j, filterColor);
                }
            }
        }
        return writableImage;
    }

    public Image rotateImage(double[] matrix, double s11, double s12, double s21, double s22, double stx, double sty) throws IOException {
        Image scaleImage = scale(selectedImage, 350, 350, true);
        WritableImage writableImage1 = new WritableImage(500, 500);
        PixelWriter pixelWriter1 = writableImage1.getPixelWriter();
        double[][] D = new double [][] {{s11 , s12, stx}, {s21, s22, sty}, {0, 0, 1}};
        double[][] B;
        double[][] C;

        for (int i = 0; i < writableImage1.getWidth(); i++) {
            for (int j = 0; j < writableImage1.getHeight(); j++) {
                pixelWriter1.setColor(i, j, Color.BLACK);
            }
        }

        for (int i = 0; i < (int) scaleImage.getWidth(); i++) {
            for (int j = 0; j < (int) scaleImage.getHeight(); j++) {
                B = new double[][]{{i}, {j}, {1}};
                C = MatrixCalc.multiplyMatrix(3,3, D,3,1, B);
                try {
                    if (C[0][0] >= 0
                            && C[1][0] >= 0
                            && i < scaleImage.getWidth()
                            && j < scaleImage.getHeight()) {
                        pixelWriter1.setColor((int) (C[0][0]), (int) (C[1][0]), scaleImage.getPixelReader().getColor(i, j));
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return writableImage1;
    }

    private int[][] decode(String mask) {
        int[][] defaultMask = new int[][] {{1,1,1}, {1, 1, 1}, {1, 1, 1}};
        if (mask.isBlank()) {
            return defaultMask;
        }
        String[] s = mask.split(";");
        int len = s.length;
        if (Math.sqrt(len * 1.0) % 1 != 0) {
            System.out.println("zła format maski!");
            return defaultMask;
        }
        int size = (int) Math.sqrt(len);
        int[][] maska = new int[size][size];
        int k = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                maska[i][j] = Integer.parseInt(s[k]);
                k++;
            }
        }
        return maska;

    }

    private Image scale(Image src, int targetWidth, int targetHeight, boolean preserveRatio) {
        ImageView imageView = new ImageView(src);
        imageView.setPreserveRatio(preserveRatio);
        imageView.setFitWidth(targetWidth);
        imageView.setFitHeight(targetHeight);
        return imageView.snapshot(null, null);
    }


    public void setWritableImage(WritableImage writableImage) {
        this.writableImage = writableImage;
    }

    public void setPixelWriter(PixelWriter pixelWriter) {
        this.pixelWriter = pixelWriter;
    }

    public void setFirstImage(Image firstImage) {
        this.firstImage = firstImage;
    }

    public void setSecondImage(Image secondImage) {
        this.secondImage = secondImage;
    }

    public void setSelectedImage(Image selectedImage) {
        this.selectedImage = selectedImage;
    }
}
