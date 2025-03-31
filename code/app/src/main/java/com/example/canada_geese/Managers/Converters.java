package com.example.canada_geese.Managers;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class Converters {
    @TypeConverter
    public static String fromList(List<String> list) {
        return new Gson().toJson(list);
    }

    @TypeConverter
    public static List<String> toList(String data) {
        return new Gson().fromJson(data, new TypeToken<List<String>>() {}.getType());
    }
}