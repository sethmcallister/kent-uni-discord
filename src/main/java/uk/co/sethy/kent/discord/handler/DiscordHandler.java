package uk.co.sethy.kent.discord.handler;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import javax.security.auth.login.LoginException;

@Service
@Getter
public class DiscordHandler {
    public static final AudioPlayerManager AUDIO_PLAYER_MANAGER;
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordHandler.class);

    private @Value("${discord.bot.token}") String discordBotToken;

    private JDA client;

    @PostConstruct
    public void load() {
        JDABuilder builder = JDABuilder.createDefault(discordBotToken);

        try {
            client = builder.build();
        } catch (LoginException e) {
            LOGGER.error("DiscordHandler.load :: Failed to build jda builder");
        }
    }

    public void registerListener(ListenerAdapter listenerAdapter) {
        client.addEventListener(listenerAdapter);
    }

    static {
        AUDIO_PLAYER_MANAGER = new DefaultAudioPlayerManager();

        // Register audio manager
        AudioSourceManagers.registerRemoteSources(AUDIO_PLAYER_MANAGER);
        AudioSourceManagers.registerLocalSource(AUDIO_PLAYER_MANAGER);
    }
}
