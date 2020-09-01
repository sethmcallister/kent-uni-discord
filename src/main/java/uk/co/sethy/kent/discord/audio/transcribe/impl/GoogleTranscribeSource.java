package uk.co.sethy.kent.discord.audio.transcribe.impl;

import org.springframework.stereotype.Component;
import uk.co.sethy.kent.discord.audio.transcribe.TranscribeSource;

@Component
public class GoogleTranscribeSource implements TranscribeSource {
    @Override
    public String transcribeBytes(byte[] raw) {
        return null;
    }
}
