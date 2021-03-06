package com.example.admin.janjaruka.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.admin.janjaruka.Bylaw_item;
import com.example.admin.janjaruka.Law_categories;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 29/06/2017.
 */

public class LawsSQLiteHandler extends SQLiteOpenHelper {
    private static final String TAG = SQLiteHandler.class.getSimpleName();
    //database version
    private static final int DATABASE_VERSION = 2;
    //database name
    private static final String DATABASE_NAME = "janjaruka_laws";
    //categories table name
    private static final String TABLE_CATEGORIES = "categories";
    private static final String TABLE_BYLAWS = "bylaws";

    //Fiels in the categories table
    private static final String KEY_CATEGORY_ID = "category_id";
    private static final String KEY_CATEGORY_TEXT = "category_text";
    private static final String KEY_CATEGORY_ICON = "category_icon";

    //Fields in the bylaws table;
    private static final String KEY_BYLAW_ID = "bylaw_id";
    private static final String KEY_BYLAW_TEXT = "bylaw_text";
    private static final String KEY_PENALTY = "penalty";
    private static final String KEY_BYLAW_CATEGORY_ID = "bylaw_category_id";

    Context context;
    private Integer category_icon_integer;
    private String  category_icon_str;

    public LawsSQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_BYLAWS_TABLE = "CREATE TABLE IF NOT EXISTS "+ TABLE_BYLAWS + "("
                +KEY_BYLAW_ID + " INTEGER PRIMARY KEY, "
                +KEY_BYLAW_CATEGORY_ID +" TEXT, "
                +KEY_BYLAW_TEXT +" TEXT, "
                +KEY_PENALTY +" TEXT "
                + ")";

        String CREATE_CATEGORIES_TABLE = "CREATE TABLE "+ TABLE_CATEGORIES + "("
                +KEY_CATEGORY_ID + " INTEGER PRIMARY KEY, "
                +KEY_CATEGORY_TEXT +" TEXT, "
                +KEY_CATEGORY_ICON +" TEXT "
                + ")";

