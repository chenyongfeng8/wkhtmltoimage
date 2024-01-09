package org.wkhtmltopdf.wkhtmltopdf;

import org.wkhtmltopdf.WkValue;

public enum PdfColorMode implements WkValue {
    COLOR("Color"),
    GRAYSCALE("Grayscale");

    private final String wkValue;

    PdfColorMode(String wkValue) {
        this.wkValue = wkValue;
    }

    @Override
    public String getWkValue() {
        return wkValue;
    }
}
