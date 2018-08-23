package gitlet;

import org.junit.Test;


public class CommandTest {

    @Test
    public void init() {
        Command.init();
    }

    @Test
    public void add() {
        Command.add("world.txt");
    }

    @Test
    public void commit() {
        Command.commit("Remove hello");
    }

    @Test
    public void rm() {
        Command.rm("hello.txt");
    }

    @Test
    public void log() {
        Command.log();
    }

    @Test
    public void globalLog() {
        Command.globalLog();
    }

    @Test
    public void find() {
        Command.find("hello version 1");
    }

    @Test
    public void status() {
        Command.status();
    }


    @Test
    public void checkoutFile() {
        Command.checkoutFilename("hello.txt");
    }

    @Test
    public void checkoutID() {
    }

    @Test
    public void checkoutBranch() {
    }

    @Test
    public void branch() {
    }

    @Test
    public void printStagedFiles() {
        Command.printStagedFiles();
    }

    @Test
    public void printRemovedFiles() {
        Command.printRemovedFiles();
    }

    @Test
    public void printModifiedFiles() {
    }

    @Test
    public void printUntrackedFiles() {
    }

    @Test
    public void rmBranch() {
    }

    @Test
    public void reset() {
    }

    @Test
    public void merge() {
    }
}
