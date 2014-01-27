package com.anupcowkur.reservoir;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;

/**
 * The main reservoir class.
 */
public class Reservoir {

    private static SimpleDiskCache cache;

    /**
     * Initialize Reservoir
     *
     * @param context context.
     * @param maxSize the maximum size in bytes.
     */
    public static synchronized void init(Context context, long maxSize) throws Exception {

        cache = SimpleDiskCache.open(context.getFilesDir(), 1, maxSize);
    }

    /**
     * Check if an object with the given key exists in the Reservoir.
     *
     * @param key they key string.
     * @return true if object with given key exists.
     */
    public static boolean contains(String key) throws Exception {

        return cache.contains(key);
    }

    /**
     * Put an object into Reservoir with the given key. This a blocking IO operation. Previously
     * stored object with the same
     * key (if any) will be overwritten.
     *
     * @param key    they key string.
     * @param object the object to be stored.
     */
    public static void put(String key, Object object) throws Exception {
        String json = new Gson().toJson(object);
        cache.put(key, json);
    }

    /**
     * Put an object into Reservoir with the given key asynchronously. Previously
     * stored object with the same
     * key (if any) will be overwritten.
     *
     * @param key      they key string.
     * @param object   the object to be stored.
     * @param callback a callback of type {@link com.anupcowkur.reservoirsample
     *                 .ReservoirPutCallback} which is called upon completion.
     */
    public static void putAsync(String key, Object object,
                                ReservoirPutCallback callback) {

        new PutTask(key, object, callback).execute();
    }

    /**
     * Get an object from Reservoir with the given key. This a blocking IO operation.
     *
     * @param key      they key string.
     * @param classOfT the Class type of the expected return object.
     * @return the object of the given type if it exists.
     */
    public static <T> T get(String key, Class<T> classOfT) throws Exception {

        String json = cache.getString(key).getString();
        return new Gson().fromJson(json, classOfT);
    }

    /**
     * Get an object from Reservoir with the given key asynchronously.
     *
     * @param key      they key string.
     * @param callback a callback of type {@link com.anupcowkur.reservoirsample
     *                 .ReservoirGetCallback} which is called upon completion.
     */
    public static <T> void getAsync(String key, Class<T> classOfT,
                                    ReservoirGetCallback<T> callback) {
        new GetTask<T>(key, classOfT, callback).execute();
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
                callback.onComplete(e);
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
                return new Gson().fromJson(json, classOfT);
            } catch (Exception e) {
                this.e = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(T object) {
            if (callback != null) {
                callback.onComplete(e, object);
            }
        }

    }
}
