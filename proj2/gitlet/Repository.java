package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static gitlet.MyUtils.differenceSet;
import static gitlet.MyUtils.unionSet;
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
            if (branch.equals(curBranch)) {
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
        Branch branch = Branch.fromFile(branchName);
        if (branch.equals(currentBranch())) {
            MyUtils.exit("No need to checkout the current branch.");
        }
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

    /**
     * Merge the branch with the given branch name.
     * 
     * @param branchName the branch name.
     */
    public static void merge(String branchName) {
        Stage stage = Stage.fromFile();
        if (!stage.isEmpty()) {
            MyUtils.exit("You have uncommitted changes.");
        }

        File file = join(REFS_DIR, branchName);
        if (!file.exists()) {
            MyUtils.exit("A branch with that name does not exist.");
        }
        Branch branch = Branch.fromFile(branchName);
        if (branch.equals(currentBranch())) {
            MyUtils.exit("Cannot merge a branch with itself.");
        }

        Commit curCommit = currentCommit();
        Commit otherCommit = branch.dereference();
        Commit splitCommit = findSplitCommit(curCommit, otherCommit);

        // check if the split commit is the same as the given branch
        if (splitCommit.equals(otherCommit)) {
            MyUtils.exit("Given branch is an ancestor of the current branch.");
        }
        // check if the split commit is the same as the current branch
        if (splitCommit.equals(curCommit)) {
            checkoutBranch(branchName);
            MyUtils.exit("Current branch fast-forwarded.");
        }

        Map<String, String> curFiles = curCommit.getTrackedFiles();
        Map<String, String> otherFiles = otherCommit.getTrackedFiles();
        Map<String, String> splitFiles = splitCommit.getTrackedFiles();
        Set<String> allFiles = unionSet(curFiles.keySet(), otherFiles.keySet());

        Map<String, String> mergedFiles = new HashMap<String, String>();
        Set<String> removedFileNames = new HashSet<String>();
        Map<String, String> conflictFiles = new HashMap<String, String>();

        boolean conflict = false;
        for (String fileName : allFiles) {
            if (curFiles.containsKey(fileName) && otherFiles.containsKey(fileName)) {
                String curBlobId = curFiles.get(fileName);
                String otherBlobId = otherFiles.get(fileName);
                if (curBlobId.equals(otherBlobId)) {
                    mergedFiles.put(fileName, curBlobId);
                } else {
                    if (!splitFiles.containsKey(fileName)) {
                        // handle conflict
                        conflict = true;
                        String contents = conflictContents(fileName, curBlobId, otherBlobId);
                        conflictFiles.put(fileName, contents);
                    } else {
                        String splitBlobId = splitFiles.get(fileName);
                        if (splitBlobId.equals(otherBlobId) && !splitBlobId.equals(curBlobId)) {
                            mergedFiles.put(fileName, curBlobId);
                        } else if (splitBlobId.equals(curBlobId) && !splitBlobId.equals(otherBlobId)) {
                            mergedFiles.put(fileName, otherBlobId);
                        } else {
                            // handle conflict
                            conflict = true;
                            String contents = conflictContents(fileName, curBlobId, otherBlobId);
                            conflictFiles.put(fileName, contents);
                        }
                    }
                }
            } else if (curFiles.containsKey(fileName) && !otherFiles.containsKey(fileName)) {
                if (!splitFiles.containsKey(fileName)) {
                    String curBlobId = curFiles.get(fileName);
                    mergedFiles.put(fileName, curBlobId);
                } else {
                    String curBlobId = curFiles.get(fileName);
                    String splitBlobId = splitFiles.get(fileName);
                    if (splitBlobId.equals(curBlobId)) {
                        // remove this file
                        removedFileNames.add(fileName);
                    } else {
                        // handle conflict
                        conflict = true;
                        String contents = conflictContents(fileName, curBlobId, "");
                        conflictFiles.put(fileName, contents);
                    }
                }
            } else {
                if (!splitFiles.containsKey(fileName)) {
                    String otherBlobId = otherFiles.get(fileName);
                    mergedFiles.put(fileName, otherBlobId);
                } else {
                    String otherBlobId = otherFiles.get(fileName);
                    String splitBlobId = splitFiles.get(fileName);
                    if (splitBlobId.equals(otherBlobId)) {
                        // nothing to do
                    } else {
                        // handle conflict
                        conflict = true;
                        String contents = conflictContents(fileName, "", otherBlobId);
                        conflictFiles.put(fileName, contents);
                    }
                }
            }
        }

        // check if there are untracked files are overwritten
        Set<String> untrackedFileNames = findAllUntrackedFileNames();
        Set<String> allMergedFileNames = unionSet(mergedFiles.keySet(), conflictFiles.keySet());
        if (MyUtils.intersectionSet(allMergedFileNames, untrackedFileNames).size() > 0) {
            MyUtils.exit("There is an untracked file in the way; delete it, or add and commit it first.");
        }

        // print the conflict message
        if (conflict) {
            System.out.println("Encountered a merge conflict.");
        }
        // write conflict contents to file and blob
        for (String fileName : conflictFiles.keySet()) {
            File fileToWrite = new File(fileName);
            String contents = conflictFiles.get(fileName);
            writeContents(fileToWrite, contents.getBytes());

            Blob blob = new Blob(fileToWrite);
            blob.saveBlob();
            mergedFiles.put(fileName, blob.getId());
        }
        // checkout merged files
        for (String fileName : mergedFiles.keySet()) {
            String blobId = mergedFiles.get(fileName);
            Blob blob = Blob.fromFile(blobId);
            byte contents[] = blob.getContents();
            File fileToWrite = new File(fileName);
            writeContents(fileToWrite, contents);
        }

        // delete files
        for (String fileName : removedFileNames) {
            File fileToRemove = new File(fileName);
            fileToRemove.delete();
        }

        // create a new commit
        Branch curBranch = currentBranch();
        String message = String.format("Merged %s into %s.", branchName, curBranch.getBranchName());
        LinkedList<String> parentIds = new LinkedList<String>();
        parentIds.add(curCommit.getId());
        parentIds.add(otherCommit.getId());
        Commit commit = new Commit(message, parentIds, mergedFiles);
        commit.saveCommit();

        // update the current branch
        curBranch.referTo(commit.getId());
        curBranch.saveBranch();

        // clear stage
        stage.clearForCheckoutCommit(commit);
        stage.saveStage();
    }

    /**
     * Find the split point of the two commits.
     * 
     * @param commit1 the first commit.
     * @param commit2 the second commit.
     * @return the split commit.
     */
    public static Commit findSplitCommit(Commit commit1, Commit commit2) {
        Set<Commit> prevCommits1 = findPrevCommits(commit1);
        Set<Commit> prevCommits2 = findPrevCommits(commit2);

        Set<Commit> commonCommit = MyUtils.intersectionSet(prevCommits1, prevCommits2);
        Commit lcaCommit = null; // last common ancestor commit
        for (Commit commit : commonCommit) {
            if (lcaCommit == null || commit.dateAfter(lcaCommit)) {
                lcaCommit = commit;
            }
        }

        return lcaCommit;
    }

    public static Set<Commit> findPrevCommits(Commit commit) {
        Set<Commit> commits = new HashSet<Commit>();
        commits.add(commit);

        for (String parentId : commit.getParentIds()) {
            Commit parentCommit = Commit.fromFile(parentId);
            commits.addAll(findPrevCommits(parentCommit));
        }

        return commits;
    }

    private static String conflictContents(String fileName, String curBlobId, String otherBlobId) {
        String curContents = "";
        if (!curBlobId.equals("")) {
            Blob curBlob = Blob.fromFile(curBlobId);
            curContents = new String(curBlob.getContents());
        }
        String otherContents = "";
        if (!otherBlobId.equals("")) {
            Blob otherBlob = Blob.fromFile(otherBlobId);
            otherContents = new String(otherBlob.getContents());
        }

        String contents = String.format("<<<<<<< HEAD\n%s=======\n%s>>>>>>>\n", curContents, otherContents);
        return contents;
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
