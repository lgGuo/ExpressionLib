package com.yctech.expressiondemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yctech.expressionlib.expression.ExpressionInputDialog;
import com.yctech.expressionlib.expression.SmileyParser;

public class MainActivity extends AppCompatActivity {

    private SmileyParser mParser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView textView = (TextView) findViewById(R.id.tv);
        final TextView textView2 = (TextView) findViewById(R.id.tv2);

        SmileyParser.init(this);
        mParser = SmileyParser.getInstance();

        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExpressionInputDialog expressionInputDialog=new ExpressionInputDialog(MainActivity.this);
                expressionInputDialog.showAtLocation();

                expressionInputDialog.setOnSendClickListener(new ExpressionInputDialog.OnSendClickListener() {
                    @Override
                    public void onSendClick(String content) {
                        textView.setText(mParser.addSmileySpans(content));
                    }
                });
            }
        });
    }
}
