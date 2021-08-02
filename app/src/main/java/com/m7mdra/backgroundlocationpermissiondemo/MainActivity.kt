package com.m7mdra.backgroundlocationpermissiondemo

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {
    var permissions =
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button = findViewById<MaterialButton>(R.id.permissionButton)
        button.setOnClickListener {
            checkLocationPermission()
        }
    }

    private fun checkLocationPermission() {
        //تحقق اذا تم منح صلاحية الوصول للموقع
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // طلب الصلاحية
            requestPermissionOrShowRational(13)
        } else {
            //  تحقق من نسخة نظام التشغيل , نسخة تساوي ١١ او اعلى
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // تحقق من صلاحية الوصول للموقع في الخلفية
                if (hasBackgroundPermission()) {
                    // تم منح الصلاحية مسبقا
                    Toast.makeText(this," تم منح الصلاحية في الخلفية",Toast.LENGTH_SHORT).show()
                } else {
                    // طلب الصلاحية
                    showBackgroundLocationDialog(13)
                }
            } else {
                // تم منح الصلاحية مسبقا
                Toast.makeText(this,"تم منح الصلاحية",Toast.LENGTH_SHORT).show()

            }
        }
    }
    private fun requestPermissionOrShowRational(requestCode: Int) {
        // تحقق اذا كان تم رفض الصلاحية مسبقا
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            //اظهار صندوق يبين للمستخدم لماذا هذه الصلاحية مهمة لكي تعمل الخاصية المعينة
            MaterialAlertDialogBuilder(this)
                .setCancelable(false)
                .setTitle("تم رفض الصلاحية")
                .setMessage("نستخدم هذه الصلاحية في محاولة ايجاد موقعك اذا قمت بالسفر الى كوكب المشتري")
                .setPositiveButton("طلب مرة اخرى") { dialogInterface: DialogInterface, _: Int ->
                    // طلب الصلاحية في حالة لم يتم رفض الصلاحية مسبقا
                    requestPermission(requestCode)
                    dialogInterface.dismiss()
                }
                .show()
        } else {
            // طلب الصلاحية في حالة لم يتم رفض الصلاحية مسبقا
            requestPermission(requestCode)
        }
    }
    private fun requestPermission(requestCode: Int) {
        ActivityCompat.requestPermissions(
            this,
            permissions,
            requestCode
        )
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun hasBackgroundPermission() =
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun showPermissionDeniedDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("تم رفض الصلاحية")
            .setMessage("نستخدم هذه الصلاحية في محاولة ايجاد موقعك اذا قمت بالسفر الى كوكب المشتري")
            .setPositiveButton("فتح الاعدادات") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                openSettings()
                finish()
            }
            .show()
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun showBackgroundLocationDialog(requestCode: Int) {
        MaterialAlertDialogBuilder(this)
            .setTitle("صلاحية الموقع في الخلفية مطلوبة")
            .setCancelable(false)
            .setMessage("لكي يتم تتبعك موقعك في كل الحالات قم بالموافقة على هذه الصلاحيات")
            .setPositiveButton("حسنا") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    requestCode
                )
            }
            .show()
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 13){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"تم منح الصلاحية",Toast.LENGTH_SHORT).show()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    //الان لنقم بطلب صلاحية الموقع من الخلفية
                    showBackgroundLocationDialog(14)
                }else {
                    Toast.makeText(this,"تم منح الصلاحية في الخلفية مسبقاً",Toast.LENGTH_SHORT).show()

                }
            }else{
                //اظهار رسالة تبين رفض الصلاحية
                showPermissionDeniedDialog()
            }
        }
        if(requestCode == 14){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"تم منح الصلاحية في الخلفية ",Toast.LENGTH_SHORT).show()
            }else{
                //اظهار رسالة تبين رفض الصلاحية
                showPermissionDeniedDialog()
            }
        }
    }


    private fun openSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri: Uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }
}