package com.diyou.util;

import static com.diyou.util.DoubleUtils.add;
import static com.diyou.util.DoubleUtils.div;
import static com.diyou.util.DoubleUtils.mul;
import static com.diyou.util.DoubleUtils.sub;

import java.util.ArrayList;
import java.util.List;

import com.diyou.bean.Reimbursement;

public class BorrowCalculateManagerImpl
{

    /**
     * 等额本息法,返回每个月的数据集合 贷款本金×月利率×（1+月利率）还款月数/[（1+月利率）还款月数-1]
     * a*[i*(1+i)^n]/[(1+I)^n-1]
     * 
     * @param vo
     * @return
     */
    public static List<GetTypeEntity> GetMonthList(GetTypeVo vo)
    {
        double account = vo.getAccount() == null ? 0 : vo.getAccount(); // 借款金额
        double year_apr = vo.getApr(); // 年利率
        int period = vo.getPeriod().intValue(); // 期限
        long time = vo.getTime() == null ? DateUtils.getCurrentTime()
                : vo.getTime();
        double month_apr = DoubleUtils.div(year_apr, 1200.0); // 月利率 1200 =
                                                              // 12*100
        double _li = Math.pow(add(1.0, month_apr), period);
        if (account < 0)
        {
            return null;
        }

        double repay_account = 0d; // 偿还总额？
        if (_li > 1)
        {
            repay_account = DoubleUtils.decimalNum(
                    div(mul(account, mul(month_apr, _li)), sub(_li, 1.0)));
        }
        else
        {
            repay_account = account;
        }

        double $_capital_all = 0d;
        double $_interest_all = 0d;
        double $_account_all = 0.00d;
        List<GetTypeEntity> entityList = new ArrayList<GetTypeEntity>();
        for (int i = 0; i < period; i++)
        {
            GetTypeEntity entity = new GetTypeEntity();
            double interest = 0d;
            double _lu = 0d;
            if (_li <= 1)
            {
                interest = 0d;
            }
            else if (i == 0)
            {
                interest = DoubleUtils.decimalNum(mul(account, month_apr));
            }
            else
            {
                _lu = Math.pow(add(1.0, month_apr), i);
                interest = DoubleUtils.decimalNum(add(
                        mul(sub(mul(account, month_apr), repay_account), _lu),
                        repay_account));
            }

            // 防止一分钱的问题
            double capital = 0d;
            if (i == period - 1)
            {
                capital = sub(account, $_capital_all);
                interest = sub(repay_account, capital);
            }
            else
            {
                capital = sub(repay_account, interest);
            }
            $_account_all = add($_account_all, repay_account);
            $_interest_all = add($_interest_all, interest);
            $_capital_all = add($_capital_all, capital);

            entity.setAccount_all(DoubleUtils.decimalNum(repay_account));
            entity.setAccount_interest(DoubleUtils.decimalNum(interest));
            entity.setAccount_capital(DoubleUtils.decimalNum(capital));
            entity.setAccount_other(DoubleUtils
                    .decimalNum(sub(mul(repay_account, (double) period),
                            mul(repay_account, (double) (i + 1)))));
            entity.setRepay_month(DoubleUtils.decimalNum(repay_account));
            entity.setRepay_time(DateUtils.getAfterNMonths(time, i + 1));
            entityList.add(entity);
        }
        return entityList;
    }

