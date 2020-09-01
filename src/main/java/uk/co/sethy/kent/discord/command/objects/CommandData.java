package uk.co.sethy.kent.discord.command.objects;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.sethy.kent.discord.command.annotations.Command;
import uk.co.sethy.kent.discord.handler.CommandHandler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CommandData {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandData.class);

    private String[] names;
    private String[] flags;
    private List<ParamData> parameters;
    private Method method;

    public CommandData(Method method, Command command, List<ParamData> paramData) {
        this.names = command.names();
        this.flags = command.flags();
        this.parameters = paramData;
        this.method = method;

        LOGGER.debug("CommandData.<init> :: Created new command {} [flags: {}]", this.names, this.flags);
    }

    public void execute(final Member member, final MessageChannel channel, String[] args) {
        final ArrayList<Object> transformedParams = new ArrayList<>();
        transformedParams.add(member);
        transformedParams.add(channel);

        for (int i = 0; i < this.getParameters().size(); ++i) {
            ParamData param = this.getParameters().get(i);

            String passedParam = ((i < args.length) ? args[i] : param.getDefaultValue());

            if (i >= args.length && (param.getDefaultValue() == null || param.getDefaultValue().trim().isEmpty())) {
                // show usage string
                return;
            }
            if (param.isWildcard() && !passedParam.trim().equals(param.getDefaultValue().trim())) {
                passedParam = toString(args, i);
            }
            Object result = CommandHandler.transformParameter(member, passedParam, param.getParameterClass());
            if (result == null) return;
            transformedParams.add(result);
            if (param.isWildcard()) return;
        }

        try {
            LOGGER.debug("CommandData.execute :: {} has executed {} [args: {}]", member.getEffectiveName(), this.names, args);

            // ensure method is accessible
            this.method.setAccessible(true);
            this.method.invoke(null, transformedParams.toArray(new Object[0]));
        } catch (Exception e) {
            LOGGER.error("CommandData.execute :: Failed to execute command", e);
        }
    }

    public static String toString(final String[] args, final int start) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int arg = start; arg < args.length; ++arg) {
            stringBuilder.append(args[arg]).append(" ");
        }
        return stringBuilder.toString().trim();
    }
}
