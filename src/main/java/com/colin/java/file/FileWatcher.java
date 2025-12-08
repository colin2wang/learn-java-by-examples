package com.colin.java.file;

import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

@Slf4j
public class FileWatcher {

    public static void main(String[] args) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    WatchService watcher = FileSystems.getDefault().newWatchService();
                    // 获取当前项目根目录
                    Path dir = Paths.get(System.getProperty("user.dir"));
                    log.info("Watching directory: {}", dir);
                    WatchKey key = dir.register(watcher, ENTRY_MODIFY);
                    while (true) {
                        key = watcher.take();
                        for (WatchEvent<?> event : key.pollEvents()) {
                            if (event.kind() == ENTRY_MODIFY) {
                                log.info("Home dir changed!");
                            }
                        }
                        key.reset();
                    }
                } catch (IOException | InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
            }
        });

        thread.start();
    }
}