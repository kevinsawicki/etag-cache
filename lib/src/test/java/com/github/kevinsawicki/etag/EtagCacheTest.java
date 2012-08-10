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

import static com.github.kevinsawicki.etag.EtagCache.ONE_MB;
import static com.github.kevinsawicki.http.HttpRequest.HEADER_ETAG;
import static com.github.kevinsawicki.http.HttpRequest.HEADER_IF_NONE_MATCH;
import static java.net.HttpURLConnection.HTTP_NOT_MODIFIED;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.junit.Test;

/**
 * Unit tests of {@link EtagCache}
 */
public class EtagCacheTest extends ServerTestCase {

  /**
   * Verify request is inserted in cache and later retrievable
   *
   * @throws Exception
   */
  @Test
  public void cachedRequest() throws Exception {
    String url = setUp(new RequestHandler() {

      @Override
      public void handle(Request request, HttpServletResponse response) {
        response.setHeader(HEADER_ETAG, "1234");
        if ("1234".equals(request.getHeader(HEADER_IF_NONE_MATCH)))
          response.setStatus(HTTP_NOT_MODIFIED);
        else {
          write("hello");
          response.setStatus(HTTP_OK);
        }
      }
    });

    File file = File.createTempFile("cache", ".dir");
    assertTrue(file.delete());
    assertTrue(file.mkdirs());

    EtagCache cache = EtagCache.create(file, ONE_MB);
    assertNotNull(cache);
    assertEquals(0, cache.getHits());
    assertEquals(0, cache.getMisses());

    CacheRequest request = CacheRequest.get(url, cache);
    assertNull(cache.get(request.getConnection()));
    assertTrue(request.ok());
    assertEquals("hello", request.body());
    assertFalse(request.cached());
    assertEquals(0, cache.getHits());
    assertEquals(1, cache.getMisses());

    request = CacheRequest.get(url, cache);
    assertTrue(request.ok());
    assertEquals("hello", request.body());
    assertTrue(request.cached());
    assertNotNull(cache.get(request.getConnection()));
    assertEquals(1, cache.getHits());
    assertEquals(1, cache.getMisses());
  }

  /**
   * Verify server that always ignores the If-None-Match header
   *
   * @throws Exception
   */
  @Test
  public void etagIgnored() throws Exception {
    String url = setUp(new RequestHandler() {

      @Override
      public void handle(Request request, HttpServletResponse response) {
        response.setHeader(HEADER_ETAG, "1234");
        write("hello");
        response.setStatus(HTTP_OK);
      }
    });

    File file = File.createTempFile("cache", ".dir");
    assertTrue(file.delete());
    assertTrue(file.mkdirs());

    EtagCache cache = EtagCache.create(file, ONE_MB);
    assertNotNull(cache);
    assertEquals(0, cache.getHits());
    assertEquals(0, cache.getMisses());

    CacheRequest request = CacheRequest.get(url, cache);
    assertNull(cache.get(request.getConnection()));
    assertTrue(request.ok());
    assertEquals("hello", request.body());
    assertFalse(request.cached());
    assertEquals(0, cache.getHits());
    assertEquals(1, cache.getMisses());

    request = CacheRequest.get(url, cache);
    assertTrue(request.ok());
    assertEquals("hello", request.body());
    assertFalse(request.cached());
    assertNotNull(cache.get(request.getConnection()));
    assertEquals(0, cache.getHits());
    assertEquals(2, cache.getMisses());
  }

  /**
   * Verify cache stats reset
   *
   * @throws Exception
   */
  @Test
  public void resetStats() throws Exception {
    String url = setUp(new RequestHandler() {

      @Override
      public void handle(Request request, HttpServletResponse response) {
        response.setHeader(HEADER_ETAG, "1234");
        if ("1234".equals(request.getHeader(HEADER_IF_NONE_MATCH)))
          response.setStatus(HTTP_NOT_MODIFIED);
        else {
          write("hello");
          response.setStatus(HTTP_OK);
        }
      }
    });

    File file = File.createTempFile("cache", ".dir");
    assertTrue(file.delete());
    assertTrue(file.mkdirs());

    EtagCache cache = EtagCache.create(file, ONE_MB);
    assertNotNull(cache);
    assertEquals(0, cache.getHits());
    assertEquals(0, cache.getMisses());

    CacheRequest request = CacheRequest.get(url, cache);
    assertTrue(request.ok());
    request.body();
    assertEquals(0, cache.getHits());
    assertEquals(1, cache.getMisses());
    request = CacheRequest.get(url, cache);
    assertTrue(request.ok());
    assertEquals("hello", request.body());
    assertTrue(request.cached());
    assertEquals(1, cache.getHits());
    assertEquals(1, cache.getMisses());
    cache.resetStats();
    assertEquals(0, cache.getHits());
    assertEquals(0, cache.getMisses());
  }
}
