/*Program:<Give your program a meaningfully descriptive name>
 Author: Eric Thoman and Nick Mullikan
 Date: 1/3/2016
 Notes: Should the grader (teacher, peer, etc.) look for anything specific?
Our group was especially proud of how the program seamlessly switched between major notes, minor notes, harmonic tones, and the otherwise standard notes of the pentatonic scale. In addition, we thought that the picture was a nice touch to the visualizer.
 Is any part of your program “broken”?
No part of the program is “broken.” However, there is sometimes lag in the visualizer because of the way the program switches between outputting audio and refreshing the image during runtime.
 Did you add special features?
The visualizer has a nice background image which we thought was added a cool twist to the project.
 Did you adapt code from somewhere else?
Yes, we used Princeton’s stdaudio and stddraw files to create this project. Other than that, this project was only inspired by Princeton’s PlayThatTune and PlayThatTuneDeluxe files.
 */

/**
 * Description of Audio
 * @author Nicholas Mullikin
 * @author Eric Thoman
 *
 *
 */

import java.io.PrintWriter;
import java.io.IOException;
import java.util.*;
public class Main {
    public static void main(String[] args) {
        StdDraw.setCanvasSize(1380, 820);
        StdDraw.setXscale(-1.6829, 1.6829);
        StdDraw.setYscale(-1.0, 1.0);
        // c - 523.25 440*2^#12
        // d- 587.33
        // e - 659.26
        // g - 783.99
        // a - 880.00



        try{
            songcreator();
        }
        catch(IOException ex){
            System.out.println("Error with songcreator");
        }



    }
    public static void songcreator() throws IOException{
        /**
        *This method creates the song
        * @author Nicholas Mullikin
        * @author Eric Thoman
        * @param none
        * @return none
        * @throws IOException This is thrown when a file cannot be created by Printwriter
        */



        PrintWriter printWriter = new PrintWriter ("Music.txt");

        double[] totalsong = new double[44100];
        double[] theDurationsTot = new double[44100];
        double[] notearray = newrandomarray(250);
        double[] durationarray = new double[250];
        int loopContinue = 0;

        ArrayList<Double> durCount = new ArrayList<>();
        double durationnum;
        for(int j = 0; j <8; j++) {//for loop inside a for loop in order to have an actual verse that repeats
            double[] durations = {0.5, 1, 1.5, 2};
            durationnum = durations[(int) (Math.random() * 4)];




            double[] temparray1 = addArrays(note(notearray[0] , durationnum), (majorchord(notearray[0], durationnum)), .5, .5);
            double[] theDurations1 = dnote(durationnum);
            printWriter.println(notearray[0] + "  " + durationnum);

            temparray1 = AddArray(temparray1, addArrays(note(notearray[0] - 1, durationnum), (majorchord(notearray[0] - 1, durationnum)), .5, .5));
            theDurations1 = AddArray(theDurations1, dnote(durationnum));
            printWriter.println(notearray[0] - 1 + "  " + durationnum);

            temparray1 = AddArray(temparray1, addArrays(note(notearray[0] - 2, durationnum), (majorchord(notearray[0] - 2, durationnum)), .5, .5));
            theDurations1 = AddArray(theDurations1, dnote(durationnum));
            printWriter.println(notearray[0] - 2 + "  " + durationnum);

            temparray1 = AddArray(temparray1, addArrays(note(notearray[0] + 4, durationnum), (majorchord(notearray[0] + 4, durationnum)), .5, .5));
            theDurations1 = AddArray(theDurations1, dnote(durationnum));
            printWriter.println(notearray[0] + 4 + "  " + durationnum);

            temparray1 = AddArray(temparray1, addArrays(note(notearray[0] , durationnum), (majorchord(notearray[0] , durationnum)), .5, .5));
            theDurations1 = AddArray(theDurations1, dnote(durationnum));
            printWriter.println(notearray[0] + "  " + durationnum);

            totalsong = AddArray(temparray1, totalsong);
            theDurationsTot = AddArray(theDurations1, theDurationsTot);

            for (int i = 1; i < 25; i++) {
                double[] durations1 = {0.5, 1, 2};
                durationnum = durations1[(int) (Math.random() * 3)];
                for (int f = 0; f < (durationnum * 44100); f++) {

                    durCount.add(durationnum);
                }
                if (loopContinue > 0) {
                    loopContinue--;
                    continue;
                }

                double random = Math.random();
                if (random < 0.25) {

                    double[] temparray = note(notearray[i+j*4], durationnum);
                    double[] theDurations = dnote(durationnum);
                    printWriter.println(notearray[i+j*4] + "  " + durationnum);


                    temparray = AddArray(temparray, note(notearray[i+j*4] - 1, durationnum));
                    theDurations = AddArray(theDurations, dnote(durationnum));
                    printWriter.println(notearray[i+j*4] - 1 + "  " + durationnum);

                    temparray = AddArray(temparray, note(notearray[i+j*4] - 2, durationnum));
                    theDurations = AddArray(theDurations, dnote(durationnum));
                    printWriter.println(notearray[i+j*4] - 2 + "  " + durationnum);

                    totalsong = AddArray(temparray, totalsong);
                    theDurationsTot = AddArray(theDurations, theDurationsTot);


                    loopContinue = 3;
                } else if (random < .5) {

                    double[] temparray = note(notearray[i+j*4], durationnum);
                    double[] theDurations = dnote(durationnum);
                    temparray = addArrays(temparray, majorchord(notearray[i+j*4], durationnum), .5, .5);
                    totalsong = AddArray(temparray, totalsong);
                    printWriter.println(notearray[i+j*4] + "  " + durationnum);

                    theDurationsTot = AddArray(theDurations, theDurationsTot);
                } else if (random < 0.75) {

                    double[] temparray = note(notearray[i+j*4], durationnum);
                    double[] theDurations = dnote(durationnum);
                    temparray = addArrays(temparray, minorchord(notearray[i+j*4], durationnum), .5, .5);
                    totalsong = AddArray(temparray, totalsong);
                    printWriter.println(notearray[i+j*4] + "  " + durationnum);

                    theDurationsTot = AddArray(theDurations, theDurationsTot);
                } else {
                    double[] temparray = note(notearray[i+j*4], durationnum);
                    double[] theDurations = dnote(durationnum);
                    temparray = addArrays(temparray, harmonic(notearray[i+j*4], durationnum), .5, .5);
                    totalsong = AddArray(temparray, totalsong);
                    printWriter.println(notearray[i+j*4] + "  " + durationnum);

                    theDurationsTot = AddArray(theDurations, theDurationsTot);
                }

            }

        }

        StdAudio.save("song.wav", totalsong);
        System.out.println("here");
        System.out.println(totalsong.length);
        System.out.println(theDurationsTot.length);
        printWriter.close ();
        StdAudio.play(totalsong, theDurationsTot, notearray, .1);
    }
    public static double[] note(double pitch, double duration) {
        /**
		 * Use for a given pitch and duration
		 *
		 * @param pitch the double of the pitch
		 *
		 * @param duration double of the seconds
		 *
		 * @return note a double array to be played
		 */
        double hz = 440 * Math.pow(2, pitch / 12.0);
        int N = (int) (StdAudio.SAMPLE_RATE * duration);
        double[] a = new double[N + 1];
        for (int i = 0; i <= N; i++) {
            a[i] = Math.sin(2 * Math.PI * i * hz / StdAudio.SAMPLE_RATE);
            //System.out.print((int) (a[i] * 360) + " ");
            if(i > 44099){
                //System.out.println(a[i]);
            }
        }
        //System.out.println("");
        return a;
    }

