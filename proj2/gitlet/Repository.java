package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static gitlet.MyUtils.differenceSet;
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

    /* TODO: fill in the rest of this class. */
    public static void init() {
        if (isInitialized()) {
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

        Set<Commit> commits = findAllCommits();
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
        Branch curBranch = currentBranch();
        Set<Branch> branches = findAllBranches();
        System.out.println("=== Branches ===");
        System.out.printf("*%s\n", curBranch.getBranchName());
        for (Branch branch : branches) {
            if (branch.getBranchName().equals(curBranch.getBranchName())) {
                continue;
            }
            System.out.println(branch.getBranchName());
        }
        System.out.println();

        Stage stage = Stage.fromFile();

        Set<String> addedFileNames = stage.getAddedFiles().keySet();
        System.out.println("=== Staged Files ===");
        for (String fileName : addedFileNames) {
            System.out.println(fileName);
        }
        System.out.println();

        Set<String> removedFileNames = stage.getRemovedFileNames();
        System.out.println("=== Removed Files ===");
        for (String fileName : removedFileNames) {
            System.out.println(fileName);
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();

        System.out.println("=== Untracked Files ===");
        Set<String> untrackedFileNames = findAllUntrackedFileNames();
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
        if (branchName.equals(currentBranch().getBranchName())) {
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
    public static void reset(String commitId) {
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

        Set<String> unTrackedFileNames = findAllUntrackedFileNames();

        // check if the checkout will overwrite the files
        if (MyUtils.intersectionSet(otherTrackedFileNames, unTrackedFileNames).size() > 0) {
            MyUtils.exit("There is an untracked file in the way; delete it, or add and commit it first.");
        }

        // checkout all the files in the given branch
        for (String fileName : otherTrackedFileNames) {
            File file = new File(fileName);
            checkoutFile(otherCommit, file);
        }

        // remove all the trakced files in current commit that are not present in the
        // given branch
        Set<String> unOverwritedTrackedFileNames = differenceSet(curTrackedFileNames, otherTrackedFileNames);
        for (String fileName : unOverwritedTrackedFileNames) {
            File file = new File(fileName);
            file.delete();
        }

        // clear stage
        Stage stage = Stage.fromFile();
        stage.clearStage(otherCommit.getTrackedFiles());
        stage.saveStage();
    }

    /**
     * Checkout the file with the given commit id and file name.
     * This is a general private method to checkout a file, used to support the
     * three public checkout methods.
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
        byte contents[] = blob.getContents();

        writeContents(file, contents);
    }

    /**
     * Create a new branch with the given name.
     * 
     * @param branchName the branch name.
     */
    public static void branch(String branchName) {
        File file = new File(REFS_DIR, branchName);
        if (file.exists()) {
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
        // cannot remove the current branch
        Branch curBranch = currentBranch();
        if (branchName.equals(curBranch.getBranchName())) {
            MyUtils.exit("Cannot remove the current branch.");
        }
        // cannot remove a branch that does not exist
        File file = new File(REFS_DIR, branchName);
        if (!file.exists()) {
            MyUtils.exit("A branch with that name does not exist.");
        }

        Branch branch = Branch.fromFile(branchName);
        branch.deleteBranch();
    }

    /**
     * Get the current branch.
     * 
     * @return the current branch.
     */
    private static Branch currentBranch() {
        Head head = Head.fromFile();
        Branch branch = head.dereference();
        return branch;
    }

    /**
     * Get the current commit.
     * 
     * @return the current commit.
     */
    private static Commit currentCommit() {
        Branch branch = currentBranch();
        Commit curCommit = branch.dereference();
        return curCommit;
    }

    /**
     * Find all branches in the repository
     * 
     * @return all branches in the repository
     */
    public static Set<Branch> findAllBranches() {
        List<String> branchNames = plainFilenamesIn(Repository.REFS_DIR);
        Set<Branch> branches = new HashSet<Branch>();

        for (String branchName : branchNames) {
            Branch branch = Branch.fromFile(branchName);
            branches.add(branch);
        }

        return branches;
    }

    /**
     * Find all commits in the repository.
     *
     * @return all commits in the repository.
     */
    private static Set<Commit> findAllCommits() {
        // Search commit files in the objects directory
        List<String> commitIds = plainFilenamesIn(Repository.OBJECTS_DIR);
        Set<Commit> commits = new HashSet<Commit>();
        for (String commitId : commitIds) {
            Commit commit = Commit.fromFile(commitId);
            commits.add(Commit.fromFile(commit.getId()));
        }

        return commits;
    }

    // /**
    // * Find all commits in the repository.
    // *
    // * @return all commits in the repository.
    // */
    // private static Set<Commit> findAllCommits() {
    // // Use BFS method to find all commits in the Commit graph, starting from the
    // // commits refered by all branches
    // Set<Branch> branches = findAllBranches();

    // Set<Commit> commits = new HashSet<Commit>();
    // for (Branch branch : branches) {
    // commits.add(branch.dereference());
    // }

    // Set<Commit> set = new HashSet<Commit>();
    // Set<Commit> next_set = new HashSet<Commit>();
    // set.addAll(commits);
    // while (!set.isEmpty()) {
    // for (Commit commit : set) {
    // List<String> parentIds = commit.getParents();
    // for (String parentId : parentIds) {
    // Commit parentCommit = Commit.fromFile(parentId);
    // if (!commits.contains(parentCommit)) {
    // next_set.add(parentCommit);
    // }
    // }
    // }

    // commits.addAll(next_set);
    // set = next_set;
    // next_set = new HashSet<Commit>();
    // }

    // return commits;
    // }

    /**
     * Find all untracked file names in the current working directory.
     * 
     * @return all untracked file names in the current working directory.
     */
    private static Set<String> findAllUntrackedFileNames() {
        Set<String> allFileNames = findAllFileNames(CWD);
        Set<String> trackedFileNames = new HashSet<String>();

        Stage stage = Stage.fromFile();
        trackedFileNames.addAll(stage.getTrackedFiles().keySet());

        Set<String> addedFileNames = stage.getAddedFileNames();
        Set<String> removedFileNames = stage.getRemovedFileNames();
        trackedFileNames.addAll(addedFileNames);
        trackedFileNames.removeAll(removedFileNames);

        return differenceSet(allFileNames, trackedFileNames);
    }

    /**
     * Find all file names in the given file.
     * 
     * @param file the file.
     * @return all file names in the given file.
     */
    private static Set<String> findAllFileNames(File file) {
        Set<String> fileNames = new HashSet<String>();
        // ignore the .gitlet directory
        if (file.equals(GITLET_DIR)) {
            return fileNames;
        }

        if (file.isFile()) {
            fileNames.add(file.getName());
            return fileNames;
        } else {
            List<String> fileOrDirNames = plainFilenamesIn(file);
            for (String name : fileOrDirNames) {
                File subFile = join(file, name);
                fileNames.addAll(findAllFileNames(subFile));
            }
        }

        return fileNames;
    }
}
