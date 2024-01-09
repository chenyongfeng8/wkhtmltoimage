## Overview

This project is based on [wkhtmltopdf](https://github.com/wkhtmltopdf/wkhtmltopdf) and [wkhtmltoimage](https://wkhtmltopdf.org/downloads.html), which convert HTML documents to PDF and images respectively. Access to wkhtmltopdf and wkhtmltoimage is performed via JNA, exposed through a Java-friendly layer.

## Getting started

The following examples should be sufficient to get you started, however there are many more options discoverable by looking into the methods of `HtmlToPdf`, `WkHtmlToPdfObject`, and `HtmlToImageConverter`.

### Saving HTML as a PDF file

```java
boolean success = HtmlToPdfConverter.create()
    .object(WkHtmlToPdfObject.forHtml("<p><em>Apples</em>, not oranges</p>"))
    .convert("/path/to/file.pdf");
```

### Saving a webpage from URL as a PDF file

```java
boolean success = HtmlToPdfConverter.create()
    .object(WkHtmlToPdfObject.forUrl("https://github.com/wooio/htmltopdf-java"))
    .convert("/path/to/file.pdf");
```

### Saving multiple objects as a PDF file

```java
boolean success = HtmlToPdfConverter.create()
    .object(WkHtmlToPdfObject.forUrl("https://github.com/wooio/htmltopdf-java"))
    .object(WkHtmlToPdfObject.forHtml("<p>This is the second object...</p>"))
    // ...
    .convert("/path/to/file.pdf");
```

### Converting to InputStream (instead of saving as file)

Converting to an InputStream would be useful if you intend on returning the resulting PDF document
as an HTTP response or adding it as an email attachment

```java
HtmlToPdfConverter htmlToPdf = HtmlToPdfConverter.create()
    // ...
    .object(WkHtmlToPdfObject.forUrl("https://github.com/wooio/htmltopdf-java"));

try (InputStream in = htmlToPdf.convert()) {
    // "in" has PDF bytes loaded
} catch (HtmlToPdfException e) {
    // HtmlToPdfException is a RuntimeException, thus you are not required to
    // catch it in this scope. It is thrown when the conversion fails
    // for any reason.
}
```

### Saving HTML as an Image file with transparent background

```java
boolean success = HtmlToImageConverter.transparent(true)
    .fromHtml("<p><em>Apples</em>, not oranges</p>")
    .saveAsImage("/path/to/file.png");
```

## Concurrency limitations

While the library is thread-safe, it unfortunately cannot perform conversions concurrently. Because wkhtmltopdf and wkhtmltoimage use Qt behind the scenes to render webpages, there is a single thread which performs such rendering across a single process. Therefore, at this point, it is only possible to perform one conversion at the same time per process.

## Troubleshooting

### Missing native dependencies

If you get the following exception:
```
java.lang.UnsatisfiedLinkError: Unable to load library '/tmp/org.htmltopdf/wkhtmltox/0.12.5/libwkhtmltox.so': Native library (tmp/io.woo.htmltopdf/wkhtmltox/0.12.5/libwkhtmltox.so) not found in resource path
```
Then that likely means that one of the native dependencies of wkhtmltopdf or wkhtmltoimage is not met. It might be worth checking that the following packages are installed:

- libc6 (or glibc)
- libx11
- libxext
- libxrender
- libstdc++
- libssl1.0
- freetype
- fontconfig