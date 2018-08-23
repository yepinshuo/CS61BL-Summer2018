package gitlet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.List;


/* Assorted utilities.
   @author P. N. Hilfinger */
class Utils {

    /* SHA-1 HASH VALUES. */

    /* Returns the SHA-1 hash of the concatenation of VALS, which may be any
       mixture of byte arrays and Strings. */
    static String sha1(Object... vals) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            for (Object val : vals) {
                if (val instanceof byte[]) {
                    md.update((byte[]) val);
                } else if (val instanceof String) {
                    md.update(((String) val).getBytes(StandardCharsets.UTF_8));
                } else {
                    throw new IllegalArgumentException("improper type to sha1");
                }
            }
            Formatter result = new Formatter();
            for (byte b : md.digest()) {
                result.format("%02x", b);
            }
            return result.toString();
        } catch (NoSuchAlgorithmException excp) {
            throw new IllegalArgumentException("System does not support SHA-1");
        }
    }

    /* Returns the SHA-1 hash of the concatenation of the strings in VALS. */
    static String sha1(List<Object> vals) {
        return sha1(vals.toArray(new Object[vals.size()]));
    }

    /* FILE DELETION */

    /* Deletes FILE if it exists and is not a directory.  Returns true if FILE
       was deleted, and false otherwise.  Refuses to delete FILE and throws
       IllegalArgumentException unless the directory designated by FILE also
       contains a directory named .gitlet. */
    static boolean restrictedDelete(File file) {
        if (!(new File(file.getParentFile(), ".gitlet")).isDirectory()) {
            throw new IllegalArgumentException("not .gitlet working directory");
        }
        if (!file.isDirectory()) {
            return file.delete();
        } else {
            return false;
        }
    }

    /* Deletes the file named FILE if it exists and is not a directory. Returns
       true if FILE was deleted, and false otherwise. Refuses to delete FILE and
       throws IllegalArgumentException unless the directory designated by FILE
       also contains a directory named .gitlet. */
    static boolean restrictedDelete(String file) {
        return restrictedDelete(new File(file));
    }

    /* READING AND WRITING FILE CONTENTS */

    /* Return the entire contents of FILE as a byte array. FILE must be a normal
       file. Throws IllegalArgumentException in case of problems. */
    static byte[] readContents(File file) {
        if (!file.isFile()) {
            throw new IllegalArgumentException("must be a normal file");
        }
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /* Write the entire contents of BYTES to FILE, creating or overwriting it as
       needed. Throws IllegalArgumentException in case of problems. */
    static void writeContents(File file, byte[] bytes) {
        try {
            if (file.isDirectory()) {
                throw
                    new IllegalArgumentException("cannot overwrite directory");
            }
            Files.write(file.toPath(), bytes);
        } catch (IOException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /* Serialize a commit object */
    public static byte[] serialize(Object obj) {
        byte[] result = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            result = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        return result;
    }

    public static Object deserialize(String filePath) {
        Object result = null;
        try {
            FileInputStream fis = new FileInputStream(filePath);
            ObjectInputStream ois = new ObjectInputStream(fis);
            result = ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    /* OTHER FILE UTILITIES */

    /* Return the concatentation of FIRST and OTHERS into a File designator,
       analogous to the {@link java.nio.file.Paths.#get(String, String[])}
       method. */
    static File join(String first, String... others) {
        return Paths.get(first, others).toFile();
    }

    /* Return the concatentation of FIRST and OTHERS into a File designator,
       analogous to the {@link java.nio.file.Paths.#get(String, String[])}
       method. */
    static File join(File first, String... others) {
        return Paths.get(first.getPath(), others).toFile();
    }

    /* DIRECTORIES */

    /* Filter out all but plain files. */
    private static final FilenameFilter PLAIN_FILES =
        new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return new File(dir, name).isFile();
            }
        };

    /* Returns a list of the names of all plain files in the directory DIR, in
       lexicographic order as Java Strings. Returns null if DIR does not denote
       a directory. */
    static List<String> plainFilenamesIn(File dir) {
        String[] files = dir.list(PLAIN_FILES);
        if (files == null) {
            return null;
        } else {
            Arrays.sort(files);
            return Arrays.asList(files);
        }
    }

    /* Returns a list of the names of all plain files in the directory DIR, in
       lexicographic order as Java Strings. Returns null if DIR does not denote
       a directory. */
    static List<String> plainFilenamesIn(String dir) {
        return plainFilenamesIn(new File(dir));
    }



    static void copyFilesFromFolderToFolder(String srcDir, String desDir)  {
        File source = new File(srcDir);
        List<String> toCopy = plainFilenamesIn(source);
        for (String fileName : toCopy) {
            File srcFile = new File(srcDir + "/" + fileName);
            File desFile = new File(desDir + "/" + fileName);
            writeContents(desFile, readContents(srcFile));
        }
    }



    static String readBranchPointer() {
        File head = new File("./.gitlet/HEAD");
        String branch = new String(Utils.readContents(head));
        File branchFile = new File(branch);
        return new String(Utils.readContents(branchFile));
    }

    /* Clear all the files inside the folder */
    static void clearFolder(String path) {
        File folder = new File(path);
        for (File file : folder.listFiles()) {
            if (!file.isDirectory()) {
                file.delete();
            }
        }
    }

    /**
     * This method checks if the Blob is in the Commit and mark as Deleted
     * 1. Deserialize the commit object in the folder
     * 2. delete the blob in the commit object
     * 3. Serialize the commit object and put back in folder
     * @param commitID
     * @param filename
     */
    static boolean containsBlobInCommit(String commitID, String filename) {
        Commit commit = (Commit) deserialize("./.gitlet/Commits/" + commitID);
        List<String> list = new LinkedList<>(commit.blobsIDs);
        for (String blobID : list) {
            if (filename.equals(blobID.substring(40))) {
                File toSave = new File("./.gitlet/Blobs/" + blobID);
                File deleted = new File("./.gitlet/Deleted/" + blobID);
                Utils.writeContents(deleted, Utils.readContents(toSave));
                return true;
            }
        }
        return false;
    }

    static boolean removeFileFromStaging(String filename) {
        List<String> filenames = plainFilenamesIn("./.gitlet/Staging/");
        for (String file : filenames) {
            if (filename.equals(file.substring(40))) {
                File toDelete = new File("./.gitlet/Staging/" + file);
                return toDelete.delete();
            }
        }
        return false;
    }

    /**
     * Untracked files are files that are not in the current commit
     * or has different content than the version in the current commit
     * @return
     */
    static List<String> getUntrackedFiles() {
        String branchPointer = readBranchPointer();
        List<String> untrackedFiles = new ArrayList<>();
        List<String> cwdFiles = plainFilenamesIn("./");
        List<String> blobFiles = plainFilenamesIn("./.gitlet/Blobs");
        List<String> stagingFiles = plainFilenamesIn("./.gitlet/Staging");
        Commit currentCommit = (Commit) deserialize("./.gitlet/Commits/" + branchPointer);
        List<String> trackedFiles = currentCommit.blobsIDs;

        for (String cwdFile : cwdFiles) {
            boolean untracked = true;
            for (String tracked : trackedFiles) {
                // check if in the current commit
                if (cwdFile.equals(tracked.substring(40))) {
                    // check if the content is the same
                    if (blobFiles.contains(tracked)) {
                        untracked = false;
                    }
                }
            }

            for (String stageFile : stagingFiles) {
                // check if in the staging area
                if (cwdFile.equals(stageFile.substring(40))) {
                    untracked = false;
                }
            }
            if (untracked) {
                untrackedFiles.add(cwdFile);
            }
        }

        return untrackedFiles;
    }


    /** Used for detect newly created files in CWD */
    static List<String> findFilesInCWDButNotInCommitsOrStaging() {
        List<String> cwdFiles = plainFilenamesIn("./");
        List<String> blobFiles = plainFilenamesIn("./.gitlet/Blobs");
        List<String> stagingFiles = plainFilenamesIn("./.gitlet/Staging");
        List<String> result = new ArrayList<>();
        for (String cwdFile : cwdFiles) {
            boolean inDir2 = false;
            for (String d2F : blobFiles) {
                if (cwdFile.equals(d2F.substring(40))) {
                    inDir2 = true;
                }
            }
            for (String stageFile : stagingFiles) {
                if (cwdFile.equals(stageFile.substring(40))) {
                    inDir2 = true;
                }
            }
            if (!inDir2) {
                result.add(cwdFile);
            }
        }
        return result;
    }

    static List<String> findFilesInCommitsOrStagingButNotInCWD() {
        List<String> cwdFiles = plainFilenamesIn("./");
        List<String> blobFiles = plainFilenamesIn("./.gitlet/Blobs");
        List<String> stagingFiles = plainFilenamesIn("./.gitlet/Staging");
        List<String> result = new ArrayList<>();
        for (String blob : blobFiles) {
            boolean in = false;
            for (String cwdFile : cwdFiles) {
                if (blob.substring(40).equals(cwdFile)) {
                    in = true;
                }
            }
            if (!in) {
                result.add(blob.substring(40));
            }
        }

        for (String stageFile : stagingFiles) {
            boolean in = false;
            for (String cwdFile : cwdFiles) {
                if (stageFile.substring(40).equals(cwdFile)) {
                    in = true;
                }
            }
            if (!in) {
                result.add(stageFile.substring(40));
            }
        }
        return result;
    }

    static List<String> findUntrackedFilesIn(String commitID) {
        List<String> untrackedFiles = new ArrayList<>();
        List<String> cwdFiles = plainFilenamesIn("./");
        List<String> blobFiles = plainFilenamesIn("./.gitlet/Blobs");
        Commit currentCommit = (Commit) deserialize("./.gitlet/Commits/" + commitID);
        List<String> trackedFiles = currentCommit.blobsIDs;
        for (String cwdFile : cwdFiles) {
            boolean untracked = true;
            for (String tracked : trackedFiles) {
                // check if in the current commit
                if (cwdFile.equals(tracked.substring(40))) {
                    // check if the content is the same
                    if (blobFiles.contains(tracked)) {
                        untracked = false;
                    }
                }
            }
            if (untracked) {
                untrackedFiles.add(cwdFile);
            }
        }

        return untrackedFiles;
    }

    /**
     * folder1 consists of the files with fileName (working directory)
     *         e.g. hello.txt
     * folder2 consists of the files with name SHA + fileName (Staging / Blobs)
     *         e.g. f572d396fae9206628714fb2ce00f72e94f2258fhello.txt
     * To detect if a file is modified,
     *         1. we look for the same fileName in folder2 and
     *         2. compare their content
     */
    static List<String> detectModifiedFilesInStagingOrCommits() {
        List<String> cwdFiles = plainFilenamesIn("./");
        List<String> blobFiles = plainFilenamesIn("./.gitlet/Blobs");
        List<String> stagingFiles = plainFilenamesIn("./.gitlet/Staging");
        List<String> result = new ArrayList<>();
        for (String file1 : cwdFiles) {
            for (String file2 : blobFiles) {
                if (file1.equals(file2.substring(40))) {
                    File f1 = new File("./" + file1);
                    File f2 = new File("./.gitlet/Blobs/" + file2);
                    byte[] f1Content = readContents(f1);
                    byte[] f2Content = readContents(f2);
                    if (!Arrays.equals(f1Content, f2Content)) {
                        result.add(file1);
                    }
                }
            }

            for (String file2 : stagingFiles) {
                if (file1.equals(file2.substring(40))) {
                    File f1 = new File("./" + file1);
                    File f2 = new File("./.gitlet/Staging/" + file2);
                    byte[] f1Content = readContents(f1);
                    byte[] f2Content = readContents(f2);
                    if (!Arrays.equals(f1Content, f2Content) && !result.contains(file1)) {
                        result.add(file1);
                    }
                }
            }
        }
        return result;
    }

    static List<String> detectModifiedFilesInCommits() {
        List<String> cwdFiles = plainFilenamesIn("./");
        List<String> blobFiles = plainFilenamesIn("./.gitlet/Blobs");
        List<String> result = new ArrayList<>();
        for (String file1 : cwdFiles) {
            for (String file2 : blobFiles) {
                if (file1.equals(file2.substring(40))) {
                    File f1 = new File("./" + file1);
                    File f2 = new File("./.gitlet/Blobs/" + file2);
                    byte[] f1Content = readContents(f1);
                    byte[] f2Content = readContents(f2);
                    if (!Arrays.equals(f1Content, f2Content)) {
                        result.add(file1);
                    }
                }
            }
        }
        return result;
    }

    static boolean detectFilesInCurrentCommit(String filename) {
        String currentCommitID = readBranchPointer();
        Commit currentCommit = (Commit) deserialize("./.gitlet/Commits/" + currentCommitID);
        if (currentCommit == null) {
            return false;
        }
        List<String> blobFiles = currentCommit.blobsIDs;
        for (String blob : blobFiles) {
            if (filename.equals(blob.substring(40))) {
                byte[] cwdFile = readContents(new File("./" + filename));
                byte[] blobFile = readContents(new File("./.gitlet/Blobs/" + blob));
                if (Arrays.equals(cwdFile, blobFile)) {
                    return true;
                }
            }
        }
        return false;
    }

    static List<String> detectModifiedFilesInCurrentCommit() {
        List<String> cwdFiles = plainFilenamesIn("./");
        String currentCommitID = readBranchPointer();
        Commit currentCommit = (Commit) deserialize("./.gitlet/Commits/" + currentCommitID);
        List<String> blobFiles = currentCommit.blobsIDs;
        List<String> result = new ArrayList<>();
        for (String file1 : cwdFiles) {
            for (String file2 : blobFiles) {
                if (file1.equals(file2.substring(40))) {
                    File f1 = new File("./" + file1);
                    File f2 = new File("./.gitlet/Blobs/" + file2);
                    byte[] f1Content = readContents(f1);
                    byte[] f2Content = readContents(f2);
                    if (!Arrays.equals(f1Content, f2Content)) {
                        result.add(file1);
                    }
                }
            }
        }
        return result;
    }

    static String extendUID(String uid) {
        List<String> commits = plainFilenamesIn("./.gitlet/Commits/");
        int n = uid.length();
        for (String commit : commits) {
            if (commit.substring(0, n).equals(uid)) {
                return commit;
            }
        }
        return "";
    }

    static void putAllFilesFromCommitInCWD(String commitID) {
        clearFolder("./");
        Commit commit = (Commit) deserialize("./.gitlet/Commits/" + commitID);
        for (String blob : commit.blobsIDs) {
            File from = new File("./.gitlet/Blobs/" + blob);
            File to = new File("./" + blob.substring(40));
            writeContents(to, readContents(from));
        }
    }

    static boolean restoreFileFromDeletedFolder(String filename) {
        List<String> files = plainFilenamesIn("./.gitlet/Deleted/");
        for (String file : files) {
            if (filename.equals(file.substring(40))) {
                File toRestore = new File("./" + file);
                File fromDelete = new File("./.gitlet/Deleted/" + file);
                writeContents(toRestore, readContents(fromDelete));
                fromDelete.delete();
                return true;
            }
        }
        return false;
    }

    static String getCurrentBranchName() {
        File head = new File("./.gitlet/HEAD");
        String branchPath = new String(readContents(head));
        return branchPath.split("/")[3];
    }

    static List<String> getAllCommitIDIn(String branch) {
        File file = new File("./.gitlet/Branches/" + branch);
        List<String> commitIDs = new ArrayList<>();
        String branchPointer = new String(readContents(file));
        do {
            Commit c = (Commit) Utils.deserialize("./.gitlet/Commits/" + branchPointer);
            commitIDs.add(branchPointer);
            branchPointer = c.parentID;
        } while (!branchPointer.equals(""));
        return commitIDs;
    }

    /**
     * Take a branch name and compare with the current branch
     * to find the split point
     * @param givenBranch
     * @return
     */
    static String findSplitPoint(String givenBranch) {
        String currentBranch = getCurrentBranchName();
        List<String> commitsInCurrentBranch = getAllCommitIDIn(currentBranch);
        List<String> commitsInGivenBranch = getAllCommitIDIn(givenBranch);
        String splitPoint = "";
        for (String commitCB : commitsInCurrentBranch) {
            for (String commitGB : commitsInGivenBranch) {
                if (commitCB.equals(commitGB)) {
                    return commitCB;
                }
            }
        }
        return splitPoint;
    }

    /**
     * Read from the given branch and set the current branch
     * to the same commit as the given branch
     * @param givenBranch
     */
    static void setCurrentBranchAs(String givenBranch) {
        File head = new File("./.gitlet/HEAD");
        String cbp = new String(readContents(head));
        File currentBranch = new File(cbp);
        File givenBranchFile = new File("./.gitlet/Branches/" + givenBranch);
        writeContents(currentBranch, readContents(givenBranchFile));
    }

    /**
     * Check if the file is in the given commit
     * In this case, we only need to check the filename and ignore the
     * shaID
     * @param blobID
     * @param commitID
     * @return
     */
    static boolean isPresentIn(String blobID, String commitID) {
        Commit commit = (Commit) deserialize("./.gitlet/Commits/" + commitID);
        for (String blob : commit.blobsIDs) {
            if (blobID.substring(40).equals(blob.substring(40))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the file is modified in the given commit
     * In other words, it is checking if the blobID exists
     * @param commitID
     * @return
     */
    static boolean isModifiedIn(String blobID, String commitID) {
        Commit commit = (Commit) deserialize("./.gitlet/Commits/" + commitID);
        if (commit.blobsIDs.contains(blobID)) {
            return false;
        }
        return true;
    }

    static boolean fileModifiedInGiven(String blobID, String givenBranchID, String splitID) {
        Commit commit = (Commit) deserialize("./.gitlet/Commits/" + givenBranchID);
        for (String blob : commit.blobsIDs) {
            if (blob.substring(40).equals(blobID.substring(40))) {
                return isModifiedIn(blob, splitID);
            }
        }
        return false;
    }

    static void makeAction(int instance, String blobID, String commitID) {
        if (instance == 1) {
            // add file from commitID into staging
            Commit commit = (Commit) deserialize("./.gitlet/Commits/" + commitID);
            for (String blob : commit.blobsIDs) {
                if (blobID.substring(40).equals(blob.substring(40))) {
                    File staging = new File("./.gitlet/Staging/" + blob);
                    File from  = new File("./.gitlet/Blobs/" + blob);
                    writeContents(staging, readContents(from));
                }
            }
        } else if (instance == 4) {
            // checkout
            File from = new File("./.gitlet/Blobs/" + blobID);
            File to = new File("./" + blobID.substring(40));
            Utils.writeContents(to, Utils.readContents(from));
            // stage
            File staging = new File("./.gitlet/Staging/" + blobID);
            writeContents(staging, readContents(from));
        } else if (instance == 5) {
            Command.rm(blobID.substring(40));
        }
    }

    static void showConflict(String blobID, String commitID) {
        String otherBlob = "";
        // case 1
        Commit commit = (Commit) deserialize("./.gitlet/Commits/" + commitID);
        for (String blob : commit.blobsIDs) {
            if (blobID.substring(40).equals(blob.substring(40))) {
                otherBlob = blob;
            }
        }

        String currentContent = "";
        String givenContent = "";
        File currentFile = new File("./.gitlet/Blobs/" + blobID);
        currentContent = new String(readContents(currentFile));
        if (!otherBlob.equals("")) {
            File givenFile = new File("./.gitlet/Blobs/" + otherBlob);
            givenContent = new String(readContents(givenFile));
        }

        String s = "<<<<<<< HEAD\n" + currentContent
                + "=======\n" + givenContent + ">>>>>>>\n";
        File cwdFile = new File("./" + blobID.substring(40));
        writeContents(cwdFile, s.getBytes());
    }

}
