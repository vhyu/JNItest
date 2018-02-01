package com.jiagutech.jnitest;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import static com.jiagutech.jnitest.R.*;


public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("android_runtime");
    }

    static {
        System.loadLibrary("log");
    }

    static {
        System.loadLibrary("nativehelper");
    }

    static {
        System.loadLibrary("cutils");
    }

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(id.sample_text);
        tv.setText("ashdjshdjhs");

        //构建一个Java类
        InputPen myInputPen = InputPen.getInstance();
        myInputPen.start();
    }
}
