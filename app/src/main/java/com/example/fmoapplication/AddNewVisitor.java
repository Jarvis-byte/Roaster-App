package com.example.fmoapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddNewVisitor extends AppCompatActivity {
    private FirebaseFirestore db;
    boolean emailLogin = false;
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;
    String name;
    ArrayList<User> Userlist = new ArrayList<>();
    TextView Welcome_User, date_picker, time_Picker_from, time_Picker_to, btn_done;
    EditText visitor_name, purpose_of_visit;
    private ALodingDialog aLodingDialog;
    Animation scaleUp, scaleDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_visitor);
        db = FirebaseFirestore.getInstance();
        visitor_name = findViewById(R.id.visitor_name);
        purpose_of_visit = findViewById(R.id.purpose_of_visit);
        date_picker = findViewById(R.id.date_picker);
        time_Picker_from = findViewById(R.id.time_Picker_from);
        time_Picker_to = findViewById(R.id.time_Picker_to);
        btn_done = findViewById(R.id.btn_done);
        Welcome_User = findViewById(R.id.Welcome_User);
        aLodingDialog = new ALodingDialog(this);
        btn_done = findViewById(R.id.btn_done);
        //Name Finding
        for (UserInfo user : FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            if (user.getProviderId().equals("password")) {
                System.out.println("User is signed in with email/password");
                emailLogin = true;
            }
        }
        GoogleSignInAccount account = null;
        if (emailLogin == false) {
            googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
            googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
            account = GoogleSignIn.getLastSignedInAccount(this);

            if (account != null) {
                name = account.getDisplayName();
                String namearr[] = name.split(" ");
                Welcome_User.setText(namearr[0] + " !");
            }

        } else {
            String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            System.out.println("UID ELSE" + Uid);
            db.collection("User").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // if the snapshot is not empty we are
                        // hiding our progress bar and adding
                        // our data in a list.

                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        System.out.println("LIST" + list);
                        for (DocumentSnapshot d : list) {
                            //  d.getId();
                            User user = d.toObject(User.class);
                            System.out.println("USER ID" + d.getId());
                            if (Uid.equals(d.getId())) {
                                System.out.println(user.getName());
                                name = user.getName();
                                Userlist.add(user);
                                String namearr[] = name.split(" ");
                                Welcome_User.setText(namearr[0] + " !");
                            }

                        }


                    } else {
                        Toast.makeText(AddNewVisitor.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println(e.getMessage());
                    Toast.makeText(AddNewVisitor.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
                }
            });
        }


        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH) + 1;
        System.out.println("Month before month" + month);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        date_picker.setText(day + "/" + month + "/" + year);

        date_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(AddNewVisitor.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        System.out.println("Month" + i1);
                        i1 = i1 + 1;
                        String date = i2 + "/" + i1 + "/" + i;
                        date_picker.setText(date);

                    }
                }, year, month - 1, day);

                dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                dialog.show();
            }
        });
        Calendar calendar1 = Calendar.getInstance();

        SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm aa");
        // time_Picker_from.setText(sdf1.format(calendar1.getTime()));


        time_Picker_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddNewVisitor.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {

                        calendar1.set(0, 0, 0, i, i1);
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
                        time_Picker_from.setText(sdf.format(calendar1.getTime()));
                    }
                }, 12, 0, false);
                timePickerDialog.show();
            }
        });


        time_Picker_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddNewVisitor.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {

                        calendar1.set(0, 0, 0, i, i1);
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
                        time_Picker_to.setText(sdf.format(calendar1.getTime()));
                    }
                }, 12, 0, false);
                timePickerDialog.show();
            }
        });
        boolean finalEmailLogin = emailLogin;
        GoogleSignInAccount finalAccount = account;
        scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down);
        btn_done.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                aLodingDialog.show();

                String uid = "";
                User user = new User();
                if (finalEmailLogin == false) {
                    uid = finalAccount.getId();
                    String firstName = finalAccount.getDisplayName();
                    //  System.out.println("name" + user.getName());
                    String namearr[] = firstName.split(" ");
                    String visitorName = visitor_name.getText().toString().trim();
                    String purposeOfvisit = purpose_of_visit.getText().toString().trim();
                    String date = date_picker.getText().toString();
                    String time_from = time_Picker_from.getText().toString();
                    String time_to = time_Picker_to.getText().toString();
                    addDataToFirestore(uid, namearr[0], visitorName, purposeOfvisit, date, time_from, time_to);
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        btn_done.startAnimation(scaleUp);
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        btn_done.startAnimation(scaleDown);
                    }

                } else {
                    uid = Userlist.get(0).getUid();
                    String firstName = Userlist.get(0).getName();
                    System.out.println("name" + user.getName());
                    String namearr[] = firstName.split(" ");
                    String visitorName = visitor_name.getText().toString().trim();
                    String purposeOfvisit = purpose_of_visit.getText().toString().trim();
                    String date = date_picker.getText().toString();
                    String time_from = time_Picker_from.getText().toString();
                    String time_to = time_Picker_to.getText().toString();
                    addDataToFirestore(uid, namearr[0], visitorName, purposeOfvisit, date, time_from, time_to);
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        btn_done.startAnimation(scaleUp);
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        btn_done.startAnimation(scaleDown);
                    }
                }


                return true;
            }
        });

    }

    private void addDataToFirestore(String uid, String nameFirst, String visitorName, String purposeOfvisit, String date, String time_from, String time_to) {


        AddVisitor addVisitor = new AddVisitor(uid, nameFirst, visitorName, purposeOfvisit, date, time_from, time_to);

        String docName = addVisitor.getDate();
        String docname[] = docName.split("/");
        String finalDocName = uid + visitorName + nameFirst;


        db.collection("Visitor Data").document(finalDocName).set(addVisitor).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                aLodingDialog.cancel();
                time_Picker_from.setText("Time - From");
                time_Picker_to.setText("Time - To");
                Toast.makeText(AddNewVisitor.this, "Your Data has been added", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                aLodingDialog.cancel();
                Toast.makeText(AddNewVisitor.this, "Fail to add data!! Please try again \n" + e, Toast.LENGTH_SHORT).show();
            }
        });

    }
}