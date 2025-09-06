package me.zimzaza4.geysercolorreplacer.config;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

public class ColorMappings {
    public static final Gson GSON = new GsonBuilder().create();

    Map<TextColor, TextColor> mappings = new HashMap<>();

    public void load(Path file) {
        mappings.clear();
        if (!Files.exists(file)) {
            try {
                Files.createDirectories(file.getParent());
                Map<String, String> map = new HashMap<>();
                map.put("#FFDE59", "e");
                map.put("#7DDA58", "e");
                Files.writeString(file, GSON.toJson(map), StandardOpenOption.CREATE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            Map<String, String> rawMappings = GSON.fromJson(Files.readString(file), new TypeToken<Map<String, String>>() {
            }.getType());
            for (Map.Entry<String, String> entry : rawMappings.entrySet()) {
                mappings.put(TextColor.fromHexString(entry.getKey()), LegacyComponentSerializer.legacyAmpersand().deserialize("&" + entry.getValue()).color());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<TextColor, TextColor> getMappings() {
        return mappings;
    }
}
