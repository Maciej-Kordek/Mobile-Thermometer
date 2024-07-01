package com.example.thermometera34;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class Painter{
    com.example.thermometera34.MainActivity main;

    TextView tempView;
    TextView peakView;
    ImageView imageView;
    Bitmap bitmap;
    Canvas canvas;
    Paint paint;

    public Painter(com.example.thermometera34.MainActivity _main)
    {
        main = _main;

        tempView = main.findViewById(R.id.tempText);
        peakView = main.findViewById(R.id.peakText);

        imageView = main.findViewById(R.id.imageView);
        bitmap = Bitmap.createBitmap(main.blockSize/2, 520, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        imageView.setImageBitmap(bitmap);
        canvas.drawColor(Color.BLACK);
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(2);

    }

    public void DrawWave()
    {
        canvas.drawColor(Color.BLACK);

        for(int i = 1; i < main.wave.length-2; i++)
        {
            int downy = 510;
            int upy = 510 - (int) main.wave[i];
            canvas.drawLine(i, downy, i, upy, paint);
        }
        System.out.println("Painted");

        double average = 0;
        for(int i = 0; i < main.historySize; i++)
            average += main.tempHistory[i];

        average /= main.historySize;

        if(main.rejectCounter > 0)
        {
            tempView.setTextColor(Color.RED);
        }else
        {
            tempView.setTextColor(Color.BLACK);
        }

        tempView.setText(average + "");
        peakView.setText(main.frequency + "");

    }
}
