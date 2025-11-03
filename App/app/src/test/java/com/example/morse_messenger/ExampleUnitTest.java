package com.example.morse_messenger;

import android.text.Editable;
import android.text.InputFilter;
import android.widget.EditText;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
public class ExampleUnitTest {

    private MainActivity activity;

    @Mock EditText mockInputEditText;
    @Mock TextView mockOutputTextView;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Создаём экземпляр MainActivity без вызова onCreate
        activity = new MainActivity();

        // Подменяем поля
        setPrivateField("inputEditText", mockInputEditText);
        setPrivateField("outputTextView", mockOutputTextView);
    }

    private void setPrivateField(String fieldName, Object value) throws Exception {
        Field field = MainActivity.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(activity, value);
    }

    private void callTranslateToMorse() throws Exception {
        Method method = MainActivity.class.getDeclaredMethod("translateToMorse");
        method.setAccessible(true);
        method.invoke(activity);
    }

    // === ТЕСТЫ ===

    @Test
    public void translateToMorse_EmptyInput_ClearsOutput() throws Exception {
        when(mockInputEditText.getText()).thenReturn(new MockEditable(""));
        callTranslateToMorse();
        verify(mockOutputTextView).setText("");
    }

    @Test
    public void translateToMorse_ValidLatinText_TranslatesCorrectly() throws Exception {
        when(mockInputEditText.getText()).thenReturn(new MockEditable("hello world"));
        callTranslateToMorse();
        verify(mockOutputTextView).setText(".... . .-.. .-.. --- / .-- --- .-. .-.. -..");
    }

    @Test
    public void translateToMorse_ValidCyrillicText_TranslatesCorrectly() throws Exception {
        when(mockInputEditText.getText()).thenReturn(new MockEditable("привет мир"));
        callTranslateToMorse();
        verify(mockOutputTextView).setText(".--. .-. .. .-- . - / -- .. .-.");
    }

    @Test
    public void translateToMorse_MixedLatinAndCyrillic_TranslatesCorrectly() throws Exception {
        when(mockInputEditText.getText()).thenReturn(new MockEditable("hello привет"));
        callTranslateToMorse();
        verify(mockOutputTextView).setText(".... . .-.. .-.. --- / .--. .-. .. .-- . -");
    }

    @Test
    public void translateToMorse_InvalidCharacters_ShowsError() throws Exception {
        when(mockInputEditText.getText()).thenReturn(new MockEditable("hello! @world"));
        callTranslateToMorse();
        verify(mockOutputTextView).setText("Недопустимые символы: @");
    }

    @Test
    public void translateToMorse_MultipleInvalidCharacters_ShowsAll() throws Exception {
        when(mockInputEditText.getText()).thenReturn(new MockEditable("test # $ %"));
        callTranslateToMorse();
        verify(mockOutputTextView).setText("Недопустимые символы: # %");
    }

    @Test
    public void translateToMorse_OnlyInvalidCharacters_ShowsError() throws Exception {
        when(mockInputEditText.getText()).thenReturn(new MockEditable("###"));
        callTranslateToMorse();
        verify(mockOutputTextView).setText("Недопустимые символы: #");
    }

    @Test
    public void translateToMorse_OnlySupportedSymbols_Translates() throws Exception {
        when(mockInputEditText.getText()).thenReturn(new MockEditable("a1.b,"));
        callTranslateToMorse();
        verify(mockOutputTextView).setText(".- .---- .-.-.- -...- --..--");
    }

    @Test
    public void translateToMorse_NoSupportedSymbols_ShowsMessage() throws Exception {
        when(mockInputEditText.getText()).thenReturn(new MockEditable("   ###   "));
        callTranslateToMorse();
        verify(mockOutputTextView).setText("Нет поддерживаемых символов");
    }

    @Test
    public void translateToMorse_HandlesMultipleSpacesCorrectly() throws Exception {
        when(mockInputEditText.getText()).thenReturn(new MockEditable("a   b  c"));
        callTranslateToMorse();
        verify(mockOutputTextView).setText(".- / -... / -.-.");
    }

    @Test
    public void morseEncode_KnownCharacters_ReturnsCorrectMorse() throws Exception {
        Method method = MainActivity.class.getDeclaredMethod("morseEncode", char.class);
        method.setAccessible(true);

        assertEquals(".-", method.invoke(null, 'a'));
        assertEquals(".-", method.invoke(null, 'а'));
        assertEquals(".", method.invoke(null, 'e'));
        assertEquals(".", method.invoke(null, 'ё'));
        assertEquals(".----", method.invoke(null, '1'));
        assertEquals("--..--", method.invoke(null, ','));
        assertEquals(".-.-.-", method.invoke(null, '.'));
        assertEquals("..--..", method.invoke(null, '?'));
        assertEquals("---.", method.invoke(null, 'ч'));
        assertEquals("----", method.invoke(null, 'ш'));
        assertEquals("..--", method.invoke(null, 'ю'));
    }

    @Test
    public void morseEncode_UnknownCharacter_ReturnsEmptyString() throws Exception {
        Method method = MainActivity.class.getDeclaredMethod("morseEncode", char.class);
        method.setAccessible(true);

        assertEquals("", method.invoke(null, '©'));
        assertEquals("", method.invoke(null, 'λ'));
    }

    @Test
    public void translateToMorse_ExceptionInProcessing_ShowsError() throws Exception {
        when(mockInputEditText.getText()).thenReturn(null);
        callTranslateToMorse();
        verify(mockOutputTextView).setText("Ошибка обработки ввода");
    }

    // === РАБОЧИЙ MockEditable ===
    private static class MockEditable implements Editable {
        private final StringBuilder builder = new StringBuilder();

        public MockEditable(String text) {
            if (text != null) builder.append(text);
        }

        @Override public String toString() { return builder.toString(); }

        @Override public int length() { return builder.length(); }
        @Override public char charAt(int index) { return builder.charAt(index); }
        @Override public CharSequence subSequence(int start, int end) { return builder.subSequence(start, end); }

        @SuppressWarnings("unchecked")
        @Override public <T> T[] getSpans(int queryStart, int queryEnd, Class<T> kind) {
            return (T[]) new Object[0];
        }

        @Override public int getSpanStart(Object tag) { return -1; }
        @Override public int getSpanEnd(Object tag) { return -1; }
        @Override public int getSpanFlags(Object tag) { return 0; }
        @Override public int nextSpanTransition(int start, int limit, Class kind) { return limit; }

        @Override public void setSpan(Object what, int start, int end, int flags) {}
        @Override public void removeSpan(Object what) {}

        @Override public Editable replace(int st, int en, CharSequence source) {
            builder.replace(st, en, source.toString());
            return this;
        }

        @Override public Editable replace(int st, int en, CharSequence source, int s, int e) {
            builder.replace(st, en, source.subSequence(s, e).toString());
            return this;
        }

        @Override public Editable insert(int where, CharSequence text) {
            builder.insert(where, text);
            return this;
        }

        @Override public Editable insert(int where, CharSequence text, int start, int end) {
            builder.insert(where, text.subSequence(start, end));
            return this;
        }

        @Override public Editable delete(int st, int en) {
            builder.delete(st, en);
            return this;
        }

        @Override public Editable append(CharSequence text) {
            builder.append(text);
            return this;
        }

        @Override public Editable append(CharSequence text, int start, int end) {
            builder.append(text, start, end);
            return this;
        }

        @Override public Editable append(char text) {
            builder.append(text);
            return this;
        }

        @Override public void clear() { builder.setLength(0); }
        @Override public void clearSpans() {}

        @Override public InputFilter[] getFilters() { return new InputFilter[0]; }
        @Override public void setFilters(InputFilter[] filters) {}

        @Override
        public void getChars(int start, int end, char[] dest, int destoff) {
            String str = builder.substring(start, end);
            str.getChars(0, str.length(), dest, destoff);
        }
    }
}