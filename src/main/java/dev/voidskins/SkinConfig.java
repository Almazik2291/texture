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
    public static final String[] MATS = {"wooden", "stone", "iron", "golden", "diamond", "netherite"};
    public static final String[] KINDS = {"sword", "pickaxe"};

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<String, String> CHOICES = new HashMap<>();

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
    }

    private static void save() {
        try (Writer w = Files.newBufferedWriter(file())) {
            GSON.toJson(CHOICES, w);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> snapshot() {
        return new HashMap<>(CHOICES);
    }

    public static void replaceAll(Map<String, String> newChoices) {
        CHOICES.clear();
        for (Map.Entry<String, String> e : newChoices.entrySet()) {
            if (SKINS.contains(e.getValue())) CHOICES.put(e.getKey(), e.getValue());
        }
        save();
    }
}
