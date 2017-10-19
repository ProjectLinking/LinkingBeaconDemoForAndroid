package com.example.nttdocomo.android.LinkingBeaconDemo.beacon;


import android.util.Log;

import com.nttdocomo.android.sdaiflib.BeaconData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** ビーコン受信データ解析 **/
public class BeaconConst {

    public static final boolean DBG = false;
    private static final String TAG = "BeaconConst";

    /** intentにサービスIDをセットする際のExtraキー **/
    public static final String INTENT_EXTRA_SERVICE_ID = "service_id";

    /**
     *各ビーコン情報に対応したビーコンサービスID
     */
    public static final int SERVICE_ID_DEFAULT = 0;     //なし（デバイスIDを取得）
    public static final int SERVICE_ID_TEMP = 1;        //気温を取得
    public static final int SERVICE_ID_HUMID = 2;       //湿度を取得
    public static final int SERVICE_ID_PRESSURE = 3;    //気圧を取得
    public static final int SERVICE_ID_BATTERY = 4;     //要充電フラグ、電池残量を取得
    public static final int SERVICE_ID_BUTTON = 5;      //押下されたボタンのIDを取得
    public static final int SERVICE_ID_6 = 6;           //Rawデータ（12bit）を取得
    public static final int SERVICE_ID_7 = 7;           //Rawデータ（12bit）を取得
    public static final int SERVICE_ID_8 = 8;           //Rawデータ（12bit）を取得
    public static final int SERVICE_ID_9 = 9;           //Rawデータ（12bit）を取得
    public static final int SERVICE_ID_10 = 10;         //Rawデータ（12bit）を取得
    public static final int SERVICE_ID_11 = 11;         //Rawデータ（12bit）を取得
    public static final int SERVICE_ID_12 = 12;         //Rawデータ（12bit）を取得
    public static final int SERVICE_ID_13 = 13;         //Rawデータ（12bit）を取得
    public static final int SERVICE_ID_14 = 14;         //Rawデータ（12bit）を取得
    public static final int SERVICE_ID_RAW = 15;        //Rawデータ（12bit）を取得

