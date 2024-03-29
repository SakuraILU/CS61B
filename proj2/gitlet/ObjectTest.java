package gitlet;

import org.junit.Test;
import java.util.*;

import static gitlet.Utils.writeContents;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

public class ObjectTest {
    @Test
    /** create file, fromFile, saveBlob, getId and getContentAsString */
    public void testBlobSimple() throws IOException {
        String contents = "Hello, world!\n Hello, java!\n\n Hello, gitlet!\n";
        // create file
        File file = new File("test.txt");
        file.createNewFile();
        writeContents(file, contents);
        // create Blob
        Blob blob = new Blob(file);
        // saveBlob
        blob.saveBlob();

        // fromFile
        Blob blob2 = Blob.fromFile(blob.getId());
        // getContentAsString
        String blobContents = blob2.getContentsAsString();

        // assert ID
        assertEquals("Should be the same", blob.getId(), blob2.getId());
        // assert Contents
        assertEquals("Should be the same", contents, blobContents);

        // delete file
        file.delete();
    }

    @Test
    /** Test file creation, fromFile, saveCommit, getId and getMessage */
    public void testCommitSimple() throws IOException {
        List<String> parents = new LinkedList<String>();
        Map<String, String> trackedFiles = new HashMap<String, String>();

        // create Commit
        Commit commit = new Commit("Hello, world!", parents, trackedFiles);
        // saveCommit
        commit.saveCommit();

        // fromFile
        Commit commit2 = Commit.fromFile(commit.getId());

        // assert ID
        assertEquals("Should be the same", commit.getId(), commit2.getId());
        // assert Message
        assertEquals("Should be the same", "Hello, world!", commit2.getMessage());
    }

    @Test
    /** Test file creation, fromFile, saveCommit, getId and getMessage */
    public void testCommit() throws IOException {
        List<String> parents = new LinkedList<String>();
        for (int i = 0; i < 2; i++) {
            parents.add("commit_id" + i);
        }

        Map<String, String> trackedFiles = new HashMap<String, String>();
        for (int i = 0; i < 10; i++) {
            trackedFiles.put("file" + i, "blob" + i);
        }

        // create Commit
        Commit commit = new Commit("Hello, world!", parents, trackedFiles);
        // saveCommit
        commit.saveCommit();

        // fromFile
        Commit commit2 = Commit.fromFile(commit.getId());

        // assert ID
        assertEquals("Should be the same", commit.getId(), commit2.getId());
        // assert Message
        assertEquals("Should be the same", "Hello, world!", commit2.getMessage());
    }

    @Test
    /** Test Stage */
    public void testStageSimple() {
        Stage stage = new Stage();
        stage.saveStage();

        Stage stage2 = Stage.fromFile();

        assertEquals("Should be the same",
                stage.getAddedFiles(), stage2.getAddedFiles());
        assertEquals("Should be the same",
                stage.getRemovedFileNames(), stage2.getRemovedFileNames());
    }

    @Test
    /** Test add files and Stage */
    public void testStage() throws IOException {
        // create several files
        List<File> files = new LinkedList<File>();
        for (int i = 0; i < 10; i++) {
            File file = new File("test" + i + ".txt");
            file.createNewFile();
            writeContents(file, "Hello, world!\n Hello, java!\n\n Hello, gitlet!\n");
            files.add(file);
        }

        // create Stage
        Stage stage = new Stage();
        for (File file : files) {
            stage.addFile(file);
        }

        // saveStage
        stage.saveStage();

        // fromFile
        Stage stage2 = Stage.fromFile();

        // assert addedFiles
        assertEquals("Should be the same",
                stage.getAddedFiles(), stage2.getAddedFiles());
        // assert removedFileNames
        assertEquals("Should be the same",
                stage.getRemovedFileNames(), stage2.getRemovedFileNames());

        // assert trackedFiles with file lists
        stage2.commitChanges();
        Map<String, String> trackedFiles = stage2.getTrackedFiles();
        assertEquals("Should be the same", files.size(), trackedFiles.size());
        for (File file : files) {
            assertTrue("Should contain", trackedFiles.containsKey(file.getName()));
        }

        // delete files
        for (File file : files) {
            file.delete();
        }
    }

    @Test
    /** Test add, remove files and Stage */
    public void testStageAddRemove() throws IOException {
        // create several files
        List<File> files = new LinkedList<File>();
        for (int i = 0; i < 10; i++) {
            File file = new File("test" + i + ".txt");
            file.createNewFile();
            writeContents(file, "Hello, world!\n Hello, java!\n\n Hello, gitlet!\n");
            files.add(file);
        }

        // create Stage
        Stage stage = new Stage();
        for (File file : files) {
            stage.addFile(file);
        }
        // remove files
        for (File file : files) {
            stage.removeFile(file);
        }
        stage.saveStage();

        // fromFile
        Stage stage2 = Stage.fromFile();

        // assert empty
        assertEquals("Should be the same", 0, stage2.getAddedFiles().size());

        // delete files
        for (File file : files) {
            file.delete();
        }
    }

    @Test
    /** add, commit, add more, remove, check stage */
    public void testStageCommit() throws IOException {
        int fileNum = 10;

        // create several files
        List<File> files = new LinkedList<File>();
        for (int i = 0; i < fileNum; i++) {
            File file = new File("test" + i + ".txt");
            file.createNewFile();
            writeContents(file, "Hello, world!\n Hello, java!\n\n Hello, gitlet!\n");
            files.add(file);
        }

        // create Stage
        Stage stage = new Stage();
        for (File file : files) {
            stage.addFile(file);
        }
        stage.saveStage();

        // commit
        stage.commitChanges();
        Map<String, String> trackedFiles = stage.getTrackedFiles();

        // add more files
        List<File> filesMore = new LinkedList<File>();
        for (int i = 0; i < fileNum; i++) {
            File file = new File("test_more" + i + ".txt");
            file.createNewFile();
            writeContents(file, "Hello, world!\n Hello, java!\n\n Hello, gitlet!\n");
            filesMore.add(file);
        }
        for (File file : filesMore) {
            stage.addFile(file);
        }

        // remove half of the files
        for (int i = 0; i < filesMore.size() / 2; i++) {
            stage.removeFile(files.get(i));
        }
        stage.saveStage();

        // fromFile
        Stage stage2 = Stage.fromFile();

        // assert addedFiles should be filesMore
        assertEquals("Should be the same", filesMore.size(), stage2.getAddedFiles().size());
        for (File file : filesMore) {
            assertTrue("Should contain", stage2.getAddedFiles().containsKey(file.getName()));
        }

        // assert removedFileNames should be the left half of files
        assertEquals("Should be the same", fileNum / 2, stage2.getRemovedFileNames().size());
        for (int i = 0; i < filesMore.size() / 2; i++) {
            assertTrue("Should contain",
                    stage2.getRemovedFileNames().contains("test" + i + ".txt"));
        }

        // assert trackedFiles should be the files
        assertEquals("Should be the same", fileNum, trackedFiles.size());
        for (File file : files) {
            assertTrue("Should contain", trackedFiles.containsKey(file.getName()));
        }

        // delete files
        for (File file : files) {
            file.delete();
        }
        for (File file : filesMore) {
            file.delete();
        }
    }
}
