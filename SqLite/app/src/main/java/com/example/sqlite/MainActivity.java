package com.example.sqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase bbdd;
    EditText editTextCodigo,editTextPersonName,editTextEdad,editTextRowid;
    TextView textViewSalida;

    bbddEmpresa conexion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextCodigo=findViewById(R.id.editTextCodigo);
        editTextPersonName=findViewById(R.id.editTextPersonName);
        editTextEdad=findViewById(R.id.editTextEdad);
        editTextRowid=findViewById(R.id.editTextRowid);
        textViewSalida=findViewById(R.id.textViewSalida);

        conexion=new bbddEmpresa(this,"bbddEmpresa",null,1);

        bbdd=conexion.getWritableDatabase();
    }

    public void insertarRegistro(View view){
        int codigo=Integer.parseInt(editTextCodigo.getText().toString());
        String nombrePersona=editTextPersonName.getText().toString();
        int edad=Integer.parseInt(editTextEdad.getText().toString());

        if (bbdd!=null){
            String sql=("INSERT INTO EMPLEADOS (CODIGO,NOMBRE,EDAD) VALUES (?,?,?)");

            SQLiteStatement statement =bbdd.compileStatement(sql);

            statement.clearBindings();

            statement.bindLong(1,codigo);
            statement.bindString(2,nombrePersona);
            statement.bindLong(3,edad);

            long rowId= statement.executeInsert();

            editTextRowid.setText(String.valueOf(rowId));
        }
    }

    public void eliminarRegistro(View view){
        int codigo=Integer.parseInt(editTextCodigo.getText().toString());

        if (bbdd!=null && !editTextCodigo.getText().toString().isEmpty()){
            String sql=("DELETE FROM EMPLEADOS WHERE (CODIGO=?)");

            SQLiteStatement statement=bbdd.compileStatement(sql);

            statement.clearBindings();

            statement.bindLong(1,codigo);

            statement.executeUpdateDelete();
        }
    }

    public void actualizarRegistro(View view){
        int codigo=Integer.parseInt(editTextCodigo.getText().toString());
        String nombrePersona=editTextPersonName.getText().toString();
        int edad=Integer.parseInt(editTextEdad.getText().toString());

        if (bbdd!=null){
            String sql=("UPDATE EMPLEADOS SET NOMBRE=?,EDAD=? WHERE(CODIGO=?)");

            SQLiteStatement statement=bbdd.compileStatement(sql);

            statement.clearBindings();

            statement.bindString(1,nombrePersona);
            statement.bindLong(2,edad);
            statement.bindLong(3,codigo);

            long rowId= statement.executeUpdateDelete();
        }
    }

    public void consultarRegistro(View view){
        String [] camposMostrar= new String[]{"codigo","nombre","edad","rowid"};
        String [] valoresWhere=new String[]{editTextCodigo.getText().toString()};

        if (bbdd!=null){
            Cursor c1= bbdd.query("Empleados",camposMostrar,"codigo=?",valoresWhere,null,null,null);

            if (c1.moveToFirst()){
                do {
                    editTextCodigo.setText(String.valueOf(c1.getInt(0)));
                    editTextPersonName.setText(c1.getString(1));
                    editTextEdad.setText(String.valueOf(c1.getInt(2)));
                    editTextRowid.setText(String.valueOf(c1.getInt(3)));
                }while (c1.moveToNext());
            }
        }
    }

    public void consultarTodosLosRegistros(View view){
        String [] camposMostrar= new String[]{"codigo","nombre","edad","rowid"};

        textViewSalida.setText("");

        if (bbdd!=null){
            Cursor c1= bbdd.query("Empleados",camposMostrar,null,null,null,null,null);

            if (c1.moveToFirst()){
                do {
                    textViewSalida.append(c1.getInt(0)+" "+c1.getString(1)+" "+c1.getInt(2)+"\n");
                }while (c1.moveToNext());
            }
        }
    }

    @SuppressLint("Range")
    public void consultarRegistroPorRowId(View view){
        String [] camposMostrar= new String[]{"codigo","nombre","edad","rowid"};
        String [] valoresWhere=new String[]{editTextRowid.getText().toString()};

        if (bbdd!=null){
            Cursor c1= bbdd.query("Empleados",camposMostrar,"rowid=?",valoresWhere,null,null,null);

            if (c1.moveToFirst()){
                do {
                    editTextCodigo.setText(String.valueOf(c1.getInt(c1.getColumnIndex("codigo"))));
                    editTextPersonName.setText(c1.getString(c1.getColumnIndex("nombre")));
                    editTextEdad.setText(String.valueOf(c1.getInt(c1.getColumnIndex("edad"))));
                    editTextRowid.setText(String.valueOf(c1.getInt(c1.getColumnIndex("rowid"))));
                }while (c1.moveToNext());
            }
        }
    }



    public void cerrarBBDD(View view){
        bbdd.close();
    }


}