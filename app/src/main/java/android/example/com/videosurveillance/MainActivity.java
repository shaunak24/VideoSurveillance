package android.example.com.videosurveillance;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private WebView videoView;
    private GestureDetector gestureDetector;
    private View.OnTouchListener gestureListener;
    private String direction;
    private String motion;
    private Boolean flag_up;
    private Boolean flag_left;
    private Boolean flag_down;
    private Boolean flag_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        direction = "s";
        motion = "s";
        flag_up = true;
        flag_left = true;
        flag_down = true;
        flag_right = true;
        videoView = findViewById(R.id.video_view);
        gestureDetector = new GestureDetector(this, new SwipeGestureDetector());
        gestureListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };
        videoView.setOnTouchListener(gestureListener);
        videoView.setWebViewClient(new myWebClient());
        videoView.getSettings().setJavaScriptEnabled(true);
        videoView.loadUrl("http://192.168.43.100:8000/stream.mjpg");

//        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video);
//        videoView.setVideoURI(uri);
//        //"http://192.168.43.100:8000/stream.mjpg"
//        //"http://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4"
//        videoView.setVideoPath("http://192.168.43.100:8000/stream.mjpg");
//        videoView.start();
//        videoView.setMode(MjpegView.MODE_FIT_WIDTH);
//        videoView.setAdjustHeight(true);
//        videoView.setUrl("http://192.168.43.100:8000/stream.mjpg");
//        videoView.startStream();
    }

    class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            try {
                HashMap<String, Integer> valuesForSwipe = getValuesForSwipe();

                if (Math.abs(e1.getX() - e2.getX()) > Math.abs(e1.getY() - e2.getY())) {
                    // Left swipe
                    if (e1.getX() - e2.getX() > valuesForSwipe.get("swipeMinDistance")
                            && Math.abs(velocityX) > valuesForSwipe.get("swipeThresholdVelocity")) {
                        showToast("LEFT");
                        if (flag_left) {
                            direction = "l";
                        } else {
                            direction = "s";
                        }
                        flag_left = !flag_left;
                    }
                    // Right swipe
                    if (e2.getX() - e1.getX() > valuesForSwipe.get("swipeMinDistance")
                            && Math.abs(velocityX) > valuesForSwipe.get("swipeThresholdVelocity")) {
                        showToast("RIGHT");
                        if (flag_right) {
                            direction = "r";
                        } else {
                            direction = "s";
                        }
                        flag_right = !flag_right;
                    }
                } else {
                    // Swipe down
                    if (e2.getY() - e1.getY() > valuesForSwipe.get("swipeMinDistance")
                            && Math.abs(velocityY) > valuesForSwipe.get("swipeThresholdVelocity")) {
                        showToast("DOWN");
                        if (flag_down) {
                            motion = "b";
                        } else {
                            motion = "s";
                        }
                        flag_down = !flag_down;
                    }
                    // Swipe up
                    if (e1.getY() - e2.getY() > valuesForSwipe.get("swipeMinDistance")
                            && Math.abs(velocityX) > valuesForSwipe.get("swipeThresholdVelocity")) {
                        showToast("UP");
                        if (flag_up) {
                            motion = "f";
                        } else {
                            motion = "s";
                        }
                        flag_up = !flag_up;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // send direction, motion coordinates
            sendCoordinates();
            return false;
        }

        @Override
        public boolean onDown(MotionEvent event) {
            Log.d("tag", "onDown: " + event.toString());
            return true;
        }
    }

    private void sendCoordinates() {
        String url = "http://192.168.43.250:8000/control/values/";
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("direction", direction)
                .addFormDataPart("motion", motion)
                .build();

        Call call = sendHttpPostRequest(url, requestBody);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
            }
        });
    }

    HashMap<String, Integer> getValuesForSwipe() {

        HashMap<String, Integer> swipeValues = new HashMap<>();
        ViewConfiguration vc = ViewConfiguration.get(this);
        swipeValues.put("swipeMinDistance", vc.getScaledPagingTouchSlop());
        swipeValues.put("swipeThresholdVelocity", vc.getScaledMinimumFlingVelocity());
        swipeValues.put("swipeMaxOffPath", vc.getScaledMinimumFlingVelocity());
        return swipeValues;
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public static Call sendHttpPostRequest(String url, RequestBody requestBody)
    {
        try
        {
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder().url(url).post(requestBody).build();
            return okHttpClient.newCall(request);
        }
        catch (Exception e)
        {
            Log.e("OkHttpUtils", e.toString());
            return null;
        }
    }
    public class myWebClient extends WebViewClient
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            view.loadUrl(url);
            return true;
        }
    }
}
