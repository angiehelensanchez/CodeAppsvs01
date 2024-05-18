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
    private TextView coinAmountTextView;
    private Button mJugar;
    private RelativeLayout mRelative;
    private String playerName;

    private Random mRandom;
    private int mIntSlot1, mIntSlot2, mIntSlot3, mIntGanancias;
    public PlayerResult playerResult;

    private MediaPlayer mediaPlayer;
    private WebView webView;
    private Button toggleWebViewButton;
    private int highestResult = 0;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "onCreate called");
        setContentView(R.layout.activity_main);
        EdgeToEdge.enable(this);

        // Obtener el récord actual
        FirebaseDatabaseHelper.getInstance().getHighestResult(new FirebaseDatabaseHelper.OnResultListener<PlayerResult>() {
            @Override
            public void onSuccess(PlayerResult result) {
                if (result != null) {
                    highestResult = result.getResult();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("MainActivity", "Error fetching highest result", e);
            }
        });

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

        mIntGanancias = coinAmount;
        Button endGameButton = findViewById(R.id.endGameButton);
        endGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playerResult = new PlayerResult(playerName, mIntGanancias);
                checkAndInsertPlayerResult(playerResult);
            }
        });

        mSlot1 = findViewById(R.id.mainActivitySlot1);
        mSlot2 = findViewById(R.id.mainActivitySlot2);
        mSlot3 = findViewById(R.id.mainActivitySlot3);

        mJugar = findViewById(R.id.mainActivityBtJugar);
        mRelative = findViewById(R.id.main);
        mRandom = new Random();
        mIntGanancias = coinAmount;

        mediaPlayer = MediaPlayer.create(this, R.raw.ruleta);

        mJugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSlotAnimation();
                mediaPlayer.start();
                botonSound.seekTo(0);
                botonSound.start();
                new Handler().postDelayed(new Runnable() {
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
        AnimationDrawable slot1Anim = (AnimationDrawable) mSlot1.getDrawable();
        slot1Anim.stop();

        AnimationDrawable slot2Anim = (AnimationDrawable) mSlot2.getDrawable();
        slot2Anim.stop();

        AnimationDrawable slot3Anim = (AnimationDrawable) mSlot3.getDrawable();
        slot3Anim.stop();
    }

    private void ponerImagenes() {
        mIntSlot1 = mRandom.nextInt(6);
        mIntSlot2 = mRandom.nextInt(6);
        mIntSlot3 = mRandom.nextInt(6);

        switch (mIntSlot1) {
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
        switch (mIntSlot2) {
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
        switch (mIntSlot3) {
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
        mIntGanancias -= 1;
        Toast.makeText(getApplicationContext(), R.string.has_lanzado_1_euro, Toast.LENGTH_SHORT).show();
        if ((mIntSlot1 == mIntSlot2) && (mIntSlot1 == mIntSlot3)) {
            mIntGanancias += 10;
            Snackbar.make(mRelative, R.string.has_ganado_10_euros, Snackbar.LENGTH_SHORT).show();
        } else if ((mIntSlot1 == mIntSlot2) || (mIntSlot1 == mIntSlot3) || (mIntSlot2 == mIntSlot3)) {
            mIntGanancias += 5;
            Snackbar.make(mRelative, R.string.has_ganado_5_euros, Snackbar.LENGTH_SHORT).show();
        }

        coinAmountTextView.setText(getString(R.string.coin_amount, mIntGanancias));
    }

    private void checkAndInsertPlayerResult(PlayerResult playerResult) {
        FirebaseDatabaseHelper dbHelper = FirebaseDatabaseHelper.getInstance();
        dbHelper.getHighestResult(new FirebaseDatabaseHelper.OnResultListener<PlayerResult>() {
            @Override
            public void onSuccess(PlayerResult highestResult) {
                if (highestResult == null || playerResult.getResult() > highestResult.getResult()) {
                    // Si el jugador ha batido el récord
                    playerResult.setResult(playerResult.getResult() + 5);
                    Toast.makeText(MainActivity.this, "¡Felicidades! Has batido el récord y ganado 5 monedas adicionales.", Toast.LENGTH_LONG).show();
                }
                insertPlayerResultInDatabase(playerResult);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("MainActivity", "Error fetching highest result", e);
                insertPlayerResultInDatabase(playerResult);
            }
        });
    }

    @SuppressLint("CheckResult")
    private void insertPlayerResultInDatabase(PlayerResult playerResult) {
        FirebaseDatabaseHelper.getInstance().addPlayerResult(playerResult);
        Log.d("MainActivity", "Resultado insertado correctamente.");
        Intent rankingIntent = new Intent(MainActivity.this, RankingActivity.class);
        startActivity(rankingIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void loadWebView() {
        webView.loadUrl("file:///android_asset/guia.html");
    }
}
