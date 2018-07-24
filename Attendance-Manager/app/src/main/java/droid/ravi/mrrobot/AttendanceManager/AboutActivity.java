package droid.ravi.mrrobot.AttendanceManager;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setDescription("Developed By - Ravi Bhatt\n" +"\nNote:-"+
                        "Long press a Subject to Edit/Delete it.")
                .setImage(R.drawable.icon)
                .addItem(new Element().setTitle("Version 1.0"))
                .addItem(new Element().setTitle("Icon Source = https://icons8.com/").setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("https://icons8.com/"))) )
                .addGroup("Connect with us")
                .addEmail("ravi.bhatt.754918@gmail.com")
                .addGitHub("raviRB")
                .create();

        setContentView(aboutPage);
    }
}
