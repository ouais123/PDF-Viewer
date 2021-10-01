package wifi.codewl.pdfviewer.Email;

public class Encrypt {

    public static String getEmail (String email) {
        char[] charsEmail = email.toCharArray();
        for (int i = 0; i < charsEmail.length; i++)
            charsEmail[i] = (char) ( ((int)charsEmail[i] / 4));
        return new String(charsEmail);
    }

    public static String getPassword (String password) {
        char[] charsPassword = password.toCharArray();
        for (int i = 0; i < charsPassword.length; i++)
            charsPassword[i] = (char) ( ((int)charsPassword[i] / 4));
        return new String(charsPassword);
    }
}
