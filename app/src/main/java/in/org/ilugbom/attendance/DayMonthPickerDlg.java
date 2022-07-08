package in.org.ilugbom.attendance;

import android.app.Dialog;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;

import java.util.Calendar;

/**
 * Created by Milind on 8/28/18.
 */

public class DayMonthPickerDlg extends AppCompatActivity
{
    MainActivity MA;
    GridView g31;

    String Month[]={"January","February","March","April","May","June","July","August","September","October","November","December" };
    int     Days[]={31,29,31,30,31,30,31,31,30,31,30,31};
    int monthindex=0;
    void SetMA(MainActivity MA){this.MA=MA;}

    void ShowDayMonthDailog() {

        final Dialog dialog = new Dialog(MA);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.day_month_picker);

        Calendar cal = Calendar.getInstance();
        monthindex = cal.get(Calendar.MONTH);


        GridView gView= (GridView)dialog.findViewById(R.id.grid31);
        final PickDayAndMonthAdapter PDMA = new PickDayAndMonthAdapter(MA);
        // gridView.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, mList));
        PDMA.SetDays(31);
        gView.setAdapter(PDMA);
        gView.setNumColumns(4);
        gView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String date=String.format("%02d/%02d",position+1,monthindex+1);
                MA.JumpOnDate(date);
                dialog.dismiss();
            }
        });

        dialog.show();


        final Button MidTitleButton= (Button) dialog.findViewById(R.id.monthTitle);
        MidTitleButton.setText(Month[monthindex]);

        ImageButton monthLeft = (ImageButton) dialog.findViewById(R.id.monthLeft);
        monthLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {  monthindex--;if(monthindex<0) monthindex=11;
               PDMA.SetDays(Days[monthindex]);
               MidTitleButton.setText(Month[monthindex]);
            }
        });

        ImageButton monthRight = (ImageButton) dialog.findViewById(R.id.monthRight);
        monthRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {   monthindex++;if(monthindex>11) monthindex=0;
                PDMA.SetDays(Days[monthindex]);
                MidTitleButton.setText(Month[monthindex]);
            }
        });








    }







}