package com.example.codeappsvs01;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;

import pl.droidsonroids.gif.GifDrawable;

public class CoinAmountActivity extends AppCompatActivity {

    private EditText coinAmountEditText;
    private Button proceedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_amount);

        ImageView backgroundImageView = findViewById(R.id.backgroundImageView);

        try {
            InputStream inputStream = getResources().openRawResource(R.raw.videofondo2);
            GifDrawable gifDrawable = new GifDrawable(inputStream);
            backgroundImageView.setImageDrawable(gifDrawable);
            gifDrawable.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        coinAmountEditText = findViewById(R.id.coinAmountEditText);
        proceedButton = findViewById(R.id.proceedButton);

        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int coinAmount;
                try {
                    coinAmount = Integer.parseInt(coinAmountEditText.getText().toString());
                } catch (NumberFormatException e) {
                    coinAmount = 0;
                }
                if (coinAmount > 0) {
                    String playerName = getIntent().getStringExtra("PLAYER_NAME");
                    String idToken = getIntent().getStringExtra("ID_TOKEN");

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("PLAYER_NAME", playerName);
                    resultIntent.putExtra("COIN_AMOUNT", coinAmount);
                    resultIntent.putExtra("ID_TOKEN", idToken);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(CoinAmountActivity.this, "Please enter a valid coin amount", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
