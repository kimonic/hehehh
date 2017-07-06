package com.diyou.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.diyou.activity.BeforeLoginActivity;
import com.diyou.application.MyApplication;
import com.diyou.config.Constants;
import com.diyou.config.UserConfig;
import com.example.encryptionpackages.AESencrypt;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.RequestParams;

import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.widget.EditText;

/**
 * 字符串工具类
 * 
 * @author Administrator
 * 
 */
public class StringUtils
{

    /**
     * 空字符串
     */
    public static final String STR_EMPTY = "";

    /**
     * 判断字符串是否为空
     * 
     * @param str
     * @return
     */
    public static boolean isEmpty(String str)
    {
        return str == null || "".equals(str.trim()) || "null".equals(str);
    }

    /**
     * 判断字符串是否为null,或者""、{}、[]
     * 
     * @param str
     * @return
     */
    public static boolean isEmpty2(String str)
    {
        return str == null || "".equals(str.trim()) || "{}".equals(str)
                || "[]".equals(str)|| "null".equals(str);
    }

    /**
     * 字符串转stream
     * 
     * @param str
     * @return
     */
    public static InputStream StringToInputStream(String str)
    {
        if (StringUtils.isEmpty(str))
            return null;
        ByteArrayInputStream stream = new ByteArrayInputStream(str.getBytes());
        return stream;
    }

