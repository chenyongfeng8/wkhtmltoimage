package org.wkhtmltopdf;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

public interface WkHtmlToX extends Library {

    /** ================================================= wkhtmltopdf begin ======================================================== **/
    interface wkhtmltopdf_str_callback extends Callback {
        /**
         * \typedef wkhtmltopdf_str_callback
         * \brief Function pointer type used for the error, warning, info and debug callbacks
         *
         * \param converter The converter that issued the callback
         * \param str A utf8 encoded string containing the error, warning, info or debug message.
         *
         * \sa wkhtmltopdf_set_error_callback, wkhtmltopdf_set_warning_callback, wkhtmltopdf_set_info_callback, wkhtmltopdf_set_debug_callback
         */
        void callback(Pointer converter, String str);
    }

    interface wkhtmltopdf_void_callback extends Callback {
        /**
         * \typedef wkhtmltopdf_void_callback
         * \brief Function pointer type used for the phase_changed callback
         *
         * \param converter The converter that issued the callback
         *
         * \sa wkhtmltopdf_set_phase_changed_callback
         */
        void callback(Pointer converter);
    }

    interface wkhtmltopdf_int_callback extends Callback {
        /**
         * \typedef wkhtmltopdf_int_callback
         * \brief Function pointer type used for the progress_changed and finished callbacks
         *
         * For the progress_changed callback the value indicated the progress
         * within the current phase in percent. For the finished callback the value
         * if 1 if the conversion has successful and 0 otherwise.
         *
         * \param converter The converter that issued the callback
         * \param val The integer value
         *
         * \sa wkhtmltopdf_set_progress_changed, wkhtmltopdf_set_finished_callback
         */
        void callback(Pointer converter, int i);
    }

    /**
     * \brief Setup wkhtmltopdf
     * <p>
     * Must be called before any other functions.
     * <p>
     * \param use_graphics Should we use a graphics system
     * \returns 1 on success and 0 otherwise
     * <p>
     * \sa wkhtmltopdf_deinit
     */
    int wkhtmltopdf_init(int useGraphics);
    /**
     * \brief Deinit wkhtmltopdf
     * <p>
     * Free up resources used by wkhtmltopdf, when this has been called no other
     * wkhtmltopdf function can be called.
     * <p>
     * \returns 1 on success and 0 otherwise
     * <p>
     * \sa wkhtmltopdf_init
     */
    int wkhtmltopdf_deinit();
    /**
     * \brief Check if the library is build against the wkhtmltopdf version of QT
     *
     * \return 1 if the library was build against the wkhtmltopdf version of QT and 0 otherwise
     */
    int wkhtmltopdf_extended_qt();
    /**
     * \brief Return the version of wkhtmltopdf
     * Example: 0.12.1-development. The string is utf8 encoded and is owned by wkhtmltopdf.
     *
     * \return Qt version
     */
    String wkhtmltopdf_version();