    /**
     * 等额本息法,返回汇总数据 贷款本金×月利率×（1+月利率）还款月数/[（1+月利率）还款月数-1]
     * a*[i*(1+i)^n]/[(1+I)^n-1]
     * 
     * @param vo
     * @return
     */
    public static GetTypeEntity GetMonth(GetTypeVo vo)
    {
        GetTypeEntity entity = new GetTypeEntity();

        double account = vo.getAccount() == null ? 0 : vo.getAccount(); // 借款金额
        double year_apr = vo.getApr(); // 年利率
        int period = vo.getPeriod().intValue(); // 期限
        double month_apr = div(year_apr, (double) (1200)); // 月利率 1200 = 12*100
        double _li = Math.pow(add(1.0, month_apr), period);
        if (account < 0)
        {
            entity.setResult(false);
            return entity;
        }

        double repay_account = 0d; // 偿还总额
        if (_li > 1)
        {
            repay_account = DoubleUtils.decimalNum(
                    div(mul(account, mul(month_apr, _li)), sub(_li, 1.0)));
        }
        else
        {
            repay_account = account;
        }

        double $_capital_all = 0d;
        double $_interest_all = 0d;
        double $_account_all = 0.00d;
        for (int i = 0; i < period; i++)
        {
            double interest = 0d;
            double _lu = 0d;
            if (_li <= 1)
            {
                interest = 0d;
            }
            else if (i == 0)
            {
                interest = DoubleUtils.decimalNum(mul(account, month_apr));
            }
            else
            {
                _lu = Math.pow(add(1.0, month_apr), i);
                interest = DoubleUtils.decimalNum(add(
                        mul(sub(mul(account, month_apr), repay_account), _lu),
                        repay_account));
            }

            // 防止一分钱的问题
            double capital = 0d;
            if (i == period - 1)
            {
                capital = sub(account, $_capital_all);
                interest = sub(repay_account, capital);
            }
            else
            {
                capital = sub(repay_account, interest);
            }
            $_account_all = add($_account_all, repay_account);
            $_interest_all = add($_interest_all, interest);
            $_capital_all = add($_capital_all, capital);
        }

        entity.setAccount_total(DoubleUtils.decimalNum($_account_all));
        entity.setInterest_total(DoubleUtils.decimalNum($_interest_all));
        entity.setCapital_total(DoubleUtils.decimalNum($_capital_all));
        entity.setRepay_month(DoubleUtils.decimalNum(repay_account));
        entity.setMonth_apr(DoubleUtils.decimalNum(mul(month_apr, 100.0)));
        entity.setResult(true);
        return entity;

    }

    /**
     * 按季等额本息法，返回每个月的数据集合
     * 
     * @param vo
     * @return
     */
    public static List<GetTypeEntity> GetSeasonList(GetTypeVo vo)
    {
        int period = 0;
        double account = 0d;
        double year_apr = 0d;
        long time = DateUtils.getCurrentTime();
        // 借款的月数
        if (vo.getPeriod() != null && vo.getPeriod() > 0)
        {
            period = vo.getPeriod().intValue();
        }
        // 按季还款必须是3的倍数
        if (period % 3 != 0)
        {
            return null;
        }
        // 借款的总金额
        if (vo.getAccount() != null && vo.getAccount() > 0)
        {
            account = vo.getAccount();
        }
        else
        {
            return null;
        }

        // 借款的年利率
        if (vo.getApr() != null && vo.getApr() > 0)
        {
            year_apr = vo.getApr();
        }
        else
        {
            return null;
        }
        // 借款的时间
        if (vo.getTime() != null && vo.getTime() > 0)
        {
            time = vo.getTime();
        }
        // 月利率
        double month_apr = div(year_apr, 1200.0);// 1200=12*100
        // 得到总季数
        int _season = period / 3;
        // 每季应还的本经
        double _season_money = DoubleUtils
                .decimalNum(div(account, (double) _season));

        double $_yes_account = 0d;// 已还金额
        double $repay_account = 0d;// 总还款额
        List<GetTypeEntity> entityList = new ArrayList<GetTypeEntity>();
        for (int i = 0; i < period; i++)
        {
            GetTypeEntity entity = new GetTypeEntity();
            double $repay = sub(account, $_yes_account);// 应还的金额
            double interest = DoubleUtils.decimalNum(mul($repay, month_apr));// 利息等于应还金额乘月利率
            double $capital = 0d;
            if (i % 3 == 2)
            {
                $capital = _season_money;// 本金只在第三个月还，本金等于借款金额除季度
                $_yes_account = add($_yes_account, $capital);
                $repay = sub(account, $_yes_account);
                $repay_account = add($repay_account, $capital);// 总还款额+本金
                // entity.setAccount_all(DoubleUtils.decimalNum($capital
                // +interest));//每个季度的最后一个月要还的款
            } /*
               * else{
               * entity.setAccount_all(DoubleUtils.decimalNum(interest));//
               * 此季度的其他连个月要还的 }
               */
            $repay_account = add(interest, $capital);
            entity.setAccount_interest(DoubleUtils.decimalNum(interest));
            entity.setAccount_capital(DoubleUtils.decimalNum($capital));
            entity.setAccount_all(DoubleUtils.decimalNum($repay_account));
            entity.setAccount_other(DoubleUtils.decimalNum($repay));
            entity.setRepay_month(DoubleUtils.decimalNum($repay_account));
            entity.setRepay_time(DateUtils.getAfterNMonths(time, i + 1));
            entityList.add(entity);
        }
        return entityList;
    }

