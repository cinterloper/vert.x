package io.vertx.core.shareddata.impl;

import io.vertx.core.*;
import io.vertx.core.impl.VertxInternal;
import io.vertx.core.shareddata.Counter;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.Lock;
import io.vertx.core.spi.data.LocalDataProvider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by g on 11/25/16.
 */
public class DefaultLocalDataProvider implements LocalDataProvider {
    boolean active = false;
    Vertx vertx;
    private final ConcurrentMap<String, AsynchronousLock> localLocks = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Counter> localCounters = new ConcurrentHashMap<>();
    private final ConcurrentMap<Object, LocalMap<?, ?>> localMaps = new ConcurrentHashMap<>();
    @Override
    public void setVertx(Vertx vertx) {
        this.vertx = vertx;
    }


    @Override
    @SuppressWarnings("unchecked")
    public <K, V> void getMap(String name, Handler<AsyncResult<LocalMap<K, V>>> asyncResultHandler) {
        asyncResultHandler.handle(Future.succeededFuture(getSyncMap(name)));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> LocalMap<K, V> getSyncMap(String name) {
        LocalMap<K, V> map = (LocalMap<K, V>) localMaps.get(name);
        if (map == null) {
            map = new LocalMapImpl<>(name, localMaps);
            LocalMap prev = localMaps.putIfAbsent(name, map);
            if (prev != null) {
                map = prev;
            }
        }
        return map;
    }

    @Override
    public void getLockWithTimeout(String name, long timeout, Handler<AsyncResult<Lock>> resultHandler) {
        AsynchronousLock lock = new AsynchronousLock(vertx);
        AsynchronousLock prev = localLocks.putIfAbsent(name, lock);
        if (prev != null) {
            lock = prev;
        }
        lock.acquire(timeout, resultHandler);
    }

    @Override
    public void getCounter(String name, Handler<AsyncResult<Counter>> resultHandler) {
        Counter counter = new AsynchronousCounter(vertx);
        Counter prev = localCounters.putIfAbsent(name, counter);
        if (prev != null) {
            counter = prev;
        }
        Counter theCounter = counter;
        Context context = vertx.getOrCreateContext();
        context.runOnContext(v -> resultHandler.handle(Future.succeededFuture(theCounter)));
    }

    @Override
    public void start(Handler<AsyncResult<Void>> resultHandler) {
        this.active=true;
        resultHandler.handle(Future.succeededFuture());
    }

    @Override
    public void stop(Handler<AsyncResult<Void>> resultHandler) {
        this.active=false;
        resultHandler.handle(Future.succeededFuture());
    }

    @Override
    public boolean isActive() {
        return this.active;
    }
}
