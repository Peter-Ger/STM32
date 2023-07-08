package com.example.app;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
public class huanjingwendu extends AppCompatActivity {
    TextView s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_huanjingwendu);
        LayoutInflater factory = LayoutInflater.from(huanjingwendu.this);//获取dialog布局文件获取View
        final View huanjingwenduView = factory.inflate(R.layout.huanjingwendu, null);
        final LineChart huanjingwendu_chart =huanjingwenduView.findViewById(R.id.huanjingshidu_line);
        LineChartFunction huanjingwendu_LineChart1 = new LineChartFunction(huanjingwendu_chart, "环境温度", Color.BLUE);
        huanjingwendu_LineChart1.setYAxis(100, 0, 1);
        for(int i=0;i<100;i++){
            huanjingwendu_LineChart1.addEntry(i);}
        s =(TextView) findViewById(androidx.constraintlayout.widget.R.id.wrap_content);
    }
}