    /**
     * \brief Create a new global settings object for pdf conversion
     *
     * Create a new global settings object for pdf conversion, settings can be altered with
     * \ref wkhtmltopdf_set_global_setting, and inspected with \ref wkhtmltopdf_get_global_setting.
     * Once the desired settings have been set a converter object can be created using \reg wkhtmltopdf_create_converter.
     *
     * \returns A wkhtmltopdf global settings object
     */
    Pointer wkhtmltopdf_create_global_settings();
    /**
     * \brief Alter a setting in a global settings object
     *
     * \sa \ref pagePdfGlobal, wkhtmltopdf_create_global_settings, wkhtmltopdf_get_global_setting
     *
     * \param settings The settings object to change
     * \param name The name of the setting
     * \param value The new value for the setting (encoded in UTF-8)
     * \returns 1 if the setting was updated successfully and 0 otherwise.
     */
    int wkhtmltopdf_set_global_setting(Pointer globalSettings, String name, String value);
    /**
     * \brief Retrieve a setting in a global settings object
     *
     * \sa \ref pagesettings, wkhtmltopdf_create_global_settings, wkhtmltopdf_set_global_setting
     *
     * \param settings The settings object to inspect
     * \param name The name of the setting to read
     * \param value A buffer of length at least \a vs, where the value (encoded in UTF-8) is stored.
     * \param vs The length of \a value
     * \returns 1 If the the setting exists and was read successfully and 0 otherwise
     */
    int wkhtmltopdf_get_global_setting(Pointer globalSettings, String name, Memory memory, int memorySize);
    /**
     * \brief Destroy a global settings  object
     *
     * Normally one would not need to call this since ownership of the
     * settings object is transfarred to the converter.
     */
    void wkhtmltopdf_destroy_global_settings(Pointer pointer);
    /**
     * \brief Create an object used to store object settings
     *
     * Create a new Object settings object for pdf conversion, settings can be altered with
     * \ref wkhtmltopdf_set_object_setting, and inspected with \ref wkhtmltopdf_get_object_setting.
     * Once the desired settings have been set the object can be added to a converter
     * by calling wkhtmltopdf_add_resource.
     *
     * \returns an object settings instance
     */
    Pointer wkhtmltopdf_create_object_settings();
    /**
     * \brief Alter a setting in a object settings object
     *
     * \sa \ref pagesettings, wkhtmltopdf_create_object_settings, wkhtmltopdf_get_object_setting
     *
     * \param settings The settings object to change
     * \param name The name of the setting
     * \param value The new value for the setting (encoded in UTF-8)
     * \returns 1 if the setting was updated successfully and 0 otherwise.
     */
    int wkhtmltopdf_set_object_setting(Pointer objectSettings, String name, String value);
    /**
     * \brief Retrieve a setting in a object settings object
     *
     * \sa \ref pagesettings, wkhtmltopdf_create_global_settings, wkhtmltopdf_set_global_setting
     *
     * \param settings The settings object to inspect
     * \param name The name of the setting to read
     * \param value A buffer of length at least \a vs, where the value is stored (encoded in UTF-8).
     * \param vs The length of \a value
     * \returns 1 If the the setting exists and was read successfully and 0 otherwise
     */
    int wkhtmltopdf_get_object_setting(Pointer objectSettings, String name, Memory memory, int memorySize);
    /**
     * \brief Destroy a global settings  object
     *
     * Normally one would not need to call this since ownership of the
     * settings object is transfarred to the converter.
     */
    void wkhtmltopdf_destroy_object_settings(Pointer pointer);
    /**
     * \brief Create a wkhtmltopdf converter object
     *
     * The converter object is used to convert one or more objects(web sides) into a single pdf.
     * Once a settings object has been parsed, it may no longer be accessed, and will eventually be freed.
     * The returned converter object must be freed by calling \ref wkhtmltopdf_destroy_converter
     *
     * \param settings The global settings to use during conversion.
     * \returns A wkhtmltopdf converter object
     */
    Pointer wkhtmltopdf_create_converter(Pointer globalSettings);
    /**
     * \brief Set the function that should be called when an warning message is issued during conversion
     *
     * \param converter The converter object on which warnings we want the callback to be called
     * \param cb The function to call when warning message is issued
     *
     */
    void wkhtmltopdf_set_warning_callback(Pointer converter, wkhtmltopdf_str_callback cb);
    /**
     * \brief Set the function that should be called when an errors occurs during conversion
     *
     * \param converter The converter object on which errors we want the callback to be called
     * \param cb The function to call when an error occurs
     */
    void wkhtmltopdf_set_error_callback(Pointer converter, wkhtmltopdf_str_callback cb);
    /**
     * \brief Set the function that should be called whenever conversion changes phase
     *
     * The number of the new phase can be found by calling \ref wkhtmltopdf_current_phase
     *
     * \param converter The converter which phase change events to call back from
     * \param cb The function to call when phases change
     *
     * \sa wkhtmltopdf_current_phase, wkhtmltopdf_phase_count, wkhtmltopdf_phase_description
     */
    void wkhtmltopdf_set_phase_changed_callback(Pointer converter, wkhtmltopdf_void_callback cb);
    /**
     * \brief Set the function that should be called when progress have been done during conversion.
     *
     * The progress in percent within the current phase is given as an integer to the callback function.
     *
     * \param converter The converter which progress events to call back from
     * \param cb The function to call when progress has occurred.
     *
     * \sa wkhtmltopdf_progress_description
     */
    void wkhtmltopdf_set_progress_changed_callback(Pointer converter, wkhtmltopdf_int_callback cb);
    /**
     * \brief Set the function that should be called once the conversion has finished.
     *

     * \param converter The converter which finish events to call back from
     * \param cb The function to call when the conversion has finished has occurred.
     *
     * \sa wkhtmltopdf_convert
     */
    void wkhtmltopdf_set_finished_callback(Pointer converter, wkhtmltopdf_int_callback cb);
    /**
     * \brief add an object (web page to convert)
     *
     * Add the object described by the supplied object settings to the list of objects (web pages to convert),
     * objects are placed in the output pdf in the order of addition. Once the object has been added, the
     * supplied settings may no longer be accessed, it Wit eventually be freed by wkhtmltopdf.
     * If a none NULL and none empty utf8 encoded string is supplied to data, this HTML content will be converted
     * instead of the content located at  "page" setting of the supplied object settings instance.
     *
     * \param converter The converter to add the object to
     * \param settings The setting describing the object to add
     * \param data HTML content of the object to convert (encoded in UTF-8) or NULL
     */
    void wkhtmltopdf_add_object(Pointer converter, Pointer objectSettings, String data);
    /**
     * \brief Get the number of the current conversion phase
     *
     * Conversion is done in a number of named phases, this
     * function will retrieve the number of the current conversion phase,
     * which will be a number between 0 and wkhtmltopdf_phase_count(converter)-1.
     *
     * The description (name) of any phase can be retrieved by calling the
     * \ref wkhtmltopdf_phase_description method.
     *
     * \param converter The converter to find the current phase of
     * \returns The current phase of the supplied converter
     */
    int wkhtmltopdf_current_phase(Pointer converter);
    /**
     * \brief Get the total number of phases the conversion process will go trough
     *
     * \param converter The converter to query
     * \returns The total number of phases in the conversion process
     *
     * \sa wkhtmltopdf_current_phase, wkhtmltopdf_phase_description
     */
    int wkhtmltopdf_phase_count(Pointer converter);
    /**
     * \brief Return a short utf8 description of a conversion phase
     *
     * \param converter The converter to query
     * \param phase The number of the conversion step of which we want a description
     * \returns A description of the conversion phase
     *
     * \sa wkhtmltopdf_current_phase, wkhtmltopdf_phase_description
     */
    String wkhtmltopdf_phase_description(Pointer converter, int phase);
    /**
     * \brief Return a short utf8 string indicating progress within a phase
     *
     * Will typically return a string like "40%"
     *
     * \param converter The converter to query
     * \returns A string containing a progress indication
     *
     * \sa wkhtmltopdf_set_progress_changed_callback
     */
    String wkhtmltopdf_progress_string(Pointer converter);
    /**
     * \brief Return the largest HTTP error code encountered during conversion
     *
     * Return the largest HTTP code greater than or equal to 300 encountered during loading
     * of any of the supplied objects, if no such error code is found 0 is returned.
     * This function will only return a useful result after \ref wkhtmltopdf_convert has been called.
     *
     * \param converter The converter to query
     * \returns The HTTP error code.
     */
    int wkhtmltopdf_http_error_code(Pointer converter);
    /**
     * \brief Convert the input objects into a pdf document
     *
     * This is the main method for the conversion process, during conversion progress debug, information,
     * warning, and errors are reported using the supplied call backs. Once the conversion is done
     * the output pdf (or ps) file will be placed at the location of the "out" setting supplied in
     * the global settings object during construction of the converter. If this setting is not supplied
     * or set to the empty string, the output can be retrieved using the \ref wkhtmltopdf_get_output
     * function.
     *
     * \paragm converter The converter to perform the conversion on.
     *
     * \returns 1 on success and 0 otherwise
     */
    int wkhtmltopdf_convert(Pointer converter);
    /**
     * \brief Get the output document generated during conversion.
     *
     * If no "out" location was specified in the global settings object, the binary
     * output (pdf document) of the convection process will be stored in a buffer.
     *
     * \param converter The converter to query
     * \param d A pointer to a pointer that will be made to point to the output data
     * \returns The length of the output data
     */
    long wkhtmltopdf_get_output(Pointer converter, PointerByReference out);
    /**
     * \brief Destroy a wkhtmltopdf converter object
     *
     * An object must be destroyed to free up its memory, once it has been destroyed it may no longer
     * be accessed.
     *
     * \param settings The converter object to destroy
     */
    void wkhtmltopdf_destroy_converter(Pointer converter);

