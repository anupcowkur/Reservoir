Reservoir
=========

Reservoir is a simple library for Android that allows you to easily serialize and cache your objects to disk using key/value pairs.

# Usage

## Initialize
Reservoir uses the internal cache storage allocated to your app. Before you can do anything, you need to initialize Reservoir with the cache size.

```java
try {
    Reservoir.init(this, 2048); //in bytes
} catch (Exception e) {
        //failure
}
```

The best place to do this would be in your application's `onCreate()` method.

Since this library depends directly on [DiskLruCache](https://github.com/JakeWharton/DiskLruCache), you can refer that project for more info on the maximum size you can allocate etc.

## Put stuff

You can put objects into Reservoir synchronously or asynchronously.

Async put will you give you a callback on completion:

```java
Reservoir.putAsync("myKey", myObject, new ReservoirPutCallback() {
            @Override
            public void onSuccess() {
                //success
            }

            @Override
            public void onFailure(Exception e) {
                //error
            }
        });
```

synchronous put:

```java
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

```java
Reservoir.getAsync("myKey", MyClass.class, new ReservoirGetCallback<MyClass>() {
            @Override
            public void onSuccess(MyClass myObject) {
                //success
            }

            @Override
            public void onFailure(Exception e) {
                //error
            }
        });
```

synchronous get:

```java
try {
    Reservoir.get("myKey",MyClass.class);
} catch (Exception e) {
        //failure
}
```

## Check for existence

If you wish to know whether an object exists for the given key, you can use:

```java
try {
    boolean objectExists = Reservoir.contains("myKey");
} catch (Exception e) {}
```

## Delete Stuff

deleting stuff can also be synchronous or asynchronous.

Async delete will give you a callback on completion:

```java
Reservoir.deleteAsync("myKey", new ReservoirDeleteCallback() {
            @Override
            public void onSuccess(MyClass myObject) {
                //success
            }

            @Override
            public void onFailure(Exception e) {
                //error
            }
        });
```

synchronous delete:

```java
try {
    Reservoir.delete("myKey");
} catch (Exception e) {
        //failure
}
```

## Clearing the cache

You can clear the entire cache at once if you want. 

asynchronous clear:

```java
Reservoir.clearAsync(new ReservoirClearCallback() {
            @Override
            public void onSuccess() {
                try {
                    assertEquals(0, Reservoir.bytesUsed());
                } catch (Exception e) {
                   
                }
            }

            @Override
            public void onFailure(Exception e) {
                
            }
        });
```

synchronous clear:

```java
try {
    Reservoir.clear();
} catch (Exception e) {
        //failure
}
```
## RxJava

As of version 2.0, you can use Reservoir with RxJava! All the async methods have RxJava variants that return observables. These observables are scheduled on a background thread and observed on the main thread by default (you can change this easily by assigning your own schedulers and observers to the returned observable).

put:
```
Reservoir.putAsync("myKey", myObject) returns Observable<Boolean>
```

get:
```
Reservoir.getAsync("myKey", MyClass.class) returns Observable<MyClass>
```

delete:
```
Reservoir.deleteAsync("myKey") returns Observable<Boolean>
```

clear:
```
Reservoir.clearAsync() returns Observable<Boolean>
```

You can subscribe to any of these returned Observables like this:

```
Reservoir.putAsync("myKey", myObject).subscribe(new Observer<Boolean>() {
            @Override
            public void onCompleted() {
                //do something on completion
            }

            @Override
            public void onError(Throwable e) {
                //do something on error
            }

            @Override
            public void onNext(Boolean success) {
                //do something on success status receipt
            }
        });
```

# Including in your project

Add the jitpack repository to your gradle build file:

```
repositories {
    maven {
        url "https://jitpack.io"
    }
}
```

Next, add Reservoir as a dependency:

```groovy
dependencies {
    compile 'com.github.anupcowkur:reservoir:2.0'
}
```

# FAQs

## What kind of objects can I add to Reservoir?
Anything that GSON can serialize.

## What happens if my cache size is exceeded?
Older objects will be removed in a LRU (Least Recently Used) order.

## Can I use this a SharedPreferences replacement?
NO! This is a cache. You should store stuff in here that is good to have around, but you wouldn't mind if they were to be removed. SharedPreferences are meant to store user preferences which is not something you want to lose.

# Sample
Check out the [sample application](https://github.com/anupcowkur/Reservoir/tree/master/Sample) for example of typical usage.

# Contributing
Contributions welcome via Github pull requests.

# Credits
Reservoir is just a tiny little convenience wrapper around the following fantastic projects:

- [DiskLruCache](https://github.com/JakeWharton/DiskLruCache)
- [Apache Commons IO](http://commons.apache.org/proper/commons-io/)
- [SimpeDiskCache](https://github.com/fhucho/simple-disk-cache)
- [GSON](https://code.google.com/p/google-gson/)
- [RxAndroid](https://github.com/ReactiveX/RxAndroid)

# License
This project is licensed under the MIT License. Please refer the [License.txt](https://github.com/anupcowkur/Reservoir/blob/master/License.txt) file.


