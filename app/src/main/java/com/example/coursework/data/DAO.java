package com.example.coursework.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import com.example.coursework.Controller.InspectorModel;
import com.example.coursework.Controller.ModelRecords;
import com.example.coursework.Controller.ReportRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//класс для работы с данными, создание методов для извлечения / записи
public class DAO {

    private SQLiteDatabase db;

    public DAO(SQLiteDatabase db) {
        this.db = db;
    }


    //список с названиями нарушений
    public List<String> getNameNarushenia() {
        List<String> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select name from narushenia", null);//запросик к бд

        if(cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    //проверка логина и пароля
    public boolean checkUser(String login, String password) {

        String query = "select * from inspector where login = ? and password = ?";
        Cursor cursor = db.rawQuery(query, new String[]{login, password});

        boolean exists = false;
        if(cursor.moveToFirst()){
            exists=true;
        }
        cursor.close();
        return exists;

    }

    //проверка существования такого пользователя
    public boolean checkLogin(String login) {
        String query = "select * from inspector where login = ?";
        Cursor cursor = db.rawQuery(query,new String[]{login});

        boolean exists = false;
        if(cursor.moveToFirst()) {
            exists=true;
        }
        cursor.close();
        return  exists;
    }


    //для регистрации пользователя
    public long insertUser(String password, String login,
            String name, String surname, String lastName,
                           String departament, String post) {

        ContentValues values = new ContentValues();
        values.put("password", password);
        values.put("login", login);
        values.put("name", name);
        values.put("last_name", surname);
        if (lastName == null || lastName.trim().isEmpty()) {
            values.putNull("second_name");
        } else {
            values.put("second_name", lastName.trim());
        }
        values.put("departament", departament);
        values.put("post", post);

        try {
            return db.insertOrThrow("inspector", null, values);
        } catch (SQLiteConstraintException e) {
            return -1;
        }

    }


    //получаем имя нашего инспектора по логину
    public String getName(String login) {
        String name = null;
        Cursor cursor = db.rawQuery("select name from inspector where login = ?", new String[]{login});

        if (cursor.moveToFirst()) {
            name = cursor.getString(0);
        }

        cursor.close();
        return name;
    }

    //добавление записи в бд
    public boolean addRecords(String date, String adress, String passport,
                           String coment, String narusheniaName,
                              int idInspector) {

        db.beginTransaction();

        try {
            int idNarushenia;

            Cursor cursor1 = db.rawQuery("select id from narushenia where name =?", new String[]{narusheniaName});

            if (!cursor1.moveToFirst()) {
                cursor1.close();
                return false;
            }
            idNarushenia=cursor1.getInt(0);
            cursor1.close();


            int idGrazhdane;

            Cursor cursor2 = db.rawQuery("Select id from grazhdane where passport = ?", new String[]{passport});

            if (!cursor2.moveToFirst()) {
                cursor2.close();
                return false;
            }

            idGrazhdane=cursor2.getInt(0);
            cursor2.close();

            int idNarushGrazh;

            Cursor cursor3 = db.rawQuery("select id from narushenia_grazhdane where id_narushenia = ? and id_grazhdane = ?",
                    new String[]{String.valueOf(idNarushenia),
                            String.valueOf(idGrazhdane)});

            if(cursor3.moveToFirst()) {
                idNarushGrazh = cursor3.getInt(0);
            }
            else {
                ContentValues value = new ContentValues();
                value.put("id_narushenia",idNarushenia);
                value.put("id_grazhdane", idGrazhdane);

                long res = db.insert("narushenia_grazhdane", null, value);
                if (res == -1) {
                    cursor3.close();
                    return false;
                }
                idNarushGrazh = (int) res;
            }
            cursor3.close();

            ContentValues values = new ContentValues();
            values.put("date",date);
            values.put("id_narushenia_grazhdane", idNarushGrazh);
            values.put("adress", adress);
            values.put("coment", coment);
            values.put("id_inspector", idInspector);

            db.insertOrThrow("records", null, values);
            db.setTransactionSuccessful();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
        }

    }

    //получение id инспектора
    public int getIdInspector(String login) {
        int idInspector = -1;
        Cursor cursor = db.rawQuery("select id from inspector where login = ?", new String[]{login});
        if (cursor.moveToFirst()){
            idInspector = cursor.getInt(0);
        }
        cursor.close();
        return idInspector;
    }

    //для получения записей на главном экране
    public List<ModelRecords> getRecordsByInspector(int inspectorId) {
        List<ModelRecords> list = new ArrayList<>();

        Cursor cursor = db.rawQuery(
                "SELECT r.date, r.adress, n.name, g.passport, r.coment " +
                        "FROM records r " +
                        "JOIN narushenia_grazhdane ng ON r.id_narushenia_grazhdane = ng.id " +
                        "JOIN narushenia n ON ng.id_narushenia = n.id " +
                        "JOIN grazhdane g ON ng.id_grazhdane = g.id " +
                        "WHERE r.id_inspector = ? " +
                        "ORDER BY r.date DESC",
                new String[]{String.valueOf(inspectorId)}
        );

        if (cursor.moveToFirst()) {
            do {
                list.add(new ModelRecords(
                        cursor.getString(0), // date
                        cursor.getString(1), // address
                        cursor.getString(3), // passport
                        cursor.getString(4), // comment
                        cursor.getString(2)  // violation (narushenia_name)
                ));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }

    //для счетчика записей в профиле
    public int getRecordsCountByInspector(int inspectorId) {
        int count = 0;
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM records WHERE id_inspector = ?",
                new String[]{String.valueOf(inspectorId)}
        );

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    //для создания отчетов
    public List<ModelRecords> getRecordsByInspectorAndDate(int inspectorId, String startDate, String endDate) {
        List<ModelRecords> list = new ArrayList<>();

        String query = "SELECT r.date, r.adress, g.passport, r.coment, n.name " +
                "FROM records r " +
                "JOIN narushenia_grazhdane ng ON r.id_narushenia_grazhdane = ng.id " +
                "JOIN narushenia n ON ng.id_narushenia = n.id " +
                "JOIN grazhdane g ON ng.id_grazhdane = g.id " +
                "WHERE r.id_inspector = ? AND r.date BETWEEN ? AND ? " +
                "ORDER BY r.date DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(inspectorId), startDate, endDate});

        if (cursor.moveToFirst()) {
            do {
                list.add(new ModelRecords(
                        cursor.getString(0), // date
                        cursor.getString(1), // address
                        cursor.getString(2), // passport
                        cursor.getString(3), // comment
                        cursor.getString(4)  // violation
                ));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }

    //получаем информацию об инспекторе для отчетов
    public InspectorModel getInspectorInfo(int inspectorId) {
        InspectorModel inspector = null;

        Cursor cursor = db.rawQuery(
                "SELECT last_name, name, second_name, departament " +
                        "FROM inspector WHERE id = ?",
                new String[]{String.valueOf(inspectorId)}
        );

        if (cursor.moveToFirst()) {
            inspector = new InspectorModel(
                    cursor.getString(0), // lastName
                    cursor.getString(1), // firstName
                    cursor.getString(2), // patronymic
                    cursor.getString(3)  // department
            );
        }

        cursor.close();
        return inspector;
    }



    public List<ReportRecord> getReportRecords(
            int inspectorId,
            String startDate,
            String endDate
    ) {
        List<ReportRecord> list = new ArrayList<>();

        Cursor cursor = db.rawQuery(
                "SELECT r.date, " +
                        "g.last_name || ' ' || g.name || ' ' || IFNULL(g.second_name, '') AS citizen_fio, " +
                        "r.adress, " +
                        "n.name AS violation, " +
                        "g.passport " +
                        "FROM records r " +
                        "JOIN narushenia_grazhdane ng ON r.id_narushenia_grazhdane = ng.id " +
                        "JOIN narushenia n ON ng.id_narushenia = n.id " +
                        "JOIN grazhdane g ON ng.id_grazhdane = g.id " +
                        "WHERE r.id_inspector = ? AND r.date BETWEEN ? AND ? " +
                        "ORDER BY r.date",
                new String[]{
                        String.valueOf(inspectorId),
                        startDate,
                        endDate
                }
        );

        if (cursor.moveToFirst()) {
            do {
                list.add(new ReportRecord(
                        cursor.getString(0), // date
                        cursor.getString(1), // citizenFio
                        cursor.getString(2), // address
                        cursor.getString(3), // violation
                        cursor.getString(4)  // passport
                ));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }

    //получение записи для дальнейшего ее изменения
    public ModelRecords getRecordById(int recordId) {
        ModelRecords record = null;

        Cursor cursor = db.rawQuery(
                "SELECT r.date, " +
                        "r.adress, " +
                        "r.coment, " +
                        "n.name AS violation, " +
                        "g.passport " +
                        "FROM records r " +
                        "JOIN narushenia_grazhdane ng ON r.id_narushenia_grazhdane = ng.id " +
                        "JOIN narushenia n ON ng.id_narushenia = n.id " +
                        "JOIN grazhdane g ON ng.id_grazhdane = g.id " +
                        "WHERE r.id = ?",
                new String[]{String.valueOf(recordId)}
        );

        if (cursor.moveToFirst()) {
            record = new ModelRecords(
                    cursor.getString(0), // date
                    cursor.getString(1), // address
                    cursor.getString(4), // passport
                    cursor.getString(2), // comment
                    cursor.getString(3)  // violation
            );
        }

        cursor.close();
        return record;
    }

    //для обновления записи

    // Обновляем только date, adress и coment, не трогая ng.id
    public boolean updateRecord(int recordId, String date, String adress, String passport, String coment, String narusheniaName) {
        try {
            db.execSQL(
                    "UPDATE records SET date = ?, adress = ?, coment = ? WHERE id = ?",
                    new Object[]{date, adress, coment, recordId}
            );
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}









