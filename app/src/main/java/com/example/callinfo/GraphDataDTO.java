package com.example.callinfo;

import java.io.Serializable;

public class GraphDataDTO implements Serializable {
    String date;
    int count;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
