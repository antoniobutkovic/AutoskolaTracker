package com.example.toni.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Toni on 27.9.2017..
 */

public class StudentInfoActivity extends AppCompatActivity{

    private ArrayList<String> studentInfo;
    private ArrayList<String> rideNum;
    private DatabaseReference databaseReference;
    private String studentName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_info);
        studentInfo = new ArrayList<String>();
        rideNum = new ArrayList<String>();

        Bundle bundle = getIntent().getExtras();
        studentName = bundle.getString("studentName");

        databaseReference = FirebaseDatabase.getInstance().getReference().child(studentName);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()){

                    Students students = studentSnapshot.getValue(Students.class);
                    String rideInfo = students.getFullName() + "  " + "Broj vožnje: " + "\n" + students.getDate() + "\n" + students.getNotes();
                    studentInfo.add(rideInfo);
                    rideNum.add(students.getRideNum());

                }
                resetView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    private void resetView() {
        if(studentInfo.isEmpty()){
            setContentView(R.layout.blank_layout);
        }else{
            ListViewAdapter adapter = new ListViewAdapter(this, studentInfo, rideNum);
            ListView listView = (ListView) findViewById(R.id.listview);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    TextView rideNumTv = (TextView) view.findViewById(R.id.rideNumTv);
                    String rideNum = rideNumTv.getText().toString();
                    Intent intent = new Intent(StudentInfoActivity.this, RideInfoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("studentName", studentName);
                    bundle.putString("rideNum", rideNum);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            listView.setAdapter(adapter);
        }
    }

}
