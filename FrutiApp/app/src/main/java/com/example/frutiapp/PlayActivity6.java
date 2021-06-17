package com.example.frutiapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PlayActivity6 extends AppCompatActivity {

    private EditText txt_respuesta;
    private TextView txt_jugador, txt_puntaje;
    private ImageView img_num1, img_num2, img_vidas, img_signo;
    private MediaPlayer mp_1, mp_great, mp_bad;

    int puntaje, numAleatorio1, numAleatorio2, resultado, vidas=3;
    String nombre_jugador, string_puntaje, string_vidas;
    String numero [] = {"cero","uno","dos","tres","cuatro","cinco","seis","siete","ocho","nueve"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play6);

        Toast.makeText(this,"Nivel 6 - Sumas, Restas y Multiplicaciones", Toast.LENGTH_SHORT).show();

        txt_respuesta = (EditText)findViewById(R.id.txt_respuesta);
        txt_jugador = (TextView)findViewById(R.id.txt_jugador);
        txt_puntaje = (TextView)findViewById(R.id.txt_puntuacion);
        img_num1 = (ImageView)findViewById(R.id.img_num1);
        img_num2 = (ImageView)findViewById(R.id.img_num2);
        img_vidas = (ImageView)findViewById(R.id.img_vidas);
        img_signo = (ImageView)findViewById(R.id.img_signo);

        nombre_jugador = getIntent().getStringExtra("jugador");
        txt_jugador.setText("Jugador: " + nombre_jugador);

        //Recuperar puntaje del nivel anterior
        string_puntaje = getIntent().getStringExtra("puntaje");
        puntaje = Integer.parseInt(string_puntaje);
        txt_puntaje.setText("Puntuación: " + puntaje);

        //Recuperar vidas del nivel anterior
        string_vidas = getIntent().getStringExtra("vidas");
        vidas = Integer.parseInt(string_vidas);
        if(vidas==3){
            img_vidas.setImageResource(R.drawable.tresvidas);
        }
        if(vidas==2){
            img_vidas.setImageResource(R.drawable.dosvidas);
        }
        if(vidas==1){
            img_vidas.setImageResource(R.drawable.unavida);
        }

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        mp_1 = MediaPlayer.create(this,R.raw.goats);
        mp_1.start();
        mp_1.setLooping(true);

        mp_great = MediaPlayer.create(this,R.raw.wonderful);
        mp_bad = MediaPlayer.create(this,R.raw.bad);

        numAleatorio();
    }

    public void comprobar(View view){
        String respuesta = txt_respuesta.getText().toString();

        if(!respuesta.equals("")){

            int respuesta_jugador = Integer.parseInt(respuesta);

            if(resultado == respuesta_jugador){
                mp_great.start();
                puntaje++;
                txt_puntaje.setText("Puntuación: " + puntaje);
                txt_respuesta.setText("");
                BaseDatos();
            } else {
                mp_bad.start();
                vidas--;
                BaseDatos();

                switch (vidas){
                    case 3:
                        img_vidas.setImageResource(R.drawable.tresvidas);
                        break;
                    case 2:
                        Toast.makeText(this,"Te quedan 2 vidas",Toast.LENGTH_SHORT).show();
                        img_vidas.setImageResource(R.drawable.dosvidas);
                        break;
                    case 1:
                        Toast.makeText(this,"Te queda 1 vida",Toast.LENGTH_SHORT).show();
                        img_vidas.setImageResource(R.drawable.unavida);
                        break;
                    case 0:
                        Toast.makeText(this,"Has perdido todas las vidas",Toast.LENGTH_SHORT).show();
                        Intent inicio = new Intent(this,MainActivity.class);
                        startActivity(inicio);
                        finish();
                        mp_1.stop();
                        mp_1.release();
                        break;
                }
                txt_respuesta.setText("");
            }
            numAleatorio();
        } else {
            Toast.makeText(this,"Escribe tu respuesta", Toast.LENGTH_SHORT).show();
        }
    }

    public void numAleatorio (){
        if(puntaje <= 200){
            numAleatorio1 = (int) (Math.random()*10);
            numAleatorio2 = (int) (Math.random()*10);

            if(numAleatorio1>=0 && numAleatorio1<=3){
                resultado=numAleatorio1+numAleatorio2;
                img_signo.setImageResource(R.drawable.adicion);
            } else if(numAleatorio1>=4 && numAleatorio1<=7) {
                resultado = numAleatorio1-numAleatorio2;
                img_signo.setImageResource(R.drawable.resta);
            } else {
                resultado = numAleatorio1*numAleatorio2;
                img_signo.setImageResource(R.drawable.multiplicacion);
            }

            if(resultado>=0) {
                for (int i = 0; i < numero.length; i++) {
                    int id = getResources().getIdentifier(numero[i], "drawable", getPackageName());
                    if (numAleatorio1 == i) {
                        img_num1.setImageResource(id);
                    }
                    if (numAleatorio2 == i) {
                        img_num2.setImageResource(id);
                    }
                }
            } else {
                numAleatorio();
            }

        } else {
            Intent siguiente = new Intent(this,MainActivity.class);

            Toast.makeText(this,"¡Eres un Genio!",Toast.LENGTH_LONG).show();

            startActivity(siguiente);
            finish();
            mp_1.stop();
            mp_1.release();
        }
    }

    public void BaseDatos(){
        AdminSQLite admin = new AdminSQLite(this,"BaseDatos",null,1);
        SQLiteDatabase BaseDatos = admin.getWritableDatabase();

        Cursor consulta = BaseDatos.rawQuery("select * from puntaje where puntaje = (select max(puntaje) from puntaje)",null);
        if(consulta.moveToFirst()){ //Para saber si se obtuvo algun registro de la consulta
            String temp_nombre = consulta.getString(0);
            String temp_puntaje = consulta.getString(1);

            int mejor_puntaje = Integer.parseInt(temp_puntaje);

            if(puntaje > mejor_puntaje){
                ContentValues modificacion = new ContentValues();
                modificacion.put("nombre",nombre_jugador);
                modificacion.put("puntaje",puntaje);

                BaseDatos.update("puntaje",modificacion,"puntaje="+mejor_puntaje,null);
            }
            BaseDatos.close();
        } else {
            ContentValues insertar = new ContentValues();
            insertar.put("nombre",nombre_jugador);
            insertar.put("puntaje",puntaje);

            BaseDatos.insert("puntaje",null,insertar);
            BaseDatos.close();
        }
    }

    @Override //Cada vez que se presione el boton back sobre escribe el metodo con las lineas siguientes
    public void onBackPressed(){

    }
}