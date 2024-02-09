package gitlet;

import java.util.*;

import static gitlet.Utils.join;
import static gitlet.Utils.plainFilenamesIn;

import java.io.*;

public class MyUtils {
    public static void exit(String msg, Object... args) {
        System.out.printf(msg + "\n", args);
        System.exit(0);
    }

    /**
     * The intersection of two sets
     * 
     * @param <T>  Element type
     * @param set1
     * @param set2
     * @return The intersection of two sets
     */
    public static <T> Set<T> intersectionSet(Set<T> set1, Set<T> set2) {
        Set<T> result = new HashSet<>();
        for (T o : set1) {
            if (set2.contains(o)) {
                result.add(o);
            }
        }
        return result;
    }

    /**
     * The union of two sets
     * 
     * @param <T>  Element type
     * @param set1
     * @param set2
     * @return The union of two sets
     */
    public static <T> Set<T> unionSet(Set<T> set1, Set<T> set2) {
        Set<T> result = new HashSet<>(set1);
        result.addAll(set2);
        return result;
    }

    /**
     * The difference of two sets
     * 
     * @param <T>  Element type
     * @param set1
     * @param set2
     * @return The difference of two sets
     */
    public static <T> Set<T> differenceSet(Set<T> set1, Set<T> set2) {
        Set<T> result = new HashSet<>(set1);
        result.removeAll(set2);
        return result;
    }

    /**
     * Get the current branch.
     * 
     * @return the current branch.
     */
    public static Branch currentBranch() {
        Head head = Head.fromFile();
        Branch branch = head.dereference();
        return branch;
    }

    /**
     * Get the current commit.
     * 
     * @return the current commit.
     */
    public static Commit currentCommit() {
        Branch branch = currentBranch();
        Commit curCommit = branch.dereference();
        return curCommit;
    }

    /**
     * Find all branches in the repository
     * 
     * @return all branches in the repository
     */
    public static Set<Branch> branches() {
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
    public static Set<Commit> commits() {
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
     * Find the split point of the two commits.
     * 
     * @param commit1 the first commit.
     * @param commit2 the second commit.
     * @return the split commit.
     */
    public static Commit splitCommitOf(Commit commit1, Commit commit2) {
        Set<Commit> prevCommits1 = prevCommitsOf(commit1);
        Set<Commit> prevCommits2 = prevCommitsOf(commit2);

        Set<Commit> commonCommit = MyUtils.intersectionSet(prevCommits1, prevCommits2);
        Commit lcaCommit = null; // last common ancestor commit
        for (Commit commit : commonCommit) {
            if (lcaCommit == null || commit.dateAfter(lcaCommit)) {
                lcaCommit = commit;
            }
        }

        return lcaCommit;
    }

    /**
     * Find all previous commits of the given commit.
     * 
     * @param commit the given commit.
     * @return
     */
    private static Set<Commit> prevCommitsOf(Commit commit) {
        Set<Commit> commits = new HashSet<Commit>();
        commits.add(commit);

        for (String parentId : commit.getParentIds()) {
            Commit parentCommit = Commit.fromFile(parentId);
            commits.addAll(prevCommitsOf(parentCommit));
        }

        return commits;
    }

    /**
     * Check if the branch exists.
     * 
     * @param branchName the name of the branch.
     * @return
     */
    public static boolean branchExists(String branchName) {
        File file = Utils.join(Repository.REFS_DIR, branchName);
        if (!file.exists()) {
            return false;
        }
        return true;
    }

    /**
     * Check if the branch is the current branch.
     * 
     * @param branchName the name of the branch.
     * @return
     */
    public static boolean isCurrentBranch(String branchName) {
        Branch branch = Branch.fromFile(branchName);
        Branch curBranch = currentBranch();
        return branch.equals(curBranch);
    }

    /**
     * check if checkout will overwrite the untracked files
     */
    public static boolean willOverwriteUntrackedFilesBy(Commit commit) {
        Set<String> untrackedFileNames = untrackedFileNames();
        Set<String> commitTrackedFileNames = commit.getTrackedFileNames();
        Set<String> intersect = intersectionSet(untrackedFileNames, commitTrackedFileNames);
        return !intersect.isEmpty();
    }

    /**
     * Find all untracked file names in the current working directory.
     * 
     * @return all untracked file names in the current working directory.
     */
    public static Set<String> untrackedFileNames() {
        Set<String> allFileNames = workingFileNames(Repository.CWD);
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
     * @return all [path:blobId] of files
     */
    public static Map<String, String> workingFiles() {
        Map<String, String> workingFiles = new HashMap<String, String>();
        Set<String> fileNames = workingFileNames(Repository.CWD);
        for (String fileName : fileNames) {
            File file = join(Repository.CWD, fileName);
            Blob blob = new Blob(file);
            workingFiles.put(fileName, blob.getId());
        }
        return workingFiles;
    }

    /**
     * Find all file names in the given file.
     * 
     * @param file the file.
     * @return all file names in the given file.
     */
    private static Set<String> workingFileNames(File file) {
        Set<String> fileNames = new HashSet<String>();
        // ignore the .gitlet directory
        if (file.equals(Repository.GITLET_DIR)) {
            return fileNames;
        }

        if (file.isFile()) {
            fileNames.add(file.getName());
            return fileNames;
        } else {
            List<String> fileOrDirNames = plainFilenamesIn(file);
            for (String name : fileOrDirNames) {
                File subFile = join(file, name);
                fileNames.addAll(workingFileNames(subFile));
            }
        }

        return fileNames;
    }
}
