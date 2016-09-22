import javax.sound.sampled.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by dizda on 9/3/16.
 */
public class main {

    public static void main(String[] arg){
        Thread t2=new Thread(new Server());
        t2.start();
    }

}
