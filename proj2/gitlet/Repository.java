package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static gitlet.MyUtils.*;
import static gitlet.Utils.*;

/**
 * Represents a gitlet repository.
 * It's a good idea to give a description here of what else this Class
 * does at a high level.
 *
 * @author Keyu Liu
 */
public class Repository {
    /**
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /*
     * .gitlet
     * |--objects
     * | |--blob_folder (id[0:2])
     * | | |--blobs (id[2:])
     * | |--commit
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

    public static void init() throws IOException {
        if (isInitialized()) {
            MyUtils.exit("A Gitlet version-control system already "
                    + "exists in the current directory.");
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

        Commit firstCommit = new Commit();
        firstCommit.saveCommit();

        Branch master = new Branch("master", firstCommit.getId());
        master.saveBranch();

        Head head = new Head("master");
        head.saveHead();

        Stage stage = new Stage();
        stage.saveStage();
    }

    /**
     * Check if the gitlet repository is initialized.
     * 
     * @return true if the current directory is in a gitlet repository
     */
    public static boolean isInitialized() {
        return GITLET_DIR.exists();
    }

    /**
     * Add a file to the stage.
     * 
     * @param file the file to be added.
     */
    public static void add(String fileName) throws IOException {
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
        if (!stage.removeFile(file)) {
            MyUtils.exit("No reason to remove the file.");
        }

        stage.saveStage();
    }

    /**
     * Commit the stage changes.
     * 
     * @param message the commit message.
     */
    public static void commit(String message) throws IOException {
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
        LinkedList<String> parentIds = new LinkedList<String>();
        parentIds.add(prevCommitId);
        Commit commit = new Commit(message, parentIds, trackedFiles);
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

        CommitIterator commitIterator = new CommitIterator(curCommit);
        for (Commit commit : commitIterator) {
            System.out.println("===");
            System.out.println(commit);
            if (commit.isInitCommit()) {
                break;
            }
        }
    }

    /**
     * Global log the commit history.
     */
    public static void globalLog() {
        Set<Commit> commits = MyUtils.commits();
        for (Commit commit : commits) {
            System.out.println("===");
            System.out.println(commit);
        }
    }

    /**
     * Find the commit with the given message.
     * 
     * @param message the commit message.
     */
    public static void find(String message) {
        boolean findCommit = false;

        Set<Commit> commits = MyUtils.commits();
        for (Commit commit : commits) {
            if (commit.getMessage().equals(message)) {
                System.out.println(commit.getId());
                findCommit = true;
            }
        }

        if (!findCommit) {
            MyUtils.exit("Found no commit with that message.");
        }
    }

