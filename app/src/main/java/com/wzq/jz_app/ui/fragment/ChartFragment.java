package com.wzq.jz_app.ui.fragment;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.wzq.jz_app.MyApplication;
import com.wzq.jz_app.R;
import com.wzq.jz_app.base.BaseMVPFragment;
import com.wzq.jz_app.model.bean.local.BBill;
import com.wzq.jz_app.model.bean.local.MonthChartBean;
import com.wzq.jz_app.model.bean.remote.MyUser;
import com.wzq.jz_app.model.repository.LocalRepository;
import com.wzq.jz_app.presenter.MonthChartPresenter;
import com.wzq.jz_app.presenter.contract.MonthChartContract;
import com.wzq.jz_app.ui.adapter.binder.MonthChartBillViewBinder;
import com.wzq.jz_app.ui.fragment.chart.LineChartEntity;
import com.wzq.jz_app.ui.fragment.chart.LineChartInViewPager;
import com.wzq.jz_app.ui.fragment.chart.NewMarkerView;
import com.wzq.jz_app.ui.fragment.chart.RealListEntity;
import com.wzq.jz_app.ui.fragment.chart.YoyListEntity;
import com.wzq.jz_app.utils.DateUtils;
import com.wzq.jz_app.utils.PieChartUtils;
import com.wzq.jz_app.utils.SnackbarUtils;
import com.wzq.jz_app.utils.StringUtils;
import com.wzq.jz_app.widget.CircleImageView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import cn.bmob.v3.BmobUser;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * 作者：wzq
 * 邮箱：wang_love152@163.com
 */
