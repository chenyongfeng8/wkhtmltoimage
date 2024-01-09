package org.wkhtmltopdf.wkhtmltopdf;

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

public class HtmlToPdfConverter {

    private final WkHtmlToXTaskExecutor taskExecutor;
    private final Map<String, String> settings;
    private final List<WkHtmlToPdfObject> objects;
    private final List<Consumer<String>> warningCallbacks = new ArrayList<>();
    private final List<Consumer<String>> errorCallbacks = new ArrayList<>();
    private final List<Consumer<WkHtmlToXProgress>> progressChangedCallbacks = new ArrayList<>();
    private final List<Consumer<Boolean>> finishedCallbacks = new ArrayList<>();
    private static final AtomicBoolean initialized = new AtomicBoolean(false);

    private HtmlToPdfConverter(WkHtmlToXTaskExecutor taskExecutor, Map<String, String> settings) {
        this.taskExecutor = taskExecutor;
        this.objects = new ArrayList<>();
        this.settings = settings;
    }

    /**
     * Creates a new {@code HtmlToPdf} instance.
     */
    public static HtmlToPdfConverter create() {
        return create(new HashMap<>());
    }

    /**
     * Creates a new {@code HtmlToPdf} instance.
     *
     * @param settings The settings to use at the new instance.
     * @return The created {@code HtmlToPdf} instance.
     */
    public static HtmlToPdfConverter create(Map<String, String> settings) {
        WkHtmlToXTaskExecutor executor = WkHtmlToXTaskExecutor.getInstance();
        return new HtmlToPdfConverter(executor, settings);
    }

    /**
     * Disable the intelligent shrinking strategy used by WebKit that makes the pixel/dpi ratio none constant
     */
    public HtmlToPdfConverter disableSmartShrinking(boolean disableSmartShrinking) {
        return setting("disable-smart-shrinking", disableSmartShrinking);
    }

    /**
     * The paper size of the output document.
     */
    public HtmlToPdfConverter pageSize(PdfPageSize pageSize) {
        return setting("size.pageSize", pageSize);
    }

    /**
     * The orientation of the output document.
     */
    public HtmlToPdfConverter orientation(PdfOrientation orientation) {
        return setting("orientation", orientation);
    }

    /**
     * The color mode of the output document.
     */
    public HtmlToPdfConverter colorMode(PdfColorMode colorMode) {
        return setting("colorMode", colorMode);
    }

    /**
     * The DPI of the output document.
     */
    public HtmlToPdfConverter dpi(int dpi) {
        return setting("dpi", dpi);
    }

    /**
     * Whether or not to collate copies.
     */
    public HtmlToPdfConverter collate(boolean collate) {
        return setting("collate", collate);
    }

    /**
     * Whether or not a table of contents should be generated. This is the table of contents
     * in the sidebar.
     */
    public HtmlToPdfConverter outline(boolean outline) {
        return setting("outline", outline);
    }

    /**
     * The maximum depth of the outline.
     */
    public HtmlToPdfConverter outlineDepth(int outlineDepth) {
        return setting("outlineDepth", outlineDepth);
    }

    /**
     * The title of the PDF document.
     */
    public HtmlToPdfConverter documentTitle(String title) {
        return setting("documentTitle", title);
    }

    /**
     * Whether or not loss-less compression should be used.
     */
    public HtmlToPdfConverter compression(boolean compression) {
        return setting("useCompression", compression);
    }

    /**
     * The size of the top margin (CSS value, e.g. "5in", "15px" etc.)
     */
    public HtmlToPdfConverter marginTop(String marginTop) {
        return setting("margin.top", marginTop);
    }

    /**
     * The size of the bottom margin (CSS value, e.g. "5in", "15px" etc.)
     */
    public HtmlToPdfConverter marginBottom(String marginBottom) {
        return setting("margin.bottom", marginBottom);
    }

    /**
     * The size of the left margin (CSS value, e.g. "5in", "15px" etc.)
     */
    public HtmlToPdfConverter marginLeft(String marginLeft) {
        return setting("margin.left", marginLeft);
    }

    /**
     * The size of the right margin (CSS value, e.g. "5in", "15px" etc.)
     */
    public HtmlToPdfConverter marginRight(String marginRight) {
        return setting("margin.right", marginRight);
    }

    /**
     * The maximum DPI to use for images.
     */
    public HtmlToPdfConverter imageDpi(int imageDpi) {
        return setting("imageDPI", imageDpi);
    }

    /**
     * JPEG compression factor (1-100)
     */
    public HtmlToPdfConverter imageQuality(int quality) {
        return setting("imageQuality", quality);
    }

    /**
     * The cookie jar to use when loading and storing cookies.
     */
    public HtmlToPdfConverter cookieJar(String cookieJar) {
        return setting("load.cookieJar", cookieJar);
    }

    /**
     * Adds an object to be converted.
     */
    public HtmlToPdfConverter object(WkHtmlToPdfObject object) {
        objects.add(object);
        return this;
    }

    private HtmlToPdfConverter setting(String name, Object value) {
        return setting(name, value.toString());
    }

    private HtmlToPdfConverter setting(String name, String value) {
        settings.put(name, value);
        return this;
    }

