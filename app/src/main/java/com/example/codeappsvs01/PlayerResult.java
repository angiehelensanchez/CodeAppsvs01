package com.example.codeappsvs01;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

@Entity
public class PlayerResult {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "player_name")
    public String playerName;

    @ColumnInfo(name = "result")
    public int result;
    @ColumnInfo(name = "date")
    public String date;

    // Constructor
    public PlayerResult(String playerName, int result) {
        this.playerName = playerName;
        this.result = result;
        this.date = toDate();
    }

    // Getters y setters
    public String toDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        String fechaComoCadena = sdf.format(new Date());
        return fechaComoCadena;
    }
    public String getDate() {
        return date;
    }

    public void String(String date) {
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
