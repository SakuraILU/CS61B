package gitlet;

import java.io.File;

public class Branch implements Dumpable {
    /** The name of the reference. */
    private final String name;
    /** The file that stores the reference. */
    private final File file;
    /** The commit id that the branch refers to. */
    private String commitId;

    /**
     * The Reference constructor.
     * 
     * @param name     the name of the reference.
     * @param commitId the id that the reference refers to.
     */
    public Branch(String name, String commitId) {
        this.name = name;
        this.file = Utils.join(Repository.REFS_DIR, name);
        this.commitId = commitId;
    }

    public void saveBranch() {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Utils.writeObject(file, this);
    }

    public void deleteBranch() {
        file.delete();
    }

    public static Branch fromFile(String name) {
        File file = Utils.join(Repository.REFS_DIR, name);
        if (!file.exists()) {
            MyUtils.exit("No such branch exists.");
        }
        return Utils.readObject(file, Branch.class);
    }

    /**
     * Change the id that the reference refers to.
     * 
     * @param name the name of the reference.
     * @param id   the id that the reference refers to.
     */
    public void referTo(String commitId) {
        this.commitId = commitId;
    }

    /**
     * dereference to the Commit object that the reference refers to.
     * 
     * @return
     */
    public Commit dereference() {
        return Commit.fromFile(commitId);
    }

    /**
     * Get the id that the reference refers to.
     * 
     * @return the id that the reference refers to.
     */
    public String getCommitId() {
        return commitId;
    }

    /**
     * Get the name of the reference.
     * 
     * @return the name of the reference.
     */
    public String getBranchName() {
        return name;
    }

    /**
     * Get the string representation of the reference.
     * Format:
     * <name> <id>
     * 
     * @return the string representation of the reference.
     */
    public String toString() {
        return String.format("%s <%s>", name, commitId);
    }

    @Override
    /**
     * Print the string representation of the reference.
     */
    public void dump() {
        System.out.println(toString());
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        Branch other = (Branch) o;
        return this.name.equals(other.name);
    }
}
