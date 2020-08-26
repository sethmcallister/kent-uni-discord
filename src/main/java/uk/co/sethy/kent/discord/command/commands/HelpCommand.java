package uk.co.sethy.kent.discord.command.commands;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.MessageChannel;
import uk.co.sethy.kent.discord.command.annotations.Command;
import uk.co.sethy.kent.discord.util.MessageUtil;

public class HelpCommand {
    @Command(names = {"help"})
    public static void help(Member member, MessageChannel channel) {
        MessageUtil.sendMessageAndTagMember(member, channel, "Test");
    }
}
