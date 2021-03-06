package com.example.pune.demo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

// Importing UploadService Package.
import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Button SelectButton, UploadButton;
    EditText PdfNameEditText;
    Uri uri;
    public static final String PDF_UPLOAD_HTTP_URL = "http://api.canvatechnoloy.in/upload_pdf.php";
    public int PDF_REQ_CODE = 1;
    String PdfNameHolder, PdfPathHolder, PdfID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RequestRunTimePermission();

        SelectButton = (Button) findViewById(R.id.Button_Select_PDF_ID);
        UploadButton = (Button) findViewById(R.id.Button_Upload_PDF_ID);
        PdfNameEditText = (EditText) findViewById(R.id.PDF_Name_EditText_ID);

        SelectButton.setOnClickListener(view -> {

            Intent intent = new Intent();
            intent.setType("application/pdf");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PDF_REQ_CODE);

        });


        UploadButton.setOnClickListener(view -> PdfUploadFunction());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PDF_REQ_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            SelectButton.setText("PDF is Selected");
        }
    }


    // PDF upload function starts from here.
    public void PdfUploadFunction() {
        PdfNameHolder = PdfNameEditText.getText().toString().trim();
        PdfPathHolder = FilePath.getPath(this, uri);
        if (PdfPathHolder == null) {
            Toast.makeText(this, "Please move your PDF file to internal storage & try again.", Toast.LENGTH_LONG).show();
        } else {
            try {
                PdfID = UUID.randomUUID().toString();
                new MultipartUploadRequest(this, PdfID, PDF_UPLOAD_HTTP_URL)
                        .addFileToUpload(PdfPathHolder, "pdf")
                        .addParameter("name", PdfNameHolder)
                        .setNotificationConfig(new UploadNotificationConfig())
                        .setMaxRetries(5)
                        .startUpload();
            } catch (Exception exception) {
                Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    // Requesting run time permission method starts from here.
    public void RequestRunTimePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this, "READ_EXTERNAL_STORAGE permission Access Dialog", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] Result) {
        switch (RC) {
            case 1:
                if (Result.length > 0 && Result[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                 else
                    Toast.makeText(MainActivity.this, "Permission Canceled", Toast.LENGTH_LONG).show();
                break;
        }
    }
}