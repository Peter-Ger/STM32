package com.example.app;

import android.graphics.Color;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

public class LineChartFunction {

    private LineChart lineChart;  //定义一个chart类型的数据
    private YAxis leftAxis;       //定义左y轴
    private YAxis rightAxis;      //定义右y轴
    private XAxis xAxis;          //定义x 轴
    private LineDataSet lineDataSet;
    private LineData lineData;

    //一条曲线
    public LineChartFunction(LineChart mLineChart, String name, int color) {
        this.lineChart = mLineChart;
        leftAxis = lineChart.getAxisLeft();
        rightAxis = lineChart.getAxisRight();
        xAxis = lineChart.getXAxis();
        initLineChart();
        initLineDataSet(name, color);
    }
    /**
     * 初始化LineChar
     */
    private void initLineChart() {
        //网格的背景是灰色还是白色
        lineChart.setDrawGridBackground(false);
        //显示边界线，边界线被加粗
        lineChart.setDrawBorders(false);
        //折线图例 标签 设置
        Legend legend = lineChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextColor(Color.BLACK); //设置Legend 文本颜色
        legend.setTextSize(11f);//折线的对应名称字的大小
        //显示位置
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        //X轴设置显示位置在底部
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(10);
        //取消右边y轴的显示
        rightAxis.setEnabled(false); //右侧Y轴不显示
        //设置网格颜色
        leftAxis.setGridColor(Color.WHITE); //网格线颜色
        leftAxis.setAxisLineColor(Color.BLACK); //Y轴颜色
        xAxis.setGridColor(Color.WHITE); //网格线颜色
        xAxis.setAxisLineColor(Color.BLACK); //X轴颜色
        //保证Y轴从0开始，不然会上移一点
        leftAxis.setAxisMinimum(0f);
        rightAxis.setAxisMinimum(0f);
    }
    /**
     * 初始化折线(一条线)
     *
     * @param name
     * @param color
     */
    private void initLineDataSet(String name, int color) {

        lineDataSet = new LineDataSet(null, name);
        lineDataSet.setLineWidth(1.5f);
        lineDataSet.setCircleRadius(1.5f);
        lineDataSet.setColor(color);//设置除了点之外折线的颜色
        //设置曲线值的圆点是实心还是空心
        lineDataSet.setDrawCircleHole(false);//false  实心  true  空心
        lineDataSet.setCircleColor(color);//设置折线上点的颜色也为黑色
        lineDataSet.setHighLightColor(color);
        //设置曲线填充
        lineDataSet.setDrawFilled(true);//就是曲线下面的颜色
        lineDataSet.setFillColor(color);//填充颜色

        lineDataSet.setDrawValues(false);//曲线上每个点的值，这里是取消显示

        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setValueTextSize(10f);//设置显示值的字体大小
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);//线模式为圆滑曲线（默认折线）

        //添加一个空的 LineData
        lineData = new LineData();
        lineChart.setData(lineData);
        lineChart.invalidate();
    }
    /**
     * 动态添加数据（一条折线图）
     *
     * @param number
     */
    public void addEntry(int number) {

        //最开始的时候才添加 lineDataSet（一个lineDataSet 代表一条线）
        if (lineDataSet.getEntryCount() == 0) {
            lineData.addDataSet(lineDataSet);
        }
        lineChart.setData(lineData);
        Entry entry = new Entry(lineDataSet.getEntryCount(), number);
        Log.v("ssssssss", String.valueOf(number));
        lineData.addEntry(entry, 0);
        //通知数据已经改变
        lineData.notifyDataChanged();
        lineChart.notifyDataSetChanged();
        //设置在曲线图中显示的最大数量
        lineChart.setVisibleXRangeMaximum(20);
        //移到某个位置
        lineChart.moveViewToX(lineData.getEntryCount() - 5);
    }
    /**
     * 设置Y轴值
     *
     * @param max
     * @param min
     * @param labelCount
     */
    public void setYAxis(float max, float min, int labelCount) {
        if (max < min) {
            return;
        }
        leftAxis.setAxisMaximum(max);
        leftAxis.setAxisMinimum(min);
        leftAxis.setLabelCount(labelCount, false);

        rightAxis.setAxisMaximum(max);
        rightAxis.setAxisMinimum(min);
        rightAxis.setLabelCount(labelCount, false);
        lineChart.invalidate();
    }
}