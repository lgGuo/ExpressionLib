package com.yctech.expressionlib.expression;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.yctech.expressionlib.R;
import com.yctech.expressionlib.util.KeyboardUtil;
import com.yctech.expressionlib.util.SystemUtil;
import com.yctech.expressionlib.wiget.FilterEmoJiEditText;
import com.yctech.expressionlib.wiget.PanelLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by glg on 2017/3/3 0003.
 * 带自定义表情的的输入框,并屏蔽系统Emoji输入
 */

public class ExpressionInputDialog extends Dialog {

    private final RelativeLayout mExpressionMenuView;
    private final SmileyParser mParser;
    private Context mContext;
    private View vRootMenuRoot;
    private View vRootMenu;
    private Button vSendBtn;
    private FilterEmoJiEditText vEditTextContent;
    private Button vChoseExpressionBtn;
    private ViewPager vViewPager;
    private LinearLayout vLl_dots;
    private PanelLayout mPanelRoot;

    private int emojiPage;//表情有几页
    List<List<Integer>> emojiList=new ArrayList<>();//表情1的数据源
    List<List<Integer>> caicaiList=new ArrayList<>();//表情2的数据源

    List<List<String>> emojiTxtList=new ArrayList<>();//表情1的符号数据源
    List<List<String>> caicaiTxtList=new ArrayList<>();//表情2的符号数据源

    Map<Integer, List<List<Integer>>> expressionTypeList=new TreeMap<>();//表情总数据源,以图标为key
    int[] expressionIconArray ={R.mipmap.custom_emoji,R.mipmap.caicai_emoji};//不同类型自定义表情图标资源
    private LinearLayout vChangeExpression;
    private List<GridView> gridList = new ArrayList<>();
    private ExpressionAdapter mExpressionAdapter;

    public void setOnSendClickListener(OnSendClickListener onSendClickListener) {
        this.onSendClickListener = onSendClickListener;
    }

    private OnSendClickListener onSendClickListener;

    public ExpressionInputDialog(Context context) {
        super(context);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mExpressionMenuView = (RelativeLayout) inflater.inflate(R.layout.expression_dialog,null);

        SmileyParser.init(context);
        mParser = SmileyParser.getInstance();
        initViews();
        init();
    }

