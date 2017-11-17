package Utils;

/**
 * Created by mac on 2017/10/17.
 */


import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * {
 "id": 2,
 "email": "123@qq.com",
 "username": "qq",
 "sex": "M",
 "header"£º"http://test.png",
 "password": "123",
 "pic_set": []
 }
 */

public class JsonUserBean {
    public int id;
    public String email;
    public String username;
    public String sex;
    public String header;
    public ArrayList<String> pic_set;

    public int get_id(){
        return id;
    }

    public String get_email(){
        return email;
    }

    public String get_username(){
        return username;
    }

    public String get_sex(){
        return sex;
    }

    public String get_header(){
        return header;
    }

    public ArrayList<String> get_pic_set(){
        return pic_set;
    }
}