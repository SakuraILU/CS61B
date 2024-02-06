package gitlet;

import org.junit.Test;
import java.io.File;
import static org.junit.Assert.*;

public class RepositoryTest {
    @Test
    /** Test init and add */
    public void testInitAdd() {
        // create a file
        File file = new File("test.txt");
        try {
            file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Repository.init();
        Repository.add(file);

        // check stage
        Stage stage = Stage.fromFile();
        assertEquals("Should be the same", 1, stage.getAddedFiles().size());

        // delete the file
        file.delete();
    }
}
