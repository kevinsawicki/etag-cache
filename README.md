# etag-cache

Library to make transparent HTTP requests that can be served locally when
the server replies with a `304 Not Modified` response for a `If-None-Match`
header set by the client.

## License

  * [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)
  
## Examples

### Making a request using a 10 MB cache

```java
File file = new File("/tmp/http-cache");
EtagCache cache = EtagCache.create(file, TEN_MB);
CacheRequest request = CacheRequest.get("http://google.com", cache);
System.out.println("Response was " + request.body();
if (request.cached())
  System.out.println("Cache hit");
else
  System.out.println("Cache miss");
## Dependencies

  * [http-request](https://github.com/kevinsawicki/http-request)
  * [DiskLruCache](https://github.com/JakeWharton/DiskLruCache)
