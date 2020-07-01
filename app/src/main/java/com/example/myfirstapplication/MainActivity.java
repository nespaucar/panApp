package com.example.myfirstapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.myfirstapplication.actividad.CalificarWebFragment;
import com.example.myfirstapplication.actividad.ContactanosFragment;
import com.example.myfirstapplication.actividad.InformacionFragment;
import com.example.myfirstapplication.actividad.ProductosFragment;
import com.example.myfirstapplication.actividad.RecuperarFragment;
import com.example.myfirstapplication.actividad.SugerenciaFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import com.bumptech.glide.Glide;
import com.example.myfirstapplication.actividad.BienvenidoFragment;
import com.example.myfirstapplication.actividad.LoginFragment;
import com.example.myfirstapplication.actividad.RegistroFragment;
import com.example.myfirstapplication.actividad.actividadCategoria;
import com.example.myfirstapplication.actividad.actividadListaCategoria;
import com.example.myfirstapplication.actividad.actividadListaMarca;
import com.example.myfirstapplication.actividad.actividadListaProducto;
import com.example.myfirstapplication.actividad.actividadListaUnidad;
import com.example.myfirstapplication.actividad.actividadMarca;
import com.example.myfirstapplication.actividad.actividadProducto;
import com.example.myfirstapplication.actividad.actividadUnidad;
import com.example.myfirstapplication.db.Conexion;
import com.example.myfirstapplication.proceso.CatalogoProductosFragment;
import com.example.myfirstapplication.proceso.MisPedidosFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        actividadProducto.OnFragmentInteractionListener,
        actividadCategoria.OnFragmentInteractionListener,
        actividadMarca.OnFragmentInteractionListener,
        actividadUnidad.OnFragmentInteractionListener,
        actividadListaCategoria.OnFragmentInteractionListener,
        actividadListaMarca.OnFragmentInteractionListener,
        actividadListaUnidad.OnFragmentInteractionListener,
        actividadListaProducto.OnFragmentInteractionListener,
        RegistroFragment.OnFragmentInteractionListener,
        LoginFragment.OnFragmentInteractionListener,
        BienvenidoFragment.OnFragmentInteractionListener,
        CatalogoProductosFragment.OnFragmentInteractionListener,
        MisPedidosFragment.OnFragmentInteractionListener,
        RecuperarFragment.OnFragmentInteractionListener,
        InformacionFragment.OnFragmentInteractionListener,
        ProductosFragment.OnFragmentInteractionListener,
        ContactanosFragment.OnFragmentInteractionListener,
        SugerenciaFragment.OnFragmentInteractionListener {

    private actividadProducto vistaproducto;
    private actividadCategoria vistacategoria;
    private actividadMarca vistamarca;
    private actividadUnidad vistaunidad;
    private RegistroFragment vistaregistro;
    private LoginFragment vistalogin;
    private BienvenidoFragment vistabienvenido;
    private RecuperarFragment vistarecuperar;
    private CatalogoProductosFragment vistacatalogoproductos;
    private InformacionFragment vistainformacionFragment;
    private ProductosFragment vistaproductosFragment;
    private ContactanosFragment vistacontactanosFragment;
    private SugerenciaFragment vistasugerenciaFragment;

    private actividadListaProducto vistalistaproducto;
    private actividadListaCategoria vistalistacategoria;
    private actividadListaMarca vistalistamarca;
    private actividadListaUnidad vistalistaunidad;

    private TextView idcabecera;

    private EditText campoHide;

    private RequestQueue requestQueue;

    private LayoutInflater sgetLayoutInflater;

    private FloatingActionButton btnEditarUsuario;

    private Activity sgetActivity;

    public static final String STRING_PREFERENCES = "PANAPP.NESTOR.PAUCAR";
    public static final String PRIVATE_STATE_BUTTON_SESION = "ESTADO.NESTOR.PAUCAR";

    public int pactive = 1;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnEditarUsuario = (FloatingActionButton) findViewById(R.id.btnEditarUsuario);

        btnEditarUsuario.setVisibility(View.GONE);

        btnEditarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambioFragment(vistaregistro, "MODIFICAR DATOS");
            }
        });

        startService(new Intent(MainActivity.this, MyService.class));

        vistalogin = new LoginFragment();

        if(this.getIntent().getExtras()!=null) {
            if(this.getIntent().getExtras().get("calificaciones")!=null) {
                if(obtenerEstadoSesion(getApplicationContext())) {
                    cambioFragment(new CalificarWebFragment(), "CALIFICACIONES");
                } else {
                    cambioFragment(vistalogin, "IDENTIFÍCATE");
                }
            } else {
                cambioFragment(vistalogin, "IDENTIFÍCATE");
            }
        } else {
            cambioFragment(vistalogin, "IDENTIFÍCATE");
        }

        vistaproducto = new actividadProducto();
        vistacategoria = new actividadCategoria();
        vistamarca = new actividadMarca();
        vistaunidad = new actividadUnidad();

        vistacatalogoproductos = new CatalogoProductosFragment();
        vistainformacionFragment = new InformacionFragment();
        vistaproductosFragment = new ProductosFragment();
        vistacontactanosFragment = new ContactanosFragment();
        vistasugerenciaFragment = new SugerenciaFragment();

        sgetActivity = vistalogin.getActivity();

        vistaregistro = new RegistroFragment();
        vistabienvenido = new BienvenidoFragment();
        vistarecuperar = new RecuperarFragment();

        vistalistaproducto = new actividadListaProducto();
        vistalistacategoria = new actividadListaCategoria();
        vistalistamarca = new actividadListaMarca();
        vistalistaunidad = new actividadListaUnidad();

        Conexion conexion = new Conexion(this);

        /*Conexion.insertarRegistro("P3SC", "PESCADOS", Utilidades.TABLA_CATEGORIA);
        Conexion.insertarRegistro("CA8N", "CARNES", Utilidades.TABLA_CATEGORIA);
        Conexion.insertarRegistro("L4CT", "LACTEOS", Utilidades.TABLA_CATEGORIA);*/

        campoHide = (EditText) findViewById(R.id.hide);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().setGroupVisible(R.id.items_nav, false);

        requestQueue = Volley.newRequestQueue(this);
    }

    public LayoutInflater sgetLayoutInflater() {
        return sgetLayoutInflater;
    }

    public Activity getSgetActivity() {
        return sgetActivity;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(pactive != id) {
            if(!item.isChecked()) {
                if(id == R.id.nav_start) {
                    cambioFragment(vistabienvenido, "BIENVENIDO");
                    pactive = R.id.nav_start;
                } else if (id == R.id.nav_inicio) {
                    cambioFragment(vistainformacionFragment, "SOBRE NOSOTROS");
                    pactive = R.id.nav_inicio;
                } else if (id == R.id.nav_inicio2) {
                    cambioFragment(vistasugerenciaFragment, "SUGERENCIA/RECLAMO");
                    pactive = R.id.nav_inicio2;
                } else if (id == R.id.nav_inicio3) {
                    cambioFragment(vistaproductosFragment, "PRODUCTOS");
                    pactive = R.id.nav_inicio3;
                } else if (id == R.id.nav_inicio4) {
                    cambioFragment(vistacontactanosFragment, "CONTÁCTANOS");
                    pactive = R.id.nav_inicio4;
                } else if (id == R.id.nav_inicio5) {
                    cambioFragment(vistaregistro, "MODIFICAR DATOS");
                    pactive = R.id.nav_inicio5;
                }
                item.setCheckable(false);
            }
        }

        /*if (id == R.id.nav_inicio) {
            cambioFragment(vistabienvenido, "BIENVENIDO");
            campoHide.setText("1");
        } else if (id == R.id.nav_home) {
            cambioFragment(vistacategoria, "CATEGORÍA");
            campoHide.setText("1");
        } else if (id == R.id.nav_gallery) {
            cambioFragment(vistamarca, "MARCA");
            campoHide.setText("2");
        } else if (id == R.id.nav_slideshow) {
            cambioFragment(vistaunidad, "UNIDAD");
            campoHide.setText("3");
        } else if (id == R.id.nav_tools) {
            cambioFragment(vistaproducto, "PRODUCTO");
            campoHide.setText("4");
        } else if (id == R.id.listaCategoria) {
            cambioFragment(vistalistacategoria, "LISTA CATEGORÍAS");
            campoHide.setText("5");
        } else if (id == R.id.listaMarca) {
            cambioFragment(vistalistamarca, "LISTA MARCAS");
            campoHide.setText("6");
        } else if (id == R.id.listaUnidad) {
            cambioFragment(vistalistaunidad, "LISTA UNIDADES");
            campoHide.setText("7");
        } else if (id == R.id.listaProducto) {
            cambioFragment(vistalistaproducto, "LISTA PRODUCTOS");
            campoHide.setText("8");
        }*/

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void cambioFragment(Fragment object, String cabecera) {
        idcabecera = (TextView) findViewById(R.id.idcabecera);

        //CAMBIO DE FRAGMENT

        if (getSupportFragmentManager().findFragmentById(R.id.contenedordinamico) != null) {
            getSupportFragmentManager()
                    .beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.contenedordinamico)).commit();
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenedordinamico, object)
                .commit();

        idcabecera.setText(cabecera);
    }

    public void mostrarToast(String mensaje) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast, (ViewGroup) findViewById(R.id.toast));

        TextView textview = layout.findViewById(R.id.texttoast);
        textview.setText(mensaje);

        ImageView image = layout.findViewById(R.id.imagetoast);

        Glide.with(getBaseContext()).load(R.drawable.panadero);
        Toast toast = new Toast(getApplicationContext());

        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 500);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
        
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void guardarEstadoSesion(Context context, Boolean estado, int id_usuario, String nombre_usuario, int id_pedido, String tipo) {
        SharedPreferences preferences = context.getSharedPreferences(MainActivity.STRING_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(MainActivity.PRIVATE_STATE_BUTTON_SESION, estado).apply();
        editor.putInt("id_usuario", id_usuario).apply();
        editor.putString("nombre_usuario", nombre_usuario).apply();
        editor.putInt("id_pedido", id_pedido).apply();
        editor.putString("tipo_user", tipo).apply();
        editor.commit();
    }

    public boolean obtenerEstadoSesion(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(MainActivity.STRING_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getBoolean(MainActivity.PRIVATE_STATE_BUTTON_SESION, false);
    }

    public int obtenerIdUsuarioEstadoSesion(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(MainActivity.STRING_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getInt("id_usuario", 0);
    }

    public String obtenerTipoUsuarioEstadoSesion(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(MainActivity.STRING_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getString("tipo_user", "");
    }

    public static int obtenerIdUsuarioEstadoSesionStatic(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(MainActivity.STRING_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getInt("id_usuario", 0);
    }

    public String obtenerNombreUsuarioEstadoSesion(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(MainActivity.STRING_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getString("nombre_usuario", "");
    }

    public int obtenerIdPedidoSesion(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(MainActivity.STRING_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getInt("id_pedido", 0);
    }

    public static void escucharNotificaciones(final Context context, RequestQueue requestQueue, SharedPreferences preferences) {
        final int user_id = preferences.getInt("id_usuario", 0);
        String URL = "http://161.35.122.212/sigre/controlador/Cpedido.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //BASTANTE ÚTIL PARA VER ERRORES
                        Log.i("tagconvertstr", "[" + response + "]");
                        try {
                            JSONObject rptaJson = new JSONObject(response);
                            int cantidadpedidos = Integer.parseInt(rptaJson.getString("cantidadpedidos"));
                            if (cantidadpedidos > 0) {
                                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                String NOTIFICATION_CHANNEL_ID = "PtMxServiFlashxPtM";
                                Uri defaultsong = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
                                builder.setAutoCancel(true)
                                        .setWhen(System.currentTimeMillis())
                                        .setSmallIcon(R.mipmap.ic_launcher)
                                        .setTicker("Hearty365")
                                        .setContentText("Califica el servicio de Reparto")
                                        .setVibrate(new long[]{0, 1000, 500, 1000})
                                        .setContentInfo("PAN")
                                        .setColor(Color.BLUE)
                                        .setLights(Color.MAGENTA, 1000, 1000)
                                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                                        .setSound(defaultsong);
                                String messageSummary = "";

                                // Valores Necesarios para cada opcion

                                Intent intent = new Intent(context, MainActivity.class);
                                intent.putExtra("calificaciones", "S");
                                builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
                                messageSummary = "Califica el servicio de Reparto";
                                builder.setColor(Color.GRAY);

                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    // Solo para android Oreo o superior
                                    @SuppressLint("WrongConstant")
                                    NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                                            "Mi notificacion ",
                                            NotificationManager.IMPORTANCE_HIGH
                                    );

                                    // Configuracion del canal de notificacion
                                    channel.setDescription("PtMxServiFlashxPtM channel para app");
                                    channel.enableLights(true);
                                    channel.setLightColor(Color.BLUE);
                                    channel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                                    channel.enableVibration(true);
                                    manager.createNotificationChannel(channel);

                                }

                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                PendingIntent pendingIntent = PendingIntent.getActivity(context, Integer.parseInt("1"), intent, 0);

                                builder.setContentIntent(pendingIntent)
                                        .setContentTitle("PanApp")
                                        .setContentText("Califica el servicio de Reparto");

                                NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle(builder);

                                style.setSummaryText(messageSummary)
                                        .setBigContentTitle("PanApp")
                                        .bigText("Califica el servicio de Reparto");

                                manager.notify(Integer.parseInt("1"), builder.build());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("id_usuario", String.valueOf(user_id));
                parametros.put("accion", "ESCUCHAR_NOTIFICACIONES");
                return parametros;
            }
        };
        requestQueue.add(stringRequest);
    }
}
