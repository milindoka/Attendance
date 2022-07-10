package in.org.ilugbom.attendance;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import java.io.File;

/**
 * Created by Milind on 8/11/18.
 */

public class SendBackupByEmail
{
    private static  MainActivity MA;
    void SetMA(MainActivity MA){this.MA=MA;}

    void Send(String email)
    {
        String FileNameWithPath = Environment.getExternalStorageDirectory().getPath()+"/AttendanceData.atd";

        File fileIn = new File(FileNameWithPath);
        Uri u = Uri.fromFile(fileIn);

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT,"Attendance Backup");
        sendIntent.putExtra(Intent.EXTRA_TEXT, "From Attendance App...");
        sendIntent.putExtra(Intent.EXTRA_STREAM, u);

        if(email.length()==0) {Msg.show("Specify Email(s) by [Set] button"); return; }
        sendIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { email });

        sendIntent.setType("text/plain");
        MA.startActivity(Intent.createChooser(sendIntent, "Send Mail"));

    }


}
