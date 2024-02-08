package gitlet;

import java.io.File;
import java.io.IOException;
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

    @Test
    /**
     * Test add several files, commit, branch, add more files, remove origin files,
     * commit and checkout branch
     */
    public void testCheckoutBranch() {
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

        // create a branch
        Repository.branch("branch");

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
            Repository.rm(files[i].getName());
        }

        // commit
        Repository.commit("commit2");

        // checkout branch
        Repository.checkoutBranch("branch");

        // check stage should be empty
        Stage stage = Stage.fromFile();
        assertEquals("Should be the same", 0, stage.getAddedFiles().size());

        // check the files1 are exist
        for (int i = 0; i < files_num; i++) {
            assertTrue("Should be recovered", new File(files[i].getName()).exists());
        }
        // files2 are removed
        for (int i = 0; i < files_num; i++) {
            assertFalse("Should be removed", new File(files2[i].getName()).exists());
        }

        // delete the files
        for (int i = 0; i < files_num; i++) {
            files[i].delete();
            files2[i].delete();
        }
    }

    @Test
    /** Test branch and rm-branch */
    public void testBranchSimple() {
        removeFoler(Repository.GITLET_DIR);

        // create a branch
        Repository.init();
        Repository.branch("branch");

        // remove the branch
        Repository.rmBranch("branch");

        // check the branch
        File file = new File(Repository.REFS_DIR, "branch");
        assertFalse("Should be removed", file.exists());
    }

    @Test
    /**
     * Test two seperate branch checkout, from other -> master *
     * *********|--- commit3 (other)
     * commit1---->commit2 (master)
     */
    public void testBranchCheckout() {
        removeFoler(Repository.GITLET_DIR);

        String str1 = "this is java";
        String str2 = "this is python";

        Repository.init();
        // create a branch
        Repository.branch("other");

        // create two file
        File file = new File("test.txt");
        try {
            file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        writeContents(file, str1);
        File file2 = new File("test2.txt");
        try {
            file2.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        writeContents(file2, str1);

        Repository.add(file.getName());
        Repository.add(file2.getName());
        Repository.commit("Main two files");

        // checkout other
        Repository.checkoutBranch("other");

        // create file
        try {
            file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        writeContents(file, str2);

        Repository.add(file.getName());
        Repository.commit("commit2");
        Repository.branch("Alternative file");

        // checkout master
        Repository.checkoutBranch("master");
        // check the file
        String contents = readContentsAsString(file);
        assertEquals("Should be the same", str1, contents);
        contents = readContentsAsString(file2);
        assertEquals("Should be the same", str1, contents);

        // checkout other
        Repository.checkoutBranch("other");
        // check the file
        contents = readContentsAsString(file);
        assertEquals("Should be the same", str2, contents);
        // check the file2 not exist
        assertFalse("Should be removed", new File(file2.getName()).exists());
    }

    @Test
    /** Test add and status */
    public void testAddStatus() {
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

        // check status
        Repository.status();
    }

    // @Test
    // /** Test no reson to rm */
    // public void testNoRm() {
    // removeFoler(Repository.GITLET_DIR);

    // Repository.init();
    // Repository.rm("test.txt");
    // }

    @Test
    /** Test findSplitCommit */
    public void testFindSplitCommit() {
        removeFoler(Repository.GITLET_DIR);

        int nfile = 10;
        File files[] = new File[nfile];
        try {
            for (int i = 0; i < nfile; i++) {
                files[i] = new File("text" + i + ".txt");
                writeContents(files[i], "text");
                files[i].createNewFile();
            }
        } catch (IOException e) {
            System.err.println(e);
        }

        Repository.init();
        Repository.add(files[0].getName());
        Repository.commit("v1");
        Repository.add(files[1].getName());
        Repository.commit("split");
        Repository.branch("other");
        Repository.branch("split1");

        for (int i = 2; i < 7; i++) {
            if (i == 5) {
                Repository.branch("before");
                Repository.branch("split2");
            }
            Repository.add(files[i].getName());
            Repository.commit("v" + i);
        }

        Repository.checkoutBranch("other");
        for (int i = 7; i < 10; i++) {
            Repository.add(files[i].getName());
            Repository.commit("v" + i);
        }

        // check before--master
        Commit before = Branch.fromFile("before").dereference();
        Commit master = Branch.fromFile("master").dereference();
        Commit split2 = Branch.fromFile("split2").dereference();
        Commit split = Repository.findSplitCommit(before, master);
        assertEquals("Should be the same", split.getId(), split2.getId());

        // check master--other
        Commit other = Branch.fromFile("other").dereference();
        Commit split1 = Branch.fromFile("split1").dereference();
        split = Repository.findSplitCommit(master, other);
        assertEquals("Should be the same", split.getId(), split1.getId());
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
