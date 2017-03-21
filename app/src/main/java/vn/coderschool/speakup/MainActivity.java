package vn.coderschool.speakup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;
import com.sinch.android.rtc.video.VideoCallListener;
import com.sinch.android.rtc.video.VideoController;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SinchClient sinchClient;
    private CallClient callClient;

    RelativeLayout remoteView;
    RelativeLayout myPreview;

    RelativeLayout viewContainer;

    private EditText etUsername, etPartnerUsername;
    private Button btnSet, btnCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        remoteView = (RelativeLayout) findViewById(R.id.partner_view);
        myPreview = (RelativeLayout) findViewById(R.id.user_view);

        viewContainer = (RelativeLayout) findViewById(R.id.view_container);

        etUsername = (EditText) findViewById(R.id.username);
        etPartnerUsername = (EditText) findViewById(R.id.partner_userName);

        btnSet = (Button) findViewById(R.id.btnSet);
        btnCall = (Button) findViewById(R.id.btnCall);

        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                configUser(etUsername.getText().toString());
            }
        });

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeCall(etPartnerUsername.getText().toString());
            }
        });
    }

    void configUser(String userId) {
        sinchClient = Sinch.getSinchClientBuilder().context(getApplicationContext())
                .applicationKey("420799df-c501-4c8c-bc3b-4e2b8f8e4946")
                .applicationSecret("dKXg2GvUx0SDLqpdzH6r9Q==")
                .environmentHost("sandbox.sinch.com")
                .userId(userId)
                .build();

        sinchClient.setSupportCalling(true);
        sinchClient.startListeningOnActiveConnection();

        sinchClient.start();

        callClient = sinchClient.getCallClient();
        callClient.addCallClientListener(new CallClientListener() {
            @Override
            public void onIncomingCall(CallClient callClient, Call call) {
                Toast.makeText(MainActivity.this, "Some one calling...", Toast.LENGTH_SHORT).show();
                configCallListener(call);
                call.answer();
            }
        });

        Toast.makeText(this, "Your id is " + userId, Toast.LENGTH_SHORT).show();
    }

    void makeCall(String partnerId) {

        Toast.makeText(this, "Start making call to " + partnerId, Toast.LENGTH_SHORT).show();
        Call call = callClient.callUserVideo(partnerId);
        configCallListener(call);
    }

    private void configCallListener(Call call) {
        call.addCallListener(new VideoCallListener() {
            @Override
            public void onVideoTrackAdded(Call call) {
                VideoController vc = sinchClient.getVideoController();

                myPreview.addView(vc.getLocalView());
                remoteView.addView(vc.getRemoteView());
            }

            @Override
            public void onCallProgressing(Call call) {
                Log.d("onCallProgressing", "...");
            }

            @Override
            public void onCallEstablished(Call call) {
                Toast.makeText(MainActivity.this, "Call accepted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCallEnded(Call call) {

            }

            @Override
            public void onShouldSendPushNotification(Call call, List<PushPair> list) {

            }
        });
    }


}
