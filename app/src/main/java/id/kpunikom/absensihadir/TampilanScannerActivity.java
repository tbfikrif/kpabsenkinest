package id.kpunikom.absensihadir;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import id.kpunikom.absensihadir.control.ApiClient;
import id.kpunikom.absensihadir.control.ApiInterface;
import id.kpunikom.absensihadir.model.Item;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TampilanScannerActivity extends AppCompatActivity {

    SurfaceView cameraPreview;
    TextView txtResult;
    TextView txtDate;
    TextView txtSudahAbsen;
    TextView txtBelumAbsen;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    Boolean codeScanned = false;
    final int RequestCameraPermissionID = 1001;

    //JSON
    String id_anggota, nama;

    //Database
    DatabaseHelper myDB;
    Cursor data;

    //Retrofit
    private ApiInterface apiInterface;

    //LAYOUT
    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;
    private HadirFragment hadirFragment;
    private BelumHadirFragment belumHadirFragment;

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
        //START NAVIGATIONBOTTOM
        mMainFrame = (FrameLayout) findViewById(R.id.main_frame);
        mMainNav = (BottomNavigationView) findViewById(R.id.main_nav);

        hadirFragment = new HadirFragment();
        belumHadirFragment = new BelumHadirFragment();

        //default
        setFragment(hadirFragment);

        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
             @Override
             public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                 switch (item.getItemId()){
                     case  R.id.nav_hadir :
                         setFragment(hadirFragment);
                         return true;
                     case  R.id.nav_belum_hadir :
                         setFragment(belumHadirFragment);
                         return true;
                 }
                 return false;
             }
         });
        //END NAVIGATIONBOTTOM
        txtDate = findViewById(R.id.txtDate);
        String getDate = getCurrentDate();
        txtDate.setText(getDate);

        cameraPreview = findViewById(R.id.cameraPreview);
        txtResult = findViewById(R.id.txtResult);
        txtSudahAbsen = findViewById(R.id.txt_value_sudahabsen);
        txtBelumAbsen = findViewById(R.id.txt_value_belumabsen);

        myDB = new DatabaseHelper(this);
        data = myDB.getListContents();

        txtSudahAbsen.setText(Integer.toString(HadirFragment.jumlahSudahAbsen));
        txtBelumAbsen.setText(Integer.toString(BelumHadirFragment.jumlahBelumAbsen));

        //myDB.deleteRecord();

        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();
        cameraSource = new CameraSource.Builder(this, barcodeDetector).setAutoFocusEnabled(true).setRequestedPreviewSize(480, 680).build();

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

                            //txtResult.setText(qrcodes.valueAt(0).displayValue);

                            AlertDialog.Builder builder = new AlertDialog.Builder(TampilanScannerActivity.this);
                            View view = getLayoutInflater().inflate(R.layout.popup_info_kehadiran, null);
                            Button closeButton = view.findViewById(R.id.closeButton);
                            TextView textViewResult = view.findViewById(R.id.textViewResult);
                            
                            //Get JSON
                            try {
                                JSONObject object =new JSONObject(qrcodes.valueAt(0).displayValue);
                                id_anggota = object.getString("id_anggota");
                                nama = object.getString("nama");
                                DateFormat df = new SimpleDateFormat("HH:mm");
                                String currentTime = df.format(Calendar.getInstance().getTime());
                                String tempTime = "09:07";

                                textViewResult.setText(nama);

                                //Post API
                                apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                                Call<ArrayList<Item>> call = apiInterface.postHadir(id_anggota, tempTime);

                                call.enqueue(new Callback<ArrayList<Item>>() {
                                    @Override
                                    public void onResponse(Call<ArrayList<Item>> call, Response<ArrayList<Item>> response) {

                                    }

                                    @Override
                                    public void onFailure(Call<ArrayList<Item>> call, Throwable t) {

                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            builder.setView(view);
                            final AlertDialog dialog = builder.create();
                            dialog.show();

                            closeButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    codeScanned = false;
                                    txtResult.setText(R.string.result_text_default);
                                    //AddData(id_anggota, nama);
                                    ShareWA();
                                    dialog.dismiss();
                                }
                            });
                        }
                    });
                }
            }
        });


        try {
            Handler handler = new Handler();
            handler.removeCallbacks(setJumlah);
            handler.postDelayed(setJumlah,1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Runnable setJumlah = new Runnable() {
        @Override
        public void run() {
            txtSudahAbsen.setText(Integer.toString(HadirFragment.jumlahSudahAbsen));
            txtBelumAbsen.setText(Integer.toString(BelumHadirFragment.jumlahBelumAbsen));
        }
    };

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        try {
            Handler handler = new Handler();
            handler.removeCallbacks(setJumlah);
            handler.postDelayed(setJumlah,1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            Handler handler = new Handler();
            handler.removeCallbacks(setJumlah);
            handler.postDelayed(setJumlah,1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //GETCURENTDATE
    public String getCurrentDate() {
        final Calendar c = Calendar.getInstance();
        int year,month,day;
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DATE);
        return day + "/" + (month+1) + "/" + year;
    }

    //FRAGMENT
    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    public void AddData(String nama, String email){
        boolean insertData = myDB.addData(nama, email);

        if (insertData){
            //Toast.makeText(TampilanScannerActivity.this, nama + " Berhasil Login.", Toast.LENGTH_LONG).show();
        } else {
            //Toast.makeText(TampilanScannerActivity.this, nama + " Gagal Login.", Toast.LENGTH_LONG).show();
        }
    }

    public void ShareWA(){
        DateFormat df = new SimpleDateFormat("HH:mm");
        String currentTime = df.format(Calendar.getInstance().getTime());
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Hadir\n---\nSaya, "+nama+" sudah hadir dikantor pada hari ini pukul "+currentTime);
        try {
            startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(TampilanScannerActivity.this, "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
