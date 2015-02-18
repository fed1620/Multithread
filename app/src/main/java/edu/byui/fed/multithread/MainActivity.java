package edu.byui.fed.multithread;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    public int progress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void createFile(View view) {
        Thread write = new Thread(new Runnable() {
            @Override
            public void run() {
                String filename = "numbers.txt";
                String line;

                try
                {
                    FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);

                    for (int i = 1; i <= 10; i++) {
                        runOnUiThread(new Runnable(){
                            @Override
                            public void run(){
                                ProgressBar progressBar =
                                        (ProgressBar)findViewById(R.id.progressBar);
                                if (progress < 100) {
                                    progress += 10;
                                    progressBar.setProgress(progress);
                                }
                            }
                        });
                        line = "" + i + '\n';
                        fos.write(line.getBytes());
                        Thread.sleep(250);
                    }
                    fos.close();
                } catch (Exception ex) {
                    ex.printStackTrace(System.out);
                }

                System.out.println("Successfully wrote to file: " + filename);
            }
        });

        write.start();

        if (!write.isAlive()) {
            try {
                write.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadFile(View view) {
        Thread load = new Thread(new Runnable() {
            @Override
            public void run() {
                progress = 0;
                String filename = "numbers.txt";

                // Store the numbers in this list:
                final List<String> list = new ArrayList<>();
                try
                {
                    InputStream inputStream = openFileInput(filename);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;

                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                        list.add(line);

                        runOnUiThread(new Runnable(){
                            @Override
                        public void run(){
                                ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
                                if (progress < 100) {
                                    progress += 10;
                                    progressBar.setProgress(progress);
                                }
                            }
                        });

                        Thread.sleep(250);
                    }
                } catch (Exception e) {
                    System.out.println("Error reading file");
                }

                // Create an array adapter and a list view
                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        ArrayAdapter<String> adapter =
                                new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, list);
                        ListView listView = (ListView) findViewById(R.id.listView);
                        listView.setAdapter(adapter);
                    }
                });

                System.out.println("Successfully read from file!");
            }
        });

        load.start();
        if (!load.isAlive()) {
            try {
                load.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void clear(View view) {
        ListView listView = (ListView)findViewById(R.id.listView);
        ArrayAdapter<String> adapter = (ArrayAdapter<String>)listView.getAdapter();

        if (adapter != null) {
            adapter.clear();
        }

        // Reset the progress bar
        progress = 0;
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setProgress(progress);
    }
}
