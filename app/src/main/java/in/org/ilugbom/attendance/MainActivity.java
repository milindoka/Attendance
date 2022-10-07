package in.org.ilugbom.attendance;

import android.Manifest;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;

import android.os.Environment;
import android.provider.ContactsContract;
import android.view.MotionEvent;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.itextpdf.text.DocumentException;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    ImportDialog importDialog;

    String Filenamewithpath;
    private File StorageDirectory;
    private static final String TAG = "MainActivity";  //for rutime storage permission code
    private static final int REQUEST_CODE = 1;
    private boolean modified=false;
    private TextView FC;
    private String ddd,mmm;
    int counter=0;
    TextAdapter TA;
    Msg msg=new Msg();

    Boolean Norecords=false;
    Model model;
    Menu settingsMenu; /// 3 dot menu on left side, this variable is used to chekmark from outside menu handler
    CreateDivDialog CDD=new CreateDivDialog();
    HelpDialog HD=new HelpDialog();
    MonthlyReport MR = new MonthlyReport();


    DayMonthPickerDlg dmpd=new DayMonthPickerDlg();
    DivMonthPickerDlg divmpd=new DivMonthPickerDlg();

    FloatingActionButton fab;
    boolean fabVisible=true;
    boolean AttendanceInProgress=false;
    boolean HistoryMode=false;

    private ImageButton  buttonLeft,  buttonRight;
    private Button buttonDivTitle;

    static  int currentDivision = 0;

    //////////////////ON CREATE METHOD //////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        //  LL = (LinearLayout) findViewById(R.id.ClassBar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        msg.SetMA(this);

        dmpd.SetMA(this);
        divmpd.SetMA(this);

        FC=(TextView) findViewById(R.id.FabCounter);

        TA = new TextAdapter(this);
        final GridView gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(TA);
   //     for (int i = 0; i < 120; i++) {
   //         TA.numbers[i] = String.format("%d", 5000 + i + 1);
   //
   //     }


        model  = new Model();
       model.LoadDivisions();



        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id)
            {
               if(!AttendanceInProgress) { Msg.ImageMessage("Tap Attendance Button",R.drawable.fab_blue_green72); return;}
               // Attendance in  Progress
                int firstPosition = gridView.getFirstVisiblePosition();
                int childPosition = position - firstPosition;
                TextView txtView = (TextView) gridView.getChildAt(childPosition);

                Integer tt = new Integer(position);
                if (TA.selectedPositions.contains(tt)) {
                    txtView.setBackgroundColor(Color.parseColor("#fbdcbb"));
                    TA.selectedPositions.remove(tt);
                    FC.setText(String.format("%d",TA.selectedPositions.size()));
                } else {
                    txtView.setBackgroundColor(getResources().getColor(R.color.colorTuch));
                    TA.selectedPositions.add((Integer) position);
                    FC.setText(String.format("%d",TA.selectedPositions.size()));

                }
                modified=true;
            }
        });



        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int position, long arg3) {
                int firstPosition = gridView.getFirstVisiblePosition();
                int childPosition = position - firstPosition;
                TextView txtView = (TextView) gridView.getChildAt(childPosition);
                Integer tt = new Integer(position);
                int index=tt.intValue();

                String RollStrip=model.GetRollStrip(currentDivision);
                String[] temp;
                temp = RollStrip.split(",");

                // Msg.Show(temp[index]);
                CreateReport CR=new CreateReport();
                CR.SetAtdFileWithPath(Filenamewithpath);
                CR.LoadHistory(model.GetDivisionTitle(currentDivision));
                CR.ShowReport(index,temp[index]);

                return true;
            }
        });



        fab = (FloatingActionButton) findViewById(R.id.fab);

        /*
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                        */


        fab.setOnTouchListener(new View.OnTouchListener() {


            float x, y;
            float x1,y1;
            float x2,y2;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction())
                {   case MotionEvent.ACTION_UP :
                    if(Math.abs(x2-x1)<10 && Math.abs(y2-y1)<10)

                    {
                        OnFloatingButton();
                    }

                    return true;
                    case MotionEvent.ACTION_MOVE:

                        x2=fab.getX()+event.getX()-x; y2=fab.getY()+event.getY()-y;

                        fab.setX(x2);
                        fab.setY(y2);
                        return true;
                    case MotionEvent.ACTION_DOWN:
                        x = event.getX();
                        y = event.getY();
                        x1=fab.getX()+event.getX()-x; y1=fab.getY()+event.getY()-y;
                        x2=fab.getX();y2=fab.getY();
                     //   x1=x;
                      //  y1=y;
                     //   Msg.show(String.format("%d",event.getX()));
                        return true;
                }

                return false;
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//////////////////// Button Listeners ////////////////////////////////////


        buttonDivTitle=findViewById(R.id.buttonDivTitle);
        buttonDivTitle.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View view)
            {

            }
        });

        buttonLeft = (ImageButton) findViewById(R.id.buttonLeft);
        buttonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {   if(AttendanceInProgress) { Msg.ImageMessage("Attendance in Progress",R.drawable.blue_red_60); return; }

                currentDivision--;
                if (currentDivision < 0) currentDivision = model.Divisions.size() - 1;
                DisplayDivision();
            }
        });


        buttonRight = (ImageButton) findViewById(R.id.buttonRight);
        buttonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            { if(AttendanceInProgress) { Msg.ImageMessage("Attendance in Progress",R.drawable.blue_red_60); return; }
                currentDivision++;
                if (currentDivision > model.Divisions.size() - 1) currentDivision = 0;
                DisplayDivision();
            }
        });

        CDD.SetRef(model);
        CDD.SetMA(this);
