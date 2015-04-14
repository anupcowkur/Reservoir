package com.anupcowkur.reservoir;

import android.test.suitebuilder.annotation.MediumTest;

import com.anupcowkur.reservoirsample.MainActivity;
import com.anupcowkur.reservoirsample.TestClass;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;

/**
 * The main reservoir class.
 */
public class ReservoirTest {

    MainActivity mainActivity;

    private static final String TEST_STRING = "my test string";
    private static final String KEY = "myKey";

    @Rule
    public final ActivityRule<MainActivity> rule = new ActivityRule<>(MainActivity.class);

    @Before
    public void init() {
        mainActivity = rule.get();
    }

    @Test
    @MediumTest
    public void testSyncShouldPutAndGetObject() throws Exception {

        TestClass testPutObject = new TestClass();

        testPutObject.setTestString(TEST_STRING);

        Reservoir.put(KEY, testPutObject);

        TestClass testResultObject = Reservoir.get(KEY, TestClass.class);

        assertEquals(TEST_STRING, testResultObject.getTestString());

    }

    @Test
    @MediumTest
    public void testAsyncShouldPutAndGetObject() throws Exception {

        TestClass testPutObject = new TestClass();

        testPutObject.setTestString(TEST_STRING);

        Reservoir.putAsync(KEY, testPutObject, new ReservoirPutCallback() {
            @Override
            public void onSuccess() {
                Reservoir.getAsync(KEY, TestClass.class, new ReservoirGetCallback<TestClass>() {
                    @Override
                    public void onSuccess(TestClass testResultObject) {
                        assertEquals(TEST_STRING, testResultObject.getTestString());
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

    }

    @Test(expected = NullPointerException.class)
    @MediumTest
    public void testSyncShouldThrowNullPointerExceptionWhenObjectDoesNotExist() throws
            Exception {

        Reservoir.get("non_existent_key", TestClass.class);

    }

    @Test
    @MediumTest
    public void testAsyncShouldCallOnFailureWhenObjectDoesNotExist() throws
            Exception {

        Reservoir.getAsync("non_existent_key", TestClass.class, new ReservoirGetCallback<TestClass>() {
            @Override
            public void onSuccess(TestClass object) {

            }

            @Override
            public void onFailure(Exception e) {
                assertThat(e, instanceOf(NullPointerException.class));
            }
        });
    }

}
