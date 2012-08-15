# etag-cache

Library to make transparent HTTP requests that can be served locally when
the server replies with a `304 Not Modified` response for a `If-None-Match`
header set by the client.

The library is available from [Maven Central](http://search.maven.org/#artifactdetails%7Ccom.github.kevinsawicki%7Cetag-cache%7C0.1%7Cjar):

```xml
<dependency>
  <groupId>com.github.kevinsawicki</groupId>
  <artifactId>etag-cache</artifactId>
  <version>0.1</version>
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
