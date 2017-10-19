package com.example.nttdocomo.android.LinkingBeaconDemo.beacon;

import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.nttdocomo.android.LinkingBeaconDemo.MainActivity;
import com.example.nttdocomo.android.LinkingBeaconDemo.Prefs;
import com.example.nttdocomo.android.LinkingBeaconDemo.R;
import com.nttdocomo.android.sdaiflib.BeaconData;
import com.nttdocomo.android.sdaiflib.BeaconReceiverBase;
import com.nttdocomo.android.sdaiflib.BeaconScanner;
import com.nttdocomo.android.sdaiflib.Define;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * ビーコン（スキャン、データ）処理
 */
public class BeaconDemoActivity extends AppCompatActivity {

    private static final boolean DBG = BeaconConst.DBG;
    private static final String TAG = "BeaconDemoActivity";

    private int serviceId;
    private BeaconScanner scanner;
    private EditText filterEdit;
    private Button filterButton;
    private Button timeFormatButton;
    private ListView listView;
    private ArrayList<String> list;
    private ArrayAdapter<String> adapter;
    private String filterId = "";
    private Handler mHandler;

    private boolean isFiltering = false;
    private boolean isLinkingTimeFormat = false;

    /**
     * onCreate
     * @param savedInstanceState:Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_demo);

        listView = (ListView) findViewById(R.id.scan_result);
        list = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        filterEdit = (EditText) findViewById(R.id.filtering_edit);
        filterButton = (Button) findViewById(R.id.filtering_button);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFiltering) {
                    isFiltering = false;
                    changeFilterLayout();
                } else {
                    filterId = filterEdit.getText().toString();
                    if (!filterId.equals("")) {
                        isFiltering = true;
                        changeFilterLayout();
                        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        manager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    } else {
                        Toast.makeText(BeaconDemoActivity.this, getResources().getString(R.string.check_filter), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        timeFormatButton = (Button) findViewById(R.id.time_button);
        timeFormatButton.setAllCaps(false);
        timeFormatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLinkingTimeFormat = !isLinkingTimeFormat;
                if(isLinkingTimeFormat) {
                    timeFormatButton.setText(getResources().getText(R.string.time_linking));
                } else {
                    timeFormatButton.setText(getResources().getText(R.string.time_app));
                }
                list.clear();
                adapter.notifyDataSetChanged();
            }
        });

        scanner = new BeaconScanner(this);
        mHandler = new Handler();
    }

    /**
     * レジューム
     * @param なし
     */
    @Override
    public void onResume() {
        super.onResume();
        serviceId = getIntent().getIntExtra(BeaconConst.INTENT_EXTRA_SERVICE_ID, BeaconConst.SERVICE_ID_DEFAULT);
        startRequestBeacon(serviceId);
    }

    /**
     * ポーズ
     * @param なし
     */
    @Override
    public void onPause() {
        super.onPause();
        stopRequestBeacon();
    }

    /**
     * フィルタレイアウト更新
     * @param なし
     */
    private void changeFilterLayout() {
        filterButton.setTextColor(ContextCompat.getColor(this,
                isFiltering ? android.R.color.white : android.R.color.black));
        filterButton.setBackground(ContextCompat.getDrawable(this,
                isFiltering ? R.drawable.shape_corners_on : R.drawable.shape_corners_off));
    }