public class ChartFragment extends BaseMVPFragment<MonthChartContract.Presenter>
        implements MonthChartContract.View, OnChartValueSelectedListener, View.OnClickListener {

    private PieChart mChart;
    private TextView centerTitle;
    private TextView centerMoney;
    private MyUser currentUser;
    private LinearLayout layoutCenter;
    private ImageView centerImg;
    private CircleImageView circleBg;
    private ImageView circleImg;
    private RelativeLayout layoutCircle;
    private TextView title;
    private TextView money;
    private TextView rankTitle;
    private RelativeLayout layoutOther;
    private TextView otherMoney;
    private SwipeRefreshLayout swipe;
    private RelativeLayout itemType;
    private RelativeLayout itemOther;
    private RecyclerView rvList;
    private LinearLayout layoutTypedata;
    private
    ArrayList<Integer> colors = new ArrayList<>();

    private boolean TYPE = true;//默认总收入true
    private List<MonthChartBean.SortTypeList> tMoneyBeanList;
    private String sort_image;//饼状图与之相对应的分类图片地址
    private String sort_name;
    private String back_color;
    private String advice;

    private MonthChartBean monthChartBean;
    private MonthChartBean yearDateBean;

    private MultiTypeAdapter adapter;

    private String setYear = DateUtils.getCurYear(DateUtils.FORMAT_Y);
    private String setMonth = DateUtils.getCurMonth(DateUtils.FORMAT_M);
    private Toolbar toolbar;
    private TextView chart_title;
    private ImageView search_date;
    private TextView chart_date;
    private LineChartInViewPager lineChart;
    private List<RealListEntity> realList;
    private List<YoyListEntity> yoyList;
    private PieChart mainPieChart;

    private List<Entry> values1, values2, dottedLineData, solidLineData;
    private List<String> dataList;
    private RealListEntity realListEntity;
    private YoyListEntity yoyListEntity;
    private DecimalFormat mFormat;
    private float maxData = 0;
    ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
    private PieChart mPieChart;
    private TextView chart_date_year;
    BigDecimal maxIn = BigDecimal.ZERO;
    BigDecimal maxOut = BigDecimal.ZERO;
    int timeIn;
    int timeOut;
    BigDecimal endIn = BigDecimal.ZERO;
    BigDecimal endOut = BigDecimal.ZERO;

    //选择时间
    protected int mYear;
    protected int mMonth;
    protected int mDay;
    protected String days;
    private Button startTime;
    private Button endTime;
    private Button look;
    private int status = 0;

    /*****************************************************************************/

    public void changeDate(String yyyy, String mm) {
        setYear = yyyy;
        setMonth = mm;
        mPresenter.getMonthChart(MyApplication.getCurrentUserId(), setYear, setMonth, "1");
        chart_date.setText(setYear + "-" + setMonth);
        chart_date_year.setText(setYear + "");
        test(yyyy);
        test(yyyy);
    }

    public void changeDate1(String start, String end) {
        chart_date.setText(start + "到" + end);
        mPresenter.getMonthChart(MyApplication.getCurrentUserId(), start, end, "0");
    }

    /*****************************************************************************/
    @Override
    protected int getLayoutId() {
        return R.layout.main_fragment_chart;
    }


    @Override
    protected void initWidget(Bundle savedInstanceState) {
        super.initWidget(savedInstanceState);
        chart_title = getViewById(R.id.chart_title);
        search_date = getViewById(R.id.toolbar_date1);
        chart_date = getViewById(R.id.date_chart);
        chart_date_year = getViewById(R.id.date_chart_year);


        //设置日期选择器初始日期
        mYear = Integer.parseInt(DateUtils.getCurYear(DateUtils.FORMAT_Y));
        mMonth = Integer.parseInt(DateUtils.getCurMonth(DateUtils.FORMAT_M));
        mDay = Integer.parseInt(DateUtils.getCurDay(DateUtils.FORMAT_D));
//
        mChart = getViewById(R.id.chart);
        centerTitle = getViewById(R.id.center_title);
        centerMoney = getViewById(R.id.center_money);
        layoutCenter = getViewById(R.id.layout_center);
        centerImg = getViewById(R.id.center_img);
        circleBg = getViewById(R.id.circle_bg);
        circleImg = getViewById(R.id.circle_img);
        layoutCircle = getViewById(R.id.layout_circle);
        title = getViewById(R.id.title);
        money = getViewById(R.id.money);
        rankTitle = getViewById(R.id.rank_title);
        layoutOther = getViewById(R.id.layout_other);
        otherMoney = getViewById(R.id.other_money);
        swipe = getViewById(R.id.swipe);
        itemType = getViewById(R.id.item_type);
        itemOther = getViewById(R.id.item_other);
        rvList = getViewById(R.id.rv_list);
        layoutTypedata = getViewById(R.id.layout_typedata);


        look = getViewById(R.id.look);
        startTime = getViewById(R.id.startTime);
        endTime = getViewById(R.id.endTime);
        //初始化饼状图
        PieChartUtils.initPieChart(mChart);
        //设置圆盘是否转动，默认转动
        mChart.setRotationEnabled(true);
        mChart.setOnChartValueSelectedListener(this);
        //改变加载显示的颜色
        swipe.setColorSchemeColors(getResources().getColor(R.color.text_red), getResources().getColor(R.color.text_red));
        //设置向下拉多少出现刷新
        swipe.setDistanceToTriggerSync(200);
        //设置刷新出现的位置
        swipe.setProgressViewEndTarget(false, 200);
        //下拉刷新监听
        swipe.setOnRefreshListener(() -> {
            swipe.setRefreshing(false);
            mPresenter.getMonthChart(MyApplication.getCurrentUserId(), setYear, setMonth, "1");
            chart_date.setText(setYear + "-" + setMonth);
            test(setYear);
            test(setYear);
        });

        rvList.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new MultiTypeAdapter();
        adapter.register(BBill.class, new MonthChartBillViewBinder(mContext));
        rvList.setAdapter(adapter);
        test(setYear);

    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initClick() {
        super.initClick();
        layoutCenter.setOnClickListener(this);
        itemType.setOnClickListener(this);
        itemOther.setOnClickListener(this);
        search_date.setOnClickListener(this);
        startTime.setOnClickListener(this);
        endTime.setOnClickListener(this);
        look.setOnClickListener(this);
    }

    @Override
    protected void processLogic() {
        super.processLogic();
        mPresenter.getMonthChart(MyApplication.getCurrentUserId(), setYear, setMonth, "1");
        chart_date.setText(setYear + "-" + setMonth);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_center:  //图表中心键
                TYPE = !TYPE;
                setReportData();
                break;
            case R.id.item_type:
                break;
            case R.id.item_other:
                break;
            case R.id.startTime:
                showTimeSelector("0");
                break;
            case R.id.endTime:
                showTimeSelector("1");
                break;
            case R.id.look:
                if (status == 1) {
                    String start = startTime.getText().toString();
                    String end = endTime.getText().toString();
                    if (start.contains("时间") || end.contains("时间")) {
                        Toast.makeText(getActivity(), "请先选择时间区间", Toast.LENGTH_SHORT).show();
                    } else {
                        changeDate1(start, end);
                    }
                } else {
                    Toast.makeText(getActivity(), "请先选择时间区间", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.toolbar_date1:
                currentUser = BmobUser.getCurrentUser(MyUser.class);
//                if (currentUser == null)
//                    Toast.makeText(getApplicationContext(), "请先登录", Toast.LENGTH_SHORT).show();
//                else {
                //时间选择器
                new TimePickerBuilder(mContext, (Date date, View vc) -> {
                    chart_date.setText(DateUtils.date2Str(date, "yyyy-MM"));
//                        monthListFragment.changeDate(DateUtils.date2Str(date, "yyyy"), DateUtils.date2Str(date, "MM"));
                    changeDate(DateUtils.date2Str(date, "yyyy"), DateUtils.date2Str(date, "MM"));
                }).setType(new boolean[]{true, true, false, false, false, false})
                        .setRangDate(null, Calendar.getInstance())
                        .isDialog(true)//是否显示为对话框样式
                        .build().show();
                break;

        }

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null)
            return;
        int entryIndex = (int) h.getX();
        setNoteData(entryIndex, e.getY());
    }


    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");
    }

    //光滑曲线图

    /**
     * 下面json转实体类
     */
    public void test(String year) {
        try {
            List<BBill> bBills = LocalRepository.getInstance().getBBillByUserIdWithYear(MyApplication.getCurrentUserId(), year);
            BigDecimal totalMoneyIn1 = BigDecimal.ZERO, totalMoneyOut1 = BigDecimal.ZERO;
            BigDecimal totalMoneyIn2 = BigDecimal.ZERO, totalMoneyOut2 = BigDecimal.ZERO;
            BigDecimal totalMoneyIn3 = BigDecimal.ZERO, totalMoneyOut3 = BigDecimal.ZERO;
            BigDecimal totalMoneyIn4 = BigDecimal.ZERO, totalMoneyOut4 = BigDecimal.ZERO;
            BigDecimal totalMoneyIn5 = BigDecimal.ZERO, totalMoneyOut5 = BigDecimal.ZERO;
            BigDecimal totalMoneyIn6 = BigDecimal.ZERO, totalMoneyOut6 = BigDecimal.ZERO;
            BigDecimal totalMoneyIn7 = BigDecimal.ZERO, totalMoneyOut7 = BigDecimal.ZERO;
            BigDecimal totalMoneyIn8 = BigDecimal.ZERO, totalMoneyOut8 = BigDecimal.ZERO;
            BigDecimal totalMoneyIn9 = BigDecimal.ZERO, totalMoneyOut9 = BigDecimal.ZERO;
            BigDecimal totalMoneyIn10 = BigDecimal.ZERO, totalMoneyOut10 = BigDecimal.ZERO;
            BigDecimal totalMoneyIn11 = BigDecimal.ZERO, totalMoneyOut11 = BigDecimal.ZERO;
            BigDecimal totalMoneyIn12 = BigDecimal.ZERO, totalMoneyOut12 = BigDecimal.ZERO;
            BigDecimal[] totalMoneyIn = null;
//            Arrays.fill(totalMoneyIn, BigDecimal.ZERO);
            BigDecimal[] totalMoneyOut = null;
//            Arrays.fill(totalMoneyOut, BigDecimal.ZERO);
            for (int j = 0; j < bBills.size(); j++) {
                String date = String.valueOf(DateUtils.getDay(bBills.get(j).getCrdate()));
                String date1 = date.substring(5, 7);
                switch (date1) {
                    case "01":
                        if (bBills.get(j).getIncome()) {
                            totalMoneyIn1 = totalMoneyIn1.add(new BigDecimal(Float.toString(bBills.get(j).getCost())));
                        } else {
                            totalMoneyOut1 = totalMoneyOut1.add(new BigDecimal(Float.toString(bBills.get(j).getCost())));
                        }
                        break;
                    case "02":
                        if (bBills.get(j).getIncome()) {
                            totalMoneyIn2 = totalMoneyIn2.add(new BigDecimal(Float.toString(bBills.get(j).getCost())));
                        } else {
                            totalMoneyOut2 = totalMoneyOut2.add(new BigDecimal(Float.toString(bBills.get(j).getCost())));
                        }
                        break;
                    case "03":
                        if (bBills.get(j).getIncome()) {
                            totalMoneyIn3 = totalMoneyIn3.add(new BigDecimal(Float.toString(bBills.get(j).getCost())));
                        } else {
                            totalMoneyOut3 = totalMoneyOut3.add(new BigDecimal(Float.toString(bBills.get(j).getCost())));
                        }
                        break;
                    case "04":
                        if (bBills.get(j).getIncome()) {
                            totalMoneyIn4 = totalMoneyIn4.add(new BigDecimal(Float.toString(bBills.get(j).getCost())));
                        } else {
                            totalMoneyOut4 = totalMoneyOut4.add(new BigDecimal(Float.toString(bBills.get(j).getCost())));
                        }
                        break;
                    case "05":
                        if (bBills.get(j).getIncome()) {
                            totalMoneyIn5 = totalMoneyIn5.add(new BigDecimal(Float.toString(bBills.get(j).getCost())));
                        } else {
                            totalMoneyOut5 = totalMoneyOut5.add(new BigDecimal(Float.toString(bBills.get(j).getCost())));
                        }
                        break;
                    case "06":
                        if (bBills.get(j).getIncome()) {
                            totalMoneyIn6 = totalMoneyIn6.add(new BigDecimal(Float.toString(bBills.get(j).getCost())));
                        } else {
                            totalMoneyOut6 = totalMoneyOut6.add(new BigDecimal(Float.toString(bBills.get(j).getCost())));
                        }
                        break;
                    case "07":
                        if (bBills.get(j).getIncome()) {
                            totalMoneyIn7 = totalMoneyIn7.add(new BigDecimal(Float.toString(bBills.get(j).getCost())));
                        } else {
                            totalMoneyOut7 = totalMoneyOut7.add(new BigDecimal(Float.toString(bBills.get(j).getCost())));
                        }
                        break;
                    case "08":
                        if (bBills.get(j).getIncome()) {
                            totalMoneyIn8 = totalMoneyIn8.add(new BigDecimal(Float.toString(bBills.get(j).getCost())));
                        } else {
                            totalMoneyOut8 = totalMoneyOut8.add(new BigDecimal(Float.toString(bBills.get(j).getCost())));
                        }
                        break;
                    case "09":
                        if (bBills.get(j).getIncome()) {
                            totalMoneyIn9 = totalMoneyIn9.add(new BigDecimal(Float.toString(bBills.get(j).getCost())));
                        } else {
                            totalMoneyOut9 = totalMoneyOut9.add(new BigDecimal(Float.toString(bBills.get(j).getCost())));
                        }
                        break;
                    case "10":
                        if (bBills.get(j).getIncome()) {
                            totalMoneyIn10 = totalMoneyIn10.add(new BigDecimal(Float.toString(bBills.get(j).getCost())));
                        } else {
                            totalMoneyOut10 = totalMoneyOut10.add(new BigDecimal(Float.toString(bBills.get(j).getCost())));;
                        }
                        break;
                    case "11":
                        if (bBills.get(j).getIncome()) {
                            totalMoneyIn11 = totalMoneyIn11.add(new BigDecimal(Float.toString(bBills.get(j).getCost())));;;
                        } else {
                            totalMoneyOut11 = totalMoneyOut11.add(new BigDecimal(Float.toString(bBills.get(j).getCost())));
                        }
                        break;
                    case "12":
                        if (bBills.get(j).getIncome()) {
                            totalMoneyIn12 = totalMoneyIn12.add(new BigDecimal(Float.toString(bBills.get(j).getCost())));
                        } else {
                            totalMoneyOut12 = totalMoneyOut12.add(new BigDecimal(Float.toString(bBills.get(j).getCost())));
                        }
                        break;
                }
            }
            totalMoneyIn = new BigDecimal[]{totalMoneyIn1, totalMoneyIn2, totalMoneyIn3, totalMoneyIn4, totalMoneyIn5, totalMoneyIn6, totalMoneyIn7, totalMoneyIn8, totalMoneyIn9, totalMoneyIn10, totalMoneyIn11, totalMoneyIn12};
            totalMoneyOut = new BigDecimal[]{totalMoneyOut1, totalMoneyOut2, totalMoneyOut3, totalMoneyOut4, totalMoneyOut5,
                    totalMoneyOut6, totalMoneyOut7, totalMoneyOut8, totalMoneyOut9, totalMoneyOut10, totalMoneyOut11, totalMoneyOut12};
            realList = new ArrayList<>();
            yoyList = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                if(totalMoneyIn[i] == null){
                    totalMoneyIn[i] = BigDecimal.ZERO;
                }
                if(totalMoneyOut[i] == null){
                    totalMoneyOut[i] = BigDecimal.ZERO;
                }
                RealListEntity realListEntity = new RealListEntity();
                YoyListEntity yoyListEntity = new YoyListEntity();
                realListEntity.setAmount(totalMoneyIn[i] + "");//收入
                realListEntity.setMonth((i + 1) + "");
                realListEntity.setYear("收入");
                realList.add(realListEntity);

                yoyListEntity.setAmount(totalMoneyOut[i] + "");//支出
                yoyListEntity.setMonth((i + 1) + "");
                yoyListEntity.setYear("支出");
                yoyList.add(yoyListEntity);


                //分析年度消费最高、收入最高
                endIn = endIn.add(totalMoneyIn[i]);
                endOut = endOut.add(totalMoneyOut[i]);
                if (totalMoneyIn[i].compareTo(maxIn) == 1) {   // 判断收入最大值
                    maxIn = totalMoneyIn[i];
                    timeIn = i + 1;
                }
                if (totalMoneyOut[i].compareTo(maxOut) == 1) {
                    maxOut = totalMoneyOut[i];
                    timeOut = i + 1;
                }
            }
            if (endIn.compareTo(BigDecimal.ZERO) == 0 && endOut.compareTo(BigDecimal.ZERO) == 0) {
                advice = "您当前没有账单记录！请开始使用APP记账吧！";
                //年度分析
                TextView textView = getViewById(R.id.sys);
                textView.setText("分析：" + advice);
            } else {
                if (endOut.compareTo(endIn) == 1)
                    advice = "收支不平衡【入不敷出，" + endIn.subtract(endOut) +"元】,建议您省吃俭用，少花钱！";
                else if (endIn.compareTo(endOut) == 0) {
                    advice = "收支平衡【没有存款】，建议您合理消费";
                } else {
                    advice = "恭喜您，【您有存款" + endIn.subtract(endOut) + "元了】，建议您享受生活，适当理财";
                }
                //年度分析
                TextView textView = getViewById(R.id.sys);
                textView.setText("分析：当前年度中" + timeIn + "月收入最高，" + timeOut + "月消费最高!\r\n" + advice);
            }
            initViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initViews() {


        lineChart = getViewById(R.id.new_lineChart);
        mFormat = new DecimalFormat("#,###.##");
        values1 = new ArrayList<>();
        values2 = new ArrayList<>();
        for (int i = 0; i < yoyList.size(); i++) {
            yoyListEntity = yoyList.get(i);
            String amount = yoyListEntity.getAmount();
            if (amount != null) {
                float f = 0;
                try {
                    f = Float.parseFloat(amount);
                } catch (Exception e) {
                    e.printStackTrace();
                    f = 0;
                }
                Entry entry = new Entry(i + 1, f);
                values1.add(entry);
            }
        }

        for (int i = 0; i < realList.size(); i++) {
            realListEntity = realList.get(i);
            String amount = realListEntity.getAmount();
            if (amount != null) {
                float f = 0;
                try {
                    f = Float.parseFloat(amount);
                } catch (Exception e) {
                    e.printStackTrace();
                    f = 0;
                }
                Entry entry = new Entry(i + 1, f);
                values2.add(entry);
            }
        }
        Drawable[] drawables = {
                ContextCompat.getDrawable(getActivity(), R.drawable.chart_thisyear_blue),
                ContextCompat.getDrawable(getActivity(), R.drawable.chart_callserice_call_casecount)
        };
        int[] callDurationColors = {Color.parseColor("#45A2FF"), Color.parseColor("#5fd1cc")};
        String thisYear = "";
        if (realList.size() > 0) {
            thisYear = realList.get(0).getYear();
        }

        String lastYear = "";
        if (yoyList.size() > 0) {
            lastYear = yoyList.get(0).getYear();
        }
        String[] labels = new String[]{thisYear, lastYear};
        updateLinehart(yoyList, realList, lineChart, callDurationColors, drawables, "", values1, values2, labels);
    }

    /**
     * 双平滑曲线传入数据，添加markview，添加实体类单位
     *
     * @param yoyList
     * @param realList
     * @param lineChart
     * @param colors
     * @param drawables
     * @param unit
     * @param values2
     * @param values1
     * @param labels
     */
    private void updateLinehart(final List<YoyListEntity> yoyList, final List<RealListEntity> realList,
                                LineChart lineChart, int[] colors, Drawable[] drawables,
                                final String unit, List<Entry> values2, List<Entry> values1, final String[] labels) {
        List<Entry>[] entries = new ArrayList[2];
        entries[0] = values1;
        entries[1] = values2;
        LineChartEntity lineChartEntity = new LineChartEntity(lineChart, entries, labels, colors,
                Color.parseColor("#999999"), 12f);
        lineChartEntity.drawCircle(true);
        lineChart.setScaleMinima(1.0f, 1.0f);
        toggleFilled(lineChartEntity, drawables, colors);
        lineChartEntity.setLineMode(LineDataSet.Mode.CUBIC_BEZIER);//曲线
        // lineChartEntity.setLineMode(LineDataSet.Mode.LINEAR);//折线
        lineChartEntity.initLegend(Legend.LegendForm.CIRCLE, 12f,
                Color.parseColor("#999999"));
        lineChartEntity.updateLegendOrientation(Legend.LegendVerticalAlignment.TOP,
                Legend.LegendHorizontalAlignment.RIGHT, Legend.LegendOrientation.HORIZONTAL);
        lineChartEntity.setAxisFormatter(
                new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        if (value == 1.0f) {
                            return mFormat.format(value) + "月";
                        }
                        String monthStr = mFormat.format(value);
                        if (monthStr.contains(".")) {
                            return "";
                        } else {
                            return monthStr;
                        }
//                        return mMonthFormat.format(value);
                    }
                },
                new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        return mFormat.format(value) + unit;
                    }
                });

        lineChartEntity.setDataValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return mFormat.format(value) + unit;
            }
        });

        final NewMarkerView markerView = new NewMarkerView(getActivity(), R.layout.custom_marker_view_layout);
        markerView.setCallBack(new NewMarkerView.CallBack() {
            @Override
            public void onCallBack(float x, String value) {
                int index = (int) (x);
                if (index < 0) {
                    return;
                }
                if (index > yoyList.size() && index > realList.size()) {
                    return;
                }
                String textTemp = "";

                if (index <= yoyList.size()) {
                    if (!StringUtils.isEmpty(textTemp)) {
                    }
                    textTemp += index + "月" + yoyList.get(index - 1).getYear() + "  " + mFormat.format(Float.parseFloat(yoyList.get(index - 1).getAmount())) + unit + "元";
                }

                if (index <= realList.size()) {
                    textTemp += "\n";
                    textTemp += index + "月" + realList.get(index - 1).getYear() + "  " + mFormat.format(Float.parseFloat(realList.get(index - 1).getAmount())) + unit + "元";
                }
                markerView.getTvContent().setText(textTemp);
            }
        });
        lineChartEntity.setMarkView(markerView);
        lineChart.getData().setDrawValues(false);
    }

    /**
     * 双平滑曲线添加线下的阴影
     *
     * @param lineChartEntity
     * @param drawables
     * @param colors
     */
    private void toggleFilled(LineChartEntity lineChartEntity, Drawable[] drawables, int[] colors) {
        if (android.os.Build.VERSION.SDK_INT >= 18) {

            lineChartEntity.toggleFilled(drawables, null, true);
        } else {
            lineChartEntity.toggleFilled(null, colors, true);
        }
    }


    /**
     * 报表数据
     */
    private void setReportData() {

        if (monthChartBean == null) {
            return;
        }

        BigDecimal totalMoney;
        if (TYPE) {
            centerTitle.setText("总支出");
            centerImg.setImageResource(R.mipmap.tallybook_output);
            tMoneyBeanList = monthChartBean.getOutSortlist();
            totalMoney = monthChartBean.getTotalOut();
        } else {
            centerTitle.setText("总收入");
            centerImg.setImageResource(R.mipmap.tallybook_input);
            tMoneyBeanList = monthChartBean.getInSortlist();
            totalMoney = monthChartBean.getTotalIn();
        }
        centerMoney.setText(totalMoney.toString());

        ArrayList<PieEntry> entries = new ArrayList<>();

        if (tMoneyBeanList != null && tMoneyBeanList.size() > 0) {
            layoutTypedata.setVisibility(View.VISIBLE);
            for (int i = 0; i < tMoneyBeanList.size(); i++) {
                BigDecimal scale = BigDecimal.ZERO;
                if(totalMoney.compareTo(BigDecimal.ZERO) != 0){
                    scale = new BigDecimal(Float.toString(tMoneyBeanList.get(i).getMoney())).divide(totalMoney, 5, BigDecimal.ROUND_HALF_UP);
                }
                float value = (scale.compareTo(new BigDecimal("0.01")) == -1) ? 0.01f : scale.floatValue();
                entries.add(new PieEntry(value, PieChartUtils.getDrawable(tMoneyBeanList.get(i).getSortImg())));
                colors.add(getResources().getColor(R.color.home_1));
                colors.add(getResources().getColor(R.color.home_2));
                colors.add(getResources().getColor(R.color.home_3));
                colors.add(getResources().getColor(R.color.home_5));
                colors.add(getResources().getColor(R.color.home_7));
                colors.add(getResources().getColor(R.color.home_10));
                colors.add(getResources().getColor(R.color.home_11));
                colors.add(getResources().getColor(R.color.home_4));
                colors.add(getResources().getColor(R.color.home_9));
                colors.add(getResources().getColor(R.color.home_8));
                colors.add(getResources().getColor(R.color.home_6));
                colors.add(getResources().getColor(R.color.home_12));
                colors.add(getResources().getColor(R.color.home_13));

            }
            setNoteData(0, entries.get(0).getY());
        } else {//无数据时的显示
            layoutTypedata.setVisibility(View.GONE);
            entries.add(new PieEntry(1f));
            colors.add(getResources().getColor(R.color.home_3));
        }

        PieChartUtils.setPieChartData(mChart, entries, colors);


    }

    /**
     * 点击饼状图上区域后相应的数据设置
     *
     * @param index
     */
    private void setNoteData(int index, float value) {
        if (null == tMoneyBeanList || tMoneyBeanList.size() == 0)
            return;
        sort_image = tMoneyBeanList.get(index).getSortImg();
        sort_name = tMoneyBeanList.get(index).getSortName();
        if (TYPE) {
            money.setText("-" + tMoneyBeanList.get(index).getMoney());
        } else {
            money.setText("+" + tMoneyBeanList.get(index).getMoney());
        }
        DecimalFormat df = new DecimalFormat("0.00%");
        title.setText(sort_name+" : "+df.format(value));
        rankTitle.setText(sort_name + "排行榜");
        circleBg.setImageDrawable(new ColorDrawable(colors.get(index)));//背景色
        circleImg.setImageDrawable(PieChartUtils.getDrawable(tMoneyBeanList.get(index).getSortImg()));
        adapter.setItems(tMoneyBeanList.get(index).getList());
        adapter.notifyDataSetChanged();

    }

    /*****************************************************************************/
    @Override
    protected MonthChartContract.Presenter bindPresenter() {
        return new MonthChartPresenter();
    }

    @Override
    public void loadDataSuccess(MonthChartBean bean) {
        monthChartBean = bean;
        setReportData();
    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onFailure(Throwable e) {
        SnackbarUtils.show(mActivity, e.getMessage());
    }


    /**
     * 显示日期选择器
     */
    public void showTimeSelector(String type1) {
        new DatePickerDialog(getActivity(), (DatePicker datePicker, int i, int i1, int i2) -> {
            mYear = i;
            mMonth = i1 + 1;
            mDay = i2;
            if (mMonth < 10) {
                if (mDay < 10) {
                    days = new StringBuffer().append(mYear).append("-").append("0").
                            append(mMonth).append("-").append("0").append(mDay).toString();
                } else {
                    days = new StringBuffer().append(mYear).append("-").append("0").
                            append(mMonth).append("-").append(mDay).toString();
                }

            } else {
                if (mDay < 10) {
                    days = new StringBuffer().append(mYear).append("-").
                            append(mMonth).append("-").append("0").append(mDay).toString();
                } else {
                    days = new StringBuffer().append(mYear).append("-").
                            append(mMonth).append("-").append(mDay).toString();
                }
            }
            if ("0".equals(type1)) {
                startTime.setText(days);
            } else {
                endTime.setText(days);
            }
            status = 1;
        }, mYear, mMonth-1, mDay).show();
    }

}
