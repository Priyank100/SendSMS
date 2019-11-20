package com.priyank.sendsms.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.priyank.sendsms.Adapter.ContactListAdapter;
import com.priyank.sendsms.Adapter.GroupAdapter;
import com.priyank.sendsms.Constant.AppUtils;
import com.priyank.sendsms.Constant.SharedPreference;
import com.priyank.sendsms.Model.GroupModel;
import com.priyank.sendsms.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
//    EditText numberText;
//    Button sendBtn;

    String[] allPermissions;
    final int MULTIPLE_PERMISSIONS = 123;

//    SmsManager sms;

    FloatingActionButton addFabBtn;
    public TextView noGroupText;
    public RecyclerView recyclerView;
    ArrayList<GroupModel> grpNameList;
    GroupAdapter grpAdapter;

    ContactListAdapter clAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        numberText = findViewById(R.id.number_text);
//        sendBtn = findViewById(R.id.send_btn);
//        sendBtn.setEnabled(false);

        addFabBtn = findViewById(R.id.add_fab_btn);
        noGroupText = findViewById(R.id.no_group_text);
        recyclerView = findViewById(R.id.recycler_view);

        grpNameList = new ArrayList<>();

        allPermissions = new String[]{
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS};

        if (hasPermissions(MainActivity.this, allPermissions)) {
            //sendBtn.setEnabled(true);

        } else {
            ActivityCompat.requestPermissions(this, allPermissions, MULTIPLE_PERMISSIONS);
        }

        /*sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberText.getText().toString().trim().isEmpty()) {
                      AppUtils.Toast(MainActivity.this, "Enter Number");
                    return;
                }
                sendSMS(numberText.getText().toString().trim(), "Hello");
            }
        });*/

        if (SharedPreference.getPreferences(MainActivity.this).contains("GroupList")) {
            grpNameList = SharedPreference.getPreferenceList(MainActivity.this, "GroupList");
        }

        if (grpNameList.size() == 0) {
            noGroupText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noGroupText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        grpAdapter = new GroupAdapter(MainActivity.this, grpNameList);
        recyclerView.setAdapter(grpAdapter);

        addFabBtn.setOnClickListener(this);
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                             ActivityCompat.requestPermissions(MainActivity.this, allPermissions, MULTIPLE_PERMISSIONS);
                            return;
                        }
                    }
//                    sendBtn.setEnabled(true);
                    AppUtils.Toast(MainActivity.this, "Permission Allowed...");
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, allPermissions, MULTIPLE_PERMISSIONS);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_fab_btn:
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.add_group_layout);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

                final EditText groupName = dialog.findViewById(R.id.group_name);
                RecyclerView recyclerView2 = dialog.findViewById(R.id.recycler_view2);
                Button addBtn = dialog.findViewById(R.id.add_btn);

                recyclerView2.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                clAdapter = new ContactListAdapter(MainActivity.this, AppUtils.getContactList(MainActivity.this));
                recyclerView2.setAdapter(clAdapter);

                addBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (groupName.getText().toString().trim().isEmpty()) {
                            AppUtils.Toast(MainActivity.this, "Enter Group Name");
                            return;
                        }
                        if (clAdapter.getFilterList().isEmpty()) {
                            AppUtils.Toast(MainActivity.this, "Select Atleast 1 Contact");
                            return;
                        }

                        GroupModel gModel = new GroupModel();
                        gModel.setGroup(groupName.getText().toString().trim().toString());
                        gModel.setList(clAdapter.getFilterList());
                        grpNameList.add(gModel);
                        grpAdapter.notifyDataSetChanged();

                        SharedPreference.enterPreferenceList(MainActivity.this, "GroupList", grpNameList);
                        dialog.dismiss();
                        recreate();
                    }
                });

                dialog.show();
                break;
        }
    }

    /*public void sendSMS(String num, String msg) {
        String numbers[] = {num};

        sms = SmsManager.getDefault();

        for(String number : numbers) {
            sms.sendTextMessage(number, null, msg, null, null);
        }
    }*/
}
