package com.anupcowkur.reservoir;

import android.test.suitebuilder.annotation.MediumTest;

import com.anupcowkur.reservoirsample.MainActivity;
import com.anupcowkur.reservoirsample.TestClass;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import rx.Observer;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
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

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

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
                        fail();
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                fail();
            }
        });

    }

    @Test
    @MediumTest
    public void testAsyncRxShouldPutAndGetObject() throws Exception {

        TestClass testPutObject = new TestClass();

        testPutObject.setTestString(TEST_STRING);

        Reservoir.putAsync(KEY, testPutObject).subscribe(new Observer<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                fail();
            }

            @Override
            public void onNext(Boolean success) {
                Reservoir.getAsync(KEY, TestClass.class).subscribe(new Observer<TestClass>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        fail();
                    }

                    @Override
                    public void onNext(TestClass testResultObject) {
                        assertEquals(TEST_STRING, testResultObject.getTestString());
                    }
                });
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
                fail();
            }

            @Override
            public void onFailure(Exception e) {
                assertThat(e, instanceOf(NullPointerException.class));
            }
        });
    }

    @Test
    @MediumTest
    public void testSyncShouldThrowIOExceptionWhenObjectSizeGreaterThanCacheSize() throws
            Exception {

        expectedEx.expect(IOException.class);
        expectedEx.expectMessage(SimpleDiskCache.OBJECT_SIZE_GREATER_THAN_CACHE_SIZE_MESSAGE);

        Reservoir.put(KEY, TestUtils.getLargeString());
    }

    @Test
    @MediumTest
    public void testASyncShouldThrowIOExceptionWhenObjectSizeGreaterThanCacheSize() throws
            Exception {

        Reservoir.putAsync(KEY, TestUtils.getLargeString(), new ReservoirPutCallback() {
            @Override
            public void onSuccess() {
                fail();
            }

            @Override
            public void onFailure(Exception e) {
                assertThat(e, instanceOf(IOException.class));
                assertEquals(SimpleDiskCache.OBJECT_SIZE_GREATER_THAN_CACHE_SIZE_MESSAGE, e.getMessage());
            }
        });

    }

    @Test
    @MediumTest
    public void testSyncShouldDeleteObject() throws Exception {

        TestClass testPutObject = new TestClass();
        testPutObject.setTestString(TEST_STRING);
        Reservoir.put(KEY, testPutObject);

        Reservoir.delete(KEY);

        assertEquals(false, Reservoir.contains(KEY));

    }

    @Test
    @MediumTest
    public void testAsyncShouldDeleteObject() throws Exception {

        TestClass testPutObject = new TestClass();
        testPutObject.setTestString(TEST_STRING);
        Reservoir.put(KEY, testPutObject);

        Reservoir.deleteAsync(KEY, new ReservoirDeleteCallback() {
            @Override
            public void onSuccess() {
                try {
                    assertEquals(false, Reservoir.contains(KEY));
                } catch (Exception e) {
                    fail();
                }
            }

            @Override
            public void onFailure(Exception e) {
                fail();
            }
        });
    }

    @Test
    @MediumTest
    public void testAsyncRxShouldDeleteObject() throws Exception {

        TestClass testPutObject = new TestClass();
        testPutObject.setTestString(TEST_STRING);
        Reservoir.put(KEY, testPutObject);

        Reservoir.deleteAsync(KEY).subscribe(new Observer<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                fail();
            }

            @Override
            public void onNext(Boolean success) {
                try {
                    assertEquals(false, Reservoir.contains(KEY));
                } catch (Exception e) {
                    fail();
                }
            }
        });
    }

    @Test
    @MediumTest
    public void testSyncShouldClearCache() throws Exception {

        TestClass testPutObject = new TestClass();
        testPutObject.setTestString(TEST_STRING);
        Reservoir.put(KEY, testPutObject);

        Reservoir.clear();

        assertEquals(0, Reservoir.bytesUsed());

    }

    @Test
    @MediumTest
    public void testAsyncShouldClearCache() throws Exception {

        TestClass testPutObject = new TestClass();
        testPutObject.setTestString(TEST_STRING);
        Reservoir.put(KEY, testPutObject);

        Reservoir.clearAsync(new ReservoirClearCallback() {
            @Override
            public void onSuccess() {
                try {
                    assertEquals(0, Reservoir.bytesUsed());
                } catch (Exception e) {
                    fail();
                }
            }

            @Override
            public void onFailure(Exception e) {
                fail();
            }
        });

    }

    @Test
    @MediumTest
    public void testAsyncRxShouldClearCache() throws Exception {

        TestClass testPutObject = new TestClass();
        testPutObject.setTestString(TEST_STRING);
        Reservoir.put(KEY, testPutObject);

        Reservoir.clearAsync().subscribe(new Observer<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                fail();
            }

            @Override
            public void onNext(Boolean success) {
                try {
                    assertEquals(0, Reservoir.bytesUsed());
                } catch (Exception e) {
                    fail();
                }
            }
        });
    }

}
