package com.example.codeappsvs01;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.OutputStream;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;


import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class RankingActivity extends AppCompatActivity {

    private RecyclerView rankingRecyclerView;
    private Button playAgainButton;
    private Button exitButton;
    private Button captureButton; // Botón para capturar pantalla
    private Button saveCalendarButton; // Botón para guardar en calendario
    private RankingAdapter rankingAdapter;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());
    private static final int REQUEST_CODE_SELECT_DIRECTORY = 1;
    private Bitmap bitmap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ranking);

        rankingRecyclerView = findViewById(R.id.rankingRecyclerView);
        rankingRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        playAgainButton = findViewById(R.id.playAgainButton);
        playAgainButton.setOnClickListener(v -> {
            // Recuperar el nombre del jugador y las monedas de la intent
            String playerName = getIntent().getStringExtra("PLAYER_NAME");
            int coinAmount = getIntent().getIntExtra("COIN_AMOUNT", 0);

            // Crear un intent para iniciar MainActivity y pasar los datos del jugador
            Intent intent = new Intent(RankingActivity.this, MainActivity.class);
            intent.putExtra("PLAYER_NAME", playerName);
            intent.putExtra("COIN_AMOUNT", coinAmount);
            startActivity(intent);
            finish();
        });


        exitButton = findViewById(R.id.exitButton);
        exitButton.setOnClickListener(v -> {
            finishAffinity(); // Cierra la aplicación completamente.
        });

        Button captureButton = findViewById(R.id.screenshotButton);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureAndSaveScreen();
            }
        });

        saveCalendarButton = findViewById(R.id.saveScoreButton);
        saveCalendarButton.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR}, 100);
            } else {
                saveScoreToCalendar();
            }
        });

        // Llamar a loadRankingData() después de que el RecyclerView se haya inicializado
        loadRankingData();
    }
    @SuppressLint("CheckResult")
    private void loadRankingData() {
        FirebaseDatabaseHelper dbHelper = FirebaseDatabaseHelper.getInstance();
        dbHelper.getAllPlayerResults(new FirebaseDatabaseHelper.OnResultsListener<PlayerResult>() {
            @Override
            public void onSuccess(List<PlayerResult> results) {
                if (results.isEmpty()) {
                    // Configurar RecyclerView con el adaptador de respaldo EmptyAdapter
                    rankingRecyclerView.setAdapter(new EmptyAdapter());
                } else {
                    // Si hay resultados, actualiza la interfaz de usuario con los resultados
                    rankingAdapter = new RankingAdapter(results);
                    rankingRecyclerView.setAdapter(rankingAdapter);
                }
            }

            @Override
            public void onFailure(Exception e) {
                // Maneja posibles errores aquí
                Log.e("RankingActivity", "Error loading ranking data", e);
            }
        });
    }
    private void captureAndSaveScreen() {
        // Obtener la vista raíz de la actividad
        View rootView = getWindow().getDecorView().getRootView();
        rootView.setDrawingCacheEnabled(true);
        bitmap = Bitmap.createBitmap(rootView.getDrawingCache());
        rootView.setDrawingCacheEnabled(false);

        // Crear un Intent para abrir un selector de directorios
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, REQUEST_CODE_SELECT_DIRECTORY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_DIRECTORY && resultCode == RESULT_OK) {
            // Obtener la URI del directorio seleccionado por el usuario
            Uri treeUri = data.getData();

            // Guardar la captura en el directorio seleccionado por el usuario
            saveBitmapToDirectory(treeUri);
        }
    }

    private void saveBitmapToDirectory(Uri directoryUri) {
        try {
            // Obtener el nombre de archivo y el directorio de almacenamiento externo
            String filename = "screenshot_" + System.currentTimeMillis() + ".png";
            DocumentFile directory = DocumentFile.fromTreeUri(this, directoryUri);
            DocumentFile file = directory.createFile("image/png", filename);

            // Escribir el bitmap en el archivo
            OutputStream outputStream = getContentResolver().openOutputStream(file.getUri());
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();

            // Escanear el archivo para que esté disponible en la galería
            MediaScannerConnection.scanFile(this, new String[]{file.getUri().toString()}, null, null);

            Toast.makeText(this, "Captura de pantalla guardada", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar la captura de pantalla", Toast.LENGTH_SHORT).show();
        }
    }


    private void saveScoreToCalendar() {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, System.currentTimeMillis());
        values.put(CalendarContract.Events.DTEND, System.currentTimeMillis() + 3600000);
        values.put(CalendarContract.Events.TITLE, "Game Score");
        values.put(CalendarContract.Events.DESCRIPTION, "Recorded a game score");
        values.put(CalendarContract.Events.CALENDAR_ID, 1);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());

        Uri uri = getContentResolver().insert(CalendarContract.Events.CONTENT_URI, values);
        if (uri != null) {
            Toast.makeText(this, "Score saved to calendar", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to save score", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            saveScoreToCalendar();
        } else {
            Toast.makeText(this, "Permission denied to write to calendar", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown(); // Asegura que el ExecutorService se detenga al cerrar la actividad.
    }
}