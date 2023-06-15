package org.apache.bookkeeper.bookie;


import org.apache.bookkeeper.conf.ServerConfiguration;
import org.apache.bookkeeper.util.IOUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class utilities {

    public static BookieImpl getBookieImpl() throws Exception {

        File tempDir = new File(System.getProperty("java.io.tmpdir"), "bookieImplTests.tmp");

        if (tempDir.exists()) {
            try {
                FileUtils.deleteDirectory(tempDir);
                System.out.println("Existing temporary directory deleted successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File dirs = IOUtils.createTempDir("bookieImplTests", ".tmp");
        ServerConfiguration config = new ServerConfiguration();
        config.setAllowLoopback(true);
        config.setJournalDirName(dirs.toString());
        config.setLedgerDirNames(new String[] {dirs.getAbsolutePath()});

        return new TestBookieImpl(config);
    }

}

