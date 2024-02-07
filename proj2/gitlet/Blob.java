package gitlet;

import static gitlet.Utils.*;
import static gitlet.MyUtils.*;

import java.io.File;

public class Blob implements Dumpable {
    /** The contents of this Blob */
    private final byte[] contents;
    /** The source file */
    private final File source;
    /** The object file */
    private final File file;
    /** The filename */
    private String fileName;
    /** The sha1 ID */
    private String id;

    /**
     * The Blob constructor from source file
     * 
     * @param source source file
     */
    public Blob(File source) {
        this.source = source;
        this.fileName = this.source.getPath();
        this.contents = readContents(source);
        this.id = sha1(fileName, contents);
        this.file = newObjectFile(id);
    }

    /**
     * Load the Blob instance from a Object File
     * 
     * @param id ID of the Object File
     * @return Blob instance
     */
    public static Blob fromFile(String id) {
        File file = newObjectFile(id);
        if (!file.exists()) {
            exit("No blob with that id exists.");
        }
        return readObject(file, Blob.class);
    }

    /**
     * Save the Blob instance to a Object File
     */
    public void saveBlob() {
        saveObjectFile(file, this);
    }

    /**
     * Get the source file name of this Blob
     * 
     * @return source file name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Get the contents of this Blob
     * 
     * @return Blob ID
     */
    public String getId() {
        return id;
    }

    /**
     * Get the contents of this Blob
     * 
     * @return Blob contents in Bytes
     */
    public byte[] getContents() {
        return contents;
    }

    /**
     * Get the Object File of this Blob
     * 
     * @return the Object File
     */
    public File getFile() {
        return file;
    }

    /**
     * Get the contents of this Blob
     * 
     * @return Blob contents in String
     */
    public String getContentsAsString() {
        return new String(contents);
    }

    /**
     * String representation of this Blob
     * Format:
     * Blob [ID] [Filename]
     * Contents
     */
    public String toString() {
        return String.format("Blob [%s] [%s]\n%s\n", id, fileName, getContentsAsString());
    }

    @Override
    /**
     * Dump the contents of this Blob
     */
    public void dump() {
        System.out.println(this);
    }
}