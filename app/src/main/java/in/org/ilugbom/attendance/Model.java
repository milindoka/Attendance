package in.org.ilugbom.attendance;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Milind on 7/3/18.
 */

public class Model
{
    ArrayList<String> Divisions = new ArrayList<>();
    ArrayList<String> DateArray = new ArrayList<>();



    String GetDivisionTitle(int index)
    {
        String[] temp;
        temp = Divisions.get(index).split("#");
        return temp[0];

    }

    String GetRollStrip(int index)
    {
        String[] temp;
        temp = Divisions.get(index).split("#");
        return temp[1];
    }


    void LoadDivisions() {

        Divisions.add("IX-A#5,25,197,331,565,1012#P");
        Divisions.add("XI-C#1,33,157,322,397,398#P");
        Divisions.add("TY-C#2,4,8,16,32,64,128,256,512,1024,2048#P");
    }


    void SaveList(String AttendanceLine,String FileNameWithPath)
    {
        String txtData = "";
        boolean newfile=false;

        try {
            File myFile = new File(FileNameWithPath);
            if (!myFile.exists()) { newfile=true; myFile.createNewFile();}
            FileOutputStream fOut = new FileOutputStream(myFile,true);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

            if(newfile)
            {  String RString = "======== Reserved Line 1 =======\n";
                RString+= "======== Reserved Line 2 =======\n";
                myOutWriter.append(RString);
            }
            txtData += GetDateTimeString();
            txtData +="#";
            txtData += GetDivisionTitle(MainActivity.currentDivision);
            txtData += "#";
            txtData += GetRollStrip(MainActivity.currentDivision);
            txtData += "#";
            txtData += AttendanceLine;
            txtData += "\n";

            myOutWriter.append(txtData);
            myOutWriter.close();
            fOut.close();
            Msg.Show("Attendance Saved");

        } catch (Exception e)
        {
            Msg.show(e.getMessage());
        }
    }

    String GetDateTimeString()
    {   String date, dow, time;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yy");
        SimpleDateFormat df2 = new SimpleDateFormat("E");
        SimpleDateFormat df3 = new SimpleDateFormat("HH:mm");
        date = df1.format(c.getTime());
        dow = df2.format(c.getTime());
        time = df3.format(c.getTime());
        return (date + " - " + dow + " - " + time);
    }

    void LoadHistory(String FileNameWithPath)   //// Load History = Load All Records
    {

        try {

            File myFile = new File(FileNameWithPath);

            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
            String aDataRow = "";

            Divisions.clear();
            DateArray.clear();
            String temp[];
            while ((aDataRow = myReader.readLine()) != null)

            {
                if (!aDataRow.contains("#")) continue;
                temp = aDataRow.split("#");
                if (temp.length < 4) continue;
                DateArray.add(temp[0]);
                Divisions.add(temp[1] + "#" + temp[2] + "#" + temp[3]);
            }
            myReader.close();

            Msg.Show("History Mode On");
        }
        catch (Exception e)

        {
            Msg.show(e.getMessage());

        }
    }


    void SaveHistory(String FileNameWithPath)
    {
        int i;
        String txtData = "";
        try {
            File myFile = new File(FileNameWithPath);
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

            String RString = "======== Reserved Line 1 =======\n";
                   RString+= "======== Reserved Line 2 =======\n";
                   myOutWriter.append(RString);

                   for(i=0;i<DateArray.size();i++)
                   {txtData+=DateArray.get(i);
                    txtData+="#";
                    txtData+=Divisions.get(i);
                    txtData+="\n";
                   }

            myOutWriter.append(txtData);
            myOutWriter.close();
            fOut.close();
            Msg.Show("History Saved");


        } catch (Exception e)
        {
            Msg.show(e.getMessage());
        }
    }


}  // end Model class

