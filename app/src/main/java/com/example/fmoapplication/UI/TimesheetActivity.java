package com.example.fmoapplication.UI;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.fmoapplication.Dialog.ALodingDialog;
import com.example.fmoapplication.Model.Data;
import com.example.fmoapplication.R;
import com.example.fmoapplication.Model.User;
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

public class TimesheetActivity extends AppCompatActivity {

    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;
    private TextView date_picker;
    private FirebaseFirestore db;
    private TextView time_Picker_from;
    private TextView time_Picker_to;
    private ConstraintLayout btn_done;
    EditText timesheet_info;
    Animation scaleUp, scaleDown;
    private ALodingDialog aLodingDialog;
    String name;
    boolean emailLogin = false;
    ImageView back;
    ArrayList<User> Userlist = new ArrayList<>();
    int hour1;
    int minute1;
    int hour2;
    int minute2;
    Toast toast;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Welcome_User = findViewById(R.id.Welcome_User);

        date_picker = findViewById(R.id.date_picker);
        time_Picker_from = findViewById(R.id.time_Picker_from);
        time_Picker_to = findViewById(R.id.time_Picker_to);
        back = findViewById(R.id.back);
        btn_done = findViewById(R.id.btn_done);
        db = FirebaseFirestore.getInstance();
        aLodingDialog = new ALodingDialog(this);
        timesheet_info = findViewById(R.id.timesheet_info);
        //Animation
        scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down);

        for (UserInfo user : FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            if (user.getProviderId().equals("password")) {
                System.out.println("User is signed in with email/password");
                emailLogin = true;
            }
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
                DatePickerDialog dialog = new DatePickerDialog(TimesheetActivity.this, R.style.TimePickerTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        System.out.println("Month" + i1);
                        i1 = i1 + 1;
                        String date = i2 + "/" + i1 + "/" + i;
                        date_picker.setText(date);

                    }
                }, year, month - 1, day);

                Calendar calendar = Calendar.getInstance();
                long currentTime = calendar.getTimeInMillis();
                dialog.getDatePicker().setMinDate(currentTime);

                // Set the maximum date to tomorrow's date
                calendar.add(Calendar.DAY_OF_MONTH, 3);
                long tomorrowTime = calendar.getTimeInMillis();
                dialog.getDatePicker().setMaxDate(tomorrowTime);
                dialog.show();
            }
        });
        Calendar calendar1 = Calendar.getInstance();

        SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm aa");
        // time_Picker_from.setText(sdf1.format(calendar1.getTime()));


        time_Picker_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_Picker_from.setError(null);
                TimePickerDialog timePickerDialog = new TimePickerDialog(TimesheetActivity.this, R.style.TimePickerTheme, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        calendar1.set(0, 0, 0, i, i1);
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            hour1 = timePicker.getHour();
                        }
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            minute1 = timePicker.getMinute();
                        }
                        time_Picker_from.setText(sdf.format(calendar1.getTime()));
                    }
                }, 12, 0, false);
                timePickerDialog.show();
            }
        });


        time_Picker_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_Picker_to.setError(null);
                TimePickerDialog timePickerDialog = new TimePickerDialog(TimesheetActivity.this, R.style.TimePickerTheme, new TimePickerDialog.OnTimeSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        hour2 = timePicker.getHour();
                        minute2 = timePicker.getMinute();
                        calendar1.set(0, 0, 0, i, i1);
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
                        time_Picker_to.setText(sdf.format(calendar1.getTime()));
                    }
                }, 12, 0, false);
                timePickerDialog.show();
            }
        });

        GoogleSignInAccount account = null;
        if (emailLogin == false) {
            googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
            googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
            account = GoogleSignIn.getLastSignedInAccount(this);

            if (account != null) {
                name = account.getDisplayName();
                String namearr[] = name.split(" ");
               // Welcome_User.setText(namearr[0] + " !");
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
                              //  Welcome_User.setText(namearr[0] + " !");
                            }

                        }


                    } else {
                        Toast.makeText(TimesheetActivity.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println(e.getMessage());
                    Toast.makeText(TimesheetActivity.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
                }
            });
        }


        // getting our instance
        // from Firebase Firestore.


        boolean finalEmailLogin = emailLogin;

        GoogleSignInAccount finalAccount = account;

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.out.println("OnClick -> Submit");
                if (!isConnected(TimesheetActivity.this)) {
                    showCustomeDialog();
                } else {
                    if (time_Picker_from.getText().equals("Time - From")) {

                        time_Picker_from.setError("Please Select Time From");

                    } else if (time_Picker_to.getText().equals("Time - To")) {
                        time_Picker_to.setError("Please Select Time To");

                    } else {
                        if (!(hour1 < hour2 || (hour1 == hour2 && minute1 < minute2))) {
                            displayToast("Please Select Time To After Time From", TimesheetActivity.this);
                            //   Toast.makeText(TimesheetActivity.this, "Please Select Time To After Time From", Toast.LENGTH_SHORT).show();
                            time_Picker_to.setError("Please Select Time-To After Time-From");
                        } else {
                            System.out.println("ELSE -> Submit");
                            aLodingDialog.show();

                            String uid = "";
                            User user = new User();
                            String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            if (finalEmailLogin == false) {

                                uid = finalAccount.getId();
                                String firstName = finalAccount.getDisplayName();
                                //  System.out.println("name" + user.getName());

                                String date = date_picker.getText().toString();
                                String time_from = time_Picker_from.getText().toString();
                                String time_to = time_Picker_to.getText().toString();
                                String timesheet_info_str = timesheet_info.getText().toString().trim();
                                addDataToFirestore(Uid, firstName, date, time_from, time_to, timesheet_info_str);

                            } else {
                                uid = Userlist.get(0).getUid();
                                String firstName = Userlist.get(0).getName();
                                System.out.println("name" + user.getName());

                                String date = date_picker.getText().toString();
                                String time_from = time_Picker_from.getText().toString();
                                String time_to = time_Picker_to.getText().toString();
                                String timesheet_info_str = timesheet_info.getText().toString().trim();
                                addDataToFirestore(Uid, firstName, date, time_from, time_to, timesheet_info_str);

                            }
                            System.out.println(uid);
                        }
                    }

                }
            }
        });

        //back
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    protected void onPause() {
        if(toast != null)
            toast.cancel();
        super.onPause();

    }

    private void addDataToFirestore(String uid, String nameFirst, String date, String time_from, String time_to, String timesheet_info_str) {


        Data data = new Data(uid, nameFirst, date, time_from, time_to, timesheet_info_str, true, 0);

        String docName = data.getDate();
        String docname[] = docName.split("/");
        String finalDocName = nameFirst + docname[0] + docname[1] + docname[2] + time_from;


        db.collection("Data").document(finalDocName).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                aLodingDialog.cancel();
                time_Picker_from.setText("Time - From");
                time_Picker_to.setText("Time - To");
                Toast.makeText(TimesheetActivity.this, "Your Entry has been successfully saved", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(TimesheetActivity.this, HomeScreenDashboard.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                aLodingDialog.cancel();
                Toast.makeText(TimesheetActivity.this, "Fail to add data!! Please try again \n" + e, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showCustomeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TimesheetActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_no_internet, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialogView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // aLodingDialog.show();
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                dialog.dismiss();


            }
        });
        dialogView.findViewById(R.id.Cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog.show();
    }

    private boolean isConnected(TimesheetActivity signInActivity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) signInActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected())) {
            return true;
        } else {
            return false;
        }


    }

    public void displayToast(String message, Context context) {
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.show();
    }

}