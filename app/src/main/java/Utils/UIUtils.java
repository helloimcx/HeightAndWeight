package Utils;

import com.example.HomeworkOne.UserHeader;
import com.example.HomeworkOne.UserInfo;

/**
 * Created by mac on 2017/11/3.
 */

public class UIUtils {
    public static int dip2Px(int dip) {
        // px/dip = density;
        // density = dpi/160
        // 320*480 density = 1 1px = 1dp
        // 1280*720 density = 2 2px = 1dp
        float density = UserHeader.context.getResources().getDisplayMetrics().density;
        int px = (int) (dip * density + 0.5f);
        return px;
    }
}
