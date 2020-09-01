package uk.co.sethy.kent.discord.handler;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.co.sethy.kent.discord.command.annotations.Command;
import uk.co.sethy.kent.discord.command.annotations.Param;
import uk.co.sethy.kent.discord.command.commands.HelpCommand;
import uk.co.sethy.kent.discord.command.commands.PlayCommand;
import uk.co.sethy.kent.discord.command.commands.SummonCommand;
import uk.co.sethy.kent.discord.command.objects.CommandData;
import uk.co.sethy.kent.discord.command.objects.ParamData;
import uk.co.sethy.kent.discord.command.transformer.ParameterTransformer;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CommandHandler extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandHandler.class);
    private static Map<Class<?>, ParameterTransformer> parameterTransformerMap = new HashMap<>();

    private @Value("${bot.command.prefix:!}") String commandPrefix;

    private @Autowired DiscordHandler discordHandler;

    private List<CommandData> commandDataList;

    @PostConstruct
    public void load() {
        this.commandDataList = new ArrayList<>();

        discordHandler.registerListener(this);

        registerClass(HelpCommand.class);
        registerClass(SummonCommand.class);
        registerClass(PlayCommand.class);
    }

    public void registerClass(Class<?> registeredClass) {
        for (Method method : registeredClass.getMethods()) {
            if (method.getAnnotation(Command.class) != null) {
                method.setAccessible(true);
                registerMethod(method);
            }
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (!e.getMessage().getContentRaw().startsWith(commandPrefix)) return;

        findAndExecuteCommand(e.getMember(), e.getChannel(), e.getMessage().getContentRaw());
    }

    private void registerMethod(Method method) {
        Command command = method.getAnnotation(Command.class);
        List<ParamData> paramData = new ArrayList<>();

        for (int i = 2; i < method.getParameterTypes().length; i++) {
            Param param = null;
            for (Annotation annotation : method.getParameterAnnotations()[i]) {
                if (annotation instanceof Param) {
                    param = (Param) annotation;
                    break;
                }
            }
            if (param == null) return;
            paramData.add(new ParamData(method.getParameterTypes()[i], param));
        }

        commandDataList.add(new CommandData(method, command, paramData));
    }

    public CommandData findAndExecuteCommand(Member member, MessageChannel channel, String message) {
        for (CommandData command : this.commandDataList) {
            String[] names = command.getNames();
            int length = names.length;

            int i = 0;
            while (i < length) {
                String alias = names[i];
                String messageString = message.substring(commandPrefix.length()).toLowerCase() + " ";
                String aliasString = alias.toLowerCase() + " ";
                if (messageString.startsWith(aliasString)) {
                    String[] args = new String[0];
                    if (message.length() > alias.length() + 2) {
                        args = message.substring(alias.length() + 2).split(" ");
                    }

                    command.execute(member, channel, args);
                    return command;
                } else {
                    i++;
                }
            }
        }
        LOGGER.warn("CommandHandler.findAndExecuteCommand :: Failed to find command meeting message {}", message);
        return null;
    }

    public static Object transformParameter(Member member, String parameter, Class<?> transformTo) {
        return transformTo.equals(String.class) ? parameter : parameterTransformerMap.get(transformTo).transform(member, parameter);
    }
}
