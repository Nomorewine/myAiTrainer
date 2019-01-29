package tftrainer.choi.alexander.myaitrainer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView txt_question, txt_answer;
    EditText et_talkbox;
    Button btn_send;

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://211.39.150.232:4422");
        } catch (URISyntaxException e) {}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt_question = (TextView) findViewById(R.id.txt_question);
        txt_answer = (TextView) findViewById(R.id.txt_answer);
        et_talkbox = (EditText) findViewById(R.id.et_talkbox);
        btn_send = (Button) findViewById(R.id.btn_send);

        Log.d("testtest", "START ");
        mSocket.on("receive-response", onNewMessage);
        mSocket.connect();
        Log.d("testtest", "after connect ");
        btn_send.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        Log.d("testtest", "hmmm");
        int id = v.getId();
        switch(id){
            case R.id.btn_send:
                Log.d("testtest", "hello");
                sendMessage();
                break;
            default:
                break;
        }
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d("testtest", " onNewMessage called");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String message;
                    try {
                        message = data.getString("message");
                    } catch (JSONException e) {
                        return;
                    }

                    // add the message to view
                    setAnswer(message);
                }
            });
        }
    };

    private void sendMessage(){
        Log.d("testtest", "sent message");
        String message = et_talkbox.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            return;
        }
        txt_question.setText(message);
        et_talkbox.setText("");
        mSocket.emit("send-question", message);
    }

    private void setAnswer(String answer){
        Log.d("testtest", "set answer ");
        txt_answer.setText(answer);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off("receive-response", onNewMessage);
    }

}
