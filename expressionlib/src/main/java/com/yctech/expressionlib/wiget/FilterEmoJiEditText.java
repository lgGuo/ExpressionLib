package com.yctech.expressionlib.wiget;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.Toast;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by glg on 2017/2/13 0013.
 * 过滤系统表情输入框
 */
public class FilterEmoJiEditText extends EditText {


    public FilterEmoJiEditText(Context context) {
        super(context);
    }

    public FilterEmoJiEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    public FilterEmoJiEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private void init(final Context context) {

        InputFilter[] filterArray = new InputFilter[1];
        InputFilter emojiFilter = new InputFilter( ) {

            Pattern emoji = Pattern. compile ("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                    Pattern. UNICODE_CASE | Pattern. CASE_INSENSITIVE ) ;

            @Override
            public CharSequence filter (CharSequence source , int start , int end , Spanned dest , int dstart , int dend ) {

                Matcher emojiMatcher = emoji . matcher ( source ) ;

                if ( emojiMatcher . find ( ) ) {

                    Toast.makeText(context,"非法字符",Toast.LENGTH_SHORT);
                    return "" ;

                }else {
                    return source ;
                }

            }
        } ;

        filterArray[0]=emojiFilter;

        this.setFilters(filterArray);
    }

}
