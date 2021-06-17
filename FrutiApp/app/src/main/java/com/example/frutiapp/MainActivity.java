package com.example.frutiapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText txt_nombre;
    private ImageView img_personaje;
    private TextView txt_puntaje;
    private MediaPlayer mp_audio;

    int num_aleatorio = (int)(Math.random()*10); //Para generar num aleatorio para cambiar la imagen al ingresar a la app

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_nombre = (EditText)findViewById(R.id.txt_nombre);
        img_personaje = (ImageView)findViewById(R.id.img_personaje);
        txt_puntaje = (TextView)findViewById(R.id.txt_puntaje);

        getSupportActionBar().setDisplayShowHomeEnabled(true); //Para ver el icono
        getSupportActionBar().setIcon(R.mipmap.ic_launcher); //Para verl el icono en el actionbar

        int id_imagen;
        if(num_aleatorio == 0 || num_aleatorio == 10){
            id_imagen = getResources().getIdentifier("mango","drawable",getPackageName());
            img_personaje.setImageResource(id_imagen);
        } else if(num_aleatorio == 1 || num_aleatorio == 9){
            id_imagen = getResources().getIdentifier("fresa","drawable",getPackageName());
            img_personaje.setImageResource(id_imagen);
        } else if(num_aleatorio == 2 || num_aleatorio == 8){
            id_imagen = getResources().getIdentifier("manzana","drawable",getPackageName());
            img_personaje.setImageResource(id_imagen);
        } else if(num_aleatorio == 3 || num_aleatorio == 7){
            id_imagen = getResources().getIdentifier("sandia","drawable",getPackageName());
            img_personaje.setImageResource(id_imagen);
        } else if(num_aleatorio == 4 || num_aleatorio == 5 || num_aleatorio == 6){
            id_imagen = getResources().getIdentifier("mango","drawable",getPackageName());
            img_personaje.setImageResource(id_imagen);
        }

        AdminSQLite admin = new AdminSQLite(this,"BaseDatos",null,1);
        SQLiteDatabase BaseDatos = admin.getWritableDatabase();

        Cursor consulta = BaseDatos.rawQuery("select * from puntaje where puntaje = (select max(puntaje) from puntaje)",null);
        if(consulta.moveToFirst()){
            String temp_nombre = consulta.getString(0);
            String temp_puntaje = consulta.getString(1);
            txt_puntaje.setText("Record: " + temp_puntaje + " de "+ temp_nombre);
            BaseDatos.close();
        } else {
            BaseDatos.close();
        }

        mp_audio = MediaPlayer.create(this,R.raw.alphabet_song);
        mp_audio.start();
        mp_audio.setLooping(true);
    }

    public void jugar(View view){

        String nombre = txt_nombre.getText().toString();

        if(!nombre.equals("")){
            Intent siguiente = new Intent(this,PlayActivity.class);
            siguiente.putExtra("jugador",nombre);
            startActivity(siguiente);
            mp_audio.stop();
            mp_audio.release(); //Para liberar recursos al parar el audio
            finish(); //Finalizar la activity actual
        } else {
            Toast.makeText(this,"Debes colocar tu nombre",Toast.LENGTH_SHORT).show();

            txt_nombre.requestFocus();
            InputMethodManager input = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE); //Para llamar el teclado
            input.showSoftInput(txt_nombre,InputMethodManager.SHOW_IMPLICIT); //Para que se deba escribir en el txt de nombre
        }
    }

    @Override //Cada vez que se presione el boton back sobre escribe el metodo con las lineas siguientes
    public void onBackPressed(){

    }

}