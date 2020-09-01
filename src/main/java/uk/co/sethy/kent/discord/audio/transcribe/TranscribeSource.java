package uk.co.sethy.kent.discord.audio.transcribe;

public interface TranscribeSource {
    String transcribeBytes(byte[] raw);

    enum Type {
        GOOGLE
    }
}
