package in.org.ilugbom.attendance;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Milind on 9/2/18.
 */

public class DivMonthPickerDlg extends AppCompatActivity
{
    MainActivity MA;
    GridView g12;

    ArrayList<String> Divisions = new ArrayList<String>();

  //  String Month[]={"XI-A","XI-B","XI-C"};
    int divindex=0;   ////div index
    void SetMA(MainActivity MA){this.MA=MA;}
    void SetDiv(ArrayList<String> ar,int currentdiv)
    {   divindex=currentdiv;
        String temp[];
        Divisions.clear();
        for(int i=0;i<ar.size();i++)
           {   temp=ar.get(i).split("#");
               Divisions.add(temp[0]);
           }

    }
    void ShowDivMonthDailog() {

        final Dialog dialog = new Dialog(MA);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.div_month_picker);


        GridView gView= (GridView)dialog.findViewById(R.id.grid12);
        final PickMonthAdapter DIVMA = new PickMonthAdapter(MA);
        // gridView.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, mList));
      //  PDMA.SetDays(31);
        gView.setAdapter(DIVMA);
        gView.setNumColumns(4);
        gView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //final String monthchosen=String.format("%02d",position+1);
                final String divichosen=Divisions.get(divindex);
                final int monthindex=position+1;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        MA.PrintMonthlyReport(divichosen,monthindex);
                    }
                }, 50);

                dialog.dismiss();
            }
        });

        dialog.show();


        final Button MidTitleButton= (Button) dialog.findViewById(R.id.divTitle);
        MidTitleButton.setText(Divisions.get(divindex));

        ImageButton monthLeft = (ImageButton) dialog.findViewById(R.id.divLeft);
        monthLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {  divindex--;if(divindex<0) divindex=Divisions.size()-1;
               // PDMA.SetDays(Days[divindex]);
                MidTitleButton.setText(Divisions.get(divindex));
            }
        });

        ImageButton monthRight = (ImageButton) dialog.findViewById(R.id.divRight);
        monthRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {   divindex++;if(divindex>=Divisions.size()) divindex=0;
               // PDMA.SetDays(Days[divindex]);
                MidTitleButton.setText(Divisions.get(divindex));
            }
        });


    }

}