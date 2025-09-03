package io.github.loliiiico.landSpeak.voice;

import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import io.github.loliiiico.landSpeak.LandSpeak;
import io.github.loliiiico.landSpeak.lands.LandsHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Supplier;

public class VoiceHook implements VoicechatPlugin {

    private final LandSpeak plugin;
    private final Supplier<LandsHook> landsSupplier;
    private static final long CACHE_MILLIS = 3000L;

    private static final class CacheEntry {
        final boolean allowed;
        final long expiry;

        CacheEntry(boolean allowed, long expiry) {
            this.allowed = allowed;
            this.expiry = expiry;
        }
    }

    private final java.util.concurrent.ConcurrentHashMap<java.util.UUID, CacheEntry> speakCache = new java.util.concurrent.ConcurrentHashMap<>();

    public VoiceHook(LandSpeak plugin, Supplier<LandsHook> landsSupplier) {
        this.plugin = plugin;
        this.landsSupplier = landsSupplier;
    }

    public void register() {
        BukkitVoicechatService service = Bukkit.getServer().getServicesManager().load(BukkitVoicechatService.class);
        if (service == null) {
            plugin.getLogger().warning("BukkitVoicechatService not available; skipping voice hook.");
            return;
        }
        service.registerPlugin(this);
        plugin.getLogger().info("Registered Voice Chat plugin hook.");
    }

    @Override
    public String getPluginId() {
        return LandSpeak.VOICE_PLUGIN_ID;
    }

    @Override
    public void initialize(VoicechatApi api) {
        // Keep for future API usage; event registration happens in registerEvents()
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        // Register with higher priority to ensure early cancellation
        registration.registerEvent(MicrophonePacketEvent.class, this::onMicrophonePacket, 1000);
        plugin.getLogger().info("Voice Chat events registered (MicrophonePacketEvent)");
    }

    private void onMicrophonePacket(MicrophonePacketEvent event) {
        try {
            UUID uuid = event.getSenderConnection().getPlayer().getUuid();
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                return;
            }
            LandsHook lands = landsSupplier.get();
            if (lands == null) {
                return; // no lands, do not block
            }
            long now = System.currentTimeMillis();
            CacheEntry entry = speakCache.get(player.getUniqueId());
            boolean cacheHit = entry != null && entry.expiry > now;
            boolean allowed = cacheHit ? entry.allowed : lands.canSpeak(player);
            if (!cacheHit) {
                speakCache.put(player.getUniqueId(), new CacheEntry(allowed, now + CACHE_MILLIS));
            }

            if (!allowed) {
                event.cancel();
                // Only send denial message when we refresh the cache (i.e., once per 3s)
                if (!cacheHit) {
                    player.sendMessage("§c你在当前区域被塞上了口球");
                }
            }
        } catch (Throwable t) {
            plugin.getLogger().warning("Voice event handling error: " + t.getMessage());
        }
    }

}
