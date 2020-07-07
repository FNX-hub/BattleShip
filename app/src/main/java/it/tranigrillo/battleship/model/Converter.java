package it.tranigrillo.battleship.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class Converter {
    public ShipMatrix matrixFromString(String value) {
        Type listType = new TypeToken<ShipMatrix>(){}.getType();
        return new Gson().fromJson(value, listType);
    }

    public List<Integer> listFromString(String value) {
        Type listType = new TypeToken<List<Integer>>(){}.getType();
        return new Gson().fromJson(value, listType);
    }

    public String fromArrayLisr(ShipMatrix shipMatrix) {
        if (!shipMatrix.getFleet().isEmpty()) {
            Gson gson = new Gson();
            String json = gson.toJson(shipMatrix);
            return json;
        }
        return null;
    }

    public String fromArrayLisr(List<Integer> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}
