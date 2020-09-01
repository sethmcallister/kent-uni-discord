package uk.co.sethy.kent.discord.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import lombok.Getter;
import net.dv8tion.jda.api.audio.AudioSendHandler;

import java.nio.ByteBuffer;

@Getter
public final class LavaPlayerAudioProvider implements AudioSendHandler {
    private final AudioPlayer player;
    private final ByteBuffer buffer;
    private final MutableAudioFrame frame;

    public LavaPlayerAudioProvider(final AudioPlayer player) {
        this.player = player;
        this.buffer = ByteBuffer.allocate(1024);

        frame = new MutableAudioFrame();
        frame.setBuffer(getBuffer());
    }

    @Override
    public boolean canProvide() {
        return this.player.provide(frame);
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        // flip to make it a read buffer
        buffer.flip();
        return buffer;
    }

    @Override
    public boolean isOpus() {
        return true;
    }
}
