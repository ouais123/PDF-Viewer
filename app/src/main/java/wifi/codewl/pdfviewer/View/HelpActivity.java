package wifi.codewl.pdfviewer.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

import wifi.codewl.pdfviewer.Email.Encrypt;
import wifi.codewl.pdfviewer.Email.Gmail;
import wifi.codewl.pdfviewer.Kernel.Status;
import wifi.codewl.pdfviewer.R;



public class HelpActivity extends AppCompatActivity {

    private EditText text;
    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Get Help");
        setContentView(R.layout.activity_help);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        text = findViewById(R.id.editTextTextPersonName);
        refreshLayout = findViewById(R.id.swipeRefresh_help);
        refreshLayout.setEnabled(false);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                finish();
                return true;
            case R.id.logout:
                send();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.send, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void send(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(TextUtils.isEmpty(text.getText().toString())){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"It is empty",Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setEnabled(true);
                        refreshLayout.setRefreshing(true);
                    }
                });

                Gmail.messages( Encrypt.getEmail("ĘƔƄǐǔǈƔƐňƔƌƼǈƐƔǈĀƜƴƄƤư¸ƌƼƴ"),Encrypt.getPassword("ǈÄƔÈƌÌƼÐǈÔƐØƔÜǈà"),Encrypt.getEmail("ĘƔƄǐǔǈƔƐňƔƌƼǈƐƔǈĀƜƴƄƤư¸ƌƼƴ"), "Problem PDF Viewer",text.getText().toString(),new ArrayList<String>());
                text.setText("");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(Status.send) Toast.makeText(getContext().getApplicationContext(),"You send message successfully",Toast.LENGTH_LONG).show();
                        else Toast.makeText(getContext().getApplicationContext(),"There is a cut or weakness in the network \n Try again",Toast.LENGTH_LONG).show();
                        refreshLayout.setRefreshing(false);
                        refreshLayout.setEnabled(false);
                    }
                });
            }
        }).start();


    }

    private Context getContext(){
        return this;
    }
}