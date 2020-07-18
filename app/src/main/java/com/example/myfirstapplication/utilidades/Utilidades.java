package com.example.myfirstapplication.utilidades;

public class Utilidades {

    public static final String NOMBRE_BASE_DATOS = "sixthbd";
    public static final int VERSION_BASE_DATOS = 1;

    public static final String TABLA_PRODUCTO = "producto";

    public static final String CAMPO_ID_PRODUCTO = "codigo";
    public static final String CAMPO_PRECIO_PRODUCTO = "precio";
    public static final String CAMPO_NOMBRE_PRODUCTO = "nombre";
    public static final String CAMPO_MARCA_PRODUCTO = "marca_id";
    public static final String CAMPO_UNIDAD_PRODUCTO = "unidad_id";
    public static final String CAMPO_CATEGORIA_PRODUCTO = "categoria_id";

    public static final String CREAR_TABLA_PRODUCTO = "CREATE TABLE " + TABLA_PRODUCTO +
            "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CAMPO_ID_PRODUCTO + " TEXT, " +
            CAMPO_NOMBRE_PRODUCTO + " TEXT, " +
            CAMPO_MARCA_PRODUCTO + " TEXT, " +
            CAMPO_UNIDAD_PRODUCTO + " TEXT," +
            " " + CAMPO_CATEGORIA_PRODUCTO + " TEXT, " +
            CAMPO_PRECIO_PRODUCTO + " FLOAT)";

    public static final String SELECT_PRODUCTO = "SELECT * FROM " + TABLA_PRODUCTO;

    public static final String TABLA_CATEGORIA = "categoria";
    public static final String CAMPO_ID_CATEGORIA = "codigo";
    public static final String CAMPO_NOMBRE_CATEGORIA = "nombre";

    public static final String CREAR_TABLA_CATEGORIA = "CREATE TABLE " + TABLA_CATEGORIA +
            "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CAMPO_ID_CATEGORIA + " TEXT, " +
            CAMPO_NOMBRE_CATEGORIA + " TEXT)";

    public static final String SELECT_CATEGORIA = "SELECT * FROM " + TABLA_CATEGORIA;

    public static final String TABLA_MARCA = "marca";
    public static final String CAMPO_ID_MARCA = "codigo";
    public static final String CAMPO_NOMBRE_MARCA = "nombre";

    public static final String CREAR_TABLA_MARCA = "CREATE TABLE " + TABLA_MARCA +
            "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CAMPO_ID_MARCA + " TEXT, " +
            CAMPO_NOMBRE_MARCA + " TEXT)";

    public static final String SELECT_MARCA = "SELECT * FROM " + TABLA_MARCA;

    public static final String TABLA_UNIDAD = "unidad";
    public static final String CAMPO_ID_UNIDAD = "codigo";
    public static final String CAMPO_NOMBRE_UNIDAD = "nombre";

    public static final String CREAR_TABLA_UNIDAD = "CREATE TABLE " + TABLA_UNIDAD +
            "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CAMPO_ID_UNIDAD + " TEXT, " +
            CAMPO_NOMBRE_UNIDAD + " TEXT)";

    public static final String SELECT_UNIDAD = "SELECT * FROM " + TABLA_UNIDAD;

    public static final String SELECT = "SELECT * FROM ";

    public static final String WHERE = " WHERE id = ";

    public static final String WEB_SERVICE = "http://161.35.122.212/mishka/controlador/Cservice.php";
    //public static final String WEB_SERVICE = "http://192.168.1.5/mishka/controlador/Cservice.php";

    public static final String WEB_IMAGEN_PRODUCTO = "http://161.35.122.212/mishka/";
    //public static final String WEB_IMAGEN_PRODUCTO = "http://192.168.1.5/mishka/";

    public static final String WS_INSERTAR = WEB_SERVICE + "?action=agregar";

    public static final String WS_LISTAR = WEB_SERVICE + "?action=listar";

    public static final String WS_ACTUALIZAR = WEB_SERVICE + "?action=actualizar";
}
