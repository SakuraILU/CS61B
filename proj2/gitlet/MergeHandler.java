package gitlet;

import java.util.*;
import java.io.*;

import static gitlet.Utils.*;

public class MergeHandler {
    /** The current branch */
    private static Branch curBranch;
    /** The other branch to be merged in */
    private static Branch otherBranch;

    /** The current commit */
    private static Commit curCommit;
    /** The other commit to be merged in */
    private static Commit otherCommit;
    /** The split point commit */
    private static Commit splitCommit;

    /** The tracked files in merged commit */
    private static Map<String, String> trackedFiles;
    /** The files to be removed */
    private static Set<String> removedFileNames;
    /** The conflict files in merged commit, also are part of tracked files */
    private static Map<String, String> conflictFiles;

    /**
     * Merge the given branch into the current branch.
     * 
     * @param branch the branch to be merged in.
     * @throws IOException if an I/O error occurs.
     */
    public static void merge(Branch branch) throws IOException {
        clear();

        curBranch = MyUtils.currentBranch();
        otherBranch = branch;

        curCommit = curBranch.dereference();
        otherCommit = otherBranch.dereference();
        splitCommit = MyUtils.splitCommitOf(curCommit, otherCommit);

        if (specialMergeIfPossible()) {
            return;
        }

        // create tracked, removed, and conflicted files
        createMergeFiles();

        // check if there are untracked files are overwritten
        Set<String> untrackedFileNames = MyUtils.untrackedFileNames();
        Set<String> allMergedFileNames = MyUtils.unionSet(
                trackedFiles.keySet(), conflictFiles.keySet());
        if (MyUtils.intersectionSet(allMergedFileNames, untrackedFileNames).size() > 0) {
            MyUtils.exit("There is an untracked file in the way; delete it, "
                    + "or add and commit it first.");
        }

        // print the conflict message
        if (conflictFiles.size() > 0) {
            System.out.println("Encountered a merge conflict.");
        }
        commitMerge();
    }

    /**
     * Check if the merge is special and can be handled directly.
     * 
     * @return true if the merge is special and can be handled directly, false
     *         otherwise.
     */
    private static boolean specialMergeIfPossible() {
        String branchName = otherBranch.getBranchName();

        // check if the split commit is the same as the given branch
        if (splitCommit.equals(otherCommit)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return true;
        }
        // check if the split commit is the same as the current branch
        if (splitCommit.equals(curCommit)) {
            Repository.checkoutBranch(branchName);
            System.out.println("Current branch fast-forwarded.");
            return true;
        }
        return false;
    }

    /**
     * Create the tracked, removed, and conflicted files for the merge.
     */
    private static void createMergeFiles() {
        trackedFiles = new HashMap<String, String>();
        removedFileNames = new HashSet<String>();
        conflictFiles = new HashMap<String, String>();

        Map<String, String> cFiles = curCommit.getTrackedFiles();
        Map<String, String> oFiles = otherCommit.getTrackedFiles();
        Map<String, String> sFiles = splitCommit.getTrackedFiles();
        Set<String> allFiles = MyUtils.unionSet(cFiles.keySet(), oFiles.keySet());

        for (String fileName : allFiles) {
            String sId = sFiles.get(fileName) == null ? "" : sFiles.get(fileName);
            String cId = cFiles.get(fileName) == null ? "" : cFiles.get(fileName);
            String oId = oFiles.get(fileName) == null ? "" : oFiles.get(fileName);

            if (sId.equals(cId) && sId.equals(oId)) {
                // file in all three commits is the same
                // nothing to do
                continue;
            } else if (sId.equals(cId)) {
                // file in split and current commit is the same
                if (oId.equals("")) {
                    // other commit removed this file
                    removedFileNames.add(fileName);
                } else {
                    // other commit modified this file
                    trackedFiles.put(fileName, oId);
                }
            } else if (sId.equals(oId)) {
                // file in split and other commit is the same
                if (cId.equals("")) {
                    // current commit removed this file
                    continue;
                } else {
                    // current commit modified this file
                    trackedFiles.put(fileName, cId);
                }
            } else if (cId.equals(oId)) {
                // file in current and other commit is the same
                trackedFiles.put(fileName, cId);
            } else {
                // file in all three commits is different
                // handle conflict
                String contents = conflictContents(cId, oId);
                conflictFiles.put(fileName, contents);
            }
        }
    }

    /**
     * Commit the merge, handle the merged Commit, Stage and Branch.
     * 
     * @throws IOException if an I/O error occurs.
     */
    private static void commitMerge() throws IOException {
        // write conflict contents to blob, add them to trackedFiles
        for (String fileName : conflictFiles.keySet()) {
            File fileToWrite = new File(fileName);
            String contents = conflictFiles.get(fileName);
            writeContents(fileToWrite, contents.getBytes());

            Blob blob = new Blob(fileToWrite);
            blob.saveBlob();
            trackedFiles.put(fileName, blob.getId());
        }
        // checkout added files
        for (String fileName : trackedFiles.keySet()) {
            String blobId = trackedFiles.get(fileName);
            Blob blob = Blob.fromFile(blobId);
            byte[] contents = blob.getContents();
            File fileToWrite = new File(fileName);
            writeContents(fileToWrite, contents);
        }

        // delete files
        for (String fileName : removedFileNames) {
            File fileToRemove = new File(fileName);
            fileToRemove.delete();
        }

        // create a new commit
        String message = String.format("Merged %s into %s.",
                otherBranch.getBranchName(), curBranch.getBranchName());
        LinkedList<String> parentIds = new LinkedList<String>();
        parentIds.add(curCommit.getId());
        parentIds.add(otherCommit.getId());
        Commit commit = new Commit(message, parentIds, trackedFiles);
        commit.saveCommit();

        // update the current branch
        curBranch.referTo(commit.getId());
        curBranch.saveBranch();

        // clear stage
        Stage stage = Stage.fromFile();
        stage.clearForCheckoutCommit(commit);
        stage.saveStage();
    }

    /**
     * Get the conflict contents of the given two blobs.
     * 
     * @param curBlobId   the blob id of the current commit.
     * @param otherBlobId the blob id of the other commit.
     * @return
     */
    private static String conflictContents(String curBlobId, String otherBlobId) {
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

        String contents = String.format("<<<<<<< HEAD\n%s=======\n%s>>>>>>>\n",
                curContents, otherContents);
        return contents;
    }

    /**
     * Clear the fields.
     */
    private static void clear() {
        curBranch = null;
        otherBranch = null;
        curCommit = null;
        otherCommit = null;
        splitCommit = null;
        trackedFiles = null;
        removedFileNames = null;
        conflictFiles = null;
    }
}
