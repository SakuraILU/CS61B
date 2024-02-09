package gitlet;

import java.io.File;

public class Head implements Dumpable {
    /** The file that stores the head reference. */
    private static final File FILE = Repository.HEAD_FILE;
    /** The head reference */
    private String branchName;

    /**
     * The defualt constructor. The head refers to the master branch.
     */
    public Head() {
        this.branchName = "master";
    }

    /**
     * The constructor with branch name.
     * 
     * @return Head instance
     */
    public Head(String branchName) {
        this.branchName = branchName;
    }

    /**
     * Load the head reference from a Object File
     * 
     * @return Head instance
     */
    public static Head fromFile() {
        return Utils.readObject(FILE, Head.class);
    }

    /**
     * Save the head reference to a Object File
     */
    public void saveHead() {
        Utils.writeObject(FILE, this);
    }

    /**
     * Change the branch that the head refers to.
     * 
     * @param name the name of the branch that the head refers to.
     */
    public void referTo(String name) {
        this.branchName = name;
    }

    /**
     * dereference to the Branch object that the head refers to.
     * 
     * @return Branch object
     */
    public Branch dereference() {
        return Branch.fromFile(branchName);
    }

    /**
     * Get the name of the reference.
     * 
     * @return the name of the reference.
     */
    public String getBranchName() {
        return branchName;
    }

    /**
     * String representation of the head reference.
     * Format:
     * "ref: refs/heads/[Branch name]"
     */
    public String toString() {
        return "ref: refs/heads/" + branchName;
    }

    @Override
    /**
     * Dump the head reference.
     */
    public void dump() {
        System.out.println(this);
    }
}
