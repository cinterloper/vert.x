/*
 * Copyright (c) 2011-2013 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 *     The Eclipse Public License is available at
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 *     The Apache License v2.0 is available at
 *     http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */

package io.vertx.core.spi.data;


import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.shareddata.Counter;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.Lock;


import java.util.List;
import java.util.Map;

public interface LocalDataProvider {

  void setVertx(Vertx vertx);

  <K, V> void getMap(String name, Handler<AsyncResult<LocalMap<K, V>>> resultHandler);

  <K, V> LocalMap<K, V> getSyncMap(String name);

  void getLockWithTimeout(String name, long timeout, Handler<AsyncResult<Lock>> resultHandler);

  void getCounter(String name, Handler<AsyncResult<Counter>> resultHandler);

  void start(Handler<AsyncResult<Void>> resultHandler);

  void stop(Handler<AsyncResult<Void>> resultHandler);

  boolean isActive();
}