    public HtmlToPdfConverter warning(Consumer<String> warningConsumer) {
        warningCallbacks.add(warningConsumer);
        return this;
    }

    public HtmlToPdfConverter error(Consumer<String> errorConsumer) {
        errorCallbacks.add(errorConsumer);
        return this;
    }

    public HtmlToPdfConverter progress(Consumer<WkHtmlToXProgress> progressChangeConsumer) {
        progressChangedCallbacks.add(progressChangeConsumer);
        return this;
    }

    public HtmlToPdfConverter finished(Consumer<Boolean> finishConsumer) {
        finishedCallbacks.add(finishConsumer);
        return this;
    }

    public HtmlToPdfConverter success(Runnable successRunnable) {
        return finished(success -> {
            if (success) {
                successRunnable.run();
            }
        });
    }

    public HtmlToPdfConverter failure(Runnable failureRunnable) {
        return finished(success -> {
            if (!success) {
                failureRunnable.run();
            }
        });
    }

    /**
     * Performs the conversion, saving the result PDF to the specified path.
     *
     * @return {@code true} if the conversion process completed successfully,
     * or {@code false} otherwise.
     */
    public boolean saveAsPdf(String path) {
        if (objects.isEmpty()) {
            return false;
        }
        Map<String, String> settings = new HashMap<>(this.settings);
        settings.put("out", path);
        return withConverter(settings, (c, wkHtmlToX) -> wkHtmlToX.wkhtmltoimage_convert(c) == 1);
    }

    /**
     * Performs the conversion, returning an {@code InputStream} with the
     * bytes of the resulting PDF.
     *
     * @throws WkHtmlToXException if conversion failed
     */
    public InputStream toInputStream() {
        Map<String, String> settings = new HashMap<>(this.settings);
        settings.remove("out");
        return withConverter(settings, (c, wkHtmlToX) -> {
            List<String> log = new ArrayList<>();
            warning(w -> log.add("Warning: " + w));
            error(e -> log.add("Error: " + e));
            PointerByReference out = new PointerByReference();
            if (wkHtmlToX.wkhtmltopdf_convert(c) == 1) {
                long size = wkHtmlToX.wkhtmltopdf_get_output(c, out);
                byte[] pdfBytes = new byte[(int) size];
                out.getValue().read(0, pdfBytes, 0, pdfBytes.length);
                return new ByteArrayInputStream(pdfBytes);
            } else {
                throw new WkHtmlToXException("Conversion returned with failure. Log:\n"
                        + log.stream().collect(Collectors.joining("\n")));
            }
        });
    }

    private <T> T withConverter(Map<String, String> settings, BiFunction<Pointer, WkHtmlToX, T> consumer) {
        return taskExecutor.execute(wkHtmlToX -> {
            initializeWkHtmlToPdf(wkHtmlToX);
            Pointer globalSettings = wkHtmlToX.wkhtmltopdf_create_global_settings();
            settings.forEach((k, v) -> wkHtmlToX.wkhtmltopdf_set_global_setting(globalSettings, k, v));
            Pointer converter = wkHtmlToX.wkhtmltopdf_create_converter(globalSettings);
            wkHtmlToX.wkhtmltopdf_set_warning_callback(converter, (c, s) -> warningCallbacks.forEach(wc -> wc.accept(s)));
            wkHtmlToX.wkhtmltopdf_set_error_callback(converter, (c, s) -> errorCallbacks.forEach(ec -> ec.accept(s)));
            wkHtmlToX.wkhtmltopdf_set_progress_changed_callback(converter, (c, phaseProgress) -> {
                int phase = wkHtmlToX.wkhtmltopdf_current_phase(c);
                int totalPhases = wkHtmlToX.wkhtmltopdf_phase_count(c);
                String phaseDesc = wkHtmlToX.wkhtmltopdf_phase_description(c, phase);
                WkHtmlToXProgress progress = new WkHtmlToXProgress(
                        phase,
                        phaseDesc,
                        totalPhases,
                        phaseProgress);
                progressChangedCallbacks.forEach(pc -> pc.accept(progress));
            });
            wkHtmlToX.wkhtmltopdf_set_finished_callback(converter, (c, i) -> finishedCallbacks.forEach(fc -> fc.accept(i == 1)));
            try {
                objects.forEach((object) -> {
                    Pointer objectSettings = wkHtmlToX.wkhtmltopdf_create_object_settings();
                    object.getSettings().forEach((k, v) -> wkHtmlToX.wkhtmltopdf_set_object_setting(objectSettings, k, v));
                    wkHtmlToX.wkhtmltopdf_add_object(converter, objectSettings, object.getHtmlData());
                });
                return consumer.apply(converter, wkHtmlToX);
            } finally {
                wkHtmlToX.wkhtmltopdf_destroy_converter(converter);
            }
        });
    }

    private static final Object initLock = new Object();

    private static void initializeWkHtmlToPdf(WkHtmlToX wkHtmlToX) {
        if (!initialized.get()) {
            synchronized (initLock) {
                if (!initialized.get()) {
                    wkHtmlToX.wkhtmltopdf_init(0);
                    initialized.set(true);
                }
            }
        }
    }

}
