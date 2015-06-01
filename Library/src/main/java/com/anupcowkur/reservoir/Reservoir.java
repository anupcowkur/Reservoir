package com.anupcowkur.reservoir;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * The main reservoir class.
 */
public class Reservoir {

    private static SimpleDiskCache cache;

    private static File cacheDir;

    private static boolean initialised = false;

    /**
     * Initialize Reservoir
     *
     * @param context context.
     * @param maxSize the maximum size in bytes.
     */
    public static synchronized void init(Context context, long maxSize) throws Exception {

        //Create a directory inside the application specific cache directory. This is where all
        // the key-value pairs will be stored.
        cacheDir = new File(context.getCacheDir() + "/Reservoir");
        createCache(cacheDir, maxSize);
        initialised = true;
    }

    /**
     * Checks if init method has been called and throws an IllegalStateException if it hasn't.
     *
     * @throws IllegalStateException
     */
    private static void failIfNotInitialised() throws IllegalStateException {
        if (!initialised) {
            throw new IllegalStateException("Init hasn't been called! You need to initialise " +
                    "Reservoir before you call any other methods.");
        }
    }

    /**
     * Creates the cache.
     *
     * @param cacheDir the directory where the cache is to be created.
     * @param maxSize  the maximum cache size in bytes.
     */
    private static synchronized void createCache(File cacheDir, long maxSize) throws Exception {
        boolean success = true;
        if (!cacheDir.exists()) {
            success = cacheDir.mkdir();
        }
        if (!success) {
            throw new IOException("Failed to create cache directory!");
        }
        cache = SimpleDiskCache.open(cacheDir, 1, maxSize);
    }

    /**
     * Check if an object with the given key exists in the Reservoir.
     *
     * @param key the key string.
     * @return true if object with given key exists.
     */
    public static boolean contains(String key) throws Exception {
        failIfNotInitialised();
        return cache.contains(key);
    }

    /**
     * Put an object into Reservoir with the given key. This a blocking IO operation. Previously
     * stored object with the same
     * key (if any) will be overwritten.
     *
     * @param key    the key string.
     * @param object the object to be stored.
     */
    public static void put(String key, Object object) throws Exception {
        failIfNotInitialised();
        String json = new Gson().toJson(object);
        cache.put(key, json);
    }

    /**
     * Put an object into Reservoir with the given key asynchronously. Previously
     * stored object with the same
     * key (if any) will be overwritten.
     *
     * @param key      the key string.
     * @param object   the object to be stored.
     * @param callback a callback of type {@link com.anupcowkur.reservoir.ReservoirPutCallback}
     *                 which is called upon completion.
     */
    public static void putAsync(String key, Object object,
                                ReservoirPutCallback callback) {
        failIfNotInitialised();
        new PutTask(key, object, callback).execute();
    }

