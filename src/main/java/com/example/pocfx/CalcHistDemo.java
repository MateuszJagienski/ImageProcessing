package com.example.pocfx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

public class CalcHistDemo {

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
        src = ImageConverter.convertImageFxToMat(firstImage);
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
        System.out.println(maximum);
        System.out.println(minimum);
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
        //Core.subtract(src, dst, mat);
        //Core.absdiff(src, dst, mat);
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

        // writableImage = (WritableImage) ImageConverter.convertImageMatToFx(mat);
        return writableImage;
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
