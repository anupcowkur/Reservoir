package com.anupcowkur.reservoirsample;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.anupcowkur.reservoir.Reservoir;
import com.anupcowkur.reservoir.ReservoirGetCallback;
import com.anupcowkur.reservoir.ReservoirPutCallback;

public class MainActivity extends ActionBarActivity {

    private static final String TEST_STRING = "my test string";
    private static final String KEY = "myKey";
    private TextView tv_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        tv_status = (TextView) findViewById(R.id.tv_status);

        putInReservoir();

    }

    private void putInReservoir() {

        TestClass testPutObject = new TestClass();

        testPutObject.setTestString(TEST_STRING);

        Reservoir.putAsync(KEY, testPutObject, new ReservoirPutCallback() {
            @Override
            public void onSuccess() {
                getFromReservoir(); //async put call completed, execute get call.
            }

            @Override
            public void onFailure(Exception e) {
                tv_status.setText(e.getMessage()); //failure
            }
        });

    }

    private void getFromReservoir() {

        Reservoir.getAsync(KEY, TestClass.class, new ReservoirGetCallback<TestClass>() {
            @Override
            public void onFailure(Exception e) {
                tv_status.setText(e.getMessage()); //failure.
            }

            @Override
            public void onSuccess(TestClass testGetObject) {
                if (testGetObject.getTestString().equals(TEST_STRING)) {
                    tv_status.setText(getString(R.string.reservoir_success)); //success!
                }
            }

        });

    }

}
