package Config;

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

import Client.Main.FormClient;
import Config.User.AccountUser;
import Config.User.Log;
import javax.swing.*;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Example to watch a directory (or tree) for changes to files.
 */

public class Tracking implements Runnable {

    private WatchService watcher;
    private Map<WatchKey, Path> keys;
    private boolean recursive;
    private boolean trace = false;
    private String filelog = "ClientLogs.txt" ;
    public static ArrayList<Log> userlogs = new ArrayList<>();
    public LogDir logdir;
    public Log temp;
    public String username;
    public DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public Boolean isrunning=true;


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
                temp = new Log(username, "Register", logdir.getSocket().getInetAddress() + ":" + logdir.getSocket().getPort() + " " + logdir.getSocket().getLocalPort(), LocalDateTime.now().format(dateFormat), "Register " + String.valueOf(dir));
                userlogs.add(temp);
                logdir.sendPack(username, logdir.getSocket(), String.valueOf(dir));
                logdir.writeFile(temp,filelog);

            } else {
                if (!dir.equals(prev)) {
                    System.out.format("Update: %s -> %s\n", prev, dir);
                    logdir.sendMess("Update");
                    temp = new Log(username, "Update", logdir.getSocket().getInetAddress() + ":" + logdir.getSocket().getPort() + " " + logdir.getSocket().getLocalPort(), LocalDateTime.now().format(dateFormat), "Update: " + String.valueOf(prev) + " -> " + String.valueOf(dir));
                    userlogs.add(temp);
                    logdir.sendPack(username, logdir.getSocket(), String.valueOf(prev) + " -> " + String.valueOf(dir));
                    logdir.writeFile(temp,filelog);
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
            temp = new Log(username, "Scanning", logdir.getSocket().getInetAddress() + ":" + logdir.getSocket().getPort() + " " + logdir.getSocket().getLocalPort(), LocalDateTime.now().format(dateFormat), "Scanning: " + String.valueOf(dir));
            userlogs.add(temp);
            logdir.sendPack(username, logdir.getSocket(), String.valueOf(dir));
            logdir.writeFile(temp,filelog);
            registerAll(dir);
            System.out.println("Done");
            logdir.sendMess("Done");
            temp = new Log(username, "Done", logdir.getSocket().getInetAddress() + ":" + logdir.getSocket().getPort() + " " + logdir.getSocket().getLocalPort(), LocalDateTime.now().format(dateFormat), "Done: " + String.valueOf(dir));
            userlogs.add(temp);
            logdir.sendPack(username, logdir.getSocket(), String.valueOf(dir));
            logdir.writeFile(temp,filelog);
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
            while (isrunning) {

                // wait for key to be signalled
                WatchKey key;
                try {
                    key = watcher.take();
                } catch (InterruptedException x) {
                    return;
                }
                if (!isrunning){
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
                    String acction="" ;
                    if (event.kind().name().equals(ENTRY_CREATE.name())) {
                        acction = "Create";
                    } else if (event.kind().name().equals(ENTRY_DELETE.name())) {
                        acction = "Delete";
                    } else if (event.kind().name().equals(ENTRY_MODIFY.name())) {
                        acction = "Modify";
                    }
                    temp = new Log(username, acction, logdir.getSocket().getInetAddress() + ":" + logdir.getSocket().getPort() + " " + logdir.getSocket().getLocalPort(), LocalDateTime.now().format(dateFormat), acction + " " + String.valueOf(child));
                    userlogs.add(temp);
                    logdir.sendPack(username, logdir.getSocket(), String.valueOf(child));
                    logdir.writeFile(temp,filelog);
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