    /**
     * 検出されたビーコンから[時間＋デバイスID＋取得したい情報]に変換する
     * @param serviceId ビーコンリクエスト時のサービスID
     * @param data 検出したビーコン情報
     * @param linkingTimeFormat true : Linkingアプリ受信時刻を表示
     * @return フォーマット後のログ
     */
    public static String getLogFormat(int serviceId, BeaconData data, boolean linkingTimeFormat) {

        /** 取得したビーコンから各データを取得 **/
        if(DBG) Log.d(TAG,
                "ビーコン検出 "
                + "時間:" + data.getTimestamp()
                + " ベンダID:" + data.getVendorId()
                + " デバイス固有ID:" + data.getExtraId()
                + " RSSI:" + data.getRssi()
                + " バージョンId:" + data.getVersion()
                + " 距離:" + data.getDistance()
                + " TxPower:" + data.getTxPower()
                + " 温度:" + data.getTemperature()
                + " 湿度:" + data.getHumidity()
                + " 気圧:" + data.getAtmosphericPressure()
                + " 要充電フラグ:" + data.getLowBattery()
                + " 電力残量:" + data.getBatteryPower()
                + " ボタンID:" + data.getButtonId()
                + " rawデータ:" + data.getRawData()
                );

        String log = "";
        String time;
        if(linkingTimeFormat) {
            time = timeLogFormat(data.getTimestamp());
        } else {
            time = timeLogFormat(System.currentTimeMillis());
        }

        switch(serviceId) {
            case SERVICE_ID_DEFAULT:
                log = String.format("%s \nId[%d] RSSI[%d]", time, data.getExtraId(), data.getRssi());
                break;
            case SERVICE_ID_TEMP:       //気温　ID:1
                log = String.format("%s \nId[%d] 気温[%.02f]", time, data.getExtraId(), data.getTemperature());
                break;
            case SERVICE_ID_HUMID:      //湿度 ID:2
                log = String.format("%s \nId[%d] 湿度[%.02f]", time, data.getExtraId(), data.getHumidity());
                break;
            case SERVICE_ID_PRESSURE:   //気圧　ID:3
                log = String.format("%s \nId[%d] 気圧[%.02f]", time, data.getExtraId(), data.getAtmosphericPressure());
                break;
            case SERVICE_ID_BATTERY:    //要充電フラグ  ID:4
                Boolean lb = data.getLowBattery();
                Float bp = data.getBatteryPower();
                log = String.format("%s \nId[%d] 電池残量低下[%b] ,電池残量[%.02f]", time, data.getExtraId(), data.getLowBattery(),data.getBatteryPower());
                break;
            case SERVICE_ID_BUTTON:     // ボタン識別子 ID:5 押下されたボタンのID
                log = String.format("%s \nId[%d] ボタン識別子[%d]", time, data.getExtraId(), data.getButtonId());
                break;
            case SERVICE_ID_6:          // 開閉センサー ID:6
                Integer dataOpenCloseSensor = data.getOpenCloseSensor();
                if (dataOpenCloseSensor != null) {
                    //フォーマット変換　rawデータ(12ビット）を開閉状態・カウント値に分解する
                    String binary = String.format(
                            "%12s",
                            Integer.toBinaryString(dataOpenCloseSensor)).replaceAll(" ", "0");
                    String binaryState = binary.substring(0, 1);
                    String binaryCount = binary.substring(1);

                    String strTime= time;
                    String strid = String.valueOf(data.getExtraId());
                    log = strTime + " Id["+ strid +"]"+
                            "\nRAW["+  "開閉状態:" + binaryState
                            + " | カウント値:" + Integer.parseInt(binaryCount, 2) + "]";
                }
                break;
            case SERVICE_ID_7:          // 人感センサー ID:7
                Integer dataHumanSensor = data.getHumanSensor();
                if (dataHumanSensor != null) {
                    //フォーマット変換　rawデータ(12ビット）をbit列表示
                    String binary = Integer.toBinaryString(dataHumanSensor);
                    String strTime= time;
                    String strid = String.valueOf(data.getExtraId());
                    log = strTime + " Id["+ strid +"]"+ " RAW["+  binary + "]";
                }
                break;
            case SERVICE_ID_8:          // 振動センサ情報 ID:8
                Integer dataShockSensor = data.getRawData8();
                if (dataShockSensor != null) {
                    //フォーマット変換　rawデータ(12ビット）を感知フラグ・カウント値に分解する
                    String binary = String.format(
                            "%12s",
                            Integer.toBinaryString(dataShockSensor)).replaceAll(" ", "0");
                    String binaryState = binary.substring(0, 1);
                    String binaryCount = binary.substring(1);

                    String strTime = time;
                    String strid = String.valueOf(data.getExtraId());
                    log = strTime + " Id["+ strid +"]"+
                            "\nRAW["+  "感知フラグ:" + binaryState
                            + " | カウント値:" + Integer.parseInt(binaryCount, 2) + "]";
                }
                break;
            case SERVICE_ID_9:
                Integer rData9 = data.getRawData9();
                if (rData9 != null) {
                    //フォーマット変換　rawデータ(12ビット）をbit列表示
                    String binary = Integer.toBinaryString(rData9);
                    String strTime= time;
                    String strid = String.valueOf(data.getExtraId());
                    log = strTime + " Id["+ strid +"]"+ " RAW["+  binary + "]";
                }
                break;
            case SERVICE_ID_10:
                Integer rData10 = data.getRawData10();
                if (rData10 != null) {
                    //フォーマット変換　rawデータ(12ビット）をbit列表示
                    String binary = Integer.toBinaryString(rData10);
                    String strTime = time;
                    String strid = String.valueOf(data.getExtraId());
                    log = strTime + " Id["+ strid +"]"+ " RAW["+  binary + "]";
                }
                break;
            case SERVICE_ID_11:
                Integer rData11 = data.getRawData11();
                if (rData11 != null) {
                    //フォーマット変換　rawデータ(12ビット）をbit列表示
                    String binary = Integer.toBinaryString(rData11);
                    String strTime = time;
                    String strid = String.valueOf(data.getExtraId());
                    log = strTime + " Id["+ strid +"]"+ " RAW["+  binary + "]";
                }
                break;
            case SERVICE_ID_12:
                Integer rData12 = data.getRawData12();
                if (rData12 != null) {
                    //フォーマット変換　rawデータ(12ビット）をbit列表示
                    String binary = Integer.toBinaryString(rData12);
                    String strTime = time;
                    String strid = String.valueOf(data.getExtraId());
                    log = strTime + " Id["+ strid +"]"+ " RAW["+  binary + "]";
                }
                break;
            case SERVICE_ID_13:
                Integer rData13 = data.getRawData13();
                if (rData13 != null) {
                    //フォーマット変換　rawデータ(12ビット）をbit列表示
                    String binary = Integer.toBinaryString(rData13);
                    String strTime = time;
                    String strid = String.valueOf(data.getExtraId());
                    log = strTime + " Id["+ strid +"]"+ " RAW["+  binary + "]";
                }
                break;
            case SERVICE_ID_14:
            case SERVICE_ID_RAW:
                Integer rData = data.getRawData();
                if (rData != null) {
                    //フォーマット変換　rawデータ(12ビット）をbit列表示
                    String binary = Integer.toBinaryString(data.getRawData());
                    Integer str = data.getRawData();
                    String strTime = time;
                    String strid = String.valueOf(data.getExtraId());
                    log = strTime + " Id["+ strid +"]"+ " RAW["+  binary + "]";
                }
                break;
        }
        return log;
    }

    private static final SimpleDateFormat LOG_FORMAT = new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.getDefault());

    /**
     * ミリ秒表示で与えられた時間を見やすくフォーマットする
     * @param data System.currentTimeMillis()などで取得された値
     * @return 引数で与えられた時間を[MM-DD hh:mm:ss.SSS]に変換した文字列
     */
    public static String timeLogFormat(long data) {
        return LOG_FORMAT.format(new Date(data));
    }
}
