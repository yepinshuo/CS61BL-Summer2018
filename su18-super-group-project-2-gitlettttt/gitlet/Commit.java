package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Commit implements Serializable {
    String parentID;
    Date timestamp;
    String logMessage;
    List<String> blobsIDs;
    private static final long serialVersionUID = -5291151260727154502L;

    public Commit(String message, String parent, List<String> blobs) {
        timestamp = new Date();
        logMessage = message;
        parentID = parent;
        blobsIDs = blobs;
    }

    /** 1. read from branch pointer as the parent of current commit
     *  2. create the commit object remember to save the blobs from the last commit
     *     then read from the Deleted folder, if the file is marked as Deleted, remove from blobs
     *  3. Serialize the Commit object
     *  4. generate the sha1 id based on the byte array from serialization
     *  5. create a commit file in commits folder
     *  6. move the branch pointer */
    public static void makeCommit(String message, List<String> blobs) {
        String lastCommitID = Utils.readBranchPointer();

        if (lastCommitID.length() == 40) {
            Commit lasCommit = (Commit) Utils.deserialize("./.gitlet/Commits/" + lastCommitID);
            List<String> list = new ArrayList<>();
            //list.addAll(lasCommit.blobs_ids);
            for (String prev : lasCommit.blobsIDs) {
                boolean inBlob = false;
                for (String now : blobs) {
                    if (prev.substring(40).equals(now.substring(40))) {
                        inBlob = true;
                    }
                }
                if (!inBlob) {
                    list.add(prev);
                }
            }
            list.addAll(blobs);
            blobs = list;
            List<String> toRemove = Utils.plainFilenamesIn("./.gitlet/Deleted/");
            for (String remove : toRemove) {
                blobs.remove(remove);
            }
            Utils.clearFolder("./.gitlet/Deleted");
        }

        Commit currentCommit = new Commit(message, lastCommitID, blobs);
        byte[] commitByteArray = Utils.serialize(currentCommit);

        String id = Utils.sha1(commitByteArray);

        /* create a new commit file and write serialized commit into it */
        File commitFile = new File("./.gitlet/Commits/" + id);
        Utils.writeContents(commitFile, commitByteArray);

        /* overwrite the pointer in the branch file */
        moveBranchPointer(id);
    }

    public static void print(String commitID) {
        Commit c = (Commit) Utils.deserialize("./.gitlet/Commits/" + commitID);
        System.out.println("===");
        System.out.println("Commit " + commitID);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(dateFormat.format(c.timestamp));
        System.out.println(c.logMessage);
        System.out.println();
    }

    public static void printBranch() {
        /* testing */
        String branchPointer = Utils.readBranchPointer();
        do {
            Commit c = (Commit) Utils.deserialize("./.gitlet/Commits/" + branchPointer);
            System.out.println("===");
            System.out.println("Commit " + branchPointer);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            System.out.println(dateFormat.format(c.timestamp));
            System.out.println(c.logMessage);
            System.out.println();
            branchPointer = c.parentID;
        } while (!branchPointer.equals(""));
    }




    /* 1. read from HEAD which branch we are currently on
    *  2. write the id of the new commit into the branch */
    private static void moveBranchPointer(String shaID) {
        File head = new File("./.gitlet/HEAD");
        String branch = new String(Utils.readContents(head));
        File branchFile = new File(branch);
        byte[] shaIDByteArray = shaID.getBytes();
        Utils.writeContents(branchFile, shaIDByteArray);
    }
}
