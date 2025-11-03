package com.example.morse_messenger;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
public class MainActivityTest {

    private MainActivity activity;
    private ActivityController<MainActivity> controller;

    @Mock private EditText mockInputEditText;
    @Mock private TextView mockOutputTextView;
    @Mock private Button mockTranslateButton;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Создаём активити через Robolectric
        controller = Robolectric.buildActivity(MainActivity.class);
        activity = controller.get();

        // Мокаем view'ы
        activity.inputEditText = mockInputEditText;
        activity.outputTextView = mockOutputTextView;
        activity.translateButton = mockTranslateButton;
    }

    @Test
    public void translateToMorse_EmptyInput_ClearsOutput() {
        when(mockInputEditText.getText()).thenReturn(new MockEditable(""));

        activity.translateToMorse();

        verify(mockOutputTextView).setText("");
    }

    @Test
    public void translateToMorse_ValidLatinText_TranslatesCorrectly() {
        when(mockInputEditText.getText()).thenReturn(new MockEditable("hello world"));

        activity.translateToMorse();

        verify(mockOutputTextView).setText(".... . .-.. .-.. --- / .-- --- .-. .-.. -..");
    }

    @Test
    public void translateToMorse_ValidCyrillicText_TranslatesCorrectly() {
        when(mockInputEditText.getText()).thenReturn(new MockEditable("привет мир"));

        activity.translateToMorse();

        verify(mockOutputTextView).setText(".--. .-. .. .-- . - / -- .. .-.");
    }

    @Test
    public void translateToMorse_MixedLatinAndCyrillic_TranslatesCorrectly() {
        when(mockInputEditText.getText()).thenReturn(new MockEditable("hello привет"));

        activity.translateToMorse();

        verify(mockOutputTextView).setText(".... . .-.. .-.. --- / .--. .-. .. .-- . -");
    }

    @Test
    public void translateToMorse_InvalidCharacters_ShowsError() {
        when(mockInputEditText.getText()).thenReturn(new MockEditable("hello! @world"));

        activity.translateToMorse();

        verify(mockOutputTextView).setText("Недопустимые символы: @");
    }

    @Test
    public void translateToMorse_MultipleInvalidCharacters_ShowsAll() {
        when(mockInputEditText.getText()).thenReturn(new MockEditable("test # $ %"));

        activity.translateToMorse();

        verify(mockOutputTextView).setText("Недопустимые символы: # $ %");
    }

    @Test
    public void translateToMorse_OnlyInvalidCharacters_ShowsError() {
        when(mockInputEditText.getText()).thenReturn(new MockEditable("###"));

        activity.translateToMorse();

        verify(mockOutputTextView).setText("Недопустимые символы: #");
    }

    @Test
    public void translateToMorse_OnlySupportedSymbols_Translates() {
        when(mockInputEditText.getText()).thenReturn(new MockEditable("a1.b,"));

        activity.translateToMorse();

        verify(mockOutputTextView).setText(".- .---- .-.-.- -...- --..--");
    }

    @Test
    public void translateToMorse_NoSupportedSymbols_ShowsMessage() {
        when(mockInputEditText.getText()).thenReturn(new MockEditable("   ###   "));

        activity.translateToMorse();

        verify(mockOutputTextView).setText("Нет поддерживаемых символов");
    }

    @Test
    public void translateToMorse_HandlesMultipleSpacesCorrectly() {
        when(mockInputEditText.getText()).thenReturn(new MockEditable("a   b  c"));

        activity.translateToMorse();

        verify(mockOutputTextView).setText(".- / -... / -.-.");
    }

    @Test
    public void morseEncode_KnownCharacters_ReturnsCorrectMorse() {
        assertEquals(".-", MainActivity.morseEncode('a'));
        assertEquals(".-", MainActivity.morseEncode('а'));
        assertEquals(".", MainActivity.morseEncode('e'));
        assertEquals(".", MainActivity.morseEncode('ё'));
        assertEquals(".----", MainActivity.morseEncode('1'));
        assertEquals("--..--", MainActivity.morseEncode(','));
        assertEquals(".-.-.-", MainActivity.morseEncode('.'));
        assertEquals("..--..", MainActivity.morseEncode('?'));
        assertEquals("---.", MainActivity.morseEncode('ч'));
        assertEquals("----", MainActivity.morseEncode('ш'));
        assertEquals("..--", MainActivity.morseEncode('ю'));
    }

    @Test
    public void morseEncode_UnknownCharacter_ReturnsEmptyString() {
        assertEquals("", MainActivity.morseEncode('©'));
        assertEquals("", MainActivity.morseEncode('λ'));
    }

    @Test
    public void translateToMorse_ExceptionInProcessing_ShowsError() {
        // Подмена getText() на null, чтобы вызвать NPE
        when(mockInputEditText.getText()).thenReturn(null);

        activity.translateToMorse();

        verify(mockOutputTextView).setText("Ошибка обработки ввода");
    }

    // Вспомогательный класс для моков Editable
    private static class MockEditable implements android.text.Editable {
        private final String text;

        public MockEditable(String text) {
            this.text = text != null ? text : "";
        }

        @Override
        public String toString() {
            return text;
        }

        // Заглушки для всех методов интерфейса Editable
        @Override public int length() { return text.length(); }
        @Override public char charAt(int index) { return text.charAt(index); }
        @Override public CharSequence subSequence(int start, int end) { return text.subSequence(start, end); }
        @Override public SpannableStringBuilder replace(int st, int en, CharSequence source, int start2, int end2) { return null; }
        @Override public void removeSpan(Object what) {}
        @Override public <T> T[] getSpans(int start, int end, Class<T> type) { return null; }
        @Override public int getSpanStart(Object tag) { return 0; }
        @Override public int getSpanEnd(Object tag) { return 0; }
        @Override public int getSpanFlags(Object tag) { return 0; }
        @Override public int nextSpanTransition(int start, int limit, Class type) { return 0; }
        @Override public void setSpan(Object what, int start, int end, int flags) {}
        @Override public Editable replace(int st, int en, CharSequence text) { return this; }
        @Override public Editable insert(int where, CharSequence text) { return this; }
        @Override public Editable delete(int st, int en) { return this; }
        @Override public void clear() {}
        @Override public void clearSpans() {}
        @Override public Editable append(CharSequence text) { return this; }
    }
}