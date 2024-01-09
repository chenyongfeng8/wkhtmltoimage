package org.wkhtmltopdf.wkhtmltoimage;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import org.wkhtmltopdf.WkHtmlToX;
import org.wkhtmltopdf.WkHtmlToXException;
import org.wkhtmltopdf.WkHtmlToXProgress;
import org.wkhtmltopdf.WkHtmlToXTaskExecutor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class HtmlToImageConverter {

    private final WkHtmlToXTaskExecutor taskExecutor;
    private final String htmlData;
    private final Map<String, String> settings;
    private final List<Consumer<String>> warningCallbacks = new ArrayList<>();
    private final List<Consumer<String>> errorCallbacks = new ArrayList<>();
    private final List<Consumer<WkHtmlToXProgress>> progressChangedCallbacks = new ArrayList<>();
    private final List<Consumer<Boolean>> finishedCallbacks = new ArrayList<>();
    private static final AtomicBoolean initialized = new AtomicBoolean(false);

    private HtmlToImageConverter(WkHtmlToXTaskExecutor taskExecutor, String htmlData, Map<String, String> settings) {
        this.taskExecutor = taskExecutor;
        this.htmlData = htmlData;
        this.settings = settings;
    }

    public static HtmlToImageConverter fromHtml(String htmlData) {
        return fromHtml(htmlData, new HashMap<>());
    }

    public static HtmlToImageConverter fromHtml(String htmlData, Map<String, String> settings) {
        WkHtmlToXTaskExecutor executor = WkHtmlToXTaskExecutor.getInstance();
        return new HtmlToImageConverter(executor, htmlData, settings);
    }

    /**
     * left/x coordinate of the window to capture in pixels. E.g. "200"
     */
    public HtmlToImageConverter cropLeft(int cropLeft) {
        return setting("crop.left", cropLeft);
    }

    /**
     * top/y coordinate of the window to capture in pixels. E.g. "200"
     */
    public HtmlToImageConverter cropTop(int cropTop) {
        return setting("crop.top", cropTop);
    }

    /**
     * Width of the window to capture in pixels. E.g. "200"
     */
    public HtmlToImageConverter cropWidth(int cropWidth) {
        return setting("crop.width", cropWidth);
    }

    /**
     * Height of the window to capture in pixels. E.g. "200"
     */
    public HtmlToImageConverter cropHeight(int cropHeight) {
        return setting("crop.height", cropHeight);
    }

    /**
     * The cookie jar to use when loading and storing cookies.
     */
    public HtmlToImageConverter cookieJar(String cookieJar) {
        return setting("load.cookieJar", cookieJar);
    }

    /**
     * When outputting a PNG or SVG, make the white background transparent. Must be either "true" or "false"
     */
    public HtmlToImageConverter transparent(boolean transparent) {
        return setting("transparent", transparent);
    }

    /**
     * The URL or path of the input file, if "-" stdin is used. E.g. "http://google.com"
     */
    public HtmlToImageConverter in(String in) {
        return setting("in", in);
    }


    /**
     * The output format to use, must be either "", "jpg", "png", "bmp" or "svg".
     */
    public HtmlToImageConverter fmt(String fmt) {
        return setting("fmt", fmt);
    }

    /**
     * The with of the screen used to render is pixels, e.g "800".
     */
    public HtmlToImageConverter screenWidth(int screenWidth) {
        return setting("screenWidth", screenWidth);
    }

    /**
     * Should we expand the screenWidth if the content does not fit? must be either "true" or "false".
     */
    public HtmlToImageConverter smartWidth(String smartWidth) {
        return setting("smartWidth", smartWidth);
    }

    /**
     * The compression factor to use when outputting a JPEG image. E.g. "94".
     */
    public HtmlToImageConverter quality(int quality) {
        return setting("quality", quality);
    }

    private HtmlToImageConverter setting(String name, Object value) {
        return setting(name, value.toString());
    }

    private HtmlToImageConverter setting(String name, String value) {
        settings.put(name, value);
        return this;
    }

    public HtmlToImageConverter warning(Consumer<String> warningConsumer) {
        warningCallbacks.add(warningConsumer);
        return this;
    }

    public HtmlToImageConverter error(Consumer<String> errorConsumer) {
        errorCallbacks.add(errorConsumer);
        return this;
    }

    public HtmlToImageConverter progress(Consumer<WkHtmlToXProgress> progressChangeConsumer) {
        progressChangedCallbacks.add(progressChangeConsumer);
        return this;
    }

    public HtmlToImageConverter finished(Consumer<Boolean> finishConsumer) {
        finishedCallbacks.add(finishConsumer);
        return this;
    }

    public HtmlToImageConverter success(Runnable successRunnable) {
        return finished(success -> {
            if (success) {
                successRunnable.run();
            }
        });
    }

    public HtmlToImageConverter failure(Runnable failureRunnable) {
        return finished(success -> {
            if (!success) {
                failureRunnable.run();
            }
        });
    }

    public InputStream toInputStream() {
        Map<String, String> settings = new HashMap<>(this.settings);
        settings.remove("out");
        return withConverter(settings, (c, wkHtmlToX) -> {
            List<String> log = new ArrayList<>();
            warning(w -> log.add("Warning: " + w));
            error(e -> log.add("Error: " + e));
            PointerByReference out = new PointerByReference();
            if (wkHtmlToX.wkhtmltoimage_convert(c) == 1) {
                long size = wkHtmlToX.wkhtmltoimage_get_output(c, out);
                byte[] pdfBytes = new byte[(int) size];
                out.getValue().read(0, pdfBytes, 0, pdfBytes.length);
                return new ByteArrayInputStream(pdfBytes);
            } else {
                throw new WkHtmlToXException("Conversion returned with failure. Log:\n"
                        + log.stream().collect(Collectors.joining("\n")));
            }
        });
    }

    public boolean saveAsImage(String path) {
        Map<String, String> settings = new HashMap<>(this.settings);
        settings.put("out", path);
        return withConverter(settings, (c, wkHtmlToX) -> wkHtmlToX.wkhtmltoimage_convert(c) == 1);
    }

    private <T> T withConverter(Map<String, String> settings, BiFunction<Pointer, WkHtmlToX, T> consumer) {
        return taskExecutor.execute(wkHtmlToX -> {
            initWkHtmlToImage(wkHtmlToX);
            Pointer globalSettings = wkHtmlToX.wkhtmltoimage_create_global_settings();
            settings.forEach((k, v) -> wkHtmlToX.wkhtmltoimage_set_global_setting(globalSettings, k, v));
            Pointer converter = wkHtmlToX.wkhtmltoimage_create_converter(globalSettings, htmlData);
            wkHtmlToX.wkhtmltoimage_set_warning_callback(converter, (c, s) -> warningCallbacks.forEach(wc -> wc.accept(s)));
            wkHtmlToX.wkhtmltoimage_set_error_callback(converter, (c, s) -> errorCallbacks.forEach(ec -> ec.accept(s)));
            wkHtmlToX.wkhtmltoimage_set_progress_changed_callback(converter, (c, phaseProgress) -> {
                int phase = wkHtmlToX.wkhtmltoimage_current_phase(c);
                int totalPhases = wkHtmlToX.wkhtmltoimage_phase_count(c);
                String phaseDesc = wkHtmlToX.wkhtmltoimage_phase_description(c, phase);
                WkHtmlToXProgress progress = new WkHtmlToXProgress(
                        phase,
                        phaseDesc,
                        totalPhases,
                        phaseProgress);
                progressChangedCallbacks.forEach(pc -> pc.accept(progress));
            });
            wkHtmlToX.wkhtmltoimage_set_finished_callback(converter, (c, i) -> finishedCallbacks.forEach(fc -> fc.accept(i == 1)));
            try {
                return consumer.apply(converter, wkHtmlToX);
            } finally {
                wkHtmlToX.wkhtmltoimage_destroy_converter(converter);
            }
        });
    }

    private static final Object initLock = new Object();

    private static void initWkHtmlToImage(WkHtmlToX wkHtmlToX) {
        if (!initialized.get()) {
            synchronized (initLock) {
                if (!initialized.get()) {
                    wkHtmlToX.wkhtmltoimage_init(0);
                    initialized.set(true);
                }
            }
        }
    }

}
