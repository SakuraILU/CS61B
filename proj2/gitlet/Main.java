package gitlet;

import java.io.IOException;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 * 
 * @author Keyu Liu
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {
        // what if args is empty?
        if (args.length == 0) {
            MyUtils.exit("Please Enter a command");
        }

        try {
            excuteCmd(args);
        } catch (IOException e) {
            MyUtils.exit(e.getMessage());
        }
    }

    private static void excuteCmd(String[] args) throws IOException {
        String command = args[0];
        // what if not in an initialized gitlet directory?
        if (!command.equals("init") && !Repository.isInitialized()) {
            MyUtils.exit("Not in an initialized Gitlet directory.");
        }

        switch (command) {
            case "init": {
                // handle the `init` command
                validateArgs(args, 1);

                Repository.init();
                break;
            }
            case "add": {
                // handle the `add [file name]` command
                validateArgs(args, 2);

                String fileName = args[1];
                Repository.add(fileName);
                break;
            }
            case "rm": {
                // handle the `rm [file name]` command
                validateArgs(args, 2);

                String fileName = args[1];
                Repository.rm(fileName);
                break;
            }
            case "commit": {
                // handle the `commit [message]` command
                validateArgs(args, 2);

                String message = args[1];
                Repository.commit(message);
                break;
            }
            case "log": {
                // handle the `log` command
                validateArgs(args, 1);

                Repository.log();
                break;
            }
            case "global-log": {
                // handle the `global-log` command
                validateArgs(args, 1);

                Repository.globalLog();
                break;
            }
            case "find": {
                // handle the `find [message]` command
                validateArgs(args, 2);

                String message = args[1];
                Repository.find(message);
                break;
            }
            case "status": {
                // handle the `status` command
                validateArgs(args, 1);

                Repository.status();
                break;
            }
            case "checkout": {
                if (args.length == 3 && args[1].equals("--")) {
                    // handle the `checkout -- [finename]` command
                    String fileName = args[2];
                    Repository.checkoutFile(fileName);
                } else if (args.length == 4 && args[2].equals("--")) {
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
                break;
            }
            case "branch": {
                // handle the `branch [branch name]` command
                validateArgs(args, 2);

                String branchName = args[1];
                Repository.branch(branchName);
                break;
            }
            case "rm-branch": {
                // handle the `rm-branch [branch name]` command
                validateArgs(args, 2);

                String branchName = args[1];
                Repository.rmBranch(branchName);
                break;
            }
            case "reset": {
                // handle the `reset [commit id]` command
                validateArgs(args, 2);

                String commitId = args[1];
                Repository.reset(commitId);
                break;
            }
            case "merge": {
                // handle the `merge [branch name]` command
                validateArgs(args, 2);

                String branchName = args[1];
                Repository.merge(branchName);
                break;
            }
            default: {
                MyUtils.exit("No command with that name exists.");
                break;
            }
        }
    }

    private static void validateArgs(String[] args, int length) {
        if (args.length != length) {
            MyUtils.exit("Incorrect operands.");
        }
    }
}