    /**
     * 按季等额本息法，返回汇总数据
     * 
     * @param vo
     * @return
     */
    public static GetTypeEntity GetSeason(GetTypeVo vo)
    {
        GetTypeEntity entity = new GetTypeEntity();

        int period = 0;
        double account = 0d;
        double year_apr = 0d;
        // 借款的月数
        if (vo.getPeriod() != null && vo.getPeriod() > 0)
        {
            period = vo.getPeriod().intValue();
        }
        // 按季还款必须是3的倍数
        if (period % 3 != 0)
        {
            entity.setResult(false);
            return entity;
        }
        // 借款的总金额
        if (vo.getAccount() != null && vo.getAccount() > 0)
        {
            account = vo.getAccount();
        }
        else
        {
            entity.setResult(false);
            return entity;
        }

        // 借款的年利率
        if (vo.getApr() != null && vo.getApr() > 0)
        {
            year_apr = vo.getApr();
        }
        else
        {
            entity.setResult(false);
            return entity;
        }
        // 月利率
        double month_apr = div(year_apr, 1200.0); // 1200=12*100
        // 得到总季数
        int _season = period / 3;
        // 每季应还的本金
        double _season_money = DoubleUtils
                .decimalNum(div(account, (double) _season));

        double $_yes_account = 0d;
        double $repay_account = 0d;// 总还款额
        double $_capital_all = 0d;
        double $_interest_all = 0d;
        double $_account_all = 0.00d;
        for (int i = 0; i < period; i++)
        {
            double $repay = sub(account, $_yes_account);// 应还的金额
            double interest = DoubleUtils.decimalNum(mul($repay, month_apr));// 利息等于应还金额乘月利率
            double $capital = 0d;
            if (i % 3 == 2)
            {
                $capital = _season_money;// 本金只在第三个月还，本金等于借款金额除季度
                $_yes_account = add($_yes_account, $capital);
                $repay_account = add($repay_account, $capital);// 总还款额+本金
            }

            double $_repay_account = add(interest, $capital);

            $_account_all = add($_account_all, $_repay_account);
            $_interest_all = add($_interest_all, interest);
            $_capital_all = add($_capital_all, $capital);
        }

        entity.setAccount_total(DoubleUtils.decimalNum($_account_all));
        entity.setInterest_total(DoubleUtils.decimalNum($_interest_all));
        entity.setCapital_total(DoubleUtils.decimalNum($_capital_all));
        entity.setRepay_month(0.00d);
        entity.setRepay_season(_season_money);
        entity.setMonth_apr(DoubleUtils.decimalNum(mul(month_apr, 100.0)));
        entity.setResult(true);
        return entity;
    }