    /**
     * Put an object into Reservoir with the given key asynchronously. Previously
     * stored object with the same
     * key (if any) will be overwritten.
     *
     * @param key    the key string.
     * @param object the object to be stored.
     * @return an {@link Observable} that will insert the object into Reservoir. By default, this
     * will be scheduled on a background thread and will be observed on the main thread.
     */
    public static Observable<Boolean> putAsync(final String key, final Object object) {
        failIfNotInitialised();
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    Reservoir.put(key, object);
                    subscriber.onNext(true);
                    subscriber.onCompleted();
                } catch (Exception exception) {
                    subscriber.onError(exception);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Get an object from Reservoir with the given key. This a blocking IO operation.
     *
     * @param key      the key string.
     * @param classOfT the Class type of the expected return object.
     * @return the object of the given type if it exists.
     */
    public static <T> T get(String key, Class<T> classOfT) throws Exception {
        failIfNotInitialised();
        String json = cache.getString(key).getString();
        T value = new Gson().fromJson(json, classOfT);
        if (value == null)
            throw new NullPointerException();
        return value;
    }

    /**
     * Get an object from Reservoir with the given key asynchronously.
     *
     * @param key      the key string.
     * @param callback a callback of type {@link com.anupcowkur.reservoir.ReservoirGetCallback}
     *                 which is called upon completion.
     */
    public static <T> void getAsync(String key, Class<T> classOfT,
                                    ReservoirGetCallback<T> callback) {
        failIfNotInitialised();
        new GetTask<>(key, classOfT, callback).execute();
    }

    /**
     * Get an object from Reservoir with the given key asynchronously.
     *
     * @param key the key string.
     * @return an {@link Observable} that will fetch the object from Reservoir. By default, this
     * will be scheduled on a background thread and will be observed on the main thread.
     */
    public static <T> Observable<T> getAsync(final String key, final Class<T> classOfT) {
        failIfNotInitialised();
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    T t = Reservoir.get(key, classOfT);
                    subscriber.onNext(t);
                    subscriber.onCompleted();
                } catch (Exception exception) {
                    subscriber.onError(exception);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Delete an object from Reservoir with the given key. This a blocking IO operation. Previously
     * stored object with the same
     * key (if any) will be deleted.
     *
     * @param key the key string.
     */
    public static void delete(String key) throws Exception {
        failIfNotInitialised();
        cache.delete(key);
    }

    /**
     * Delete an object into Reservoir with the given key asynchronously. Previously
     * stored object with the same
     * key (if any) will be deleted.
     *
     * @param key      the key string.
     * @param callback a callback of type {@link com.anupcowkur.reservoir.ReservoirDeleteCallback}
     *                 which is called upon completion.
     */
    public static void deleteAsync(String key, ReservoirDeleteCallback callback) {
        failIfNotInitialised();
        new DeleteTask(key, callback).execute();
    }

    /**
     * Delete an object into Reservoir with the given key asynchronously. Previously
     * stored object with the same
     * key (if any) will be deleted.
     *
     * @param key the key string.
     * @return an {@link Observable} that will delete the object from Reservoir.By default, this
     * will be scheduled on a background thread and will be observed on the main thread.
     */
    public static Observable<Boolean> deleteAsync(final String key) {
        failIfNotInitialised();
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    Reservoir.delete(key);
                    subscriber.onNext(true);
                    subscriber.onCompleted();
                } catch (Exception exception) {
                    subscriber.onError(exception);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Clears the cache. Deletes all the stored key-value pairs synchronously.
     */
    public static void clear() throws Exception {
        failIfNotInitialised();
        long maxSize = cache.getMaxSize();
        cache.destroy();
        createCache(cacheDir, maxSize);
    }

    /**
     * Clears the cache. Deletes all the stored key-value pairs asynchronously.
     */
    public static void clearAsync(ReservoirClearCallback callback) throws Exception {
        failIfNotInitialised();
        new ClearTask(callback).execute();
    }

    /**
     * Clears the cache. Deletes all the stored key-value pairs asynchronously.
     *
     * @return an {@link Observable} that will clear all the key-value pairs from Reservoir.By default, this
     * will be scheduled on a background thread and will be observed on the main thread.
     */
    public static Observable<Boolean> clearAsync() {
        failIfNotInitialised();
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    Reservoir.clear();
                    subscriber.onNext(true);
                    subscriber.onCompleted();
                } catch (Exception exception) {
                    subscriber.onError(exception);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Returns the number of bytes being used currently by the cache.
     */
    static long bytesUsed() throws Exception {
        failIfNotInitialised();
        return cache.bytesUsed();
    }


    /**
     * AsyncTask to perform put operation in a background thread.
     */
    private static class PutTask extends AsyncTask<Void, Void, Void> {

        private final String key;
        private Exception e;
        private final ReservoirPutCallback callback;
        final Object object;

        private PutTask(String key, Object object, ReservoirPutCallback callback) {
            this.key = key;
            this.callback = callback;
            this.object = object;
            this.e = null;
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                String json = new Gson().toJson(object);
                cache.put(key, json);
            } catch (Exception e) {
                this.e = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (callback != null) {
                if (e == null) {
                    callback.onSuccess();
                } else {
                    callback.onFailure(e);
                }
            }
        }

    }

    /**
     * AsyncTask to perform get operation in a background thread.
     */
    private static class GetTask<T> extends AsyncTask<Void, Void, T> {

        private final String key;
        private final ReservoirGetCallback callback;
        private final Class<T> classOfT;
        private Exception e;

        private GetTask(String key, Class<T> classOfT, ReservoirGetCallback callback) {
            this.key = key;
            this.callback = callback;
            this.classOfT = classOfT;
            this.e = null;
        }

        @Override
        protected T doInBackground(Void... params) {
            try {
                String json = cache.getString(key).getString();
                T value = new Gson().fromJson(json, classOfT);
                if (value == null)
                    throw new NullPointerException();
                return value;
            } catch (Exception e) {
                this.e = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(T object) {
            if (callback != null) {
                if (e == null) {
                    callback.onSuccess(object);
                } else {
                    callback.onFailure(e);
                }
            }
        }

    }

    /**
     * AsyncTask to perform delete operation in a background thread.
     */
    private static class DeleteTask extends AsyncTask<Void, Void, Void> {

        private final String key;
        private Exception e;
        private final ReservoirDeleteCallback callback;

        private DeleteTask(String key, ReservoirDeleteCallback callback) {
            this.key = key;
            this.callback = callback;
            this.e = null;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                cache.delete(key);
            } catch (Exception e) {
                this.e = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (callback != null) {
                if (e == null) {
                    callback.onSuccess();
                } else {
                    callback.onFailure(e);
                }
            }
        }

    }

    /**
     * AsyncTask to perform clear operation in a background thread.
     */
    private static class ClearTask extends AsyncTask<Void, Void, Void> {

        private Exception e;
        private final ReservoirClearCallback callback;

        private ClearTask(ReservoirClearCallback callback) {
            this.callback = callback;
            this.e = null;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                clear();
            } catch (Exception e) {
                this.e = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (callback != null) {
                if (e == null) {
                    callback.onSuccess();
                } else {
                    callback.onFailure(e);
                }
            }
        }

    }

}
