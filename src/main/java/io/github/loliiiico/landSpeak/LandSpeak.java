package io.github.loliiiico.landSpeak;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.loliiiico.landSpeak.lands.LandsHook;
import io.github.loliiiico.landSpeak.voice.VoiceHook;

public final class LandSpeak extends JavaPlugin {

    public static final String VOICE_PLUGIN_ID = "landspeak";

    private boolean landsPresent;
    private LandsHook landsHook;
    private VoiceHook voiceHook;

    @Override
    public void onLoad() {
        // Register Lands flags at the correct lifecycle phase
        Plugin lands = Bukkit.getPluginManager().getPlugin("Lands");
        landsPresent = lands != null; // at onLoad, plugin might not be enabled yet
        if (!landsPresent) return;

        try {
            landsHook = new LandsHook(this);
            landsHook.registerSpeakFlag();
        } catch (Throwable inner) {
            getLogger().severe("Failed to register Lands flags onLoad: " + inner.getMessage());
        }
    }

    @Override
    public void onEnable() {
        detectDependencies();
        if (!landsPresent) {
            getLogger().warning("Lands not found. Flag registration and checks are disabled.");
        }

        // Always attempt to register the voice chat plugin via the service,
        // exactly like the SVC example. The service will be null if voicechat
        // is not present, and our VoiceHook handles logging for that case.
        try {
            voiceHook = new VoiceHook(this, () -> landsHook);
            voiceHook.register();
        } catch (Throwable t) {
            getLogger().severe("Failed to initialize Voice Chat integration: " + t.getMessage());
        }
    }

    @Override
    public void onDisable() {
        // Unregister voice chat plugin hook if present
        try {
            if (voiceHook != null) {
                Bukkit.getServer().getServicesManager().unregister(voiceHook);
                getLogger().info("Unregistered Voice Chat plugin hook.");
            }
        } catch (Throwable t) {
            getLogger().warning("Failed to unregister Voice Chat plugin hook: " + t.getMessage());
        }
    }

    private void detectDependencies() {
        Plugin lands = Bukkit.getPluginManager().getPlugin("Lands");
        landsPresent = lands != null && lands.isEnabled();

        // No need to check voicechat presence here; we load the service directly when registering.
    }
}
