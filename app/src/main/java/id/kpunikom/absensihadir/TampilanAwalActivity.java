package id.kpunikom.absensihadir;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import at.markushi.ui.CircleButton;

public class TampilanAwalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tampilan_awal);

        CircleButton btnScanner = findViewById(R.id.btnscanner);
         btnScanner.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent TampilanScannerActivity = new
                         Intent(TampilanAwalActivity.this, TampilanScannerActivity.class);
                 startActivity(TampilanScannerActivity);
             }
         });

    }


}
