package de.mbranig.emiexercise3;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.ToggleButton;

public class SoundSynthesisActivity extends AppCompatActivity {


    // --- variables for frequency and amplitude slider ---
    SeekBar fSlider;
    double fSliderval;


    // --- variables for the sound synthesis ---
    Thread t;
    boolean isRunning = true;
    boolean isPlaying = true;
    boolean isSineWave = true;

    final int sr = 44100;                   // maximum frequency
    double twopi = 8. * Math.atan(1.);      // atan(1) is Pi/4
    int amp = 10000;                        // amplitude
    double fr = 440.f;                      // frequency
    double ph = 0.0;                        // phase

    int buffsize = AudioTrack.getMinBufferSize(sr, AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT);
    short samples[] = new short[buffsize];

    // create an audiotrack object
    AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sr,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            buffsize,
            AudioTrack.MODE_STREAM);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_synthesis);

        // point the slider to the GUI widget
        fSlider = (SeekBar) findViewById(R.id.frequency);

        // create a listener for the frequency slider bar;
        SeekBar.OnSeekBarChangeListener fListener = new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) { }
            public void onStartTrackingTouch(SeekBar seekBar) { }
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) fSliderval = progress / (double)seekBar.getMax();
            }
        };

        // set the listener on the frequency slider
        fSlider.setOnSeekBarChangeListener(fListener);






        // opening a thread for sound synthesis
        t = new Thread() {

            public void run() {
                // set process priority
                setPriority(Thread.MAX_PRIORITY);

                // start audio
                audioTrack.play();
                // synthesis loop
                while (isRunning) {
                    fr = 440. + 440. * fSliderval;
                    if(isSineWave){
                        fillblockSine();
                    } else {
                        fillblockSaw();
                    }
                }
                audioTrack.stop();
                audioTrack.release();
            }

            /**
             * This methods fills the buffer with the values of a sine wave.
             */
            void fillblockSine() {
                for (int i = 0; i < buffsize; i++) {
                    samples[i] = (short) (amp * Math.sin(ph));
                    ph += twopi * fr / sr;
                }
                audioTrack.write(samples, 0, buffsize);
            }

            /**
             * This method fills the buffer with the values of a sawtooth wave.
             */
            void fillblockSaw() {
                for (int i = 0; i < buffsize; i++) {
                    samples[i] = (short) (amp * (ph - Math.floor(ph)));
                    ph = ph + fr / sr;
                }
                audioTrack.write(samples, 0, buffsize);
            }
        };
        t.start();

        /**
         * Implementation of the toggle button to change between sinewave and sawtoothwave.
         */
        ToggleButton toggle = (ToggleButton) findViewById(R.id.btnSchwingung);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isSineWave = false;
                } else {
                    isSineWave = true;
                }
            }
        });


    }


    /**
     * Method to stop the synthesized tone playing or start it again.
     * @param view
     */
    public void onStartStop(View view){
        if (isPlaying){
            isPlaying = false;
            audioTrack.pause();
        } else {
            isPlaying = true;
            audioTrack.play();
        }
    }

    public void onDestroy(){
        super.onDestroy();
        isRunning = false;
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t = null;
    }
}
