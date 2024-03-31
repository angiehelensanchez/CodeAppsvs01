package com.example.codeappsvs01;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PlayerResult {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "player_name")
    public String playerName;

    @ColumnInfo(name = "result")
    public int result;

    // Constructor
    public PlayerResult(String playerName, int result) {
        this.playerName = playerName;
        this.result = result;
    }
    // Getters y setters
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