    public static double[] dnote(double duration) {
         /**
		 * Use to convert a duration into a blank array at the sample rate.
		 *
		 * @param duration the double of the duration
		 *
		 * @return dnote a double array to be used
		 */
        int N = (int) (StdAudio.SAMPLE_RATE * duration);
        double[] a = new double[N + 1];
        for (int i = 0; i <= N; i++) {
            a[i] = duration;

        }

        return a;
    }
    public static double[] AddArray(double[] array1, double[] array2) {
         /**
		 * Concatenates two arrays
		 *
		 * @param array1 the first array
		 *
		 * @param array2 the array added to the first
		 *
		 * @return array1and2 the final concatenated array
		 */

        double[] array1and2 = new double[array1.length + array2.length];
        System.arraycopy(array1, 0, array1and2, 0, array1.length);
        System.arraycopy(array2, 0, array1and2, array1.length, array2.length);

        return array1and2;
    }
    public static double[] addArrays(double[] array1, double[] array2, double a, double b) {
         /**
		 * Adds two equivalent weighted arrays
		 *
		 * @param array1 the first array
		 * @param array2 the second array
		 * @param a the weight of the first array
		 * @param b the weight of the second array
		 * @return array the final added array
		 */
        double[] array = new double[array1.length];
        for (int i = 0; i < array1.length; i++){
            array[i] = array1[i]*a+array2[i]*b;
        }
        return array;
    }

