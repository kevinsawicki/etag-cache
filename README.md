# etag-cache [![Build Status](https://travis-ci.org/kevinsawicki/etag-cache.png)](https://travis-ci.org/kevinsawicki/etag-cache)

Library to make transparent HTTP requests that can be served locally when
the server replies with a `304 Not Modified` response for a `If-None-Match`
header set by the client.

The library is available from [Maven Central](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.github.kevinsawicki%22%20AND%20a%3A%22etag-cache%22):

```xml
<dependency>
  <groupId>com.github.kevinsawicki</groupId>
  <artifactId>etag-cache</artifactId>
  <version>0.2</version>
</dependency>
```

## License

  * [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

## Examples

### Making a request using a 10 MB cache

```java
File file = new File("/tmp/http-cache");
EtagCache cache = EtagCache.create(file, TEN_MB);
CacheRequest request = CacheRequest.get("http://google.com", cache);
System.out.println("Response was " + request.body());
if (request.cached())
  System.out.println("Cache hit");
else
  System.out.println("Cache miss");
```

## Dependencies

  * [kevinsawicki/http-request](https://github.com/kevinsawicki/http-request)
  * [JakeWharton/DiskLruCache](https://github.com/JakeWharton/DiskLruCache)
