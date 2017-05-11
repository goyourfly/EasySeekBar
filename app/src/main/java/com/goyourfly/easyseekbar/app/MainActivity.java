package com.goyourfly.easyseekbar.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.goyourfly.easyseekbar.EasySeekBar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EasySeekBar seekBar = (EasySeekBar) findViewById(R.id.seekbar);
        seekBar.setProgressChangeListener(new EasySeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(int progress, int max, CharSequence value) {
                Toast.makeText(MainActivity.this,"Progress:" + progress + ",Max:" + max + ",Value:" + value,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
