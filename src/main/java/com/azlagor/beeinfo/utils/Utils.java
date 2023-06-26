package com.azlagor.beeinfo.utils;

import com.azlagor.beeinfo.BeeInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.md_5.bungee.api.ChatColor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

public class Utils {
    public static void loadLang()
    {
        try {
            BufferedReader langReader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(BeeInfo.class.getResourceAsStream("/lang/" + BeeInfo.config.lang + ".json")),
                    StandardCharsets.UTF_8));
            Gson nGson = new Gson();
            Type type = new TypeToken<Map<String, String>>(){}.getType();
            Map<String, String> data = nGson.fromJson(langReader, type);
            for(String key : data.keySet())
            {
                String text = data.get(key);
                text = setHexColor(text);
                data.put(key,text);
            }
            BeeInfo.config.langData = data;
            langReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String setHexColor(String str)
    {
        String[] parts = str.split("[{}]");
        if(!str.contains("{#") || parts.length < 2) return str;
        return ChatColor.of(parts[1]) + parts[2].trim();
    }

    public static String lang(String mod)
    {
        return BeeInfo.config.langData.get(mod);
    }
}
