package gitlet;

import java.io.File;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 * 
 * @author TODO
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if (args.length == 0) {
            MyUtils.exit("Please Enter a command");
        }

        String firstArg = args[0];
        switch (firstArg) {
            case "init":
                // handle the `init` command
                Repository.init();
                break;
            case "add":
                // handle the `add [filename]` command
                String fileName = args[1];
                Repository.add(new File(fileName));
                break;
            // TODO: FILL THE REST IN
            case "rm":
                // handle the `rm [filename]` command
                fileName = args[1];
                Repository.rm(new File(fileName));
                break;
            case "commit":
                // handle the `commit [message]` command
                String message = args[1];
                Repository.commit(message);
                break;
            case "log":
                // handle the `log` command
                Repository.log();
                break;
        }
    }
}
