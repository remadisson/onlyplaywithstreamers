package de.remadisson.opws.mojang;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.UUID;

public class JsonUtils {

    public static HashMap<String, String> getPlayerInJson(UUID uuid) {
        HashMap<String, String> playerProfile = new HashMap<>();

        String replaced = uuid.toString().replace("-", "");
        JsonParser jsonParser = new JsonParser();
        try {
            URL request = new URL("https://api.mojang.com/user/profiles/" + replaced + "/names");
            URLConnection recon = request.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(recon.getInputStream()));

            String input;
            if((input = in.readLine()) != null){
                JsonArray jsonArray = jsonParser.parse(input).getAsJsonArray();
                String slot = jsonArray.get(jsonArray.size()-1).toString();
                JsonObject jsonObject = jsonParser.parse(slot).getAsJsonObject();
                playerProfile.put("id", uuid.toString());
                playerProfile.put("name", jsonObject.get("name").toString().replace("\"", ""));
            }

            in.close();

        } catch (IOException | NullPointerException e) {
            System.out.println("REQUEST FROM '" + uuid + "' returns null!");
            e.printStackTrace();
        }

        return playerProfile;
    }


    public static HashMap<String, String> getPlayerInJson(String name) {
        HashMap<String, String> playerProfile = new HashMap<>();

        JsonParser jsonParser = new JsonParser();
        try {
            URL request = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            URLConnection recon = request.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(recon.getInputStream()));

            String input;
            if((input = in.readLine()) != null){
                JsonObject element = jsonParser.parse(input).getAsJsonObject();
                playerProfile.put("id", element.get("id").toString().replace("\"", ""));
                playerProfile.put("name", element.get("name").toString().replace("\"", ""));
            }

            in.close();

        } catch (IOException | NullPointerException e) {
            System.out.println("REQUEST FROM '" + name + "' returns null!");
            e.printStackTrace();
        }

        return playerProfile;
    }
}
