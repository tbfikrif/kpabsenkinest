package id.kpunikom.absensihadir;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import id.kpunikom.absensihadir.control.ApiClient;

public class TampilanPengaturanActivity extends AppCompatActivity {

    private EditText ubahHost;
    private Button btnUbahHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tampilan_pengaturan);

        ubahHost = findViewById(R.id.editHost);
        btnUbahHost = findViewById(R.id.okButton);

        btnUbahHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent TampilanAwalActivity = new
                        Intent(TampilanPengaturanActivity.this, TampilanAwalActivity.class);
                startActivity(TampilanAwalActivity);
            }
        });
    }
}
