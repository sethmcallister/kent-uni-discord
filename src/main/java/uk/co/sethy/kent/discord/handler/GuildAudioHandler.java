package uk.co.sethy.kent.discord.handler;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import lombok.Getter;
import org.springframework.stereotype.Component;
import uk.co.sethy.kent.discord.audio.AudioTrackScheduler;
import uk.co.sethy.kent.discord.audio.LavaPlayerAudioProvider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Getter
public class GuildAudioHandler {
    private static final Map<Long, GuildAudioHandler> MANAGERS = new ConcurrentHashMap<>();

    public static GuildAudioHandler of(long id) {
        if (MANAGERS.containsKey(id)) return MANAGERS.get(id);

        GuildAudioHandler guildAudioHandler = new GuildAudioHandler();
        MANAGERS.put(id, guildAudioHandler);
        return guildAudioHandler;
    }

    private final AudioPlayer player;
    private final AudioTrackScheduler scheduler;
    private final LavaPlayerAudioProvider provider;

    private GuildAudioHandler() {
        this.player = DiscordHandler.AUDIO_PLAYER_MANAGER.createPlayer();
        this.scheduler = new AudioTrackScheduler(this.player);
        this.provider = new LavaPlayerAudioProvider(this.player);

        this.player.addListener(this.scheduler);
    }
}
