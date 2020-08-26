package uk.co.sethy.kent.discord.handler;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import uk.co.sethy.kent.discord.command.annotations.Command;
import uk.co.sethy.kent.discord.command.annotations.Param;
import uk.co.sethy.kent.discord.command.commands.HelpCommand;
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
public class CommandHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandHandler.class);
    private static Map<Class<?>, ParameterTransformer> parameterTransformerMap = new HashMap<>();

    private @Value("${bot.command.prefix:!}") String commandPrefix;

    private @Autowired DiscordHandler discordHandler;

    private List<CommandData> commandDataList;

    @PostConstruct
    public void load() {
        this.commandDataList = new ArrayList<>();

        registerClass(HelpCommand.class);

        discordHandler.getDiscordClient().withGateway(gateway -> {
            Publisher<?> command = gateway.on(MessageCreateEvent.class, event ->
                Mono.just(event.getMessage())
                        .filter(message -> event.getMember().isPresent())
                        .filter(message -> message.getContent().toLowerCase().contains(commandPrefix.toLowerCase()))
                        .flatMap(Message::getChannel)
                        .flatMap(channel -> findAndExecuteCommand(event.getMember().get(), channel, event.getMessage().getContent()))
            );

            return Mono.when(command);
        }).block();
    }

    public void registerClass(Class<?> registeredClass) {
        for (Method method : registeredClass.getMethods()) {
            if (method.getAnnotation(Command.class) != null) {
                method.setAccessible(true);
                registerMethod(method);
            }
        }
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

    public Mono<CommandData> findAndExecuteCommand(Member member, MessageChannel channel, String message) {
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
                    return Mono.just(command);
                } else {
                    i++;
                }
            }
        }
        LOGGER.warn("CommandHandler.findAndExecuteCommand :: Failed to find command meeting message {}", message);
        return Mono.empty();
    }

    public static Object transformParameter(Member member, String parameter, Class<?> transformTo) {
        return transformTo.equals(String.class) ? parameter : parameterTransformerMap.get(transformTo).transform(member, parameter);
    }
}
