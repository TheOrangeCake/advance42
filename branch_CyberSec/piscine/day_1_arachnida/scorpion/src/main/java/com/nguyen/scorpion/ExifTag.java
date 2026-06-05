package com.nguyen.scorpion;

import lombok.Getter;

@Getter
public enum ExifTag {
    IMAGE_WIDTH(0x0100, "ImageWidth", Category.IMAGE),
    IMAGE_HEIGHT(0x0101, "ImageHeight", Category.IMAGE),
    BITS_PER_SAMPLE(0x0102, "BitsPerSample", Category.IMAGE),
    COMPRESSION(0x0103, "Compression", Category.IMAGE),
    MAKE(0x010F, "Make", Category.IMAGE),
    MODEL(0x0110, "Model", Category.IMAGE),
    ORIENTATION(0x0112, "Orientation", Category.IMAGE),
    X_RESOLUTION(0x011A, "XResolution", Category.IMAGE),
    Y_RESOLUTION(0x011B, "YResolution", Category.IMAGE),
    RESOLUTION_UNIT(0x0128, "ResolutionUnit", Category.IMAGE),
    SOFTWARE(0x0131, "Software", Category.IMAGE),
    DATE_TIME(0x0132, "DateTime", Category.IMAGE),
    ARTIST(0x013B, "Artist", Category.IMAGE),
    COPYRIGHT(0x8298, "Copyright", Category.IMAGE),

    EXPOSURE_TIME(0x829A, "ExposureTime", Category.EXPOSURE),
    F_NUMBER(0x829D, "FNumber", Category.EXPOSURE),
    EXPOSURE_PROGRAM(0x8822, "ExposureProgram", Category.EXPOSURE),
    ISO_SPEED_RATINGS(0x8827, "ISOSpeedRatings", Category.EXPOSURE),
    SHUTTER_SPEED_VALUE(0x9201, "ShutterSpeedValue", Category.EXPOSURE),
    APERTURE_VALUE(0x9202, "ApertureValue", Category.EXPOSURE),
    BRIGHTNESS_VALUE(0x9203, "BrightnessValue", Category.EXPOSURE),
    EXPOSURE_BIAS_VALUE(0x9204, "ExposureBiasValue", Category.EXPOSURE),
    MAX_APERTURE_VALUE(0x9205, "MaxApertureValue", Category.EXPOSURE),
    SUBJECT_DISTANCE(0x9206, "SubjectDistance", Category.EXPOSURE),
    METERING_MODE(0x9207, "MeteringMode", Category.EXPOSURE),
    FLASH(0x9209, "Flash", Category.EXPOSURE),
    FOCAL_LENGTH(0x920A, "FocalLength", Category.EXPOSURE),
    EXPOSURE_MODE(0xA402, "ExposureMode", Category.EXPOSURE),
    WHITE_BALANCE(0xA403, "WhiteBalance", Category.EXPOSURE),
    DIGITAL_ZOOM_RATIO(0xA404, "DigitalZoomRatio", Category.EXPOSURE),
    FOCAL_LENGTH_IN_35MM(0xA405, "FocalLengthIn35mmFilm", Category.EXPOSURE),
    SCENE_CAPTURE_TYPE(0xA406, "SceneCaptureType", Category.EXPOSURE),
    GAIN_CONTROL(0xA407, "GainControl", Category.EXPOSURE),
    CONTRAST(0xA408, "Contrast", Category.EXPOSURE),
    SATURATION(0xA409, "Saturation", Category.EXPOSURE),
    SHARPNESS(0xA40A, "Sharpness", Category.EXPOSURE),

    DATE_TIME_ORIGINAL(0x9003, "DateTimeOriginal", Category.DATETIME),
    DATE_TIME_DIGITIZED(0x9004, "DateTimeDigitized", Category.DATETIME),

    COLOR_SPACE(0xA001, "ColorSpace", Category.CHARACTERISTICS),
    PIXEL_X_DIMENSION(0xA002, "PixelXDimension", Category.CHARACTERISTICS),
    PIXEL_Y_DIMENSION(0xA003, "PixelYDimension", Category.CHARACTERISTICS),
    SENSING_METHOD(0xA217, "SensingMethod", Category.CHARACTERISTICS),
    CUSTOM_RENDERED(0xA401, "CustomRendered", Category.CHARACTERISTICS),
    SUBJECT_DISTANCE_RANGE(0xA40C, "SubjectDistanceRange", Category.CHARACTERISTICS),
    IMAGE_UNIQUE_ID(0xA420, "ImageUniqueID", Category.CHARACTERISTICS),

    LENS_MAKE(0xA433, "LensMake", Category.LENS),
    LENS_MODEL(0xA434, "LensModel", Category.LENS),

    GPS_LATITUDE_REF(0x0001, "GPSLatitudeRef", Category.GPS),
    GPS_LATITUDE(0x0002, "GPSLatitude", Category.GPS),
    GPS_LONGITUDE_REF(0x0003, "GPSLongitudeRef", Category.GPS),
    GPS_LONGITUDE(0x0004, "GPSLongitude", Category.GPS),
    GPS_ALTITUDE_REF(0x0005, "GPSAltitudeRef", Category.GPS),
    GPS_ALTITUDE(0x0006, "GPSAltitude", Category.GPS),
    GPS_TIMESTAMP(0x0007, "GPSTimeStamp", Category.GPS),
    GPS_SPEED_REF(0x000C, "GPSSpeedRef", Category.GPS),
    GPS_SPEED(0x000D, "GPSSpeed", Category.GPS),
    GPS_IMG_DIRECTION_REF(0x0010, "GPSImgDirectionRef", Category.GPS),
    GPS_IMG_DIRECTION(0x0011, "GPSImgDirection", Category.GPS),
    GPS_DATESTAMP(0x001D, "GPSDateStamp", Category.GPS);

    final int id;
    final String name;
    final Category category;

    ExifTag(int id, String name, Category category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }

    public static ExifTag fromId(int id) {
        for (ExifTag tag : values()) {
            if (tag.id == id) return tag;
        }
        return null;
    }

    @Getter
    public enum Category {
        IMAGE("Image"),
        EXPOSURE("Exposure"),
        DATETIME("Date/Time"),
        CHARACTERISTICS("Image Characteristics"),
        LENS("Lens"),
        GPS("GPS");

        final String label;

        Category(String label) {
            this.label = label;
        }
    }
}
