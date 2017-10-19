/**
 * Copyright (C) 2016 NTT DOCOMO, INC. All Rights Reserved.
 */

package com.example.nttdocomo.android.LinkingBeaconDemo;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nttdocomo.android.sdaiflib.BeaconData;
import com.nttdocomo.android.sdaiflib.BeaconReceiverBase;
import com.nttdocomo.android.sdaiflib.BeaconScanner;
import com.nttdocomo.android.sdaiflib.Define;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * ビーコンスキャン開始/停止Fragment.
 */
public class BeaconScanFragment extends Activity

    implements OnClickListener {
    private static final int[] SERVICE_ID_LIST = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
    private static final String LOG_SEPARATOR = "\n========================\n";
    private static final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm:ss.SSS", Locale.JAPANESE);
    /**
     *  Beaconスキャン結果受信Receiver.
     */
    private class BeaconReceiver extends BeaconReceiverBase {
        @Override
        protected void onReceiveScanResult(BeaconData data) {
            StringBuilder sb = new StringBuilder();
            String[] distanceStr = getResources().getStringArray(R.array.distance);
            sb.append(LOG_SEPARATOR);
            sb.append("\nビーコンスキャン結果");
            sb.append("\n通知時刻: " + SDF.format(new Date()));
            sb.append("\n検出時刻: " + SDF.format(new Date(data.getTimestamp())));
            sb.append("\nベンダ識別子: " + data.getVendorId());
            sb.append("\n個別番号: " + data.getExtraId());
            sb.append("\nRSSI値: " + data.getRssi());
            sb.append("\nバージョン: " + data.getVersion());
            Integer distance = data.getDistance();
            sb.append("\n距離種別: " + distance);
            if (distance != null) {
                sb.append(" [");
                if (distance - 1 >= 0 && distance - 1 < distanceStr.length) {
                    sb.append(distanceStr[distance - 1]);
                } else {
                    sb.append("unknown");
                }
                sb.append("]");
            }
            sb.append("\nTxパワー: " + data.getTxPower());
            sb.append("\n温度(℃): " + data.getTemperature());
            sb.append("\n湿度(％): " + data.getHumidity());
            sb.append("\n気圧(hPa): " + data.getAtmosphericPressure());
            sb.append("\n電池残量低下(要充電)フラグ: " + data.getLowBattery());
            sb.append("\n電池残量(％): " + data.getBatteryPower());
            sb.append("\nボタン識別子: " + data.getButtonId());
            sb.append("\n開閉フラグ: "  + data.getOpenCloseSensor());
            sb.append("\n人感反応有無フラグ: "  + data.getHumanSensor());
            sb.append("\nRawデータ(ビーコンサービスID 8): "  + data.getRawData8());
            sb.append("\nRawデータ(ビーコンサービスID 9): "  + data.getRawData9());
            sb.append("\nRawデータ(ビーコンサービスID 10): " + data.getRawData10());
            sb.append("\nRawデータ(ビーコンサービスID 11): " + data.getRawData11());
            sb.append("\nRawデータ(ビーコンサービスID 12): " + data.getRawData12());
            sb.append("\nRawデータ(ビーコンサービスID 13): " + data.getRawData13());
            sb.append("\nRawデータ " + data.getRawData());
            sb.append('\n');
            mLogView.append(sb.toString());
            mScrollView.post(new Runnable() {
                public void run() { 
                    mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }

        /*
        * ビーコンスキャン結果/状態通知を受け取る
        * @param scanState：スキャン状態
        * @param detail：詳細コード
        * */
        @Override
        protected void onReceiveScanState(int scanState, int detail) {
            StringBuilder sb = new StringBuilder();
            String[] scanStateStr = getResources().getStringArray(R.array.scanState);
            String[] detailStr = getResources().getStringArray(R.array.detail);
            sb.append(LOG_SEPARATOR);
            sb.append("\nビーコンスキャン状態");
            sb.append("\n通知時刻: " + SDF.format(new Date()));
            sb.append("\nscanState: " + scanState + " [");
            if (scanState >= 0 && scanState < scanStateStr.length) {
                sb.append(scanStateStr[scanState]);
            } else {
                sb.append("unknown");
            }
            sb.append("]");
            sb.append("\ndetail: " + detail + " [");
            if (detail >= 0 && detail < detailStr.length) {
                sb.append(detailStr[detail]);
            } else {
                sb.append("unknown");
            }
            sb.append("]");
            sb.append('\n');
            mLogView.append(sb.toString());
            mScrollView.post(new Runnable() {
                public void run() { 
                    mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }
    };

    /* Privte */
    private BeaconScanner mScanner;
    private BeaconReceiver mReceiver;
    private ScrollView mScrollView;
    private TextView mLogView;
    private List<CheckBox> mCheckBox;
    private RadioGroup mScanModeGroup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ビーコンスキャナー
        mScanner = new BeaconScanner(this);

        //ビーコンレシーバー
        mReceiver = new BeaconReceiver();

        //Filter
        IntentFilter filter = new IntentFilter();
        filter.addAction(Define.filterBeaconScanResult);
        filter.addAction(Define.filterBeaconScanState);

        //Receiver
        registerReceiver(mReceiver, filter);

        //View
        setContentView(R.layout.beacon_scan_list);
        createView();
    }

    /*
    * View生成
    * @param なし
    * */
    private void createView(){
        mScrollView = (ScrollView)findViewById(R.id.scroll_view);
        mLogView = (TextView)findViewById(R.id.text_log);
        LinearLayout checkBoxContainer1 = (LinearLayout) findViewById(R.id.check_box_container1);
        LinearLayout checkBoxContainer2 = (LinearLayout) findViewById(R.id.check_box_container2);
        LinearLayout checkBoxContainer3 = (LinearLayout) findViewById(R.id.check_box_container3);
        mCheckBox = new ArrayList<CheckBox>();
        for (int id : SERVICE_ID_LIST) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(Integer.toString(id));
            checkBox.setTag(id);
            switch (id / 6){
                case 0:
                    checkBoxContainer1.addView(checkBox, new ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
                    break;
                case 1:
                    checkBoxContainer2.addView(checkBox, new ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
                    break;
                case 2:
                    checkBoxContainer3.addView(checkBox, new ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
                    break;
            }
            mCheckBox.add(checkBox);
        }

        mScanModeGroup = (RadioGroup) findViewById(R.id.radio_group_scan_mode);

        //開始ボタン
        Button button = (Button)findViewById(R.id.btn_start);
        button.setOnClickListener(this);

        //終了ボタン
        button = (Button)findViewById(R.id.btn_stop);
        button.setOnClickListener(this);

        //クリアボタン
        button = (Button)findViewById(R.id.btn_clear);
        button.setOnClickListener(this);

    }

    /*
    * onDestroy
    * @param なし
    * */
    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    /*
    * onclick
    * @param view
    * */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                List<Integer> checkedIds = new ArrayList<Integer>();
                for (CheckBox checkBox : mCheckBox) {
                    if (checkBox.isChecked()) {
                        checkedIds.add((Integer) checkBox.getTag());
                    }
                }
                int[] serviceIds = new int[checkedIds.size()];
                for (int i = 0; i < serviceIds.length; i++) {
                    serviceIds[i] = checkedIds.get(i);
                }
                int scanModeId = mScanModeGroup.getCheckedRadioButtonId();
                switch (scanModeId) {
                    case R.id.radio_scan_mode_low_letency:
                        mScanner.startScan(serviceIds, 0);
                        break;
                    case R.id.radio_scan_mode_balanced:
                        mScanner.startScan(serviceIds, 1);
                        break;
                    case R.id.radio_scan_mode_low_power:
                        mScanner.startScan(serviceIds, 2);
                        break;
                    case R.id.radio_scan_mode_none:
                    default:
                        mScanner.startScan(serviceIds);
                        break;
                    }
                break;
            case R.id.btn_stop:
                mScanner.stopScan();
                break;
            case R.id.btn_clear:
                mLogView.setText(null);
                break;
            default:
                break;
        }
    }
}
