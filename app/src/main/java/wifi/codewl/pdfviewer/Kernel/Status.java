package wifi.codewl.pdfviewer.Kernel;

import android.view.View;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

public class Status {
    public static Set<File> chooser;
    public static Set<View> views;
    public static boolean send;

    static {
        chooser = new LinkedHashSet<>();
        views = new LinkedHashSet<>();


    }


    public static int currentPage;


}
