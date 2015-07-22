# Bridge

Bridge is a simple but powerful HTTP networking library for Android. It features a Fluent chainable API,
powered by Java/Android's URLConnection classes for maximum compatibility and speed.

# Gradle Dependency

First, add JitPack.io to the repositories list in your app module's build.gradle file:

```gradle
repositories {
    maven { url "https://jitpack.io" }
}
```

Then, add Bridge to your dependencies list:

```gradle
dependencies {
    compile 'com.afollestad:bridge:1.3.0'
}
```

![JitPack Badge](https://img.shields.io/github/release/afollestad/bridge.svg?label=bridge)

# Table of Contents

1. [Basics](https://github.com/afollestad/bridge#basics)
    1. [Request API](https://github.com/afollestad/bridge#request-api)
    2. [URL Format Args](https://github.com/afollestad/bridge#url-format-args)
    3. [Response Headers](https://github.com/afollestad/bridge#response-headers)
    4. [Response Bodies](https://github.com/afollestad/bridge#response-bodies)
2. [Request Headers](https://github.com/afollestad/bridge#request-headers)
3. [Request Bodies](https://github.com/afollestad/bridge#request-bodies)
    1. [Basics](https://github.com/afollestad/bridge#basics-1)
    2. [Forms](https://github.com/afollestad/bridge#forms)
    3. [MultipartForms](https://github.com/afollestad/bridge#multipartforms)
    4. [Streaming (Pipe)](https://github.com/afollestad/bridge#streaming-pipe)
4. [Async Requests, Duplicate Avoidance, and Progress Callbacks](https://github.com/afollestad/bridge#async-requests-and-duplicate-avoidance)
    1. [Example](https://github.com/afollestad/bridge#example)
    2. [Duplicate Avoidance](https://github.com/afollestad/bridge#duplicate-avoidance)
    3. [Progress Callbacks](https://github.com/afollestad/bridge#progress-callbacks)
5. [Request Cancellation](https://github.com/afollestad/bridge#request-cancellation)
    1. [Cancelling Individual Requests](https://github.com/afollestad/bridge#cancelling-individual-requests)
    2. [Cancelling Multiple Requests](https://github.com/afollestad/bridge#cancelling-multiple-requests)
        1. [All Active](https://github.com/afollestad/bridge#all-active)
        1. [Method, URL/Regex](https://github.com/afollestad/bridge#method-urlregex)
        2. [Tags](https://github.com/afollestad/bridge#tags)
    3. [Preventing Cancellation](https://github.com/afollestad/bridge#preventing-cancellation)
6. [Configuration](https://github.com/afollestad/bridge#configuration)
    1. [Host](https://github.com/afollestad/bridge#host)
    2. [Default Headers](https://github.com/afollestad/bridge#default-headers)
    3. [Timeouts](https://github.com/afollestad/bridge#timeouts)
    4. [Buffer Size](https://github.com/afollestad/bridge#buffer-size)
7. [Cleanup](https://github.com/afollestad/bridge#cleanup)

------

# Basics

### Request API

Here's a very basic GET `Request` that retrieves Google's home page and saves the content to a `String`.

```java
try {
    Response response = Bridge.client()
        .get("http://www.google.com")
        .response();

    if (!response.isSuccess()) {
        // If response code was not 200 OK
        throw new Exception(response.code() + " " + response.phrase());
    } else {
        String responseContent = response.asString();
        // Do something with response
    }
} catch (Exception e) {
    // An error occurred
}
```

This could be shortened to:

```java
try {
    String content = Bridge.client()
        .get("http://www.google.com")
        .asString();
    // Do something with response
} catch (Exception e) {
    // An error occurred OR response code was not 200 OK
}
```

Behind the scenes, this is actually performing this:

```java
try {
    String content = Bridge.client()
        .get("http://www.google.com")
        .response()
        .throwIfNotSuccessful()
        .asString();
} catch (Exception e) {
    // An error occurred
}
```

It just takes out the need to use `response()`, and it makes use of `throwIfNotSuccessful()` to automatically
throw an `Exception` if the response code is not 200 OK.

### URL Format Args

When passing query parameters in a URL, you can use format args:

```java
try {
    String searchQuery = "hello, how are you?";
    String content = Bridge.client()
        .get("http://www.google.com/search?q=%s", searchQuery)
        .asString();
    // Do something with response
} catch (Exception e) {
    // An error occurred OR response code was not 200 OK
}
```

This is using Java's `String.format()` method behind the scenes. The `searchQuery` variable replaces
`%s` in your URL. You can have multiple format args, and they don't all have to be strings (e.g. `%d` for any number variable).
Args that are strings will be automatically URL encoded for you.

### Response Headers

Retrieving response headers is simple:

```java
Response response = // ...
String contentType = response.header("Content-Type");
```

Headers can also have multiple values, separated by commas:

```java
Response response = // ...
List<String> values = response.headerList("header-name");
```

Since *Content-Type* and *Content-Length* are commonly used response headers, there's convenience methods
to get these values:

```java
Response response = // ...
String contentType = response.contentType();
int contentLength = response.contentLength();
```

### Response Bodies

The above examples all use `asString()` to save response content as a `String`. There are many other
formats that responses can be converted/saved to:

```java
Response response = // ...

byte[] responseRawData = response.asBytes();

String responseString = response.asString();

// If you set this to a TextView, it will display HTML formatting
Spanned responseHtml = response.asHtml();

JSONObject responseJsonObject = response.asJsonObject();

JSONArray responseJsonArray = response.asJsonArray();

// Don't forget to recycle!
// Once you use this method once, the resulting Bitmap is cached in the Response object,
// Meaning asBitmap() will always return the same Bitmap from any reference to this response.
Bitmap responseImage = response.asBitmap();

// Save the response content to a File of your choosing
response.asFile(new File("/sdcard/Download.extension"));
```

------

# Request Headers

Changing or adding `Request` headers is pretty simple. You just use the `header(String, String)` method
which can be chained to add multiple headers.

```java
try {
    String content = Bridge.client()
        .get("http://www.google.com")
        .header("User-Agent", "BridgeSampleProject")
        .header("CustomHeader", "Hello")
        .asString();
    // Do something with response
} catch (Exception e) {
    // An error occurred OR response code was not 200 OK
}
```

**Default headers**: see the [Configuration](https://github.com/afollestad/bridge#configuration) section
for info on how to set default headers that are automatically included in every request.

------

# Request Bodies

### Basics

Here's a basic `POST` request that sends plain text in the body:

```java
try {
    String postContent = "Hello, how are you?";
    String response = Bridge.client()
        .post("http://someurl.com/post")
        .body(postContent)
        .asString();
} catch(Exception e) {
    // An error occurred
}
```

Passing a `String` will automatically set the `Content-Type` header to `text/plain`. The `body()`
method takes other various forms of data, including:

* Raw `byte[]` data
* `JSONObject`/`JSONArray`
* `Form`
* `MultipartForm`
* `Pipe`
* `File`

Byte arrays are obviously raw data. Passing a `JSONObject` or `JSONArray` will automatically set the `Content-Type` header
to `application/json`. `Form`, `MultipartForm`, and `Pipe` will be discussed in the next few sections.
`File` relies on the `Pipe` feature, it reads the file and sets the request body to the raw contents.

### Forms

`Form`'s are commonly used with PUT/POST requests. They're basically the same thing as query strings
with get requests, but the parameters are included in the body of the request rather than the URL.

Here's a basic example of how it's done:

```java
try {
    Form form = new Form()
        .add("Username", "Aidan")
        .add("Password", "Hello");

    String response = Bridge.client()
        .post("http://someurl.com/login")
        .body(form)
        .asString();
} catch (Exception e) {
    // An error occurred
}
```

This will automatically set the `Content-Type` header to `application/x-www-form-urlencoded`.

### MultipartForms

A `MultipartForm` is a bit different than a regular form. Content is added as a "part" to the request body.
The content is included as raw data associated with a content type, allowing you to include entire files.
Multipart forms are commonly used in HTML forms (e.g. a contact form on a website), and they can be used
for uploading files to a website.

Here's a basic example of how it's done:

```java
try {
    MultipartForm form = new MultipartForm()
        .add("Subject", "Hello")
        .add("Body", "Hey, how are you?")
        .add("FileUpload", new File("/sdcard/Download/ToUpload.txt"));

    String response = Bridge.client()
        .post("http://someurl.com/post")
        .body(form)
        .asString();
} catch (Exception e) {
    // An error occurred
}
```

This will automatically set the `Content-Type` header to `multipart/form-data`.

**Note**: `MultipartForm` has an `add()` method that accepts a `Pipe`. This can be used to add parts
from streams (see the section below on how `Pipe` is used). `add()` for `File` objects is actually using
this indirectly for you.

### Streaming (Pipe)

Bridge allows you to stream data directly into a post body:

```java
try {
    Pipe pipe = new Pipe() {
        @Override
        public void writeTo(OutputStream os) throws IOException {
            os.write("Hello, this is a streaming example".getBytes());
        }

        @Override
        public String contentType() {
            return "text/plain";
        }
    };

    String response = Bridge.client()
        .post("http://someurl.com/post")
        .body(pipe)
        .asString();
} catch (Exception e) {
    // An error occurred
}
```

**Note**: when you use a `Pipe` as a body, the `Content-Type` header will automatically be set based
on what `contentType()` in the `Pipe` implementation returns. If you want to override this header,
you can change it in the `Pipe` or reset the header after setting the body.

`Pipe` has three convenience methods that create a pre-designed `Pipe` instance:

```java
Pipe uriPipe = Pipe.forUri(this, Uri.parse(
    "content://com.example.provider/documents/images/1"));

Pipe filePipe = Pipe.forFile(new File("/sdcard/myfile.txt"));

InputStream is = // ...
Pipe transferPipe = Pipe.forStream(is, "text/plain");
```

`forUri(Context, Uri)` will read from a File URI or content URI, so basically any file on an Android device can
be read. `forFile(File)` indirectly uses `forUri(Context, Uri)` specifically for file:// URIs.
`forStream(InputStream, String)` reads an `InputStream` and transfers the content into the Pipe,
you need to specify a Content-Type value in the second parameter.

------

# Async Requests, Duplicate Avoidance, and Progress Callbacks

### Example

Here's a basic example of an asynchronous request:

```java
Bridge.client()
    .get("http://www.google.com")
    .request(new Callback() {
        @Override
        public void response(Request request, Response response, RequestException e) {
            if (e != null || !response.isSuccess()) {
                // An error occurred or response was not successful
            } else {
                String content = response.asString();
            }
        }
    });
```

There's two major advantages to using async requests:

1. Threads are not blocked. You can execute this code on UI thread (e.g. in an `Activity`) without freezing or errors.
2. Duplicate avoidance (discussed below).

You can replace `get()` with `post()`, `put()` or `delete()` too.

### Duplicate Avoidance

Duplicate avoidance is a feature in this library that allows you to avoid making multiple requests to
the same URL at the same time. For an example, if you were to do this...

```java
Bridge.client()
    .get("http://www.google.com")
    .request(new Callback() {
        @Override
        public void response(Request request, Response response, RequestException e) {
            if (e != null || !response.isSuccess()) {
                // An error occurred or the response was not successful
            } else {
                // Use the Response object
            }
        }
    });

Bridge.client()
    .get("http://www.google.com")
    .request(new Callback() {
        @Override
        public void response(Request request, Response response, RequestException e) {
            if (e != null || !response.isSuccess()) {
                // An error occurred or the response was not successful
            } else {
                // Use the Response object
            }
        }
    });
```

...Google would only be contacted once by the library. When that single request is complete, both callbacks
would be called at the same time with the same response data.

This lets you be very efficient on bandwidth and resource usage. If this library was being used to load images
into `ImageView`'s, you could display 100 `ImageView`'s in a list, make a single request, and immediately populate
all 100 `ImageView`'s with the same image at the same time. Check out the sample project to see this in action.

### Progress Callbacks

The `Callback` class has an optional `progress(Request, int, int, int)` method that can be overridden to receive
progress updates for response downloading. The second int parameter represents the percentage that's been downloaded.

```java
Bridge.client()
    .get("http://someurl/bigfile.extension")
    .request(new Callback() {
        @Override
        public void response(Request request, Response response, RequestException e) {
            if (e != null || !response.isSuccess()) {
                // An error occurred or the response was not successful
            } else {
                // Use the Response object
            }
        }

        @Override
        public void progress(Request request, int current, int total, int percent) {
            // Progress updates
        }
    });
```

**Note**: progress callbacks are only called if the library is able to determinate the size of the
content being downloaded. Generally, this means the requested endpoint needs to return a `Content-Length`
header.

------

# Request Cancellation

### Cancelling Individual Requests

This library allows you easily cancel requests:

```java
Request request = Bridge.client()
    .get("http://www.google.com")
    .request(new Callback() {
        @Override
        public void response(Request request, Response response, RequestException e) {
            if (e != null || !response.isSuccess()) {
                if (e.isCancelled()) {
                    // Request was cancelled
                } else {
                    // Error occurred
                }
            } else {
                // Use the Response
            }
        }
    });

request.cancel();
```

When a request is cancelled, the `RequestException` will *not* be null (it will say the request was cancelled),
and `RequestException#isCancelled()` will return true.

### Cancelling Multiple Requests

The `Bridge` singleton allows you to cancel managed **async** requests.

#### All Active

This code will cancel all active requests, regardless of method or URL:

```java
Bridge.client()
    .cancelAll();
```

#### Method, URL/Regex

The `cancelAll(Method, String)` allows you to cancel all active requests that match an HTTP method
*and* a URL or regular expression pattern.

This will cancel all GET requests to any URL starting with http:// and ending with `.png`:

```java
Bridge.client()
    .cancelAll(Method.GET, "http://.*\\.png");
```

`.*` is a wildcard in regular expressions, \\\\ escapes the period to make it literal.

If you want to cancel all requests to a specific URL, you can use `Pattern.quote()` to specify a regex
that matches literal text:

```java
Bridge.client()
    .cancelAll(Method.GET, Pattern.quote("http://www.android.com/media/android_vector.jpg"));
```

**Note**: if you pass `null` for the first parameter (`Method`), it will ignore the HTTP method when
looking for requests to cancel. In other words, you could cancel `GET`, `POST`, `PUT`, *and* `DELETE`
requests to a specific URL.

#### Tags

When making a request, you can tag it with a value:

```java
Bridge.client()
    .get("http://www.google.com")
    .tag("Hello!")
    .request(new Callback() {
        @Override
        public void response(Request request, Response response, RequestException e) {
            if (e.isCancelled()) {
                // Request was cancelled
            } else {
                // Error occurred
            }
        }
    });
```

By "a value", I mean literally any type of Object. It could be a `String`, `int`, `boolean`, etc.

You can cancel all requests marked with a specific tag value:

```java
// Add a second parameter with a value of true to cancel un-cancellable requests
Bridge.client()
    .cancelAll("Hello!");
```

### Preventing Cancellation

There are certain situations in which you wouldn't want to allow a request to be cancelled. For an example,
your app may make calls to `Bridge.client().cancelAll()` when an `Activity` pauses; that way, all requests
that were active in that screen are cancelled. However, there may be a `Service` in your app that's making requests
in the background that you would want to maintain. You can make those requests non-cancellable:

```java
Bridge.client()
    .get("http://www.google.com")
    .cancellable(false)
    .request(new Callback() {
        @Override
        public void response(Request request, Response response, RequestException e) {
            if (e != null || !response.isSuccess()) {
                // Error occurred
            } else {
                // Use the Response
            }
        }
    });
```

If you tried to make a call to `cancel()` on this `Request`, an `IllegalStateException` would be thrown.
If you really want to cancel an un-cancellable request (`Request.isCancellable()` returns true), you
can force it to be cancelled with `cancel(true)`.

Un-cancellable requests will be ignored by `Bridge.cancelAll()` and the other variants of that method,
unless you pass `true` for the `force` parameter.

------

# Configuration

Bridge allows you configure various functions globally.

### Host

You can set a host that is used as the base URL for every request.

```java
Bridge.client().config()
    .host("http://www.google.com");
```

With Google's homepage set as the host, the code below would request `http://www.google.com/search?q=Hello`:

```java
Bridge.client()
    .get("/search?q=%s", "Hello")
    .asString();
```

Basically, the URL you pass with each request is appended to the end of the host. If you were to pass a full
URL (beginning with *HTTP*) in `get()` above, it would skip using the host for just that request.

### Default Headers

Default headers are headers that are automatically applied to every request. You don't have to do it
yourself with every request in your app.

```java
Bridge.client().config()
    .defaultHeader("User-Agent", "Bridge Sample Code")
    .defaultHeader("Content-Type", "application/json")
    .defaultHeader("Via", "My App");
```

Every request, regardless of the method, will include those headers. You can override them at the
individual request level by setting the header as you normally would.


### Timeouts

You can configure how long the library will wait until timing out, either for connections or reading:

```java
Bridge.client().config()
    .connectTimeout(10000)
    .readTimeout(15000);
```

You can set timeouts at the request level too:

```java
Bridge.client()
    .get("http://someurl.com/bigVideo.mp4")
    .connectTimeout(10000)
    .readTimeout(15000)
    .asFile(new File("/sdcard/Download/bigVideo.mp4"));
```

### Buffer Size

The default buffer size is *1024 * 4* (4096). Basically, when you download a webpage or file, the
buffer size is how big the byte array is with each pass. A large buffer size will create a larger
byte array, which can affect memory usage, but it also increases the pace in which the content is downloaded.

The buffer size can easily be configured:

```java
Bridge.client().config()
    .bufferSize(1024 * 10);
```

Just remember to be careful with how much memory you consume, and test on various devices.

You can set the buffer size at the request level too:

```java
Bridge.client()
    .get("http://someurl.com/bigVideo.mp4")
    .bufferSize(1024 * 10)
    .asFile(new File("/sdcard/Download/bigVideo.mp4"));
```

**Note**: the buffer size is used in a few other places, such as pre-built `Pipe`'s (`Pipe#forUri`, `Pipe#forStream`, etc.).

------

# Cleanup

When you're done with Bridge (e.g. your app is terminating), you *should* call the cleanup method to
avoid any memory leaks. Your app would be fine without this, but this is good practice and it helps speed
up Java's garbage collection.

```java
Bridge.cleanup();
```

**Note**: Calling this method will also cancel all active requests for you.