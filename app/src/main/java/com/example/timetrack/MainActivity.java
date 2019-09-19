package com.example.timetrack;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    Timer mTimer;
    TimeList[] times;
    File newXml;
    int[] btn_ids = {R.id.btn, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        times = new TimeList[6];
        for (int i = 0; i < 6; i++) {
            times[i] = new TimeList(new ArrayList<Long>(), 0, (ToggleButton) findViewById(btn_ids[i]), i, i ,"Empty");
            times[i].btn.setOnCheckedChangeListener(this);
        }
        newXml = new File(getFilesDir() + "/new.xml");
        if (newXml.exists()) {
            System.out.println("true");
            readFile(newXml, times);
        }else{
            System.out.println("false");
        }
        mTimer = new Timer();
        startTimer();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (newXml.exists()) {
            newXml.delete();
        }
        writeData();
        System.out.println("Data saved");
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        for (TimeList i : times) {
            if (buttonView.getId() == i.getButton().getId()) {
                if (isChecked) {
                    if(i.getListOfExitEnterPoints().size()%2==1) return;
                    i.addEnterPoint(new Date().getTime());
                } else {
                    i.addExitPoint(new Date().getTime());
                    i.getButton().setTextOff(Objects.toString(i.getSpentTime() / 1000));
                }
            }
        }

    }

    public void onBtnClick(View view) {
        if(view.getId() == R.id.btn6){
            for(TimeList i: times){
                i.spentTime = 0;
                i.listOfExitEnterPoints = new ArrayList<Long>();
            }
        }
        for (TimeList i : times) {
            if (view.getId() != i.getButton().getId() && i.getButton().isChecked()) {
                i.getButton().setChecked(false);
            }
        }
    }

    protected void addTimeList(XmlSerializer serializer, TimeList timeList) {
        try {
            serializer.startTag(null, "TimeList");
            serializer.startTag(null, "Name");
            serializer.text(timeList.getName());
            serializer.endTag(null, "Name");
            serializer.startTag(null, "SpentTime");
            serializer.text(String.valueOf(timeList.getSpentTime()));
            serializer.endTag(null, "SpentTime");
            serializer.startTag(null, "btn");
            serializer.text(String.valueOf(timeList.getBtnNum()));
            serializer.endTag(null, "btn");
            serializer.startTag(null, "Number");
            serializer.text(String.valueOf(timeList.getNumber()));
            serializer.endTag(null, "Number");
            serializer.startTag(null, "ListOfPoints");
            for (int i = 0; i < timeList.getListOfExitEnterPoints().size(); i++) {
                serializer.startTag(null, "Point");
                serializer.text(String.valueOf(timeList.getListOfExitEnterPoints().get(i)));
                serializer.endTag(null, "Point");
            }
            serializer.endTag(null, "ListOfPoints");
            serializer.endTag(null, "TimeList");
        } catch (Exception E) {
        }
    }

    protected void createFile(long[] SpentTimes) {
        try {
            String filename = "new.xml";

            FileOutputStream fos;
            fos = openFileOutput(filename, Context.MODE_APPEND);

            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fos, "UTF-8");
            serializer.startDocument(null, true);

            serializer.startTag(null, "root");

            //addTimeList(serializer, SpentTimes[0], 0);

            serializer.endTag(null, "root");

            serializer.endDocument();
            serializer.flush();

            fos.close();
        } catch (Exception E) {
        }
    }

    protected void writeData() {
        try {
            String filename = "new.xml";

            FileOutputStream fos;
            fos = openFileOutput(filename, Context.MODE_APPEND);

            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fos, "UTF-8");
            serializer.startDocument(null, true);

            serializer.startTag(null, "root");

            for (TimeList i : times) {
                addTimeList(serializer, i);
            }

            serializer.endTag(null, "root");

            serializer.endDocument();
            serializer.flush();

            fos.close();
        } catch (Exception E) {

        }
    }

    public void readFileOld(File file, TimeList[] times) {
        try {
            // Создается построитель документа
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            // Создается дерево DOM документа из файла
            Document document = documentBuilder.parse(file);

            // Получаем корневой элемент
            Node root = document.getDocumentElement();

            System.out.println("List of books:");
            System.out.println();
            // Просматриваем все подэлементы корневого - т.е. книги
            NodeList records = root.getChildNodes();
            System.out.println(records.getLength());
            for (int i = 0; i < records.getLength(); i++) {
                Node record = records.item(i);
                int btn = Integer.valueOf(record.getChildNodes().item(1).getTextContent());
                long SpentTime = Long.valueOf(record.getChildNodes().item(0).getTextContent());
                times[btn].spentTime = SpentTime;
                System.out.println(record.getTextContent());
                System.out.println("===========>>>>");
            }

        } catch (ParserConfigurationException ex) {
            ex.printStackTrace(System.out);
        } catch (SAXException ex) {
            ex.printStackTrace(System.out);
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }
    }

    public void readFile(File file, TimeList[] times) {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(file);

            // Get the root el.
            Node root = document.getDocumentElement();

            System.out.println("List of lists");
            System.out.println();
            // Look through all lists
            NodeList TimeLists = root.getChildNodes();
            System.out.println(TimeLists.getLength());

            for (int i = 0; i < TimeLists.getLength(); i++) {
                Node TimeList = TimeLists.item(i);
                String name = TimeList.getChildNodes().item(0).getTextContent();
                long SpentTime = Long.valueOf(TimeList.getChildNodes().item(1).getTextContent());
                int btn = Integer.valueOf(TimeList.getChildNodes().item(2).getTextContent());
                int number = Integer.valueOf(TimeList.getChildNodes().item(3).getTextContent());
                Node ListOfPoints = TimeList.getChildNodes().item(4);
                for (int j = 0; j < ListOfPoints.getChildNodes().getLength(); j++) {
                    times[number].listOfExitEnterPoints.add(Long.valueOf(ListOfPoints.getChildNodes().item(j).getTextContent()));
                }
                times[number].spentTime = SpentTime;
                times[number].btn = findViewById(btn_ids[btn]);
                times[number].name = name;
                times[number].number = number;
                times[number].btn.setTextOff(String.valueOf(SpentTime));
                if (times[number].listOfExitEnterPoints.size() % 2 == 1) {

                    times[number].btn.setChecked(true);
                }

            }

        } catch (ParserConfigurationException ex) {
            ex.printStackTrace(System.out);
        } catch (SAXException ex) {
            ex.printStackTrace(System.out);
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }
    }

    private void startTimer() {
        mTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                btnsUpdate();
            }
        }, 1000, 1000); //Первое значение - через сколько происходит запуск таймера, Второе - период срабатываний. В данном случае 1сек и 1сек.
    }

    public void btnsUpdate() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                for (TimeList i : times) {
                    i.btn.setTextOff(String.valueOf(i.spentTime));
                    if (i.btn.isChecked()) {
                        System.out.println(i.spentTime / 1000 + (new Date().getTime() - i.getLastPoint()) / 1000);
                        TextView txt = findViewById(R.id.textView);
                        txt.setText(String.valueOf(i.spentTime / 1000 + (new Date().getTime() - i.getLastPoint()) / 1000));
                    }
                }

            }
        });
    }


}