//        msg.SetMA(this);

        setTitle(model.GetDateTimeString());

       CDD.LoadDivisionsFromPrefs();currentDivision=0;


       DisplayDivision();   //assert currentDivision=0;    ??


      //  navigationView.setBackgroundColor(getResources().getColor(R.color.skyBlue));

          Filenamewithpath=get_atd_FilePath();
       //   StorageDirectory=getStorageDirectory();
       // File myFile = new File(Filenamewithpath);
        //if (!myFile.exists()) {
          //  try {
          //      myFile.createNewFile();
          //  } catch (IOException e) {
          //      e.printStackTrace();
          //  }
       // }

        File myFile = new File(Filenamewithpath);
        if (!myFile.exists()) { Norecords=true;}

        MR.SetAtdFileWithPath(Filenamewithpath);



    }  ////////////////////////////////////////// END OF ONCREATE
       /////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////





    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else

        if(modified)  {  ShowPopupMenu(); }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        settingsMenu=menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_division)
        {  if (!ClearedHistoryMode()) return true; //Switch off history mode if ON
            //Msg.show("Settings");
            CDD.editmode=false;
            // Toast.makeText(getBaseContext(), "create", Toast.LENGTH_SHORT).show();
            CDD.showDialog(MainActivity.this);
            return true;
        }


        if (id == R.id.action_carry_forward)
        {
            if (Norecords) { Msg.Show("No Previous Records"); return true;}
            String lastAPchain=CarryForward.LastAPchain(model.GetDivisionTitle(currentDivision),Filenamewithpath);
         if(lastAPchain.length()==0)  { Msg.Show("No Previous Record"); return true;}
            TA.Fillpositions(lastAPchain);
            TA.notifyDataSetChanged();
            FC.setText(String.format("%d",TA.selectedPositions.size()));

            fab.setBackgroundTintList(getResources().getColorStateList(R.color.colorPink));
            Msg.Show("Last Attendance Copied");
            AttendanceInProgress=true;
            modified=true;
            return true;
        }




        if (id == R.id.action_invert_selection)
        {
            InvertAttendance();

            return true;
        }


        if (id==R.id.action_edit_div)
        {   if (!ClearedHistoryMode()) return true;
            EditDivision();
        }


        if (id == R.id.action_delete_div)
        {   if (!ClearedHistoryMode()) return true;
            DeleteDivision();
            return true;
        }

        if (id == R.id.action_history_mode)
        {   if(Norecords) { Msg.Show("No History"); return true;}
            if(HistoryMode)   /// if historymode is ON
            { if(modified) {  Msg.Show("History modified, Save or Discard First !");
                            return true;}
                SetHistoryMode(); // switch off history
                item.setChecked(false);
                return true;
            }
            //// Now Attendance ON HistoryMode OFF



            if(modified) {  Msg.Show("Attendance Modified, Save or Discard First !");
                return true; }
               // otherwise switch history mode to ON

            SetHistoryMode();
            item.setChecked(true);
            return true;
        }


        if (id == R.id.action_delete_record)
        {
//           model.Divisions.remove(currentDivision);
            if(!HistoryMode) { Msg.ImageMessage("History Mode Off",R.drawable.exclaimation60); return true; }

            DeleteHistoryRecord();
          //  Msg.show("History - Del Record");
            return true;
        }



        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.nav_printmonthlyreport :
                if (Norecords) { Msg.Show("No Records"); break;}
                divmpd.SetDiv(model.Divisions,currentDivision);
                divmpd.ShowDivMonthDailog();
