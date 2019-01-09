package util;

import java.io.File;

public class PathUtil {
    private static final String RESOURCES_DIR = System.getProperty("user.dir") + "\\src\\main\\resources";

    static String getPageImagesDir() {
        return RESOURCES_DIR + "\\page-images";
    }

    public static String getPageContentsDir() {
        return RESOURCES_DIR + "\\page-contents";
    }

    public static String getContentsFileForPage(int pageNo) {
        return String.format("%s%s%d.page", getPageContentsDir(), File.separator, pageNo);
    }

    public static String getImageFileForPage(int pageNo) {
        return String.format("%s%s%d.jpg", getPageImagesDir(), File.separator, pageNo);
    }

}
