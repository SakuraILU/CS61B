package gitlet;

import static gitlet.MyUtils.newObjectFile;
import static gitlet.MyUtils.saveObjectFile;
import static gitlet.Utils.readObject;
import static gitlet.Utils.sha1;

// TODO: any imports you need here

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Represents a gitlet commit object.
 * TODO: It's a good idea to give a description here of what else this Class
 * does at a high level.
 *
 * @author TODO
 */
public class Commit implements Dumpable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The date */
    private final Date date;
    /** The message */
    private final String message;
    /** The parents */
    private final List<String> parents;
    /** The map[path:id] of tracked files */
    private final Map<String, String> trackedFiles;
    /** The ID */
    private final String id;
    /** The object file */
    private final File file;

    /* TODO: fill in the rest of this class. */

    /**
     * defualt construct, used to create the first commit.
     */
    public Commit() {
        this.date = new Date(0);
        this.message = "initial commit";
        this.trackedFiles = new HashMap<String, String>();
        this.parents = new LinkedList<String>();
        this.id = sha1(this.message);
        this.file = newObjectFile(id);
    }

    /**
     * constructor with message, parents and trackedFiles
     * 
     * @param message
     * @param parents
     * @param trackedFiles
     */
    public Commit(String message, List<String> parents, Map<String, String> trackedFiles) {
        this.date = new Date();
        this.message = message;
        this.parents = parents;
        this.trackedFiles = trackedFiles;
        this.id = sha1(getTimestamp(), message, parents.toString(), trackedFiles.toString());
        this.file = newObjectFile(this.id);
    }

    /**
     * Load the Commit instance from a object file.
     * 
     * @param id ID of the Object File
     * @return
     */
    public static Commit fromFile(String id) {
        File file = newObjectFile(id);
        return readObject(file, Commit.class);
    }

    /**
     * Save the Commit instance to a object file.
     */
    public void saveCommit() {
        saveObjectFile(file, this);
    }

    /**
     * Is init Commit which has no parent
     * 
     * @return true if it is init Commit
     */
    public boolean isInitCommit() {
        return parents.isEmpty();
    }

    /**
     * Get the message of this Commit
     * 
     * @return Commit message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get the ID of this Commit
     * 
     * @return Commit ID
     */
    public String getId() {
        return id;
    }

    /**
     * Get the parents of this Commit
     * 
     * @return Commit parents
     */
    public List<String> getParents() {
        return parents;
    }

    /**
     * Get the tracked files of this Commit
     * 
     * @return Commit tracked files
     */
    public Map<String, String> getTrackedFiles() {
        return trackedFiles;
    }

    /**
     * Get the names of tracked files of this Commit
     * 
     * @return
     */
    public Set<String> getTrackedFileNames() {
        return trackedFiles.keySet();
    }

    /**
     * String representation of this Commit
     * if the Commit has only one parent, format follows:
     * commit a0da1ea5a15ab613bf9961fd86f010cf74c7ee48
     * Date: Thu Nov 9 20:00:05 2017 -0800
     * A commit message.
     * 
     * 
     * if the Commit has two parents (a merged Commit), format follows:
     * commit 3e8bf1d794ca2e9ef8a4007275acf3751c7170ff
     * Merge: 4975af1 2c1ead1
     * Date: Sat Nov 11 12:30:00 2017 -0800
     * Merged development into master.
     * 
     * where the two hexadecimal numerals following “Merge:” consist of the first
     * seven digits of the first and second parents’ commit ids, in that order. The
     * first parent is the branch you were on when you did the merge; the second is
     * that of the merged-in branch. This is as in regular Git.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // commit <id>
        sb.append("commit ").append(id).append("\n");

        // Merge: <parent1> <parent2>
        if (parents.size() == 2) {
            String parentId0 = parents.get(0).substring(0, 7);
            String parentId1 = parents.get(1).substring(0, 7);
            sb.append("Merge: ").append(parentId0).append(" ").append(parentId1).append("\n");
        } else if (parents.size() > 2) {
            MyUtils.exit("Invalid commit: more than 2 parents.");
        }

        // Date: <date>
        sb.append("Date: ").append(getTimestamp()).append("\n");
        // <message>
        sb.append(message).append("\n");

        return sb.toString();
    }

    @Override
    /**
     * Dump the Commit instance
     */
    public void dump() {
        System.out.println(this);
    }

    private String getTimestamp() {
        // Thu Jan 1 00:00:00 1970 +0000
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.ENGLISH);
        return dateFormat.format(date);
    }
}