    /**
     * 到期还本还息，返回汇总数据集合
     * 
     * @param vo
     * @return
     */
    public static List<GetTypeEntity> GetEndList(GetTypeVo vo)
    {
        GetTypeEntity entity = new GetTypeEntity();

        int period = 0;
        double $account = 0d;
        double $year_apr = 0d;
        long time = DateUtils.getCurrentTime();
        // 借款的月数,必须大于0
        if (vo.getPeriod() != null && vo.getPeriod() > 0)
        {
            period = vo.getPeriod().intValue();
        }
        else
        {
            return null;
        }
        // 借款的总金额
        if (vo.getAccount() != null && vo.getAccount() > 0)
        {
            $account = vo.getAccount();
        }
        else
        {
            return null;
        }
        // 借款的年利率
        if (vo.getApr() != null && vo.getApr() > 0)
        {
            $year_apr = vo.getApr();
        }
        else
        {
            return null;
        }
        // 借款的时间
        if (vo.getTime() != null && vo.getTime() > 0)
        {
            time = vo.getTime();
        }

        // 月利率
        double $month_apr = div($year_apr, 1200.0);// 1200=12*100
        double $interest = mul(mul($month_apr, (double) period), $account);
        entity.setAccount_all(DoubleUtils.decimalNum(add($interest, $account)));
        entity.setAccount_interest(DoubleUtils.decimalNum($interest));
        entity.setAccount_capital(DoubleUtils.decimalNum($account));
        entity.setAccount_other(0.00d);
        entity.setRepay_month(DoubleUtils.decimalNum(add($interest, $account)));
        entity.setRepay_time(DateUtils.getAfterNMonths(time, period)); // 还款时间
        List<GetTypeEntity> entityList = new ArrayList<GetTypeEntity>();
        entityList.add(entity);
        return entityList;
    }

    /**
     * 到期还本还息，返回汇总数据
     * 
     * @param vo
     * @return
     */
    public static GetTypeEntity GetEnd(GetTypeVo vo)
    {
        GetTypeEntity entity = new GetTypeEntity();

        int period = 0;
        double $account = 0d;
        double $year_apr = 0d;
        // 借款的月数
        if (vo.getPeriod() != null && vo.getPeriod() > 0)
        {
            period = vo.getPeriod().intValue();
        }
        // 借款的总金额
        if (vo.getAccount() != null && vo.getAccount() > 0)
        {
            $account = vo.getAccount();
        }
        else
        {
            entity.setResult(false);
            return entity;
        }
        // 借款的年利率
        if (vo.getApr() != null && vo.getApr() > 0)
        {
            $year_apr = vo.getApr();
        }
        else
        {
            entity.setResult(false);
            return entity;
        }

        // 月利率
        double $month_apr = div($year_apr, 1200.0);// 1200=12*100
        double $interest = mul(mul($month_apr, (double) period), $account);
        entity.setAccount_total(
                DoubleUtils.decimalNum(add($account, $interest)));
        entity.setInterest_total(DoubleUtils.decimalNum($interest));
        entity.setCapital_total(DoubleUtils.decimalNum($account));
        entity.setRepay_month(DoubleUtils.decimalNum(add($account, $interest)));
        entity.setMonth_apr(DoubleUtils.decimalNum(mul($month_apr, 100.0)));

        return entity;
    }

