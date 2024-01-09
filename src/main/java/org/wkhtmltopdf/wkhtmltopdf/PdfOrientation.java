package org.wkhtmltopdf.wkhtmltopdf;

import org.wkhtmltopdf.WkValue;

public enum PdfOrientation implements WkValue {
    PORTRAIT("Portrait"),
    LANDSCAPE("Landscape");

    private final String wkValue;

    PdfOrientation(String wkValue) {
        this.wkValue = wkValue;
    }

    @Override
    public String getWkValue() {
        return wkValue;
    }
}
