package nju.androidchat.client.mvvm2;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import lombok.extern.java.Log;
import nju.androidchat.client.BR;
import nju.androidchat.client.R;
import nju.androidchat.client.Utils;
import nju.androidchat.client.mvvm2.model.ClientMessageObservable;
import nju.androidchat.client.mvvm2.viewmodel.RecallHandler;
import nju.androidchat.client.mvvm2.viewmodel.Mvvm2ViewModel;
import nju.androidchat.client.mvvm2.viewmodel.UiOperator;

@Log
public class Mvvm2TalkActivity extends AppCompatActivity implements TextView.OnEditorActionListener, UiOperator {
    private Mvvm2ViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new Mvvm2ViewModel(this);
        ViewDataBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main_mvvm2);
        binding.setVariable(BR.viewModel, viewModel);
    }


    @Override
    public void onBackPressed() {
        AsyncTask.execute(() -> {
            viewModel.disconnect();
        });

        Utils.jumpToHome(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            log.info("not on focus");
            return hideKeyboard();
        }
        return super.onTouchEvent(event);
    }

    private boolean hideKeyboard() {
        return Utils.hideKeyboard(this);
    }


    private void sendText() {
        viewModel.sendMessage();
    }

    public void onBtnSendClicked(View v) {
        hideKeyboard();
        sendText();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (Utils.send(actionId, event)) {
            hideKeyboard();
            // 异步地让Controller处理事件
            sendText();
        }
        return false;
    }

    @Override
    public void scrollListToBottom() {
        Utils.scrollListToBottom(this);
    }

    @Override
    public void showRecallUi(ClientMessageObservable messageObservable, RecallHandler recallHandler) {
        if(messageObservable.isSend()) {
            //    通过AlertDialog.Builder这个类来实例化我们的一个AlertDialog的对象
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //    设置Title的图标
            builder.setIcon(R.drawable.ic_launcher_background);
            //    设置Title的内容
            builder.setTitle("撤回消息提示");
            //    设置Content来显示一个信息
            builder.setMessage("确定撤回消息吗？");
            //    设置一个PositiveButton
            builder.setPositiveButton("确定", (dialog, which) -> {
                recallHandler.handleRecall(messageObservable);
                Toast.makeText(this, "撤回消息成功", Toast.LENGTH_SHORT).show();
            });
            //    设置一个NegativeButton
            builder.setNegativeButton("取消", (dialog, which) -> Toast.makeText(this, "已取消撤回操作", Toast.LENGTH_SHORT).show());
            //    显示出该对话框
            builder.show();
        }else {
            Toast.makeText(this, "不是您发出的消息，不能撤回", Toast.LENGTH_SHORT).show();
        }
    }
}
