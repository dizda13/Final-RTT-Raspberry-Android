package com.example.dizda.api16_rtt;

import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.net.ServerSocket;
import java.net.Socket;

public class MediaStreamServer implements Runnable {
    static final int frequency = 44100;
    static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    boolean isPlaying;
    int playBufSize;
    Socket connfd;
    ServerSocket sockfd;
    AudioTrack audioTrack;
    private static final String TAG = "MyActivity";
    public static final int SERVERPORT = 8083;
    Context ctx;

    public MediaStreamServer(final Context ctx) {
        playBufSize=AudioTrack.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
        audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, frequency, channelConfiguration, audioEncoding, playBufSize, AudioTrack.MODE_STREAM);
        audioTrack.setStereoVolume(1f, 1f);
        this.ctx=ctx;
    }

    public void stop() {
        isPlaying = false;

    }


    public void setVolume(float lvol, float rvol) {
        audioTrack.setStereoVolume(lvol, rvol);
    }

    @Override
    public void run() {
        byte[] buffer = new byte[playBufSize];
            try { sockfd = new ServerSocket(SERVERPORT); }
            catch (Exception e) {
                e.printStackTrace();
                MainActivity.toast("Port unavailable",ctx);
                return;
            }
            while(true) {
                try {
                    connfd = sockfd.accept();
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.toast("Connection not accepted",ctx);
                    continue;
                }
                MainActivity.toast("Connected",ctx);
                audioTrack.play();
                isPlaying = true;
                while (isPlaying) {
                    int readSize = 0;
                    try {
                        readSize = connfd.getInputStream().read(buffer);
                    } catch (Exception e) {
                        Log.v(TAG, "palo");
                        e.printStackTrace();
                        MainActivity.toast("Closed stream by sender",ctx);
                        break;
                    }
                    audioTrack.write(buffer, 0, readSize);
                }
                audioTrack.stop();
                audioTrack.flush();
                try {
                    connfd.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.toast("Can't close connection!",ctx);
                }
            }
    }
}