package wifi.codewl.pdfviewer.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.comparator.LastModifiedFileComparator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;


import wifi.codewl.pdfviewer.Controller.AdapterFile;
import wifi.codewl.pdfviewer.Kernel.FileSystem;
import wifi.codewl.pdfviewer.Kernel.Status;
import wifi.codewl.pdfviewer.Kernel.Text;
import wifi.codewl.pdfviewer.Model.PDF;
import wifi.codewl.pdfviewer.R;

public class FileActivity extends AppCompatActivity {


    public static ConstraintLayout label;
    public static Button rename;
    private ConstraintLayout layout;
    private Button delete, characteristics, sharing;


    public static RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private AdapterFile adapter;
    private List<PDF> pdfs;
    private Bundle extra;
    private String path;
    private final String FILE = "file";

    private SharedPreferences sharedPreferences;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setContentView(R.layout.activity_file);
        extra = getIntent().getExtras();
        path = extra.getString("path");
        pdfs = new ArrayList<>();
        File folder = new File(path);
        final File []files = folder.listFiles();
        sharedPreferences = getSharedPreferences(FILE,0);
        int value = sharedPreferences.getInt("display",0);

         if(value == 1){
            Arrays.sort(files);
        }
         if(value == 2) {
            Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_COMPARATOR);
            File file;
            for (int i = 0; i < files.length/2; i++) {
                file = files[i];
                files[i] = files[files.length-i-1];
                files[files.length-i-1] = file;
            }
        }

        for (int i = 0; i < files.length; i++)
            if(Text.getSuffix(files[i].getName()).equalsIgnoreCase("pdf"))
                pdfs.add(new PDF(files[i].getName(),files[i].getPath()));

        recyclerView = findViewById(R.id.recylerViewFile);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(pdfs.size());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdapterFile(this,pdfs);
        recyclerView.setAdapter(adapter);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle(folder.getName());


        refreshLayout= findViewById(R.id.swipeRefresh_file);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary,getTheme()));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                update();
            }
        });


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int topRowVerticalPosition = recyclerView.getChildCount() == 0 ? 0 : recyclerView.getChildAt(0).getTop();
                LinearLayoutManager linearLayoutManager1 = (LinearLayoutManager) recyclerView.getLayoutManager();
                assert linearLayoutManager1 != null;
                int firstVisibleItem = linearLayoutManager1.findFirstVisibleItemPosition();
                refreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });

        layout = findViewById(R.id.layout_file);
        layout.setZ(0);
        label = findViewById(R.id.label_file);
        label.setZ(1);
        delete = findViewById(R.id.delete_file);
        rename = findViewById(R.id.rename_file);
        characteristics = findViewById(R.id.characteristics_file);
        sharing = findViewById(R.id.select_file);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                StringBuilder message = new StringBuilder("All PDF files will be permanently deleted\n");
                for (File file : Status.chooser)
                    message.append(file.getName()).append(", ");
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Delete");
                builder.setMessage(message);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        for(View view : Status.views){
                            view.setBackgroundColor(getContext().getResources().getColor(R.color.whit, getContext().getTheme()));
                        }

                        for (File file : Status.chooser) {
                            adapter.removeAt(adapter.removeAtFile(file.getPath()));
                            file.delete();
                        }

                        //rename.setBackground(getContext().getResources().getDrawable(R.drawable.ic_baseline_create_24,getContext().getTheme()));
                        Drawable img = getResources().getDrawable(R.drawable.ic_baseline_create_24,getTheme());
                        rename.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                        rename.setEnabled(true);
                        adapter.clear();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.create().show();
            }
        });

        rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Rename");
                final File file = Status.chooser.iterator().next();
                final EditText input = new EditText(getContext());
                input.setText(Text.getName(file.getName()));
                builder.setView(input);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = input.getText().toString()+".pdf";
                        if(TextUtils.isEmpty(name))
                            return;
                        if(file.getPath().equalsIgnoreCase(Environment.getExternalStorageDirectory().getPath()))
                            return;
                        File []files = new File(file.getParent()).listFiles();
                        for (File value : files)
                            if (value.getName().equalsIgnoreCase(name))
                                return;
                        int position = adapter.removeAtFile(file.getPath());
                        File newFile = new File(file.getParent(),name);
                        file.renameTo(newFile);

                        Status.views.iterator().next().setBackgroundColor(getContext().getResources().getColor(R.color.whit, getContext().getTheme()));
                        adapter.changeAt(position,new PDF(newFile.getName(),newFile.getPath()));
                        adapter.clear();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.create().show();
            }
        });

        characteristics.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                Iterator<File> iterator = Status.chooser.iterator();
                StringBuilder massage = new StringBuilder();

                File file;
                long total=0;
                long index=0;
                int MB =0;
                int KB =0;
                while (iterator.hasNext()){
                    file = iterator.next();
                    massage.append("Name : ").append(file.getName()).append("\n");
                    massage.append("Place : ").append(file.getPath()).append("\n");
                    massage.append("Date : ").append(new Date(file.lastModified())).append("\n");

                    index += file.length();
                    KB = (int) index/1024;

                    if(KB>1024){
                        MB = (int) KB/1024;
                        massage.append("Size : ").append(MB).append(".").append(Text.getNumber(KB,2)).append(" MB\n\n");
                    }else {
                        massage.append("Size : ").append(KB).append(" KB\n\n");
                    }

                    total += index;
                    index = 0;

                }
                KB = (int) total/1024;

                if(KB>1024){
                    MB = (int) KB/1024;
                    massage.append("Total ( ").append(MB).append(".").append(Text.getNumber(KB,2)).append(" MB )\n\n");
                }else {
                    massage.append("Total ( ").append(KB).append(" KB )\n\n");
                }


                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.setTitle("Characteristics");
                builder.setMessage(massage);
                AlertDialog dialog =  builder.create();
                dialog.show();
                TextView textView = dialog.findViewById(android.R.id.message);
                assert textView != null;
                textView.setTextColor(R.color.cardview_shadow_end_color);
                textView.setTextSize(14);
            }
        });

        sharing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Iterator<File> iterator = Status.chooser.iterator();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files.");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setType("application/pdf"); /* This example is sharing jpeg images. */
                ArrayList<Uri> list = new ArrayList<>();
                while (iterator.hasNext())
                    list.add(FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".provider",iterator.next()));
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, list);
                startActivity(intent);
            }
        });




    }

    private Context getContext(){
        return this;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.update:
                refreshLayout.setRefreshing(true);
                update();
                return true;
            case R.id.view_from_internet:
                getFromInternet();
                return true;
            case R.id.about_as:
                startActivity(new Intent(this,AboutAsActivity.class));
                return true;
            case R.id.get_help:
                startActivity(new Intent(this,HelpActivity.class));
                return  true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.option, menu);
        final MenuItem random = menu.findItem(R.id.random);
        final MenuItem alphabet = menu.findItem(R.id.alphabet);
        final MenuItem last_modified = menu.findItem(R.id.last_modified);

        sharedPreferences = getSharedPreferences(FILE,0);
        int value = sharedPreferences.getInt("display",0);

        if(value == 0)
            random.setChecked(true);
        if(value == 1)
            alphabet.setChecked(true);
        if(value == 2)
            last_modified.setChecked(true);



        random.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                sharedPreferences = getSharedPreferences(FILE,0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("display",0);
                editor.apply();
                editor.commit();
                random.setChecked(true);
                alphabet.setChecked(false);
                last_modified.setChecked(false);
                update();
                return false;
            }
        });

        alphabet.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                sharedPreferences = getSharedPreferences(FILE,0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("display",1);
                editor.apply();
                editor.commit();
                random.setChecked(false);
                alphabet.setChecked(true);
                last_modified.setChecked(false);
                update();
                return false;
            }
        });

        last_modified.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                sharedPreferences = getSharedPreferences(FILE,0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("display",2);
                editor.apply();
                editor.commit();
                random.setChecked(false);
                alphabet.setChecked(false);
                last_modified.setChecked(true);
                update();
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
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBackPressed() {
        if(Status.chooser.isEmpty())
            super.onBackPressed();
        for(View view:Status.views)
            view.setBackgroundColor(getContext().getResources().getColor(R.color.whit, getContext().getTheme()));
        Status.views.clear();
        Status.chooser.clear();
        //rename.setBackground(this.getResources().getDrawable(R.drawable.ic_baseline_create_24,this.getTheme()));
        Drawable img = getResources().getDrawable(R.drawable.ic_baseline_create_24,getTheme());
        rename.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        rename.setEnabled(true);
        adapter.clear();
    }


    @SuppressLint({"ResourceAsColor", "PrivateResource"})
    private void getFromInternet(){
        final EditText input = new EditText(this);
        input.setTextSize(14);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("View from internet");
        builder.setMessage("In order to read from the Internet you must enter a link like  \nhttp://www.example.com/file.pdf");
        builder.setView(input);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String name = input.getText().toString();
                if(TextUtils.isEmpty(name))
                    return;

                if(!Text.getSuffix(name).equalsIgnoreCase("pdf"))
                    return;

                if(!Text.isValidUrl(name))
                    return;

                Toast.makeText(getContext().getApplicationContext(),"Download ... , will open when end",Toast.LENGTH_SHORT).show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        long ID = System.currentTimeMillis();
                        FileSystem.download(getContext(),name,String.valueOf(ID),"pdf");
                        Intent intent = new Intent(getContext(),PDFActivity.class);
                        String file = Environment.getExternalStorageDirectory().getPath()+File.separator+Environment.DIRECTORY_DOWNLOADS+File.separator + ID+".pdf";
                        intent.putExtra("path",file);
                        getContext().startActivity(intent);
                    }
                }).start();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog =  builder.create();
        dialog.show();
        TextView textView =  dialog.findViewById(android.R.id.message);
        assert textView != null;
        textView.setTextColor(R.color.cardview_shadow_end_color);
        textView.setTextSize(14);

    }

    private void update(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(true);
                    }
                });
                final List<PDF> list = new ArrayList<>();
                File folder = new File(path);
                final File []files = folder.listFiles();
                sharedPreferences = getSharedPreferences(FILE,0);
                int value = sharedPreferences.getInt("display",0);

                if(value == 1){
                    Arrays.sort(files);
                }

                if(value == 2) {
                    Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_COMPARATOR);
                    File file;
                    for (int i = 0; i < files.length/2; i++) {
                        file = files[i];
                        files[i] = files[files.length-i-1];
                        files[files.length-i-1] = file;
                    }
                }

                for (int i = 0; i < files.length; i++)
                    if(Text.getSuffix(files[i].getName()).equalsIgnoreCase("pdf"))
                        list.add(new PDF(files[i].getName(),files[i].getPath()));


                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.clear();
                        adapter.updateData(list);
                        refreshLayout.setRefreshing(false);
                    }
                });

            }
        }).start();
    }



}