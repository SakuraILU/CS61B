package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static gitlet.Utils.*;

// TODO: any imports you need here

/**
 * Represents a gitlet repository.
 * TODO: It's a good idea to give a description here of what else this Class
 * does at a high level.
 *
 * @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /*
     * .gitlet
     * |--objects
     * | |--commit and blob
     * |--refs
     * | |--heads
     * | |--master
     * |--HEAD
     * |--stage
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The objects direcotry */
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    /** The refs direcotry */
    public static final File REFS_DIR = join(GITLET_DIR, "refs");
    /** The HEAD */
    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");
    /** The stage */
    public static final File STAGE_FILE = join(GITLET_DIR, "stage");

    /* TODO: fill in the rest of this class. */
    public static void init() {
        if (GITLET_DIR.exists()) {
            MyUtils.exit("A Gitlet version-control system already exists in the current directory.");
        }

        GITLET_DIR.mkdir();
        OBJECTS_DIR.mkdir();
        REFS_DIR.mkdir();
        try {
            HEAD_FILE.createNewFile();
            STAGE_FILE.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Commit first_commit = new Commit();
        first_commit.saveCommit();

        Branch master = new Branch("master", first_commit.getId());
        master.saveBranch();

        Head head = new Head("master");
        head.saveHead();

        Stage stage = new Stage();
        stage.saveStage();
    }

    /**
     * Add a file to the stage.
     * 
     * @param file the file to be added.
     */
    public static void add(File file) {
        if (!file.exists()) {
            MyUtils.exit("File does not exist.");
        }

        Stage stage = Stage.fromFile();
        stage.addFile(file);
        stage.saveStage();
    }

    /**
     * Remove a file from the stage.
     * 
     * @param file the file to be removed.
     */
    public static void rm(File file) {
        Stage stage = Stage.fromFile();
        stage.removeFile(file);
        stage.saveStage();
    }

    /**
     * Commit the stage changes.
     * 
     * @param message the commit message.
     */
    public static void commit(String message) {
        if (message == null || message.length() == 0) {
            MyUtils.exit("Please enter a commit message.");
        }

        // commit stage changes
        Stage stage = Stage.fromFile();
        if (!stage.commitChanges()) {
            MyUtils.exit("No changes added to the commit.");
        }
        stage.saveStage();
        Map<String, String> trackedFiles = stage.getTrackedFiles();

        // prev Commit ID
        Head head = Head.fromFile();
        Branch branch = head.dereference();
        String prevCommitId = branch.getCommitId();

        // create a new Commit
        LinkedList<String> parents = new LinkedList<String>();
        parents.add(prevCommitId);
        Commit commit = new Commit(message, parents, trackedFiles);
        commit.saveCommit();

        // update the branch
        branch.referTo(commit.getId());
        branch.saveBranch();
    }

    /**
     * Log the commit history.
     */
    public static void log() {
        Head head = Head.fromFile();
        Branch branch = head.dereference();
        Commit curCommit = branch.dereference();

        while (!curCommit.isInitCommit()) {
            System.out.println("===");
            System.out.println(curCommit);

            String parentId = curCommit.getParents().get(0);
            curCommit = Commit.fromFile(parentId);
        }
    }

}
