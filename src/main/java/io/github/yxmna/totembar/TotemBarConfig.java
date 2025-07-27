package io.github.yxmna.totembar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class TotemBarConfig {
    public static int yOffset = 0;

    public static boolean enabled = true;

    public enum RenderMode {
        INVENTORY_ONLY,
        COMBINED
    }
    public static RenderMode renderMode = RenderMode.COMBINED;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Path.of("config", "totem-bar.json");

    public static void load() {
        try {
            if (!Files.exists(CONFIG_PATH)) {
                save();
                return;
            }

            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                ConfigData data = GSON.fromJson(reader, ConfigData.class);
                if (data != null) {
                    enabled = data.enabled;
                    yOffset = data.yOffset;
                    renderMode = data.renderMode != null ? data.renderMode : RenderMode.COMBINED;
                }
            }
        } catch (IOException e) {
            System.err.println("[TotemBar] Failed to load config: " + e.getMessage());
        }
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                ConfigData data = new ConfigData(enabled, yOffset, renderMode);
                GSON.toJson(data, writer);
            }
        } catch (IOException e) {
            System.err.println("[TotemBar] Failed to save config: " + e.getMessage());
        }
    }

    private static class ConfigData {
        boolean enabled;
        int yOffset;
        RenderMode renderMode;

        ConfigData(boolean enabled, int yOffset, RenderMode renderMode) {
            this.enabled = enabled;
            this.yOffset = yOffset;
            this.renderMode = renderMode;
        }
    }
}
