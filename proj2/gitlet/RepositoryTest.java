package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;
import org.junit.Test;

import static gitlet.Utils.readContentsAsString;
import static gitlet.Utils.writeContents;
import static org.junit.Assert.*;

public class RepositoryTest {
    @Test
    /** Test init and add */
    public void testInitAdd() {
        // remove the .gitlet folder
        removeFoler(Repository.GITLET_DIR);

        // create a file
        File file = new File("test.txt");
        try {
            file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Repository.init();
        Repository.add(file.getName());

        // check stage
        Stage stage = Stage.fromFile();
        assertEquals("Should be the same", 1, stage.getAddedFiles().size());

        // delete the file
        file.delete();
    }

    @Test
    public void testInitMultAdd() {
        // remove the .gitlet folder
        removeFoler(Repository.GITLET_DIR);

        // create several files
        int files_num = 10;
        File[] files = new File[files_num];
        for (int i = 0; i < files_num; i++) {
            files[i] = new File("test" + i + ".txt");
            try {
                files[i].createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Repository.init();
        for (int i = 0; i < files_num; i++) {
            Repository.add(files[i].getName());
        }

        // check stage
        Stage stage = Stage.fromFile();
        assertEquals("Should be the same", files_num, stage.getAddedFiles().size());

        // delete the files
        for (int i = 0; i < files_num; i++) {
            files[i].delete();
        }
    }

    @Test
    /** Test init, add, commit */
    public void testInitAddCommit() {
        // remove the .gitlet folder
        removeFoler(Repository.GITLET_DIR);

        // create a file
        File file = new File("test.txt");
        try {
            file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Repository.init();
        Repository.add(file.getName());
        Repository.commit("commit");

        // check stage
        Stage stage = Stage.fromFile();
        assertEquals("Should be the same", 0, stage.getAddedFiles().size());
        assertEquals("Should be the same", 1, stage.getTrackedFiles().size());

        // check commit
        Head head = Head.fromFile();
        Branch branch = Branch.fromFile(head.getBranchName());
        Commit commit = branch.dereference();
        assertEquals("Should be the same", "commit", commit.getMessage());
        assertEquals("Should be the same", 1, commit.getTrackedFiles().size());

        // delete the file
        file.delete();
    }

    @Test
    /** Test add, remove, commit */
    public void testAddRmCommit() {
        // remove the .gitlet folder
        removeFoler(Repository.GITLET_DIR);

        // create two files
        File file1 = new File("test1.txt");
        File file2 = new File("test2.txt");
        try {
            file1.createNewFile();
            file2.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Repository.init();
        Repository.add(file1.getName());
        Repository.add(file2.getName());
        Repository.rm(file1.getName());
        Repository.commit("commit");

        // check stage
        Stage stage = Stage.fromFile();
        assertEquals("Should be the same", 0, stage.getAddedFiles().size());
        assertEquals("Should be the same", 0, stage.getRemovedFileNames().size());
        assertEquals("Should be the same", 1, stage.getTrackedFiles().size());

        // check commit
        Head head = Head.fromFile();
        Branch branch = Branch.fromFile(head.getBranchName());
        Commit commit = branch.dereference();
        assertEquals("Should be the same", "commit", commit.getMessage());
        assertEquals("Should be the same", 1, commit.getTrackedFiles().size());

        // delete the files
        file1.delete();
        file2.delete();
    }

    @Test
    /** Test add, commit, add more, remove untrackedFiles */
    public void testAddCommitAddRmUntracked() {
        // remove the .gitlet folder
        removeFoler(Repository.GITLET_DIR);

        // create several files
        int files_num = 10;
        File[] files = new File[files_num];
        for (int i = 0; i < files_num; i++) {
            files[i] = new File("test" + i + ".txt");
            try {
                files[i].createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Repository.init();
        for (int i = 0; i < files_num; i++) {
            Repository.add(files[i].getName());
        }

        Repository.commit("commit");

        // create more files
        File[] files2 = new File[files_num];
        for (int i = 0; i < files_num; i++) {
            files2[i] = new File("test_more" + i + ".txt");
            try {
                files2[i].createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // add the files
        for (int i = 0; i < files_num; i++) {
            Repository.add(files2[i].getName());
        }
        // remove these untracked files
        for (int i = 0; i < files_num; i++) {
            Repository.rm(files2[i].getName());
        }

        // check stage
        Stage stage = Stage.fromFile();
        assertEquals("Should be the same", 0, stage.getAddedFiles().size());
        assertEquals("Should be the same", 0, stage.getRemovedFileNames().size());
        assertEquals("Should be the same", files_num, stage.getTrackedFiles().size());

        // check commit
        Head head = Head.fromFile();
        Branch branch = Branch.fromFile(head.getBranchName());
        Commit commit = branch.dereference();
        assertEquals("Should be the same", "commit", commit.getMessage());
        assertEquals("Should be the same", files_num, commit.getTrackedFiles().size());

        // this untrackedFiles shouldn't be removed
        for (int i = 0; i < files_num; i++) {
            assertTrue("Should be the same", new File(files2[i].getName()).exists());
        }

        // delete the files
        for (int i = 0; i < files_num; i++) {
            files[i].delete();
            files2[i].delete();
        }
    }

    @Test
    /** Test add, commit, remove trackedFiles */
    public void testAddCommitRmTracked() {
        // remove the .gitlet folder
        removeFoler(Repository.GITLET_DIR);

        // create several files
        int files_num = 10;
        File[] files = new File[files_num];
        for (int i = 0; i < files_num; i++) {
            files[i] = new File("test" + i + ".txt");
            try {
                files[i].createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Repository.init();
        for (int i = 0; i < files_num; i++) {
            Repository.add(files[i].getName());
        }

        Repository.commit("commit");

        // remove the files
        for (int i = 0; i < files_num; i++) {
            Repository.rm(files[i].getName());
        }

        // check stage
        Stage stage = Stage.fromFile();
        assertEquals("Should be the same", 0, stage.getAddedFiles().size());
        assertEquals("Should be the same", files_num, stage.getRemovedFileNames().size());
        assertEquals("Should be the same", files_num, stage.getTrackedFiles().size());

        // check commit
        Head head = Head.fromFile();
        Branch branch = Branch.fromFile(head.getBranchName());
        Commit commit = branch.dereference();
        assertEquals("Should be the same", "commit", commit.getMessage());
        assertEquals("Should be the same", 10, commit.getTrackedFiles().size());

        // this trackedFiles should be removed
        for (int i = 0; i < files_num; i++) {
            assertFalse("Should be the same", new File(files[i].getName()).exists());
        }
    }

    @Test
    /** Test add, commit, modify file and checkout file */
    public void testCheckoutFile() {
        removeFoler(Repository.GITLET_DIR);

        String str1 = "hello, java";
        String str2 = "hello, python";
        // create a file with contents "java"
        File file = new File("test.txt");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        writeContents(file, str1);

        Repository.init();
        Repository.add(file.getName());
        Repository.commit("test");

        writeContents(file, str2);

        file = new File("test.txt");
        String contents = readContentsAsString(file);
        assertEquals("Should be the same", str2, contents);

        Repository.checkoutFile(file.getName());
        contents = readContentsAsString(file);
        assertEquals("Should be the same", str1, contents);
    }

    @Test
    /** Test add, commit, modify file and checkout file with commit id */
    public void testCheckoutFileWithCommitId() {
        removeFoler(Repository.GITLET_DIR);

        String str1 = "hello, java";
        String str2 = "hello, python";
        // create a file with contents "java"
        File file = new File("test.txt");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        writeContents(file, str1);

        Repository.init();
        Repository.add(file.getName());
        Repository.commit("test");

        writeContents(file, str2);

        file = new File("test.txt");
        String contents = readContentsAsString(file);
        assertEquals("Should be the same", str2, contents);

        // get the commit id
        Head head = Head.fromFile();
        Branch branch = Branch.fromFile(head.getBranchName());
        Commit commit = branch.dereference();
        String commitId = commit.getId();

        Repository.checkoutFile(commitId, file.getName());
        contents = readContentsAsString(file);
        assertEquals("Should be the same", str1, contents);
    }

    private static void removeFoler(File file) {
        if (file.isDirectory()) {
            for (File subFile : file.listFiles()) {
                removeFoler(subFile);
            }
        }
        file.delete();
    }
}
