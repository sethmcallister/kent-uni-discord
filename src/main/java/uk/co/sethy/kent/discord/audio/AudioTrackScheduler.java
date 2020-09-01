package uk.co.sethy.kent.discord.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import lombok.Getter;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

@Getter
public final class AudioTrackScheduler extends AudioEventAdapter {
    private final Queue<AudioTrack> audioQueue;
    private final AudioPlayer player;

    public AudioTrackScheduler(final AudioPlayer audioPlayer) {
        this.audioQueue = new LinkedBlockingQueue<>();
        this.player = audioPlayer;
    }


    public boolean play(AudioTrack audioTrack) {
        boolean playing = player.startTrack(audioTrack, true);

        if (!playing) {
            audioQueue.offer(audioTrack);
        }
        return playing;
    }

    public void skip() {
        player.startTrack(audioQueue.poll(), false);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            skip();
        }
    }
}
