package com.example.thermometera34;

import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    int samplingFrequency = 8000;
    int blockSize = 2048;
    int historySize = 10;
    double[] x;
    double[] y;
    double[] wave;
    int frequency = 0;
    double[] tempHistory;
    int rejectCounter = 0;
    double ymax = 0;
    int ymaxi = 0;

    AudioInput audioInput;
    Painter painter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        x = new double[blockSize];
        y = new double[blockSize];
        wave = new double[blockSize/2];
        tempHistory = new double[historySize];

        painter = new Painter(this);

        audioInput = new AudioInput(this);
        audioInput.start();
    }

    public void DrawWave()
    {
        painter.DrawWave();
    }
}