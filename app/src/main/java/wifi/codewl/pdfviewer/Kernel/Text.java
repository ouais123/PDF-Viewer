package wifi.codewl.pdfviewer.Kernel;

import android.util.Patterns;
import android.webkit.URLUtil;

import java.net.MalformedURLException;
import java.net.URL;

public class Text {
    public static String getSuffix(String name){
        String []array = name.split("\\.");
        return array[array.length-1];
    }

    public static int getNumber(int number,int index){
        StringBuilder s = new StringBuilder(String.valueOf(number));
        char []chars = s.toString().toCharArray();
        if(chars.length<=index)
            return number;
        s = new StringBuilder();
        for (int i = 0; i < index; i++) {
            s.append(String.valueOf(chars[i]));
        }
        return Integer.parseInt(s.toString());
    }

    public static boolean isValidUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            return URLUtil.isValidUrl(urlString) && Patterns.WEB_URL.matcher(urlString).matches();
        } catch (MalformedURLException ignored) {
        }
        return false;
    }

    public static String getName(String name){
        String []array = name.split("\\.");
        StringBuilder s = new StringBuilder();
        for(int  i=0;i<array.length-1;i++)
            s.append(array[i]);
        return s.toString();
    }
}
