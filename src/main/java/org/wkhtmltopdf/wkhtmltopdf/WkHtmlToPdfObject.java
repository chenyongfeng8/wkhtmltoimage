package org.wkhtmltopdf.wkhtmltopdf;

import org.wkhtmltopdf.WkValue;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class WkHtmlToPdfObject {

    /**
     * Creates a new {@code HtmlToPdfObject} using the specified HTML as content.
     *
     * @param html The HTML code to convert to PDF.
     * @return The created {@code HtmlToPdfObject} instance.
     */
    public static WkHtmlToPdfObject forHtml(String html) {
        return forHtml(html, new HashMap<>());
    }

    /**
     * Creates a new {@code HtmlToPdfObject} using the specified HTML as content.
     *
     * @param html     The HTML code to convert to PDF.
     * @param settings The settings to use at the new instance.
     * @return The created {@code HtmlToPdfObject} instance.
     */
    public static WkHtmlToPdfObject forHtml(String html, Map<String, String> settings) {
        if (html == null || html.isEmpty() || html.startsWith("\0")) {
            throw new IllegalArgumentException("No content specified for object.");
        }
        return new WkHtmlToPdfObject(html, settings);
    }

    /**
     * Creates a new {@code HtmlToPdfObject} for the specified URL. The content will be
     * obtained from the specified URL during the conversion process.
     *
     * @param url The URL to obtain HTML content from.
     * @return The created {@code HtmlToPdfObject} instance.
     */
    public static WkHtmlToPdfObject forUrl(String url) {
        return forUrl(url, new HashMap<>());
    }

    /**
     * Creates a new {@code HtmlToPdfObject} for the specified URL, using the specified
     * settings. The content will be obtained from the specified URL during the conversion
     * process.
     *
     * @param url      The URL to obtain the content from.
     * @param settings The settings to use.
     * @return The created {@code HtmlToPdfObject} instance.
     */
    public static WkHtmlToPdfObject forUrl(String url, Map<String, String> settings) {
        settings.put("page", url);
        return new WkHtmlToPdfObject(null, settings);
    }

    private final Map<String, String> settings;
    private final String htmlData;

    private WkHtmlToPdfObject(String htmlData, Map<String, String> settings) {
        this.settings = settings;
        this.htmlData = htmlData;
    }

    /**
     * Whether or not to show the page's background.
     */
    public WkHtmlToPdfObject showBackground(boolean background) {
        return setting("web.background", background);
    }

    /**
     * Whether or not to load images.
     */
    public WkHtmlToPdfObject loadImages(boolean load) {
        return setting("web.loadImages", load);
    }

    /**
     * Whether or not to enable javascript.
     */
    public WkHtmlToPdfObject enableJavascript(boolean enable) {
        return setting("web.enableJavascript", enable);
    }

    /**
     * Whether or not to enable Intelligent Shrinking. Intelligent Shrinking will
     * attempt to fit more content into pages if enabled.
     */
    public WkHtmlToPdfObject enableIntelligentShrinking(boolean enable) {
        return setting("web.enableIntelligentShrinking", enable);
    }

    /**
     * The minimum font size allowed.
     */
    public WkHtmlToPdfObject minimumFontSize(int size) {
        return setting("web.minimumFontSize", size);
    }

    /**
     * Whether or not to use "print" media type (instead of "screen" media type) for CSS styles.
     */
    public WkHtmlToPdfObject usePrintMediaType(boolean use) {
        return setting("web.printMediaType", use);
    }

    /**
     * The character encoding to use when not specified by the webpage (e.g. "utf-8")
     */
    public WkHtmlToPdfObject defaultEncoding(String encoding) {
        return setting("web.defaultEncoding", encoding);
    }

    /**
     * A stylesheet to apply for the conversion.
     *
     * @param urlOrPath The URL or path to the stylesheet to apply.
     */
    public WkHtmlToPdfObject userStylesheet(String urlOrPath) {
        return setting("web.userStyleSheet", urlOrPath);
    }

    /**
     * The auth username to use when requesting the webpage.
     */
    public WkHtmlToPdfObject authUsername(String username) {
        return setting("load.username", username);
    }

    /**
     * The auth password to use when requesting the webpage.
     */
    public WkHtmlToPdfObject authPassword(String password) {
        return setting("load.password", password);
    }

    /**
     * Delay, in milliseconds, to allow between the time when the page has been
     * loaded and the time of conversion to PDF. This might be needed to allow
     * javascript to load and display content.
     */
    public WkHtmlToPdfObject javascriptDelay(int delayMs) {
        return setting("load.jsdelay", delayMs);
    }

    /**
     * Amount of zoom to use when converting.
     */
    public WkHtmlToPdfObject zoomFactor(float factor) {
        return setting("load.zoomFactor", factor);
    }

    /**
     * Whether or not to block the webpage from accessing local file access.
     */
    public WkHtmlToPdfObject blockLocalFileAccess(boolean block) {
        return setting("load.blockLocalFileAccess", block);
    }

    /**
     * Whether or not to stop slow running javascript.
     */
    public WkHtmlToPdfObject stopSlowScript(boolean stop) {
        return setting("load.stopSlowScript", stop);
    }

    /**
     * Whether or not to debug javascript warnings and errors. If enabled,
     * warnings and errors from javascript will be added to {@link WkHtmlToPdfObject}
     * warning callback.
     *
     * @see HtmlToPdfConverter#warning(Consumer)
     */
    public WkHtmlToPdfObject debugJavascriptWarningsAndErrors(boolean debug) {
        return setting("load.debugJavascript", debug);
    }

    /**
     * Specifies the way in which errors are handled for this object.
     */
    public WkHtmlToPdfObject handleErrors(ObjectErrorHandling errorHandling) {
        return setting("load.loadErrorHandling", errorHandling);
    }

    /**
     * The font size of the custom header to add.
     */
    public WkHtmlToPdfObject headerFontSize(int size) {
        return setting("header.fontSize", size);
    }

    /**
     * The font name of the custom header to add.
     */
    public WkHtmlToPdfObject headerFontName(String fontName) {
        return setting("header.fontName", fontName);
    }

    /**
     * Whether or not to add a line beneath the custom header.
     */
    public WkHtmlToPdfObject headerLine(boolean line) {
        return setting("header.line", line);
    }

    /**
     * The amount of spacing between the header and the content.
     */
    public WkHtmlToPdfObject headerSpacing(int spacing) {
        return setting("header.spacing", spacing);
    }

    /**
     * URL for an HTML document to use for the header.
     */
    public WkHtmlToPdfObject headerHtmlUrl(String url) {
        return setting("header.htmlUrl", url);
    }

    /**
     * Text to write in the left part of the header.
     */
    public WkHtmlToPdfObject headerLeft(String text) {
        return setting("header.left", text);
    }

    /**
     * Text to write in the center part of the header.
     */
    public WkHtmlToPdfObject headerCenter(String text) {
        return setting("header.center", text);
    }

    /**
     * Text to write in the right part of the header.
     */
    public WkHtmlToPdfObject headerRight(String text) {
        return setting("header.right", text);
    }

    /**
     * Whether or not to use dotted lines for the table of contents.
     */
    public WkHtmlToPdfObject tableOfContentsDottedLines(boolean dottedLines) {
        return setting("toc.useDottedLines", dottedLines);
    }

    /**
     * The caption text to use for the table of contents.
     */
    public WkHtmlToPdfObject tableOfContentsCaptionText(String captionText) {
        return setting("toc.captionText", captionText);
    }

    /**
     * Whether or not the table of contents should link to the content in the PDF document.
     */
    public WkHtmlToPdfObject tableOfContentsForwardLinks(boolean forward) {
        return setting("toc.forwardLinks", forward);
    }

    /**
     * Whether or not content should link back to the table of contents.
     */
    public WkHtmlToPdfObject tableOfContentsBackLinks(boolean backLinks) {
        return setting("toc.backLinks", backLinks);
    }

    /**
     * The indentation to use for the table of contents. This string is a size
     * such as used in CSS ("5px", "2em" etc.)
     */
    public WkHtmlToPdfObject tableOfContentsIndentation(String indentation) {
        return setting("toc.indentation", indentation);
    }

    /**
     * The scale-down per indentation of the table of contents. For instance, a value of
     * {@code 0.8} will scale the font down by 20% for every level in the table of contents.
     */
    public WkHtmlToPdfObject tableOfContentsIndentationFontScaleDown(float scale) {
        return setting("toc.fontScale", scale);
    }

    /**
     * Whether or not sections from the document should be included in the outline of the
     * table of contents.
     */
    public WkHtmlToPdfObject tableOfContentsIncludeSections(boolean include) {
        return setting("includeInOutline", include);
    }

    /**
     * Whether or not to keep external links.
     */
    public WkHtmlToPdfObject useExternalLinks(boolean use) {
        return setting("useExternalLinks", use);
    }

    /**
     * Whether or not to convert links within the webpage to PDF references.
     */
    public WkHtmlToPdfObject convertInternalLinksToPdfReferences(boolean convert) {
        return setting("useLocalLinks", convert);
    }

    /**
     * Whether or not to turn HTML forms into PDF forms.
     */
    public WkHtmlToPdfObject produceForms(boolean produce) {
        return setting("produceForms", produce);
    }

    /**
     * Whether or not to include page count in the header, footer, and table of contents.
     */
    public WkHtmlToPdfObject pageCount(boolean pageCount) {
        return setting("pagesCount", pageCount);
    }

    private WkHtmlToPdfObject setting(String name, Object value) {
        return setting(name, value.toString());
    }

    private WkHtmlToPdfObject setting(String name, WkValue value) {
        return setting(name, value.getWkValue());
    }

    private WkHtmlToPdfObject setting(String name, String value) {
        settings.put(name, value);
        return this;
    }

    Map<String, String> getSettings() {
        return settings;
    }

    String getHtmlData() {
        return htmlData;
    }
}
