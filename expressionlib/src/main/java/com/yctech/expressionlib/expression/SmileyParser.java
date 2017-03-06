package com.yctech.expressionlib.expression;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;

import com.yctech.expressionlib.R;
import com.yctech.expressionlib.util.SystemUtil;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式转换条目中图文混排的工具类
 *
 * @author  glg
 * @data 2014-12-19
 */

public class SmileyParser {
    /*
     * 单例模式 1文字资源，图片资源 2.使用正则表达式进行匹配文字 3.把edittext当中整体的内容匹配正则表达式一次
     * 4.SpannableStringBuilder 进行替换
     */
    private static SmileyParser sInstance;

    public static SmileyParser getInstance() {
        return sInstance;
    }

    public static void init(Context context) {
        sInstance = new SmileyParser(context);
    }

    private final Context mContext;
    public  String[] arrTextEMoji;
    public  String[] arrTextCaiCai;

    // 正则表达式
    private final Pattern mPattern;

    // String 图片字符串 Integer表情
    private final HashMap<String, Integer> mSmileyToRes;

    // arrays里面的表情内容
    public static final int EMOJI_SMILEY_TEXTS =  R.array.custom_smiley_texts;
    public static final int CAICAI_SMILEY_TEXTS =  R.array.caicai_smiley_texts;


    private SmileyParser(Context context) {
        mContext = context;
        // 获取表情文字资源
        arrTextEMoji = mContext.getResources().getStringArray(EMOJI_SMILEY_TEXTS);
        arrTextCaiCai = mContext.getResources().getStringArray(CAICAI_SMILEY_TEXTS);

        // 获取表情ID与表情图标的Map
        mSmileyToRes = buildSmileyToRes();
        // 获取构建的正则表达式
        mPattern = buildPattern();
    }



    /**
     * 第一类所有表情的资源
     */
    public static final int[] EMOJI_SMILEY_RES_IDS={
            R.mipmap.custo_000, R.mipmap.custo_001, R.mipmap.custo_002,R.mipmap.custo_003, R.mipmap.custo_004,R.mipmap.custo_005, R.mipmap.custo_006,
            R.mipmap.custo_007, R.mipmap.custo_008,R.mipmap.custo_009,

            R.mipmap.custo_010, R.mipmap.custo_011,R.mipmap.custo_012,R.mipmap.custo_013,
            R.mipmap.custo_014,R.mipmap.custo_015,R.mipmap.custo_016,R.mipmap.custo_017, R.mipmap.custo_018,R.mipmap.custo_019,

            R.mipmap.custo_020,R.mipmap.custo_021, R.mipmap.custo_022,R.mipmap.custo_023,R.mipmap.custo_024,R.mipmap.custo_025, R.mipmap.custo_026,
            R.mipmap.custo_027, R.mipmap.custo_028, R.mipmap.custo_029,R.mipmap.custo_030,R.mipmap.custo_031,R.mipmap.custo_032,R.mipmap.custo_033,
            R.mipmap.custo_034,R.mipmap.custo_035, R.mipmap.custo_036,R.mipmap.custo_037,R.mipmap.custo_038, R.mipmap.custo_039,

            R.mipmap.custo_040, R.mipmap.custo_041,R.mipmap.custo_042,R.mipmap.custo_043,R.mipmap.custo_044,R.mipmap.custo_045,R.mipmap.custo_046,
            R.mipmap.custo_047,R.mipmap.custo_048, R.mipmap.custo_049,R.mipmap.custo_050,R.mipmap.custo_051,R.mipmap.custo_052,R.mipmap.custo_053,
            R.mipmap.custo_054,R.mipmap.custo_055,R.mipmap.custo_056,R.mipmap.custo_057,R.mipmap.custo_058,R.mipmap.custo_059,

            R.mipmap.custo_060, R.mipmap.custo_061,R.mipmap.custo_062,R.mipmap.custo_063,R.mipmap.custo_064,R.mipmap.custo_065,R.mipmap.custo_066,
            R.mipmap.custo_067,R.mipmap.custo_068, R.mipmap.custo_069,R.mipmap.custo_070,R.mipmap.custo_071,R.mipmap.custo_072,R.mipmap.custo_073,
            R.mipmap.custo_074,R.mipmap.custo_075,R.mipmap.custo_076,R.mipmap.custo_077,R.mipmap.custo_078,R.mipmap.custo_079,

            R.mipmap.custo_080, R.mipmap.custo_081,R.mipmap.custo_082,R.mipmap.custo_083,R.mipmap.custo_084,R.mipmap.custo_085,R.mipmap.custo_086,
            R.mipmap.custo_087,R.mipmap.custo_088, R.mipmap.custo_089,R.mipmap.custo_090,R.mipmap.custo_091,R.mipmap.custo_092,R.mipmap.custo_093,
            R.mipmap.custo_094,R.mipmap.custo_095,R.mipmap.custo_096,R.mipmap.custo_097,R.mipmap.custo_098,R.mipmap.custo_099
    };

