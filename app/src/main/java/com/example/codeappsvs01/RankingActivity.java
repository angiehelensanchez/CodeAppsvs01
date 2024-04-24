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
import android.os.Environment;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ranking);

        rankingRecyclerView = findViewById(R.id.rankingRecyclerView);
        rankingRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        playAgainButton = findViewById(R.id.playAgainButton);
        playAgainButton.setOnClickListener(v -> {
            Intent intent = new Intent(RankingActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        exitButton = findViewById(R.id.exitButton);
        exitButton.setOnClickListener(v -> {
            finishAffinity(); // Cierra la aplicación completamente.
        });

        captureButton = findViewById(R.id.screenshotButton);
        captureButton.setOnClickListener(v -> captureAndSaveScreen());

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
        PlayerResultDao dao = AppDatabase.Db.getInstance(getApplicationContext()).getDAO();
        dao.getRanking()
                .subscribeOn(Schedulers.io()) // Ejecuta la consulta en el hilo IO
                .observeOn(AndroidSchedulers.mainThread()) // Observa los resultados en el hilo principal
                .subscribe(results -> {
                    if (results.isEmpty()) {
                        // Configurar RecyclerView con el adaptador de respaldo EmptyAdapter
                        rankingRecyclerView.setAdapter(new EmptyAdapter());
                    } else {
                        // Si hay resultados, actualiza la interfaz de usuario con los resultados
                        rankingAdapter = new RankingAdapter(results);
                        rankingRecyclerView.setAdapter(rankingAdapter);
                    }
                }, error -> {
                    // Maneja posibles errores aquí
                    Log.e("RankingActivity", "Error loading ranking data", error);
                });
    }
    private void captureAndSaveScreen() {
        // Obtener la vista raíz de la actividad
        View rootView = getWindow().getDecorView().getRootView();
        rootView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(rootView.getDrawingCache());
        rootView.setDrawingCacheEnabled(false);

        // Guardar la captura en el almacenamiento externo
        String filename = "screenshot_" + System.currentTimeMillis() + ".png";
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File screenshotFile = new File(directory, filename);

        try {
            FileOutputStream outputStream = new FileOutputStream(screenshotFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();

            MediaScannerConnection.scanFile(this, new String[]{screenshotFile.toString()}, null, null);

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