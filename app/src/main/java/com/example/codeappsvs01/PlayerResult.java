package com.example.codeappsvs01;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PlayerResult {
    private int id;
    private String playerName;
    private int result;
    private String date;

    // Constructor vacío requerido para Firestore
    public PlayerResult() {}

    // Constructor
    public PlayerResult(String playerName, int result) {
        this.playerName = playerName;
        this.result = result;
        this.date = toDate();
    }

    // Getters y setters
    public String toDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        return sdf.format(new Date());
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
