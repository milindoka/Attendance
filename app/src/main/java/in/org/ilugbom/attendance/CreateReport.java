package in.org.ilugbom.attendance;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by student on 8/13/18.
 */

public class CreateReport
{
    String FileNameWithPath;
    ArrayList<String> Divisions = new ArrayList<String>();

    void SetAtdFileWithPath(String atdfname)
    { FileNameWithPath= atdfname; }

    void LoadHistory(String divtitle)   //// Load All Records of one division
    {

        try {

            File myFile = new File(FileNameWithPath);
            if(!myFile.exists()) { Msg.show("No Attendance Record !"); return; }
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
            String aDataRow = "";

            Divisions.clear();

            String temp[];
            while ((aDataRow = myReader.readLine()) != null)
            {
                if (!aDataRow.contains("#")) continue;
                temp = aDataRow.split("#");
                if (temp.length < 4) continue;
                if(!temp[1].contains(divtitle)) continue;
                Divisions.add(temp[3]);
             }
            myReader.close();


        }
        catch (Exception e)

        {
            Msg.show(e.getMessage());
            //    Toast.makeText(getBaseContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }



    void ShowReport(int index)
    {
        int counter=0;
        for(int i=0;i<Divisions.size();i++)
            if(Divisions.get(i).charAt(index)=='P') counter++;
        float percentage,sum;
        sum=counter;
        percentage=sum*100/Divisions.size();
        Msg.show(String.format("%d/%d  =  %.2f%%",counter,Divisions.size(),percentage));

    }

}