    public void showAtLocation() {
        setCanceledOnTouchOutside(true);
        vEditTextContent.setText("");
        vEditTextContent.setHint("");
        vEditTextContent.requestFocus();

        //延迟弹出，否则弹不出来
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                KeyboardUtil.showKeyboard(vEditTextContent);
            }
        },300);


        mPanelRoot.setVisibility(View.GONE);
        try {
            show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 初始化控件
     */
    private void initViews() {
        vRootMenu=mExpressionMenuView.findViewById(R.id.ll_expression_meanu);
        vRootMenuRoot=mExpressionMenuView.findViewById(R.id.rl_expression_meanu_root);
        vSendBtn = (Button) mExpressionMenuView.findViewById(R.id.btn_send);
        vEditTextContent = (FilterEmoJiEditText) mExpressionMenuView .findViewById(R.id.et_content);
        vChoseExpressionBtn = (Button) mExpressionMenuView.findViewById(R.id.btn_chose_expression);
        vViewPager=(ViewPager)mExpressionMenuView.findViewById(R.id.viwepager_expression);
        vLl_dots=(LinearLayout)mExpressionMenuView.findViewById(R.id.ll_dot_container);
        mPanelRoot=(PanelLayout)mExpressionMenuView.findViewById(R.id.panel_root);
        vChangeExpression=(LinearLayout)mExpressionMenuView.findViewById(R.id.select_expression_type_layout);



        //注册该layout监听，防止点击评论周围灰色区域将dialog给dismiss
        vRootMenuRoot.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                KeyboardUtil.hideKeyboard(vEditTextContent);
                mPanelRoot.setVisibility(View.GONE);
                dismiss();
            }
        });

        //注册该layout监听，防止点击评论周围灰色区域将dialog给dismiss
        vRootMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });

        //弹出或隐藏表情区域
        vChoseExpressionBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mPanelRoot.getVisibility() == View.VISIBLE) {
                    KeyboardUtil.hideKeyboard(vEditTextContent);
                    mPanelRoot.setVisibility(View.GONE);
                } else {
                    KeyboardUtil.hideKeyboard(vEditTextContent);
                    mPanelRoot.setVisibility(View.VISIBLE);
                }
            }
        });


        //点击输入框隐藏表情区域
        vEditTextContent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mPanelRoot.setVisibility(View.GONE);
            }
        });

        vEditTextContent.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_BACK) {
                    dismiss();
                }
                return false;
            }
        });

        vSendBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onSendClickListener.onSendClick(vEditTextContent.getText().toString());
            }
        });

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(mExpressionMenuView);
        this.getWindow().setBackgroundDrawableResource(R.color.transparent);
        this.getWindow().setLayout(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND | WindowManager.LayoutParams.FLAG_DIM_BEHIND);

    }

    private void init() {
        List<Integer> data1=new ArrayList<>();
        for (int i = 0; i < mParser.EMOJI_SMILEY_RES_IDS.length; i ++) {
            data1.add( mParser.EMOJI_SMILEY_RES_IDS[i]);
        }

        List<Integer> data2=new ArrayList<>();
        for (int i = 0; i < mParser.CAICAI_SMILEY_RES_IDS.length; i ++) {
            data2.add( mParser.CAICAI_SMILEY_RES_IDS[i]);
        }

        //把图片数据分页，每页20个表情一个删除键，三行
        emojiList = splitList(data1, 20);
        caicaiList = splitList(data2, 20);


        expressionTypeList.put(R.mipmap.caicai_emoji,caicaiList);
        expressionTypeList.put(R.mipmap.custom_emoji,emojiList);

        //把符号数据分页，每页最多20个表情，加上一个删除键
        emojiTxtList = splitStringList(Arrays.asList(mParser.arrTextEMoji), 20);
        caicaiTxtList = splitStringList(Arrays.asList(mParser.arrTextCaiCai), 20);


        initEmojiAdapter(emojiList.size(),R.mipmap.custom_emoji);


        for (int i=0;i<expressionIconArray.length;i++) {
            final int icon = expressionIconArray[i];
            ImageView imageView=new ImageView(mContext);
            imageView.setImageResource(icon);
            vChangeExpression.addView(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    initEmojiAdapter(expressionTypeList.get(icon).size(),icon);
                }
            });
        }

    }

    /**
     * 把lista按固定长度分割成若干list
     *
     * @param dataList
     * @param length 每个集合长度
     * @return
     */
    public static List<List<Integer>> splitList(List<Integer> dataList, int length) {
        List<List<Integer>> lists = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i = i + length) {
            int j = i + length;
            if (j > dataList.size()) {
                j = dataList.size();
            }

            List<Integer> insertList = dataList.subList(i, j);
            if (insertList.size() == 0) {
                break;
            }
            lists.add(insertList);
        }


        return lists;
    }

    /**
     * 把lista按固定长度分割成若干list
     *
     * @param dataList
     * @param length 每个集合长度
     * @return
     */
    public static List<List<String>> splitStringList(List<String> dataList, int length) {
        List<List<String>> lists = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i = i + length) {
            int j = i + length;
            if (j > dataList.size()) {
                j = dataList.size();
            }

            List<String> insertList = dataList.subList(i, j);
            if (insertList.size() == 0) {
                break;
            }
            lists.add(insertList);
        }


        return lists;
    }

    /**
     * 初始化表情资源
     */
    private void initEmojiAdapter(int emojiPage,int type) {
        gridList.clear();
        if (type==R.mipmap.custom_emoji) {
            for (int i = 0; i < emojiPage; i++) {
                GridView gridView = (GridView) getLayoutInflater().inflate(R.layout.gridview_emoji, null);
                final List<Integer> emojiResource = emojiList.get(i);
                final List<String> emojiResourceName = emojiTxtList.get(i);

                mExpressionAdapter = new ExpressionAdapter(getLayoutInflater(), emojiResource);
                gridView.setAdapter(mExpressionAdapter);
                //点击表情，将表情添加到输入框中。
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        if (position != emojiResource.size()) {
                            vEditTextContent.getText().insert(vEditTextContent.getSelectionStart(),
                                    mParser.addSmileySpans(emojiResourceName.get(position)));
                        } else {
                            int keyCode = KeyEvent.KEYCODE_DEL;
                            KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
                            vEditTextContent.dispatchKeyEvent(keyEvent);


                        }

                    }
                });
                gridList.add(gridView);
            }
        }else if (type==R.mipmap.caicai_emoji){

            for (int i = 0; i < emojiPage; i++) {
                GridView gridView = (GridView) getLayoutInflater().inflate(R.layout.gridview_emoji, null);
                final List<Integer> emojiResource = caicaiList.get(i);
                final List<String> emojiResourceName = caicaiTxtList.get(i);
                mExpressionAdapter = new ExpressionAdapter(getLayoutInflater(), emojiResource);
                gridView.setAdapter(mExpressionAdapter);
                //点击表情，将表情添加到输入框中。
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        if (position != emojiResource.size()) {
                            vEditTextContent.getText().insert(vEditTextContent.getSelectionStart(),
                                    mParser.addSmileySpansReSize((emojiResourceName.get(position)),20,20));
                        } else {
                            int keyCode = KeyEvent.KEYCODE_DEL;
                            KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
                            vEditTextContent.dispatchKeyEvent(keyEvent);


                        }

                    }
                });
                gridList.add(gridView);
            }


        }


        vViewPager.setAdapter(new EmojiAdapter(gridList));
        gotoInitData(gridList);
    }

    /**
     * 初始表情布局下底部圆点
     *
     * @param list
     */
    private void gotoInitData(List<GridView> list) {
        vLl_dots.removeAllViews();
        for (int i = 0; i < list.size(); i++) {
            ImageView imageView = new ImageView(mContext);
            if (i == 0) {
                imageView.setImageResource(R.drawable.shape_dot_select);

            } else {
                imageView.setImageResource(R.drawable.shape_dot_nomal);
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(SystemUtil.dp2px(mContext,8),
                    SystemUtil.dp2px(mContext,8));
            layoutParams.setMargins(20, 0, 0, 0);
            vLl_dots.addView(imageView, layoutParams);

        }

        if (vLl_dots.getChildCount() <= 1) {
            vLl_dots.setVisibility(View.GONE);
        } else {
            vLl_dots.setVisibility(View.VISIBLE);
        }

        vViewPager.setOffscreenPageLimit(6);
        vViewPager.setCurrentItem(0);

        vViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < vLl_dots.getChildCount(); i++) {
                    if (i != position) {
                        ((ImageView) vLl_dots.getChildAt(i)).setImageResource(R.drawable.shape_dot_nomal);
                    }
                }
                ((ImageView) vLl_dots.getChildAt(position)).setImageResource(R.drawable.shape_dot_select);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 表情适配器
     */
    private class EmojiAdapter extends PagerAdapter {
        private List<GridView> list;

        public EmojiAdapter(List<GridView> list) {
            this.list = list;
        }

        @Override
        public int getCount() {

            if (list != null && list.size() > 0) {
                return list.size();
            } else {
                return 0;
            }
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((GridView) object);
        }

        @Override
        public GridView instantiateItem(ViewGroup container, int position) {
            GridView GridView = list.get(position);
            container.addView(GridView);
            return GridView;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }


    public interface OnSendClickListener {
        void onSendClick(String content);
    }
}
