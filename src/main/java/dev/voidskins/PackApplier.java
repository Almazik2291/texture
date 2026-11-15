package dev.voidskins;

import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.repository.PackRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/** Writes a folder resource pack into resourcepacks/, enables it and reloads resources. */
public final class PackApplier {
    public static final String PACK_NAME = "VoidSkinsAuto";

    private PackApplier() {}

    public static void apply() {
        Minecraft mc = Minecraft.getInstance();
        try {
            Path dir = mc.getResourcePackDirectory().resolve(PACK_NAME);
            Path items = dir.resolve("assets").resolve("minecraft").resolve("items");
            if (Files.exists(items)) {
                try (Stream<Path> s = Files.list(items)) {
                    for (Path p : s.toList()) Files.deleteIfExists(p);
                }
            }
            Files.createDirectories(items);
            Files.writeString(dir.resolve("pack.mcmeta"),
                    "{\"pack\":{\"pack_format\":46,\"description\":\"Void Skins (auto-generated)\"}}");
            for (Map.Entry<String, String> e : SkinConfig.snapshot().entrySet()) {
                String itemPath = e.getKey();
                String kind = itemPath.endsWith("_sword") ? "sword" : "pickaxe";
                String json = "{\"model\":{\"type\":\"minecraft:model\",\"model\":\"voidskins:item/"
                        + kind + "_" + e.getValue() + "\"}}";
                Files.writeString(items.resolve(itemPath + ".json"), json);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        PackRepository repo = mc.getResourcePackRepository();
        repo.reload();
        String id = "file/" + PACK_NAME;
        if (repo.isAvailable(id) && !repo.getSelectedIds().contains(id)) {
            List<String> selected = new ArrayList<>(repo.getSelectedIds());
            selected.add(id);
            repo.setSelected(selected);
            mc.options.updateResourcePacks(repo);
        } else {
            mc.reloadResourcePacks();
        }
    }
}