        db.execSQL(CREATE_BYLAWS_TABLE);
        db.execSQL(CREATE_CATEGORIES_TABLE);
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(CREATE_BYLAWS_TABLE);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.show();
        Log.d(TAG, "Database tables created");
    }

    //Insert category details into the database
    public void addCategories(Integer category_id, String category_text, int category_icon){
        SQLiteDatabase db = this.getWritableDatabase();
        category_icon_integer= ((Integer) category_icon);
        category_icon_str=category_icon_integer.toString();

        String INSERT_OR_REPLACE_CATEGORIES = "INSERT OR REPLACE INTO "+TABLE_CATEGORIES+" ("
                +KEY_CATEGORY_ID+", "+KEY_CATEGORY_TEXT+", "+KEY_CATEGORY_ICON+" ) VALUES ('"
                +category_id.toString()+"', '"+category_text+"', '"+category_icon_str+"' ) ";
        db.execSQL(INSERT_OR_REPLACE_CATEGORIES);
        db.close(); //close database connection

    }
    public void addBylaws(Integer bylaw_id,Integer category_id, String category_text, String penalty){
        SQLiteDatabase db = this.getWritableDatabase();
        String INSERT_OR_REPLACE_BYLAWS = "INSERT OR REPLACE INTO "+TABLE_BYLAWS+" ("
                +KEY_BYLAW_ID+", "+KEY_BYLAW_CATEGORY_ID+", "+KEY_BYLAW_TEXT+","+KEY_PENALTY+" ) VALUES ('"
                +bylaw_id.toString()+"', '"+category_id.toString()+"', '"+category_text+"', '"+penalty+"' ) ";
        db.execSQL(INSERT_OR_REPLACE_BYLAWS);
        db.close();
    }
    //when a category is not in the database online remove it from the sqlite database
    public void removeCategories(Integer[] category_ids){
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM "+TABLE_CATEGORIES+"";   //selet all details in the sqlite databse

        Cursor cursor = db.rawQuery(selectQuery, null);
        List <Integer> allCategories = new ArrayList<Integer>();
        if(cursor.moveToFirst()){
            do{
                Integer category_id_in_table = Integer.valueOf(cursor.getString(0));
                allCategories.add(category_id_in_table);                //add all the category ids that are in the table to an arraylist
            }while (cursor.moveToNext());
        }

        for (int i=0; i< category_ids.length; i++){
            allCategories.remove(category_ids[i]);   //remove all categories in the table matching with the ones that are online and leave non-matching ones
        }

        for (int i = 0; i<allCategories.size(); i++){
            String REMOVE_CATEGORIES = "DELETE FROM "+TABLE_CATEGORIES+" WHERE "+KEY_CATEGORY_ID+" = '"+allCategories.get(i)+"'"; //remove the non-matching categories
            db.execSQL(REMOVE_CATEGORIES);
        }

    }
    //when a bylaw is not in the database online remove it from the sqlite database
    public void removeBylaws(Integer[] bylaw_ids){
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Removing");
        progressDialog.show();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM "+TABLE_BYLAWS+"";   //selet all details in the sqlite databse

        Cursor cursor = db.rawQuery(selectQuery, null);
        List <Integer> allBylaws = new ArrayList<Integer>();
        if(cursor.moveToFirst()){
            do{
                Integer bylaw_id_in_table = Integer.valueOf(cursor.getString(0));
                //add all the bylaw_ids that are in the table to an arraylist
                allBylaws.add(bylaw_id_in_table);
            }while (cursor.moveToNext());
        }

        for (int i=0; i< bylaw_ids.length; i++){
            //remove all bylaws in the table matching with the ones that are online and leave non-matching ones
            allBylaws.remove(bylaw_ids[i]);
        }

        for (int i = 0; i<allBylaws.size(); i++){
            //remove the non-matching bylaws
            String REMOVE_BYLAWS = "DELETE FROM "+TABLE_BYLAWS+" WHERE "+KEY_BYLAW_ID+" = '"+allBylaws.get(i)+"'";
            db.execSQL(REMOVE_BYLAWS);
        }

    }

    public List<Law_categories> getCategories(){

        List<Law_categories> categories_list = new ArrayList<Law_categories>();
        String selectQuery = "SELECT * FROM " + TABLE_CATEGORIES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //looping throught all; rows and adding to list

        if(cursor.moveToFirst()){
            do{
                Law_categories law_categories = new Law_categories();
                law_categories.category_id = Integer.valueOf(cursor.getString(0));
                law_categories.category_text= cursor.getString(1);
                law_categories.category_icon= Integer.parseInt(cursor.getString(2));

                //add category to list
                categories_list.add(law_categories);
            }while (cursor.moveToNext());

        }
        //return categories list
        return categories_list;
    }

    public List<Bylaw_item> getBylaws(Integer category_id){
        List<Bylaw_item> bylaws_list = new ArrayList<Bylaw_item>();
        String selectQuery = "SELECT * FROM " +TABLE_BYLAWS+" WHERE "+KEY_BYLAW_CATEGORY_ID+" = '"+category_id.toString()+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //looping throught all; rows and adding to list
        if(cursor.moveToFirst()){
            do{
                Bylaw_item bylaw_item = new Bylaw_item();
                bylaw_item.bylaw_id = Integer.valueOf(cursor.getString(0));
                bylaw_item.category_id= Integer.valueOf(cursor.getString(1));
                bylaw_item.bylaw_text = cursor.getString(2);
                bylaw_item.penalty = cursor.getString(3);
                //add bylaw_object to list
                bylaws_list.add(bylaw_item);
            }while (cursor.moveToNext());

        }
        //return bylaws list
        return bylaws_list;
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //drop oldertable if t existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BYLAWS);
        //create tables again
        onCreate(db);
    }
    /**
     * Recreate database ... delete all tables an create them again
     */
    public void deleteAllCategories() {
        SQLiteDatabase db = this.getWritableDatabase();
        //Delete all rows
        db.delete(TABLE_CATEGORIES, null, null);
        db.close();
        Log.d(TAG, "Deleted all info.");
    }
    public void deleteAllBylaws() {
        SQLiteDatabase db = this.getWritableDatabase();
        //Delete all rows
        db.delete(TABLE_BYLAWS, null, null);
        db.close();
        Log.d(TAG, "Deleted all info.");
    }
}
