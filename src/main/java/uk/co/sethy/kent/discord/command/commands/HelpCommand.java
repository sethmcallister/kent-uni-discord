package uk.co.sethy.kent.discord.command.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import uk.co.sethy.kent.discord.command.annotations.Command;
import uk.co.sethy.kent.discord.util.MessageUtil;

public class HelpCommand {
    @Command(names = {"help"})
    public static void help(Member member, TextChannel channel) {
        MessageUtil.sendMessageAndTagMember(member, channel, "Test");
    }
}
