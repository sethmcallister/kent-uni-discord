package uk.co.sethy.kent.discord.command.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.co.sethy.kent.discord.command.annotations.Command;
import uk.co.sethy.kent.discord.command.annotations.Param;
import uk.co.sethy.kent.discord.handler.DiscordHandler;
import uk.co.sethy.kent.discord.handler.GuildAudioHandler;
import uk.co.sethy.kent.discord.util.MessageUtil;

@Component
public class PlayCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayCommand.class);

    @Command(names = "play")
    public static void play(Member member, TextChannel textChannel, @Param(name = "trackUrl") String content) {
        GuildAudioHandler guildAudioHandler = GuildAudioHandler.of(member.getGuild().getIdLong());

        DiscordHandler.AUDIO_PLAYER_MANAGER.loadItemOrdered(guildAudioHandler, content, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                MessageUtil.sendMessageAndTagMember(member, textChannel, String.format("Added %s to the queue", audioTrack.getInfo().title));

                guildAudioHandler.getScheduler().play(audioTrack);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) { }

            @Override
            public void noMatches() {
                MessageUtil.sendMessageAndTagMember(member, textChannel, String.format("Could not find any content meeting %s", content));
            }

            @Override
            public void loadFailed(FriendlyException e) {
                LOGGER.error("PlayCommand.play :: Failed to load track", e);
            }
        });
    }
}
