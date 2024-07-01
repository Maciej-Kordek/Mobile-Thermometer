package com.example.thermometera34;

import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import androidx.core.app.ActivityCompat;

public class AudioInput extends Thread {
    com.example.thermometera34.MainActivity main;

    int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

    int samplingFrequency;
    int blockSize;

    android.app.Activity activity;
    double[] x;
    double[] y;
    double[] wave;
    double frequency;
    double[] tempHistory;
    int rejectCounter;
    FFT myFFT;

    public AudioInput(com.example.thermometera34.MainActivity _main)
    {
        main = _main;
        blockSize = _main.blockSize;
        samplingFrequency = _main.samplingFrequency;
        activity = _main;
        x = _main.x;
        y = _main.y;
        wave = _main.wave;
        frequency = _main.frequency;
        tempHistory = _main.tempHistory;
        rejectCounter = _main.rejectCounter;

        myFFT = new FFT(blockSize);
    }

    public void run()
    {
        System.out.println("Readout thread " + Thread.currentThread().getId()+ " is running");

        short[] audioBuffer = new short[blockSize];

        int bufferSize = AudioRecord.getMinBufferSize(samplingFrequency, channelConfiguration, audioEncoding);

        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.RECORD_AUDIO},0);
            return;
        }
        AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, samplingFrequency,channelConfiguration, audioEncoding, bufferSize);
        audioRecord.startRecording();

        while (true)
        {
            for (int i = 0; i < blockSize; i++)
            {
                y[i] = 0;
            }
            /*---READING---*/
            int bufferReadResult = audioRecord.read(audioBuffer, 0, blockSize);

            for (int i = 0; i < blockSize && i < bufferReadResult; i++)
            {
                x[i] = (double) audioBuffer[i] / 32768.0; // signed 16 bit
            }

            /*---COMPUTING---*/
            GetWave();
            TrackTemp();
            System.out.println("Computed");
            main.DrawWave();

            /*---SLEEP---*/
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }
    public void GetWave()
    {
        myFFT.fft(x, y);
        main.ymax = 0;
        main.ymaxi = 0;
        for(int i = 1; i < blockSize/2 - 1; i++)
        {
            wave[i] = x[i] * x[i] + y[i] * y[i];
            if(wave[i] > main.ymax)
            {
                main.ymax = wave[i];
                main.ymaxi = i;
            }
        }
        for(int i = 1; i < blockSize/2 - 1; i++)
        {
            wave[i] = wave[i] * 500 / main.ymax;
        }
    }
    public void TrackTemp()
    {
        main.frequency = main.ymaxi * main.samplingFrequency / main.blockSize;
        double temperature = 0.0203 * main.frequency - 35.664;

        if(tempHistory == null)
        {
            for(int i = 0; i < main.historySize; i++)
                tempHistory[i] = temperature;
        }

        if(main.rejectCounter >= 3)
        {
            //Accept and readjust
            AcceptTemperature(temperature);
        }
        else
        if(Math.abs(tempHistory[0] - temperature) > 3 )
        {
            main.rejectCounter++;
            return;
        }

        for(int i = main.historySize-1; i > 0; i--)
            tempHistory[i] = tempHistory[i-1];

        tempHistory[0] = temperature;
        main.rejectCounter = 0;
    }

    public void AcceptTemperature(double temperature)
    {
        for(int i = 0; i < main.historySize; i++)
            tempHistory[i] = temperature;

        main.rejectCounter = 0;
    }
}
