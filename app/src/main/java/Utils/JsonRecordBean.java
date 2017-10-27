package Utils;

/**
 * Created by mac on 2017/10/17.
 */


import java.util.ArrayList;

/**
 * {
 "records": [
 {
 "record_id": 1,
 "record_time": "2017-10-22T00:00:00",
 "android_account_id": 14,
 "height": 180,
 "weight": 20
 },
 {
 "record_id": 6,
 "record_time": "2017-10-24T22:50:16.503558",
 "android_account_id": 14,
 "height": 78,
 "weight": 78
 }
 ],
 "count_record": 2
 }
 }
 */

public class JsonRecordBean {

    public static class RecordBean{
        public int record_id;
        public String record_time;
        public int android_account_id;
        public double height;
        public double weight;

        public int get_record_id(){
            return record_id;
        }
        public String get_record_time(){
            return record_time;
        }
        public int get_android_account_id(){
            return android_account_id;
        }
        public double get_height(){
            return height;
        }
        public double get_weight(){
            return weight;
        }
    }

    public ArrayList<RecordBean> records;
    public int count_record;

    public ArrayList<RecordBean> get_records(){
        return records;
    }

    public int get_count_record(){
        return count_record;
    }
}