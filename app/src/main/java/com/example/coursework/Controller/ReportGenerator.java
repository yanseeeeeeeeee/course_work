package com.example.coursework.Controller;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ReportGenerator {

    public static File generateReport(Context context, List<ModelRecords> records, boolean isPdf) {

        // Проверка разрешения на запись
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
            Toast.makeText(context, "Разрешение на запись не предоставлено", Toast.LENGTH_SHORT).show();
            return null;
        }

        // Папка "Загрузки"
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!downloadsDir.exists()) downloadsDir.mkdirs();

        File file;

        if (isPdf) {
            file = new File(downloadsDir, "report.pdf");

            try {
                PdfDocument pdf = new PdfDocument();
                PdfDocument.PageInfo info = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
                PdfDocument.Page page = pdf.startPage(info);

                Canvas canvas = page.getCanvas();
                Paint paint = new Paint();
                paint.setTextSize(12);

                int y = 30;
                for (ModelRecords record : records) {
                    canvas.drawText(
                            record.date + " | " + record.adress + " | " + record.narushenia_name + " | " + record.passport + " | " + record.coment,
                            10, y, paint
                    );
                    y += 20;
                }

                pdf.finishPage(page);
                pdf.writeTo(new FileOutputStream(file));
                pdf.close();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Ошибка при создании PDF", Toast.LENGTH_SHORT).show();
            }

        } else {
            file = new File(downloadsDir, "report.csv");

            try (FileWriter writer = new FileWriter(file)) {
                writer.append("Дата,Адрес,Нарушение,Паспорт,Комментарий\n");
                for (ModelRecords record : records) {
                    writer.append(record.date).append(",")
                            .append(record.adress).append(",")
                            .append(record.narushenia_name).append(",")
                            .append(record.passport).append(",")
                            .append(record.coment != null ? record.coment : "")
                            .append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Ошибка при создании CSV", Toast.LENGTH_SHORT).show();
            }
        }

        Toast.makeText(context, "Отчёт создан в Загрузках", Toast.LENGTH_LONG).show();
        return file;
    }
}
