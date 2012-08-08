/*
 * Copyright 2012 Kevin Sawicki <kevinsawicki@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.kevinsawicki.etag;

import static java.net.HttpURLConnection.HTTP_NOT_MODIFIED;

import com.github.kevinsawicki.etag.EtagCache.CacheResponse;
import com.github.kevinsawicki.http.HttpRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Request that uses a cache
 */
public class CacheRequest extends HttpRequest {

  /**
   * Start a 'GET' request to the given URL
   *
   * @param url
   * @param cache
   * @return request
   * @throws HttpRequestException
   */
  public static CacheRequest get(final CharSequence url, final EtagCache cache)
      throws HttpRequestException {
    return new CacheRequest(url, METHOD_GET, cache);
  }

  /**
   * Start a 'GET' request to the given URL
   *
   * @param url
   * @param cache
   * @return request
   * @throws HttpRequestException
   */
  public static CacheRequest get(final URL url, final EtagCache cache)
      throws HttpRequestException {
    return new CacheRequest(url, METHOD_GET, cache);
  }

  private final EtagCache cache;

  private CacheResponse response;

  private boolean etagAdded;

  /**
   * Create cache request
   *
   * @param url
   * @param method
   * @param cache
   * @throws HttpRequestException
   */
  public CacheRequest(final CharSequence url, final String method,
      final EtagCache cache) throws HttpRequestException {
    super(url, method);

    this.cache = cache;
  }

  /**
   * Create cache request
   *
   * @param url
   * @param method
   * @param cache
   * @throws HttpRequestException
   */
  public CacheRequest(final URL url, final String method, final EtagCache cache)
      throws HttpRequestException {
    super(url, method);

    this.cache = cache;
  }

  private void closeCacheResponse() {
    if (response == null)
      return;

    response.close();
    response = null;
  }

  @Override
  protected HttpRequest closeOutput() throws IOException {
    // Only attempt to add an etag once
    if (!etagAdded) {
      etagAdded = true;
      response = cache.get(getConnection());
      if (response != null)
        ifNoneMatch(response.eTag);
    }

    return super.closeOutput();
  }

  @Override
  public int code() throws HttpRequestException {
    final int code = super.code();

    if (code != HTTP_NOT_MODIFIED)
      closeCacheResponse();
    return code;
  }

  @Override
  public HttpRequest disconnect() {
    closeCacheResponse();

    return super.disconnect();
  }

  @Override
  public InputStream stream() throws HttpRequestException {
    if (notModified() && response != null) {
      cache.registerHit();
      return response.body;
    }

    if (ok()) {
      cache.registerMiss();
      final InputStream streamWrapper = cache.put(getConnection());
      if (streamWrapper != null)
        return streamWrapper;
    }

    closeCacheResponse();

    return super.stream();
  }
}
