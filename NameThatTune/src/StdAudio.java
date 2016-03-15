
/******************************************************************************
 *  Compilation:  javac StdAudio.java
 *  Execution:    java StdAudio
 *  Dependencies: none
 *
 *  Simple library for reading, writing, and manipulating .wav files.
 *
 *
 *  Limitations
 *  -----------
 *    - Does not seem to work properly when reading .wav files from a .jar file.
 *    - Assumes the audio is monaural, with sampling rate of 44,100.
 *
 ******************************************************************************/


import java.applet.AudioClip;

import java.util.*;
import java.applet.Applet;
import java.io.File;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
/**
 *  <i>Standard audio</i>. This class provides a basic capability for
 *  creating, reading, and saving audio. 
 *  <p>
 *  The audio format uses a sampling rate of 44,100 (CD quality audio), 16-bit, monaural.
 *
 *  <p>
 *  For additional documentation, see <a href="http://introcs.cs.princeton.edu/15inout">Section 1.5</a> of
 *  <i>Introduction to Programming in Java: An Interdisciplinary Approach</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public final class StdAudio {
    /**
     *  The sample rate - 44,100 Hz for CD quality audio.
     */
    public static final int SAMPLE_RATE = 44100;
    public static int counter = 0;
    private static final int BYTES_PER_SAMPLE = 2;                // 16-bit audio
    private static final int BITS_PER_SAMPLE = 16;                // 16-bit audio
    private static final double MAX_16_BIT = Short.MAX_VALUE;     // 32,767
    private static final int SAMPLE_BUFFER_SIZE = 4096;
    public static long millis = System.currentTimeMillis();

    private static SourceDataLine line;   // to play the sound
    private static byte[] buffer;         // our internal buffer
    private static int bufferSize = 0;    // number of samples currently in internal buffer
    public static double radius = .25;
    private StdAudio() {
        // can not instantiate
    }

    // static initializer
    static {
        init();
    }
    // open up an audio stream
    private static void init() {
        try {
            // 44,100 samples per second, 16-bit audio, mono, signed PCM, little Endian
            AudioFormat format = new AudioFormat((float) SAMPLE_RATE, BITS_PER_SAMPLE, 1, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format, SAMPLE_BUFFER_SIZE * BYTES_PER_SAMPLE);

            // the internal buffer is a fraction of the actual buffer size, this choice is arbitrary
            // it gets divided because we can't expect the buffered data to line up exactly with when
            // the sound card decides to push out its samples.
            buffer = new byte[SAMPLE_BUFFER_SIZE * BYTES_PER_SAMPLE/3];
        }
        catch (LineUnavailableException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        // no sound gets made before this call
        line.start();
    }
    /**
     * Closes standard audio.
     */
    public static void close() {
        line.drain();
        line.stop();
    }

    /**
     * Writes one sample (between -1.0 and +1.0) to standard audio.
     * If the sample is outside the range, it will be clipped.
     *
     * @param  sample the sample to play
     * @throws IllegalArgumentException if the sample is <tt>Double.NaN</tt>
     */
    public static void play(double sample, double[] duration, double[] hz, double amplitude) {


        // clip if outside [-1, +1]
        if (Double.isNaN(sample)) throw new IllegalArgumentException("sample is NaN");
        if (sample < -1.0) sample = -1.0;
        if (sample > +1.0) sample = +1.0;

        // convert to bytes
        short s = (short) (MAX_16_BIT * sample);
        buffer[bufferSize++] = (byte) s;
        buffer[bufferSize++] = (byte) (s >> 8);   // little Endian
        //System.out.println(s);
        // send to sound card if buffer is full        
        if (bufferSize >= buffer.length) {

            line.write(buffer, 0, buffer.length);
            bufferSize = 0;

            StdAudio.visualizer(duration, hz, amplitude);
            //if(!millis-System.currentTimeMillis()>duration)
            //counter++;
        }





    }
    /**
     * Writes the array of samples (between -1.0 and +1.0) to standard audio.
     * If a sample is outside the range, it will be clipped.
     *
     * @param  samples the array of samples to play
     * @throws IllegalArgumentException if any sample is <tt>Double.NaN</tt>
     */
    public static void play(double[] samples, double[] duration, double[] hz, double amplitude) {

        if (samples == null) throw new NullPointerException("argument to play() is null");
        System.out.println(samples.length);
        for (int i = 0; i < samples.length; i++) {
            if(i%(duration[i]*SAMPLE_RATE)==0&&i!=0){
                StdAudio.counter++; //this



            }
            play(samples[i], duration, hz, amplitude);

        }
    }


    /**
     * Reads audio samples from a file (in .wav or .au format) and returns
     * them as a double array with values between -1.0 and +1.0.
     *
     * @param  filename the name of the audio file
     * @return the array of samples
     */
    public static double[] read(String filename) {
        byte[] data = readByte(filename);
        int N = data.length;
        double[] d = new double[N/2];
        for (int i = 0; i < N/2; i++) {
            d[i] = ((short) (((data[2*i+1] & 0xFF) << 8) + (data[2*i] & 0xFF))) / ((double) MAX_16_BIT);
        }
        return d;
    }
    /**
     * Plays an audio file (in .wav, .mid, or .au format) in a background thread.
     *
     * @param filename the name of the audio file
     */
    public static void play(String filename) {
        URL url = null;
        try {
            File file = new File(filename);
            if (file.canRead()) url = file.toURI().toURL();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        // URL url = StdAudio.class.getResource(filename);
        if (url == null) throw new RuntimeException("audio " + filename + " not found");
        AudioClip clip = Applet.newAudioClip(url);
        clip.play();
    }
    /**
     * Plays an audio file (in .wav, .mid, or .au format) in a loop in a background thread.
     *
     * @param  filename the name of the audio file
     */
    public static void loop(String filename) {
        URL url = null;
        try {
            File file = new File(filename);
            if (file.canRead()) url = file.toURI().toURL();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        // URL url = StdAudio.class.getResource(filename);
        if (url == null) throw new RuntimeException("audio " + filename + " not found");
        AudioClip clip = Applet.newAudioClip(url);
        clip.loop();
    }
    // return data as a byte array
    private static byte[] readByte(String filename) {
        byte[] data = null;
        AudioInputStream ais = null;
        try {
            // try to read from file
            File file = new File(filename);
            if (file.exists()) {
                ais = AudioSystem.getAudioInputStream(file);
                data = new byte[ais.available()];
                ais.read(data);
            }
            // try to read from URL
            else {
                URL url = StdAudio.class.getResource(filename);
                ais = AudioSystem.getAudioInputStream(url);
                data = new byte[ais.available()];
                ais.read(data);
            }
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Could not read " + filename);
        }
        catch (UnsupportedAudioFileException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(filename + " in unsupported audio format");
        }
        return data;
    }
    /**
     * Saves the double array as an audio file (using .wav or .au format).
     *
     * @param  filename the name of the audio file
     * @param  samples the array of samples
     */
    public static void save(String filename, double[] samples) {
        // assumes 44,100 samples per second
        // use 16-bit audio, mono, signed PCM, little Endian
        AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
        byte[] data = new byte[2 * samples.length];
        for (int i = 0; i < samples.length; i++) {
            int temp = (short) (samples[i] * MAX_16_BIT);
            data[2*i + 0] = (byte) temp;
            data[2*i + 1] = (byte) (temp >> 8);
        }
        // now save the file
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            AudioInputStream ais = new AudioInputStream(bais, format, samples.length);
            if (filename.endsWith(".wav") || filename.endsWith(".WAV")) {
                AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(filename));
            }
            else if (filename.endsWith(".au") || filename.endsWith(".AU")) {
                AudioSystem.write(ais, AudioFileFormat.Type.AU, new File(filename));
            }
            else {
                throw new RuntimeException("File format not supported: " + filename);
            }
        }
        catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }
    }

    public static void visualizer(double[] duration, double[] pitch, double amplitude){
        /*
		 * This is the visualizer
		 *
		 * @param duration an array of the durations of each note
		 *
		 * @param pitch an array of the notes
		 *
		 * @param amplitude the degree to which the visualizer works
		 *
		 * @return none
		 */
        double vx = .4207, vy=.125;     // position
        double rx = locationX(0, vx, amplitude);
        double ry=locationY(0, vy, amplitude);


        if(counter<pitch.length){
            if(pitch[counter]<=0){
                StdDraw.setPenColor(StdDraw.BLACK);
                //radius(rx, ry, amplitude);
            }
            else if (pitch[counter]<=1){
                StdDraw.setPenColor(StdDraw.DARK_GRAY);
                //radius(rx, ry, amplitude);
            }
            else if (pitch[counter]<=2){
                StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
                //radius(rx, ry, amplitude);
            }
            else if (pitch[counter]<=3){
                StdDraw.setPenColor(StdDraw.MAGENTA);
                //radius(rx, ry, amplitude);
            }
            else if (pitch[counter]<=4){
                StdDraw.setPenColor(StdDraw.ORANGE);
                //radius(rx, ry, amplitude);
            }
            else if (pitch[counter]<=5){
                StdDraw.setPenColor(StdDraw.PINK);
                //radius(rx, ry, amplitude);
            }
            else if (pitch[counter]<=6){
                StdDraw.setPenColor(StdDraw.RED);
                //radius(rx, ry, amplitude);
            }
            else if (pitch[counter]<=7){
                StdDraw.setPenColor(StdDraw.GREEN);
                //radius(rx, ry, amplitude);
            }
            else if (pitch[counter]<=8){
                StdDraw.setPenColor(StdDraw.YELLOW);
                //radius(rx, ry, amplitude);
            }
            else if (pitch[counter]<=9){
                StdDraw.setPenColor(StdDraw.BLUE);
                //radius(rx, ry, amplitude);
            }
            else if (pitch[counter]<=10){
                StdDraw.setPenColor(StdDraw.BOOK_BLUE);
                //radius(rx, ry, amplitude);
            }
            else if (pitch[counter]<=11){
                StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
                //radius(rx, ry, amplitude);
            }
            else {
                StdDraw.setPenColor(StdDraw.WHITE);
                //radius(rx, ry, amplitude);
            }
        }

        //System.out.println(pitch[counter]);


        radius(rx, ry, amplitude);
        StdDraw.show(1);
        //StdDraw.clear(StdDraw.GRAY);
        StdDraw.picture(0, 0, "Room_21.png");
        //System.out.println(counter);
        //System.out.println(rx);
    }

    public static void radius(double rx, double ry, double amplitude){

        if(radius!=amplitude){
            //StdDraw.clear(StdDraw.GRAY);
            //StdDraw.picture(.5, .5, "Room_21.png");
            if(radius<amplitude){
                while(radius<amplitude){
                    radius+=.001;
                    StdDraw.filledCircle(rx, ry, radius);
                }
            }
            if(radius>amplitude){
                while(radius>amplitude){
                    radius-=.001;
                    StdDraw.filledCircle(rx, ry, radius);
                }
            }
        }




        //StdDraw.clear(StdDraw.GRAY);
        //StdDraw.picture(.5, .5, "Room_21.png");
        StdDraw.filledCircle(rx, ry, radius);




    }


    public static double locationX(double loc, double velocity, double amplitude){
        for(int i=0; i<counter; i++){
            if (Math.abs(loc + velocity) + amplitude > 1.6829) {
                velocity = -velocity;

            }

            //System.out.println(rx);
            loc = loc + velocity;

        }
        return loc;
    }
    public static double locationY(double loc, double velocity, double amplitude){
        for(int i=0; i<counter; i++){
            if (Math.abs(loc + velocity) + amplitude > 1.0) {
                velocity = -velocity;

            }

            //System.out.println(rx);
            loc = loc + velocity;

        }
        return loc;
    }
    /***************************************************************************
     * Unit tests <tt>StdAudio</tt>.
     ***************************************************************************/
    // create a note (sine wave) of the given frequency (Hz), for the given
    // duration (seconds) scaled to the given volume (amplitude)
    private static double[] note(double hz, double duration, double amplitude) {
        int N = (int) (StdAudio.SAMPLE_RATE * duration);
        double[] a = new double[N+1];
        for (int i = 0; i <= N; i++)
            a[i] = amplitude * Math.sin(2 * Math.PI * i * hz / StdAudio.SAMPLE_RATE);
        return a;
    }
    /**
     * Test client - play an A major scale to standard audio.
     */
    public static void main(String[] args) {
        StdDraw.setCanvasSize(1380, 820);
        StdDraw.setXscale(-1.6829, 1.6829);
        StdDraw.setYscale(-1.0, 1.0);

//        // 440 Hz for 1 sec
//
//        double freq = 440.0;
//        double duration = 1;
//
//        double amplitude = .025;
//        //for (int i = 0; i <= StdAudio.SAMPLE_RATE; i++) {
//        //StdAudio.play(0.5 * Math.sin(2*Math.PI * freq * i / StdAudio.SAMPLE_RATE), 1);
//        // }
//
//        // scale increments
//        double[] steps = { 0, 2, 4, 5, 7, 9, 11, 12 };
//        for (int i = 0; i < steps.length; i++) {
//            double hz = 440.0 * Math.pow(2, steps[i] / 12.0);
//            //StdAudio.play(note(hz, duration, amplitude), duration, steps, amplitude);
//        }
//        // need to call this in non-interactive stuff so the program doesn't terminate
//        // until all the sound leaves the speaker.
//        StdAudio.close();
//        // need to terminate a Java program with sound
//        System.exit(0);

    }
}