    public static double[] newrandomarray(int integer) {
          /**
		 * Makes an array filled with notes based on gaussian distribution
		 *
		 * @param integer the length of the array
		 *
		 * @return array the randomized array
		 */

        double[] array = new double[integer];
        int freC = 2;
        int freD = 4;
        int freE = 5;
        int freG = 7;
        int freA = 9;
        int[] notes = {freC, freD, freE, freG, freA};
        for (int j = 0; j < integer; j++) {
            Random r = new Random();
            array[j] = (notes[(int) (2.5 - (r.nextGaussian() / 2))]);
        }
        return array;
    }
    public static double[] majorchord(double root, double duration) {
          /**
		 * Creates the major chord which must be added to the root array.
		 *
		 * @param root the root note
		 *
		 * @param duration the length in seconds of the array
		 *
		 * @return array the  array
		 */
        double[] array = addArrays(note(root+4.00015,duration), note(root+7.00015, duration), .5, .5);
        //array = note(17.5647552, 1);
        return array;
    }
    public static double[] minorchord(double root, double duration) {
         /**
		 * Creates the minor chord which must be added to the root array.
		 *
		 * @param root the root note
		 *
		 * @param duration the length in seconds of the array
		 *
		 * @return array the  array
		 */
        double[] array =  addArrays(note(root+3,duration), note(root+7, duration), .5, .5);
        return array;
    }
    public static void changeVolume(double[] array, double modifier) {
        /**
		 * Changes the amplitude of an array
		 *
		 * @param array the array to be modified
		 *
		 * @param modifier the amount to change the array
		 *
		 * @return none
		 */

        for (int i = 0; i < array.length; i++) {
            array[i] = array[i] * modifier;
        }
    }
    public static double[] harmonic(double root, double duration)
    {
        /**
		 * Creates the harmonic which must be added to the root array.
		 *
		 * @param root the root note
		 *
		 * @param duration the length in seconds of the array
		 *
		 * @return array the array
		 */
        double[] array =  addArrays(note(root+12,duration), note(root-12, duration), .5, .5);
        return array;
    }
    public static double[] echo(double array[], double timeInBetween, int amountOfRepeats)
    {
        /**
		 * Creates an array that has a repeating array and rest
		 *
		 * @param array the array to be repeated
		 *
		 * @param timeInBetween seconds between each repeat of the array
		 *
		 * @return amountOfRepeats the amount of times to repeat the array and blank spots
		 */
        double[] array1 = {};
        double[] arrayInBetween = new double[(int)(timeInBetween)*44100];
        for(int i = 0; i < amountOfRepeats; i++)
        {
            array1= AddArray(array1, AddArray(array,arrayInBetween));

        }
        return array1;
    }
}
