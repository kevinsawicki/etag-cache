# etag-cache

Library to make transparent HTTP requests that can be served locally when
the server replies with a `304 Not Modified` response for a `If-None-Match`
header set by the client.

## License

  * [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

## Dependencies

  * [http-request](https://github.com/kevinsawicki/http-request)
  * [DiskLruCache](https://github.com/JakeWharton/DiskLruCache)
