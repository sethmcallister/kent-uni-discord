package uk.co.sethy.kent.discord.handler;

import discord4j.core.DiscordClient;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Getter
public class DiscordHandler {
    private @Value("${discord.bot.token}") String discordBotToken;

    private DiscordClient discordClient;

    @PostConstruct
    public void load() {
        discordClient = DiscordClient.create(discordBotToken);
    }
}
