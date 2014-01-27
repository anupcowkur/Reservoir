package com.anupcowkur.reservoirsample;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.anupcowkur.reservoir.Reservoir;
import com.anupcowkur.reservoir.ReservoirGetCallback;
import com.anupcowkur.reservoir.ReservoirPutCallback;

public class MainActivity extends Activity {

    private static final String TEST_STRING = "my test string";
    private static final String KEY = "myKey";
    private TextView tv_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_status = (TextView) findViewById(R.id.tv_status);

        putInReservoir();

    }

    private void putInReservoir() {

        TestClass testPutObject = new TestClass();

        testPutObject.setTestString(TEST_STRING);

        Reservoir.putAsync(KEY, testPutObject, new ReservoirPutCallback() {
            @Override
            public void onComplete(Exception e) {
                if (e == null)
                    getFromReservoir(); //async put call completed, execute get call.
                else
                    tv_status.setText(e.getMessage()); //failure
            }
        });

    }

    private void getFromReservoir() {

        Reservoir.getAsync(KEY, TestClass.class, new ReservoirGetCallback<TestClass>() {
            @Override
            public void onComplete(Exception e, TestClass testGetObject) {
                if (e == null && testGetObject.getTestString().equals(TEST_STRING)) {
                    tv_status.setText(getString(R.string.success)); //success!
                } else
                    tv_status.setText(e.getMessage()); //failure.
            }

        });

    }

}
