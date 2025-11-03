package com.example.morse_messenger;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private EditText inputEditText;
    private TextView outputTextView;
    private Button translateButton;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket socket;
    private OutputStream outputStream;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String PI_NAME = "raspberry";
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputEditText = findViewById(R.id.input_edit_text);
        outputTextView = findViewById(R.id.output_text_view);
        translateButton = findViewById(R.id.translate_button);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            outputTextView.setText("Bluetooth не поддерживается");
            return;
        }

        // Запрос разрешений
        requestBluetoothPermissions();

        translateButton.setOnClickListener(v -> {
            if (!hasPermissions()) {
                outputTextView.setText("Нет разрешений на Bluetooth");
                return;
            }
            if (!connectToPi()) {
                outputTextView.setText("Не удалось подключиться к Pi");
                return;
            }
            translateToMorse(); // твоя функция с комментариями
        });
    }

    private boolean hasPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestBluetoothPermissions() {
        String[] permissions = {
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.ACCESS_FINE_LOCATION
        };

        boolean allGranted = true;
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }

        if (!allGranted) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        } else {
            connectToPi();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                runOnUiThread(() -> {
                    outputTextView.setText("Разрешения получены!");
                    connectToPi();
                });
            } else {
                outputTextView.setText("Разрешения отклонены. Bluetooth не работает.");
            }
        }
    }

    private boolean connectToPi() {
        if (socket != null && socket.isConnected()) return true;

        try {
            if (!hasPermissions()) return false;

            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            BluetoothDevice piDevice = null;

            for (BluetoothDevice device : pairedDevices) {
                String name = device.getName();
                if (name != null && name.contains("raspberry")) {
                    piDevice = device;
                    break;
                }
            }

            if (piDevice == null) {
                runOnUiThread(() -> outputTextView.setText("Raspberry Pi не найден в сопряжённых"));
                return false;
            }

            socket = piDevice.createRfcommSocketToServiceRecord(MY_UUID);
            socket.connect();
            outputStream = socket.getOutputStream();

            runOnUiThread(() -> {
                outputTextView.setText("Подключено к Raspberry Pi!");
                Toast.makeText(this, "Bluetooth подключён", Toast.LENGTH_SHORT).show();
            });
            return true;

        } catch (SecurityException e) {
            runOnUiThread(() -> outputTextView.setText("Нет разрешения: " + e.getMessage()));
            return false;
        } catch (Exception e) {
            runOnUiThread(() -> outputTextView.setText("Ошибка: " + e.getMessage()));
            return false;
        }
    }

    // === ТВОЯ ФУНКЦИЯ С КОММЕНТАРИЯМИ ===
    private void translateToMorse() {
        try {
            String input = inputEditText.getText().toString().trim();
            if (input.isEmpty()) {
                outputTextView.setText("");
                return;
            }

            // === ВАЛИДАЦИЯ ===
            StringBuilder invalidChars = new StringBuilder();
            for (int i = 0; i < input.length(); i++) {
                char c = Character.toLowerCase(input.charAt(i));
                if (c == ' ') continue;
                if (morseEncode(c).isEmpty() && invalidChars.indexOf(String.valueOf(c)) == -1) {
                    invalidChars.append(c).append(" ");
                }
            }

            if (invalidChars.length() > 0) {
                outputTextView.setText("Недопустимые символы: " + invalidChars.toString().trim());
                return;
            }
            // === КОНЕЦ ВАЛИДАЦИИ ===

            // === ПЕРЕВОД ===
            StringBuilder result = new StringBuilder();
            boolean inWord = false;

            for (int i = 0; i < input.length(); i++) {
                char c = Character.toLowerCase(input.charAt(i));
                String morse = morseEncode(c);

                if (c == ' ') {
                    if (inWord) {
                        result.append(" / ");
                        inWord = false;
                    }
                    continue;
                }

                if (inWord && result.length() > 0) {
                    result.append(' ');
                }

                result.append(morse);
                inWord = true;
            }

            String out = result.toString().trim();
            outputTextView.setText(out.isEmpty() ? "Нет поддерживаемых символов" : out);

            // === ОТПРАВКА НА PI ===
            if (outputStream != null) {
                try {
                    outputStream.write((out + "\n").getBytes());
                    outputStream.flush();
                } catch (Exception e) {
                    outputTextView.setText("Ошибка отправки: " + e.getMessage());
                }
            }

        } catch (Exception e) {
            outputTextView.setText("Ошибка обработки ввода");
        }
    }

    @Override
    protected void onDestroy() {
        try {
            if (outputStream != null) outputStream.close();
            if (socket != null) socket.close();
        } catch (Exception ignored) {}
        super.onDestroy();
    }

    // === ТВОЯ ФУНКЦИЯ morseEncode (без изменений) ===
    private static String morseEncode(char c) {
        switch (c) {
            // --- Латиница + Кириллица (общие) ---
            case 'a':
            case 'а':
                return ".-";
            case 'b':
            case 'б':
                return "-...";
            case 'c':
            case 'ц':
                return "-.-.";
            case 'd':
            case 'д':
                return "-..";
            case 'e':
            case 'ё':
            case 'е':
                return ".";
            case 'f':
            case 'ф':
                return "..-.";
            case 'g':
            case 'г':
                return "--.";
            case 'h':
            case 'х':
                return "....";
            case 'i':
            case 'и':
                return "..";
            case 'j':
            case 'й':
                return ".---";
            case 'k':
            case 'к':
                return "-.-";
            case 'l':
            case 'л':
                return ".-..";
            case 'm':
            case 'м':
                return "--";
            case 'n':
            case 'н':
                return "-.";
            case 'o':
            case 'о':
                return "---";
            case 'p':
            case 'п':
                return ".--.";
            case 'q':
            case 'щ':
                return "--.-";
            case 'r':
            case 'р':
                return ".-.";
            case 's':
            case 'с':
                return "...";
            case 't':
            case 'т':
                return "-";
            case 'u':
            case 'у':
                return "..-";

            // --- в и v — одинаково ---
            case 'в':
            case 'v':
                return ".--";

            // --- w и ж — одинаково, отдельно от в ---
            case 'w':
            case 'ж':
                return "...-";

            // --- Остальные ---
            case 'x':
            case 'ь':
                return "-..-";
            case 'y':
            case 'ы':
                return "-.--";
            case 'z':
            case 'з':
                return "--..";

            // --- Цифры ---
            case '0':
                return "-----";
            case '1':
                return ".----";
            case '2':
                return "..---";
            case '3':
                return "...--";
            case '4':
                return "....-";
            case '5':
                return ".....";
            case '6':
                return "-....";
            case '7':
                return "--...";
            case '8':
                return "---..";
            case '9':
                return "----.";

            // --- Уникальные кириллические ---
            case 'ч':
                return "---.";
            case 'ш':
                return "----";
            case 'ъ':
                return "--.--";
            case 'э':
                return "..-..";
            case 'ю':
                return "..--";
            case 'я':
                return ".-.-";

            // --- Знаки препинания ---
            case ',':
                return "--..--";
            case '.':
                return ".-.-.-";
            case '?':
                return "..--..";
            case '\'':
                return ".----.";
            case '!':
                return "-.-.--";
            case '/':
                return "-..-.";
            case '(':
                return "-.--.";
            case ')':
                return "-.--.-";
            case '&':
                return ".-...";
            case ':':
                return "---...";
            case ';':
                return "-.-.-.";
            case '=':
                return "-...-";
            case '+':
                return ".-.-.";
            case '-':
                return "-....-";
            case '_':
                return "..--.-";
            case '"':
                return ".-..-.";
            case '$':
                return "...-..-";
            case '@':
                return ".--.-.";
            case '№':
                return "--.--";

            default:
                return "";
        }
    }
}