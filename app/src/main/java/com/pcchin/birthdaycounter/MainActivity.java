package com.pcchin.birthdaycounter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class MainActivity extends AppCompatActivity {
    static boolean confettiEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Read confetti state
        SharedPreferences appKeys = this.getSharedPreferences(getString(R.string.shared_pref_key), Context.MODE_PRIVATE);

        // ****** FOR BIRTHDAY ****** //

        // Bind confetti
        Switch confettiSwitch = findViewById(R.id.birthday_now_confetti);
        confettiEnabled = appKeys.getBoolean("confetti", true);
        confettiSwitch.setChecked(confettiEnabled);
        confettiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("ApplySharedPref")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedIntPreferences = getApplicationContext().getSharedPreferences(getString(R.string.shared_pref_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor appKeysIntEditor = sharedIntPreferences.edit();
                appKeysIntEditor.putBoolean("confetti", isChecked);
                appKeysIntEditor.apply();

                confettiEnabled = sharedIntPreferences.getBoolean("confetti", false);
                startConfetti();
            }
        });

        // Set on click listener
        ViewGroup mainLayout = findViewById(R.id.main_box);
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.popper);
        mainLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mp.start();
                KonfettiView konfettiView = findViewById(R.id.konfettiView);
                v.performClick();
                konfettiView.build()
                        .addColors(Color.YELLOW, Color.GREEN, Color.RED, Color.MAGENTA, Color.BLUE)
                        .setDirection(0.0, 359.0)
                        .setSpeed(1f, 5f)
                        .setFadeOutEnabled(true)
                        .setTimeToLive(2000L)
                        .addShapes(Shape.RECT, Shape.CIRCLE)
                        .addSizes(new Size(12, 5f))
                        .setPosition(event.getX() - 10f, event.getX() + 10f, event.getY() - 10f, event.getY() - 10f)
                        .streamFor(300, 50L);
                return false;
            }
        });

        startConfetti();

        // Set age
        DateFormat dateFormat = new SimpleDateFormat("d/M/YYYY", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        Date birthdayDate;
        Date currentDate = new Date();
        try {
            birthdayDate = dateFormat.parse(appKeys.getString("birthday", null));
            currentDate = dateFormat.parse(dateFormat.format(currentDate.getTime()));

            calendar.setTime(birthdayDate);
            int birthdayYear = calendar.get(Calendar.YEAR);

            calendar.setTime(currentDate);
            int currentYear = calendar.get(Calendar.YEAR);

            TextView textView = findViewById(R.id.birthday_now_age);
            textView.setText(String.format(Locale.ENGLISH, "Congrats on your %dth birthday!", currentYear - birthdayYear));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void startConfetti() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final KonfettiView konfettiView = findViewById(R.id.konfettiView);
                DisplayMetrics displaymetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                Random random = new Random();

                for (int i=0; i<1800; i++) {
                    if (! confettiEnabled) {return;}
                    int currentWidth = random.nextInt(displaymetrics.widthPixels);
                    int currentHeight = random.nextInt(displaymetrics.heightPixels);
                    konfettiView.build()
                            .addColors(Color.YELLOW, Color.GREEN, Color.RED, Color.MAGENTA, Color.BLUE)
                            .setDirection(0.0, 359.0)
                            .setSpeed(1f, 5f)
                            .setFadeOutEnabled(true)
                            .setTimeToLive(500L)
                            .addShapes(Shape.RECT, Shape.CIRCLE)
                            .addSizes(new Size(12, 5f))
                            .setPosition(currentWidth - 20f, currentWidth + 20f, currentHeight - 20f, currentHeight - 20f)
                            .streamFor(300, 500L);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

}
