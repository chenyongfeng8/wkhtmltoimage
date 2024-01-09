package org.wkhtmltopdf;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

public final class WkHtmlToXTaskExecutor {
    private static final WkHtmlToXTaskExecutor instance = new WkHtmlToXTaskExecutor();
    private final ExecutorService executorService;
    private final WkHtmlToX wkHtmlToX;

    private WkHtmlToXTaskExecutor() {
        executorService = Executors.newSingleThreadExecutor(r -> {
            Thread thread = Executors.defaultThreadFactory().newThread(r);
            thread.setDaemon(true);
            return thread;
        });
        wkHtmlToX = WkHtmlToXLibraryLoader.getInstance();
    }

    public static WkHtmlToXTaskExecutor getInstance() {
        return instance;
    }

    public <T> T execute(Function<WkHtmlToX, T> fn) {
        try {
            return executorService.submit(() -> {
                T t = fn.apply(wkHtmlToX);
                return t;
            }).get();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw new IllegalStateException(e);
        } catch (InterruptedException e) {
            throw new IllegalStateException("Thread is interrupted!", e);
        }
    }

}


