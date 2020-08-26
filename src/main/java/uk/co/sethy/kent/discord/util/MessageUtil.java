package uk.co.sethy.kent.discord.util;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.MessageChannel;

public class MessageUtil {
    public static void sendMessageAndTagMember(Member member, MessageChannel channel, String message) {
        channel.createMessage(String.format("%s: %s", member.getMention(), message)).block();
    }
}
