package com.viper.client.cosmetics;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.viper.client.ViperClient;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CosmeticsManager {

    private static final Gson GSON = new GsonBuilder().create();
    private static final Map<String, PlayerCosmetics> PLAYER_CACHE = new HashMap<>();
    private static Map<String, String> CAPE_TEXTURES = new HashMap<>();
    private static boolean loaded = false;

    public static class PlayerCosmetics {
        public List<String> capes = new ArrayList<>();
        public List<String> hats = new ArrayList<>();
        public List<String> killEffects = new ArrayList<>();
        public String activeCape = null;
        public String activeHat = null;
    }

    private static class CosmeticsData {
        Map<String, PlayerCosmetics> players = new HashMap<>();
        Map<String, String> capeTextures = new HashMap<>();
    }

    public static void load() {
        if (loaded) return;
        loaded = true;
        try {
            InputStream in = CosmeticsManager.class.getResourceAsStream("/assets/viper/cosmetics.json");
            if (in == null) {
                ViperClient.LOGGER.warn("[Cosmetics] cosmetics.json not found");
                return;
            }
            InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
            CosmeticsData data = GSON.fromJson(reader, CosmeticsData.class);
            reader.close();

            if (data != null) {
                if (data.players != null) PLAYER_CACHE.putAll(data.players);
                if (data.capeTextures != null) CAPE_TEXTURES.putAll(data.capeTextures);
                ViperClient.LOGGER.info("[Cosmetics] loaded " + PLAYER_CACHE.size() + " players, " + CAPE_TEXTURES.size() + " capes");
            }
        } catch (Exception e) {
            ViperClient.LOGGER.error("[Cosmetics] load failed", e);
        }
    }

    public static PlayerCosmetics getForPlayer(String username) {
        if (username == null) return null;
        load();
        return PLAYER_CACHE.get(username);
    }

    public static Identifier getCapeTexture(String capeId) {
        load();
        String path = CAPE_TEXTURES.get(capeId);
        if (path == null) return null;
        try {
            String[] parts = path.split(":", 2);
            if (parts.length != 2) return null;
            String namespace = parts[0];
            String texturePath = parts[1];
            if (!texturePath.endsWith(".png")) return null;
            texturePath = texturePath.substring(0, texturePath.length() - 4);
            return Identifier.of(namespace, texturePath);
        } catch (Throwable t) {
            return null;
        }
    }

    public static boolean hasCape(String username) {
        PlayerCosmetics pc = getForPlayer(username);
        return pc != null && pc.activeCape != null && !pc.activeCape.isEmpty();
    }

    public static String getActiveCape(String username) {
        PlayerCosmetics pc = getForPlayer(username);
        if (pc == null) return null;
        return pc.activeCape;
    }
}