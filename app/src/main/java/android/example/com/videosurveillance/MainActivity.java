package android.example.com.videosurveillance;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    private VideoView videoView;
    private final String DEBUG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = findViewById(R.id.video_view);

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video);
        videoView.setVideoURI(uri);
        //videoView.setVideoPath("http://192.168.43.250:8000/video/");

        videoView.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case (MotionEvent.ACTION_DOWN): {
                Log.e(DEBUG_TAG, "Action was DOWN");
                return true;
            }
            case (MotionEvent.ACTION_UP): {
                Log.e(DEBUG_TAG, "Action was UP");
                return true;
            }
            default:
                return super.onTouchEvent(event);
        }
    }
}
