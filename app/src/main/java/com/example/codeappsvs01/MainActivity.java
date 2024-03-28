package com.example.codeappsvs01;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ImageView mSlot1, mSlot2, mSlot3;
    private TextView mGanancias;
    private Button mJugar;
    private RelativeLayout mRelative;

    private Random mRandom;
    private int mIntSlot1, mIntSlot2, mIntSlot3, mIntGanancias;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Obtiene los datos pasados desde StartActivity
        Intent intent = getIntent();
        String playerName = intent.getStringExtra("PLAYER_NAME");
        int coinAmount = intent.getIntExtra("COIN_AMOUNT", 0);

        // Crea una instancia de Player con los datos obtenidos
        Player Player = new Player(playerName, coinAmount);

        // Encuentra TextViews en tu layout y actualízalos con los datos
        TextView playerNameTextView = findViewById(R.id.playerNameTextView);
        TextView coinAmountTextView = findViewById(R.id.coinAmountTextView);
        playerNameTextView.setText(playerName);
        coinAmountTextView.setText(getString(R.string.coin_amount, coinAmount));

        // Botón para finalizar la partida y mostrar el ranking
        Button endGameButton = findViewById(R.id.endGameButton);
        endGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RankingActivity.class);
                startActivity(intent);
            }
        });


        mSlot1 = findViewById(R.id.mainActivitySlot1);
        mSlot2 = findViewById(R.id.mainActivitySlot2);
        mSlot3 = findViewById(R.id.mainActivitySlot3);

        mGanancias=findViewById(R.id.mainActivityTvGanancias);
        mJugar=findViewById(R.id.mainActivityBtJugar);
        mRelative=findViewById(R.id.main);

        mRandom = new Random();
        mIntGanancias = 5;
        mJugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Has lanzado: -1 euro", Toast.LENGTH_SHORT).show();

                mSlot1.setImageResource(R.drawable.animation);
                final AnimationDrawable slot1Anim = (AnimationDrawable) mSlot1.getDrawable();
                slot1Anim.start();

                mSlot2.setImageResource(R.drawable.animation);
                final AnimationDrawable slot2Anim = (AnimationDrawable) mSlot2.getDrawable();
                slot2Anim.start();

                mSlot3.setImageResource(R.drawable.animation);
                final AnimationDrawable slot3Anim = (AnimationDrawable) mSlot3.getDrawable();
                slot3Anim.start();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        slot1Anim.stop();
                        slot2Anim.stop();
                        slot3Anim.stop();

                        ponerImagenes();
                        dineroAcumulado();
                    }
                }, 1000);

            }
        });

        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/
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
    private void dineroAcumulado(){
        if ((mIntSlot1 == mIntSlot2)&&(mIntSlot1 == mIntSlot3)){
            Snackbar.make(mRelative, "Has Ganado 100 euros", Snackbar.LENGTH_SHORT).show();
            mIntGanancias = mIntGanancias + 100;
        }else if((mIntSlot1 == mIntSlot2)||(mIntSlot1 == mIntSlot3)|| (mIntSlot2 == mIntSlot3)){
            Snackbar.make(mRelative, "Has Ganado 5 euros", Snackbar.LENGTH_SHORT).show();
            mIntGanancias = mIntGanancias + 5;
        }
        mIntGanancias = mIntGanancias -1;
        mGanancias.setText(String.valueOf(mIntGanancias));
    }
}

