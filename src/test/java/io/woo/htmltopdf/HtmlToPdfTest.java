package io.woo.htmltopdf;

import com.sun.net.httpserver.HttpServer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.wkhtmltopdf.WkHtmlToXException;
import org.wkhtmltopdf.wkhtmltopdf.HtmlToPdfConverter;
import org.wkhtmltopdf.wkhtmltopdf.WkHtmlToPdfObject;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class HtmlToPdfTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void convertReturnsTrueWhenConversionSuccessful() throws IOException {
        File file = tempFolder.newFile();
        boolean result = HtmlToPdfConverter.create()
                .object(WkHtmlToPdfObject.forHtml("<p>Test</p>"))
                .saveAsPdf(file.getPath());

        assertTrue(result);
    }

    @Test
    public void convertReturnsFalseWhenConversionFailed() throws IOException {
        File file = tempFolder.newFile();
        boolean result = HtmlToPdfConverter.create()
                .object(WkHtmlToPdfObject.forUrl("file:///path/that/does/not/exist"))
                .saveAsPdf(file.getPath());
        assertFalse(result);
    }

    @Test
    public void convertReturnsFalseWhenNoObjectsSpecified() throws IOException {
        File file = tempFolder.newFile();
        boolean result = HtmlToPdfConverter.create().saveAsPdf(file.getPath());
        assertFalse(result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void creatingObjectForHtmlWithNoContentThrowsException() {
        WkHtmlToPdfObject.forHtml("");
    }

    @Test(expected = WkHtmlToXException.class)
    public void convertToInputStreamThrowsExceptionOnFailure() {
        HtmlToPdfConverter.create()
                .object(WkHtmlToPdfObject.forUrl("file:///path/that/does/not/exist"))
                .toInputStream();
    }

    @Test
    public void itDoesNotHangWhenAccessedByMultipleThreads() throws InterruptedException {
        int concurrency = 5;

        AtomicReference<InterruptedException> interrupted = new AtomicReference<>();
        ExecutorService service = Executors.newFixedThreadPool(concurrency);
        CountDownLatch latch = new CountDownLatch(concurrency);

        AtomicInteger converted = new AtomicInteger();

        IntStream.range(0, concurrency)
                .<Runnable>mapToObj((i) -> () -> {
                    latch.countDown();
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        interrupted.set(e);
                    }
                    try {
                        HtmlToPdfConverter.create()
                                .object(WkHtmlToPdfObject.forHtml("<p>test</p>"))
                                .toInputStream();
                        converted.getAndIncrement();
                    } catch (WkHtmlToXException e) {
                        e.printStackTrace();
                    }
                })
                .forEach(service::submit);
        service.shutdown();
        service.awaitTermination(10, TimeUnit.MINUTES);

        if (interrupted.get() != null) {
            throw interrupted.get();
        }

        assertEquals(concurrency, converted.get());
    }

    @Test
    public void itReleasesHandleToPdfFileWhenConversionIsDone() throws IOException {
        File file = tempFolder.newFile();
        HtmlToPdfConverter.create()
                .object(WkHtmlToPdfObject.forHtml("<p>test</p>"))
                .saveAsPdf(file.getPath());
        assertTrue(file.renameTo(file));
    }

    @Test
    public void itConvertsMarkupFromUrlToPdf() throws IOException {
        String html = "<html><head><title>Test page</title></head><body><p>This is just a simple test.</p></html>";

        HttpServer httpServer = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        httpServer.createContext("/test", httpExchange -> {
            httpExchange.sendResponseHeaders(200, html.length());
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(html.getBytes(StandardCharsets.UTF_8));
            }
        });
        httpServer.start();

        String url = String.format("http://127.0.0.1:%d/test",
                httpServer.getAddress().getPort());

        boolean success = HtmlToPdfConverter.create()
                .object(WkHtmlToPdfObject.forUrl(url))
                .saveAsPdf("/dev/null");

        assertTrue(success);
    }

}