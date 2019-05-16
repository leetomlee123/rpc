package com.lx.rpc.product.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.io.Resources;
import com.lx.rpc.product.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class ScanClassUtils {
    public static Map<String, Class> class2rpc = new HashMap<>();
    public static LoadingCache<Class, Object> beans;
    private static final Class ANONOTATION_NAME = RpcService.class;

    static {
       beans= CacheBuilder.newBuilder()
                //设置缓存最大容量为100，超过100之后就会按照LRU最近虽少使用算法来移除缓存项
                //build方法中可以指定CacheLoader，在缓存不存在时通过CacheLoader的实现自动加载缓存
                .build(
                        new CacheLoader<Class, Object>() {
                            @Override
                            public Object load(Class key) throws Exception {
                                return key.newInstance();
                            }
                        }

                );
    }


    public static void init() {
        URL resource = Resources.getResource("");
        File file = new File(resource.getFile());
        getClass(file);
    }

    private static void getClass(File file) {
        for (File listFile : Objects.requireNonNull(file.listFiles())) {
            if (listFile.isDirectory()) {
                getClass(listFile);
            } else {
                if (listFile.getName().endsWith(".class")) {
                    String path = listFile.getPath();
                    String classes = path.substring(path.lastIndexOf("classes") + 8, path.indexOf(".")).replace(File.separator, ".");
                    try {
                        Class<?> aClass = Class.forName(classes);
                        if (aClass.isAnnotationPresent(ANONOTATION_NAME)) {
                            String simpleName = aClass.getInterfaces()[0].getSimpleName();
                            class2rpc.put(simpleName, aClass);
                            beans.put(aClass, aClass.newInstance());
                        }

                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
