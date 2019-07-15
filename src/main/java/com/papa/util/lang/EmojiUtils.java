package com.papa.util.lang;

import com.vdurmont.emoji.EmojiParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author binfeng huang
 * @date 2018/4/27 12:38
 **/
public class EmojiUtils {

    private static Logger logger = LoggerFactory.getLogger(EmojiUtils.class);
    /**
     * @Description 将字符串中的emoji表情转换成可以在utf-8字符集数据库中保存的格式（表情占4个字节，需要utf8mb4字符集）
     * @param str
     * 待转换字符串
     * @return 转换后字符串
     * @throws UnsupportedEncodingException
     * exception
     */
    public static String emojiConvert(String str) {
        if(StringUtils.isEmpty(str)){
            return "";
        }
        String patternString = "([\\x{10000}-\\x{10ffff}\ud800-\udfff])";

        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while(matcher.find()) {
            try {
                matcher.appendReplacement(
                        sb,
                        "[["
                                + URLEncoder.encode(matcher.group(1),
                                "UTF-8") + "]]");
            } catch(UnsupportedEncodingException e) {
                logger.error("emojiConvert error", e);
            }
        }
        matcher.appendTail(sb);
        logger.debug("emojiConvert " + str + " to " + sb.toString()
                + ", len：" + sb.length());
        return sb.toString();
    }

    /**
     * @Description 还原utf8数据库中保存的含转换后emoji表情的字符串
     * @param str
     * 转换后的字符串
     * @return 转换前的字符串
     * @throws UnsupportedEncodingException
     * exception
     */
    public static String emojiRecovery(String str){
        if(StringUtils.isEmpty(str)){
            return "";
        }
        String patternString = "\\[\\[(.*?)\\]\\]";

        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(str);

        StringBuffer sb = new StringBuffer();
        while(matcher.find()) {
            try {
                matcher.appendReplacement(sb,
                        URLDecoder.decode(matcher.group(1), "UTF-8"));
            } catch(UnsupportedEncodingException e) {
                logger.error("emojiRecovery error", e);
            }
        }
        matcher.appendTail(sb);
        logger.debug("emojiRecovery " + str + " to " + sb.toString());
        return sb.toString();
    }

    /**
     * 获取字符串的unicode编码
     * 汉字“木”的Unicode 码点为Ox6728
     *
     * @param s 木
     * @return \ufeff\u6728  \ufeff控制字符 用来表示「字节次序标记（Byte Order Mark）」不占用宽度
     * 在java中一个char是采用unicode存储的 占用2个字节 比如 汉字木 就是 Ox6728 4bit+4bit+4bit+4bit=2字节
     */
    public static String stringToUnicode(String s) {
        try {
            StringBuffer out = new StringBuffer("");
            //直接获取字符串的unicode二进制
            byte[] bytes = s.getBytes("unicode");
            //然后将其byte转换成对应的16进制表示即可
            for (int i = 0; i < bytes.length - 1; i += 2) {
                out.append("\\u");
                String str = Integer.toHexString(bytes[i + 1] & 0xff);
                for (int j = str.length(); j < 2; j++) {
                    out.append("0");
                }
                String str1 = Integer.toHexString(bytes[i] & 0xff);
                out.append(str1);
                out.append(str);
            }
            return out.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String removeAllEmojis(String content){
        return EmojiParser.removeAllEmojis(content);
    }

    public static String removeAllSpecialWord(String content){
        return content.replaceAll("[^0-9a-zA-Z\u4e00-\u9fa5.，,。？“”]+","");
    }

    public static void main(String[] args) {

    }
}