/*
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showAlertDialog();
                    }
                }, 300);
*/
            break;

            case R.id.nav_jump : if (Norecords) { Msg.Show("No Records"); break;}
                                dmpd.ShowDayMonthDailog(); break;

            case R.id.nav_help : HD.showDialog(MainActivity.this); break;

            case R.id.nav_setpreferences : CDD.showPreferenceDialog(MainActivity.this); break;

            case R.id.nav_share : if (Norecords) { Msg.Show("No Records To Share"); break;}
                                   ShareFile(Filenamewithpath);break;

            case R.id.nav_import : //Msg.Show("Data Import not yet implemented");
                                 ShowImportDialog();
                     break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void ShowImportDialog(){
         importDialog=new ImportDialog();
         importDialog.SetFilePath(Filenamewithpath);
        importDialog.show(getSupportFragmentManager(),"Import Dialog");

    }


    void OnFloatingButton()
    {
       // ShowPopupMenu();
        if(!AttendanceInProgress) {
            fab.setBackgroundTintList(getResources().getColorStateList(R.color.colorPink));
            Msg.Show("Mark Attendance");
            AttendanceInProgress=!AttendanceInProgress;
        }

        else {

            ShowPopupMenu();

             }
    }
////////////////////////////////////////////////



    void EditDivision()
    {
        CDD.editmode=true;
        CDD.tempDivTitle=model.GetDivisionTitle(currentDivision);
        CDD.showDialog(MainActivity.this);
    }


    String GetAttendanceLine()
    {String Line="";
        for(int i=0;i<TA.numbers.length;i++)
        {Integer tt=new Integer(i);
            if(TA.selectedPositions.contains(tt))
                Line+="A";
            else
                Line+="P";
        }
        return Line;
    }



    void DeleteHistoryRecord()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Delete This Record ?");

        // Set the alert dialog yes button click listener
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

                if(model.Divisions.size()>1)
                {
                    model.Divisions.remove(currentDivision);
                    model.DateArray.remove(currentDivision);
                    currentDivision--;
                    if(currentDivision<0) currentDivision=0;
                    DisplayDivision();
                    // Toast.makeText(getApplicationContext(),
                    //       "Division Deleted",Toast.LENGTH_LONG).show();
                    model.SaveHistory(Filenamewithpath);
                    Msg.Show("History Record Deleted");
                    //CDD.SaveDivisionsInPrefs();

                }
            }
        });

        // Set the alert dialog no button click listener
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing on No button clicked
            }
        });

        AlertDialog dialog = builder.create();
        // Display the alert dialog on interface
        dialog.show();
    }



    void DeleteDivision()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Delete This Division ?");   // Set a title for alert dialog

        // Set the alert dialog yes button click listener
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

                if(model.Divisions.size()>1)
                {
                    model.Divisions.remove(currentDivision);
                    currentDivision--;
                    if(currentDivision<0) currentDivision=0;
                    DisplayDivision();
                   // Toast.makeText(getApplicationContext(),
                     //       "Division Deleted",Toast.LENGTH_LONG).show();
                    Msg.Show("Division Deleted");
                    CDD.SaveDivisionsInPrefs();

                }
            }
        });

        // Set the alert dialog no button click listener
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing on No button clicked
            }
        });

        AlertDialog dialog = builder.create();
        // Display the alert dialog on interface
        dialog.show();
    }




    void ShowPopupMenu()
    {
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(MainActivity.this, fab);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item)
            {
 //               Toast.makeText(MainActivity.this,"You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
            //    Msg.show("Test");
               int option=item.getItemId();
               switch(option)
               { case R.id.one : CloseAndSaveAttendance(); break;
                 case R.id.two :Msg.Show("Continue Attendance"); break;
                   case R.id.three :
                       fab.setBackgroundTintList(getResources().getColorStateList(R.color.colorGreen));
                       Msg.Show("Attendance Discarded");
                       TA.selectedPositions.clear();
                       modified=false;
                       AttendanceInProgress=!AttendanceInProgress;
                       DisplayDivision();
                       break;
               }
                return true;
            }
        });

        popup.show();//showing popup menu
    }
