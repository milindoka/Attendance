package in.org.ilugbom.attendance;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CarryForward
{


  static   String LastAPchain(String division, String FileNameWithPath)
    {

        String lastrecord="";


        try {
            File FileToRead = new File(FileNameWithPath);
            FileInputStream FINS = new FileInputStream(FileToRead);
            BufferedReader bfrReader = new BufferedReader(new InputStreamReader(FINS));
            String AttendanceRecord = " ";
            String temp[], Roll;

           // String strMonth=String.format("%02d",month);
            while ((AttendanceRecord = bfrReader.readLine()) != null)
            {   // if(!AttendanceRecord.substring(3,5).equalsIgnoreCase(strMonth)) continue;
                //Msg.show(AttendanceRecord.substring(3,4));
                if (AttendanceRecord.contains("#"+division+"#"))
                    lastrecord=AttendanceRecord;
            }
        } catch (Exception e)
        {  Msg.show(e.getMessage());
        }

        if(lastrecord.length()==0) return lastrecord;
            String temp[];
        //assert 4 parts in first split
        temp=lastrecord.split("#");
        return temp[3];
    }



}
