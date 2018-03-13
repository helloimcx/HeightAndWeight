package Utils;

/**
 * Created by mac on 2017/11/19.
 */

import java.util.ArrayList;

/**
 * {
 "count": 1,
 "next": null,
 "previous": null,
 "results": [
 {
 "moment_id": 2,
 "account_id": 17,
 "moment_content": "这是我的第一次分享...",
 "moment_url": "https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1511009236&di=e549945bbb23ca682722e4fdd5eb42a6&src=http://www.pp3.cn/uploads/201606/20160617016.jpg",
 "moment_time": "2017-11-18T20:50:38.860179",
 "is_public": 1,
 "account_name": "卡夫卡",
 "account_header": "http://ht-data.oss-cn-shenzhen.aliyuncs.com/31161dc322651e330802cbd401104507.jpg"
 }
 ]
 }
 */

public class JsonMomentBean {

    public static class MomentBean{
        public int moment_id;
        public int account_id;
        public String moment_content;
        public String moment_url;
        public String moment_time;
        public boolean is_public;
        public String account_name;
        public String account_header;

        public int getMoment_id(){
            return moment_id;
        }
        public int getAccount_id(){
            return account_id;
        }
        public String getMoment_content(){
            return moment_content;
        }
        public String getMoment_url(){
            return moment_url;
        }
        public String getMoment_time(){
            return moment_time;
        }
        public boolean getMomentIsPublic() {return is_public;}
        public String getAccount_name(){
            return account_name;
        }
        public String getAccount_header(){
            return account_header;
        }
    }

    public int count;
    public String next;
    public String previous;
    public ArrayList<MomentBean> results;

    public int getCount(){
        return count;
    }
    public String getNext(){
        return next;
    }
    public String getPrevious(){
        return previous;
    }
    public ArrayList<MomentBean> getResults(){
        return results;
    }
}
