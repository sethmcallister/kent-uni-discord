package uk.co.sethy.kent.discord.audio.transcribe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.co.sethy.kent.discord.audio.transcribe.impl.GoogleTranscribeSource;

@Service
public class TranscribeService {
    private @Value("${discord.bot.transcribe.source:GOOGLE}") TranscribeSource.Type transcribeSourceType;

    private @Autowired GoogleTranscribeSource googleTranscribeSource;

    public TranscribeSource getInstance() {
        switch (transcribeSourceType) {
            // Currently only one implemented source
            default:
                return googleTranscribeSource;
        }
    }
}
