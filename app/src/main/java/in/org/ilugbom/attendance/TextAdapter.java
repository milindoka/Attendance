package in.org.ilugbom.attendance;


/**
 * Created by student on 7/14/18.
 */

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Milind on 24/6/18.
 */

public class TextAdapter extends BaseAdapter
{
   // String[] numbers = new String[8];
    String[] numbers={"1","3","34","77","122"};
    Set<Integer> selectedPositions=new HashSet<Integer>();
    Set<Integer> tempPositions=new HashSet<Integer>();
    boolean found=false;
    ArrayList<String> Divisions=new ArrayList<String>();
    Context context;



    void Fillpositions(String abChain)
    {   selectedPositions.clear();
        int len=abChain.length();
        for (int i=0; i <len;i++)
            if(abChain.charAt(i)=='A')
                selectedPositions.add(i);
    }


    public TextAdapter(Context context)
    {
        this.context = context;
    }





    void DisplayDivision(String divline)
    {
        String temp[],temp1[];
        temp=divline.split("#");
        temp1=temp[1].split(",");
        int len=temp[1].length();
     //   String start=temp1[0];
      //  String end=temp1[len-1];


       Fillpositions(temp[2]);

        numbers=new String[len];
        numbers=temp1;
        /*
        for (int i =0; i <len; i++)
        {
            {
                numbers[i] = temp1[i];

            }
            notifyDataSetChanged();
        }
        */
            notifyDataSetChanged();
    }

    @Override
    public int getCount() {

        return numbers.length;
    }

    @Override
    public Object getItem(int position)
    {
        // TODO Auto-generated method stub
        return numbers[position];
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        TextView text = new TextView(this.context);
        text.setText(numbers[position]);
        text.setGravity(Gravity.CENTER);
        Integer tt=new Integer(position);
        if(selectedPositions.contains(tt))
            text.setBackgroundColor(context.getResources().getColor(R.color.colorTuch));
        else
            text.setBackgroundColor(Color.parseColor("#fbdcbb"));

        text.setTextColor(Color.parseColor("#040404"));
        text.setTextSize(25);
        return text;
    }


}
