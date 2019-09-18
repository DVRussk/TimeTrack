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
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.widget.CompoundButton;
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
    TimeList[] times;
    File newXml;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int[] btn_ids = {R.id.btn, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6};
        times = new TimeList[6];
        for (int i = 0; i < 6; i++) {
            times[i] = new TimeList(new ArrayList<Long>(), 0, (ToggleButton) findViewById(btn_ids[i]));
            times[i].btn.setOnCheckedChangeListener(this);
        }
        newXml = new File(getFilesDir() + "/new.xml");
        if (newXml.exists()) {
            System.out.println("true");
        } else {
            System.out.println("false");
        }
        readFile(newXml, times);

    }

    @Override
    protected void onStop() {
        super.onStop();
        newXml.delete();
        createFile(new long[]{times[0].getSpentTime(),times[1].getSpentTime(),times[2].getSpentTime(),
                times[3].getSpentTime(),times[4].getSpentTime(),times[5].getSpentTime()});
    }

    public void onBtnClick(View view) {
        for (TimeList i : times) {
            if (view.getId() != i.getButton().getId() && i.getButton().isChecked()) {
                i.getButton().setChecked(false);
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        for (TimeList i : times) {
            if (buttonView.getId() == i.getButton().getId()) {
                if (isChecked) {
                    i.addEnterPoint(new Date().getTime());
                } else {
                    i.addExitPoint(new Date().getTime());
                    i.getButton().setTextOff(Objects.toString(i.getSpentTime() / 1000));
                }
            }
        }

    }

    protected void addTimeList(XmlSerializer serializer, long SpentTime, int btn){
        try {
            serializer.startTag(null, "TimeList");
            serializer.startTag(null, "SpentTime");
            serializer.text(String.valueOf(SpentTime));
            serializer.endTag(null, "SpentTime");
            serializer.startTag(null, "btn");
            serializer.text(String.valueOf(btn));
            serializer.endTag(null, "btn");
            serializer.endTag(null, "TimeList");
        }catch (Exception E){}
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

            addTimeList(serializer, SpentTimes[0], 0);
            addTimeList(serializer, SpentTimes[1], 1);
            addTimeList(serializer, SpentTimes[2], 2);
            addTimeList(serializer, SpentTimes[3], 3);
            addTimeList(serializer, SpentTimes[4], 4);
            addTimeList(serializer, SpentTimes[5], 5);

            serializer.endTag(null, "root");

            serializer.endDocument();
            serializer.flush();

            fos.close();
        } catch (Exception E) {
        }
    }

    public void readFile(File file, TimeList[] times) {
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
}
