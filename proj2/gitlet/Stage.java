package gitlet;

import static gitlet.Utils.readObject;
import static gitlet.Utils.restrictedDelete;
import static gitlet.Utils.writeObject;

import java.io.File;
import java.util.*;

public class Stage implements Dumpable {

    /** The tracked files */
    private Map<String, String> trackedFiles;
    /** The added files */
    private Map<String, String> addedFiles;
    /** The removed files */
    private Set<String> removedFileNames;
    /** The index file */
    private static final File file = Repository.STAGE_FILE;

    public Stage() {
        this.addedFiles = new HashMap<String, String>();
        this.removedFileNames = new HashSet<String>();
        this.trackedFiles = new HashMap<String, String>();
    }

    /**
     * Create a new stage from the STAGE file.
     * 
     * @return the new stage
     */
    public static Stage fromFile() {
        return readObject(file, Stage.class);
    }

    /**
     * Save the stage to the STAGE file.
     */
    public void saveStage() {
        writeObject(file, this);
    }

    /**
     * Add a file to the stage.
     * 
     * @param file the file to add
     * @return true if the file was added, false otherwise
     */
    public boolean addFile(File file) {
        boolean changed = false;

        Blob blob = new Blob(file);
        String fileName = blob.getFileName();
        String blobId = blob.getId();

        if (removedFileNames.remove(fileName)) {
            changed = true;
        }

        if (trackedFiles.containsKey(fileName)) {
            if (trackedFiles.get(fileName).equals(blobId)) {
                if (addedFiles.remove(fileName) != null) {
                    changed = true;
                }
                return changed;
            }
        }

        String prevBlobId = addedFiles.put(fileName, blobId);
        if (prevBlobId != null && prevBlobId.equals(blobId)) {
            changed = false;
        } else {
            changed = true;
            if (!blob.getFile().exists()) {
                blob.saveBlob();
            }
        }

        return changed;
    }

    /**
     * Remove a file from the stage.
     * 
     * @param file the file to remove
     * @return true if the file was removed, false otherwise
     */
    public boolean removeFile(File file) {
        boolean changed = false;

        String fileName = file.getName();

        if (addedFiles.remove(fileName) != null) {
            changed = true;
        }

        if (trackedFiles.containsKey(fileName)) {
            if (removedFileNames.add(fileName)) {
                changed = true;
            }

            if (file.exists()) {
                restrictedDelete(file);
            }
        }

        return changed;
    }

    /**
     * Commit the stage changes to trackedFiles.
     * 
     * @return true if the stage was committed, false otherwise
     */
    public boolean commitChanges() {
        if (isEmpty()) {
            return false;
        }

        trackedFiles.putAll(addedFiles);

        for (String fileName : removedFileNames) {
            trackedFiles.remove(fileName);
        }

        clearChanges();

        return true;
    }

    /**
     * Get the added files.
     * 
     * @return the added files
     */
    public Map<String, String> getAddedFiles() {
        return addedFiles;
    }

    /**
     * Get the added file names.
     * 
     * @return the added file names
     */
    public Set<String> getAddedFileNames() {
        return addedFiles.keySet();
    }

    /**
     * Get the removed file names.
     * 
     * @return the removed file names
     */
    public Set<String> getRemovedFileNames() {
        return removedFileNames;
    }

    /**
     * Get the tracked files.
     * 
     * @return the tracked files
     */
    public Map<String, String> getTrackedFiles() {
        return trackedFiles;
    }

    /**
     * Clear the stage.
     */
    private void clearChanges() {
        addedFiles.clear();
        removedFileNames.clear();
    }

    /**
     * Clear the stage when checkout to other branch.
     * 
     * @param trackFiles the track files of the given branch
     */
    public void clearForCheckoutCommit(Commit commit) {
        addedFiles.clear();
        removedFileNames.clear();
        this.trackedFiles = commit.getTrackedFiles();
    }

    /**
     * Check if the stage is empty.
     * 
     * @return true if the stage is empty
     */
    public boolean isEmpty() {
        return addedFiles.isEmpty() && removedFileNames.isEmpty();
    }

    /**
     * String representation of the tracked files.
     * Format:
     * Stage:
     * <fileName1> (<blobId1>)
     * <fileName2> (<blobId2>)
     * ...
     * 
     * @return the tracked files
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Stage:\n");
        for (String fileName : addedFiles.keySet()) {
            sb.append(fileName + " (" + addedFiles.get(fileName) + ")\n");
        }
        return sb.toString();
    }

    @Override
    public void dump() {
        System.out.println(this);
    }
}
