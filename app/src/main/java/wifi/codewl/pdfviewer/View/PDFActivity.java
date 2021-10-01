package wifi.codewl.pdfviewer.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnDrawListener;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageScrollListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Pattern;

import wifi.codewl.pdfviewer.Kernel.Status;
import wifi.codewl.pdfviewer.R;

public class PDFActivity extends AppCompatActivity{

    private PDFView pdfView;
    private Bundle extra;
    private File file;
    private int size;


    @SuppressLint({"WrongConstant", "RestrictedApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.ActionBar);
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setContentView(R.layout.activity_p_d_f);
        Intent intent = getIntent();
        extra = intent.getExtras();

        assert extra != null;
        file = new File(Objects.requireNonNull(extra.getString("path")));
        Objects.requireNonNull(getSupportActionBar()).setTitle(file.getName());
        size = getPageCount();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        final int yOffset = dm.heightPixels/8;

        pdfView = findViewById(R.id.pdfView);
        pdfView.fromFile(file).defaultPage(Status.currentPage).spacing(5).enableDoubletap(true).enableSwipe(true).load();
        pdfView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Objects.requireNonNull(getSupportActionBar()).isShowing()) {
                    getSupportActionBar().hide();
                } else {
                    getSupportActionBar().show();
                    Toast toast = Toast.makeText(getContext(),"Page "+(pdfView.getCurrentPage()+1)+" / "+size,Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP|Gravity.CENTER,0,yOffset);
                    toast.show();
                }
            }
        });

        Toast toast = Toast.makeText(getContext(),"Page "+(Status.currentPage+1)+" / "+size,Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP|Gravity.CENTER,0,yOffset);
        toast.show();



    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Status.currentPage = pdfView.getCurrentPage();
    }

    private Context getContext(){
        return this;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                finish();
                return true;
            case R.id.send_a_file:
                sendFile();
                return true;
            case R.id.opened_by_using:
                openFile();
                return true;
            case R.id.report_a_problem:
                startActivity(new Intent(this,HelpActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pdf, menu);
        final MenuItem item4 = menu.findItem(R.id.vertical);
        final MenuItem item5 = menu.findItem(R.id.horizontal);
        item4.setChecked(true);
        item4.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                item4.setChecked(true);
                item5.setChecked(false);
                int page = pdfView.getCurrentPage();
                pdfView.fromFile(file).spacing(5).defaultPage(page).enableDoubletap(true).enableSwipe(true).load();
                return false;
            }
        });

        item5.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                item4.setChecked(false);
                item5.setChecked(true);
                int page = pdfView.getCurrentPage();
                pdfView.fromFile(file).spacing(5).defaultPage(page).enableDoubletap(true).enableSwipe(true).load();
                pdfView.setSwipeVertical(false);
                return false;
            }
        });

        getMenuInflater().inflate(R.menu.search,menu);
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!isNumeric(newText))
                    return true;
                pdfView.jumpTo(Integer.parseInt(newText),true);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void sendFile(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files.");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setType("application/pdf"); /* This example is sharing jpeg images. */
        ArrayList<Uri> list = new ArrayList<>();
        list.add(FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider",file));
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, list);
        startActivity(intent);
    }

    private void openFile(){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri contentUri;
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 24) {
            Uri apkURI = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);
            intent.setDataAndType(apkURI, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            contentUri = Uri.fromFile(file);
            intent.setDataAndType(contentUri, "application/pdf");
        }
        startActivity(intent);
    }

    private Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    public boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }

    private int getPageCount(){
        PdfRenderer renderer = null;
        try {
            renderer = new PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY));
            return renderer.getPageCount();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (renderer != null)
                renderer.close();
        }
        return 0;
    }



}