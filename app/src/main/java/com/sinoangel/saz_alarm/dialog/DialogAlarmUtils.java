package com.sinoangel.saz_alarm.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.sinoangel.saz_alarm.R;


/**
 * Created by Z on 2016/12/19.
 */

public class DialogAlarmUtils {

    public static void showYNDialog(Context context, int id, final OnClickDialogLintener vol) {
        ImageView iv_ok, iv_close;
        TextView tv_text;
        final Dialog dialog = new Dialog(context, R.style.customDialog);
        dialog.setContentView(R.layout.dialog_alarm);

        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);

        iv_ok = (ImageView) dialog.findViewById(R.id.iv_ok);
        iv_close = (ImageView) dialog.findViewById(R.id.iv_close);
        tv_text = (TextView) dialog.findViewById(R.id.tv_text);
        tv_text.setText(id);

        iv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vol.onOk();
                dialog.dismiss();
            }
        });
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vol.onClose();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public interface OnClickDialogLintener {
        void onOk();

        void onClose();
    }
}