    /**
     * URL编码转换
     * 
     * @param src
     * @return
     */
    public static String escape(String src)
    {
        int i;
        char j;
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length() * 6);
        for (i = 0; i < src.length(); i++)
        {
            j = src.charAt(i);
            if (Character.isDigit(j) || Character.isLowerCase(j)
                    || Character.isUpperCase(j))
                tmp.append(j);
            else if (j < 256)
            {
                tmp.append("%");
                if (j < 16)
                    tmp.append("0");
                tmp.append(Integer.toString(j, 16));
            }
            else
            {
                tmp.append("%u");
                tmp.append(Integer.toString(j, 16));
            }
        }
        return tmp.toString();
    }

    /**
     * URL解码转换
     * 
     * @param src
     * @return
     */
    public static String unescape(String src)
    {
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length());
        int lastPos = 0, pos = 0;
        char ch;
        while (lastPos < src.length())
        {
            pos = src.indexOf("%", lastPos);
            if (pos == lastPos)
            {
                if (src.charAt(pos + 1) == 'u')
                {
                    ch = (char) Integer
                            .parseInt(src.substring(pos + 2, pos + 6), 16);
                    tmp.append(ch);
                    lastPos = pos + 6;
                }
                else
                {
                    ch = (char) Integer
                            .parseInt(src.substring(pos + 1, pos + 3), 16);
                    tmp.append(ch);
                    lastPos = pos + 3;
                }
            }
            else
            {
                if (pos == -1)
                {
                    tmp.append(src.substring(lastPos));
                    lastPos = src.length();
                }
                else
                {
                    tmp.append(src.substring(lastPos, pos));
                    lastPos = pos;
                }
            }
        }
        return tmp.toString();
    }

    /**
     * 字符串替换
     * 
     * @param line
     * @param oldString
     * @param newString
     * @return
     */
    public static final String replace(String line, String oldString,
            String newString)
    {
        if (line == null)
        {
            return null;
        }
        int i = 0;
        if ((i = line.indexOf(oldString, i)) >= 0)
        {
            char[] line2 = line.toCharArray();
            char[] newString2 = newString.toCharArray();
            int oLength = oldString.length();
            StringBuffer buf = new StringBuffer(line2.length);
            buf.append(line2, 0, i).append(newString2);
            i += oLength;
            int j = i;
            while ((i = line.indexOf(oldString, i)) > 0)
            {
                buf.append(line2, j, i - j).append(newString2);
                i += oLength;
                j = i;
            }
            buf.append(line2, j, line2.length - j);
            return buf.toString();
        }
        return line;
    }

    /**
     * 根据body内容生成完整的HTML内容
     * 
     * @param bodyContent
     * @param encode
     *            默认为utf-8
     * @return
     */
    public static String genHtml(String bodyContent, String encode)
    {
        if (encode == null || "".equals(encode.trim()))
            encode = "utf-8";

        StringBuffer sb = new StringBuffer();
        sb.append("<html xmlns=http://www.w3.org/1999/xhtml>\n");
        sb.append("<head>\n");
        sb.append("<meta http-equiv='Content-Type' content='text/html; charset="
                + encode + "' />\n");
        sb.append("<title>查看消息</title>\n");
        sb.append("</head>\n");
        sb.append("\n");
        sb.append("<body>\n");
        sb.append(bodyContent);
        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
    }

    public static String removeHtml(String htmlStr)
    {
        String result = "";
        boolean flag = true;
        if (htmlStr == null || "".equals(htmlStr.trim()))
        {
            return "";
        }

        htmlStr = htmlStr.replace("\"", ""); // 去掉引号

        char[] a = htmlStr.toCharArray();
        int length = a.length;
        for (int i = 0; i < length; i++)
        {
            if (a[i] == '<')
            {
                flag = false;
                continue;
            }
            if (a[i] == '>')
            {
                flag = true;
                continue;
            }
            if (flag == true)
            {
                result += a[i];
            }
        }
        return result.toString();
    }

    /*************************************
     * Base64 编解码
     *********************************************/
    private static final char[] legalChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
            .toCharArray();

    /** Base64 encode the given data */
    public static String encode(byte[] data)
    {
        int start = 0;
        int len = data.length;
        StringBuffer buf = new StringBuffer(data.length * 3 / 2);

        int end = len - 3;
        int i = start;
        int n = 0;

        while (i <= end)
        {
            int d = ((((int) data[i]) & 0x0ff) << 16)
                    | ((((int) data[i + 1]) & 0x0ff) << 8)
                    | (((int) data[i + 2]) & 0x0ff);

            buf.append(legalChars[(d >> 18) & 63]);
            buf.append(legalChars[(d >> 12) & 63]);
            buf.append(legalChars[(d >> 6) & 63]);
            buf.append(legalChars[d & 63]);

            i += 3;

            if (n++ >= 14)
            {
                n = 0;
                buf.append(" ");
            }
        }

        if (i == start + len - 2)
        {
            int d = ((((int) data[i]) & 0x0ff) << 16)
                    | ((((int) data[i + 1]) & 255) << 8);

            buf.append(legalChars[(d >> 18) & 63]);
            buf.append(legalChars[(d >> 12) & 63]);
            buf.append(legalChars[(d >> 6) & 63]);
            buf.append("=");
        }
        else if (i == start + len - 1)
        {
            int d = (((int) data[i]) & 0x0ff) << 16;

            buf.append(legalChars[(d >> 18) & 63]);
            buf.append(legalChars[(d >> 12) & 63]);
            buf.append("==");
        }

        return buf.toString();
    }

    /**
     * Decodes the given Base64 encoded String to a new byte array. The byte
     * array holding the decoded data is returned.
     */
    public static byte[] decode(String s)
    {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try
        {
            decode(s, bos);
        }
        catch (IOException e)
        {
            throw new RuntimeException();
        }
        byte[] decodedBytes = bos.toByteArray();
        try
        {
            bos.close();
            bos = null;
        }
        catch (IOException ex)
        {
            System.err.println("Error while decoding BASE64: " + ex.toString());
        }
        return decodedBytes;
    }

    private static void decode(String s, OutputStream os) throws IOException
    {
        int i = 0;

        int len = s.length();

        while (true)
        {
            while (i < len && s.charAt(i) <= ' ')
                i++;

            if (i == len)
                break;

            int tri = (decode(s.charAt(i)) << 18)
                    + (decode(s.charAt(i + 1)) << 12)
                    + (decode(s.charAt(i + 2)) << 6)
                    + (decode(s.charAt(i + 3)));

            os.write((tri >> 16) & 255);
            if (s.charAt(i + 2) == '=')
                break;
            os.write((tri >> 8) & 255);
            if (s.charAt(i + 3) == '=')
                break;
            os.write(tri & 255);

            i += 4;
        }
    }

    private static int decode(char c)
    {
        if (c >= 'A' && c <= 'Z')
            return ((int) c) - 65;
        else if (c >= 'a' && c <= 'z')
            return ((int) c) - 97 + 26;
        else if (c >= '0' && c <= '9')
            return ((int) c) - 48 + 26 + 26;
        else
            switch (c)
            {
            case '+':
                return 62;
            case '/':
                return 63;
            case '=':
                return 0;
            default:
                throw new RuntimeException("unexpected code: " + c);
            }
    }

    /*************************************
     * 压缩、解压
     ****************************************/

    /**
     * 压缩方法
     * 
     * @param str
     * @return
     */
    public static byte[] compress(String str)
    {
        if (str == null)
            return null;
        byte[] compressed;
        ByteArrayOutputStream out = null;
        ZipOutputStream zout = null;
        try
        {
            out = new ByteArrayOutputStream();
            zout = new ZipOutputStream(out);
            zout.putNextEntry(new ZipEntry("0"));
            zout.write(str.getBytes());
            zout.closeEntry();
            compressed = out.toByteArray();
        }
        catch (IOException e)
        {
            compressed = null;
        }
        finally
        {
            if (zout != null)
            {
                try
                {
                    zout.close();
                }
                catch (IOException e)
                {
                }
            }
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (IOException e)
                {
                }
            }
        }
        return compressed;
    }

    /**
     * 
     * @param compressed
     * @return
     */
    public static final String decompress(byte[] compressed)
    {

        if (compressed == null)
            return null;

        ByteArrayOutputStream out = null;
        ByteArrayInputStream in = null;
        ZipInputStream zin = null;
        String decompressed;

        try
        {

            out = new ByteArrayOutputStream();
            in = new ByteArrayInputStream(compressed);
            zin = new ZipInputStream(in);
            byte[] buffer = new byte[1024];
            int offset = -1;

            while ((offset = zin.read(buffer)) != -1)
            {
                out.write(buffer, 0, offset);
            }
            decompressed = out.toString();
        }
        catch (IOException e)
        {
            decompressed = null;
        }
        finally
        {
            if (zin != null)
            {
                try
                {
                    zin.close();
                }
                catch (IOException e)
                {
                }
            }

            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException e)
                {
                }

            }

            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (IOException e)
                {
                }
            }
        }

        return decompressed;
    }

    /**
     * 检查是否符合手机号码格式
     * 
     * @param phoneNum
     * @return
     */
    public static boolean checkPhoneNum(String phoneNum)
    {
        // final String regx = "^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$";
        final String regx = "^1[3|4|5|7|8][0-9]\\d{8}$";

        Pattern p = Pattern.compile(regx);
        Matcher m = p.matcher(phoneNum);

        return m.matches();
    }

    public static boolean checkChinese(String name)
    {

        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        char[] cTemp = name.toCharArray();
        for (int i = 0; i < name.length(); i++)
        {
            Matcher m = p.matcher(cTemp[i] + "");
            if (!m.matches())
            {
                return false;
            }
        }
        return true;

    }

    /**
     * 验证Email
     * 
     * @param email
     *            email地址，格式：zhangsan@sina.com，zhangsan@xxx.com.cn，xxx代表邮件服务商
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkEmail(String email)
    {
        String regex = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]+@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        return Pattern.matches(regex, email);
    }

    /**
     * 验证身份证号码
     * 
     * @param idCard
     *            居民身份证号码15位或18位，最后一位可能是数字或字母
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkIdCard(String idCard)
    {
        String regex = "[1-9]\\d{13,16}[a-zA-Z0-9]{1}";
        return Pattern.matches(regex, idCard);
    }

    public static String format(double value, String chart)
    {
        DecimalFormat df = new DecimalFormat();
        df.applyPattern(chart);
        return df.format(value);
    }

    public static String getVolume(String src)
    {
        if (!src.contains("."))
        {
            return src;
        }
        else
        {
            String tmp = src.substring(src.indexOf(".") + 1);
            int i = Integer.parseInt(tmp);
            System.out.println(i);
            if (i == 0)
            {
                return src.substring(0, src.indexOf("."));
            }
            else
            {
                return src;
            }
        }
    }

    /**
     * Unicode转String方法
     * 
     * @param str
     * @return
     */
    public static String UnicodeToString(String str)
    {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find())
        {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch + "");
        }
        return str;
    }

    /**
     * 字体大小动态设置方法
     * 
     * @param string
     *            ,start,end,TextSize
     * @return
     */
    public static SpannableString setMyTextType(String string, int start,
            int end, int TextSize)
    {
        SpannableString builder = new SpannableString(string);
        builder.setSpan(new AbsoluteSizeSpan(TextSize), start, end,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        return builder;
    }

    /**
     * 
     * Caoxiangyu 2015.2.11 从服务器的data中获取数据内容
     * 
     */
    public static JSONObject parseContent(JSONObject obj)
    {
        JSONObject correct = null;
        try
        {
            String diyou = CreateCode.s2pDiyou(obj.getString("diyou"));
            correct = new JSONObject(diyou);
            if (correct.get("result").equals("error")
                    && correct.getInt("code") == 995)
            {
                UserConfig.getInstance().clear();
                SharedPreUtils.putString(
                        Constants.SHARE_ISLOGIN, AESencrypt
                                .encrypt2PHP(CreateCode.getSEND_AES_KEY(), "0"),
                        MyApplication.getInstance());
                Intent login = new Intent("com.diyou.v5.BaseActivity");
                MyApplication.getInstance().sendBroadcast(login);
                Intent i = new Intent(MyApplication.getInstance(),
                        BeforeLoginActivity.class);
                i.putExtra("type", "kiffout");
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MyApplication.getInstance().startActivity(i);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return correct;
    }

    public static RequestParams getRequestParams(TreeMap<String, String> map)
    {
        RequestParams params = new RequestParams();
        params.put("diyou", CreateCode.p2sDiyou(CreateCode.GetJson(map)));
        params.put("xmdy", CreateCode
                .p2sXmdy(CreateCode.p2sDiyou(CreateCode.GetJson(map))));
        return params;
    }

    public static String getString(EditText editText)
    {
        if (editText == null)
        {
            return "";
        }
        else
        {
            return editText.getText().toString().trim();
        }
    }

    public static String getMoneyFormat(String num)
    {
        if (isEmpty(num))
        {
            return "0.00";
        }
        Double doubleValue = Double.valueOf(num);
        DecimalFormat format = new DecimalFormat("######0.00");
        return format.format(doubleValue);
    }

    public static String getHidePhone(String phone)
    {
        if(StringUtils.isEmpty(phone)){
            return "";
        }else if(checkPhoneNum(phone)){
            return phone.subSequence(0, 3) + "****" + phone.subSequence(7, 11);
        }
        return phone;
    }

    public static String getHideUser(String user) {
        if (StringUtils.isEmpty(user)) {
            return "";
        } else if (user.length() >= 11 && checkPhoneNum(user.substring(user.length() - 11))) {
            return getHidePhone(user.substring(user.length() - 11));
        } else if (user.length() > 2) {
            return user.substring(0, 2) + "****";
        } else if (user.length() > 1) {
            return user.substring(0, 1) + "**";
        }
        return user;
    }

    public static String getHideName(String name)
    {
        if (StringUtils.isEmpty(name))
        {
            return "";
        }
        else
        {
            return "*" + name.substring(1);
        }
    }

    public static String getHideCardID(String id)
    {
        if (StringUtils.isEmpty(id))
        {
            return "";
        }
        else if (id.length() >= 14)
        {
            return id.substring(0, 4) + "**********" + id.substring(14);
        }
        else
        {
            return id.substring(0, 2) + "****" + id.substring(6);
        }
    }

    /**
     * 银行卡号隐藏
     * @param account
     * @return
     */
    public static String getHideBankAccount(String account) {
        if (StringUtils.isEmpty(account)) {
            return "";
        } else if (account.length() < 8) {
            return account;
        } else {
            return account.substring(0, 4) + " **** **** " + account.substring(account.length()-4);
        }
    }
    public static String getHideEmail(String email)
    {
        String str = "";
        if (!StringUtils.isEmpty(email))
        {
            String[] split = email.split("@");
            if (split.length == 2)
            {
                if (split[0].length() >= 3)
                {
                    str = split[0].substring(0, 2);
                    for (int i = 0; i < split[0].length() - 2; i++)
                    {
                        str = str + "*";
                    }
                    str = str + "@" + split[1];
                }else{
                    str=split[0].substring(0, 1)+"*";
                    str = str + "@" + split[1];
                }
            }
        }
        return str;
    }

    public static String getRandom()
    {
        return (int) ((Math.random() * 9 + 1) * 100000) + "";
    }

    /**
     * 保留两位小数
     * 
     * @param num
     *            需要转化的double小数
     * @return
     */
    public static String getDoubleFormat(double num)
    {
        if (num == 0d)
        {
            return "0.00";
        }
        // 国际标准
        // NumberFormat format=NumberFormat.getNumberInstance();
        // format.setMinimumFractionDigits(2);//最小保留小数位数
        // format.setMaximumFractionDigits(2);//最大保留小数位数

        DecimalFormat format = new DecimalFormat("######0.00");
        return format.format(num);
    }

    public static String getDoubleFormat(String num)
    {
        if (num == null || "".equals(num))
        {
            return "0";
        }
        Double d = Double.valueOf(num);
        return String.format("%.2f", d);
    }
    
    /**
     * 检测登录密码格式合法性
     * @param string
     * @return
     */
    public static boolean checkLoginPassword(String string)
    {
        if (isEmpty(string))
        {
            ToastUtil.show("请输入密码！");
            return false;
        }else if(6 > string.length() || 15 < string.length()){
            ToastUtil.show("密码应为6-15位的数字和字母组合！");
            return false;
        }else if (isSpechars(string))
        {
            ToastUtil.show("密码存在非法字符");
            return false;
        }else if (string.contains(" ") || string.contains(" "))
        {
            ToastUtil.show("密码不合法！");
            return false;
        }else if (isAllNum(string))
        {
            ToastUtil.show("密码不能全为数字！");
            return false;
        }else if (isAllLetter(string))
        {
            ToastUtil.show("密码不能全为字母！");
            return false;
        }

        return true;
    }
    /**
     * 是否存在特殊字符
     * 
     * @param string
     * @return
     */
    public static boolean isSpechars(String string)
    {
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(string);
        return m.find();
    }
    
    /**
     * 是否纯字母
     * @param str
     * @return
     */
    public static boolean isAllLetter(String str){
        return Pattern.compile("[a-z,A-Z]*").matcher(str).matches();
    }
    /**
     * 是否纯数字
     * @param str
     * @return
     */
    public static boolean isAllNum(String str){
        return Pattern.compile("[0-9]*").matcher(str).matches();
    }
    
    public boolean checkInput(String str)
    {
        int num = 0;
        num = Pattern.compile("\\d").matcher(str).find() ? num + 1 : num;
        num = Pattern.compile("[a-z]").matcher(str).find() ? num + 1 : num;
        num = Pattern.compile("[A-Z]").matcher(str).find() ? num + 1 : num;
        num = Pattern.compile("[-.!@#$%^&*()+?><]").matcher(str).find()
                ? num + 1 : num;
        return num >= 2;
    }
}
