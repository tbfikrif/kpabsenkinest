package id.kpunikom.absensihadir;

import android.Manifest;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class TampilanScannerActivity extends AppCompatActivity {

    SurfaceView cameraPreview;
    TextView txtResult;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    Boolean codeScanned = false;
    final int RequestCameraPermissionID = 1001;

    //JSON
    String nama, email;

    //Database
    DatabaseHelper myDB;

    ListView listView;
    ArrayList<String> theList;
    ArrayAdapter<String> arrayAdapter;
    Cursor data;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(cameraPreview.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tampilan_scanner);

        cameraPreview = findViewById(R.id.cameraPreview);
        txtResult = findViewById(R.id.txtResult);
        listView = findViewById(R.id.listView);

        myDB = new DatabaseHelper(this);
        theList = new ArrayList<>();
        data = myDB.getListContents();

        ShowData();

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize(480, 680)
                .build();

        //Add Event
        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //Request permission
                    ActivityCompat.requestPermissions(TampilanScannerActivity.this, new String[]{Manifest.permission.CAMERA}, RequestCameraPermissionID);
                    return;
                }
                try {
                    cameraSource.start(cameraPreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrcodes = detections.getDetectedItems();
                if (qrcodes.size() != 0 && !codeScanned) {
                    txtResult.post(new Runnable() {
                        @Override
                        public void run() {
                            //Validasi
                            codeScanned = true;

                            //Create Vibrate
                            Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(50);

                            //Create Sound
                            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                            mediaPlayer.start();

                            txtResult.setText(qrcodes.valueAt(0).displayValue);

                            AlertDialog.Builder builder = new AlertDialog.Builder(TampilanScannerActivity.this);
                            View view = getLayoutInflater().inflate(R.layout.popup_info_kehadiran, null);
                            Button closeButton = view.findViewById(R.id.closeButton);
                            TextView textViewResult = view.findViewById(R.id.textViewResult);
                            
                            //Get JSON
                            try {
                                JSONObject object =new JSONObject(qrcodes.valueAt(0).displayValue);
                                nama = object.getString("nama");
                                email = object.getString("email");

                                textViewResult.setText(nama);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            UpdateData();

                            builder.setView(view);
                            final AlertDialog dialog = builder.create();
                            dialog.show();

                            closeButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    codeScanned = false;
                                    txtResult.setText(R.string.result_text_default);
                                    AddData(nama);
                                    dialog.dismiss();
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    public void AddData(String newEntry){
        boolean insertData = myDB.addData(newEntry);

        if (insertData){
            Toast.makeText(TampilanScannerActivity.this, newEntry + " Berhasil Login.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(TampilanScannerActivity.this, newEntry + " Gagal Login.", Toast.LENGTH_LONG).show();
        }
    }

    public void ShowData(){
        if (data.getCount() == 0){
            Toast.makeText(TampilanScannerActivity.this, "Belum ada yang Login.", Toast.LENGTH_LONG).show();
        } else {
            while (data.moveToNext()){
                theList.add(data.getString(1));
                ListAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, theList);
                arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, theList);
                //listView.setAdapter(listAdapter);
                listView.setAdapter(arrayAdapter);
            }
        }
    }

    public void UpdateData(){
        while (data.moveToNext()){
            theList.add(data.getString(1));
            arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, theList);
            arrayAdapter.notifyDataSetChanged();
            listView.invalidateViews();
            listView.refreshDrawableState();
            listView.setAdapter(arrayAdapter);
        }
    }

}
