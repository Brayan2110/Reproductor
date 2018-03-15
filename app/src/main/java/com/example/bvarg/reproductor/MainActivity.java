package com.example.bvarg.reproductor;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    int actual;
    Button pausa;
    MediaPlayer cancion = null;
    SeekBar duracion;
    SeekBar volumen;
    EditText letra;
    Handler handler;
    Runnable runnable;
    ListView lista;
    RelativeLayout layoutletra;
    GridLayout grid_lista;
    ArrayList listacanciones = new ArrayList();
    ArrayList array = new ArrayList();
    ArrayList array_letras = new ArrayList();
    TextView textView_nombre;
    AudioManager audioManager;
    boolean pausaactiva = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler();
        pausa = (Button)findViewById(R.id.button_pausa);
        duracion = (SeekBar)findViewById(R.id.seekBar_cancion);
        duracion.setEnabled(false);
        lista = (ListView)findViewById(R.id.lista_musica);
        grid_lista = (GridLayout)findViewById(R.id.grid_lista);
        textView_nombre = (TextView)findViewById(R.id.textView_nombre);
        volumen = (SeekBar)findViewById(R.id.seekBar_volumen);
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        letra = (EditText)findViewById(R.id.editText_letra);
        layoutletra = (RelativeLayout)findViewById(R.id.relativeLayoutLetra);

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
        listacanciones.add(R.raw.grupo_extra__me_emborrachare);
        listacanciones.add(R.raw.johnny_sky__quiereme);
        listacanciones.add(R.raw.johnny_sky__solo_quiero);
        listacanciones.add(R.raw.romeo_santos__imitadora);
        listacanciones.add(R.raw.romeo_santos__you);
        listacanciones.add(R.raw.romeo_santos_ft_usher__promise);
        array_letras.add(R.raw.grupo_extra__me_emborrachare_letra);
        array_letras.add(R.raw.johnny_sky__quiereme_letra);
        array_letras.add(R.raw.johnny_sky__solo_quiero_letra);
        array_letras.add(R.raw.romeo_santos__imitadora_letra);
        array_letras.add(R.raw.romeo_santos__you_letra);
        array_letras.add(R.raw.romeo_santos_ft_usher__promise_letra);
        array.add("Grupo Extra - Me Emborrachare");
        array.add("Johnny Sky - Quiereme");
        array.add("Johnny Sky - Solo Quiero");
        array.add("Romeo Santos - Imitadora");
        array.add("Romeo Santos - You");
        array.add("Romeo Santos Ft. Usher - Promise");
        ArrayAdapter adaptador = new ArrayAdapter(this, android.R.layout.simple_list_item_1, array);
        lista.setAdapter(adaptador);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                duracion.setEnabled(true);
                escoger(position);
            }
        });
        volumen();
    }

    public void escoger(int n){
        if(!pausaactiva){
            cancion.stop();
            pausaactiva = true;
        }
        try {
            letra(n);
        } catch (IOException e) {
            e.printStackTrace();
        }
        actual = n;
        textView_nombre.setText(String.valueOf(array.get(n)));
        cancion = MediaPlayer.create(this, (int)listacanciones.get(n));
        cancion.setAudioStreamType(AudioManager.STREAM_MUSIC);
        validar();
    }

    public void letra(int n) throws IOException {
        String Total = "";
        String linea;
        InputStream is = this.getResources().openRawResource((int)array_letras.get(n));
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        if(is!=null){
            while((linea=reader.readLine())!=null){
                Total = Total + linea;
            }
        }
        letra.setText(Total);
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

    public void iniciar_pausar(View view){
        if(cancion != null) {
            if (pausaactiva) {
                duracion.setMax(cancion.getDuration());
                cancion.start();
                playCycle();
                pausaactiva = false;
                int resId = getResources().getIdentifier("@android:drawable/ic_media_pause", "drawable", MainActivity.this.getPackageName());
                Drawable d = MainActivity.this.getResources().getDrawable(resId);
                pausa.setBackground(d);
            } else {
                cancion.pause();
                pausaactiva = true;
                int resId = getResources().getIdentifier("@android:drawable/ic_media_play", "drawable", MainActivity.this.getPackageName());
                Drawable d = MainActivity.this.getResources().getDrawable(resId);
                pausa.setBackground(d);
            }
        }
    }

    public void atras(View view){
        if(cancion != null) {
            cancion.stop();
            pausaactiva = true;
            if(actual==0){
                escoger(listacanciones.size()-1);
            }
            else{
                escoger(actual-1);
            }

        }
    }

    public void adelante(View view){
        if(cancion != null) {
            cancion.stop();
            pausaactiva = true;
            if(actual == listacanciones.size()-1){
                escoger(0);
            }
            else{
                escoger(actual+1);
            }
        }
    }

    public void mostrarlista(View view){
        if(grid_lista.getVisibility() == View.VISIBLE){
            grid_lista.setVisibility(View.INVISIBLE);
            layoutletra.setVisibility(View.VISIBLE);
        }
        else{
            grid_lista.setVisibility(View.VISIBLE);
            layoutletra.setVisibility(View.INVISIBLE);

        }
    }

    public void validar(){
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

    public void volumen() {
        try {
            volumen.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumen.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            volumen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override            public void onStopTrackingTouch(SeekBar arg0) {
                }

                @Override            public void onStartTrackingTouch(SeekBar arg0) {
                }

                @Override            public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(cancion != null) {
            if (!pausaactiva) {
                cancion.start();
            }
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(cancion != null) {
            cancion.pause();
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(cancion != null) {
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
// Esto es lo que hace mi botón al pulsar ir a atrás
            minimizeApp();
            return true;
        }
        else if(keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
// Esto es lo que hace mi botón al pulsar volumen abajo
            volumenalto();
            return true;
        }
        else if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
// Esto es lo que hace mi botón al pulsar volumen abajo
            volumenbajo();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void minimizeApp(){
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    public void volumenbajo(){
        volumen.setProgress(volumen.getProgress()-1);
    }

    public void volumenalto(){
        volumen.setProgress(volumen.getProgress()+1);
    }
}
