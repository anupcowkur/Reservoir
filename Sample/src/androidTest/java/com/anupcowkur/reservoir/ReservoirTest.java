package com.anupcowkur.reservoir;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.anupcowkur.reservoirsample.MainActivity;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import rx.Observer;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ReservoirTest {

    private static final String TEST_STRING = "my test string";
    private static final String KEY = "myKey";
    private static final List<Object> TEST_COLLECTION = new ArrayList<>();
    private int i;

    @Rule
    public final ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void testSyncShouldPutAndGetObject() throws Exception {

        TestClass testPutObject = new TestClass();

        testPutObject.setTestString(TEST_STRING);

        Reservoir.put(KEY, testPutObject);

        TestClass testResultObject = Reservoir.get(KEY, TestClass.class);

        assertEquals(TEST_STRING, testResultObject.getTestString());

    }

    @Test
    public void testSyncShouldPutAndGetCollectionObject() throws Exception {

        Reservoir.put(KEY, TEST_COLLECTION);

        Type testResultType = new TypeToken<List<Object>>() {
        }.getType();

        List<Object> testResultCollection = Reservoir.get(KEY, testResultType);

        assertEquals(TEST_COLLECTION, testResultCollection);

    }

    @Test
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
    public void testAsyncShouldPutAndGetCollectionObject() throws Exception {

        Reservoir.putAsync(KEY, TEST_COLLECTION, new ReservoirPutCallback() {
            @Override
            public void onSuccess() {

                final Type testResultType = new TypeToken<List<Object>>() {
                }.getType();

                Reservoir.getAsync(KEY, testResultType, new ReservoirGetCallback<List<Object>>() {
                    @Override
                    public void onSuccess(List<Object> testResultCollection) {
                        assertEquals(TEST_COLLECTION, testResultCollection);
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
    public void testRxShouldPutAndGetObject() throws Exception {

        TestClass testPutObject = new TestClass();

        testPutObject.setTestString(TEST_STRING);

        Reservoir.putUsingObservable(KEY, testPutObject).subscribe(new Observer<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                fail();
            }

            @Override
            public void onNext(Boolean success) {
                Reservoir.getUsingObservable(KEY, TestClass.class).subscribe(new Observer<TestClass>() {
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

    @Test
    public void testRxShouldPutAndGetCollectionObject() throws Exception {

        final String[] strings = {"one", "two", "three", "four"};

        List<String> testStrings = new ArrayList<>(Arrays.asList(strings));

        i = 0;

        Reservoir.putUsingObservable(KEY, testStrings).subscribe(new Observer<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                fail();
            }

            @Override
            public void onNext(Boolean success) {

                final Type testResultType = new TypeToken<List<String>>() {
                }.getType();

                Reservoir.getUsingObservable(KEY, String.class, testResultType).subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        fail();
                    }

                    @Override
                    public void onNext(String testResultString) {
                        assertEquals(strings[i++], testResultString);
                    }
                });
            }
        });
    }

    @Test(expected = NullPointerException.class)
    public void testSyncShouldThrowNullPointerExceptionWhenObjectDoesNotExist() throws
            Exception {

        Reservoir.get("non_existent_key", TestClass.class);

    }

    @Test
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
    public void testRxShouldCallOnFailureWhenObjectDoesNotExist() throws
            Exception {

        Reservoir.getUsingObservable("non_existent_key", TestClass.class).subscribe(new Observer<TestClass>() {
            @Override
            public void onCompleted() {
                fail();
            }

            @Override
            public void onError(Throwable e) {
                assertThat(e, instanceOf(NullPointerException.class));
            }

            @Override
            public void onNext(TestClass testClass) {
                fail();
            }
        });
    }

    @Test
    public void testSyncShouldThrowIOExceptionWhenObjectSizeGreaterThanCacheSize() throws
            Exception {

        expectedEx.expect(IOException.class);
        expectedEx.expectMessage(SimpleDiskCache.OBJECT_SIZE_GREATER_THAN_CACHE_SIZE_MESSAGE);

        Reservoir.put(KEY, TestUtils.getLargeString());
    }

    @Test
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
    public void testRxShouldThrowIOExceptionWhenObjectSizeGreaterThanCacheSize() throws
            Exception {

        Reservoir.putUsingObservable(KEY, TestUtils.getLargeString()).subscribe(new Observer<Boolean>() {
            @Override
            public void onCompleted() {
                fail();
            }

            @Override
            public void onError(Throwable e) {
                assertThat(e, instanceOf(IOException.class));
                assertEquals(SimpleDiskCache.OBJECT_SIZE_GREATER_THAN_CACHE_SIZE_MESSAGE, e.getMessage());
            }

            @Override
            public void onNext(Boolean aBoolean) {
                fail();
            }
        });

    }

    @Test
    public void testSyncShouldDeleteObject() throws Exception {

        TestClass testPutObject = new TestClass();
        testPutObject.setTestString(TEST_STRING);
        Reservoir.put(KEY, testPutObject);

        Reservoir.delete(KEY);

        assertEquals(false, Reservoir.contains(KEY));

    }

    @Test
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
    public void testRxShouldDeleteObject() throws Exception {

        TestClass testPutObject = new TestClass();
        testPutObject.setTestString(TEST_STRING);
        Reservoir.put(KEY, testPutObject);

        Reservoir.deleteUsingObservable(KEY).subscribe(new Observer<Boolean>() {
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
    public void testSyncShouldClearCache() throws Exception {

        TestClass testPutObject = new TestClass();
        testPutObject.setTestString(TEST_STRING);
        Reservoir.put(KEY, testPutObject);

        Reservoir.clear();

        assertEquals(0, Reservoir.bytesUsed());

    }

    @Test
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
    public void testRxShouldClearCache() throws Exception {

        TestClass testPutObject = new TestClass();
        testPutObject.setTestString(TEST_STRING);
        Reservoir.put(KEY, testPutObject);

        Reservoir.clearUsingObservable().subscribe(new Observer<Boolean>() {
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
