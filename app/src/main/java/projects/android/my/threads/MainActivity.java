package projects.android.my.threads;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    String fileName ="text.txt";
    TextView textView;
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.txtView);
        editText =(EditText) findViewById(R.id.content) ;
        String[] externalStoragePermissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
        {
            //Request Permission for reading and writting of external storage

            ActivityCompat.requestPermissions(this,externalStoragePermissions,100);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //Action on permission reqiest
        if(requestCode == 100)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1]== PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this,"Permission Granted", Toast.LENGTH_LONG).show();
                //Check if external Storage is there
            }
        }
    }

    public void downloadImage(View view)
    {

        WriteFile writeFile = new WriteFile(this,textView,editText.getText().toString());
        writeFile.execute();
    }

    public  void DeleteFile(View view)
    {
        new DeleteFile().execute(fileName);
    }

    public class WriteFile extends AsyncTask
    {

        Context context;
        TextView textView;
        String editText;
        public WriteFile(Context context,TextView textView,String editText)
        {
            this.textView = textView;
            this.context = context;
            this.editText=editText;
        }

        @Override
        protected Object doInBackground(Object[] params)
        {

            FileOutputStream outputStream;
            File file = new File(context.getFilesDir(),fileName);
            if(file.exists())
                file.delete();

            try
            {
                //Open file for writting
                outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
                String msg = editText;
                //write the file based on the text entered
                outputStream.write(msg.getBytes());
                //close connetion with the file
                outputStream.close();
            }
            catch (IOException ex)
            {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
           File file = new File(context.getFilesDir(),fileName);
            if(file.exists())
            {
                StringBuilder text = new StringBuilder();
                try
                {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;

                    while ((line = br.readLine()) != null) {
                        text.append(line);
                        text.append('\n');
                    }
                    br.close();
                }
                catch (IOException e)
                {

                }

                textView.setText(text.toString());
            }
            else
            {
                //file not present
                Toast.makeText(context,"File Does Not Exists",Toast.LENGTH_LONG).show();
            }

        }
    }

    public class DeleteFile extends AsyncTask
    {
         @Override
        protected Object doInBackground(Object[] params)
        {
            String res;
            String fileName = params[0].toString();
            File file = new File(MainActivity.this.getFilesDir(),fileName);
            if(file.exists())
            {
                file.delete();
                res="1";
            }
            else
            {
                res="0";
            }

        return res;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            String result = (String) o;
            if(result.equals("1"))
            Toast.makeText(MainActivity.this,"Success",Toast.LENGTH_LONG).show();
            else
                Toast.makeText(MainActivity.this,"Error",Toast.LENGTH_LONG).show();

        }
    }


}
