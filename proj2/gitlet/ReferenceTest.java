package gitlet;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.*;

import org.junit.Test;

public class ReferenceTest {
    @Test
    /** Test Reference from file simple */
    public void testReferenceFromFile() throws IOException {
        Branch branch = new Branch("master", "123");
        branch.saveBranch();

        Branch branch2 = Branch.fromFile("master");
        assertEquals("Should refer to 123", "123", branch2.getCommitId());
    }

    @Test
    /** Test Reference from file simple */
    public void testReference() throws IOException {
        Branch branch = new Branch("master", "123");
        branch.saveBranch();
        assertEquals("Should refer to 123", "123", branch.getCommitId());

        Branch branch2 = Branch.fromFile("master");
        branch2.referTo("456");
        assertEquals("Should refer to 456 after changing", "456", branch2.getCommitId());
    }

    @Test
    /** Test save and fromFile */
    public void testSaveFromFile() throws IOException {
        Branch branch = new Branch("master", "123");
        branch.saveBranch();

        Branch branch2 = Branch.fromFile("master");
        assertEquals("Should refer to 123", "123", branch2.getCommitId());
    }

    @Test
    /** Test head */
    public void testHeadSimple() {
        Head head = new Head();
        head.saveHead();

        Head head2 = Head.fromFile();
        assertEquals("Should refer to master", "master", head2.getBranchName());
    }

    @Test
    /** Test head refer to a branch */
    public void testHeadReferToBranch() throws IOException {
        // create a commit
        List<String> parents = new LinkedList<String>();
        Map<String, String> trackedFiles = new HashMap<String, String>();
        Commit commit = new Commit("Hello, world!", parents, trackedFiles);
        commit.saveCommit();

        // create a branch and refer to the commit
        Branch branch = new Branch("master", commit.getId());
        branch.saveBranch();

        // create a head and refer to the branch
        Head head = new Head();
        head.referTo("master");
        head.saveHead();

        // load the head
        Head head2 = Head.fromFile();
        assertEquals("Should refer to the branch", "master", head2.getBranchName());

        // dereference the head
        Branch branch2 = head2.dereference();
        assertEquals("Should refer to the commit", commit.getId(), branch2.getCommitId());

        // dereference the branch
        Commit commit2 = branch2.dereference();
        // assert the commit
        assertEquals("Should be the same", commit.getId(), commit2.getId());
        // assert the message
        assertEquals("Should be the same", "Hello, world!", commit2.getMessage());
    }
}
