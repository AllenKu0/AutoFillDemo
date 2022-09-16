package com.example.autofillex.DataBase;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "account")
public class AccountEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;

    private String password;

    public AccountEntity(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}