package com.example;

import java.io.File;

public class StorageItem extends File {
    private String displayName;

    public StorageItem(String displayName, File file) {
        super(file.getAbsolutePath());
        this.displayName = displayName;
    }

    @Override
    public String getName() {
        return displayName;
    }
}