package gitlet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Command {

    /**
     * 1. create a new gitlet version control system
     * a. .gitlet directory
     * b. HEAD file storing which branch we are on
     * c. Staging folder represents the Staging area
     * d. Commits folder storing commits
     * e. Blobs folder storing all the Blobs
     * f. Branches folder storing all the branch names
     * g. master file that store the id the first commit
     * 2. make an initial commit and create branch master
     */
    public static void init() {
        File gitlet = new File("./.gitlet");
        File head = new File("./.gitlet/HEAD");
        File staging = new File("./.gitlet/Staging");
        File commits = new File("./.gitlet/Commits");
        File deleted = new File("./.gitlet/Deleted");
        File blobs = new File("./.gitlet/Blobs");
        File branches = new File("./.gitlet/Branches");
        File master = new File("./.gitlet/Branches/master");

        if (!gitlet.exists()) {
            gitlet.mkdir();
            commits.mkdir();
            deleted.mkdir();
            blobs.mkdir();
            staging.mkdir();
            branches.mkdir();
            try {
                head.createNewFile();
                FileWriter headWriter = new FileWriter(head, false);
                headWriter.write("./.gitlet/Branches/master");
                headWriter.close();
                master.createNewFile();
            } catch (IOException e) {
                System.out.println("Did not succeed in creating HEAD/logs File");
                System.exit(0);
            }

        } else {
            System.out.println("A gitlet version-control "
                    + "system already exists in the current directory.");
            System.exit(0);
        }

        Commit.makeCommit("initial commit", new ArrayList<>());
    }

    /**
     * If the files is in the Deleted folder, simply restore it
     * Else try to add it as normal
     * 1. Store the files we added into it
     * 2. The name of the file will be the SHA_ID of the file
     * 3. The content of the file will be the serialization of content
     * 4. If the file had been marked to be removed, delete that mark
     * before adding the file as usual.
     *
     * @Params: List of files
     */
    public static void add(String filename) {
        File f = new File("./" + filename);
        if (!f.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }

        boolean isRestored = Utils.restoreFileFromDeletedFolder(filename);
        if (isRestored) {
            return;
        }
        File f1 = new File(filename);
        byte[] serial = Utils.readContents(f1);
        String shaID = Utils.sha1(serial);

        boolean isInCurrentCommit = Utils.detectFilesInCurrentCommit(filename);
        File f2 = new File("./.gitlet/Staging/" + shaID + filename);
        if (!f2.exists() && !isInCurrentCommit) {
            try {
                f2.createNewFile();
            } catch (IOException e) {
                System.out.println("failed in creating a new file in Staging folder");
                System.exit(0);
            }
            Utils.writeContents(f2, serial);
        }
    }

    /**
     * 1. Create a commit object, store its parent_id
     * 2. Store the list of blobs, which is just the SHA
     * 3. Serialize the commit object and store in Commits folder
     * 4. The name of the file will be the SHA_ID of the commit
     * 5. remember to change where the branch is pointing to
     */
    public static void commit(String message) {
        // if message is empty
        if (message.trim().length() <= 0) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        // if no file to be committed
        List<String> blobIDs = Utils.plainFilenamesIn("./.gitlet/Staging");
        List<String> removed = Utils.plainFilenamesIn("./.gitlet/Deleted");
        if (blobIDs.isEmpty() && removed.isEmpty()) {
            System.out.println("No changes added to the commit.");
        }
        // make commit
        Commit.makeCommit(message, blobIDs);
        // move all the blobs into the Blobs folder
        Utils.copyFilesFromFolderToFolder("./.gitlet/Staging", "./.gitlet/Blobs");
        // clear out the staging folder
        Utils.clearFolder("./.gitlet/Staging");
    }


    /**
     * This method should mark the file as Deleted and remove in the CWD (not from Commit yet)
     * and put them in the Deleted folder and we can restore the
     * file by doing checkout previous commit
     * remember it should never be removed from the Blobs folder
     * 1. deserialize current commit
     * 2. remove file from the Commits and remove from the CWD
     * 3. remove file from the Staging but not from CWD
     * 4. put in Deleted from exists in Commits
     * Problem: if we modify the file after we committed it and then trying to remove the file
     * do we still want to remove the file. Which version do we keep
     * Answer: we keep the version from the commit
     */
    public static void rm(String filename) {
        String branchPointer = Utils.readBranchPointer();
        File file = new File("./" + filename);

        boolean isInCommit = Utils.containsBlobInCommit(branchPointer, filename);
        boolean isRemovedFromStaging = Utils.removeFileFromStaging(filename);

        if (!isInCommit && !isRemovedFromStaging) {
            System.out.println("No reason to remove the file.");
        } else if (isInCommit) {
            file.delete();
        }
    }

    /**
     * 1. Deserialize the commit objects on the current branch
     * 2. Print out the content of the commits in specified form
     * 3. To get the current branch, we read the HEAD file
     * 4. look for the branch and then read the SHA_ID inside it
     */
    public static void log() {
        Commit.printBranch();
    }


    /* Print out everything inside the commits folder */
    public static void globalLog() {
        List<String> fileNames = Utils.plainFilenamesIn(".gitlet/Commits");
        for (String fileName : fileNames) {
            Commit.print(fileName);
        }
    }

    /**
     * 1. look through all the commits
     * 2. if there is a commit that has the same message print its id
     */
    public static void find(String message) {
        List<String> fileNames = Utils.plainFilenamesIn(".gitlet/Commits");
        boolean find = false;
        for (String fileName : fileNames) {
            Commit commit = (Commit) Utils.deserialize("./.gitlet/Commits/" + fileName);
            if (commit.logMessage.equals(message)) {
                System.out.println(fileName);
                find = true;
            }
        }
        if (!find) {
            System.out.println("Found no commit with that message.");
        }
    }

    /**
     * 1. Branches
     * 2. Staged Files
     * 3. Removed File
     * 4. Modified
     * 5. Untracked
     *
     * @Tracked: in Commits folder
     * @Untracked: not in the Commits folder but in the Current Working Directory and modified ?
     * @Staged: in Staging folder
     * @Staged_&_Tracked: Committed already and then made changes and added again
     * @Removed: not in the Commits and Staging folder but in the CWD
     * @Deleted: 1. in the Staging folder but deleted in the CWD
     * 2. in the Commits folder but deleted in the CWD
     * @Modified: Modification I made after I committed the file (current commit?)
     * @Untracked: Never being added
     */
    public static void status() {
        printBranches();
        printStagedFiles();
        printRemovedFiles();
        printModifiedFiles();
        printUntrackedFiles();
    }

    private static void printBranches() {
        List<String> branchlist = Utils.plainFilenamesIn("./.gitlet/Branches/");
        Collections.sort(branchlist);
        File head = new File("./.gitlet/HEAD");
        String currentBranchPath = new String(Utils.readContents(head));
        String currentBranch = currentBranchPath.split("/")[3];

        System.out.println("=== Branches ===");
        for (int i = 0; i < branchlist.size(); i++) {
            if (currentBranch.equals(branchlist.get(i))) {
                System.out.println("*" + branchlist.get(i));
            } else {
                System.out.println(branchlist.get(i));
            }
        }
    }

    public static void printStagedFiles() {
        List<String> stageList = Utils.plainFilenamesIn("./.gitlet/Staging/");
        Collections.sort(stageList);

        System.out.println("\n=== Staged Files ===");
        for (int i = 0; i < stageList.size(); i++) {
            System.out.println(stageList.get(i).substring(40));
        }
    }

    public static void printRemovedFiles() {
        System.out.println("\n=== Removed Files ===");
        List<String> removed = Utils.plainFilenamesIn("./.gitlet/Deleted");
        for (String s : removed) {
            System.out.println(s.substring(40));
        }
    }

    /**
     * Tracked but changed in CWD
     * Tracked but deleted in CWD
     * Staged but changed in CWD
     * Staged but deleted in CWD
     */
    public static void printModifiedFiles() {
        System.out.println("\n=== Modifications Not Staged For Commit ===");
//        List<String> deleted = Utils.findFilesInCommitsOrStagingButNotInCWD();
//        for (String s : deleted) {
//            System.out.println(s + " (deleted)");
//        }
//        List<String> modified = Utils.detectModifiedFilesInCommits();
//        for (String s : modified) {
//            System.out.println(s + " (modified)");
//        }
    }

    /**
     * @Untracked: Never being added
     */
    public static void printUntrackedFiles() {
        System.out.println("\n=== Untracked Files ===");
//        List<String> untracked = Utils.findFilesInCWDButNotInCommitsOrStaging();
//        for (String s : untracked) {
//            System.out.println(s);
//        }
    }

    /**
     * 1. Deserialize the head commit in the current branch
     * 2. Look in the blobs list for the name of that file
     * 3. Use the sha_id to get the file in the Blobs folder
     * 4. Restore: overwrite the same one in the working directory
     */
    public static void checkoutFilename(String filename) {
        String branchPointer = Utils.readBranchPointer();
        Commit commit = (Commit) Utils.deserialize("./.gitlet/Commits/" + branchPointer);

        for (String blobID : commit.blobsIDs) {
            if (filename.equals(blobID.substring(40))) {
                File from = new File("./.gitlet/Blobs/" + blobID);
                File to = new File("./" + filename);
                Utils.writeContents(to, Utils.readContents(from));
                return;
            }
        }
        System.out.println("File does not exist in that commit.");
    }

    /**
     * 1. Deserialize the commit with the given id
     * 2. Look for the file in the list of blobs
     * 3. Restore the file found
     */
    public static void checkoutIdFilename(String commitID, String filename) {
        // try extend commit id
        commitID = Utils.extendUID(commitID);
        if (commitID.equals("")) {
            System.out.println("No commit with that id exists.");
            return;
        }

        File file = new File("./.gitlet/Commits/" + commitID);
        if (!file.exists()) {
            System.out.println("No commit with that id exists.");
            return;
        }

        Commit commit = (Commit) Utils.deserialize("./.gitlet/Commits/" + commitID);
        for (String blobID : commit.blobsIDs) {
            if (filename.equals(blobID.substring(40))) {
                File from = new File("./.gitlet/Blobs/" + blobID);
                File to = new File("./" + filename);
                Utils.writeContents(to, Utils.readContents(from));
                return;
            }
        }
        System.out.println("File does not exist in that commit.");
    }

    /**
     * 1. Get the head commit in the given branch
     * 2. If a working file is different from the blobs in current branch, print and exit
     * 2. else Puts everything in working directory (overwrite)
     * 3. Any file that are in the current branch but are not
     * in the checkout branch should be deleted
     * 3. Set the HEAD to the given branch
     * 4. if branch is current branch, print message and abort
     */
    public static void checkoutBranch(String branch) {
        File head = new File("./.gitlet/HEAD");
        String headBranchPath = new String(Utils.readContents(head));
        String headBranch = headBranchPath.split("/")[3];

        File branchFile = new File("./.gitlet/Branches/" + branch);
        if (!branchFile.exists()) {
            System.out.println("No such branch exists.");
            return;
        }
        String branchPointer = new String(Utils.readContents(branchFile));
        if (!branch.equals(headBranch)) {
            List<String> untrackedFiles = Utils.getUntrackedFiles();
            //List<String> modified = Utils.detectModifiedFilesInStagingOrCommits();
            if (!untrackedFiles.isEmpty()) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it or add it first.");
                System.exit(0);
            } else {  // put everything in the CWD
                Utils.putAllFilesFromCommitInCWD(branchPointer);
                String newHead = "./.gitlet/Branches/" + branch;
                Utils.writeContents(head, newHead.getBytes());
            }
        } else {
            System.out.println("No need to checkout the current branch.");
        }

    }

    /* Add a new branch pointer pointing to where HEAD is pointing to */
    public static void branch(String branchName) {
        File newBranch = new File("./.gitlet/Branches/" + branchName);
        String currCommit = Utils.readBranchPointer();

        if (!newBranch.exists()) {
            Utils.writeContents(newBranch, currCommit.getBytes());
        } else {
            System.out.println("A branch with that name already exists.");
        }
    }

    /* Remove the branch pointer
     * and remove every commits on this branch */
    public static void rmBranch(String branchName) {
        File head = new File("./.gitlet/HEAD");
        String currBranch = new String(Utils.readContents(head));

        if (branchName != currBranch) {
            File branchToDelete = new File("./.gitlet/Branches/" + branchName);
            if (!branchToDelete.exists()) {
                branchToDelete.delete();
            } else {
                System.out.println("A branch with that name does not exist.");
                System.out.println(0);
            }
        } else {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
    }

    /**
     * 1. Look in the commit folder for the given ID
     * 2. If found then try to restore the files in the commit
     * 3. If there is untracked file, not present in the given commit, print and exit
     * 4. Else delete all the files and put everything back
     * 5. Reset the current branch pointer
     */
    public static void reset(String commitID) {
        File commitFile = new File("./.gitlet/Commits/" + commitID);

        if (commitFile.exists()) {
            Commit commit = (Commit) Utils.deserialize("./.gitlet/Commits/" + commitID);
            List<String> untracked = Utils.getUntrackedFiles();


            if (untracked.isEmpty()) {
                Utils.clearFolder(".");
                for (String blobID : commit.blobsIDs) {
                    File from = new File("./.gitlet/Blobs/" + blobID);
                    File to = new File("./" + blobID.substring(40));
                    Utils.writeContents(to, Utils.readContents(from));
                }
                Utils.clearFolder("./.gitlet/Staging/");
                File head = new File("./.gitlet/HEAD");
                String currentBranchPath = new String(Utils.readContents(head));
                File currentBranch = new File(currentBranchPath);
                Utils.writeContents(currentBranch, commitID.getBytes());
            } else {
                System.out.println("There is an untracked file in the way; "
                        + "delete it or add it first.");
                System.exit(0);
            }
        } else {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
    }

    /**
     * Failure:
     * 1. Untracked Files
     * 2. stagedFiles and removal
     * split & current  |   split & given  |  current & given  |  Action
     * ------------------------------------------------------
     * same                         |  print
     * same                                              |  set current and print
     *
     * @param branchName
     */
    public static void merge(String branchName) {
        String headBranch = mergeFailure(branchName);
        if (headBranch == null) {
            return;
        }
        File file = new File("./.gitlet/Branches/" + branchName);
        String currentBranch = Utils.readBranchPointer();
        String splitPoint = Utils.findSplitPoint(branchName);
        String givenBranch = new String(Utils.readContents(file));
        if (splitPoint.equals(givenBranch)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        } else if (splitPoint.equals(currentBranch)) {
            // set the current branch to the same commit as the given branch
            Utils.setCurrentBranchAs(branchName);
            System.out.println("Current branch fast-forwarded.");
            return;
        }
        Commit commit = (Commit) Utils.deserialize("./.gitlet/Commits/" + currentBranch);
        boolean hasConflict = false;
        boolean finalHasConflict = false;
        for (String blobID : commit.blobsIDs) {
            int instance = 0;
            boolean inSplit = Utils.isPresentIn(blobID, splitPoint);
            boolean inGiven = Utils.isPresentIn(blobID, givenBranch);
            if (inSplit && inGiven) {
                if (Utils.isModifiedIn(blobID, splitPoint)) {
                    if (Utils.isModifiedIn(blobID, givenBranch)) {
                        // Need to check the modification between given and split.
                        if (Utils.fileModifiedInGiven(blobID, givenBranch, splitPoint)) {
                            instance = 7;
                        } else {
                            instance = 2;
                        }
                    } else {
                        instance = 3;
                    }
                } else {
                    if (Utils.isModifiedIn(blobID, givenBranch)) {
                        instance = 1;
                    } else {
                        instance = 3; // All three is present and unmodified.
                    }
                }
            } else if (!inSplit && inGiven) {
                if (Utils.isModifiedIn(blobID, givenBranch)) {
                    instance = 7;
                } else {
                    instance = 3;
                }
            } else if (inSplit && !inGiven) {
                if (Utils.isModifiedIn(blobID, splitPoint)) {
                    instance = 7; //here entered
                } else {
                    instance = 5;
                }
            } else if (!inSplit && !inGiven) {
                instance = 3;
            }
            if (instance == 7) {
                hasConflict = true;
                finalHasConflict = true;
            }
            if (!hasConflict) {
                Utils.makeAction(instance, blobID, givenBranch);
            } else {
                Utils.showConflict(blobID, givenBranch);
                hasConflict = false;
            }
        }
        finalHasConflict = mergeSecondPart(currentBranch, splitPoint,
                givenBranch, hasConflict, finalHasConflict);
        if (finalHasConflict) {
            System.out.println("Encountered a merge conflict.");
        } else {
            // no conflicts. make commit automatically.
            Command.commit("Merged " + headBranch + " with " + branchName + ".");
        }
    }

    private static String mergeFailure(String branchName) {
        List<String> untracked = Utils.getUntrackedFiles();
        if (!untracked.isEmpty()) {
            System.out.println("There is an untracked file in the way; delete "
                    + "it or add it first.");
            System.exit(0);
        }
        List<String> stagedFiles = Utils.plainFilenamesIn("./.gitlet/Staging");
        List<String> removedFiles = Utils.plainFilenamesIn("./.gitlet/Deleted");
        if (!stagedFiles.isEmpty() || !removedFiles.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            return null;
        }
        File givenBranchFile = new File("./.gitlet/Branches/" + branchName);
        if (!givenBranchFile.exists()) {
            System.out.println("A branch with that name does not exist.");
            return null;
        }
        File head = new File("./.gitlet/HEAD");
        String headBranchPath = new String(Utils.readContents(head));
        String headBranch = headBranchPath.split("/")[3];
        if (headBranch.equals(branchName)) {
            System.out.println("Cannot merge a branch with itself.");
            return null;
        }
        return headBranch;
    }

    private static boolean mergeSecondPart(String currentBranch,
                                           String splitPoint, String givenBranch,
                                           boolean hasConflict, boolean finalHasConflict) {
        Commit commit2 = (Commit) Utils.deserialize("./.gitlet/Commits/" + givenBranch);
        for (String blobID : commit2.blobsIDs) {
            int instance = 0;
            boolean inSplit = Utils.isPresentIn(blobID, splitPoint);
            boolean inCurr = Utils.isPresentIn(blobID, currentBranch);
            if (!inCurr) {
                if (inSplit) {
                    if (Utils.isModifiedIn(blobID, splitPoint)) {
                        instance = 7;
                    } else {
                        instance = 6;
                    }
                } else {
                    instance = 4;
                }
            }
            if (instance == 7) {
                hasConflict = true;
                finalHasConflict = true;
            }
            if (!hasConflict) {
                Utils.makeAction(instance, blobID, givenBranch);
            }
        }
        return finalHasConflict;
    }
}