    /**
     * 到期还本，按月付息，返回汇总信息
     * 
     * @param vo
     * @return
     */
    public static List<GetTypeEntity> GetEndMonthList(GetTypeVo vo)
    {

        int period = 0;
        double $account = 0d;
        double $year_apr = 0d;
        long time = DateUtils.getCurrentTime();
        // 借款的月数
        if (vo.getPeriod() != null && vo.getPeriod() > 0)
        {
            period = vo.getPeriod().intValue();
        }
        // 借款的总金额
        if (vo.getAccount() != null && vo.getAccount() > 0)
        {
            $account = vo.getAccount();
        }
        else
        {
            return null;
        }
        // 借款的年利率
        if (vo.getApr() != null && vo.getApr() > 0)
        {
            $year_apr = vo.getApr();
        }
        else
        {
            return null;
        }
        // 借款的时间
        if (vo.getTime() != null && vo.getTime() > 0)
        {
            time = vo.getTime();
        }

        // 月利率
        double $month_apr = div($year_apr, 1200.0);// 1200=12*100
        double $interest = DoubleUtils.decimalNum(mul($account, $month_apr));// 利息等于应还金额乘月利率
        double $_capital_all = 0d;
        double $_interest_all = 0d;
        double $_account_all = 0.00d;
        List<GetTypeEntity> entityList = new ArrayList<GetTypeEntity>();
        for (int i = 0; i < period; i++)
        {
            GetTypeEntity entity = new GetTypeEntity();
            double $capital = 0;
            if (i + 1 == period)
            {
                $capital = $account;// 本金只在第三个月还，本金等于借款金额除季度
            }
            double $_repay_account = add($interest, $capital);
            $_account_all = add($_account_all, $_repay_account);
            $_interest_all = add($_interest_all, $interest);
            $_capital_all = add($_capital_all, $capital);
            entity.setAccount_all(add($interest, $capital));
            entity.setAccount_interest($interest);
            entity.setAccount_capital($capital);
            entity.setAccount_other(
                    DoubleUtils.decimalNum(sub($account, $capital)));
            entity.setRepay_year($account);
            entity.setRepay_time(DateUtils.getAfterNMonths(time, i + 1));
            entityList.add(entity);
        }
        return entityList;
    }

    /**
     * 到期还本，按月付息，返回汇总信息
     * 
     * @param vo
     * @return
     */
    public static GetTypeEntity GetEndMonth(GetTypeVo vo)
    {
        GetTypeEntity entity = new GetTypeEntity();

        int period = 0;
        double $account = 0d;
        double $year_apr = 0d;
        // 借款的月数
        if (vo.getPeriod() != null && vo.getPeriod() > 0)
        {
            period = vo.getPeriod().intValue();
        }
        // 借款的总金额
        if (vo.getAccount() != null && vo.getAccount() > 0)
        {
            $account = vo.getAccount();
        }
        else
        {
            entity.setResult(false);
            return entity;
        }
        // 借款的年利率
        if (vo.getApr() != null && vo.getApr() > 0)
        {
            $year_apr = vo.getApr();
        }
        else
        {
            entity.setResult(false);
            return entity;
        }

        // 月利率
        double $month_apr = div($year_apr, 1200.0);// 1200=12*1000
        double $interest = DoubleUtils.decimalNum(mul($account, $month_apr));// 利息等于应还金额乘月利率
        double $_capital_all = 0d;
        double $_interest_all = 0d;
        double $_account_all = 0.00d;
        for (int i = 0; i < period; i++)
        {
            double $capital = 0;
            if (i + 1 == period)
            {
                $capital = $account;// 本金只在第三个月还，本金等于借款金额除季度
            }
            double $_repay_account = add($interest, $capital);
            $_account_all = add($_account_all, $_repay_account);
            $_interest_all = add($_interest_all, $interest);
            $_capital_all = add($_capital_all, $capital);
        }
        entity.setAccount_total(add($account, mul($interest, (double) period)));
        entity.setInterest_total($_interest_all);
        entity.setCapital_total($account);
        entity.setRepay_month($interest);
        entity.setMonth_apr(DoubleUtils.decimalNum(mul($month_apr, 100.0)));
        entity.setResult(true);
        return entity;
    }

