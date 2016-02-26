package bg.devlabs.devquiz;

import android.app.Application;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by Simona Stoyanova on 2/26/2016.
 * Dev Labs
 * simona@devlabs.bg
 */
public class QuizApplication extends Application {

    public static String CHAT_SERVER_URL = "http://192.168.10.203:3000";
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(CHAT_SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSocket.connect();
    }

    public Socket getSocket() {
        return mSocket;
    }
}
