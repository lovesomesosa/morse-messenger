package com.example.morse_messenger;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText inputEditText;
    private TextView outputTextView;
    private Button translateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputEditText = findViewById(R.id.input_edit_text);
        outputTextView = findViewById(R.id.output_text_view);
        translateButton = findViewById(R.id.translate_button);

        translateButton.setOnClickListener(v -> {
            try {
                translateToMorse();
            } catch (Exception e) {
                outputTextView.setText("Неожиданная ошибка");
            }
        });
    }

    private void translateToMorse() {
        try {
            String input = inputEditText.getText().toString().trim();
            if (input.isEmpty()) {
                outputTextView.setText("");
                return;
            }

            StringBuilder result = new StringBuilder();
            boolean inWord = false;

            for (int i = 0; i < input.length(); i++) {
                char c = Character.toLowerCase(input.charAt(i));
                String morse = morseEncode(c);

                if (morse.isEmpty()) {
                    continue;
                }

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

        } catch (Exception e) {
            outputTextView.setText("Ошибка обработки ввода");
        }
    }

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
            case 'ñ':
                return "--.--";

            default:
                return "";
        }
    }
}