    /**
     * 到期还本，按月付息,且当月还息，返回每个月数据集合
     * 
     * @param vo
     * @return
     */
    public static List<GetTypeEntity> GetEndMonthsList(GetTypeVo vo)
    {
        int period = 0;
        double $account = 0d;
        double $year_apr = 0d;
        long borrow_time = DateUtils.getCurrentTime();
        // 借款的月数
        if (vo.getPeriod() != null && vo.getPeriod() > 0)
        {
            period = vo.getPeriod().intValue();
        }
        // 借款的总金额
        if (vo.getAccount() != null && vo.getAccount() > 0)
        {
            $account = vo.getAccount();
        }
        else
        {
            return null;
        }
        // 借款的年利率
        if (vo.getApr() != null && vo.getApr() > 0)
        {
            $year_apr = vo.getApr();
        }
        else
        {
            return null;
        }
        // 借款的时间
        if (vo.getTime() != null && vo.getTime() > 0)
        {
            borrow_time = vo.getTime();
        }

        // 月利率
        double $month_apr = div($year_apr, 1200.0);// 1200=12*100
        double $interest = DoubleUtils.decimalNum(mul($account, $month_apr));// 利息等于应还金额乘月利率
        double $_account_all = 0d;
        double $_interest_all = 0d;
        double $_capital_all = 0d;
        List<GetTypeEntity> entityList = new ArrayList<GetTypeEntity>();
        for (int i = 0; i < period; i++)
        {
            GetTypeEntity entity = new GetTypeEntity();
            double $capital = 0;
            double $_repay_account = add($interest, $capital);
            $_account_all = add($_account_all, $_repay_account);
            $_interest_all = add($_interest_all, $interest);
            $_capital_all = add($_capital_all, $capital);
            entity.setAccount_all(add($interest, $capital));
            entity.setAccount_interest($interest);
            entity.setAccount_capital($capital);
            entity.setAccount_other(
                    DoubleUtils.decimalNum(sub($account, $capital)));
            entity.setRepay_year($account);
            if (i == 0)
            {
                entity.setRepay_time(borrow_time);
            }
            else
            {
                entity.setRepay_time(DateUtils.getAfterNMonths(borrow_time, i));
            }
            entityList.add(entity);
        }
        // 最后一次只还本金,没有利息,日期顺延一个月
        GetTypeEntity entity = new GetTypeEntity();
        entity.setAccount_all($account);
        entity.setAccount_capital($account);
        entity.setAccount_interest(0d);
        entity.setAccount_other(0d);
        entity.setRepay_year($account);
        entity.setRepay_time(DateUtils.getAfterNMonths(borrow_time, period));
        entityList.add(entity);

        return entityList;
    }

    /**
     * 到期还本，按月付息,且当月还息，返回汇总信息
     * 
     * @param vo
     * @return
     */
    public static GetTypeEntity GetEndMonths(GetTypeVo vo)
    {
        GetTypeEntity entity = new GetTypeEntity();
        int period = 0;
        double $account = 0d;
        double $year_apr = 0d;
        // 借款的月数
        if (vo.getPeriod() != null && vo.getPeriod() > 0)
        {
            period = vo.getPeriod().intValue();
        }
        // 借款的总金额
        if (vo.getAccount() != null && vo.getAccount() > 0)
        {
            $account = vo.getAccount();
        }
        else
        {
            entity.setResult(false);
            return entity;
        }
        // 借款的年利率
        if (vo.getApr() != null && vo.getApr() > 0)
        {
            $year_apr = vo.getApr();
        }
        else
        {
            entity.setResult(false);
            return entity;
        }
        // 月利率
        double $month_apr = div($year_apr, 1200.0);// 1200=12*100
        double $interest = DoubleUtils.decimalNum(mul($account, $month_apr));// 利息等于应还金额乘月利率

        double $_account_all = 0d;
        double $_interest_all = 0d;
        double $_capital_all = 0d;
        for (int i = 0; i < period; i++)
        {
            double $capital = 0;
            double $_repay_account = add($interest, $capital);
            $_account_all = add($_account_all, $_repay_account);
            $_interest_all = add($_interest_all, $interest);
            $_capital_all = add($_capital_all, $capital);
        }
        entity.setAccount_total(add($account, mul($interest, (double) period)));
        entity.setInterest_total($_interest_all);
        entity.setCapital_total($account);
        entity.setRepay_month($interest);
        entity.setMonth_apr(DoubleUtils.decimalNum(mul($month_apr, 100.0)));
        entity.setResult(true);
        return entity;
    }

