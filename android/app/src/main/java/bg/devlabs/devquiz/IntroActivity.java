package bg.devlabs.devquiz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class IntroActivity extends AppCompatActivity {
    public ProgressDialog progressDialog;
    @Bind(R.id.nameEditText)
    EditText nameEditText;
    @Bind(R.id.okButton)
    Button okButton;

    private Socket mSocket;
    private String mStatus;

    @OnClick(R.id.okButton)
    void onClick(View view) {
        registerToServer();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        ButterKnife.bind(this);
        okButton.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.blueButton), PorterDuff.Mode.SRC_ATOP);
        QuizApplication app = (QuizApplication) getApplication();
        mSocket = app.getSocket();
    }

    private void registerToServer() {
        String name = nameEditText.getText().toString();
        if (name.length() >= 1) {
            attemptRegister(name);
        }
    }

    private void attemptRegister(String name) {
        progressDialog = ProgressDialog.show(IntroActivity.this, "Please wait", "The game will start soon... asd asd", true);
        mSocket.emit("name", name);
        mSocket.on("status", onStatus);
    }



    private void startMainActivity() {
        startActivity(new Intent(IntroActivity.this, MainActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.off("status", onStatus);
    }


    private Emitter.Listener onStatus = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            try {
                mStatus = data.getString("status");
            } catch (JSONException e) {
                return;
            }
            if(mStatus.equals(ApiHelper.STATUS_OK)){
                progressDialog.dismiss();
                startMainActivity();
            }
        }
    };
}
