package gitlet;

/* Driver class for Gitlet, the tiny stupid version-control system.
   @author
*/
public class Main {
    /* Usage: java gitlet.Main ARGS, where ARGS contains
       <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        /** INIT */
        if (args[0].equals("init")) {
            Command.init();
        }

        /** ADD */
        if (args[0].equals("add")) {
            if (args.length == 2) {
                Command.add(args[1]);
            } else {
                System.out.println("File does not exist.");
            }
        }

        /** COMMIT */
        if (args[0].equals("commit")) {
            if (args.length == 2) {
                Command.commit(args[1]);
            } else {
                System.out.println("Please enter a commit message.");
            }
        }

        /** REMOVE */
        if (args[0].equals("rm")) {
            if (args.length == 2) {
                Command.rm(args[1]);
            } else {
                System.out.println("Please specify the files you want to remove");
            }
        }

        /** LOG */
        if (args[0].equals("log")) {
            Command.log();
        }

        /** GLOBAL-LOG */
        if (args[0].equals("global-log")) {
            Command.globalLog();
        }

        /** FIND */
        if (args[0].equals("find")) {
            if (args.length == 2) {
                Command.find(args[1]);
            } else {
                System.out.println("Found no commit with that message.");
            }
        }

        /** STATUS */
        if (args[0].equals("status")) {
            Command.status();
        }

        /** CHECKOUT */
        if (args[0].equals("checkout")) {
            if (args.length == 2) {
                Command.checkoutBranch(args[1]);
            } else if (args.length == 3
                    && args[1].equals("--")) {
                Command.checkoutFilename(args[2]);
            } else if (args.length == 4
                    && args[2].equals("--")) {
                Command.checkoutIdFilename(args[1], args[3]);
            } else {
                System.out.println("Incorrect Operands.");
            }
        }

        calledFromMain(args);
    }

    static void calledFromMain(String... args) {
        /** BRANCH */
        if (args[0].equals("branch")) {
            if (args.length == 2) {
                Command.branch(args[1]);
            }
        }


        /** RM-BRANCH */
        if (args[0].equals("rm-branch")) {
            if (args.length == 2) {
                Command.rmBranch(args[1]);
            }
        }

        /** RESET */
        if (args[0].equals("reset")) {
            if (args.length == 2) {
                Command.reset(args[1]);
            }
        }

        /** MERGE */
        if (args[0].equals("merge")) {
            if (args.length == 2) {
                Command.merge(args[1]);
            }
        }
    }

}
