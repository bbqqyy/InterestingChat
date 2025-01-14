package com.bqy.common.common.service.cache;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.Iterables;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class AbstractLocalCache<IN,OUT> implements BatchCache<IN,OUT>{
    private Class<IN> inClass;
    private Class<OUT> outClass;
    private LoadingCache<IN,OUT> loadingCache;
    protected AbstractLocalCache(){
        init(60,10*60,1024);
    };
    protected AbstractLocalCache(long refreshSeconds,long expireSeconds,int maxSize){
        init(refreshSeconds,expireSeconds,maxSize);
    }
    private void init(long refreshSeconds,long expireSeconds,int maxSize){
        ParameterizedType generateSuperClass = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.inClass = (Class<IN>) generateSuperClass.getActualTypeArguments()[0];
        this.outClass = (Class<OUT>) generateSuperClass.getActualTypeArguments()[1];
        loadingCache = Caffeine.newBuilder()
                .refreshAfterWrite(refreshSeconds, TimeUnit.SECONDS)
                .expireAfterWrite(expireSeconds,TimeUnit.SECONDS)
                .maximumSize(maxSize)
                .build(new CacheLoader<IN,OUT>() {
                    @Nullable
                    @Override
                    public OUT load(@NonNull IN in) throws Exception {
                        return AbstractLocalCache.this.load(Collections.singletonList(in)).get(in);
                    }

                    @Override
                    public @NonNull Map<@NonNull IN, @NonNull OUT> loadAll(@NonNull Iterable<? extends @NonNull IN> keys) throws Exception {
                        IN[] ins = Iterables.toArray(keys,inClass);
                        return AbstractLocalCache.this.load(Arrays.asList(ins));

                    }
                });
    }
    protected abstract Map<IN, OUT> load(List<IN> req);

    @Override
    public OUT get(IN req) {
        return loadingCache.get(req);
    }

    @Override
    public Map<IN, OUT> getBatch(List<IN> req) {
        return loadingCache.getAll(req);
    }

    @Override
    public void delete(IN req) {
        loadingCache.invalidate(req);
    }

    @Override
    public void deleteBatch(List<IN> req) {
        loadingCache.invalidateAll(req);
    }
}
