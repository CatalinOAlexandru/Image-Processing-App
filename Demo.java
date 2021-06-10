// ECS605U - IMAGE PROCESSING - 2019/20
// Cãtãlin Alexandru
// 170467913

import java.io.*;
import java.util.TreeSet;
import java.util.Arrays;
import java.util.Stack;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.*;
import java.util.Random;
import java.nio.file.Files;
import javax.swing.event.*;
import java.util.ArrayList;


public class Demo extends Component implements ActionListener {

    //File Selector, Formats, Reset All
    static JComboBox listoFfiles, optionstoselect, formats;
    static JButton resetAllSettings, undoButton;

    //Lab1-2 Text fields, Buttons
    static JTextField rescaleinput, shiftinput;
    static JButton scaleShiftButton, scaleShiftResetButton, randomValueButton;

    //Lab3 Elements
    static JComboBox secondImageDropdown, roiImageDropdown;
    static ButtonGroup imageArithButton, imageBooleanButton;
    static JButton resetArithButton, resetBoolButton;
    static JRadioButton[] imageArithmeticOptions, imageBooleanOptions;

    //Lab4 Elements
    static JTextField powerLawInput, bitPlaneInput;
    static JButton powerLawButton, bitPlaneButton, resetPowerBitButton;
    static JCheckBox[] pointProcessing;

    //Lab5 Elements
    static JLabel histogramNoPixels;
    static JButton histogramButton, resetHistorgramButton;

    //Lab6 Elements
    static JCheckBox[] filteringOptions;

    //Lab7 Elements
    static JCheckBox[] orderFilteringOptions;
    
    //Lab8 Elements
    static JTextField treshiNput;
    static JButton treshbUtton, resetTresh;
    static JCheckBox autoThres;

    //This stack will be used to undo changes
    Stack<BufferedImage> imageHistory = new Stack<>();

    private BufferedImage bi, biFiltered; // the input image saved as bi;//
    int w, h, w1, h1;

    public Demo() {
        try {
            bi = ImageIO.read(new File("images/default.jpg")); //this display the default image
            w = bi.getWidth(null);
            h = bi.getHeight(null);
            if (bi.getType() != BufferedImage.TYPE_INT_RGB) {
                BufferedImage bi2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics big = bi2.getGraphics();
                big.drawImage(bi, 0, 0, null);
                biFiltered = bi = bi2;}
              imageHistory.push(biFiltered);} 

        catch (IOException event) {  // deal with the situation that the image has problem;/
            JOptionPane.showMessageDialog(null, "ERROR 03: Image could not be read");}}


    // the windows size will be set here
    public Dimension getPreferredSize() {return new Dimension(w+w1, h);}


    // Return available formats listed bellow
    public String[] getFormats() {
        String[] formats = {"-select-","bmp","gif","jpeg","jpg","png"};
        TreeSet<String> formatSet = new TreeSet<String>();
        for (String s : formats) {
            formatSet.add(s.toLowerCase());}
        return formatSet.toArray(new String[0]);}


    //  Repaint will call this function so the image will change.
    public void paint(Graphics g) {g.drawImage(biFiltered, 0, 0, null);}


    // *********************************************** //
    // Takes an image and will converts it to an array //
    // *********************************************** //
    private static int[][][] convertToArray(BufferedImage image){
      int width = image.getWidth();
      int height = image.getHeight();

      int[][][] result = new int[width][height][4];

      for (int y = 0; y < height; y++) {
         for (int x = 0; x < width; x++) {
            int p = image.getRGB(x,y);
            int a = (p>>24)&0xff;
            int r = (p>>16)&0xff;
            int g = (p>>8)&0xff;
            int b = p&0xff;

            result[x][y][0]=a;
            result[x][y][1]=r;
            result[x][y][2]=g;
            result[x][y][3]=b;}}
   
      return result;}


