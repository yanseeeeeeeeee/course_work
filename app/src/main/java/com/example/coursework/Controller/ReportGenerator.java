package com.example.coursework.Controller;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.OutputStream;
import java.util.List;

public class ReportGenerator {

    public static void generateReport(
            Context context,
            List<ReportRecord> records,
            InspectorModel inspector,
            boolean isPdf
    ) {
        String fileName = isPdf ? "Отчёт_инспектора.pdf" : "Отчёт_инспектора.csv";

        try {
            Uri fileUri;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                values.put(MediaStore.Downloads.MIME_TYPE,
                        isPdf ? "application/pdf" : "text/csv");

                fileUri = context.getContentResolver()
                        .insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
            } else {
                Toast.makeText(context,
                        "Поддерживается только Android 10+",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (fileUri == null) {
                Toast.makeText(context,
                        "Ошибка создания файла",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            try (OutputStream out = context.getContentResolver().openOutputStream(fileUri)) {

                if (isPdf) {
                    PdfDocument pdf = new PdfDocument();
                    PdfDocument.PageInfo pageInfo =
                            new PdfDocument.PageInfo.Builder(595, 842, 1).create();

                    PdfDocument.Page page = pdf.startPage(pageInfo);
                    Canvas canvas = page.getCanvas();

                    Paint titlePaint = new Paint();
                    titlePaint.setTextSize(16);
                    titlePaint.setFakeBoldText(true);

                    Paint textPaint = new Paint();
                    textPaint.setTextSize(11);

                    int y = 40;
                    int lineHeight = 16;

                    // ===== ШАПКА =====
                    canvas.drawText("Министерство внутренних дел", 40, y, titlePaint);
                    y += 25;

                    canvas.drawText("Подразделение: " + inspector.department, 40, y, textPaint);
                    y += 25;

                    canvas.drawText("ОТЧЁТ О ВЫЯВЛЕННЫХ НАРУШЕНИЯХ", 40, y, titlePaint);
                    y += 30;

                    canvas.drawText("Инспектор: " + inspector.getFullName(), 40, y, textPaint);
                    y += 20;

                    canvas.drawText(
                            "Дата формирования: " +
                                    java.text.DateFormat.getDateInstance().format(new java.util.Date()),
                            40, y, textPaint
                    );
                    y += 30;

                    // ===== КОЛОНКИ =====
                    int colDate = 40;
                    int colCitizen = 110;
                    int colAddress = 230;
                    int colViolation = 360;
                    int colPassport = 500;

                    int wDate = 60;
                    int wCitizen = 110;
                    int wAddress = 120;
                    int wViolation = 120;
                    int wPassport = 80;

                    titlePaint.setTextSize(12);
                    canvas.drawText("Дата", colDate, y, titlePaint);
                    canvas.drawText("Гражданин", colCitizen, y, titlePaint);
                    canvas.drawText("Адрес", colAddress, y, titlePaint);
                    canvas.drawText("Нарушение", colViolation, y, titlePaint);
                    canvas.drawText("Паспорт", colPassport, y, titlePaint);
                    y += lineHeight;

                    // ===== ДАННЫЕ =====
                    for (ReportRecord r : records) {

                        int startY = y;

                        int h1 = drawWrapped(canvas, textPaint, r.date, colDate, startY, wDate);
                        int h2 = drawWrapped(canvas, textPaint, r.citizenFio, colCitizen, startY, wCitizen);
                        int h3 = drawWrapped(canvas, textPaint, r.address, colAddress, startY, wAddress);
                        int h4 = drawWrapped(canvas, textPaint, r.violation, colViolation, startY, wViolation);
                        int h5 = drawWrapped(canvas, textPaint, r.passport, colPassport, startY, wPassport);

                        // высота строки = максимум из всех колонок
                        y = startY + Math.max(
                                Math.max(h1, h2),
                                Math.max(Math.max(h3, h4), h5)
                        ) + 6;

                        if (y > 800) {
                            pdf.finishPage(page);
                            page = pdf.startPage(pageInfo);
                            canvas = page.getCanvas();
                            y = 40;
                        }
                    }

                    pdf.finishPage(page);
                    pdf.writeTo(out);
                    pdf.close();

                } else {
                    // ===== CSV =====
                    StringBuilder sb = new StringBuilder();
                    sb.append("Инспектор;").append(inspector.getFullName()).append("\n");
                    sb.append("Подразделение;").append(inspector.department).append("\n");
                    sb.append("Дата формирования;")
                            .append(java.text.DateFormat.getDateInstance()
                                    .format(new java.util.Date()))
                            .append("\n\n");

                    sb.append("Дата;Гражданин;Адрес;Нарушение;Паспорт\n");

                    for (ReportRecord r : records) {
                        sb.append(r.date).append(";")
                                .append(r.citizenFio).append(";")
                                .append(r.address).append(";")
                                .append(r.violation).append(";")
                                .append(r.passport)
                                .append("\n");
                    }

                    out.write(sb.toString().getBytes());
                }
            }

            Toast.makeText(context,
                    "Отчёт сохранён в Загрузки: " + fileName,
                    Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context,
                    "Ошибка создания отчёта",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // ===== ПЕРЕНОС ТЕКСТА В КОЛОНКЕ =====
    private static int drawWrapped(
            Canvas canvas,
            Paint paint,
            String text,
            int x,
            int y,
            int maxWidth
    ) {
        if (text == null) text = "";

        String[] words = text.split(" ");
        String line = "";
        int startY = y;

        for (String word : words) {
            String test = line.isEmpty() ? word : line + " " + word;

            if (paint.measureText(test) > maxWidth) {
                canvas.drawText(line, x, y, paint);
                y += 16;
                line = word;
            } else {
                line = test;
            }
        }

        if (!line.isEmpty()) {
            canvas.drawText(line, x, y, paint);
            y += 16;
        }

        return y - startY;
    }
}
