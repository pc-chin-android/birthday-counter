package com.pcchin.birthdaycounter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.skydoves.colorpickerpreference.ColorEnvelope;
import com.skydoves.colorpickerpreference.ColorListener;
import com.skydoves.colorpickerpreference.ColorPickerDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class MainNormalActivity extends AppCompatActivity {
    static boolean confettiEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_normal);

        SharedPreferences appKeys = this.getSharedPreferences(getString(R.string.shared_pref_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor appKeysEditor = appKeys.edit();

        // ## For Shannon ## //
        // Set default birthday
        appKeysEditor.putString("birthday", "20/12/2002");
        appKeysEditor.apply();

        // Check current date
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        final Date birthdayDate;
        Date currentDate = new Date();
        try {
            birthdayDate = dateFormat.parse(appKeys.getString("birthday", null));
            currentDate = dateFormat.parse(dateFormat.format(currentDate.getTime()));

            calendar.setTime(birthdayDate);
            int birthdayMonth = calendar.get(Calendar.MONTH);
            int birthdayDay = calendar.get(Calendar.DAY_OF_MONTH);

            calendar.setTime(currentDate);
            int currentMonth = calendar.get(Calendar.MONTH);
            int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

            // Go to other activity if it is birthday
            if ((birthdayMonth == currentMonth) && (birthdayDay == currentDay)) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    final TextView textView = findViewById(R.id.birthday_future_datetime);
                    //noinspection InfiniteLoopStatement
                    while (true) {
                        Calendar calendar = Calendar.getInstance();
                        // Check when is next birthday
                        calendar.setTime(birthdayDate);
                        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                        while (calendar.getTime().before(new Date())) {
                            calendar.add(Calendar.YEAR, 1);
                        }

                        while (new Date().before(calendar.getTime())) {
                            final long timeDifference = calendar.getTimeInMillis() - new Date().getTime();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    long second = (timeDifference / 1000) % 60;
                                    long minute = (timeDifference / (1000 * 60)) % 60;
                                    long hour = (timeDifference / (1000 * 60 * 60)) % 24;
                                    long day = (timeDifference / (1000 * 60 * 60 * 24));
                                    textView.setText(String.format(getString(R.string.hrs_timer), day, hour, minute, second));                                }
                            });
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }).start();
            System.out.println(calendar.getTime().toString());

        } catch (ParseException e) {
            e.printStackTrace();
        }

        // TODO: Make timer

        // Set background color
        Button bgButton = findViewById(R.id.birthday_future_sel_bg);
        bgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog.Builder bgColorPicker = new ColorPickerDialog.Builder(MainNormalActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                bgColorPicker.setTitle(R.string.sel_bg);
                bgColorPicker.setPreferenceName("select_bg_dialog");
                bgColorPicker.setPositiveButton(getString(R.string.ok), new ColorListener() {
                    @Override
                    public void onColorSelected(ColorEnvelope colorEnvelope) {
                        ConstraintLayout constraintLayout = findViewById(R.id.birthday_future);
                        constraintLayout.setBackgroundColor(colorEnvelope.getColor());
                    }
                });
                bgColorPicker.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                bgColorPicker.create().show();
            }
        });

        // Bind confetti
        Switch confettiSwitch = findViewById(R.id.birthday_future_confetti);
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

        startConfetti();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    public void startConfetti() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final KonfettiView konfettiView = findViewById(R.id.konfettiViewFuture);
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
}
