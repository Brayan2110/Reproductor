package com.example.bvarg.reproductor;

import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {

    Button pausa;
    MediaPlayer cancion;
    SeekBar duracion;
    Handler handler;
    Runnable runnable;
    boolean pausaactiva = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler();
        pausa = (Button)findViewById(R.id.button_pausa);
        duracion = (SeekBar)findViewById(R.id.seekBar_cancion);

        cancion = MediaPlayer.create(this, R.raw.quiereme);
        cancion.setAudioStreamType(AudioManager.STREAM_MUSIC);

        duracion.setMax(cancion.getDuration());

        duracion.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    cancion.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void playCycle(){
        duracion.setProgress(cancion.getCurrentPosition());
        if(cancion.isPlaying()){
            runnable = new Runnable() {
                @Override
                public void run() {
                    playCycle();
                }
            };
            handler.postDelayed(runnable,1000);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void iniciar_pausar(View view){
        if(pausaactiva){
            duracion.setMax(cancion.getDuration());
            cancion.start();
            playCycle();
            pausaactiva = false;
            int resId = getResources().getIdentifier("@android:drawable/ic_media_pause","drawable",MainActivity.this.getPackageName());
            Drawable d = MainActivity.this.getResources().getDrawable(resId);
            pausa.setBackground(d);
        }
        else{
            cancion.pause();
            pausaactiva = true;
            int resId = getResources().getIdentifier("@android:drawable/ic_media_play","drawable",MainActivity.this.getPackageName());
            Drawable d = MainActivity.this.getResources().getDrawable(resId);
            pausa.setBackground(d);
        }
    }
}
