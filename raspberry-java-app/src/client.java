import javax.sound.sampled.*;
import java.awt.*;
import java.lang.annotation.Target;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by hadoop on 9/14/16.
 */
public class client implements Runnable {
    static final int frequency = 44100;
    //static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    //static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    boolean isRecording;
    int recBufSize;
    public static final int SERVERPORT = 8083;
    //ServerSocket sockfd;
    Socket connfd;
    //AudioRecord audioRecord;
    private static final String TAG = "MyActivity";

    public client(String ip){
        //String ip="192.168.31.170";

        AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
        DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, format);

        TargetDataLine targetDataLine = null;
        try {
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            targetDataLine.open();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        TargetDataLine finalTargetDataLine = targetDataLine;
        new Thread() {
            byte[] buffer = new byte[640];
            public void run() {
                try {
                    connfd = new Socket(ip, SERVERPORT);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                finalTargetDataLine.start();
                isRecording = true;
                System.out.println("Transmitting...");
                while (isRecording) {
                    int readSize = finalTargetDataLine.read(buffer, 0, 640);
                    try {
                        connfd.getOutputStream().write(buffer, 0, readSize);
                    } catch (Exception e) {
                        //e.printStackTrace();
                        System.out.println("Transmitting stopped");
                        break;
                    }
                }
                finalTargetDataLine.stop();
                finalTargetDataLine.flush();
                finalTargetDataLine.close();
                try {
                    connfd.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void run() {

    }

    void stop(){
        isRecording=false;
    }
}
