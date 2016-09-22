import javax.sound.sampled.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by hadoop on 9/14/16.
 */
public class Server implements  Runnable {

    @Override
    public void run() {
        final int frequency = 44100;
        //static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
        //static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
        boolean isPlaying;
        int playBufSize;
        Socket connfd;
        ServerSocket sockfd;
        //final String TAG = "MyActivity";
        final int SERVERPORT = 8087;
        connfd = new Socket();


        Scanner reader = new Scanner(System.in);  // Reading from System.in
        String ip=reader.nextLine();

        AudioFormat format = new AudioFormat(44100, 16, 2, true, false);
        DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine sourceDataLine = null;
        try {
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            sourceDataLine.open();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }


        byte[] buffer = new byte[640];

        try {
            sockfd = new ServerSocket(SERVERPORT);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        while (true) {
            client c=new client(ip);
            try {
                connfd = sockfd.accept();
            } catch (Exception e) {
                e.printStackTrace();
            }
            c.stop();
            System.out.println("Receiving...");
            SourceDataLine finalSourceDataLine = sourceDataLine;
            Socket finalConnfd = connfd;

            FloatControl volumeControl = (FloatControl) finalSourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue(6.0f);
            try {
                finalSourceDataLine.open(format);
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            }
            finalSourceDataLine.start();
            while (true) {
                int readSize = 0;
                try {
                    readSize = finalConnfd.getInputStream().read(buffer);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
                if (readSize == -1)
                    break;
                finalSourceDataLine.write(buffer, 0, readSize);
            }
            System.out.println("Stopped receiving");
            /*try {
                finalConnfd.close();
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        }
    }
}
