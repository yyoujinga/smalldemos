package com.example.peripheraldevice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.StorageItem;
import com.example.peripheraldevice.adapter.FileAdapter;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private TextView currentPathText;
    private File currentDir;
    private List<File> fileList;
    private FileAdapter adapter;
    private USBReceiver mUsbReceiver;
    private static final int PERMISSION_REQUEST_CODE = 1000;

    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 设置返回按钮点击事件
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());
        initViews();
        checkAndRequestPermissions();
        registerReceiver();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        currentPathText = findViewById(R.id.currentPathText);

        fileList = new ArrayList<>();
        adapter = new FileAdapter(fileList, this::onFileClick);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            } else {
                showRootDirectories();
            }
        } else {
            showRootDirectories();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showRootDirectories();
            } else {
                Toast.makeText(this, "需要存储权限才能访问文件",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void onFileClick(File file) {
        if (file.isDirectory()) {
            if (!file.canRead()) {
                Toast.makeText(this, "无法访问该目录", Toast.LENGTH_SHORT).show();
                return;
            }
            currentDir = file;
            loadDirectory(file);
        } else {
            Toast.makeText(this, "文件: " + file.getName(),
                    Toast.LENGTH_SHORT).show();
        }
    }


    private void showRootDirectories() {
        fileList.clear();
        // 隐藏返回按钮
        btnBack.setVisibility(View.GONE);

        // 添加内部存储
        String internalPath = getStoragePathAlternative(this, "EXT");
        if (!TextUtils.isEmpty(internalPath)) {
            File internalStorage = new File(internalPath);
            if (internalStorage.exists()) {
                fileList.add(new StorageItem("内部存储", internalStorage));
            }
        }

        // 添加SD卡
        String sdPath = getStoragePathAlternative(this, "SD");
        if (!TextUtils.isEmpty(sdPath)) {
            File sdCard = new File(sdPath);
            if (sdCard.exists() && sdCard.canRead()) {
                fileList.add(new StorageItem("SD卡", sdCard));
            }
        }

        // 添加U盘
        String usbPath = getStoragePathAlternative(this, "USB");
        if (!TextUtils.isEmpty(usbPath)) {
            File usbStorage = new File(usbPath);
            if (usbStorage.exists() && usbStorage.canRead()) {
                fileList.add(new StorageItem("U盘", usbStorage));
            }
        }

        adapter.updateData(fileList);
        currentPathText.setText("存储设备");

        // 打印调试信息
        Log.d(TAG, "Internal: " + internalPath);
        Log.d(TAG, "SD Card: " + sdPath);
        Log.d(TAG, "USB: " + usbPath);
    }


    private void loadDirectory(File directory) {
        // 显示返回按钮
        btnBack.setVisibility(View.VISIBLE);
        fileList.clear();
        File[] files = directory.listFiles();
        if (files != null) {
            // 排序：文件夹在前，文件在后，按名称排序
            Arrays.sort(files, (f1, f2) -> {
                if (f1.isDirectory() && !f2.isDirectory()) return -1;
                if (!f1.isDirectory() && f2.isDirectory()) return 1;
                return f1.getName().compareToIgnoreCase(f2.getName());
            });
            fileList.addAll(Arrays.asList(files));
        }
        adapter.updateData(fileList);
        currentPathText.setText(directory.getAbsolutePath());
    }

    // 获取存储路径的方法修改如下：
//    public static String getStoragePath(Context mContext, String keyword) {
//        String resultPath = "";
//        StorageManager storageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
//
//        try {
//            String[] paths = getAllVolumePaths(mContext);
//            if (paths == null || paths.length == 0) {
//                return resultPath;
//            }
//
//            // 获取外置SD卡路径
//            String sdCardPath = "";
//            for (String path : paths) {
//                if (path.contains("external") || path.contains("sdcard1")) {
//                    sdCardPath = path;
//                    break;
//                }
//            }
//
//            switch (keyword) {
//                case "EXT":
//                    // 内部存储
//                    resultPath = Environment.getExternalStorageDirectory().getAbsolutePath();
//                    break;
//                case "SD":
//                    // SD卡
//                    if (!TextUtils.isEmpty(sdCardPath)) {
//                        resultPath = sdCardPath;
//                    }
//                    break;
//                case "USB":
//                    // U盘
//                    for (String path : paths) {
//                        if (!path.equals(Environment.getExternalStorageDirectory().getAbsolutePath())
//                                && !path.equals(sdCardPath)
//                                && path.contains("usb")) {
//                            resultPath = path;
//                            break;
//                        }
//                    }
//                    break;
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "getStoragePath error: " + e.getMessage());
//        }
//
//        return resultPath;
//    }

    // 使用 StorageVolume 来获取存储路径（Android 6.0及以上）
    @SuppressLint("NewApi")
    public static String getStoragePathAlternative(Context context, String keyword) {
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        List<StorageVolume> volumes = storageManager.getStorageVolumes();

        for (StorageVolume volume : volumes) {
            try {
                Method getPathMethod = StorageVolume.class.getMethod("getPath");
                String path = (String) getPathMethod.invoke(volume);

                boolean isRemovable = volume.isRemovable();
                boolean isPrimary = volume.isPrimary();

                if ("EXT".equals(keyword) && isPrimary) {
                    return path;
                } else if ("SD".equals(keyword) && isRemovable && !path.contains("usb")) {
                    return path;
                } else if ("USB".equals(keyword) && isRemovable && path.contains("usb")) {
                    return path;
                }
            } catch (Exception e) {
                Log.e(TAG, "getStoragePathAlternative error: " + e.getMessage());
            }
        }

        return "";
    }


    // 注册广播接收器
    public void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        // USB相关
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        // SD卡相关
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_CHECKING);
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addDataScheme("file");

        mUsbReceiver = new USBReceiver();
        registerReceiver(mUsbReceiver, filter);
    }

    // 广播接收器
    private class USBReceiver extends BroadcastReceiver {
        final String SD_IN = "android.intent.action.MEDIA_MOUNTED";
        final String SD_OUT = "android.intent.action.MEDIA_UNMOUNTED";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) return;

            // 刷新存储设备列表
            switch (action) {
                case UsbManager.ACTION_USB_DEVICE_ATTACHED:
                    Toast.makeText(context, "USB设备已插入", Toast.LENGTH_SHORT).show();
                    refreshStorageList();
                    break;
                case UsbManager.ACTION_USB_DEVICE_DETACHED:
                    Toast.makeText(context, "USB设备已移除", Toast.LENGTH_SHORT).show();
                    refreshStorageList();
                    break;
                case SD_IN:
                    Toast.makeText(context, "SD卡已插入", Toast.LENGTH_SHORT).show();
                    refreshStorageList();
                    break;
                case SD_OUT:
                    Toast.makeText(context, "SD卡已移除", Toast.LENGTH_SHORT).show();
                    refreshStorageList();
                    break;
            }
        }
    }

    // 刷新存储设备列表
    private void refreshStorageList() {
        if (currentDir == null) {
            showRootDirectories();
        }
    }

    public void unregisterReceiver() {
        if (mUsbReceiver != null) {
            unregisterReceiver(mUsbReceiver);
            mUsbReceiver = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (currentDir != null) {
            if (isStorageRoot(currentDir)) {
                // 如果当前在存储根目录，返回到设备列表
                showRootDirectories();
                currentDir = null;
            } else {
                // 否则返回上一级目录
                File parentFile = currentDir.getParentFile();
                if (parentFile != null) {
                    currentDir = parentFile;
                    loadDirectory(parentFile);
                }
            }
        } else {
            super.onBackPressed();
        }
    }

    // 在类的成员变量中添加
    private String initialSdPath = null;
    private String initialUsbPath = null;
    private boolean isStorageRoot(File file) {
        try {
            // 获取所有存储路径
            String internalPath = getStoragePathAlternative(this, "EXT");
            String sdPath = getStoragePathAlternative(this, "SD");
            String usbPath = getStoragePathAlternative(this, "USB");

            // 保存初始路径
            if (initialSdPath == null && !TextUtils.isEmpty(sdPath)) {
                initialSdPath = sdPath;
            }
            if (initialUsbPath == null && !TextUtils.isEmpty(usbPath)) {
                initialUsbPath = usbPath;
            }

            // 检查是否是任何一个存储设备的根目录
            return file.getAbsolutePath().equals(internalPath) ||
                    file.getAbsolutePath().equals(initialSdPath) ||
                    file.getAbsolutePath().equals(initialUsbPath);
        } catch (Exception e) {
            Log.e(TAG, "isStorageRoot error: " + e.getMessage());
            return false;
        }
    }



}