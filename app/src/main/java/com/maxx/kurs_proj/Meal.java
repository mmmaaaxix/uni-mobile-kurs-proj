package com.maxx.kurs_proj;

import java.io.Serializable;

public class Meal implements Serializable {
    private long _id;
    private String _name;
    private String _instructions;
    private String _imgUrl;

    public Meal(long id, String name, String instructions, String imgUrl) {
        _id = id;
        _name = name;
        _instructions = instructions;
        _imgUrl = imgUrl;
    }

    public long GetId() {return _id;}
    public String GetName() {return _name;}
    public String GetInstructions() {return _instructions;}
    public String GetImgUrl() {return _imgUrl;}
}