    // ***************************************************** //
    // Takes an image array and re-creates the BufferedImage //
    // ***************************************************** //
    public BufferedImage convertToBimage(int[][][] TmpArray){

        int width = TmpArray.length;
        int height = TmpArray[0].length;

        BufferedImage tmpimg=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                int a = TmpArray[x][y][0];
                int r = TmpArray[x][y][1];
                int g = TmpArray[x][y][2];
                int b = TmpArray[x][y][3];

                int p = (a<<24) | (r<<16) | (g<<8) | b;
                tmpimg.setRGB(x, y, p);}}

        return tmpimg;}


    // *********************************** //
    // Lab 1 & Lab 2                       //
    // Raw files, RescaleInput, ShiftInput //
    // *********************************** //

    // To shiftinput by t and rescaleinput by s without finding the min and the max
    public void ImageRescaleShift(){
      double [] inputs = convertInputScaleShift();
      double t = inputs[0];
      double s = inputs[1];

      // bi is the image on the left
      // biFilteres is the editing image on the right
      int width = bi.getWidth();
      int height = bi.getWidth();

      int[][][] ImageArray1 = convertToArray(biFiltered);
      int[][][] ImageArray2 = convertToArray(biFiltered);

      for(int y=0; y<height; y++){
        for(int x=0; x<width; x++){
          ImageArray2[x][y][1] = (int)(s*(ImageArray1[x][y][1]+t)); //r
          ImageArray2[x][y][2] = (int)(s*(ImageArray1[x][y][2]+t)); //g
          ImageArray2[x][y][3] = (int)(s*(ImageArray1[x][y][3]+t)); //b

          if (ImageArray2[x][y][1]<0) { ImageArray2[x][y][1] = 0; }
          if (ImageArray2[x][y][2]<0) { ImageArray2[x][y][2] = 0; }
          if (ImageArray2[x][y][3]<0) { ImageArray2[x][y][3] = 0; }
          if (ImageArray2[x][y][1]>255) { ImageArray2[x][y][1] = 255; }
          if (ImageArray2[x][y][2]>255) { ImageArray2[x][y][2] = 255; }
          if (ImageArray2[x][y][3]>255) { ImageArray2[x][y][3] = 255; }}}

      biFiltered = convertToBimage(ImageArray2);
      imageHistory.push(biFiltered);}


    public double [] convertInputScaleShift(){
      double [] values = new double[2];
      values[0] = -1;
      values[1] = -1;

      if(shiftinput.getText().equals("")){values[0] = 0;}
      else if(!shiftinput.getText().equals("")){values[0] = Double.parseDouble(shiftinput.getText());}
      if(rescaleinput.getText().equals("")){values[1] = 1;}
      else if(!rescaleinput.getText().equals("")){values[1] = Double.parseDouble(rescaleinput.getText());}
      return values;}


      public BufferedImage randomValueScaleShift(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][][] ImgArray = convertToArray(image); // image to array

        int minimumR = 255;
        int minimumG = 255;
        int minimumB = 255;
        int maxR = 0;
        int maxG = 0;
        int maxB = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                for (int z = 1; z <= 3; z++) {

                    Random random = new Random();
                    int randShiftVal = random.nextInt(50) + 1; // will return a number between 1 and 50.

                    Random random2 = new Random();
                    float randScaleVal = random2.nextFloat() * 2; // will generate a float between 0 and 2

                    ImgArray[x][y][z] = (int) (randScaleVal * (ImgArray[x][y][z] + randShiftVal));

                    if (z == 1) {
                        if (minimumR > ImgArray[x][y][z]) {
                            minimumR = ImgArray[x][y][z];
                        }
                        if (maxR < ImgArray[x][y][z]) {
                            maxR = ImgArray[x][y][z];
                        }

                    } else if (z == 2) {
                        if (minimumG > ImgArray[x][y][z]) {
                            minimumG = ImgArray[x][y][z];
                        }
                        if (maxG < ImgArray[x][y][z]) {
                            maxG = ImgArray[x][y][z];
                        }

                    } else if (z == 3) {
                        if (minimumB > ImgArray[x][y][z]) {
                            minimumB = ImgArray[x][y][z];
                        }
                        if (maxB < ImgArray[x][y][z]) {
                            maxB = ImgArray[x][y][z];
                        }
                    }
                }
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                ImgArray[x][y][1] = 255 * (ImgArray[x][y][1] - minimumR) / (maxR - minimumR);
                ImgArray[x][y][2] = 255 * (ImgArray[x][y][2] - minimumG) / (maxG - minimumG);
                ImgArray[x][y][3] = 255 * (ImgArray[x][y][3] - minimumB) / (maxB - minimumB);}}
      imageHistory.push(convertToBimage(ImgArray));
      return convertToBimage(ImgArray);} // Array to BufferedImage


    // *********************************************** //
    // Lab 3                                           //
    // Arithmetic and Boolean Operations               //
    // Addition, Subtraction, Multiplication, Division //
    // *********************************************** //

    public void ImageArithmetic(int selectedArithmetic){
      
      try{
        BufferedImage imageBuffer = ImageIO.read(new File("images/"+secondImageDropdown.getSelectedItem().toString()));
        int width = imageBuffer.getWidth(null);
        int height = imageBuffer.getHeight(null);

        int [][][] ImgArray1 = convertToArray(biFiltered);
        int [][][] ImgArray2 = convertToArray(imageBuffer);
        int [][][] newImage = convertToArray(bi);


        for(int y=0; y<height; y++){
          for(int x=0; x<width; x++){

            if (selectedArithmetic == 0){ // addition operation
                newImage[x][y][1] = ImgArray1[x][y][1] + ImgArray2[x][y][1];  //r
                newImage[x][y][2] = ImgArray1[x][y][2] + ImgArray2[x][y][2];  //g
                newImage[x][y][3] = ImgArray1[x][y][3] + ImgArray2[x][y][3];} //b
            else if(selectedArithmetic == 1){ // subtraction operation
              newImage[x][y][1] = ImgArray1[x][y][1] - ImgArray2[x][y][1];  //r
              newImage[x][y][2] = ImgArray1[x][y][2] - ImgArray2[x][y][2];  //g
              newImage[x][y][3] = ImgArray1[x][y][3] - ImgArray2[x][y][3];} //b  
            else if(selectedArithmetic == 2){ // multiplication operation
              newImage[x][y][1] = ImgArray1[x][y][1] * ImgArray2[x][y][1];  //r
              newImage[x][y][2] = ImgArray1[x][y][2] * ImgArray2[x][y][2];  //g
              newImage[x][y][3] = ImgArray1[x][y][3] * ImgArray2[x][y][3];} //b
            

            // division operation
            // will set image 1 as the result if the 2nd image value is 0
            else if(selectedArithmetic == 3){ 
              if (ImgArray2[x][y][1]==0){newImage[x][y][1] = ImgArray1[x][y][1];}
              else{newImage[x][y][1] = ImgArray1[x][y][1] / ImgArray2[x][y][1];} //r
              if (ImgArray2[x][y][2]==0){newImage[x][y][2] = ImgArray1[x][y][2];}
              else{newImage[x][y][2] = ImgArray1[x][y][2] / ImgArray2[x][y][2];} //g
              if (ImgArray2[x][y][3]==0){newImage[x][y][3] = ImgArray1[x][y][3];}
              else{newImage[x][y][3] = ImgArray1[x][y][3] / ImgArray2[x][y][3];}} //b

            // makes sure values are not going under or over the black and white values
            if (newImage[x][y][1]<0) { newImage[x][y][1] = 0;}
            if (newImage[x][y][2]<0) { newImage[x][y][2] = 0;}
            if (newImage[x][y][3]<0) { newImage[x][y][3] = 0;}
            if (newImage[x][y][1]>255) { newImage[x][y][1] = 255;}
            if (newImage[x][y][2]>255) { newImage[x][y][2] = 255;}
            if (newImage[x][y][3]>255) { newImage[x][y][3] = 255;}}}

        biFiltered = convertToBimage(newImage);
        imageHistory.push(biFiltered);}
      
      catch (IOException event) { // deal with the situation that th image has problem;/
        JOptionPane.showMessageDialog(null, "ERROR 4: A 2nd image was not selected.");
        imageArithButton.clearSelection();}}


    // ***************** //
    // NOT, AND, OR, XOR //
    // ***************** //
    public void ImageBoolean(int selectedBoolean){
      try{
        // gets all the available images for ROI (Region of Interest) operations
        BufferedImage image2buf = ImageIO.read(new File("images/roi/"+roiImageDropdown.getSelectedItem().toString()));

        int width = image2buf.getWidth(null);
        int height = image2buf.getHeight(null);

        int [][][] ImgArray1 = convertToArray(biFiltered);
        int [][][] ImgArray2 = convertToArray(image2buf);
        int [][][] newImage = convertToArray(bi);

        for(int y=0; y<height; y++){
          for(int x=0; x<width; x++){
            if (selectedBoolean == 0){ // 0 NOT

              int r = ImgArray1[x][y][1]; //r
              int g = ImgArray1[x][y][2]; //g
              int b = ImgArray1[x][y][3]; //b

              newImage[x][y][1] = (~r)&0xFF; //r
              newImage[x][y][2] = (~g)&0xFF; //g
              newImage[x][y][3] = (~b)&0xFF;} //b

            
            else if(selectedBoolean == 1){ // 1 AND 
              if(ImgArray2[x][y][1] == 0){newImage[x][y][1] = 0;}
              else if (ImgArray2[x][y][1] == 255){newImage[x][y][1] = ImgArray1[x][y][1];}
              if(ImgArray2[x][y][2] == 0){newImage[x][y][2] = 0;}
              else if (ImgArray2[x][y][2] == 255){newImage[x][y][2] = ImgArray1[x][y][2];}
              if(ImgArray2[x][y][3] == 0){newImage[x][y][3] = 0;}
              else if (ImgArray2[x][y][3] == 255){newImage[x][y][3] = ImgArray1[x][y][3];}}
            

            else if(selectedBoolean == 2){ // 2 OR
              if(ImgArray2[x][y][1] == 255){newImage[x][y][1] = 255;}
              else if (ImgArray2[x][y][1] == 0){newImage[x][y][1] = ImgArray1[x][y][1];}
              if(ImgArray2[x][y][2] == 255){newImage[x][y][2] = 255;}
              else if (ImgArray2[x][y][2] == 0){newImage[x][y][2] = ImgArray1[x][y][2];}
              if(ImgArray2[x][y][3] == 255){newImage[x][y][3] = 255;}
              else if (ImgArray2[x][y][3] == 0){newImage[x][y][3] = ImgArray1[x][y][3];}}
            

            else if(selectedBoolean == 3){ // 3 XOR - for binary images only
              if(ImgArray1[x][y][1] == 0 && ImgArray2[x][y][1] == 0){newImage[x][y][1] = 0;}
              else if(ImgArray1[x][y][1] == 255 && ImgArray2[x][y][1] == 0){newImage[x][y][1] = 255;}
              else if(ImgArray1[x][y][1] == 0 && ImgArray2[x][y][1] == 255){newImage[x][y][1] = 255;}
              else if(ImgArray1[x][y][1] == 255 && ImgArray2[x][y][1] == 255){newImage[x][y][1] = 0;}

              if(ImgArray1[x][y][2] == 0 && ImgArray2[x][y][2] == 0){newImage[x][y][2] = 0;}
              else if(ImgArray1[x][y][2] == 255 && ImgArray2[x][y][2] == 0){newImage[x][y][2] = 255;}
              else if(ImgArray1[x][y][2] == 0 && ImgArray2[x][y][2] == 255){newImage[x][y][2] = 255;}
              else if(ImgArray1[x][y][2] == 255 && ImgArray2[x][y][2] == 255){newImage[x][y][2] = 0;}

              if(ImgArray1[x][y][3] == 0 && ImgArray2[x][y][3] == 0){newImage[x][y][3] = 0;}
              else if(ImgArray1[x][y][3] == 255 && ImgArray2[x][y][3] == 0){newImage[x][y][3] = 255;}
              else if(ImgArray1[x][y][3] == 0 && ImgArray2[x][y][3] == 255){newImage[x][y][3] = 255;}
              else if(ImgArray1[x][y][3] == 255 && ImgArray2[x][y][3] == 255){newImage[x][y][3] = 0;}}}}

        biFiltered = convertToBimage(newImage);
        imageHistory.push(biFiltered);}
      
      // prints error in case of a problem
      catch (IOException event) {
          JOptionPane.showMessageDialog(null, "ERROR 5: ROI Image not selected");
          imageBooleanButton.clearSelection();}}
    


    // ************************************** //
    // Lab 4                                  //
    // Point Processing and Bit Plane Slicing //
    // ************************************** //
    public void ImagePointProcessing(int selectedOption){
        // selectedOption contains the checkbox selected by the user
        // index 0,1,2 are the checkboxed, 4 and 5 are the buttons (powerlaw and bitplate)

        int width = bi.getWidth();
        int height = bi.getHeight();
        double c = 1;

        int[][][] ImgArray = convertToArray(biFiltered); //image to array
        int[][][] newImage = convertToArray(bi);
        int[] LUT = new int[256];

        // 0 Negative Image Linear Transform
        if(selectedOption == 0){ 
          for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                ImgArray[x][y][1] = 255-ImgArray[x][y][1];  //r
                ImgArray[x][y][2] = 255-ImgArray[x][y][2];  //g
                ImgArray[x][y][3] = 255-ImgArray[x][y][3];} //b
          }biFiltered = convertToBimage(ImgArray);
          imageHistory.push(biFiltered);
          return;}
        

        //Look up table for logarithmic
        else if(selectedOption == 1){
          for(int k=0; k<=255; k++){LUT[k] = (int)(Math.log(1+k)*255/Math.log(256));}}

        //Look up table for power law
        else if(selectedOption == 4){
          if(!powerLawInput.getText().equals("")){
            double p = Double.parseDouble(powerLawInput.getText());
            for(int k=0; k<=255; k++){
              LUT[k] = (int)(Math.pow(255,1-p)*Math.pow(k,p));}}

          else{return;}}
        

        //Look up table for random
        else if(selectedOption == 2){
          for(int k=0; k<=255; k++){
            LUT[k] = (int)(Math.random() * 255) + 1;}}

        else if(selectedOption == 5){
          if(!bitPlaneInput.getText().equals("")){
            
            int k = Integer.parseInt(bitPlaneInput.getText());

            for(int y=0; y<height; y++){
              for(int x=0; x<width; x++){
                int r = ImgArray[x][y][1]; //r
                int g = ImgArray[x][y][2]; //g
                int b = ImgArray[x][y][3]; //b

                r = (r>>k)&1;
                g = (g>>k)&1;
                b = (b>>k)&1;

                // pixel is black if it ends in 0
                if(r==0 || g==0 || b==0){ 
                  newImage[x][y][1]=0;
                  newImage[x][y][2]=0;
                  newImage[x][y][3]=0;}

                // pixel is white if it ends in 1
                else if(r == 1 || g==1 || b==1){ 
                  newImage[x][y][1]=255;
                  newImage[x][y][2]=255;
                  newImage[x][y][3]=255;}}}

            biFiltered = convertToBimage(newImage);
            imageHistory.push(biFiltered);
            return;}

          else{return;}}

        // Logarithmic Powerlaw Random LUT
        for(int y=0; y<height; y++){ 
            for(int x =0; x<width; x++){
              newImage[x][y][1] = LUT[ImgArray[x][y][1]];
              newImage[x][y][2] = LUT[ImgArray[x][y][2]];
              newImage[x][y][3] = LUT[ImgArray[x][y][3]];

              // makes sure values dont go over and under white and black
              if (newImage[x][y][1]<0) { newImage[x][y][1] = 0; }
              if (newImage[x][y][2]<0) { newImage[x][y][2] = 0; }
              if (newImage[x][y][3]<0) { newImage[x][y][3] = 0; }
              if (newImage[x][y][1]>255) { newImage[x][y][1] = 255; }
              if (newImage[x][y][2]>255) { newImage[x][y][2] = 255; }
              if (newImage[x][y][3]>255) { newImage[x][y][3] = 255; }}}

        // Array to BufferedImage
        biFiltered = convertToBimage(newImage); 
        imageHistory.push(biFiltered);
        return;}


    // ********************************** //
    // Lab 5                              //
    // Histogram & Histogram Equalisation //
    // ********************************** //

    public void ImageHistogram(){
      int numberOfPixels = 0;
      int [] HistgramR = new int[256];
      int [] HistgramG = new int[256];
      int [] HistgramB = new int[256];

      double [] HistoNormR = new double[256];
      double [] HistoNormG = new double[256];
      double [] HistoNormB = new double[256];

      double [] HistoCumulativeR = new double[256];
      double [] HistoCumulativeG = new double[256];
      double [] HistoCumulativeB = new double[256];

      int [] HistoEqualisedR = new int[256];
      int [] HistoEqualisedG = new int[256];
      int [] HistoEqualisedB = new int[256];

      int width = biFiltered.getWidth();
      int height = biFiltered.getHeight();

      int[][][] ImgArray = convertToArray(bi);

      for(int k=0; k<=255; k++){ // Initialisation
        HistgramR[k] = 0;
        HistgramG[k] = 0;
        HistgramB[k] = 0;}
        

        for(int y=0; y<height; y++){
          for(int x=0; x<width; x++){
            int r = ImgArray[x][y][1]; //r
            int g = ImgArray[x][y][2]; //g
            int b = ImgArray[x][y][3]; //b

            HistgramR[r]++;
            HistgramG[g]++;
            HistgramB[b]++;

            numberOfPixels++;}}

      histogramNoPixels.setText(Integer.toString(numberOfPixels));
      
      //Histogram Normalisation & Equalisation
      for (int i = 0; i <=255; i++){

        //Normalisation
        HistoNormR[i] = (double) HistgramR[i] / (double) numberOfPixels;
        HistoNormG[i] = (double) HistgramG[i] / (double) numberOfPixels;
        HistoNormB[i] = (double) HistgramB[i] / (double) numberOfPixels;

        //Equalisation
        if (i == 0){
          HistoCumulativeR[i] = HistoNormR[i];
          HistoCumulativeG[i] = HistoNormG[i];
          HistoCumulativeB[i] = HistoNormB[i];}

        else{
          HistoCumulativeR[i] = HistoCumulativeR[i-1] + HistoNormR[i];
          HistoCumulativeG[i] = HistoCumulativeG[i-1] + HistoNormG[i];
          HistoCumulativeB[i] = HistoCumulativeB[i-1] + HistoNormB[i];}

        HistoEqualisedR[i] = (int) (HistoCumulativeR[i] * 255);
        HistoEqualisedG[i] = (int) (HistoCumulativeG[i] * 255);
        HistoEqualisedB[i] = (int) (HistoCumulativeB[i] * 255);}


      // The HistoEqualisedR is used as a Look Up Table to equalise pixel values
      for(int y = 0; y<height; y++){
        for(int x = 0; x<width; x++){
          ImgArray[x][y][1] = HistoEqualisedR[ImgArray[x][y][1]];
          ImgArray[x][y][2] = HistoEqualisedR[ImgArray[x][y][2]];
          ImgArray[x][y][3] = HistoEqualisedR[ImgArray[x][y][3]];}}
        
        biFiltered = convertToBimage(ImgArray);
        imageHistory.push(biFiltered);}


    // *************** //
    // Lab 6           //
    // Image Filtering //
    // *************** //
    public void ImageFiltering(int selectedFilter){
      
      int[][][] newImage = convertToArray(bi);
      int[][][] ImgArray = convertToArray(biFiltered);
      double[][] Mask = new double[][]{{-1,-1,-1},
                                        {-1,-1,-1},
                                        {-1,-1,-1}};

      int width = biFiltered.getWidth();
      int height = biFiltered.getHeight();

      if (selectedFilter == 0){
        double avg = 1.0 / 9.0;
        Mask = new double[][] {{avg,avg,avg},
                              {avg,avg,avg},
                              {avg,avg,avg}};}

      else if(selectedFilter == 1){
        double weighAvg = 1.0 / 16.0;
        Mask = new double[][]{{weighAvg*1.0,weighAvg*2.0,weighAvg*1.0},
                              {weighAvg*2.0,weighAvg*4.0,weighAvg*2.0},
                              {weighAvg*1.0,weighAvg*2.0,weighAvg*1.0}};}

      else if(selectedFilter == 2){
        Mask = new double[][]{{0.0, -1.0, 0.0},
                              {-1.0, 4.0, -1.0},
                              {0.0, -1.0, 0.0}};}

      else if(selectedFilter == 3){
        Mask = new double[][]{{-1.0, -1.0, -1.0},
                              {-1.0, 8.0, -1.0},
                              {-1.0, -1.0, -1.0}};}

      else if(selectedFilter == 4){
        Mask = new double[][]{{0.0, -1.0, 0.0},
                              {-1.0, 5.0, -1.0},
                              {0.0, -1.0, 0.0}};}

      else if(selectedFilter == 5){
        Mask = new double[][]{{-1.0, -1.0, -1.0},
                              {-1.0, 9.0, -1.0},
                              {-1.0, -1.0, -1.0}};}

      else if(selectedFilter == 6){
        Mask = new double[][]{{0.0, 0.0, 0.0},
                              {0.0, 0.0, -1.0},
                              {0.0, 1.0, 0.0}};}

      else if(selectedFilter == 7){
        Mask = new double[][]{{0.0, 0.0, 0.0},
                              {0.0, -1.0, 0.0},
                              {0.0, 1.0, 1.0}};}

      else if(selectedFilter == 8){
        Mask = new double[][]{{-1.0, 0.0, 1.0},
                              {-2.0, 0.0, 2.0},
                              {-1.0, 0.0, 1.0}};}

      else if(selectedFilter == 9){
        Mask = new double[][]{{-1.0, -2.0, -1.0},
                              {0.0, 0.0, 0.0},
                              {1.0, 2.0, 1.0}};}


      for(int y=1; y<height-1; y++){
        for(int x=1; x<width-1; x++){
          double r=0;
          double g=0;
          double b=0;

          for(int s=-1; s<=1; s++){
            for(int t=-1; t<=1; t++){
              r = r + Mask[1-s][1-t]* (double) ImgArray[x+s][y+t][1]; //r
              g = g + Mask[1-s][1-t]* (double) ImgArray[x+s][y+t][2]; //g
              b = b + Mask[1-s][1-t]* (double) ImgArray[x+s][y+t][3];}} //b
            
          newImage[x][y][1] = (int) Math.round(r); //r
          newImage[x][y][2] = (int) Math.round(g); //g
          newImage[x][y][3] = (int) Math.round(b);}} //b
      
      biFiltered = convertToBimage(newImage);
      imageHistory.push(biFiltered);}


    // ************************** //
    // Lab 7                      //
    // Order-statistics Filtering //
    // ************************** //
    public void ImageOrderFilter(int orderFilterOptions){
      int [] newValue = new int[3];

      int [][][] newImg = convertToArray(bi);
      int [][][] saltPepper = convertToArray(biFiltered);
      int [][][] ImageArray = convertToArray(biFiltered);
      Random randomNumber = new Random();
      int bounding = 256;

      int width = biFiltered.getWidth();
      int height = biFiltered.getHeight();

      // Selected salt and pepper
      if(orderFilterOptions == 0){ 
        for(int y=0; y<height; y++){
          for(int x=0; x<width; x++){
            saltPepper[x][y][1] = randomNumber.nextInt(bounding);
            saltPepper[x][y][2] = randomNumber.nextInt(bounding);
            saltPepper[x][y][3] = randomNumber.nextInt(bounding);

            if(saltPepper[x][y][1] == 0){
              newImg[x][y][1] = 0;
              newImg[x][y][2] = 0;
              newImg[x][y][3] = 0;}

            else if(saltPepper[x][y][2] == 0){
              newImg[x][y][1] = 0;
              newImg[x][y][2] = 0;
              newImg[x][y][3] = 0;}

            else if(saltPepper[x][y][3] == 0){
              newImg[x][y][1] = 0;
              newImg[x][y][2] = 0;
              newImg[x][y][3] = 0;}

            else if(saltPepper[x][y][1] == 255){
              newImg[x][y][1] = 255;
              newImg[x][y][2] = 255;
              newImg[x][y][3] = 255;}

            else if(saltPepper[x][y][2] == 255){
              newImg[x][y][1] = 255;
              newImg[x][y][2] = 255;
              newImg[x][y][3] = 255;}

            else if(saltPepper[x][y][3] == 255){
              newImg[x][y][1] = 255;
              newImg[x][y][2] = 255;
              newImg[x][y][3] = 255;}}}}

      // Selected minimum filter
      else if(orderFilterOptions > 0){ 
        int [] windowR = new int [9];
        int [] windowG = new int [9];
        int [] windowB = new int [9];

        for(int y=1; y<height-1; y++){
          for(int x=1; x<width-1; x++){
            int k = 0;
            for(int s=-1; s<=1; s++){
              for(int t=-1; t<=1; t++){
                windowR[k] = ImageArray[x+s][y+t][1]; //r
                windowG[k] = ImageArray[x+s][y+t][2]; //g
                windowB[k] = ImageArray[x+s][y+t][3]; //b
                k++;}}
           
            Arrays.sort(windowR);
            Arrays.sort(windowG);
            Arrays.sort(windowB);

            // Select minimum filter
            if(orderFilterOptions == 1){ 
              newValue[0] = windowR[0];
              newValue[1] = windowG[0];
              newValue[2] = windowB[0];}

            // Select max filder
            else if(orderFilterOptions == 2){ 
              newValue[0] = windowR[8];
              newValue[1] = windowG[8];
              newValue[2] = windowB[8];}

            // Select midpoint filter
            else if(orderFilterOptions == 3){ 
              newValue[0] = Math.round((windowR[0]+windowR[8]) / 2);
              newValue[1] = Math.round((windowG[0]+windowG[8]) / 2);
              newValue[2] = Math.round((windowB[0]+windowB[8]) / 2);}

            // Select median filder
            else if(orderFilterOptions == 4){ 
              newValue[0] = windowR[4];
              newValue[1] = windowG[4];
              newValue[2] = windowB[4];}

            newImg[x][y][1] = newValue[0];    //r
            newImg[x][y][2] = newValue[1];    //g
            newImg[x][y][3] = newValue[2];}}} //b

      biFiltered = convertToBimage(newImg);
      imageHistory.push(biFiltered);}


    // ************ //
    // Lab 8        //
    // Thresholding //
    // ************ //
    public void ImageThresholding(int threshold){
      int [][][] imageThresh = convertToArray(bi);

      // Image is in greyscale
      imageThresh = convertToGreyscale(imageThresh);
      
      int width = bi.getWidth();
      int height = bi.getHeight();

      for(int y=0; y<height; y++){
        for(int x=0; x<width; x++){

          // everything over the treshhold will be set to black
          if(imageThresh[x][y][1] > threshold){
            imageThresh[x][y][1] = 255;
            imageThresh[x][y][2] = 255;
            imageThresh[x][y][3] = 255;}

          // everything under the treshhold will be set to white
          else{
            imageThresh[x][y][1] = 0;
            imageThresh[x][y][2] = 0;
            imageThresh[x][y][3] = 0;}}}

      biFiltered = convertToBimage(imageThresh);
      imageHistory.push(biFiltered);}


    //************************// 
    // Lab 8                  //
    // Automatic Thresholding //
    // As seen in the slides  //
    //************************//
    public BufferedImage autoThresholding(BufferedImage image2Thresh) {

        int[][][] ImgArray = convertToArray(image2Thresh); // Image to array
        int width = image2Thresh.getWidth();
        int height = image2Thresh.getHeight();
        int sum = width * height;

        int thresholdInit = 0;
        int currThresh = 0;
        int nextThresh = 0;
        int difThresh = 0;
        int obj = 0;
        int backgroundImg;
        int sumOfValues = 0; // hold values of the sum of all pixels
        int r, g, b;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                r = ImgArray[x][y][1];
                g = ImgArray[x][y][2];
                b = ImgArray[x][y][3];
                sumOfValues += r;}}

        int bgr = ImgArray[0][0][1] + ImgArray[0][511][1] + ImgArray[511][0][1] + ImgArray[511][511][1];
        backgroundImg = (int) (bgr) / 4;
        obj = (int) (sumOfValues - bgr) / (sum - 4);


        // **************************** //
        // Calculating new Thresholding //
        // **************************** //
        
        thresholdInit = (int) (backgroundImg + obj) / 2;
        while (true) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    r = ImgArray[x][y][1];
                    g = ImgArray[x][y][2];
                    b = ImgArray[x][y][3];
                    sumOfValues += r;}}

            bgr = ImgArray[0][0][1] + ImgArray[0][511][1] + ImgArray[511][0][1] + ImgArray[511][511][1];
            backgroundImg = (int) (bgr) / 4;
            obj = (int) (sumOfValues - bgr) / (sum - 4);


            // **************************** //
            // New Thresholding Calculation //
            // **************************** //
            nextThresh = (int) (backgroundImg + obj) / 2;


            // ********************************** //
            // Intermediate threshold Calculation //
            // ********************************** //

            difThresh = Math.abs(nextThresh - currThresh);
            if (difThresh < thresholdInit) {break;} 
            else {currThresh = nextThresh;}}


        // ****************************** //
        // Setting thresholding on pixels // 
        // ****************************** //
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                r = ImgArray[x][y][1];
                g = ImgArray[x][y][2];
                b = ImgArray[x][y][3];

                ImgArray[x][y][1] = thresholdFunction(r, thresholdInit);
                ImgArray[x][y][2] = thresholdFunction(g, thresholdInit);
                ImgArray[x][y][3] = thresholdFunction(b, thresholdInit);}}

        imageHistory.push(convertToBimage(ImgArray));
        return convertToBimage(ImgArray);} // Array to BufferedImage


    // Helper function for auto treshholding
    public int thresholdFunction(int i, int thresholdVal) {
        if (i >= thresholdVal) {return 255;}
        return 0;}



    // **************************************** //
    // Reset all function and values to default //
    // **************************************** //
    public void resetAllSettings(){
      formats.setSelectedIndex(0);
      rescaleinput.setText("");
      shiftinput.setText("");
      secondImageDropdown.setSelectedIndex(0);
      roiImageDropdown.setSelectedIndex(0);
      imageArithButton.clearSelection();
      imageBooleanButton.clearSelection();
      for(int i = 0; i < pointProcessing.length; i++){pointProcessing[i].setSelected(false);}
      
      powerLawInput.setText("");
      bitPlaneInput.setText("");
      histogramNoPixels.setText("N/A");

      for(int i = 0; i < filteringOptions.length; i++){
        filteringOptions[i].setSelected(false);}
      for(int i = 0; i < orderFilteringOptions.length; i++){
        orderFilteringOptions[i].setSelected(false);}

      treshiNput.setText("");
      autoThres.setSelected(false);}


    // Using the Luminosity method we converts to greyscale
    // (0.3 * R) + (0.59 * G) + (0.11 * B) 
    // we take specific amount of red, green and blue to operate in a right image processing way
    // we do this because we can see more green than red and red is more seen than blue
    // this is because the colour profiles and colour ranges
    public int[][][] convertToGreyscale(int [][][] ImageArray){
      int [][][] newGreyImage = ImageArray;
      for(int y=0; y<ImageArray[1].length; y++){
        for(int x=0; x<ImageArray[0].length; x++){
          double avg = (0.3 * ImageArray[x][y][1]) + (0.59 * ImageArray[x][y][2]) + (0.11 * ImageArray[x][y][3]);
          int avgPixel = (int) Math.round(avg);
          newGreyImage[x][y][1] = avgPixel;
          newGreyImage[x][y][2] = avgPixel;
          newGreyImage[x][y][3] = avgPixel;}}
      return newGreyImage;}
    

    // *********************************************** //
    // ActionPerformed                                 //
    // Everytime a change is made & something happenes //
    // this function will run to update the images     //
    //************************************************ //
    public void actionPerformed(ActionEvent event) { 
      // everytime a ui elememnt is changed the right image is changed with the left one (original)
      // all the new, old changes are done on the new copied imaga

      biFiltered = bi;

      if (event.getSource() == scaleShiftButton){ImageRescaleShift();}
      if (event.getSource() == randomValueButton){biFiltered = randomValueScaleShift(biFiltered);}
      if (event.getSource() == scaleShiftResetButton){
        rescaleinput.setText("");
        shiftinput.setText("");}

      if (event.getSource() == resetArithButton){imageArithButton.clearSelection();}
      if (event.getSource() == resetBoolButton){imageBooleanButton.clearSelection();}
      if (event.getSource() == powerLawButton){ImagePointProcessing(4);}
      if (event.getSource() == bitPlaneButton){ImagePointProcessing(5);}
      if (event.getSource() == resetPowerBitButton){
        powerLawInput.setText("");
        bitPlaneInput.setText("");}
      if (event.getSource() == histogramButton){ImageHistogram();}
      if (event.getSource() == resetHistorgramButton){histogramNoPixels.setText("N/A");}
      if (event.getSource() == resetTresh){
        treshiNput.setText("");
        autoThres.setSelected(false);}
      if (event.getSource() == treshbUtton){
        if(!treshiNput.getText().equals("")){
          ImageThresholding(Integer.parseInt(treshiNput.getText()));}}
      if (event.getSource() == resetAllSettings){resetAllSettings();}

      for (int i = 0; i < imageArithmeticOptions.length; i++){ // Arithmetic checkboxes
          if(imageArithmeticOptions[i].isSelected()){
            ImageArithmetic(i);}}
      for (int i = 0; i < imageBooleanOptions.length; i++){ // FilteringOptions checkboxes
          if(imageBooleanOptions[i].isSelected()){
            ImageBoolean(i);}}
      for ( int i = 0; i < pointProcessing.length; i++){ // Point processing
        if(pointProcessing[i].isSelected()){
          ImagePointProcessing(i);}}
      for (int i = 0; i < filteringOptions.length; i++){ // FilteringOptions checkboxes
        if(filteringOptions[i].isSelected()){
          ImageFiltering(i);}}
      for (int i = 0; i < orderFilteringOptions.length; i++){ // Order Filtering checkboxes
        if(orderFilteringOptions[i].isSelected()){
          ImageOrderFilter(i);}}

      if(autoThres.isSelected()){biFiltered = autoThresholding(biFiltered);}

       if (event.getSource() == formats) {
          String format = (String)formats.getSelectedItem();
          if(!format.equals("-select-")){
            File saveFile = new File("savedimage."+format);
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(saveFile);
            int rval = chooser.showSaveDialog(formats);
            if (rval == JFileChooser.APPROVE_OPTION) {
                saveFile = chooser.getSelectedFile();
                try {ImageIO.write(biFiltered, format, saveFile);} 
                catch (IOException ex) {}}}
          formats.setSelectedIndex(0);}

       else if (event.getSource() == listoFfiles){
          String selectedFile = listoFfiles.getSelectedItem().toString();
          try{
              if (selectedFile.contains(".raw")){loadRawImage(new File("images/" + selectedFile));}
              
              else{
                  bi = ImageIO.read(new File("images/" + selectedFile));
                  w = bi.getWidth(null);
                  h = bi.getHeight(null);
                  if (bi.getType() != BufferedImage.TYPE_INT_RGB) {biFiltered = bi;}}}

          catch(IOException err){}}

      if (event.getSource() == undoButton){
        if(!imageHistory.empty()){biFiltered = imageHistory.pop();}
        else{System.out.println("Image History Empty...");}
      }
      // the only way to update the image. It will redraw the image again with the new updates
      repaint();
      }


    // ************************************************ //
    // GetFiles                                         //
    // When the program starts, we search all the files //
    // which are compatible in the image folder         //
    //************************************************8 //
    public static String[] getFiles(){
        ArrayList<String> files = new ArrayList<String>();
        files.add(">>> SELECT IMAGE <<<");
        File image = new File("images/");

        files.addAll(Arrays.asList(image.list()));
        files.sort(String::compareToIgnoreCase);
        
        for(int i = 0; i < files.size(); i++){
          if(files.get(i).matches("^\\..*$")){
            files.remove(i);}
          if(files.get(i).equals("roi")){
            files.remove(i);}}
      
        String[] simplePaths = new String[files.size()];
        return files.toArray(simplePaths);}


    // *********************************************** //
    // Gets only the ROI files form the image/roi path //
    //************************************************ //
    public static String[] getROIFiles(){

      ArrayList<String> files = new ArrayList<String>();
      files.add(">>> SELECT IMAGE <<<");
      File image = new File("images/roi/");

      files.addAll(Arrays.asList(image.list()));
      files.sort(String::compareToIgnoreCase);

      for(int i = 0; i < files.size(); i++){
        if(files.get(i).matches("^\\..*$")){
          files.remove(i);}
        if(files.get(i).equals("roi")){
          files.remove(i);}}
 
      String[] simplePaths = new String[files.size()];
      return files.toArray(simplePaths);}


  // ************************************************** //
  // If an image is .raw only this function can read it //
  //*************************************************** //
  public void loadRawImage(File file){
        BufferedImage image;
        try{
            byte[] fc = Files.readAllBytes(file.toPath());
            
            // only for 128 square pixel images
            if(fc.length == (128*128)){
                image = new BufferedImage(128,128,BufferedImage.TYPE_BYTE_GRAY);
                image.getRaster().setDataElements(0, 0, 128, 128, fc);
                bi = image;
                w = bi.getWidth(null);
                h = bi.getHeight(null);
                if (bi.getType() != BufferedImage.TYPE_INT_RGB) {
                    biFiltered = bi;}}


            // only for 512 square pixel images
            else if(fc.length == 512*512){
                image = new BufferedImage(512,512,BufferedImage.TYPE_BYTE_GRAY);
                image.getRaster().setDataElements(0, 0, 512, 512, fc);
                bi = image;
                w = bi.getWidth(null);
                h = bi.getHeight(null);
                if (bi.getType() != BufferedImage.TYPE_INT_RGB) {
                    biFiltered = bi;}}}

        catch(IOException event){System.out.println("File error: " + event.toString());}}



    // ********************************** //
    // First method which runs at StartUp //
    //*********************************** //
    public static void main(String s[]) {
        System.out.println("Starting...");
        String [] allFiles = getFiles(); // gets all the available files in the image folder

        JFrame f = new JFrame("ECS605U Image Processing Coursework - Cãtãlin Alexandru - 170467913");
        f.addWindowListener(new WindowAdapter(){ // helps to detect changes
            public void windowClosing(WindowEvent event) {
              System.out.println("Closing...");
              System.exit(0);}});

        Demo defaultImage = new Demo(); // default image which wont change
        Demo editedImage = new Demo(); // all changes apply to this image

        // we set the default image on the center of the application
        // the image which is edited will be on the right
        // left side (West) is occupied by the option menu
        f.add("Center", defaultImage);
        f.add("East", editedImage);

        // geats the available formats to save the image
        formats = new JComboBox(editedImage.getFormats());
        formats.setActionCommand("Formats");
        formats.addActionListener(editedImage);

        // created the list of all the available files for the user to select
        listoFfiles = new JComboBox(allFiles);
        listoFfiles.setActionCommand("Files");
        listoFfiles.addActionListener(editedImage);
        listoFfiles.addActionListener(defaultImage);

        // undo button to return to the previous versions of the image
        undoButton = new JButton("Undo");
        undoButton.addActionListener(editedImage);

        // button to reset all the current changes in the menu
        resetAllSettings = new JButton("Reset All");
        resetAllSettings.setForeground(Color.RED);
        resetAllSettings.addActionListener(editedImage);

        // creates the program panel
        JPanel leftMenuPanel = new JPanel();
        leftMenuPanel.setLayout(new BoxLayout(leftMenuPanel, BoxLayout.PAGE_AXIS));

        // adding the image selector which was created above
        JPanel row0s0 = new JPanel();
        row0s0.setLayout(new FlowLayout());
        row0s0.add(listoFfiles);

        // adding the save as for the available formats
        JPanel row0s1 = new JPanel();
        row0s1.setLayout(new FlowLayout());
        row0s1.add(new JLabel("Save As"));
        row0s1.add(formats);

        // adding the undo and reset button
        JPanel row0s2 = new JPanel();
        row0s2.setLayout(new FlowLayout());
        row0s2.add(undoButton);
        row0s2.add(resetAllSettings);

        // Lab 1-2 in row1
        // Initilializing elements for row1
        rescaleinput = new JTextField("");
        rescaleinput.setPreferredSize(new Dimension(50,30));
        shiftinput = new JTextField("");
        shiftinput.setPreferredSize(new Dimension(50,30));
        scaleShiftButton = new JButton("Rescale/Shift");
        scaleShiftButton.addActionListener(editedImage);
        scaleShiftResetButton = new JButton("Reset");
        scaleShiftResetButton.addActionListener(editedImage);
        randomValueButton = new JButton("Randomize");
        randomValueButton.addActionListener(editedImage);

        // Adding the Elements to row1
        JPanel row1s0 = new JPanel();
        row1s0.setBackground(Color.WHITE);
        row1s0.setLayout(new FlowLayout());
        row1s0.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
        JLabel lab12 = new JLabel("<>LAB 1-2<> ");
        Font lab12Font = lab12.getFont();
        lab12.setFont(lab12Font.deriveFont(lab12Font.getStyle() | Font.BOLD));
        lab12.setForeground(Color.BLUE);
        row1s0.add(lab12);
        row1s0.add(scaleShiftResetButton);

        // sub section 1 of row1
        JPanel row1s1 = new JPanel();
        row1s1.setBackground(Color.WHITE);
        row1s1.setLayout(new FlowLayout());
        row1s1.add(new JLabel("Rescale:"));
        row1s1.add(rescaleinput);
        row1s1.add(new JLabel("Shift:"));
        row1s1.add(shiftinput);

        // sub section 2 of row1
        JPanel row1s2 = new JPanel();
        row1s2.setBackground(Color.WHITE);
        row1s2.setLayout(new FlowLayout());
        row1s2.add(scaleShiftButton);
        row1s2.add(randomValueButton);


        // Lab 3 in row2-4
        // Initializing the Elements for row2,3,4
        secondImageDropdown = new JComboBox(allFiles);
        roiImageDropdown = new JComboBox(getROIFiles());
        // array of radio buttons to be easier to use
        imageArithmeticOptions = new JRadioButton[4];
        // used ButtonGroup to make sure we cant sellect more than 1 radio buttons from the array above
        imageArithButton = new ButtonGroup();

        for(int i = 0; i < imageArithmeticOptions.length; i++){
          imageArithmeticOptions[i] = new JRadioButton();
          imageArithmeticOptions[i].addActionListener(editedImage);
          imageArithButton.add(imageArithmeticOptions[i]);}

        resetArithButton = new JButton("Reset");
        resetArithButton.addActionListener(editedImage);
        imageBooleanOptions = new JRadioButton[4];
        imageBooleanButton = new ButtonGroup();

        for(int i = 0; i < imageBooleanOptions.length; i++){
          imageBooleanOptions[i] = new JRadioButton();
          imageBooleanOptions[i].addActionListener(editedImage);
          imageBooleanButton.add(imageBooleanOptions[i]);}

        resetBoolButton = new JButton("Reset");
        resetBoolButton.addActionListener(editedImage);


        // Adding elements to row 2
        JPanel row2s0 = new JPanel();
        row2s0.setLayout(new FlowLayout());
        row2s0.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
        JLabel lab3 = new JLabel("<>LAB 3<>");
        Font lab3Font = lab3.getFont();
        lab3.setFont(lab3Font.deriveFont(lab3Font.getStyle() | Font.BOLD));
        lab3.setForeground(Color.BLUE);
        row2s0.add(lab3);

        // sub section 1 of row2
        JPanel row2s1 = new JPanel();
        row2s1.setLayout(new FlowLayout());
        row2s1.add(new JLabel("Image Arithmetic Second Image"));

        // sub section 2 of row2
        JPanel row2s2 = new JPanel();
        row2s2.setLayout(new FlowLayout());
        row2s2.add(secondImageDropdown);

        // sub section 3 of row2
        JPanel row2s3 = new JPanel();
        row2s3.setLayout(new FlowLayout());
        row2s3.add(new JLabel("Image Boolean ROI"));

        // sub section 4 of row2
        JPanel row2s4 = new JPanel();
        row2s4.setLayout(new FlowLayout());
        row2s4.add(roiImageDropdown);


        // Adding elements to row 3
        JPanel row3s0 = new JPanel();
        row3s0.setBackground(Color.WHITE);
        row3s0.setLayout(new FlowLayout());
        row3s0.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
        JLabel imgari = new JLabel("Select Image Arithmetics");
        Font font = imgari.getFont();
        imgari.setFont(font.deriveFont(font.getStyle() | Font.BOLD));
        row3s0.add(imgari);
        row3s0.add(resetArithButton);

        // sub section 1 of row3
        JPanel row3s1 = new JPanel();
        row3s1.setBackground(Color.WHITE);
        row3s1.setLayout(new FlowLayout());
        row3s1.add(imageArithmeticOptions[0]);
        row3s1.add(new JLabel("Addition"));
        
        // sub section 2 of row3
        JPanel row3s2 = new JPanel();
        row3s2.setBackground(Color.WHITE);
        row3s2.setLayout(new FlowLayout());
        row3s2.add(imageArithmeticOptions[1]);
        row3s2.add(new JLabel("Subtraction"));
        
        // sub section 3 of row3
        JPanel row3s3 = new JPanel();
        row3s3.setBackground(Color.WHITE);
        row3s3.setLayout(new FlowLayout());
        row3s3.add(imageArithmeticOptions[2]);
        row3s3.add(new JLabel("Multiplication"));
        
        // sub section 4 of row3
        JPanel row3s4 = new JPanel();
        row3s4.setBackground(Color.WHITE);
        row3s4.setLayout(new FlowLayout());
        row3s4.add(imageArithmeticOptions[3]);
        row3s4.add(new JLabel("Division"));
        

        // Adding elements to row4
        JPanel row4 = new JPanel();
        row4.setLayout(new FlowLayout());
        row4.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
        JLabel imgbool = new JLabel("Image Boolean ");
        Font font1 = imgbool.getFont();
        imgbool.setFont(font1.deriveFont(font1.getStyle() | Font.BOLD));
        row4.add(imgbool);
        row4.add(resetBoolButton);

        // sub section 1 of row4
        JPanel row4s1 = new JPanel();
        row4s1.setLayout(new FlowLayout());
        row4s1.add(imageBooleanOptions[0]);
        row4s1.add(new JLabel("NOT"));
        row4s1.add(imageBooleanOptions[1]);
        row4s1.add(new JLabel("AND"));
        

        // sub section 2 of row4
        JPanel row4s2 = new JPanel();
        row4s2.setLayout(new FlowLayout());
        row4s2.add(imageBooleanOptions[2]);
        row4s2.add(new JLabel("OR"));
        row4s2.add(imageBooleanOptions[3]);
        row4s2.add(new JLabel("XOR"));
        

        // Lab4  in row5-6
        // Initializing elements for row5,6
        powerLawInput = new JTextField("");
        powerLawInput.setPreferredSize(new Dimension(50,30));
        bitPlaneInput = new JTextField("");
        bitPlaneInput.setPreferredSize(new Dimension(50,30));
        pointProcessing = new JCheckBox[3];

        for (int i = 0; i < pointProcessing.length; i++){
          pointProcessing[i] = new JCheckBox();
          pointProcessing[i].addActionListener(editedImage);}

        powerLawButton = new JButton("PowerLaw");
        powerLawButton.addActionListener(editedImage);
        bitPlaneButton = new JButton("BitPlane");
        bitPlaneButton.addActionListener(editedImage);
        resetPowerBitButton = new JButton("Reset");
        resetPowerBitButton.addActionListener(editedImage);

        // Adding elements to row5
        JPanel row5s0 = new JPanel();
        row5s0.setBackground(Color.WHITE);
        row5s0.setLayout(new FlowLayout());
        row5s0.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
        JLabel lab4 = new JLabel("<>LAB 4<>");
        Font lab4Font = lab4.getFont();
        lab4.setFont(lab4Font.deriveFont(lab4Font.getStyle() | Font.BOLD));
        lab4.setForeground(Color.BLUE);
        row5s0.add(lab4);

        // sub section 1 of row5
        JPanel row5s1 = new JPanel();
        row5s1.setBackground(Color.WHITE);
        row5s1.setLayout(new FlowLayout());
        row5s1.add(pointProcessing[0]);
        row5s1.add(new JLabel("Negative Linear Transforms"));
        
        // sub section 2 of row5
        JPanel row5s2 = new JPanel();
        row5s2.setBackground(Color.WHITE);
        row5s2.setLayout(new FlowLayout());
        row5s2.add(pointProcessing[1]);
        row5s2.add(new JLabel("Logarithmic"));

        // sub section 3 of row5
        JPanel row5s3 = new JPanel();
        row5s3.setBackground(Color.WHITE);
        row5s3.setLayout(new FlowLayout());
        row5s3.add(pointProcessing[2]);
        row5s3.add(new JLabel("Random Look-Up Table"));
        
        
        // Adding elements to row6
        JPanel row6s0 = new JPanel();
        row6s0.setLayout(new FlowLayout());
        row6s0.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
        row6s0.add(new JLabel("Power-Law (0.01-25)"));
        row6s0.add(powerLawInput);
        
        // sub section 1 of row6
        JPanel row6s1 = new JPanel();
        row6s1.setLayout(new FlowLayout());
        row6s1.add(new JLabel("Bit-Plane slicing (0-7)"));
        row6s1.add(bitPlaneInput);
        
        // sub section 2 of row6
        JPanel row6s2 = new JPanel();
        row6s2.setLayout(new FlowLayout());
        row6s2.add(powerLawButton);
        row6s2.add(bitPlaneButton);
        row6s2.add(resetPowerBitButton);
        

        // Lab5 in row7 
        // Initializing elements for row7
        histogramNoPixels = new JLabel("N/A");
        histogramNoPixels.setPreferredSize(new Dimension(80,30));
        histogramButton = new JButton("Press here");
        histogramButton.addActionListener(editedImage);
        resetHistorgramButton = new JButton("Reset");
        resetHistorgramButton.addActionListener(editedImage);

        // Adding elements to row7
        JPanel row7s0 = new JPanel();
        row7s0.setBackground(Color.WHITE);
        row7s0.setLayout(new FlowLayout());
        row7s0.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
        JLabel lab5 = new JLabel("<>LAB 5<> ");
        Font lab5Font = lab5.getFont();
        lab5.setFont(lab5Font.deriveFont(lab5Font.getStyle() | Font.BOLD));
        lab5.setForeground(Color.BLUE);
        row7s0.add(lab5);
        row7s0.add(resetHistorgramButton);

        // sub section 1 of row7
        JPanel row7s1 = new JPanel();
        row7s1.setBackground(Color.WHITE);
        row7s1.setLayout(new FlowLayout());
        row7s1.add(new JLabel("Histogram: Number of pixels: "));
        row7s1.add(histogramNoPixels);

        // sub section 2 of row7
        JPanel row7s2 = new JPanel();
        row7s2.setBackground(Color.WHITE);
        row7s2.setLayout(new FlowLayout());
        row7s2.add(new JLabel("Histogram equalisation:"));
        row7s2.add(histogramButton);
        
        
        // Lab 6 in row8-9
        // Initialising elements for row8,9
        filteringOptions = new JCheckBox[10];
        for(int i = 0; i < filteringOptions.length; i++){
          filteringOptions[i] = new JCheckBox();
          filteringOptions[i].addActionListener(editedImage);}

        // Adding elements to row8
        JPanel row8s0 = new JPanel();
        row8s0.setLayout(new FlowLayout());
        row8s0.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
        JLabel lab6 = new JLabel("<>LAB 6<>");
        Font lab6Font = lab6.getFont();
        lab6.setFont(lab6Font.deriveFont(lab6Font.getStyle() | Font.BOLD));
        lab6.setForeground(Color.BLUE);
        row8s0.add(lab6);

        // sub section 1 of row8
        JPanel row8s1 = new JPanel();
        row8s1.setLayout(new FlowLayout());
        row8s1.add(filteringOptions[0]);
        row8s1.add(new JLabel("Averaging"));
        row8s1.add(filteringOptions[1]);
        row8s1.add(new JLabel("Weighted Averaging"));

        // sub section 2 of row8
        JPanel row8s2 = new JPanel();
        row8s2.setLayout(new FlowLayout());
        row8s2.add(filteringOptions[2]);
        row8s2.add(new JLabel("4-neighbour Laplacian"));
        
        // sub section 3 of row8
        JPanel row8s3 = new JPanel();
        row8s3.setLayout(new FlowLayout());
        row8s3.add(filteringOptions[3]);
        row8s3.add(new JLabel("8-neighbour Laplacian"));

        // sub section 4 of row8
        JPanel row8s4 = new JPanel();
        row8s4.setLayout(new FlowLayout());
        row8s4.add(filteringOptions[4]);
        row8s4.add(new JLabel("4-neighbour Laplacian Enhancement"));
        
        // sub section 5 of row8
        JPanel row8s5 = new JPanel();
        row8s5.setLayout(new FlowLayout());
        row8s5.add(filteringOptions[5]);
        row8s5.add(new JLabel("8-neighbour Laplacian Enhancement"));
        
        // sub section 6 of row8
        JPanel row8s6 = new JPanel();
        row8s6.setLayout(new FlowLayout());
        row8s6.add(filteringOptions[6]);
        row8s6.add(new JLabel("Roberts1"));
        row8s6.add(filteringOptions[7]);
        row8s6.add(new JLabel("Roberts2"));

        // sub section 7 of row8
        JPanel row8s7 = new JPanel();
        row8s7.setLayout(new FlowLayout());
        row8s7.add(filteringOptions[8]);
        row8s7.add(new JLabel("SobelX"));
        row8s7.add(filteringOptions[9]);
        row8s7.add(new JLabel("SobelY"));


        // Lab 7 in row9
        // Initializing elements for row9
        orderFilteringOptions = new JCheckBox[5];
        for(int i = 0; i < orderFilteringOptions.length; i++){
          orderFilteringOptions[i] = new JCheckBox();
          orderFilteringOptions[i].addActionListener(editedImage);}

        // Adding elements to row9
        JPanel row9s0 = new JPanel();
        row9s0.setBackground(Color.WHITE);
        row9s0.setLayout(new FlowLayout());
        row9s0.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
        JLabel lab7 = new JLabel("<>LAB 7<>");
        Font lab7Font = lab7.getFont();
        lab7.setFont(lab7Font.deriveFont(lab7Font.getStyle() | Font.BOLD));
        lab7.setForeground(Color.BLUE);
        row9s0.add(lab7);

        // sub section 1 of row9
        JPanel row9s1 = new JPanel();
        row9s1.setBackground(Color.WHITE);
        row9s1.setLayout(new FlowLayout());
        row9s1.add(orderFilteringOptions[0]);
        row9s1.add(new JLabel("Salt and Pepper Noise"));
        
        // sub section 2 of row10
        JPanel row9s2 = new JPanel();
        row9s2.setBackground(Color.WHITE);
        row9s2.setLayout(new FlowLayout());
        row9s2.add(orderFilteringOptions[1]);
        row9s2.add(new JLabel("Min Filtering"));
        row9s2.add(orderFilteringOptions[2]);
        row9s2.add(new JLabel("Max Filtering"));

        // sub section 3 of row9
        JPanel row9s3 = new JPanel();
        row9s3.setBackground(Color.WHITE);
        row9s3.setLayout(new FlowLayout());
        row9s3.add(orderFilteringOptions[3]);
        row9s3.add(new JLabel("Midpoint Filtering"));
        row9s3.add(orderFilteringOptions[4]);
        row9s3.add(new JLabel("Median Filtering"));
        

        // Lab 8 in row10
        // Initializing elements for row10
        treshiNput = new JTextField("");
        treshiNput.setPreferredSize(new Dimension(50,30));
        treshbUtton = new JButton("Apply");
        treshbUtton.addActionListener(editedImage);
        autoThres = new JCheckBox();
        autoThres.addActionListener(editedImage);
        resetTresh = new JButton("Reset");
        resetTresh.addActionListener(editedImage);

        // Adding elements to row10
        JPanel row10s0 = new JPanel();
        row10s0.setLayout(new FlowLayout());
        row10s0.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
        JLabel lab8 = new JLabel("<>LAB 8<>");
        Font lab8Font = lab8.getFont();
        lab8.setFont(lab8Font.deriveFont(lab8Font.getStyle() | Font.BOLD));
        lab8.setForeground(Color.BLUE);
        row10s0.add(lab8);
        row10s0.add(resetTresh);

        // sub section 1 of row10
        JPanel row10s1 = new JPanel();
        row10s1.setLayout(new FlowLayout());
        row10s1.add(new JLabel("Simple Thresholding, Enter value (0-255)"));

        // sub section 1 of row10
        JPanel row10s2 = new JPanel();
        row10s2.setLayout(new FlowLayout());
        row10s2.add(treshiNput);
        row10s2.add(treshbUtton);

        // sub section 1 of row10
        JPanel row10s3 = new JPanel();
        row10s3.setLayout(new FlowLayout());
        row10s3.add(autoThres);
        row10s3.add(new JLabel("Auto thresholding:"));
        

        // **************************************************************************** //
        // Adding all rows with the elements to the menu panel on the left of the sceen //
        //***************************************************************************** //
        leftMenuPanel.add(row0s0);
        leftMenuPanel.add(row0s1);
        leftMenuPanel.add(row0s2);

        leftMenuPanel.add(row1s0);
        leftMenuPanel.add(row1s1);
        leftMenuPanel.add(row1s2);

        leftMenuPanel.add(row2s0);
        leftMenuPanel.add(row2s1);
        leftMenuPanel.add(row2s2);
        leftMenuPanel.add(row2s3);
        leftMenuPanel.add(row2s4);

        leftMenuPanel.add(row3s0);
        leftMenuPanel.add(row3s1);
        leftMenuPanel.add(row3s2);
        leftMenuPanel.add(row3s3);
        leftMenuPanel.add(row3s4);

        leftMenuPanel.add(row4);
        leftMenuPanel.add(row4s1);
        leftMenuPanel.add(row4s2);

        leftMenuPanel.add(row5s0);
        leftMenuPanel.add(row5s1);
        leftMenuPanel.add(row5s2);
        leftMenuPanel.add(row5s3);

        leftMenuPanel.add(row6s0);
        leftMenuPanel.add(row6s1);
        leftMenuPanel.add(row6s2);

        leftMenuPanel.add(row7s0);
        leftMenuPanel.add(row7s1);
        leftMenuPanel.add(row7s2);

        leftMenuPanel.add(row8s0);
        leftMenuPanel.add(row8s1);
        leftMenuPanel.add(row8s2);
        leftMenuPanel.add(row8s3);
        leftMenuPanel.add(row8s4);
        leftMenuPanel.add(row8s5);
        leftMenuPanel.add(row8s6);
        leftMenuPanel.add(row8s7);

        leftMenuPanel.add(row9s0);
        leftMenuPanel.add(row9s1);
        leftMenuPanel.add(row9s2);
        leftMenuPanel.add(row9s3);

        leftMenuPanel.add(row10s0);
        leftMenuPanel.add(row10s1);
        leftMenuPanel.add(row10s2);
        leftMenuPanel.add(row10s3);

        // Creates a scroll pane for better visuals and to be able to scroll down
        JScrollPane pane = new JScrollPane(leftMenuPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pane.setPreferredSize(new Dimension(320,500));

        // Adds the last elements to the window and makes it visible after all changes are done
        f.add("West", pane);
        f.pack();
        f.setVisible(true);
    }
}
