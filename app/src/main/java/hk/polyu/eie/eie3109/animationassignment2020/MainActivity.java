package hk.polyu.eie.eie3109.animationassignment2020;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import hk.polyu.eie.eie3109.Panel;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new Panel(this));


    }
}