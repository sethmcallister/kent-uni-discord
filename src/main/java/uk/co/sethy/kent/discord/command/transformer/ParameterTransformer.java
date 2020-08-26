package uk.co.sethy.kent.discord.command.transformer;

import discord4j.core.object.entity.Member;

public abstract class ParameterTransformer<T> {
    public abstract T transform(final Member member, String parameter);
}
