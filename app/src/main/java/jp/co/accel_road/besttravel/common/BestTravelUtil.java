package jp.co.accel_road.besttravel.common;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * 共通処理を行うユーティリティ
 *
 * Created by masato on 2015/12/30.
 */
public class BestTravelUtil {
    /**
     * 日付の文字列から日付を取得する。
     *
     * @param dateTimeStr
     * @return
     */
    public static Date convertStringToDate (String dateTimeStr) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日");
        if (dateTimeStr == null || "".equals(dateTimeStr)) {
            return null;
        }
        try {
            return sdf.parse(dateTimeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //変換に失敗した場合
        return null;
    }

    /**
     * 日付から日付の文字列を取得する。
     *
     * @param date
     * @return
     */
    public static String convertDateToString (Date date) {

        if (date == null || "".equals(date)) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日");
        return sdf.format(date);
    }

    /**
     * 日付から年月の文字列を取得する。
     *
     * @param date
     * @return
     */
    public static String convertDateToStringMonthDate (Date date) {

        if (date == null || "".equals(date)) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("M月d日");
        return sdf.format(date);
    }

    /**
     * 日付から時間の文字列を取得する。
     *
     * @param date
     * @return
     */
    public static String convertDateToStringTime (Date date) {

        if (date == null || "".equals(date)) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(date);
    }

    /**
     * 日付から画面表示用の日付の文字列を取得する。
     *
     * @param date
     * @return
     */
    public static String convertDateToStringDispDate (Date date) {

        if (date == null || "".equals(date)) {
            return null;
        }
        //カレンダークラスを取得
        Calendar paramDateCalendar = Calendar.getInstance();
        paramDateCalendar.setTime(date);

        //現在の日時のカレンダークラスを取得
        Calendar nowDateCalendar = Calendar.getInstance();

        //年が異なる場合
        SimpleDateFormat sdf = null;
        if (paramDateCalendar.get(Calendar.YEAR) != nowDateCalendar.get(Calendar.YEAR)) {
            sdf = new SimpleDateFormat("yyyy年M月d日");
        } else {
            sdf = new SimpleDateFormat("M月d日");
        }

        return sdf.format(date);
    }

    /**
     * 日付から画面表示用の日時の文字列を取得する。
     *
     * @param date
     * @return
     */
    public static String convertDateToStringDispDateTime (Date date) {

        if (date == null || "".equals(date)) {
            return null;
        }
        //カレンダークラスを取得
        Calendar paramDateCalendar = Calendar.getInstance();
        paramDateCalendar.setTime(date);

        //現在の日時のカレンダークラスを取得
        Calendar nowDateCalendar = Calendar.getInstance();

        //年が異なる場合
        SimpleDateFormat sdf = null;
        if (paramDateCalendar.get(Calendar.YEAR) != nowDateCalendar.get(Calendar.YEAR)) {
            sdf = new SimpleDateFormat("yyyy年M月d日 HH:mm");
        } else if (paramDateCalendar.get(Calendar.DAY_OF_YEAR) != nowDateCalendar.get(Calendar.DAY_OF_YEAR)) {
            sdf = new SimpleDateFormat("M月d日 HH:mm");
        } else {
            sdf = new SimpleDateFormat("HH:mm");
        }

        return sdf.format(date);
    }

    /**
     * 日付からJSON用の日付の文字列を取得する。
     *
     * @param date
     * @return
     */
    public static String convertJsonDateToString (Date date) {

        if (date == null || "".equals(date)) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");
        return sdf.format(date);
    }

    /**
     * 指定した日数後の日付を取得する。
     *
     * @param date 基準となる日付
     * @param addDateCount 加算する日数
     * @return 加算後の日付
     */
    public static Date addDate(Date date, int addDateCount) {

        //カレンダークラスを取得
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, addDateCount);
        return calendar.getTime();
    }

    /**
     * URIよりファイルパスを取得する
     *
     * @param filePassUri ファイルのURI
     * @param contentResolver
     * @return ファイルのファイルパス
     */
    public static String getFilePath(Uri filePassUri, ContentResolver contentResolver) {

        //画像が設定されていない場合は、処理を終了する。
        if (filePassUri == null) {
            return null;
        }

        String path = null;

        String scheme = filePassUri.getScheme();
        if ("file".equals(scheme)) {
            path = filePassUri.getPath();
        } else if("content".equals(scheme)) {
            Cursor cursor = contentResolver.query(filePassUri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int columnIdx = cursor.getColumnIndex("_display_name");
                path = cursor.getString(columnIdx);
                cursor.close();
            }
        }
        return path;
    }

    /**
     * ファイルのURIより、送信するリクエスト情報を取得する
     *
     * @param filePassUri ファイルパスのURI
     * @param contentResolver
     * @return リクエスト情報
     */
    public static RequestBody getImageRequestBody(Uri filePassUri, ContentResolver contentResolver) {


        ByteArrayOutputStream bout = null;
        try {
            InputStream iStream = contentResolver.openInputStream(filePassUri);
            bout = new ByteArrayOutputStream();
            byte [] buffer = new byte[1024];
            while (true) {
                int len = iStream.read(buffer);
                if(len < 0) {
                    break;
                }
                bout.write(buffer, 0, len);
            }

            iStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return RequestBody.create(MediaType.parse("multipart/form-data"), bout.toByteArray());
    }

    /**
     * ドライブサイズを画面に表示するための文字列を取得する
     *
     * @param driveSize
     * @return
     */
    public static String getDispDriveSize(long driveSize) {

        //GB単位の場合
        if (driveSize >= 1000000000) {
            return String.valueOf(driveSize / 1000000000) + "GB";
        } else if (driveSize >= 1000000) {
            //MB単位の場合
            return String.valueOf(driveSize / 1000000) + "MB";
        }
        return String.valueOf(driveSize / 1000) + "KB";
    }

    /**
     * 生年月日から年齢を算出する。
     *
     * @param birthday 生年月日
     * @return 年齢
     */
    public static Integer getAge(Date birthday) {

        if (birthday == null) {
            return null;
        }

        // 誕生日の年月日を得るためCalendar型のインスタンス取得
        Calendar birthdayCal = Calendar.getInstance();
        birthdayCal.setTime(birthday);

        // 年齢計算日を得るためCalendar型のインスタンス取得
        Calendar theDayCal = Calendar.getInstance();
        theDayCal.setTime(new Date());
        // 計算日の年と誕生日の年の差を算出
        int yearDiff = theDayCal.get(Calendar.YEAR) - birthdayCal.get(Calendar.YEAR);
        // ただし誕生月・日より年齢計算月日が前であれば年齢は1歳少ない
        if (theDayCal.get(Calendar.MONTH) < birthdayCal.get(Calendar.MONTH)) {
            yearDiff--;
        } else if (theDayCal.get(Calendar.DAY_OF_MONTH) < birthdayCal.get(Calendar.DAY_OF_MONTH)) {
            yearDiff--;
        }
        return yearDiff;
    }
}
