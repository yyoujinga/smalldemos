package com.example.smalldemos;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.smalldemos.permission.PermissionHelper;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PermissionHelper permissionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化 PermissionHelper
        permissionHelper = new PermissionHelper(this);

        // 设置权限请求回调
        permissionHelper.setPermissionCallback(new PermissionHelper.PermissionCallback() {
            @Override
            public void onPermissionsGranted() {
                Toast.makeText(MainActivity.this, "存储权限已授予，可以开始加载音乐文件了", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionsDenied(List<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "缺少存储权限，部分功能可能无法使用: " + deniedPermissions, Toast.LENGTH_LONG).show();
            }
        });

        // 请求存储权限
        permissionHelper.requestStoragePermissions();
    }
}