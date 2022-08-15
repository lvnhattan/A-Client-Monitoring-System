package Client;

/*
 * Copyright (c) 2008, 2010, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import Config.User.Log;

import javax.swing.*;
import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;
import java.nio.file.attribute.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Example to watch a directory (or tree) for changes to files.
 */

public class Tracking implements Runnable {

    private WatchService watcher;
    private Map<WatchKey, Path> keys;
    private boolean recursive;
    private boolean trace = false;
    public static ArrayList<Log> userlogs = new ArrayList<>();
    public LogDir logdir;
    public Log temp;
    public String username;

    public Tracking(LogDir logdir) {
        this.logdir = logdir;
    }

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    /**
     * Register the given directory with the WatchService
     */
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        if (trace) {
            Path prev = keys.get(key);
            if (prev == null) {
                System.out.format("Register: %s\n", dir);
                logdir.sendMess("Register");
                temp = new Log(username, "Register", String.valueOf(logdir.getSocket()), LocalDateTime.now(), "Register " + String.valueOf(dir));
                userlogs.add(temp);
                logdir.sendPack(username, logdir.getSocket(), String.valueOf(dir));
            } else {
                if (!dir.equals(prev)) {
                    System.out.format("Update: %s -> %s\n", prev, dir);
                    logdir.sendMess("Update");
                    temp = new Log(username, "Update", String.valueOf(logdir.getSocket()), LocalDateTime.now(), "Update: "+String.valueOf(prev)+" -> "+String.valueOf(dir));
                    userlogs.add(temp);
                    logdir.sendPack(username, logdir.getSocket(), String.valueOf(prev)+" -> "+String.valueOf(dir));
                }
            }
        }
        keys.put(key, dir);
    }

    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void registerAll(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Creates a WatchService and registers the given directory
     */
    public Tracking(Path dir, boolean recursive, LogDir logdir, String username) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey, Path>();
        this.recursive = recursive;
        this.logdir = logdir;
        this.username = username;

        if (recursive) {
            System.out.format("Scanning %s ...\n", dir);
            logdir.sendMess("Scanning");
            temp = new Log(username, "Scanning", String.valueOf(logdir.getSocket()), LocalDateTime.now(), "Scanning: " + String.valueOf(dir));
            userlogs.add(temp);
            logdir.sendPack(username, logdir.getSocket(), String.valueOf(dir));
            registerAll(dir);
            System.out.println("Done");
            logdir.sendMess("Done");
            temp = new Log(username, "Done", String.valueOf(logdir.getSocket()), LocalDateTime.now(), "Done: " + String.valueOf(dir));
            userlogs.add(temp);
            logdir.sendPack(username, logdir.getSocket(), String.valueOf(dir));
        } else {
            register(dir);
        }

        // enable trace after initial registration
        this.trace = true;
    }

    /**
     * Process all events for keys queued to the watcher
     */

    @Override
    public void run() {
        try {
            while (true) {

                // wait for key to be signalled
                WatchKey key;
                try {
                    key = watcher.take();
                } catch (InterruptedException x) {
                    return;
                }

                Path dir = keys.get(key);
                if (dir == null) {
                    System.err.println("WatchKey not recognized!!");
                    continue;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind kind = event.kind();

                    // TBD - provide example of how OVERFLOW event is handled
                    if (kind == OVERFLOW) {
                        continue;
                    }

                    // Context for directory entry event is the file name of entry
                    WatchEvent<Path> ev = cast(event);
                    Path name = ev.context();
                    Path child = dir.resolve(name);

                    // print out event
                    System.out.format("%s: %s\n", event.kind().name(), child);
                    logdir.sendMess(event.kind().name());
                    logdir.sendPack(username, logdir.getSocket(), String.valueOf(child));
                    temp = new Log(username, event.kind().name(), String.valueOf(logdir.getSocket()), LocalDateTime.now(), event.kind().name() + " " + String.valueOf(dir));
                    userlogs.add(temp);
                    // if directory is created, and watching recursively, then
                    // register it and its sub-directories
                    if (recursive && (kind == ENTRY_CREATE)) {
                        try {
                            if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                                registerAll(child);
                            }
                        } catch (IOException x) {
                            // ignore to keep sample readable
                        }
                    }
                }

                // reset key and remove from set if directory no longer accessible
                boolean valid = key.reset();
                if (!valid) {
                    keys.remove(key);

                    // all directories are inaccessible
                    if (keys.isEmpty()) {
                        break;
                    }
                }
            }
        } catch (
                Exception e) {
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Bug", e.getMessage(), JOptionPane.ERROR_MESSAGE);
            System.out.println(e.getMessage());
        }
    }
}
/*    static void usage() {
        System.err.println("usage: java Tracking [-r] dir");
        System.exit(-1);
    }*/

