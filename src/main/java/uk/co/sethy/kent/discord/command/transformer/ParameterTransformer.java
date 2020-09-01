package uk.co.sethy.kent.discord.command.transformer;


import net.dv8tion.jda.api.entities.Member;

public abstract class ParameterTransformer<T> {
    public abstract T transform(final Member member, String parameter);
}
