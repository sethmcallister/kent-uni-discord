package uk.co.sethy.kent.discord.audio.transcribe.impl;

import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.co.sethy.kent.discord.audio.transcribe.TranscribeSource;

import java.io.IOException;
import java.util.List;

@Component
public class GoogleTranscribeSource implements TranscribeSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleTranscribeSource.class);

    @Override
    public String transcribeBytes(byte[] raw) {
        try (SpeechClient speechClient = SpeechClient.create()) {
            ByteString rawAudio = ByteString.copyFrom(raw);

            RecognitionConfig config = RecognitionConfig.newBuilder()
                                            .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                                            .setSampleRateHertz(16000)
                                            .setLanguageCode("en-US")
                                            .build();

            RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(rawAudio).build();

            RecognizeResponse response = speechClient.recognize(config, audio);
            List<SpeechRecognitionResult> resultList = response.getResultsList();

            for (SpeechRecognitionResult result : resultList) {
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                return alternative.getTranscript();
            }
        } catch (IOException e) {
            LOGGER.error("GoogleTranscribeSource.transcribeBytes :: Failed to transcribe audio");
        }
        return null;
    }
}
