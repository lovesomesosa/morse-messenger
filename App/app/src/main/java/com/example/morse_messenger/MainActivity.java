package com.example.morse_messenger;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

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

        translateButton.setOnClickListener(v -> translateToMorse());
    }

    private void translateToMorse() {
        String inputText = inputEditText.getText().toString().trim();
        if (inputText.isEmpty()) {
            outputTextView.setText("");
            return;
        }

        StringBuilder morseResult = new StringBuilder();
        String[] letters = inputText.split("");
        for (String letter : letters) {
            String morse = morseEncode(letter);
            if (!morse.isEmpty()) {
                morseResult.append(morse).append(" ");
            }
        }
        outputTextView.setText(morseResult.toString().trim());
    }

    static String morseEncode(String x) {
        switch (x.toLowerCase(Locale.getDefault())) {
            case "a": return ".-";
            case "b": return "-...";
            case "c": return "-.-.";
            case "d": return "-..";
            case "e": return ".";
            case "f": return "..-.";
            case "g": return "--.";
            case "h": return "....";
            case "i": return "..";
            case "j": return ".---";
            case "k": return "-.-";
            case "l": return ".-..";
            case "m": return "--";
            case "n": return "-.";
            case "o": return "---";
            case "p": return ".--.";
            case "q": return "--.-";
            case "r": return ".-.";
            case "s": return "...";
            case "t": return "-";
            case "u": return "..-";
            case "v": return "...-";
            case "w": return ".--";
            case "x": return "-..-";
            case "y": return "-.--";
            case "z": return "--..";
            case " ": return "/ ";
            case "0": return "-----";
            case "1": return ".----";
            case "2": return "..---";
            case "3": return "...--";
            case "4": return "....-";
            case "5": return ".....";
            case "6": return "-....";
            case "7": return "--...";
            case "8": return "---..";
            case "9": return "----.";
            case "а": return ".-";
            case "б": return "-...";
            case "в": return ".--";
            case "г": return "--.";
            case "д": return "-..";
            case "е": return ".";
            case "ё": return ".";
            case "ж": return "...-";
            case "з": return "--..";
            case "и": return "..";
            case "й": return ".---";
            case "к": return "-.-";
            case "л": return ".-..";
            case "м": return "--";
            case "н": return "-.";
            case "о": return "---";
            case "п": return ".--.";
            case "р": return ".-.";
            case "с": return "...";
            case "т": return "-";
            case "у": return "..-";
            case "ф": return "..-.";
            case "х": return "....";
            case "ц": return "-.-.";
            case "ч": return "---.";
            case "ш": return "----";
            case "щ": return "--.-";
            case "ъ": return "--.--";
            case "ы": return "-.--";
            case "ь": return "-..-";
            case "э": return "..-..";
            case "ю": return "..--";
            case "я": return ".-.-";
            case "ñ": return "--.--";
            case ",": return "--..--";
            case ".": return ".-.-.-";
            case "?": return "..--..";
            case "'": return ".----.";
            case "!": return "-.-.--";
            case "/": return "-..-.";
            case "(": return "-.--.";
            case ")": return "-.--.-";
            case "&": return ".-...";
            case ":": return "---...";
            case ";": return "-.-.-.";
            case "=": return "-...-";
            case "+": return ".-.-.";
            case "-": return "-....-";
            case "_": return "..--.-";
            case "\"": return ".-..-.";
            case "$": return "...-..-";
            case "@": return ".--.-.";
            default: return "";
        }
    }
}