//});//closing the setOnClickListener method

void CloseAndSaveAttendance()
    {

        fab.setBackgroundTintList(getResources().getColorStateList(R.color.colorGreen));
        AttendanceInProgress=false;
        String AL=GetAttendanceLine(); //current line :  AAPAPAPP...etc

        if(HistoryMode)
        {
            String atdLine=model.Divisions.get(currentDivision);
            String temp[];
            temp=atdLine.split("#");
            model.Divisions.set(currentDivision,temp[0]+"#"+temp[1]+"#"+AL);
            model.SaveHistory(Filenamewithpath);
            modified=false;
            FC.setText(String.format("%d",TA.selectedPositions.size()));
            return;

        }


        ///else normal mode so save on sd card
            model.SaveList(AL,Filenamewithpath);
            TA.selectedPositions.clear();
            DisplayDivision();
            modified=false;
            Norecords=false;

    }

    void DisplayDivision()   //// Display division with index currentdivision
    {   TA.Divisions.clear();
        buttonDivTitle.setText(model.GetDivisionTitle(currentDivision));
      //  Msg.Show(model.Divisions.get(currentDivision));
        TA.DisplayDivision(model.Divisions.get(currentDivision));
        if(HistoryMode) {
            setTitle(model.DateArray.get(currentDivision));
            FC.setText(String.format("%d",TA.selectedPositions.size()));
        }
        else
            FC.setText("");

    }


    void InvertAttendance()
    {
        if(!AttendanceInProgress) {Msg.Show("Attendance Mode Off"); return;}
        String Line="";
            for(int i=0;i<TA.numbers.length;i++)
            {Integer tt=new Integer(i);
                if(TA.selectedPositions.contains(tt))
                    Line+="P";
                else
                    Line+="A";
            }

        TA.Fillpositions(Line);
            TA.notifyDataSetChanged();
        FC.setText(String.format("%d",TA.selectedPositions.size()));
        Msg.Show("Selection Inverted");

    }

    void PrintMonthlyReport(String div,int month)
    {
        String[] monthnames = {" ","Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
     //  Msg.Show("test");



        String tempfilename="Report-"+div+"-"+monthnames[month]+".pdf";
        File atdDirectory=getApplicationContext().getFilesDir();
        File pdfFile=new File(atdDirectory,tempfilename);
        String filename = pdfFile.getPath();



        MR.SetCollegeTeacherSubject(CDD.GetCollege(),CDD.GetTeacher(),CDD.GetSubject());
        try {
            MR.PrintAttendanceReportPDF(div,month,filename);
            ShareFile(filename);
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

       // ShareFile(filename);
    }


    public void ShareFile(String sharefilepath){

        File myFile = new File(sharefilepath);

        Intent intentShareFile = new Intent(Intent.ACTION_SEND);

        Uri uri = FileProvider.getUriForFile(MainActivity.this,
                BuildConfig.APPLICATION_ID + ".provider",
                myFile);

        if(myFile.exists())
        {
            intentShareFile.setType("application/pdf");
            intentShareFile.putExtra(Intent.EXTRA_STREAM, uri);

            intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                    "Sharing File Attendance...");
            intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing Attendance File For Backup Purpos...");

            this.startActivity(Intent.createChooser(intentShareFile, "Share File Attendance Data"));
        }
        else
            Msg.Show("File Not Found");
    }


void    SetHistoryMode()
    {
        if(HistoryMode) //if historymode is true then switch it off and load opening screen
        {
            HistoryMode=false;
            model.Divisions.clear();
            model.LoadDivisions();
            CDD.LoadDivisionsFromPrefs();
            currentDivision=0;
            DisplayDivision();
            setTitle(model.GetDateTimeString());
            FC.setText(String.format(""));
            Msg.Show("History Mode Off");
        }
        else
        {
            HistoryMode=true;
            if (Filenamewithpath.isEmpty()) {  Msg.Show("No Records Found"); return;}
            model.LoadHistory(Filenamewithpath);
            currentDivision = model.Divisions.size()-1;
            DisplayDivision();
        }
        fab.setBackgroundTintList(getResources().getColorStateList(R.color.colorGreen));
        AttendanceInProgress=false;

    }


    void JumpOnDate(String DayMonth)
    {
      if(modified) { Msg.Show("Save Before Jump"); return;}
      if(!HistoryMode) { SetHistoryMode(); settingsMenu.getItem(5).setChecked(true); }

      if(model.DateArray.size()>0)
      {   boolean found=false;
          for(int i=0;i<model.DateArray.size();i++)
          if(model.DateArray.get(i).contains(DayMonth))
            {currentDivision=i; found=true; }
          DisplayDivision();
          if(!found) Msg.Show(DayMonth+"  Not Found");
      }

    }


    boolean ClearedHistoryMode()
    {
        if(HistoryMode)   /// if historymode is ON
        { if(modified) {  Msg.Show("History modified, Save or Discard First !");
            return false;}
            SetHistoryMode(); // switch off history
            settingsMenu.getItem(4).setChecked(false);
            return true;
        }
        ////  Now History Mode is Off
        if(modified) {  Msg.Show("Attendance Modified, Save or Discard First !");
            return false; }
        /// Now Attendance is off OR Attendance is ON but not Modified
        /// Make Green in any case
        fab.setBackgroundTintList(getResources().getColorStateList(R.color.colorGreen));
        AttendanceInProgress=false;
        return true;  ///cleard all cases preferencace can be saved

    }


    private String get_atd_FilePath()
    {   File atdDirectory=getApplicationContext().getFilesDir();
        //ContextWrapper contextwrap = new ContextWrapper(getApplicationContext());
        //File atdDirectory=contextwrap.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File atdFile=new File(atdDirectory,"AttendanceData.txt");
        return atdFile.getPath();
    }


}   /////CLASS END




///////////////////////// <<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>///////////////////////////////
//////////////////////////<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>/////////////////////////////
/////////////////////////// Extra Code  Snippets


/*
            if(!AttendanceInProgress) LL.setBackgroundColor(Color.RED);
            else LL.setBackgroundColor( getResources().getColor(R.color.SecondBar));

            AttendanceInProgress=!AttendanceInProgress;
*/

// Change FAB mail icon
//fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_launcher_round2));

            /*
            hide and show FAB toggle

            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
            p.setAnchorId(View.NO_ID);
            fab.setLayoutParams(p);
            if(fabVisible) fab.setVisibility(View.GONE);
            else
                fab.setVisibility(View.VISIBLE);
            fabVisible=!fabVisible;

            */


