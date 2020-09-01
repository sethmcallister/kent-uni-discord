package uk.co.sethy.kent.discord.command.commands;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.sethy.kent.discord.command.annotations.Command;
import uk.co.sethy.kent.discord.handler.DiscordHandler;
import uk.co.sethy.kent.discord.util.MessageUtil;

import javax.annotation.PostConstruct;

@Component
public class SummonCommand extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(SummonCommand.class);

    private @Autowired DiscordHandler discordHandler;

    @PostConstruct
    public void load() {
        this.discordHandler.registerListener(this);
    }

    @Command(names = {"summon"})
    public static void summon(Member member, TextChannel channel) {
        GuildVoiceState voiceState;
        if (((voiceState = member.getVoiceState())) == null || !voiceState.inVoiceChannel()) {
            MessageUtil.sendMessageAndTagMember(member, channel, "You must be in a voice channel to summon the bot");
            return;
        }
        VoiceChannel voiceChannel = voiceState.getChannel();
        if (voiceChannel == null) return;

        Guild guild;
        if ((guild = voiceChannel.getGuild()) == null) return;

        AudioManager audioManager = guild.getAudioManager();

        audioManager.openAudioConnection(voiceChannel);
    }

    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {
        VoiceChannel channelLeft = event.getChannelLeft();
        // If bot isn't connected to any channel then ignore
        if (event.getGuild().getAudioManager().getConnectedChannel() == null) return;

        // If the bot isn't connected to that channel then ignore
        String channelId;
        if ((channelId = event.getGuild().getAudioManager().getConnectedChannel().getId()) != null
                && channelId.equals(channelLeft.getId()))
        // If only 1 user is left, then must be the bot
        if (channelLeft.getMembers().size() > 1) return;

        event.getGuild().getAudioManager().closeAudioConnection();
    }
}