    /**
     * 到期还本，按天付息，返回汇总数据
     * 
     * @param vo
     * @return
     */
    public static GetTypeEntity GetEndDay(GetTypeVo vo)
    {
        GetTypeEntity entity = new GetTypeEntity();

        // 天利率
        double $day_apr = div(vo.getApr(), 36000.0);// 36000=360*100
        double $_interest_all = DoubleUtils.decimalNum(
                mul(mul(vo.getAccount(), (double) vo.getPeriod()), $day_apr));
        double $_account_all = add($_interest_all, vo.getAccount());
        entity.setAccount_total($_account_all);
        entity.setInterest_total($_interest_all);
        entity.setCapital_total(vo.getAccount());
        entity.setDay_apr($day_apr);
        entity.setResult(true);
        return entity;
    }

    /**
     * 到期还本，按天付息，返回汇总数据集合
     * 
     * @param vo
     * @return
     */
    public static List<GetTypeEntity> GetEndDayList(GetTypeVo vo)
    {
        GetTypeEntity entity = new GetTypeEntity();
        long borrow_time = 0l;
        // 借款的时间
        if (vo.getTime() != null)
        {
            borrow_time = vo.getTime();
        }
        else
        {
            borrow_time = DateUtils.getCurrentTime();
        }

        // 天利率
        double $day_apr = div(vo.getApr(), 36000.0);// 36000=360*100
        double $_interest_all = DoubleUtils.decimalNum(
                mul(mul(vo.getAccount(), (double) vo.getPeriod()), $day_apr));
        double $_account_all = add($_interest_all, vo.getAccount());
        entity.setAccount_all($_account_all);
        entity.setAccount_interest($_interest_all);
        entity.setAccount_capital(vo.getAccount());
        entity.setRepay_time(DateUtils.getAfterNDays(borrow_time,
                vo.getPeriod() == null ? 0 : vo.getPeriod().intValue()));
        entity.setResult(true);
        List<GetTypeEntity> entityList = new ArrayList<GetTypeEntity>();
        entityList.add(entity);
        return entityList;
    }

    /**
     * 还款利率计算 caoxiangyu 2015.7.16
     * 
     * @param loanAmount
     *            还款金额
     * @param loanPeriod
     *            还款期限
     * @param rates
     *            还款年利率
     * @param repayment
     *            还款方式
     * @return 实际收益金额
     * 
     */
    public static String Bill(double loanAmount, double loanPeriod,
            double rates, Reimbursement repayment)
    {
        double totalRevenue = 0; // 总收益
        try
        {
            double interest = DoubleUtils.div(DoubleUtils.div(rates, 12.0),
                    100.0); // 月利率
            double dailyRate = DoubleUtils.div(DoubleUtils.div(rates, 360.0),
                    100.0); // 日利率
            switch (repayment)
            {
            case Dengebenxi:
                totalRevenue = DoubleUtils.sub(
                        DoubleUtils.div(
                                DoubleUtils.mul(
                                        DoubleUtils.mul(DoubleUtils
                                                .mul(loanPeriod, loanAmount),
                                        interest),
                        Math.pow((1 + interest), loanPeriod)),
                        (Math.pow((1 + interest), loanPeriod) - 1)),
                        loanAmount);
                break;
            case Daoqihuanbenhuanxi:
                totalRevenue = DoubleUtils
                        .mul(DoubleUtils.mul(loanAmount, interest), loanPeriod);
                break;
            case Anyuefuxi:
                totalRevenue = DoubleUtils
                        .mul(DoubleUtils.mul(loanAmount, interest), loanPeriod);
                break;
            case Anyuefuxidaoqihuanben:
                totalRevenue = DoubleUtils
                        .mul(DoubleUtils.mul(loanAmount, interest), loanPeriod);
                break;

            default:
                break;
            }
        }
        catch (Exception e)
        {
        }

        return String.valueOf(totalRevenue);
    }
}
