package dev.voidskins;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SkinConfig {
    public static final List<String> SKINS = List.of("void", "ice", "blood", "toxic", "ember");

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<String, String> CHOICES = new HashMap<>();
    private static final Map<String, Float> INDEX = new HashMap<>();

    private SkinConfig() {}

    private static Path file() {
        return FMLPaths.CONFIGDIR.get().resolve("voidskins.json");
    }

    public static void load() {
        CHOICES.clear();
        try {
            Path f = file();
            if (Files.exists(f)) {
                try (Reader r = Files.newBufferedReader(f)) {
                    Map<String, String> m = GSON.fromJson(r, new TypeToken<Map<String, String>>() {}.getType());
                    if (m != null) CHOICES.putAll(m);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        rebuild();
    }

    private static void save() {
        try (Writer w = Files.newBufferedWriter(file())) {
            GSON.toJson(CHOICES, w);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void rebuild() {
        INDEX.clear();
        for (Map.Entry<String, String> e : CHOICES.entrySet()) {
            int idx = SKINS.indexOf(e.getValue()) + 1;
            if (idx > 0) INDEX.put(e.getKey(), (float) idx);
        }
    }

    /** 0 = default, 1..5 = skin index. Read by the item property every frame. */
    public static float index(String itemPath) {
        return INDEX.getOrDefault(itemPath, 0.0f);
    }

    public static String get(String itemPath) {
        return CHOICES.getOrDefault(itemPath, "default");
    }

    public static void set(String itemPath, String skin) {
        if ("default".equals(skin)) CHOICES.remove(itemPath);
        else CHOICES.put(itemPath, skin);
        save();
        rebuild();
    }

    public static void resetAll() {
        CHOICES.clear();
        save();
        rebuild();
    }
}
