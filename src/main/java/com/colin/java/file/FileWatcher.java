package com.colin.java.file;

import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class FileWatcher {

    public static void main(String[] args) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    WatchService watcher = FileSystems.getDefault().newWatchService();
                    Path dir = FileSystems.getDefault().getPath("F:\\Workspaces\\JetBrains\\IdeaProjects\\HelloJava");
                    WatchKey key = dir.register(watcher, ENTRY_MODIFY);
                    while (true) {
                        key = watcher.take();
                        for (WatchEvent<?> event : key.pollEvents()) {
                            if (event.kind() == ENTRY_MODIFY) {
                                System.out.println("Home dir changed!");
                            }
                        }
                        key.reset();
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
}
