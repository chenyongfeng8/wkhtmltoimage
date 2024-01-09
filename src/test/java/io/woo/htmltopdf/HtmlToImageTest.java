package io.woo.htmltopdf;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.wkhtmltopdf.wkhtmltoimage.HtmlToImageConverter;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class HtmlToImageTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void itReleasesHandleToImageFileWhenConversionIsDone() throws IOException {
        String html = "\"<html><head><title>Test page</title></head><body><p>This is just a simple test.</p></html>\"";
        File file = tempFolder.newFile(System.currentTimeMillis() + ".png");
        HtmlToImageConverter.fromHtml(html).transparent(true).saveAsImage(file.getPath());

        assertTrue(file.renameTo(file));
    }

    @Test
    public void itConvertsMarkupFromUrlToImage() throws IOException {
        File file = tempFolder.newFile(System.currentTimeMillis() + ".png");

        boolean success = HtmlToImageConverter.fromHtml(null)
                .in("https://www.google.com/")
                .saveAsImage("/path/to/file.png");
        assertTrue(file.renameTo(file));
    }

}