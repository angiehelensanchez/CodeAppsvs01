package com.example.codeappsvs01;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start); // Asegúrate de tener un layout `activity_start.xml`

        final EditText playerNameEditText = findViewById(R.id.playerName);
        final EditText coinAmountEditText = findViewById(R.id.coinAmount);
        Button startGameButton = findViewById(R.id.startGameButton);

        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String playerName = playerNameEditText.getText().toString();
                int coinAmount;
                try {
                    coinAmount = Integer.parseInt(coinAmountEditText.getText().toString());
                } catch (NumberFormatException e) {
                    coinAmount = 0; // O maneja el error como prefieras
                }

                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                intent.putExtra("PLAYER_NAME", playerName);
                intent.putExtra("COIN_AMOUNT", coinAmount);
                startActivity(intent);
            }
        });
    }
}
