package com.example.codeappsvs01;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.Random;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private ImageView mSlot1, mSlot2, mSlot3;
    private TextView  coinAmountTextView;
    private Button mJugar;
    private RelativeLayout mRelative;
    private String playerName;

    private Random mRandom;
    private int mIntSlot1, mIntSlot2, mIntSlot3, mIntGanancias;
    public PlayerResult playerResult;

    private MediaPlayer mediaPlayer;
    private WebView webView;
    private Button toggleWebViewButton;
    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "onCreate called");
        setContentView(R.layout.activity_main);
        EdgeToEdge.enable(this);
        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        toggleWebViewButton = findViewById(R.id.button);
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

        Intent intent = getIntent();
        playerName = intent.getStringExtra("PLAYER_NAME");
        int coinAmount = intent.getIntExtra("COIN_AMOUNT", 0);

        MediaPlayer botonSound = MediaPlayer.create(this, R.raw.pulsar_boton);


        TextView playerNameTextView = findViewById(R.id.playerNameTextView);
        coinAmountTextView = findViewById(R.id.coinAmountTextView);
        playerNameTextView.setText(playerName);
        coinAmountTextView.setText(getString(R.string.coin_amount, coinAmount));

        // Inicializa mIntGanancias con el valor de coinAmount
        mIntGanancias = coinAmount;

        // Botón para finalizar la partida y mostrar el ranking
        Button endGameButton = findViewById(R.id.endGameButton);
        endGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playerResult = new PlayerResult(playerName, mIntGanancias);
                insertPlayerResultInDatabase(playerResult);
            }
        });
        // Inicialización de los ImageView para los slots
        mSlot1 = findViewById(R.id.mainActivitySlot1);
        mSlot2 = findViewById(R.id.mainActivitySlot2);
        mSlot3 = findViewById(R.id.mainActivitySlot3);

        // Inicialización del botón para jugar
        mJugar=findViewById(R.id.mainActivityBtJugar);
        // Inicialización del RelativeLayout
        mRelative=findViewById(R.id.main);
        // Inicialización del objeto Random para los slots
        mRandom = new Random();
        // Establece el saldo inicial del jugador basado en el valor pasado de StartActivity
        mIntGanancias = coinAmount;

        // Inicializar MediaPlayer para el sonido de la ruleta
        mediaPlayer = MediaPlayer.create(this, R.raw.ruleta);

        /*mJugar=findViewById(R.id.mainActivityBtJugar);*/
        mJugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Iniciar la animación de las imágenes de los slots
                startSlotAnimation();
                // Reproducir el sonido de la ruleta cuando se inicia la animación de las imágenes
                mediaPlayer.start();
                // Reiniciar y reproducir el sonido del botón
                botonSound.seekTo(0); // Reinicia el sonido
                botonSound.start(); // Reproduce el sonido

                // Retrasar la ejecución del código después de un segundo
                new Handler().postDelayed(new Runnable() {
                    // Detener la música cuando finaliza la animación de las imágenes
                    @Override
                    public void run() {
                        if (mIntGanancias > 0) {
                            stopSlotAnimation();
                            ponerImagenes();
                            dineroAcumulado();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.without_coins, Toast.LENGTH_SHORT).show();
                        }

                        mediaPlayer.pause();
                    }
                }, 1000);
            }
        });
    }


    private void startSlotAnimation() {
        // Iniciar la animación de las imágenes de los slots
        mSlot1.setImageResource(R.drawable.animation);
        final AnimationDrawable slot1Anim = (AnimationDrawable) mSlot1.getDrawable();
        slot1Anim.start();

        mSlot2.setImageResource(R.drawable.animation);
        final AnimationDrawable slot2Anim = (AnimationDrawable) mSlot2.getDrawable();
        slot2Anim.start();

        mSlot3.setImageResource(R.drawable.animation);
        final AnimationDrawable slot3Anim = (AnimationDrawable) mSlot3.getDrawable();
        slot3Anim.start();
    }

    private void stopSlotAnimation() {
        // Detener la animación de las imágenes de los slots
        AnimationDrawable slot1Anim = (AnimationDrawable) mSlot1.getDrawable();
        slot1Anim.stop();

        AnimationDrawable slot2Anim = (AnimationDrawable) mSlot2.getDrawable();
        slot2Anim.stop();

        AnimationDrawable slot3Anim = (AnimationDrawable) mSlot3.getDrawable();
        slot3Anim.stop();
    }


    private void ponerImagenes(){
        mIntSlot1 = mRandom.nextInt(6);
        mIntSlot2 = mRandom.nextInt(6);
        mIntSlot3 = mRandom.nextInt(6);

        switch (mIntSlot1){
            case 0:
                mSlot1.setImageResource(R.drawable.ic_ancla);
                break;
            case 1:
                mSlot1.setImageResource(R.drawable.ic_balanza);
                break;
            case 2:
                mSlot1.setImageResource(R.drawable.ic_conejo);
                break;
            case 3:
                mSlot1.setImageResource(R.drawable.ic_nieve);
                break;
            case 4:
                mSlot1.setImageResource(R.drawable.ic_noria);
                break;
            case 5:
                mSlot1.setImageResource(R.drawable.ic_viejo);
                break;
        }
        switch (mIntSlot2){
            case 0:
                mSlot2.setImageResource(R.drawable.ic_ancla);
                break;
            case 1:
                mSlot2.setImageResource(R.drawable.ic_balanza);
                break;
            case 2:
                mSlot2.setImageResource(R.drawable.ic_conejo);
                break;
            case 3:
                mSlot2.setImageResource(R.drawable.ic_nieve);
                break;
            case 4:
                mSlot2.setImageResource(R.drawable.ic_noria);
                break;
            case 5:
                mSlot2.setImageResource(R.drawable.ic_viejo);
                break;
        }
        switch (mIntSlot3){
            case 0:
                mSlot3.setImageResource(R.drawable.ic_ancla);
                break;
            case 1:
                mSlot3.setImageResource(R.drawable.ic_balanza);
                break;
            case 2:
                mSlot3.setImageResource(R.drawable.ic_conejo);
                break;
            case 3:
                mSlot3.setImageResource(R.drawable.ic_nieve);
                break;
            case 4:
                mSlot3.setImageResource(R.drawable.ic_noria);
                break;
            case 5:
                mSlot3.setImageResource(R.drawable.ic_viejo);
                break;
        }

    }
    private void dineroAcumulado() {
        // Asume que la apuesta es 1 moneda por juego
        mIntGanancias -= 1; // Costo de jugar una partida
        Toast.makeText(getApplicationContext(), R.string.has_lanzado_1_euro, Toast.LENGTH_SHORT).show();
        if ((mIntSlot1 == mIntSlot2) && (mIntSlot1 == mIntSlot3)) {
            mIntGanancias += 10;
            Snackbar.make(mRelative, R.string.has_ganado_10_euros, Snackbar.LENGTH_SHORT).show();
        } else if ((mIntSlot1 == mIntSlot2) || (mIntSlot1 == mIntSlot3) || (mIntSlot2 == mIntSlot3)) {
            // El jugador gana 5 monedas
            mIntGanancias += 5;
            Snackbar.make(mRelative, R.string.has_ganado_5_euros, Snackbar.LENGTH_SHORT).show();
        }
        // Actualizar la UI con el nuevo saldo
        coinAmountTextView.setText(getString(R.string.coin_amount, mIntGanancias));
    }

    @SuppressLint("CheckResult")
    private void insertPlayerResultInDatabase(PlayerResult playerResult) {
        AppDatabase.Db db = AppDatabase.Db.getInstance(this);
        db.getDAO().insertResult(playerResult)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Log.d("MainActivity", "Resultado insertado correctamente.");
                    Intent rankingIntent = new Intent(MainActivity.this, RankingActivity.class);
                    startActivity(rankingIntent);
                }, throwable -> {
                    Log.e("MainActivity", "Error al insertar resultado", throwable);
                });
    }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            // Liberar recursos del reproductor de música
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
    }
    private void loadWebView() {
        // Carga el archivo HTML en el WebView
        webView.loadUrl("file:///android_asset/guia.html");
    }
}