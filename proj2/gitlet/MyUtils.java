package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.join;
import static gitlet.Utils.writeObject;

public class MyUtils {
    public static File newObjectFile(String id) {
        String dir = objectDir(id);
        String fileName = objectFileName(id);
        return join(Repository.OBJECTS_DIR, dir, fileName);
    }

    private static String objectDir(String id) {
        return id.substring(0, 2);
    }

    private static String objectFileName(String id) {
        return id.substring(2);
    }

    public static void saveObjectFile(File file, Serializable obj) {
        // if the parent directory does not exist, create it
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        writeObject(file, obj);
    }

    public static void exit(String msg, Object... args) {
        Utils.error(msg, args);
        System.exit(0);
    }

}
