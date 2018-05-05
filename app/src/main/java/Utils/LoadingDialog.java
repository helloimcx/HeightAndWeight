package Utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.HomeworkOne.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Objects;

/**
 * Author: kafca
 * Date: 2018/5/5
 * Description: LoadingDialog
 */

public class LoadingDialog extends Dialog {

    private AVLoadingIndicatorView avi;
    private TextView messagetv;
    private RelativeLayout loadingbg;


    public LoadingDialog(Context context, int theme) {
        super(context, theme);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.loading_dialog);
        loadingbg = findViewById(R.id.loadingbg);
        avi = findViewById(R.id.avi);
        messagetv = findViewById(R.id.message);
    }

    public LoadingDialog setMessage(String message) {
        messagetv.setText(message);
        return this;
    }

    @Override
    public void show() {
        super.show();
        avi.smoothToShow();
    }

    public LoadingDialog setLoadingBg(int Colorbg) {
        loadingbg.setBackgroundColor(Colorbg);
        return this;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        avi.smoothToHide();
    }
}
