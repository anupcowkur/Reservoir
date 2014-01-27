Reservoir
=========

Reservoir is a simple library for Android that allows you to easily serialize and persist your objects on disk using key/value pairs.

# Usage

## Initialize
Reservoir uses the internal storage allocated to your app. Before you can do anything, you need to initialize Reservoir with the cache size.

```
try {
    Reservoir.init(this, 2048);
} catch (Exception e) {
        //failure
}
```

The best place to do this would be in your application's `onCreate()` method.

Since this library depends directly on [DiskLruCache](https://github.com/JakeWharton/DiskLruCache), you can refer that project for more info on the maximum size you can allocate etc.

## Put stuff

You can put objects into Reservoir synchronously or asynchronously.

Async put will you give you a callback on completion:

```
Reservoir.putAsync("myKey", myObject, new ReservoirPutCallback() {
            @Override
            public void onComplete(Exception e) {
                if (e == null)
                    //success
                else
                    //failure
            }
        });
```

synchronous put:

```
try {
    Reservoir.put("myKey",myObject);
} catch (Exception e) {
    //failure;
}
```

Async put uses the standard AsyncTask provided by the Android framework.

## Get Stuff

You can get stuff out of Reservoir synchronously or asynchronously as well.

Async get will give you a callback on completion:

```
Reservoir.getAsync("myKey", MyClass.class, new ReservoirGetCallback<MyClass>() {
            @Override
            public void onComplete(Exception e, MyClass myObject) {
                if (e == null) {
                   //success
                } else
                  //failure.
            }

        });
```

synchronous get:

```
try {
    Reservoir.get("myKey",MyClass.class);
} catch (Exception e) {
        //failure
}
```

## Check for existence

If you wish to know whether an object exists for the given key, you can use:

```
try {
    boolean objectExists = Reservoir.contains("myKey");
} catch (Exception e) {}
```
# Including in your project

You can directly [download the jar](https://github.com/anupcowkur/Reservoir/releases/download/v1.0/reservoir-1.0.jar) and add it to your project. You will also need to add the jars on which Reservoir depends i.e. [DiskLruCache](https://github.com/anupcowkur/Reservoir/releases/download/v1.0/disklrucache-2.0.2.jar), [Apache Commons IO](https://github.com/anupcowkur/Reservoir/releases/download/v1.0/commons-io-2.4.jar) and [GSON](https://github.com/anupcowkur/Reservoir/releases/download/v1.0/gson-2.2.4.jar).

You can also add it as a [library project](https://developer.android.com/tools/projects/index.html#LibraryProjects) and reference it directly without adding the jar.

Maven and Gradle import coming soon.

# Sample
Check out the [sample application](https://github.com/anupcowkur/Reservoir/tree/master/Sample) to see it in action!

# Contributing
Contributions welcome via Github pull requests.

# Credits
Reservoir is just a tiny little convenience wrapper around the following fantastic projects:

- [DiskLruCache](https://github.com/JakeWharton/DiskLruCache)
- [Apache Commons IO](http://commons.apache.org/proper/commons-io/)
- [SimpeDiskCache](https://github.com/fhucho/simple-disk-cache)
- [GSON](https://code.google.com/p/google-gson/)

Thanks, you guys!

# License
This project is licensed under the MIT License. Please refer the [License.txt](https://github.com/anupcowkur/Reservoir/blob/master/LICENSE.txt) file.


