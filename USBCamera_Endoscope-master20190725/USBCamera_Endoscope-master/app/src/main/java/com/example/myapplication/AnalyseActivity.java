package com.example.myapplication;

import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.example.myapplication.base.DemoBase;
import com.example.myapplication.entity.BundleData;


import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class AnalyseActivity extends DemoBase implements OnChartValueSelectedListener {
    private LineChart chart;
    XAxis xAxis;
    YAxis yAxis;


    private List mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_analyse);
        //设置当前acitivity的名称
        setTitle("最近菌斑占比变化情况");

        chart = findViewById(R.id.chart1);

        // background color
        chart.setBackgroundColor(Color.WHITE);

        // disable description text
        chart.getDescription().setEnabled(false);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // set listeners
        chart.setOnChartValueSelectedListener(this);
        chart.setDrawGridBackground(false);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        // chart.setScaleXEnabled(true);
        // chart.setScaleYEnabled(true);

        // force pinch zoom along both axis
        chart.setPinchZoom(true);

        // // X-Axis Style // //
        xAxis = chart.getXAxis();

        // vertical grid lines
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置x轴显示位置

        yAxis = chart.getAxisLeft();

        // disable dual axis (only use LEFT axis)
        chart.getAxisRight().setEnabled(false);

        // // Y-Axis Style // //
        // horizontal grid lines
        yAxis.enableGridDashedLine(10f, 10f, 0f);

        // axis range
        yAxis.setAxisMaximum(100f);
        yAxis.setAxisMinimum(0f);

        LimitLine llXAxis = new LimitLine(9f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);
        llXAxis.setTypeface(tfRegular);

        //表格中的限制线，上线条
        LimitLine ll1 = new LimitLine(80f, "菌斑正常范围上限");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);
        ll1.setTypeface(tfRegular);

        LimitLine ll2 = new LimitLine(20f, "菌斑正常范围下限");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);
        ll2.setTypeface(tfRegular);

        // 画出数据后的背景线
        yAxis.setDrawLimitLinesBehindData(true);
        xAxis.setDrawLimitLinesBehindData(true);

        // 添加上下限制线
        yAxis.addLimitLine(ll1);
        yAxis.addLimitLine(ll2);
        //xAxis.addLimitLine(llXAxis);

        setData(7, 100);

        // draw points over time
//        chart.animateX(1500);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();

        // draw legend entries as lines
        l.setForm(Legend.LegendForm.LINE);
    }

    //参数1 ： 数据数量
    //参数2 ： 随机出来的数据范围
    private void setData(int count, float range) {
        mData = getData();

        //设置x轴坐标 自定义内容适配器
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return ((Entry)mData.get((int) value)).getData() + "";
            }
        });
        //设置y轴坐标 自定义内容适配器
        yAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return value + "%";
            }
        });


        if (mData.size() == 0) return;

        LineDataSet set1;
        //判断图表中原来是否有数据
        if(chart.getData() != null && chart.getData().getDataSetCount() > 0){
            //获取数据1
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(mData);
            //刷新数据
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        }else {
            //设置数据1  参数1：数据源 参数2：图例名称
            set1 = new LineDataSet(mData, "菌斑面占比");
            set1.setDrawIcons(false);

            // draw dashed line
            set1.enableDashedLine(10f, 5f, 0f);

            // black lines and points
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);

            // line thickness and point size
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);

            // draw points as solid circles
            set1.setDrawCircleHole(false);

            // customize legend entry
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            // text size of values
            set1.setValueTextSize(9f);

            // draw selection line as dashed
            set1.enableDashedHighlightLine(10f, 5f, 0f);

            // set the filled area
            set1.setDrawFilled(true);

            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return chart.getAxisLeft().getAxisMinimum();
                }
            });


            //格式化显示数据
            final DecimalFormat mFormat = new DecimalFormat("###,###,##0");
            set1.setValueFormatter(new ValueFormatter(){
                @Override
                public String getFormattedValue(float value) {
                    return mFormat.format(value);
                }
            });

            // set color of filled area
            if (Utils.getSDKInt() >= 18) {
                // drawables only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
                set1.setFillDrawable(drawable);
            } else {
                set1.setFillColor(Color.BLACK);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the data sets

            // create a data object with the data sets
            LineData data = new LineData(dataSets);
            // set data
            chart.setData(data);
        }
    }

    //数据绑定
    private List getData() {
        List<HistoryLog> historyList = (List<HistoryLog>) ((BundleData) getIntent().getSerializableExtra("historyData")).getData();

        Log.i("他来了他来:___",historyList.get(0).getDiagno().trim().substring(1,historyList.get(0).getDiagno().trim().length()));
        ArrayList mData2 = new ArrayList<>();
        float diagno;
        int i = 0;
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        for (HistoryLog historyLog:historyList) {
            try {
                diagno = Float.parseFloat(historyLog.getDiagno().trim().substring(0,historyLog.getDiagno().trim().length()-1));
            } catch (Exception e) {
                diagno = 0;
                Log.i("图标显示","数据转换失败");
            }
            if(i<=7)//只取出前七条数据
                mData2.add(new Entry(i++,diagno,historyLog.getdate().substring(4,8)));
        }
//        mData2.add(new Entry(1, 15,"08-04"));
//        mData2.add(new Entry(2, 25,"08-06"));
//        mData2.add(new Entry(3, 19,"08-08"));
//        mData2.add(new Entry(4, 25,"08-10"));
//        mData2.add(new Entry(5, 16,"08-12"));
//        mData2.add(new Entry(6, 40,"08-22"));
//        mData2.add(new Entry(7, 24,"08-18"));
//        mData2.add(new Entry(8, 27,"08-21"));
        return mData2;
    }

    @Override
    protected void saveToGallery() {
        saveToGallery(chart, "LineChartActivity1");
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
        Log.i("LOW HIGH", "low: " + chart.getLowestVisibleX() + ", high: " + chart.getHighestVisibleX());
        Log.i("MIN MAX", "xMin: " + chart.getXChartMin() + ", xMax: " + chart.getXChartMax() + ", yMin: " + chart.getYChartMin() + ", yMax: " + chart.getYChartMax());
    }


    @Override
    public void onNothingSelected() {

    }
}
