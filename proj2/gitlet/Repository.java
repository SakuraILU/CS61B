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
    public static final File REFS_DIR = join(GITLET_DIR, "refs", "heads");
    /** The HEAD */
    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");
    /** The stage */
    public static final File STAGE_FILE = join(GITLET_DIR, "stage");

    /* TODO: fill in the rest of this class. */
    public static void init() {
        if (GITLET_DIR.exists()) {
            MyUtils.exit("A Gitlet version-control system already exists in the current directory.");
        }

        GITLET_DIR.mkdirs();
        OBJECTS_DIR.mkdirs();
        REFS_DIR.mkdirs();
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
    public static void add(String fileName) {
        File file = new File(fileName);
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
    public static void rm(String fileName) {
        File file = new File(fileName);
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
        Commit curCommit = currentCommit();
        while (true) {
            System.out.println("===");
            System.out.println(curCommit);

            if (!curCommit.hasParents()) {
                break;
            }
            String parentId = curCommit.getParents().get(0);
            curCommit = Commit.fromFile(parentId);
        }
    }

    /**
     * Global log the commit history.
     */
    public static void globalLog() {
        Set<Commit> commits = findAllCommits();
        for (Commit commit : commits) {
            System.out.println(commit);
        }
    }

    /**
     * Find the commit with the given message.
     * 
     * @param message the commit message.
     */
    public static void find(String message) {
        Set<Commit> commits = findAllCommits();
        for (Commit commit : commits) {
            if (commit.getMessage().equals(message)) {
                System.out.println(commit);
            }
        }
    }

    public static void checkoutFile(String fileName) {
        Commit commit = currentCommit();
        File file = new File(fileName);
        checkoutFile(commit, file);
    }

    public static void checkoutFile(String commitId, String fileName) {
        Commit commit = Commit.fromFile(commitId);
        File file = new File(fileName);
        checkoutFile(commit, file);
    }

    private static void checkoutFile(Commit commit, File file) {
        String fileName = file.getName();

        Map<String, String> trakcedFiles = commit.getTrackedFiles();
        if (!trakcedFiles.containsKey(fileName)) {
            MyUtils.exit("File does not exist.");
        }

        String blobId = trakcedFiles.get(fileName);
        Blob blob = Blob.fromFile(blobId);
        byte contents[] = blob.getContents();

        writeContents(file, contents);
    }

    public static void checkoutBranch(String branchName) {
        if (branchName.equals(currentBranch().getBranchName())) {
            MyUtils.exit("No need to checkout the current branch.");
        }

        Branch branch = Branch.fromFile(branchName);

    }

    private static Branch currentBranch() {
        Head head = Head.fromFile();
        Branch branch = head.dereference();
        return branch;
    }

    private static Commit currentCommit() {
        Branch branch = currentBranch();
        Commit curCommit = branch.dereference();
        return curCommit;
    }

    private static Set<Commit> findAllCommits() {
        List<String> branchNames = plainFilenamesIn(Repository.REFS_DIR);
        Set<Commit> commits = new HashSet<Commit>();
        for (String name : branchNames) {
            Branch branch = Branch.fromFile(name);
            commits.add(branch.dereference());
        }

        Set<Commit> set = new HashSet<Commit>();
        Set<Commit> next_set = new HashSet<Commit>();

        set.addAll(commits);
        while (!set.isEmpty()) {
            for (Commit commit : set) {
                List<String> parentIds = commit.getParents();
                for (String parentId : parentIds) {
                    Commit parentCommit = Commit.fromFile(parentId);
                    if (!commits.contains(parentCommit)) {
                        next_set.add(parentCommit);
                    }
                }
            }

            commits.addAll(next_set);
            set = next_set;
            next_set = new HashSet<Commit>();
        }

        return commits;
    }
}
