package com.example.nttdocomo.android.LinkingBeaconDemo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nttdocomo.android.LinkingBeaconDemo.beacon.BeaconConst;
import com.example.nttdocomo.android.LinkingBeaconDemo.beacon.BeaconDemoActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 各機能に画面遷移
 */
public class MainActivity extends AppCompatActivity {
    private static final int SID_9 = 9;
    private static final int SID_13 = 13;

    /** ビーコンデモ **/
    public  static  boolean checked;

    final Context context = this;
    private Button modeButton;
    private Button beaconIdButton;
    private Button tempButton;
    private Button humidButton;
    private Button pressureButton;
    private CheckBox checkboxButton;
    private EditText result;
    private Button batteryButton;
    private Button id5Button;
    private Button openCloseButton;
    private Button humanSensorButton;
    private Button moveSensorButton;
    private Button startEndButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon);

        /**
         *scanMode変更
         *0：高精度モード（スキャン実行間隔：3.0sスキャン / 2.0sインターバル）
         *1：通常モード（スキャン実行間隔：3.0sスキャン / 7.0sインターバル）
         *2：低精度(省電力)モード（スキャン実行間隔：3.0sスキャン / 17.0sインターバル）
         */
        modeButton = (Button) findViewById(R.id.beacon_mode_button);
        modeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items = {"高精度モード", "通常モード", "低精度(省電力)"};
                int defaultItem = Prefs.loadBltAccuracy(getApplicationContext());
                final List<Integer> checkedItems = new ArrayList<>();
                checkedItems.add(defaultItem);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("スキャンモード選択")
                        .setSingleChoiceItems(items, defaultItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                checkedItems.clear();
                                checkedItems.add(which);
                            }
                        })
                        .setPositiveButton("決定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!checkedItems.isEmpty()) {
                                    Prefs.saveBltAccuracy(getApplicationContext(), checkedItems.get(0));
                                }
                            }
                        })
                        .setNegativeButton("キャンセル", null)
                        .show();
            }
        });

        //ビーコンID
        beaconIdButton = (Button) findViewById(R.id.beacon_id_button);
        beaconIdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent beaconIntent = new Intent(MainActivity.this, BeaconDemoActivity.class);
                startActivity(beaconIntent);
            }
        });

        //気温
        tempButton = (Button) findViewById(R.id.temp_button);
        tempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent beaconIntent = new Intent(MainActivity.this, BeaconDemoActivity.class);
                beaconIntent.putExtra(BeaconConst.INTENT_EXTRA_SERVICE_ID, BeaconConst.SERVICE_ID_TEMP);
                startActivity(beaconIntent);
            }
        });

        //湿度
        humidButton = (Button) findViewById(R.id.humid_button);
        humidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent beaconIntent = new Intent(MainActivity.this, BeaconDemoActivity.class);
                beaconIntent.putExtra(BeaconConst.INTENT_EXTRA_SERVICE_ID, BeaconConst.SERVICE_ID_HUMID);
                startActivity(beaconIntent);
            }
        });

        //気圧
        pressureButton = (Button) findViewById(R.id.pressure_button);
        pressureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent beaconIntent = new Intent(MainActivity.this, BeaconDemoActivity.class);
                beaconIntent.putExtra(BeaconConst.INTENT_EXTRA_SERVICE_ID, BeaconConst.SERVICE_ID_PRESSURE);
                startActivity(beaconIntent);
            }
        });

        //電池残量
        batteryButton = (Button) findViewById(R.id.battery_button);
        batteryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent beaconIntent = new Intent(MainActivity.this, BeaconDemoActivity.class);
                beaconIntent.putExtra(BeaconConst.INTENT_EXTRA_SERVICE_ID, BeaconConst.SERVICE_ID_BATTERY);
                startActivity(beaconIntent);
            }
        });

        //ボタン識別子
        id5Button = (Button) findViewById(R.id.id5_button);
        id5Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent beaconIntent = new Intent(MainActivity.this, BeaconDemoActivity.class);
                beaconIntent.putExtra(BeaconConst.INTENT_EXTRA_SERVICE_ID, BeaconConst.SERVICE_ID_BUTTON);
                startActivity(beaconIntent);

            }
        });

        //開閉
        openCloseButton = (Button) findViewById(R.id.open_close_button);
        openCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent beaconIntent = new Intent(MainActivity.this, BeaconDemoActivity.class);
                beaconIntent.putExtra(BeaconConst.INTENT_EXTRA_SERVICE_ID, BeaconConst.SERVICE_ID_6);
                startActivity(beaconIntent);
            }
        });

        //人感
        humanSensorButton = (Button) findViewById(R.id.human_button);
        humanSensorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent beaconIntent = new Intent(MainActivity.this, BeaconDemoActivity.class);
                beaconIntent.putExtra(BeaconConst.INTENT_EXTRA_SERVICE_ID, BeaconConst.SERVICE_ID_7);
                startActivity(beaconIntent);
            }
        });

        //振動
        moveSensorButton = (Button) findViewById(R.id.move_button);
        moveSensorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent beaconIntent = new Intent(MainActivity.this, BeaconDemoActivity.class);
                beaconIntent.putExtra(BeaconConst.INTENT_EXTRA_SERVICE_ID, BeaconConst.SERVICE_ID_8);
                startActivity(beaconIntent);
            }
        });

        //汎用
        startEndButton = (Button) findViewById(R.id.beacon_start_end_button);
        startEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("MainActivity", "汎用ボタン");

                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.general, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);
                alertDialogBuilder.setView(promptsView);
                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        String serviceID = userInput.getText().toString();
                                        if(!serviceID.equals("")) {
                                            int sID = Integer.valueOf(serviceID);
                                            if (sID >= SID_9 && sID <= SID_13) {
                                                Intent beaconIntent = new Intent(MainActivity.this, BeaconDemoActivity.class);
                                                Integer.valueOf(serviceID);
                                                beaconIntent.putExtra(BeaconConst.INTENT_EXTRA_SERVICE_ID, Integer.valueOf(serviceID));
                                                startActivity(beaconIntent);
                                            } else {
                                                dialog.cancel();
                                                Toast.makeText(MainActivity.this, getResources().getString(R.string.check_sid), Toast.LENGTH_SHORT).show();
                                            }
                                        }else{
                                            dialog.cancel();
                                            Toast.makeText(MainActivity.this, getResources().getString(R.string.check_sid), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        //チェックボックス
        checkboxButton = (CheckBox) findViewById(R.id.beacon_checkbox);
        checkboxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checked = ((CheckBox) v).isChecked();
            }
        });
    }
}