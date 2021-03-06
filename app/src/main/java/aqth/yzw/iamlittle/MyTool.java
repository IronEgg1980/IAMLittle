package aqth.yzw.iamlittle;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.StrictMode;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public final class MyTool {
    // 时间毫秒数
    public final static long ONE_MINUTE_MILLIISECOND = 60 * 1000;
    public final static long ONE_HOUR_MILLISECOND = 60 * ONE_MINUTE_MILLIISECOND;
    public final static long ONE_DAY_MILLISECOND = 24 * ONE_HOUR_MILLISECOND;
    public final static long ONE_WEEK_MILLISECOND = 7 * ONE_DAY_MILLISECOND;
    // 人员状态
    public final static int PERSON_STATUS_ONDUTY = 1;
    public final static int PERSON_STATUS_LEAVE = 2;
    // 班次种类
    public final static int SHIFT_NORMAL = 1;
    public final static int SHIFT_LEAVEOFF = 2;
    public final static int SHIFT_NEEDCOUNT = 3;
    // 绩效工资种类
    public final static int JXGZ_RATIO = 1;
    public final static int JXGZ_AVERAGE = 2;
    public final static int JXGZ_DEDUCE = 3;
    public final static int JXGZ_ADD = 4;
    public final static int JXGZ_ADJUST = 5;
    // 性别
    public final static boolean GENDER_MAN = true;
    public final static boolean GENDER_WOMAN = false;
    // 绩效工资扣款页面Recyclerview Adapter有关常量
    public final static int DEDUCE_ITEM_MODE = 1;
    public final static int SELECT_OTHERSPERSON_MODE = 2;

    // 常用方法
    public static String packageName(Context context) {
        PackageManager manager = context.getPackageManager();
        String name = "";
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }

    public static String getJXGZ_TypeString(int type) {
        String typeText = "";
        switch (type) {
            case MyTool.JXGZ_RATIO:
                typeText = "系数分配";
                break;
            case MyTool.JXGZ_AVERAGE:
                typeText = "平均分配";
                break;
            case MyTool.JXGZ_DEDUCE:
                typeText = "扣款";
                break;
            case MyTool.JXGZ_ADD:
                typeText = "二次分配";
                break;
            case MyTool.JXGZ_ADJUST:
                typeText = "计算误差";
                break;
        }
        return typeText;
    }

    // 取得随机字符串
    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public static long getDayStart(Date date) {
        Calendar c = new GregorianCalendar();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    public static String getDayStartString(Date date) {
        return Long.toString(getDayStart(date));
    }

    public static long getDayEnd(Date date) {
        Calendar c = new GregorianCalendar();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTimeInMillis();
    }

    public static String getDayEndString(Date date) {
        return Long.toString(getDayEnd(date));
    }

    public static long[] getDayStartEnd(Date date) {
        long[] times = new long[2];
        long start = getDayStart(date);
        long end = getDayEnd(date);
        times[0] = start;
        times[1] = end;
        return times;
    }

    public static long[] getWeekStartEnd(Date date) {
        long[] times = new long[2];
        Calendar c = new GregorianCalendar();
        c.setTime(getMonday(date));
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long start = c.getTimeInMillis();
        long end = start - 1 + ONE_WEEK_MILLISECOND;
        times[0] = start;
        times[1] = end;
        return times;
    }

    public static long[] getMonthStartEnd(Date date) {
        long[] times = new long[2];
        Calendar c = new GregorianCalendar();
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long start = c.getTimeInMillis();
        long end = start - 1 + c.getActualMaximum(Calendar.DAY_OF_MONTH) * ONE_DAY_MILLISECOND;
        times[0] = start;
        times[1] = end;
        return times;
    }

    public static long[] getYearStartEnd(Date date) {
        long[] times = new long[2];
        Calendar c = new GregorianCalendar();
        c.setTime(date);
        c.set(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long start = c.getTimeInMillis();
        long end = start - 1 + c.getActualMaximum(Calendar.DAY_OF_YEAR) * ONE_DAY_MILLISECOND;
        times[0] = start;
        times[1] = end;
        return times;
    }

    public static String[] getMonthStartAndEndString(Date date) {
        String[] times = new String[2];
        long[] temp = getMonthStartEnd(date);
        times[0] = Long.toString(temp[0]);
        times[1] = Long.toString(temp[1]);
        return times;
    }

    public static String[] getDayStartEndString(Date date) {
        String[] times = new String[2];
        long[] temp = getDayStartEnd(date);
        times[0] = Long.toString(temp[0]);
        times[1] = Long.toString(temp[1]);
        return times;
    }

    public static String[] getWeekStartEndString(Date date) {
        String[] times = new String[2];
        long[] temp = getWeekStartEnd(date);
        times[0] = Long.toString(temp[0]);
        times[1] = Long.toString(temp[1]);
        return times;
    }

    public static String[] getYearStartEndString(Date date) {
        String[] times = new String[2];
        long[] temp = getYearStartEnd(date);
        times[0] = Long.toString(temp[0]);
        times[1] = Long.toString(temp[1]);
        return times;
    }

    public static Date getMonday(Date date) {
        Calendar calendar = new GregorianCalendar(Locale.CHINA);
        calendar.setTime(date);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int i = (day + 5) % 7;
        calendar.add(Calendar.DAY_OF_MONTH, -i);
        return calendar.getTime();
    }

    public static Date[] getAWeekDates(Date date) {
        Date[] dates = new Date[7];
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(getMonday(date));
        for (int k = 0; k < 7; k++) {
            if (k > 0) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            dates[k] = calendar.getTime();
        }
        return dates;
    }

    /**
     * 检测手机是否安装某个应用
     *
     * @param context
     * @param appPackageName 应用包名
     * @return true-安装，false-未安装
     */
    public static boolean isAppInstall(Context context, String appPackageName) {
        PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (appPackageName.equals(pn)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 分享前必须执行本代码，主要用于兼容SDK18以上的系统
     */
    public static void checkFileUriExposure() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();
        }
    }



    // 以下为获取农历所需代码
    final private static long[] lunarInfo = new long[]{0x04bd8, 0x04ae0, 0x0a570, 0x054d5, 0x0d260, 0x0d950, 0x16554,
            0x056a0, 0x09ad0, 0x055d2, 0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540, 0x0d6a0, 0x0ada2, 0x095b0,
            0x14977, 0x04970, 0x0a4b0, 0x0b4b5, 0x06a50, 0x06d40, 0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970, 0x06566,
            0x0d4a0, 0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3, 0x092e0, 0x1c8d7, 0x0c950, 0x0d4a0, 0x1d8a6, 0x0b550,
            0x056a0, 0x1a5b4, 0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557, 0x06ca0, 0x0b550, 0x15355, 0x04da0, 0x0a5d0,
            0x14573, 0x052d0, 0x0a9a8, 0x0e950, 0x06aa0, 0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570, 0x05260, 0x0f263,
            0x0d950, 0x05b57, 0x056a0, 0x096d0, 0x04dd5, 0x04ad0, 0x0a4d0, 0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b5a0,
            0x195a6, 0x095b0, 0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50, 0x06d40, 0x0af46, 0x0ab60, 0x09570, 0x04af5,
            0x04970, 0x064b0, 0x074a3, 0x0ea50, 0x06b58, 0x055c0, 0x0ab60, 0x096d5, 0x092e0, 0x0c960, 0x0d954, 0x0d4a0,
            0x0da50, 0x07552, 0x056a0, 0x0abb7, 0x025d0, 0x092d0, 0x0cab5, 0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9,
            0x04ba0, 0x0a5b0, 0x15176, 0x052b0, 0x0a930, 0x07954, 0x06aa0, 0x0ad50, 0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0,
            0x0d260, 0x0ea65, 0x0d530, 0x05aa0, 0x076a3, 0x096d0, 0x04bd7, 0x04ad0, 0x0a4d0, 0x1d0b6, 0x0d250, 0x0d520,
            0x0dd45, 0x0b5a0, 0x056d0, 0x055b2, 0x049b0, 0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0};

    final private static int[] year20 = new int[]{1, 4, 1, 2, 1, 2, 1, 1, 2, 1, 2, 1};

    final private static int[] year19 = new int[]{0, 3, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0};

    final private static int[] year2000 = new int[]{0, 3, 1, 2, 1, 2, 1, 1, 2, 1, 2, 1};

    public final static String[] nStr1 = new String[]{"", "正", "二", "三", "四", "五", "六", "七", "八", "九", "十", "冬",
            "腊"};

    private final static String[] Gan = new String[]{"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};

    private final static String[] Zhi = new String[]{"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};

    private final static String[] Animals = new String[]{"鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪"};

    // 传回农历 y年的总天数
    final private static int lYearDays(int y) {
        int i, sum = 348;
        for (i = 0x8000; i > 0x8; i >>= 1) {
            if ((lunarInfo[y - 1900] & i) != 0)
                sum += 1;
        }
        return (sum + leapDays(y));
    }

    // 传回农历 y年闰月的天数

    final private static int leapDays(int y) {
        if (leapMonth(y) != 0) {
            if ((lunarInfo[y - 1900] & 0x10000) != 0)
                return 30;
            else
                return 29;
        } else
            return 0;
    }


    // 传回农历 y年闰哪个月 1-12 , 没闰传回 0

    final private static int leapMonth(int y) {
        return (int) (lunarInfo[y - 1900] & 0xf);
    }


    //传回农历 y年m月的总天数

    final private static int monthDays(int y, int m) {
        if ((lunarInfo[y - 1900] & (0x10000 >> m)) == 0)
            return 29;
        else
            return 30;
    }

    // 传回农历 y年的生肖

    final public static String AnimalsYear(int y) {
        return Animals[(y - 4) % 12];
    }


    //传入 月日的offset 传回干支,0=甲子

    final private static String cyclicalm(int num) {
        return (Gan[num % 10] + Zhi[num % 12]);
    }


    // 传入 offset 传回干支, 0=甲子

    final public static String cyclical(int y) {
        int num = y - 1900 + 36;
        return (cyclicalm(num));
    }

    // 传出农历.year0 .month1 .day2 .yearCyl3 .monCyl4 .dayCyl5 .isLeap6

    final private long[] Lunar(int y, int m) {
        long[] nongDate = new long[7];
        int i = 0, temp = 0, leap = 0;
        Date baseDate = new GregorianCalendar(1900 + 1900, 1, 31).getTime();
        Date objDate = new GregorianCalendar(y + 1900, m, 1).getTime();
        long offset = (objDate.getTime() - baseDate.getTime()) / 86400000L;
        if (y < 2000)
            offset += year19[m - 1];
        if (y > 2000)
            offset += year20[m - 1];
        if (y == 2000)
            offset += year2000[m - 1];
        nongDate[5] = offset + 40;
        nongDate[4] = 14;

        for (i = 1900; i < 2050 && offset > 0; i++) {
            temp = lYearDays(i);
            offset -= temp;
            nongDate[4] += 12;
        }
        if (offset < 0) {
            offset += temp;
            i--;
            nongDate[4] -= 12;
        }
        nongDate[0] = i;
        nongDate[3] = i - 1864;
        leap = leapMonth(i); // 闰哪个月
        nongDate[6] = 0;

        for (i = 1; i < 13 && offset > 0; i++) {
            // 闰月
            if (leap > 0 && i == (leap + 1) && nongDate[6] == 0) {
                --i;
                nongDate[6] = 1;
                temp = leapDays((int) nongDate[0]);
            } else {
                temp = monthDays((int) nongDate[0], i);
            }

            // 解除闰月
            if (nongDate[6] == 1 && i == (leap + 1))
                nongDate[6] = 0;
            offset -= temp;
            if (nongDate[6] == 0)
                nongDate[4]++;
        }

        if (offset == 0 && leap > 0 && i == leap + 1) {
            if (nongDate[6] == 1) {
                nongDate[6] = 0;
            } else {
                nongDate[6] = 1;
                --i;
                --nongDate[4];
            }
        }
        if (offset < 0) {
            offset += temp;
            --i;
            --nongDate[4];
        }
        nongDate[1] = i;
        nongDate[2] = offset + 1;
        return nongDate;
    }


    // 传出y年m月d日对应的农历.year0 .month1 .day2 .yearCyl3 .monCyl4 .dayCyl5 .isLeap6

    final public static long[] calElement(int y, int m, int d) {
        long[] nongDate = new long[7];
        int i = 0, temp = 0, leap = 0;
        Date baseDate = new GregorianCalendar(0 + 1900, 0, 31).getTime();
        Date objDate = new GregorianCalendar(y, m - 1, d).getTime();
        long offset = (objDate.getTime() - baseDate.getTime()) / 86400000L;
        nongDate[5] = offset + 40;
        nongDate[4] = 14;

        for (i = 1900; i < 2050 && offset > 0; i++) {
            temp = lYearDays(i);
            offset -= temp;
            nongDate[4] += 12;
        }
        if (offset < 0) {
            offset += temp;
            i--;
            nongDate[4] -= 12;
        }
        nongDate[0] = i;
        nongDate[3] = i - 1864;
        leap = leapMonth(i); // 闰哪个月
        nongDate[6] = 0;

        for (i = 1; i < 13 && offset > 0; i++) {
            // 闰月
            if (leap > 0 && i == (leap + 1) && nongDate[6] == 0) {
                --i;
                nongDate[6] = 1;
                temp = leapDays((int) nongDate[0]);
            } else {
                temp = monthDays((int) nongDate[0], i);
            }

            // 解除闰月
            if (nongDate[6] == 1 && i == (leap + 1))
                nongDate[6] = 0;
            offset -= temp;
            if (nongDate[6] == 0)
                nongDate[4]++;
        }

        if (offset == 0 && leap > 0 && i == leap + 1) {
            if (nongDate[6] == 1) {
                nongDate[6] = 0;
            } else {
                nongDate[6] = 1;
                --i;
                --nongDate[4];
            }
        }
        if (offset < 0) {
            offset += temp;
            --i;
            --nongDate[4];
        }
        nongDate[1] = i;
        nongDate[2] = offset + 1;
        return nongDate;
    }

    public final static String getChinaDate(int day) {
        String a = "";
        if (day == 10)
            return "初十";
        if (day == 20)
            return "二十";
        if (day == 30)
            return "三十";
        int two = (int) ((day) / 10);
        if (two == 0)
            a = "初";
        if (two == 1)
            a = "十";
        if (two == 2)
            a = "廿";
        if (two == 3)
            a = "三";
        int one = (int) (day % 10);
        switch (one) {
            case 1:
                a += "一";
                break;
            case 2:
                a += "二";
                break;
            case 3:
                a += "三";
                break;
            case 4:
                a += "四";
                break;
            case 5:
                a += "五";
                break;
            case 6:
                a += "六";
                break;
            case 7:
                a += "七";
                break;
            case 8:
                a += "八";
                break;
            case 9:
                a += "九";
                break;
        }
        return a;
    }

    public static String today() {
        Calendar today = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
        int year = today.get(Calendar.YEAR);
        int month = today.get(Calendar.MONTH) + 1;
        int date = today.get(Calendar.DATE);
        long[] l = calElement(year, month, date);
        StringBuffer sToday = new StringBuffer();
        try {
            sToday.append(sdf.format(today.getTime()));
            sToday.append(" \n");
            sToday.append(" \n");
            sToday.append(" \n");
            sToday.append("   农历");
            sToday.append(cyclical(year));
            sToday.append('(');
            sToday.append(AnimalsYear(year));
            sToday.append(")年");
            sToday.append("     ");
            sToday.append(nStr1[(int) l[1]]);
            sToday.append("月");
            sToday.append(getChinaDate((int) (l[2])));
            return sToday.toString();
        } finally {
            sToday = null;
        }
    }

    public static String getNongLi(int year, int month, int day) {
        long[] l = calElement(year, month, day);
        StringBuffer sToday = new StringBuffer();
        try {
            sToday.append(cyclical(year));
            sToday.append('(');
            sToday.append(AnimalsYear(year));
            sToday.append(")年  ");
            sToday.append(nStr1[(int) l[1]]);
            sToday.append("月");
            sToday.append(getChinaDate((int) (l[2])));
            return sToday.toString();
        } finally {
            sToday = null;
        }
    }

    public static String getNongLi(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        return getNongLi(year, month, day);
    }

    public static String getNongLiNoYear(int year, int month, int day) {
        long[] l = calElement(year, month, day);
        StringBuffer sToday = new StringBuffer();
        try {
            sToday.append(nStr1[(int) l[1]]);
            sToday.append("月");
            sToday.append(getChinaDate((int) (l[2])));
            return sToday.toString();
        } finally {
            sToday = null;
        }
    }

    public static String getNongLiNoYear(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        return getNongLiNoYear(year, month, day);
    }

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日 EEEEE");
    // 一下为得到两个日期相差年月日的代码
    // 返回int数组，0-4依次为年、月、天数、小时、分
    // 如果第一个日期参数大于第二个日期参数，返回null，注意空指针
    private static int mYear,mMonth,mDayOfMonth,mHour,mMinute;
    public static int[] getValues(long l1,long l2){
        Calendar c1 = new GregorianCalendar();
        Calendar c2 = new GregorianCalendar();
        c1.setTimeInMillis(l1);
        c2.setTimeInMillis(l2);
        return getValues(c1,c2);
    }
    public static int[] getValues(Date date1, Date date2){
        Calendar c1 = new GregorianCalendar();
        Calendar c2 = new GregorianCalendar();
        c1.setTime(date1);
        c2.setTime(date2);
        return getValues(c1,c2);
    }
    public static int[] getValues (Calendar calendar1,Calendar calendar2) {
        if(calendar1.getTimeInMillis()>calendar2.getTimeInMillis()){
            return null;
        }
        mYear = calendar2.get(Calendar.YEAR) - calendar1.get(Calendar.YEAR);
        mMonth = calendar2.get(Calendar.MONTH) -
                calendar1.get(Calendar.MONTH);
        mDayOfMonth = 0;
        mHour = calendar2.get(Calendar.HOUR_OF_DAY)-calendar1.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar2.get(Calendar.MINUTE) - calendar1.get(Calendar.MINUTE);
        getMinute();
        getHour();
        getDayOfMonth(calendar1,calendar2);
        getMonth();
        return new int[]{mYear,mMonth,mDayOfMonth,mHour,mMinute};
    }

    private static void getMonth(){
        if(mMonth < 0){
            mMonth+=12;
            mYear--;
        }
    }
    private static void getDayOfMonth(Calendar calendar1,Calendar calendar2){
        int dayDiff = 0;
        int day1 = calendar1.get(Calendar.DAY_OF_MONTH);
        int maxDay1 = calendar1.getActualMaximum(Calendar.DAY_OF_MONTH);
        int day2 = calendar2.get(Calendar.DAY_OF_MONTH);
        int maxDay2 =calendar2.getActualMaximum(Calendar.DAY_OF_MONTH);
        if(day1 == maxDay1){
            if(day2 == maxDay2){
                dayDiff =0+mDayOfMonth;
            }else{
                mMonth--;
                dayDiff = day2+mDayOfMonth;
            }
            if(dayDiff < 0){
                mMonth--;
                dayDiff = day2 - 1;
            }
        }else if(day2 == maxDay2){
            if(day1 >= day2){
                dayDiff = 0+mDayOfMonth;
                if(dayDiff < 0){
                    mMonth--;
                    dayDiff = day2 - 1;
                }
            }else{
                dayDiff = mDayOfMonth + maxDay2 - day1;
            }
        }else{
            if(day2 >= day1){
                dayDiff = day2 - day1+mDayOfMonth;
                if(dayDiff < 0){
                    mMonth --;
                    dayDiff = maxDay1 - day1 + day2 -1;
                }
            }else {
                mMonth--;
                dayDiff = maxDay1 - day1 + day2;
            }
        }
        mDayOfMonth = dayDiff;
    }
    private static void getHour(){
        if(mHour < 0){
            mDayOfMonth --;
            mHour+= 24;
        }
    }

    private static void getMinute(){
        if(mMinute<0) {
            mHour--;
            mMinute += 60;
        }
    }

}
