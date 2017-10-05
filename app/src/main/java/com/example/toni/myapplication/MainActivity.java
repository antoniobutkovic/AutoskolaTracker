package com.example.toni.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Toni on 24.9.2017..
 */



public class MainActivity extends AppCompatActivity {

    private Button startBtn, addBtn, loadBtn;
    private StudentsDatabaseHandler studentsDatabaseHandler;
    private EditText nameEt, surnameEt;
    private Spinner studentsSpinner;
    private List<String> students;
    private Cursor cursor;
    private ArrayAdapter<String> dataAdapter;
    private CheckForInternetAndGPS checkForInternetAndGPS;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT){
            setContentView(R.layout.activity_main);
        }else{
            setContentView(R.layout.activity_main_land);
        }

        final Button startBtn = (Button) findViewById(R.id.startBtn);
        Button addBtn = (Button) findViewById(R.id.addBtn);
        final Button loadBtn = (Button) findViewById(R.id.loadBtn);
        final Spinner studentsSpinner = (Spinner) findViewById(R.id.studentsSpinner);
        studentsDatabaseHandler = new StudentsDatabaseHandler(this);
        cursor = studentsDatabaseHandler.getAllStudents();
        students = new ArrayList<String>();
        checkForInternetAndGPS = new CheckForInternetAndGPS();

        if (cursor.moveToFirst()){
            do{
                students.add(cursor.getString(1));
            }while(cursor.moveToNext());
        }

        dataAdapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_item, students);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        studentsSpinner.setAdapter(dataAdapter);

        if (checkForInternetAndGPS.isInternetConnected(MainActivity.this) == false){
            Toast.makeText(this, "Molim omogućite Internet", Toast.LENGTH_SHORT).show();
        }

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (studentsSpinner.getSelectedItem() == null) {
                    startBtn.setEnabled(false);
                    Toast.makeText(MainActivity.this, "Potrebno je dodati kandidata.", Toast.LENGTH_SHORT).show();
                }else{
                    if (checkForInternetAndGPS.isGPSEnabled(MainActivity.this) == true && checkForInternetAndGPS.isInternetConnected(MainActivity.this) == true){
                        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                        String studentName = studentsSpinner.getSelectedItem().toString();
                        Bundle bundle = new Bundle();
                        bundle.putString("studentName", studentName);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }else{
                        Toast.makeText(MainActivity.this, "Molim omogućite GPS i Internet.", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                view = getLayoutInflater().inflate(R.layout.custom_student_dialog, null);
                final EditText nameEt = (EditText) view.findViewById(R.id.nameEt);
                final EditText surnameEt = (EditText) view.findViewById(R.id.surnameEt);
                startBtn.setEnabled(true);
                loadBtn.setEnabled(true);
                builder.setNegativeButton("Poništi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.setPositiveButton("Dodaj kandidata", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String name = nameEt.getText().toString();
                        String surname = surnameEt.getText().toString();

                        if (name.trim().length() < 1 || surname.trim().length() < 1){
                            Toast.makeText(MainActivity.this, "Potrebno je popuniti sva polja.", Toast.LENGTH_SHORT).show();
                        }else{
                            studentsDatabaseHandler.addStudent(name + " " + surname);
                            students.add(name + " " + surname);
                            studentsSpinner.setAdapter(dataAdapter);
                        }
                    }
                });
                builder.setView(view);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        loadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (studentsSpinner.getSelectedItem() == null) {
                    loadBtn.setEnabled(false);
                    Toast.makeText(MainActivity.this, "Potrebno je dodati kandidata.", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(MainActivity.this, StudentInfoActivity.class);
                    String studentName = studentsSpinner.getSelectedItem().toString();
                    Bundle bundle = new Bundle();
                    bundle.putString("studentName", studentName);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_template, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        final Spinner studentsSpinner = (Spinner) findViewById(R.id.studentsSpinner);
        if (studentsSpinner.getSelectedItem() == null) {
            Toast.makeText(this, "Potrebno je dodati kandidata.", Toast.LENGTH_SHORT).show();
        }else{
            if (checkForInternetAndGPS.isInternetConnected(MainActivity.this) == true){
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(studentsSpinner.getSelectedItem().toString());
                databaseReference.removeValue();
                StudentsDatabaseHandler studentsDatabaseHandler = new StudentsDatabaseHandler(this);
                studentsDatabaseHandler.deleteStudent(studentsSpinner.getSelectedItem().toString());

                students.remove(studentsSpinner.getSelectedItemPosition());
                studentsSpinner.setAdapter(dataAdapter);
            }else {
                Toast.makeText(this, "Molim omogućite Internet", Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }
}
