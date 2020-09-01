package uk.co.sethy.kent.discord.util;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class MessageUtil {
    public static void sendMessageAndTagMember(Member member, TextChannel channel, String message) {
        MessageAction messageAction = channel.sendMessage(String.format("%s: %s", member.getAsMention(), message));
        messageAction.queue();
    }
}
