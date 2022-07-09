package in.org.ilugbom.attendance;

import android.os.Environment;
import androidx.appcompat.app.AlertDialog;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;

import static android.media.CamcorderProfile.get;

public class MonthlyReport {


//////////////////// NEW ROUTINE FOR FLEXIBLE ROLL ////////////////////////

    int totalDays=0;
    int PresentCount[];
    int DailyCount[]=new int[31]; //For daily present total
    String ClassDiv="";
    String college,teacher,subject;

    ArrayList<String> roll=new ArrayList<String>();//creating new generic arraylist
    ArrayList<String> attendanceLines=new ArrayList<String>();//creating new generic arraylist
    ArrayList<String> APchain=new ArrayList<String>();//creating new generic arraylist
    int strength=35,requiredtables=3;

     Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
     Font small = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL);
     void SetCollegeTeacherSubject(String college,String teacher, String subject)
     { this.college=college; this.teacher=teacher;this.subject=subject;  }

    void FillRollArray()
    {   roll.removeAll(roll);
        APchain.removeAll(APchain);

        String temp[],rolltemp[];

        //assert 4 parts in first split
        temp=attendanceLines.get(0).split("#");
        ClassDiv=new String(temp[1]);
        rolltemp=temp[2].split(",");


        for (int x=0;x<rolltemp.length;x++)
        { roll.add(rolltemp[x]);
            APchain.add("");
        }

        for(int x=0;x<31;x++) DailyCount[x]=0; //initialize dailycount

        PresentCount=new int[rolltemp.length];
    }


    void LoadAttendanceLines(String division, int month)
    {
        attendanceLines.removeAll(attendanceLines);

        String rootDir = Environment.getExternalStorageDirectory().getPath();

        String FileNameWithPath    = Environment.getExternalStorageDirectory().getPath()+"/AttendanceData.atd";

        try {
            File FileToRead = new File(FileNameWithPath);
            FileInputStream FINS = new FileInputStream(FileToRead);
            BufferedReader bfrReader = new BufferedReader(new InputStreamReader(FINS));
            String AttendanceRecord = " ";
            String temp[], Roll;

            String strMonth=String.format("%02d",month);
            while ((AttendanceRecord = bfrReader.readLine()) != null)
            {    if(!AttendanceRecord.substring(3,5).equalsIgnoreCase(strMonth)) continue;
                //Msg.show(AttendanceRecord.substring(3,4));
                 if (AttendanceRecord.contains("#"+division+"#"))
                    attendanceLines.add(AttendanceRecord);
             }
        } catch (Exception e)
          {  Msg.show(e.getMessage());
          }
    }


    void Check_ThirtyOneDays_And_Fill_APChain()
    { boolean datefound=false;
        String currentline="";
        String lastblock="";
        totalDays=0;
        for(int i=1;i<32;i++)
        {
            String TwoDigitMonthDay=String.format("%02d",i);

            datefound=false;
            for(int j=0;j<attendanceLines.size();j++)
            {   currentline=attendanceLines.get(j);
                String str=attendanceLines.get(j).substring(0, 2);
                if(str.equalsIgnoreCase(TwoDigitMonthDay))
                {datefound=true; break;}
            }

            if(datefound)
            { totalDays++;
                lastblock="";
                String temp[];
                //assert 4 parts in first split
                temp=currentline.split("#");
                lastblock+=temp[3];

            }

        for(int k=0;k<roll.size();k++)
            {
                String temp2=APchain.get(k);

                if(datefound)
                { char ap=lastblock.charAt(k);
                    if(ap=='P') { PresentCount[k]++;DailyCount[i-1]++;}
                    APchain.set(k,temp2+ap);

                }
                else
                    APchain.set(k,temp2+" ");

            }
        }

    }


    void PrintAttendanceReportPDF(String divi,int mon) throws DocumentException, IOException
    {  String[] monthnames = {" ","JAN","FEB","MAR","APR","MAY",
            "JUN","JUL","AUG","SEP","OCT","NOV","DEC"};

        LoadAttendanceLines(divi, mon);
        if(attendanceLines.size()<=0) { Msg.Show("No Attendance Record Found"); return;}
        if(strength>200) return;

        requiredtables=strength/35;
        if(strength%35!=0) requiredtables++;

        String rootDir = Environment.getExternalStorageDirectory().getPath();
        String filename=rootDir + "/" + "Report-"+divi+"-"+monthnames[mon]+".pdf";

        Document document = new Document(PageSize.A4.rotate());
        document.setMargins(50, 10, 25, 25);
        PdfWriter.getInstance(document, new FileOutputStream(filename));
        document.open();

        FillRollArray();
        Check_ThirtyOneDays_And_Fill_APChain();

        AddTheHeader(document,monthnames[mon]);
        AttendanceGrid(document);

//	        AddFooter(document);

        document.close();

        Msg.Show("Report-"+divi+"-"+monthnames[mon]+".pdf Created");
    }


    void AddTheHeader(Document document,String month) throws DocumentException, IOException
    {
        PdfPTable table = new PdfPTable(3);

        PdfPCell cell = new PdfPCell(new Phrase("Attendance Report"));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setBorder(PdfPCell.NO_BORDER);
        table.setWidthPercentage(95);
        table.addCell(cell);


        cell = new PdfPCell(new Phrase(college));cell.setBorder(PdfPCell.NO_BORDER);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        table.addCell(cell);


        cell = new PdfPCell(new Phrase(month));cell.setBorder(PdfPCell.NO_BORDER);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Teacher : "+teacher));cell.setBorder(PdfPCell.NO_BORDER);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Class-Div : "+ ClassDiv));cell.setBorder(PdfPCell.NO_BORDER);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        table.addCell(cell);


        cell = new PdfPCell(new Phrase("Subject : "+subject));cell.setBorder(PdfPCell.NO_BORDER);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);


        table.setSpacingAfter(10f);
        document.add(table);

    }

    void AttendanceGrid(Document document) throws DocumentException, IOException
    {

        float col[]={4,8,2,2,2,2,2,2,2,2,2,2,
                2,2,2,2,2,2,2,2,2,2,
                2,2,2,2,2,2,2,2,2,2,
                2,4,5};

        //////////   TITLE ROW  ///////////////////////

        PdfPTable table2 = new PdfPTable(col);
        table2.setWidthPercentage(95);
        PdfPCell cell = new PdfPCell(new Phrase("Sr No",normal));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setPaddingBottom(5f);
        table2.addCell(cell);

        cell = new PdfPCell(new Phrase("Roll No",normal));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        //cell.setBorder(PdfPCell.NO_BORDER);
        table2.addCell(cell);

        String dateno;
        for(int j=0;j<31;j++)
        {
            dateno=String.format("%d",j+1);
            cell = new PdfPCell(new Phrase(dateno,normal));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setPaddingBottom(5f);
            table2.addCell(cell);
        }

        cell = new PdfPCell(new Phrase("Total",normal));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        //cell.setBorder(PdfPCell.NO_BORDER);
        table2.addCell(cell);

        cell = new PdfPCell(new Phrase(" % ",normal));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        //cell.setBorder(PdfPCell.NO_BORDER);
        table2.addCell(cell);

        ////////////////// END OF TITLE ROW

        ////////////// REMAING TABLE ROWS

        String srno;
        for (int i=0;i<roll.size();i++)
        {
            srno=String.format("%d",i+1);
            cell = new PdfPCell(new Phrase(srno,normal));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setPaddingBottom(5f);
            table2.addCell(cell);

            cell = new PdfPCell(new Phrase(roll.get(i),normal));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            //cell.setBorder(PdfPCell.NO_BORDER);
            table2.addCell(cell);

            for(int j=0;j<31;j++)
            { String APmark=""+APchain.get(i).charAt(j);
                cell = new PdfPCell(new Phrase(APmark,normal));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                cell.setPaddingBottom(5f);
                table2.addCell(cell);
            }

            cell = new PdfPCell(new Phrase(String.format("%02d",PresentCount[i]),normal));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            //cell.setBorder(PdfPCell.NO_BORDER);
            table2.addCell(cell);

            DecimalFormat df = new DecimalFormat("000.00");
            double percent = PresentCount[i]*100/totalDays;
            String percentage = df.format(percent);

            cell = new PdfPCell(new Phrase(percentage,normal));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            //cell.setBorder(PdfPCell.NO_BORDER);
            table2.addCell(cell);
        }
///////////// Second-Last Present  count row

        //srno=String.format("%d",0);
        cell = new PdfPCell();
        cell.setBorder(PdfPCell.NO_BORDER);
        table2.addCell(cell);

        cell = new PdfPCell(new Phrase("Total - P",normal));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        //cell.setBorder(PdfPCell.NO_BORDER);
        table2.addCell(cell);

        for(int j=0;j<31;j++)
        {   String APmark="";
            if(DailyCount[j]>0) APmark=String.format("%d",DailyCount[j]);
            cell = new PdfPCell(new Phrase(APmark,small));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setPaddingBottom(5f);
            table2.addCell(cell);
        }

        cell = new PdfPCell();
        cell.setBorder(PdfPCell.NO_BORDER);
        table2.addCell(cell);

        cell = new PdfPCell();
        cell.setBorder(PdfPCell.NO_BORDER);
        table2.addCell(cell);

///////////////Second Last Line End /////////////////////////////////////

///////////// Last-Line  Absent Count row

        //srno=String.format("%d",0);
        cell = new PdfPCell();
        cell.setBorder(PdfPCell.NO_BORDER);
        table2.addCell(cell);

        cell = new PdfPCell(new Phrase("Total - A",normal));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        //cell.setBorder(PdfPCell.NO_BORDER);
        table2.addCell(cell);

        for(int j=0;j<31;j++)
        {   String APmark="";
            if(DailyCount[j]>0) APmark=String.format("%d",roll.size()-DailyCount[j]);
            cell = new PdfPCell(new Phrase(APmark,small));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setPaddingBottom(5f);
            table2.addCell(cell);
        }

        cell = new PdfPCell();
        cell.setBorder(PdfPCell.NO_BORDER);
        table2.addCell(cell);

        cell = new PdfPCell();
        cell.setBorder(PdfPCell.NO_BORDER);
        table2.addCell(cell);

///////////////Second Last Line End /////////////////////////////////////



        document.add(table2);
    }


}  ////class ends