    /**
     * 第2类所有表情的资源
     */
    public static final int[] CAICAI_SMILEY_RES_IDS={
            R.mipmap.em_caicai_01, R.mipmap.em_caicai_02,R.mipmap.em_caicai_03,R.mipmap.em_caicai_04, R.mipmap.em_caicai_05,
            R.mipmap.em_caicai_06, R.mipmap.em_caicai_07,
            R.mipmap.em_caicai_08, R.mipmap.em_caicai_09,R.mipmap.em_caicai_10,R.mipmap.em_caicai_11, R.mipmap.em_caicai_12,R.mipmap.em_caicai_13,

            R.mipmap.em_caicai_14,
            R.mipmap.em_caicai_15,R.mipmap.em_caicai_16,R.mipmap.em_caicai_17,R.mipmap.em_caicai_18, R.mipmap.em_caicai_19,R.mipmap.em_caicai_20,

            R.mipmap.em_caicai_21,R.mipmap.em_caicai_22, R.mipmap.em_caicai_23,R.mipmap.em_caicai_24,R.mipmap.em_caicai_25,R.mipmap.em_caicai_26,
            R.mipmap.em_caicai_27,

    };

    /**
     * 使用HashMap的key-value的形式来影射表情的ID和图片资源
     *
     * @return
     */
    private HashMap<String, Integer> buildSmileyToRes() {

        if (EMOJI_SMILEY_RES_IDS.length != arrTextEMoji.length||CAICAI_SMILEY_RES_IDS.length != arrTextCaiCai.length) {
            throw new IllegalStateException("ID和图片不匹配");
        }

        HashMap<String, Integer> smileyToRes = new HashMap<String, Integer>();

        for (int i = 0; i < arrTextEMoji.length; i++) {
            // 图片名称作为key值，图片资源ID作为value值
            smileyToRes.put(arrTextEMoji[i], EMOJI_SMILEY_RES_IDS[i]);
        }

        for (int i = 0; i < arrTextCaiCai.length; i++) {
            // 图片名称作为key值，图片资源ID作为value值
            smileyToRes.put(arrTextCaiCai[i], CAICAI_SMILEY_RES_IDS[i]);
        }


        return smileyToRes;
    }

    /**
     * 构建正则表达式,用来找到我们所要使用的图片
     *
     * @return
     */
    private Pattern buildPattern() {
        StringBuilder patternString = new StringBuilder((arrTextEMoji.length+ arrTextCaiCai.length)* 3);
        patternString.append('(');
        for (String s : arrTextEMoji) {
            patternString.append(Pattern.quote(s));
            patternString.append('|');
        }

        for (String s : arrTextCaiCai) {
            patternString.append(Pattern.quote(s));
            patternString.append('|');
        }
        patternString.replace(patternString.length() - 1,
                patternString.length(), ")");
        // 把String字符串编译成正则表达式()
        // ([调皮]|[调皮]|[调皮])
        return Pattern.compile(patternString.toString());
    }

    /**
     * 根据文本替换成图片
     *
     * @param text
     *            对应表情
     * @return 一个表示图片的序列
     */
    public CharSequence addSmileySpans(CharSequence text) {
        // 把文字替换为对应图片
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        // 判断提取工具类（按照正则表达式）
        Matcher matcher = mPattern.matcher(text);
        while (matcher.find()) {
            // 获取对应表情的图片id
            int resId = mSmileyToRes.get(matcher.group());
            // 替换制定字符
            builder.setSpan(new ImageSpan(mContext
                    ,resId), matcher.start(),
                    matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        }
        return builder;
    }


    /**
     * 根据文本替换成图片，int width, int height 可控制表情大小
     *
     * @param text
     *            对应表情
     * @return 一个表示图片的序列
     */
    public CharSequence addSmileySpansReSize(CharSequence text, int width, int height) {
        // 把文字替换为对应图片
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        // 判断提取工具类（按照正则表达式）
        Matcher matcher = mPattern.matcher(text);
        while (matcher.find()) {
            // 获取对应表情的图片id
            int resId = mSmileyToRes.get(matcher.group());
            // 替换制定字符
            builder.setSpan(new ImageSpan(mContext
                            ,decodeSampledBitmapFromResource(mContext.getResources(),resId,
                    SystemUtil.dp2px(mContext,width),SystemUtil.dp2px(mContext,height))), matcher.start(),
                    matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        }
        return builder;
    }



    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inDensity=res.getDisplayMetrics().densityDpi;
        BitmapFactory.decodeResource(res, resId, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }


}
