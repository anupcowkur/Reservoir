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

If you want to pass in a custom GSON instance for whatever reason, you can do that too:

```java
try {
    Reservoir.init(this, 2048, myGsonInstance);
} catch (Exception e) {
        //failure
}
```

The best place to do this initialization would be in your application's `onCreate()` method.

Since this library depends directly on [DiskLruCache](https://github.com/JakeWharton/DiskLruCache), you can refer that project for more info on the maximum size you can allocate etc.

## Put stuff

You can put objects into Reservoir synchronously or asynchronously.

Async put will you give you a callback on completion:

```java

//Put a simple object
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


//Put collection
List<String> strings = new ArrayList<String>();
strings.add("one");
strings.add("two");
strings.add("three");
Reservoir.putAsync("myKey", strings, new ReservoirPutCallback() {
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
//Put a simple object
try {
    Reservoir.put("myKey", myObject);
} catch (Exception e) {
    //failure;
}

//Put collection
List<String> strings = new ArrayList<String>();
strings.add("one");
strings.add("two");
strings.add("three");
try {
    Reservoir.put("myKey", strings);
} catch (Exception e) {
    //failure;
}
```

Async put uses the standard AsyncTask provided by the Android framework.

## Get Stuff

You can get stuff out of Reservoir synchronously or asynchronously as well.

Async get will give you a callback on completion:

```java
//Get a simple object
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

//Get collection
Type resultType = new TypeToken<List<String>>() {}.getType();
Reservoir.getAsync("myKey", resultType, new ReservoirGetCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> strings) {
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
//Get a simple object
try {
    Reservoir.get("myKey", MyClass.class);
} catch (Exception e) {
        //failure
}

//Get collection
Type resultType = new TypeToken<List<String>>() {}.getType();
try {
    Reservoir.get("myKey", resultType);
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

Reservoir is down with RxJava! All the async methods have RxJava variants that return observables. These observables are scheduled on a background thread and observed on the main thread by default (you can change this easily by assigning your own schedulers and observers to the returned observable).

put:

```
//Put a simple object
Reservoir.putAsync("myKey", myObject) returns Observable<Boolean>

//Put collection
List<String> strings = new ArrayList<String>();
strings.add("one");
strings.add("two");
strings.add("three");
Reservoir.putAsync("myKey", strings) returns Observable<Boolean>
```

get:
```
//Get a simple object
Reservoir.getAsync("myKey", MyClass.class) returns Observable<MyClass>

//Get collection
//Note : Rx observables return items one at a time. So even if you put in a complete collection, the items in the collection will be returned 
//one by one by the observable.
Type collectionType = new TypeToken<List<String>>() {}.getType();
Reservoir.getAsync("myKey", String.class, collectionType) returns Observable<String>
```

delete:
```
Reservoir.deleteAsync("myKey") returns Observable<Boolean>
```

clear:
```
Reservoir.clearAsync() returns Observable<Boolean>
```

If you'd like to see examples of using these observables, check out the [tests in the sample application](https://github.com/anupcowkur/Reservoir/blob/master/Sample/src/androidTest/java/com/anupcowkur/reservoir/ReservoirTest.java).

# Including in your project

Add the jcenter repository to your gradle build file if it's not already present:

```
repositories {
    jcenter()
}
```

Next, add Reservoir as a dependency:

```groovy
dependencies {
    compile 'com.anupcowkur:reservoir:2.1'
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
Check out the [sample application tests](https://github.com/anupcowkur/Reservoir/blob/master/Sample/src/androidTest/java/com/anupcowkur/reservoir/ReservoirTest.java) for complete examples of API usage.

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


