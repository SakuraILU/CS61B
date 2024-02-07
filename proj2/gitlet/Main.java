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
            case "init": {
                // handle the `init` command
                Repository.init();
                break;
            }
            case "add": {
                // handle the `add [file name]` command
                String fileName = args[1];
                Repository.add(fileName);
                break;
            }
            case "rm": {
                // handle the `rm [file name]` command
                String fileName = args[1];
                Repository.rm(fileName);
                break;
            }
            case "commit": {
                // handle the `commit [message]` command
                String message = args[1];
                Repository.commit(message);
                break;
            }
            case "log": {
                // handle the `log` command
                Repository.log();
                break;
            }
            case "global-log": {
                // handle the `global-log` command
                Repository.globalLog();
                break;
            }
            case "find": {
                // handle the `find [message]` command
                String message = args[1];
                Repository.find(message);
                break;
            }
            case "checkout": {
                if (args.length == 3) {
                    // handle the `checkout -- [finename]` command
                    String fileName = args[2];
                    Repository.checkoutFile(fileName);
                } else if (args.length == 4) {
                    // handle the `checkout [commit id] -- [file name]`
                    String commitId = args[1];
                    String fileName = args[3];
                    Repository.checkoutFile(commitId, fileName);
                } else if (args.length == 2) {
                    // handle the `checkout [branch name]` command
                    String branchName = args[1];
                    Repository.checkoutBranch(branchName);
                } else {
                    MyUtils.exit("Incorrect operands.");
                }
            }
        }
    }
}