    public static void status() {
        System.out.println("=== Branches ===");
        Branch curBranch = currentBranch();
        Set<Branch> branches = MyUtils.branches();
        System.out.printf("*%s\n", curBranch.getBranchName());
        for (Branch branch : branches) {
            if (branch.equals(curBranch)) {
                continue;
            }
            System.out.println(branch.getBranchName());
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        Stage stage = Stage.fromFile();
        Set<String> addedFileNames = stage.getAddedFiles().keySet();
        for (String fileName : addedFileNames) {
            System.out.println(fileName);
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        Set<String> removedFileNames = stage.getRemovedFileNames();
        for (String fileName : removedFileNames) {
            System.out.println(fileName);
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        Map<String, String> trackedFiles = currentCommit().getTrackedFiles();
        Map<String, String> workingFiles = MyUtils.workingFiles();
        Map<String, String> addedFiles = stage.getAddedFiles();
        Set<String> removedFiles = stage.getRemovedFileNames();
        // changes not staged from current Commit --> working directory
        for (String fileName : trackedFiles.keySet()) {
            String cId = trackedFiles.get(fileName);
            String wId = workingFiles.get(fileName);

            if (wId == null) {
                if (!removedFiles.contains(fileName)) {
                    System.out.println(fileName + " (deleted)");
                }
            } else if (!cId.equals(wId)) {
                String aId = addedFiles.get(fileName);
                if (aId == null || !aId.equals(wId)) {
                    System.out.println(fileName + " (modified)");
                }
            }
        }
        // changes not staged from stage --> working directory (addedFiles)
        for (String fileName : differenceSet(addedFiles.keySet(), trackedFiles.keySet())) {
            String aId = addedFiles.get(fileName);
            String wId = workingFiles.get(fileName);
            if (wId == null) {
                System.out.println(fileName + " (deleted)");
            } else if (!aId.equals(wId)) {
                System.out.println(fileName + " (modified)");
            }
        }
        // changes not staged from working directory --> stage (removedFiles)
        for (String fileName : differenceSet(removedFileNames, trackedFiles.keySet())) {
            String wId = workingFiles.get(fileName);
            if (wId != null) {
                System.out.println(fileName + " (deleted)");
            }
        }
        System.out.println();

        System.out.println("=== Untracked Files ===");
        Set<String> untrackedFileNames = MyUtils.untrackedFileNames();
        for (String fileName : untrackedFileNames) {
            System.out.println(fileName);
        }
        System.out.println();
    }

    /**
     * Checkout the file with the given name.
     * 
     * @param fileName the file name.
     */
    public static void checkoutFile(String fileName) {
        Commit commit = currentCommit();
        File file = new File(fileName);
        checkoutFile(commit, file);
    }

    /**
     * Checkout the file with the given commit id and file name.
     * 
     * @param commitId the commit id.
     * @param fileName the file name.
     */
    public static void checkoutFile(String commitId, String fileName) {
        Commit commit = Commit.fromFile(commitId);
        File file = new File(fileName);
        checkoutFile(commit, file);
    }

    /**
     * Checkout the branch with the given branch name.
     * 
     * @param branchName the branch name.
     */
    public static void checkoutBranch(String branchName) {
        if (MyUtils.isCurrentBranch(branchName)) {
            MyUtils.exit("No need to checkout the current branch.");
        }
        Branch branch = Branch.fromFile(branchName);
        Commit commit = branch.dereference();
        checkoutCommit(commit);

        // update the head
        Head head = Head.fromFile();
        head.referTo(branchName);
        head.saveHead();
    }

    /**
     * Reset the commit with the given commit id.
     * 
     * @param commitId the commit id.
     */
    public static void reset(String commitId) throws IOException {
        Commit reseCommit = Commit.fromFile(commitId);
        checkoutCommit(reseCommit);

        // update Branch
        Branch branch = currentBranch();
        branch.referTo(commitId);
        branch.saveBranch();
    }

    /**
     * Checkout the branch with the given branch which has no effect to Head and
     * Branch.
     * This is a general private method to checkout a branch, used to support the
     * public checkout [branch name] and reset [commit id] method.
     * 
     * @param branch the branch
     */
    private static void checkoutCommit(Commit otherCommit) {
        Commit curCommit = currentCommit();
        Set<String> curTrackedFileNames = curCommit.getTrackedFileNames();
        Set<String> otherTrackedFileNames = otherCommit.getTrackedFileNames();

        Set<String> unTrackedFileNames = MyUtils.untrackedFileNames();

        // check if the checkout will overwrite the files
        if (MyUtils.intersectionSet(otherTrackedFileNames, unTrackedFileNames).size() > 0) {
            MyUtils.exit("There is an untracked file in the way; delete it, "
                    + "or add and commit it first.");
        }

        // checkout all the files in the given branch
        for (String fileName : otherTrackedFileNames) {
            File file = new File(fileName);
            checkoutFile(otherCommit, file);
        }

        // remove all the trakced files in current commit that are not present in the
        // given branch
        Set<String> unoverwritedTrackedFileNames = differenceSet(
                curTrackedFileNames, otherTrackedFileNames);
        for (String fileName : unoverwritedTrackedFileNames) {
            File file = new File(fileName);
            file.delete();
        }

        // clear stage
        Stage stage = Stage.fromFile();
        stage.clearForCheckoutCommit(otherCommit);
        stage.saveStage();
    }

    /**
     * Checkout the file with the given commit id and file name.
     * This is a general private method to checkout a file, used to support the
     * checkout methods.
     * 
     * @param commit the commit.
     * @param file   the file.
     */
    private static void checkoutFile(Commit commit, File file) {
        String fileName = file.getName();

        Map<String, String> trakcedFiles = commit.getTrackedFiles();
        if (!trakcedFiles.containsKey(fileName)) {
            MyUtils.exit("File does not exist in that commit.");
        }

        String blobId = trakcedFiles.get(fileName);
        Blob blob = Blob.fromFile(blobId);
        byte[] contents = blob.getContents();

        writeContents(file, contents);
    }

    /**
     * Create a new branch with the given name.
     * 
     * @param branchName the branch name.
     */
    public static void branch(String branchName) throws IOException {
        if (MyUtils.branchExists(branchName)) {
            MyUtils.exit("A branch with that name already exists.");
        }

        Commit curCommit = currentCommit();
        Branch newBranch = new Branch(branchName, curCommit.getId());
        newBranch.saveBranch();
    }

    /**
     * Remove the branch with the given name.
     * 
     * @param branchName the branch name.
     */
    public static void rmBranch(String branchName) {
        if (!MyUtils.branchExists(branchName)) {
            MyUtils.exit("A branch with that name does not exist.");
        }

        if (MyUtils.isCurrentBranch(branchName)) {
            MyUtils.exit("Cannot remove the current branch.");
        }

        Branch branch = Branch.fromFile(branchName);
        branch.deleteBranch();
    }

    /**
     * Merge the branch with the given branch name.
     * 
     * @param branchName the branch name.
     */
    public static void merge(String branchName) throws IOException {
        checkMerge(branchName);

        Branch otheBranch = Branch.fromFile(branchName);
        MergeHandler.merge(otheBranch);
    }

    /**
     * Check if the merge is valid.
     * 
     * @return true if the merge is valid, false otherwise.
     */
    private static boolean checkMerge(String branchName) {
        Stage stage = Stage.fromFile();
        if (!stage.isEmpty()) {
            MyUtils.exit("You have uncommitted changes.");
        }

        if (!MyUtils.branchExists(branchName)) {
            MyUtils.exit("A branch with that name does not exist.");
        }
        if (MyUtils.isCurrentBranch(branchName)) {
            MyUtils.exit("Cannot merge a branch with itself.");
        }

        return true;
    }

    /**
     * Represent a Iterator of the Commit.
     * Iterator from a given Commit to the init Commit,
     * if there are multi-parentIds, choose the first parent to move forward.
     */
    private static class CommitIterator implements Iterator<Commit>, Iterable<Commit> {

        private Commit curCommit;

        CommitIterator(Commit commit) {
            this.curCommit = commit;
        }

        public boolean hasNext() {
            return curCommit != null;
        }

        public Commit next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Commit commit = curCommit;

            if (commit.isInitCommit()) {
                curCommit = null;
            } else {
                String parentId = curCommit.getParentIds().get(0);
                curCommit = Commit.fromFile(parentId);
            }

            return commit;
        }

        @Override
        public Iterator<Commit> iterator() {
            return this;
        }
    }
}
