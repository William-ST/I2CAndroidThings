package com.cursoandroid.i2c;

import android.app.Activity;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManager;

import java.io.IOException;
import java.util.List;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 */
public class MainActivity extends Activity {

    private static final byte ACTIVA_SALIDA = 0x40; // 0100 00 00
    private static final byte AUTOINCREMENTO = 0x04; // 0000 01 00
    private static final byte ENTRADA_0 = 0x00; // 0000 00 00
    private static final byte ENTRADA_1 = 0x01; // 0000 00 01

    private static final byte ENTRADA_2 = 0x02; // 0000 00 10
    private static final byte ENTRADA_3 = 0x03; // 0000 00 11
    private static final String IN_I2C_NOMBRE = "I2C1"; // Puerto de entrada
    private static final int IN_I2C_DIRECCION = 0x48; // Dirección de entrada
    private I2cDevice i2c;
    private final String TAG = MainActivity.class.getCanonicalName();
    byte[] buffer = new byte[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PeripheralManager manager = PeripheralManager.getInstance();
        List<String> listaDispositivos = manager.getI2cBusList();
        Log.d(TAG, "=== ListaDispositivos === ");
        for (int i = 0; i < listaDispositivos.size(); i++) {
            Log.d(TAG, listaDispositivos.get(i));
        }
        try {
            i2c = manager.openI2cDevice(IN_I2C_NOMBRE, IN_I2C_DIRECCION);
            byte[] config = new byte[2];
            config[0] = (byte) ACTIVA_SALIDA + ENTRADA_0; // byte de control
            config[1] = (byte) 0x80; // valor de salida (128/255)
            i2c.write(config, config.length);// escribimos 2 bytes
            read();
            /*
            i2c.read(buffer, buffer.length); // leemos 5 bytes
            String s = "";
            for (int i = 0; i < buffer.length; i++) {
                s += " byte " + i + ": " + (buffer[i] & 0xFF);
            }
            Log.d(TAG, s); // mostramos salida
            i2c.close(); // cerramos i2c
            i2c = null; // liberamos memoria
            */
        } catch (IOException e) {
            Log.e(TAG, "Error en al acceder a dispositivo I2C", e);
        }
    }

    private void read() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    i2c.read(buffer, buffer.length); // leemos 5 bytes
                    String s = "";
                    for (int i = 0; i < buffer.length; i++) {
                        s += " byte " + i + ": " + (buffer[i] & 0xFF);
                    }
                    Log.d(TAG, s); // mostramos salida
                } catch (IOException e) {
                    //e.printStackTrace();
                    //Log.e(TAG, "Runnable - Error en al acceder a dispositivo I2C", e);
                }
                read();
            }
        }, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            i2c.close(); // cerramos i2c
            i2c = null; // liberamos memoria
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
