package org.wkhtmltopdf;

public class WkHtmlToXException extends RuntimeException {
    public WkHtmlToXException() {
    }

    public WkHtmlToXException(String message) {
        super(message);
    }

    public WkHtmlToXException(String message, Throwable cause) {
        super(message, cause);
    }

    public WkHtmlToXException(Throwable cause) {
        super(cause);
    }

    public WkHtmlToXException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
