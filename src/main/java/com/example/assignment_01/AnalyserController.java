package com.example.assignment_01;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;

import java.util.List;
import java.util.Random;

public class AnalyserController {
    public Label display;
    @FXML
    Image image, displayImage;

    @FXML
    ImageView imageView0, imageView1;

    Color pickedColour;


    @FXML
    //Exits the program
    private void exit() {
        System.exit(0);
    }

    @FXML
        // Open an image
    void openImage() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png"));

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            image = new Image(selectedFile.toURI().toString());
            imageView0.setImage(image);
            imageView1.setImage(image);
        }
    }

    @FXML
    public void reset() {
        imageView1.setImage(image);
    }

    int clickedX = 0;
    int clickedY = 0;

    @FXML
    private void imageClick(MouseEvent event) {
        if (image != null) {
            double x = event.getX();
            double y = event.getY();

            int roundedX = (int) (x / imageView1.getBoundsInLocal().getWidth() * image.getWidth());
            int roundedY = (int) (y / imageView1.getBoundsInLocal().getHeight() * image.getHeight());

            int imageWidth = (int) image.getWidth();
            int imageHeight = (int) image.getHeight();

            if (roundedX >= 0 && roundedX < imageWidth && roundedY >= 0 && roundedY < imageHeight) {
                Color clickedPixel = image.getPixelReader().getColor(roundedX, roundedY);
                Image modImage = modImagePixels(clickedPixel, image);
                imageView1.setImage(modImage);
                pickedColour = clickedPixel;

                clickedX = roundedX;
                clickedY = roundedY;
            }
        }
    }


    private Image modImagePixels(Color color, Image image) {

        PixelReader pixelReader = image.getPixelReader();
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();


        WritableImage writableImage = new WritableImage(width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                Color pixelColour = pixelReader.getColor(x, y);

                if (similarColour(color, pixelColour)) {
                    pixelWriter.setColor(x, y, Color.WHITE);
                } else {
                    pixelWriter.setColor(x, y, Color.BLACK);
                }
            }
        }

        return writableImage;
    }


    private boolean similarColour(Color color0, Color color1) {

        return Math.abs(color0.getRed() - color1.getRed()) < 0.1 &&
                Math.abs(color0.getGreen() - color1.getGreen()) < 0.08 &&
                Math.abs(color0.getBlue() - color1.getBlue()) < 0.11;

    }

    int[] imageAnalyser;


    @FXML
    private void confirm() {

        PixelReader pixelReader = imageView1.getImage().getPixelReader();
        int width = (int) imageView1.getImage().getWidth();
        int height = (int) imageView1.getImage().getHeight();

        imageAnalyser = new int[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                Color pixelColour = pixelReader.getColor(x, y);

                if (!pixelColour.equals(Color.BLACK)) {
                    imageAnalyser[y * width + x] = y * width + x;
                } else {
                    imageAnalyser[y * width + x] = -1;
                }
            }

        }


        myDisjointSet disjointSet = new myDisjointSet(width * height);


        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = y * width + x;

                if (imageAnalyser[index] == -1) {
                    continue;
                }

                if (x + 1 < width && imageAnalyser[index + 1] != -1) {
                    disjointSet.union(imageAnalyser, index, index + 1);
                }

                if (y + 1 < height && imageAnalyser[index + width] != -1) {
                    disjointSet.union(imageAnalyser, index, index + width);
                }

                if (x > 0 && y + 1 < height && imageAnalyser[index + width - 1] != -1) {
                    disjointSet.union(imageAnalyser, index, index + width - 1);
                }

            }
        }

        displayDSAsText(width);

        int size = calcSize(clickedX, clickedY);

        int[] pills = new int[width * height];
        for (int i = 0; i < imageAnalyser.length; i++) {
            int root = disjointSet.find(imageAnalyser, i);
            if (root != -1) {
                pills[root]++;
            }
        }

        int pillCounter = 1;
        String label = "";

        for (int i = 0; i < pills.length; i++) {
            if (pills[i] > (size - size / 4)) {
                System.out.println("Pill " + pillCounter + " is " + pills[i] + " pixels in size");
                label += "Pill " + pillCounter + " is " + pills[i] + " pixels in size\n";
                pillCounter++;
            }
        }

        pillCounter = pillCounter - 1;
        //System.out.println("Total amount of Pills = " + pillCounter + " with the Colour of " + pickedColour.toString());
        display.setText(label + "Total amount of Pills = " + pillCounter + " with the Colour of " + pickedColour.toString());
        getRectanglePositions(imageAnalyser);
    }

    private int calcSize(int clickedX, int clickedY) {
        int width = (int) imageView0.getImage().getWidth();
        int height = (int) imageView0.getImage().getHeight();

        myDisjointSet disjointSet = new myDisjointSet(width * height);

        int index = clickedY * width + clickedX;

        int root = disjointSet.find(imageAnalyser, index);

        int size = 0;
        for (int i = 0; i < imageAnalyser.length; i++) {
            if (disjointSet.find(imageAnalyser, i) == root) {
                size++;
            }
        }
        return size;
    }

    public void displayDSAsText(int width) {
        for (int i = 0; i < imageAnalyser.length; i++)
            System.out.println(imageAnalyser[i] + ((i + 1) % width == 0 ? "\n" : " "));
    }

    @FXML
    private Image convertBW() {
        modImagePixels(pickedColour, imageView0.getImage());

        PixelReader pixelReader = imageView1.getImage().getPixelReader();

        int width = (int) imageView1.getImage().getWidth();
        int height = (int) imageView1.getImage().getHeight();

        WritableImage writableImage = new WritableImage(width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                Color image = pixelReader.getColor(x, y);

                if (!image.equals(Color.BLACK)) {
                    pixelWriter.setColor(x, y, Color.WHITE);
                } else {
                    pixelWriter.setColor(x, y, Color.BLACK);
                }
            }
        }
        imageView1.setImage(writableImage);
        return writableImage;

    }


    @FXML
    private Image convertToPicked() {

        Image modImage = modImagePixels(pickedColour, image);
        imageView1.setImage(modImage);

        PixelReader pixelReader = imageView1.getImage().getPixelReader();

        int width = (int) imageView1.getImage().getWidth();
        int height = (int) imageView1.getImage().getHeight();

        WritableImage writableImage = new WritableImage(width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                Color image = pixelReader.getColor(x, y);

                if (!image.equals(Color.BLACK)) {
                    pixelWriter.setColor(x, y, pickedColour);
                } else {
                    pixelWriter.setColor(x, y, Color.BLACK);
                }
            }
        }
        imageView1.setImage(writableImage);
        return writableImage;
    }

    @FXML
    private Image convertToRandom() {

        Image modImage = modImagePixels(pickedColour, image);
        imageView1.setImage(modImage);

        PixelReader pixelReader = imageView1.getImage().getPixelReader();

        int width = (int) imageView1.getImage().getWidth();
        int height = (int) imageView1.getImage().getHeight();

        WritableImage writableImage = new WritableImage(width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        Random random = new Random();

        boolean[][] visited = new boolean[width][height];


        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (!visited[x][y]) {

                    Color image = pixelReader.getColor(x, y);

                    if (!image.equals(Color.BLACK)) {
                        Color randomColor = Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
                        fillConnected(pixelReader, pixelWriter, randomColor, visited, x, y, width, height);
                    } else {
                        pixelWriter.setColor(x, y, Color.BLACK);
                    }
                }
            }
        }
        imageView1.setImage(writableImage);
        return writableImage;

    }

    //Find if the pixel has already been visited and changed as well as seeing if a pixel in connected to make it the same random colour
    private void fillConnected(PixelReader pixelReader, PixelWriter pixelWriter, Color randomColor, boolean[][] visited, int x, int y, int width, int height) {
        if (x < 0 || x >= width || y < 0 || y >= height || visited[x][y]) {
            return;
        }
        visited[x][y] = true;
        Color current = pixelReader.getColor(x, y);
        if (!current.equals(Color.BLACK)) {
            pixelWriter.setColor(x, y, randomColor);
            fillConnected(pixelReader, pixelWriter, randomColor, visited, x + 1, y, width, height);
            fillConnected(pixelReader, pixelWriter, randomColor, visited, x, y + 1, width, height);
            fillConnected(pixelReader, pixelWriter, randomColor, visited, x - 1, y + 1, width, height);
        }
    }

    // Initialize noiseReduction to 0 and create an ArrayList called disjointSetValues
    List<Integer> disjointSetValues = new ArrayList<>();

    public void getRectanglePositions(int[] imageArray) {
        // Remove any existing rectangles from the parent Pane
        ((Pane) imageView0.getParent()).getChildren().removeIf(c -> c instanceof Rectangle);
        ((Pane) imageView0.getParent()).getChildren().removeIf(c -> c instanceof Text);

        myDisjointSet disjointSet = new myDisjointSet(imageArray.length);

        // Loop through the imageArray and find the first instance of an element that is not equal to -1
        // and add it to the List disjointSetValues if it is not already present
        for (int i = 0; i < imageArray.length; i++) {
            if (imageArray[i] != -1 && !disjointSetValues.contains(disjointSet.find(imageArray, i))) {
                disjointSetValues.add(disjointSet.find(imageArray, i));
            }
        }

        // Loop through the values in disjointSetValues
        for (int i = 0; i < disjointSetValues.size(); i++) {
            int rootValue = disjointSetValues.get(i);
            // Initialize minX, minY, maxX, and maxY to the dimensions of the image
            int minX = Integer.MAX_VALUE;
            int minY = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            int maxY = Integer.MIN_VALUE;
            int count = 0;

            // Loop through the imageArray and find the minimum and maximum x and y values for each element that
            // is equal to the current rootValue
            for (int j = 0; j < imageArray.length; j++) {
                if (imageArray[j] != -1) {
                    int root = disjointSet.find(imageArray, j);

                    if (rootValue == root) {
                        int rootX = j % (int) imageView0.getImage().getWidth();
                        int rootY = j / (int) imageView0.getImage().getWidth();

                        minX = Math.min(minX, rootX);
                        minY = Math.min(minY, rootY);
                        maxX = Math.max(maxX, rootX);
                        maxY = Math.max(maxY, rootY);
                    }

                }



                // Noise Reduction
                // If the area of the component is greater than the noiseReduction value, call drawRectangles

            }
                if ((((maxX - minX) * (maxY - minY)) > 10)) {
                    drawRectangles(minX, minY, maxX, maxY, i+ 1);

            }
        }
    }

    public void drawRectangles(double minX, double minY, double maxX, double maxY, int numberSeq) {
        // Create a new Rectangle object
        Rectangle rectangle = new Rectangle();

        rectangle.setX(2.05 * minX);
        rectangle.setY(2.05 * minY);

        // Set the dimensions of the rectangle
        rectangle.setWidth(2 * (maxX - minX));
        rectangle.setHeight(2 * (maxY - minY));

        // Set the stroke color to blue with a width of 1
        rectangle.setStroke(Color.BLUE);
        rectangle.setStrokeWidth(1);

        // Set the fill color of the rectangle to transparent
        rectangle.setFill(Color.TRANSPARENT);

        // Add the rectangle to the parent Pane
        Pane pane = (Pane) imageView0.getParent();
        pane.getChildren().add(rectangle);
        //System.out.println("Rectangle: X=" + minX + ", Y=" + minY + ", Width=" + rectangle.getWidth() + ", Height=" + rectangle.getHeight());
        if((((maxX - minX) * (maxY - minY)) )> 10) {
            Text number = new Text(2.05 * minX, (2.05 * maxY) - 2.5, Integer.toString(numberSeq));
            number.setFill(Color.BLACK);
            pane.getChildren().add(number);
        }
    }
}