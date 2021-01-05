package com.gmail.theonlyonechanel.simplecollect;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {
    int quantity=0;
    String[] data = {"шт", "кг", "л", "м", "м2", "м3"};
   String goods_parametrs = "";
    final String LOG_TAG = "myLogs.txt";
    final String FILENAME = "list.txt";
    final String DIR_SD = "";
    //final String FILENAME_SD = "fileSD";
    /** Called when the activity is first created. */
    //кнопка сканирования и текствью его отображения
    private Button scanbtn;
    private TextView contentTxt;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //выбор ед.измерения
        Spinner spinner = (Spinner) findViewById(R.id.measure);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        // заголовок
        spinner.setPrompt("Ед. измерения");
        // выделяем элемент
        spinner.setSelection(0);
        // устанавливаем обработчик нажатия
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // показываем позиция нажатого элемента
                Toast.makeText(getBaseContext(), "Позиция" + position, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        scanbtn = (Button)findViewById(R.id.codebar);
        contentTxt = (TextView)findViewById(R.id.codebar_txt);
        scanbtn.setOnClickListener(this);
    }

    public void onclick(View v) {
        switch (v.getId()) {
            case R.id.btnWrite:
                writeFile();
                break;
            case R.id.btnWriteSD:
                writeFileSD();
                break;
            case R.id.save:
                saving();
                break;
            /*case R.id.codebar:
                scaning_code();
                break;*/
            case R.id.decrement:
                decrement();
                break;
            case R.id.increment:
                increment();
                break;
        }
    }

    private void display(int number) {
        EditText quantity_Goods= (EditText)findViewById(R.id.quantity);
        quantity_Goods.setText("" + number);
    }
    private void increment() {
        quantity= quantity+1;
        display(quantity);
    }

    private void decrement() {
        quantity = quantity-1;
        display(quantity);
        if(quantity<0)
            display(0);
    }

    void writeFile() {
        try {
            // отрываем поток для записи
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    openFileOutput(FILENAME, MODE_PRIVATE)));
            // пишем данные
                bw.write(goods_parametrs);
            // закрываем поток
            bw.close();
            Log.d(LOG_TAG, "Файл записан");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void writeFileSD() {
        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
            return;
        }
        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + DIR_SD);
        // создаем каталог
        sdPath.mkdirs();
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(sdPath, FILENAME);
        try {
            // открываем поток для записи
            BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
            // пишем данные
            bw.write( goods_parametrs);
            // закрываем поток
            bw.close();
            Log.d(LOG_TAG, "Файл записан на SD: " + sdFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //сканировать штрихкод
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.codebar) {
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();
            TextView codebar_enter = (TextView) findViewById(R.id.codebar_txt);
            codebar_enter.setText("Скоро это дерьмо будет работать!");
        }
    }
    //обработка полученного штрихкода
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
       //если данные получены
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
           // String scanFormat = scanningResult.getFormatName();
            contentTxt.setText(scanContent);
        }
        //если не удалось прочесть, выводим сообщение
        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Не удалось считать код!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    private void saving() {
        //добавляем наименование товара
        EditText name_goods = (EditText) findViewById(R.id.nameGoods);
        //добавляем строку ед.измерения
        Spinner spinner = (Spinner) findViewById(R.id.measure);
        //добавляем артикул
        EditText vendCode_goods = (EditText) findViewById(R.id.vendorСode);
//добавляем количество
        EditText quantity_Goods= (EditText)findViewById(R.id.quantity);
        //добавляем цену
        EditText price_value = (EditText)findViewById(R.id.price);
        //добавляем штрихкод
        EditText codebar_handle_enter = (EditText) findViewById(R.id.codebar_text);
        TextView codebar_enter = (TextView) findViewById(R.id.codebar_txt);
        String codeBar_quality = codebar_enter.getText().toString();
        if (codebar_enter.getText().toString().equals(""))
            codeBar_quality=codebar_handle_enter.getText().toString();

        //создаем строку параметров товара для сохранения в список

String parametr = name_goods.getText().toString()+","+spinner.getSelectedItem().toString()+
        ","+vendCode_goods.getText().toString() +","+quantity_Goods.getText().toString() +","
        +price_value.getText().toString()+","+codeBar_quality+ ";";
//записываем параметры в список
        goods_parametrs= goods_parametrs+parametr;
        //выводим сообщение об успешной записи
        Toast toast = Toast.makeText(MainActivity.this,
                "Товар записан", Toast.LENGTH_SHORT);
        toast.show();
        //очищаем поля ввода
        name_goods.setText("");
        vendCode_goods.setText("");
        quantity_Goods.setText("");
        codebar_handle_enter.setText("");
        price_value.setText("");

    }


}