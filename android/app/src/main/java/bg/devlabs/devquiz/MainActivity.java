package bg.devlabs.devquiz;

import android.app.ProgressDialog;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {
    public ProgressDialog progressDialog;
    @Bind(R.id.aButton)
    Button aButton;
    @Bind(R.id.bButton)
    Button bButton;
    @Bind(R.id.cButton)
    Button cButton;
    @Bind(R.id.dButton)
    Button dButton;

    private Socket mSocket;
    private String mGameStatus;

    @OnClick(R.id.aButton)
    void onAaClick(View view) {
        sendAnswer(ApiHelper.aAnswer);
    }
    @OnClick(R.id.bButton)
    void onBaClick(View view) {
        sendAnswer(ApiHelper.bAnswer);
    }
    @OnClick(R.id.cButton)
    void onCaClick(View view) {
        sendAnswer(ApiHelper.cAnswer);
    }
    @OnClick(R.id.dButton)
    void onDaClick(View view) {
        sendAnswer(ApiHelper.dAnswer);
    }

    private void sendAnswer(String answer) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        aButton.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.blueButton), PorterDuff.Mode.SRC_ATOP);
        bButton.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.yellowButton), PorterDuff.Mode.SRC_ATOP);
        cButton.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.pinkButton), PorterDuff.Mode.SRC_ATOP);
        dButton.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.raspberryButton), PorterDuff.Mode.SRC_ATOP);
        QuizApplication app = (QuizApplication) getApplication();
        mSocket = app.getSocket();
        progressDialog = ProgressDialog.show(MainActivity.this, "Please wait", "The game will start soon... asd asd", true);
        mSocket.on("status", onQuestion);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.off("start", onQuestion);
    }

// mSocket.on("start", onGameStart);
    private Emitter.Listener onQuestion = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            try {
                mGameStatus = data.getString("status");
            } catch (JSONException e) {
                return;
            }
            if(mGameStatus.equals(ApiHelper.STATUS_OK)){
            }

        }
    };
}
