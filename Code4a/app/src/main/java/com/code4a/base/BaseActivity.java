package com.code4a.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by jiang on 15/12/13.
 */
public class BaseActivity extends AppCompatActivity {

    protected void openActivty(Class<?> clazz) {
        openActivty(clazz, null);
    }

    protected void openActivty(Class<?> clazz, Bundle pBundle) {
        Intent intent = new Intent(this, clazz);
        if (pBundle != null) intent.putExtras(pBundle);
        startActivity(intent);
    }
}
