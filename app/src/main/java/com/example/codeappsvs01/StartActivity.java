package com.example.codeappsvs01;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import java.util.Locale;

import java.io.IOException;
import java.io.InputStream;
import android.Manifest;
import android.content.pm.PackageManager;

import pl.droidsonroids.gif.GifDrawable;

public class StartActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SELECT_MUSIC = 100;
    private MediaPlayer mediaPlayer;
    private boolean isMusicPlaying = false;
    public String lenguage = "Español";
    Spinner spinner;
    public static final String[] Languages = {"Select Language", "English","Spain", "中文"};
    private WebView webView;
    private Button toggleWebViewButton;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private LocationDataBase dbLocalitation;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            obtenerYGuardarUbicacion();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }

        webView = findViewById(R.id.webView2);
        webView.getSettings().setJavaScriptEnabled(true);
        toggleWebViewButton = findViewById(R.id.button2);
        toggleWebViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView.getVisibility() == View.VISIBLE) {
                    webView.setVisibility(View.GONE);
                } else {
                    webView.setVisibility(View.VISIBLE);
                    loadWebView();
                }
            }
        });
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("UnsafeIntentLaunch")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLang = parent.getItemAtPosition(position).toString();
                if (selectedLang.equals("English")) {
                    setLocale("en");
                } else if (selectedLang.equals("Spain")) {
                    setLocale("es");
                } else if ((selectedLang.equals("中文"))) {
                    setLocale("zh");
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final ImageView backgroundImageView = findViewById(R.id.backgroundImageView);

        try {

            InputStream inputStream = getResources().openRawResource(R.raw.videofondo2);
            GifDrawable gifDrawable = new GifDrawable(inputStream);

            backgroundImageView.setImageDrawable(gifDrawable);

            gifDrawable.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
         MediaPlayer botonSound = MediaPlayer.create(this, R.raw.pulsar_boton);

        mediaPlayer = MediaPlayer.create(this, R.raw.melodia_fondo);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        final EditText playerNameEditText = findViewById(R.id.playerName);
        final EditText coinAmountEditText = findViewById(R.id.coinAmount);
        Button startGameButton = findViewById(R.id.startGameButton);
        Button musicToggleButton = findViewById(R.id.musicToggleButton);
        musicToggleButton.setText(R.string.music_button_controller);
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                botonSound.start();
                String playerName = playerNameEditText.getText().toString();
                int coinAmount;
                try {
                    coinAmount = Integer.parseInt(coinAmountEditText.getText().toString());
                } catch (NumberFormatException e) {
                    coinAmount = 0;
                }

                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                intent.putExtra("PLAYER_NAME", playerName);
                intent.putExtra("COIN_AMOUNT", coinAmount);
                startActivity(intent);
            }
        });

        musicToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                botonSound.start();

                if (isMusicPlaying) {

                    mediaPlayer.pause();
                    isMusicPlaying = false;
                    musicToggleButton.setText(R.string.music_off);
                } else {

                    mediaPlayer.start();
                    isMusicPlaying = true;
                    musicToggleButton.setText(R.string.music_on);
                }
            }
        });

        Button selectMusicButton = findViewById(R.id.selectMusicButton);
        selectMusicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent, "Selecciona una melodía"), REQUEST_CODE_SELECT_MUSIC);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_MUSIC) {
            if (resultCode == RESULT_OK) {

                if (data != null) {
                    Uri selectedMusicUri = data.getData();

                    try {
                        if (mediaPlayer != null) {
                            mediaPlayer.reset();
                        } else {
                            mediaPlayer = new MediaPlayer();
                        }

                        mediaPlayer.setDataSource(this, selectedMusicUri);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        isMusicPlaying = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Toast.makeText(this, "Permiso denegado para acceder al archivo de música seleccionado.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isMusicPlaying && mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isMusicPlaying && mediaPlayer != null) {
            mediaPlayer.start();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    public void setLocale(String languageCode) {
        Locale myLocale = new Locale(languageCode);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, StartActivity.class);
        finish();
        startActivity(refresh);
    }
    private void loadWebView() {
        webView.loadUrl("file:///android_asset/guia.html");
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerYGuardarUbicacion();
            } else {
                Toast.makeText(this, "La funcionalidad de ubicación está limitada porque no se concedieron permisos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void obtenerYGuardarUbicacion() {
        Location ubicacion = null;
        if (ubicacion != null) {
            SQLiteDatabase db = dbLocalitation.getWritableDatabase();
            dbLocalitation.insertarUbicacion(db, ubicacion.getLatitude(), ubicacion.getLongitude());
            db.close();
        }
    }
}