    /** ================================================= wkhtmltopdf end ======================================================== **/

    /** ================================================= wkhtmltoimage begin ======================================================== **/
    interface wkhtmltoimage_str_callback extends Callback {
        void callback(Pointer converter, String str);
    }

    interface wkhtmltoimage_void_callback extends Callback {
        void callback(Pointer converter);
    }

    interface wkhtmltoimage_int_callback extends Callback {
        void callback(Pointer converter, int i);
    }

    int wkhtmltoimage_init(int useGraphics);

    int wkhtmltoimage_deinit();

    int wkhtmltoimage_extended_qt();

    String wkhtmltoimage_version();


    Pointer wkhtmltoimage_create_global_settings();

    int wkhtmltoimage_set_global_setting(Pointer globalSettings, String name, String value);

    int wkhtmltoimage_get_global_setting(Pointer globalSettings, String name, Memory memory, int memorySize);

    void wkhtmltoimage_destroy_global_settings(Pointer pointer);


    Pointer wkhtmltoimage_create_converter(Pointer globalSettings, String data);

    void wkhtmltoimage_set_warning_callback(Pointer converter, wkhtmltoimage_str_callback cb);

    void wkhtmltoimage_set_error_callback(Pointer converter, wkhtmltoimage_str_callback cb);

    void wkhtmltoimage_set_phase_changed_callback(Pointer converter, wkhtmltoimage_void_callback cb);

    void wkhtmltoimage_set_progress_changed_callback(Pointer converter, wkhtmltoimage_int_callback cb);

    void wkhtmltoimage_set_finished_callback(Pointer converter, wkhtmltoimage_int_callback cb);

    int wkhtmltoimage_current_phase(Pointer converter);

    int wkhtmltoimage_phase_count(Pointer converter);

    String wkhtmltoimage_phase_description(Pointer converter, int phase);

    String wkhtmltoimage_progress_string(Pointer converter);

    int wkhtmltoimage_http_error_code(Pointer converter);

    int wkhtmltoimage_convert(Pointer converter);

    long wkhtmltoimage_get_output(Pointer converter, PointerByReference out);

    void wkhtmltoimage_destroy_converter(Pointer converter);
    /** ================================================= wkhtmltoimage end ======================================================== **/
}
