package wifi.codewl.pdfviewer.Kernel;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;


import java.io.File;


public class FileSystem {
    public static void deletePath(File folder){
        File []files = folder.listFiles();
        for (File file : files)
            if (file.isFile() && Text.getSuffix(file.getName()).equalsIgnoreCase("pdf"))
                file.delete();
    }

    public static void download(Context context,String url, String title,String suffix){
        System.out.println("--------------------------------");
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI|DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle("Download");
        request.setDescription("Download File ...");

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title+"."+suffix);
        DownloadManager manager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
        //request.setMimeType("application/pdf");
        manager.enqueue(request);
    }

}
