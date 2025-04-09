package com.example.smalldemos.permission;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionHelper {

    private final AppCompatActivity activity;
    private final ActivityResultLauncher<String[]> requestPermissionLauncher;
    private PermissionCallback callback;

    public PermissionHelper(AppCompatActivity activity) {
        this.activity = activity;

        // 初始化 Activity Result API 权限请求
        this.requestPermissionLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    List<String> deniedPermissions = new ArrayList<>();
                    for (String permission : result.keySet()) {
                        if (Boolean.FALSE.equals(result.get(permission))) {
                            deniedPermissions.add(permission);
                        }
                    }

                    if (deniedPermissions.isEmpty()) {
                        // 所有权限都已授予
                        if (callback != null) {
                            callback.onPermissionsGranted();
                        }
                    } else {
                        // 有权限被拒绝
                        handleDeniedPermissions(deniedPermissions);
                    }
                }
        );
    }

    // 设置回调
    public void setPermissionCallback(PermissionCallback callback) {
        this.callback = callback;
    }

    // 请求存储权限
    public void requestStoragePermissions() {
        List<String> permissions = new ArrayList<>();

        // 根据 Android 版本动态选择权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13 及以上
            permissions.add(android.Manifest.permission.READ_MEDIA_AUDIO);
            // 如果需要访问图片或视频，可以添加以下权限
             permissions.add(android.Manifest.permission.READ_MEDIA_IMAGES);
             permissions.add(android.Manifest.permission.READ_MEDIA_VIDEO);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // Android 6.0 至 Android 12
            permissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                permissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                permissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }

        // 检查权限是否已授予
        List<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        if (permissionsToRequest.isEmpty()) {
            // 所有权限都已授予
            if (callback != null) {
                callback.onPermissionsGranted();
            }
        } else {
            // 请求未授予的权限
            requestPermissionLauncher.launch(permissionsToRequest.toArray(new String[0]));
        }
    }

    // 处理权限被拒绝的情况
    private void handleDeniedPermissions(List<String> deniedPermissions) {
        List<String> permanentlyDenied = new ArrayList<>();
        for (String permission : deniedPermissions) {
            // 检查是否是“不再询问”状态
            if (!activity.shouldShowRequestPermissionRationale(permission)) {
                permanentlyDenied.add(permission);
            }
        }

        if (!permanentlyDenied.isEmpty()) {
            // 用户选择了“不再询问”，引导到设置页面
            showGoToSettingsDialog(permanentlyDenied);
        } else {
            // 用户拒绝但未选择“不再询问”，显示解释对话框
            showExplainDialog(deniedPermissions);
        }
    }

    // 显示解释对话框
    private void showExplainDialog(List<String> deniedPermissions) {
        new AlertDialog.Builder(activity)
                .setTitle("权限请求")
                .setMessage("应用需要访问存储权限以读取音乐文件，请允许权限以继续。")
                .setPositiveButton("允许", (dialog, which) -> {
                    // 重新请求权限
                    requestPermissionLauncher.launch(deniedPermissions.toArray(new String[0]));
                })
                .setNegativeButton("拒绝", (dialog, which) -> {
                    if (callback != null) {
                        callback.onPermissionsDenied(deniedPermissions);
                    }
                })
                .setCancelable(false)
                .show();
    }

    // 引导用户到设置页面
    private void showGoToSettingsDialog(List<String> permanentlyDenied) {
        new AlertDialog.Builder(activity)
                .setTitle("权限被永久拒绝")
                .setMessage("部分权限已被永久拒绝，请在设置中手动开启存储权限。")
                .setPositiveButton("去设置", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                    intent.setData(uri);
                    activity.startActivity(intent);
                })
                .setNegativeButton("取消", (dialog, which) -> {
                    if (callback != null) {
                        callback.onPermissionsDenied(permanentlyDenied);
                    }
                })
                .setCancelable(false)
                .show();
    }

    // 权限请求回调接口
    public interface PermissionCallback {
        void onPermissionsGranted();

        void onPermissionsDenied(List<String> deniedPermissions);
    }
}