    /**
     * ビーコン取得要求をLinkingに送る
     * ビーコン受信用のReceiverに対してビーコンスキャン状態通知とビーコンスキャン結果通知のIntentFilterを設定する
     * @param serviceId 取得したいビーコンのserviceId
     */
    public void startRequestBeacon(int serviceId) {

        int[] request;

        if(serviceId == BeaconConst.SERVICE_ID_DEFAULT) {
            //ビーコンIDボタン押下時は全てのビーコンを取得するため全ＩＤを指定
            request = new int[]{
                    BeaconConst.SERVICE_ID_DEFAULT,
                    BeaconConst.SERVICE_ID_TEMP,
                    BeaconConst.SERVICE_ID_HUMID,
                    BeaconConst.SERVICE_ID_PRESSURE,
                    BeaconConst.SERVICE_ID_BATTERY,
                    BeaconConst.SERVICE_ID_BUTTON,
                    BeaconConst.SERVICE_ID_6,
                    BeaconConst.SERVICE_ID_7,
                    BeaconConst.SERVICE_ID_8,
                    BeaconConst.SERVICE_ID_9,
                    BeaconConst.SERVICE_ID_10,
                    BeaconConst.SERVICE_ID_11,
                    BeaconConst.SERVICE_ID_12,
                    BeaconConst.SERVICE_ID_13,
                    BeaconConst.SERVICE_ID_14,
                    BeaconConst.SERVICE_ID_RAW
            };
        } else {
            request = new int[] {serviceId};
        }

        // スキャンモード読み込み
        int scanMode = Prefs.loadBltAccuracy(getApplicationContext());

        IntentFilter filter = new IntentFilter();
        filter.addAction(Define.filterBeaconScanResult); //ビーコン結果通知
        filter.addAction(Define.filterBeaconScanState);  //ビーコンスキャン状態通知
        registerReceiver(receiver, filter);
        scanner.startScan(request, scanMode);

    }

    /**
     * ビーコン取得終了
     * @param なし
     */
    public void stopRequestBeacon() {
        unregisterReceiver(receiver);
        scanner.stopScan();
    }

    /** ビーコンを検出時にLinkingから通知を受け取るためのBroadcastReceiver拡張クラス **/
    BeaconReceiverBase receiver = new BeaconReceiverBase() {
        private static final String LOG_FILE_NAME = "LinkingIFDemo_BeaconScanLog.csv";
        BeaconData receivedData;

        /**
         * Linkingからビーコン検出の通知を受信した場合の処理を行う
         * @param beacondata:ビーコンデータ
         */
        @Override
        protected void onReceiveScanResult(final BeaconData beaconData) {
            if(isFiltering && beaconData.getExtraId() != Integer.parseInt(filterId)) {
                //フィルタリング中
                return;
            }

            receivedData = beaconData;
            mHandler.postDelayed(listUpdateTask, 0);

        }

        /**
         * Linkingからビーコン検出の通知を受信した場合の処理を行う
         * @param scanState:スキャン状態
         * @param detail:詳細コード
         */
        @Override
        protected void onReceiveScanState(int scanState, int detail) {
            //TODO
            String state = "";

            if (scanState == 0) {
                if (detail == 0) {
                    state = "スキャン実行中";
                } else {
                    state = "エラーが発生しました：" + detail;
                }
            } else {
                if(detail == 0) {
                    state = "スキャン要求に失敗しました : " + detail;
                } else if (detail == 1) {
                    state = "スキャン要求がタイムアウトしました";
                }
            }
            Toast.makeText(BeaconDemoActivity.this, state, Toast.LENGTH_SHORT).show();
        }

        //ListViewの更新
        Runnable listUpdateTask = new Runnable() {

            @Override
            public void run() {

                //rawDataは、ビット列配列へ変更

                /** ビーコンに含まれているデータを取得 **/
                String log = BeaconConst.getLogFormat(serviceId, receivedData, isLinkingTimeFormat);

                if(log.equals("")) {
                    return;
                }
                if (list.size() == 0) {
                    list.add(log);
                } else if (list.size() == 1) {
                    list.add(list.get(0));
                    list.set(0, log);
                } else {
                    if (list.size() < 100) {
                        list.add(list.get(list.size() - 1));
                        for (int i = 1; i < list.size(); i++) {
                            list.set(list.size() - i, list.get(list.size() - i - 1));
                        }
                        list.set(0, log);
                    } else {
                        for (int i = 1; i < list.size() && i < 100; i++) {
                            list.set(list.size() - i, list.get(list.size() - i - 1));
                        }
                        list.set(0, log);
                    }
                }
                adapter.notifyDataSetChanged();
                /** ログ出力 **/
                if(MainActivity.checked == true) {
                    String exDir = Environment.getExternalStorageDirectory().toString();
                    String filePath = exDir + "/" + LOG_FILE_NAME;
                    File file = new File(filePath);
                    try {
                        /** ログ情報の改行を消去し、末尾に改行を足す **/
                        String logStr = log.replace("\n", "") + "\n";
                        FileOutputStream out = new FileOutputStream(file, true);
                        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, "SJIS"));
                        writer.append(logStr);
                        writer.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    };

}
