package android.example.com.videosurveillance;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private VideoView videoView;
    private GestureDetector gestureDetector;
    private View.OnTouchListener gestureListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = findViewById(R.id.video_view);
        gestureDetector = new GestureDetector(this, new SwipeGestureDetector());
        gestureListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };
        videoView.setOnTouchListener(gestureListener);

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video);
        videoView.setVideoURI(uri);
        //videoView.setVideoPath("http://192.168.43.250:8000/video/");

        videoView.start();
    }

    class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            try {
                HashMap<String, Integer> valuesForSwipe = getValuesForSwipe();

                if(Math.abs(e1.getX() - e2.getX()) > Math.abs(e1.getY() - e2.getY())) {
                    // Left swipe
                    if (e1.getX() - e2.getX() > valuesForSwipe.get("swipeMinDistance")
                            && Math.abs(velocityX) > valuesForSwipe.get("swipeThresholdVelocity")) {
                        showToast("LEFT");
                    }
                    // Right swipe
                    if (e2.getX() - e1.getX() > valuesForSwipe.get("swipeMinDistance")
                            && Math.abs(velocityX) > valuesForSwipe.get("swipeThresholdVelocity")) {
                        showToast("RIGHT");
                    }
                }
                else {
                    // Swipe down
                    if (e2.getY() - e1.getY() > valuesForSwipe.get("swipeMinDistance")
                            && Math.abs(velocityY) > valuesForSwipe.get("swipeThresholdVelocity")) {
                        showToast("DOWN");
                    }
                    // Swipe up
                    if (e1.getY() - e2.getY() > valuesForSwipe.get("swipeMinDistance")
                            && Math.abs(velocityX) > valuesForSwipe.get("swipeThresholdVelocity")) {
                        showToast("UP");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public boolean onDown(MotionEvent event) {
            Log.d("tag", "onDown: " + event.toString());
            return true;
        }
    }

    HashMap<String, Integer> getValuesForSwipe() {

        HashMap<String, Integer> swipeValues = new HashMap<>();
        ViewConfiguration vc = ViewConfiguration.get(this);
        swipeValues.put("swipeMinDistance", vc.getScaledPagingTouchSlop());
        swipeValues.put("swipeThresholdVelocity", vc.getScaledMinimumFlingVelocity());
        swipeValues.put("swipeMaxOffPath", vc.getScaledMinimumFlingVelocity());
        return swipeValues;
    }

    private void showToast(String msg) {Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();}
}
