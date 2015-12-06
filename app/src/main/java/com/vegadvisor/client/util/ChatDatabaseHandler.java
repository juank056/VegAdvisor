package com.vegadvisor.client.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by JuanCamilo on 05/12/2015.
 */
public class ChatDatabaseHandler extends SQLiteOpenHelper {

    /**
     * Version de la base de datos
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Nombre de la base de datos
     */
    private static final String DATABASE_NAME = "chatMessagesDatabase";

    /**
     * Tabla de mensajes
     */
    private static final String TABLE_MESSAGES = "contacts";

    /**
     * Columnas de la tabla
     */
    // Contacts Table Columns names
    private static final String USER_SESSION = "userSession";
    private static final String USER_OTHER = "userOther";
    private static final String MESSAGE_DATE = "messageDate";
    private static final String MESSAGE_KEY = "messageKey";
    private static final String MESSAGE_TIME = "messageTime";
    private static final String MESSAGE_CONTENT = "messageContent";
    private static final String MESSAGE_RECEIVED = "messageReceived";
    private static final String USER_NAME = "userName";

    /**
     * Constructor de handler de la base de datos
     *
     * @param context Contexto
     */
    public ChatDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_MESSAGES + "("
                + USER_SESSION + " VARCHAR(25),"
                + USER_OTHER + " VARCHAR(25),"
                + MESSAGE_DATE + " VARCHAR(10),"
                + MESSAGE_KEY + " LONG,"
                + MESSAGE_TIME + " VARCHAR(8),"
                + MESSAGE_CONTENT + " VARCHAR(256),"
                + MESSAGE_RECEIVED + " VARCHAR(1),"
                + USER_NAME + " VARCHAR(50),"
                + "PRIMARY KEY (" + USER_SESSION + ", " + USER_OTHER + ", " + MESSAGE_DATE + ", " + MESSAGE_KEY + "))";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p/>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop de la estructura
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        // Crea nuevamente
        onCreate(db);
    }

    /**
     * Se encarga de guardar un mensaje en la base de datos
     *
     * @param userSession     Usuario en sesion
     * @param userOther       Otro usuario del sistema
     * @param message         Mensaje a enviar
     * @param messageReceived Indicador de si es un mensaje recibido ('0' enviado, '1' recibido)
     * @param userName        Nombre del otro usuario
     */
    public void saveMessage(String userSession, String userOther, String message,
                            String messageReceived, String userName) {
        //Fecha actual
        Date current = DateUtils.getCurrentUtilDate();
        //Obtiene base de datos
        SQLiteDatabase db = this.getWritableDatabase();
        //Content Values
        ContentValues values = new ContentValues();
        //Asigna datos del mensaje
        values.put(USER_SESSION, userSession);
        values.put(USER_OTHER, userOther);
        values.put(MESSAGE_DATE, DateUtils.getDateString(current));
        values.put(MESSAGE_KEY, System.currentTimeMillis());
        values.put(MESSAGE_TIME, DateUtils.getTimeString(current));
        values.put(MESSAGE_CONTENT, message);
        values.put(MESSAGE_RECEIVED, messageReceived);
        values.put(USER_NAME, userName);
        // Guarda registro
        db.insert(TABLE_MESSAGES, null, values);
        //Cierra base de datos
        db.close();
    }

    /**
     * Obtiene los ultimos mensajes enviados a un usuario
     *
     * @param userSession Usuario en sesion
     * @param userOther   Otro usuario del sistema
     * @param max         Maximo de mensajes a obtener
     * @return Lista de mensajes de chat
     */
    public List<ChatMessage> getMessages(String userSession, String userOther, int max) {
        List<ChatMessage> messages = new ArrayList<>();
        // Query a ejecutar
        String selectQuery = "SELECT  * FROM " + TABLE_MESSAGES + " WHERE "
                + USER_SESSION + "='&1' AND " + USER_OTHER + "='&2' ORDER BY " + MESSAGE_KEY + " DESC LIMIT " + max;
        //Asigna parametros
        selectQuery = selectQuery.replace(Constants.URL_PARAM01, userSession);
        selectQuery = selectQuery.replace(Constants.URL_PARAM02, userOther);
        //Ejecuta Query
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Obtiene registros
        if (cursor.moveToFirst()) {
            do {
                // Addiciona mensaje a la lista
                messages.add(new ChatMessage(userSession,
                        userOther,
                        cursor.getString(2),
                        cursor.getString(4),
                        cursor.getString(5), cursor.getString(6), cursor.getString(7)));
            } while (cursor.moveToNext());
        }

        // Retorna
        return messages;
    }

    /**
     * Obtiene contactos recientes de conversacion del usuario
     *
     * @param userSession Usuario en sesion
     * @return Lista de contactos con los que ha chateado
     */
    public List<String[]> getContacts(String userSession) {
        List<String[]> contacts = new ArrayList<>();
        // Query a ejecutar
        String selectQuery = "SELECT DISTINCT " + USER_OTHER + "," + USER_NAME + " FROM " + TABLE_MESSAGES + " WHERE "
                + USER_SESSION + "='&1'";
        //Asigna parametros
        selectQuery = selectQuery.replace(Constants.URL_PARAM01, userSession);
        //Ejecuta Query
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Obtiene registros
        if (cursor.moveToFirst()) {
            do {
                // Addiciona contacto a la lista
                contacts.add(new String[]{cursor.getString(0), cursor.getString(1)});
            } while (cursor.moveToNext());
        }

        // Retorna
        return contacts;
    }
}
