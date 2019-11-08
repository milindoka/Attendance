package in.org.ilugbom.attendance;

import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.security.auth.Subject;

import static android.media.CamcorderProfile.get;
import static in.org.ilugbom.attendance.MainActivity.currentDivision;

public class MonthlyReport {



    String DIV,MONT;
    void SetDIVMON(String div,String month) { DIV=div; MONT=month; }
    void callcdd(CreateDivDialog CDD){ this.CDD = CDD; }

    Model model = new Model();
    CreateDivDialog CDD=new CreateDivDialog();
    private MainActivity MA;
    Msg msg=new Msg();

    ArrayList<String> DateArray = new ArrayList<String>();
    ArrayList<String> Divisions = new ArrayList<String>();
    ArrayList<String> RollNos = new ArrayList<String>();
    ArrayList<String> PresencyLine = new ArrayList<String>();

    int columns = 35;
    String[][] headerMatrix = new String[1][8];
    String[][] matrix = new String[200][columns];

    String Month[] = {"JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY",
            "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"};

    String NumericMonth[] = {"01", "02", "03", "04", "05", "06",
            "07", "08", "09", "10", "11", "12"};

    String StaticRow [] = {"Sr.No","Roll.No",
            "01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
            "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
            "Total", "Percent" };

    void SetRef(Model model){this.model=model;}
    void SetMA(MainActivity MA){this.MA=MA;}

    void AttendanceReportPdf() throws FileNotFoundException,DocumentException{

        matrix = new String[200][columns];
        headerMatrix = new String[1][8];
        File myFile = new File("/sdcard/Monthly Report.pdf");

        OutputStream output = new FileOutputStream(myFile);
        Document document = new Document();
        document = new Document(PageSize.A4.rotate());

        PdfWriter.getInstance(document, output);
        document.open();
//            document.add(new Paragraph("Hello World!"));
        AddHeader(document);
         FillHeaderMmatrix();
    //    FillMatrix();
        matrixtoPdf(document);

        document.close();
    }

    void AddHeader(Document document) throws DocumentException
    {
        PdfPTable table = new PdfPTable(1);
        PdfPCell cell = new PdfPCell();
        cell.setBorder(PdfPCell.NO_BORDER);
        table.setWidthPercentage(100);
        table.addCell(cell);
        String CollNem = CDD.college;
        cell = new PdfPCell(new Phrase(CollNem.toUpperCase()));
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        table.addCell(cell);

        table.setSpacingAfter(5f);
        document.add(table);
    }

    public void matrixtoPdf(Document document) throws DocumentException{

        float colwidth[] = {4,5,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,4,5};
        float colwidth1[] = {7,9,4,6,5,5,5,5};

        PdfPTable table1 = new PdfPTable(colwidth1);
        table1.setWidthPercentage(100);

        for(int i = 0; i < headerMatrix[0].length; i++){
            table1.addCell(headerMatrix[0][i]);
        }

        PdfPTable table = new PdfPTable(colwidth);
        table.setWidthPercentage(100);

        for(int i = 0; i < columns; i++){
            table.addCell(StaticRow[i]);
        }

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++)
            {
                table.addCell(matrix[i][j]);
            }
        }

        table.setHeaderRows(1);
        table1.setSpacingAfter(2f);
        document.add(table1);
        document.add(table);
    }

    void FillMatrix(){
        String date = "", rollRange;


        LoadFromHistory (DIV, MONT);
        try {
            String AttendenceData;

            int Last= PresencyLine.size();
           // rollRange = RollNos.get(0);
            String RollNoArray [] = RollNos.get(0).split(",");
         //   rollNoStart = RollNoArray[0];

            int TotNoStnts = RollNoArray.length;

            for (int i = 0; i < TotNoStnts; i++) {         // matrix.length = Tot No of Students
                for (int j = 0; j < matrix[i].length; j++){   // matrix[i].length = 35 (Columns)
                    matrix[i][j]=" ";
                }
            }

            for(int i = 0; i < PresencyLine.size(); i++){

                date = DateArray.get(i).substring(0,2);
                AttendenceData = PresencyLine.get(i);
                for(int j = 0; j < TotNoStnts; j++){
                    matrix[j][Integer.parseInt(date)+1] = Character.toString(AttendenceData.charAt(j));
                }

            }

            float percent, Total;
            for(int i = 0; i < TotNoStnts; i++){
                int numofPs = 0, numofAs = 0 ;

                for(int j = 0; j < 31; j++){
                    if(matrix[i][j].contains("P")) numofPs++;
                    if(matrix[i][j].contains("A")) numofAs++;
                }

                Total = numofAs+numofPs;
                //              matrix[i][33] = String.valueOf(numofPs)+"/"+String.valueOf(Total);
                matrix[i][33] = String.valueOf(numofPs);
                percent = numofPs*100/Total;
                String percentage = String.format("%.2f",percent);
                matrix[i][34] = String.valueOf(percentage);
            }

            for(int i = 0; i < TotNoStnts; i++){
                matrix[i][1] = RollNoArray[i];
            }

            for(int i = 0; i < TotNoStnts; i++){
                matrix[i][0]= "" + (i+1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void FillHeaderMmatrix(){
        String HeaderRow[] = {"Teacher's Name", " ", "Subject", " ",
                "Class & Div"    ," ", "Month",   " " };
        String month = "";

//        Div = CDD.tempDivTitle;
//        Div=model.GetDivisionTitle(currentDivision);

        String TrName = CDD.teacher;
        String Subject = CDD.subject;

        for(int i = 0; i < HeaderRow.length; i++){
            headerMatrix[0][i] = HeaderRow[i];
        }

        LoadFromHistory (DIV, MONT);
        try {
            String date = DateArray.get(0);
            month = date.substring(3, 5);
            for(int i = 0; i < Month.length; i++) {
                if (month.contains(NumericMonth[i])) month = Month[i];
            }

            for(int i = 0; i < Divisions.size(); i++) {
                DIV = Divisions.get(i);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

//        Msg.show(month);
        headerMatrix[0][1] = TrName;
        headerMatrix[0][3] = Subject;
        headerMatrix[0][5] = DIV;
        headerMatrix[0][7] = month;
    }

    public void LoadFromHistory(String div, String month )
    {

        String FileNameWithPath = "/sdcard/AttendanceData.atd";
        try {
            File FileToRead = new File(FileNameWithPath);
            FileInputStream FINS = new FileInputStream(FileToRead);
            BufferedReader bfrReader = new BufferedReader(new InputStreamReader(FINS));
            String AttendanceRecord = " ";
            String temp[], Roll;
            DateArray.clear();
            Divisions.clear();
            PresencyLine.clear();
            RollNos.clear();

            while ((AttendanceRecord = bfrReader.readLine()) != null) {

                if (!AttendanceRecord.contains("#")) continue;
                temp = AttendanceRecord.split("#");
                if(temp[0].contains(month) &&  temp[1].contains(div)) {
                    DateArray.add(temp[0]);
                    Divisions.add(temp[1]);
                    RollNos.add(temp[2]);
                    Msg.Show(temp[2]);
                    PresencyLine.add(temp[3]);
                }
            }
        } catch (Exception e){

            Msg.show(e.getMessage());
            //    Toast.makeText(getBaseContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
//////////////////// NEW ROUTINE FOR FLEXIBLE ROLL ////////////////////////

    int totalDays=0;
    int PresentCount[];
    String ClassDiv="";

    ArrayList<String> roll=new ArrayList<String>();//creating new generic arraylist
    ArrayList<String> attendanceLines=new ArrayList<String>();//creating new generic arraylist
    ArrayList<String> APchain=new ArrayList<String>();//creating new generic arraylist
    int strength=35,requiredtables=3;


    Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);


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

        PresentCount=new int[rolltemp.length];

    }


    void LoadAttendanceLines(String division, int month)
    {
        attendanceLines.removeAll(attendanceLines);
       // Msg.show(month);
        String FileNameWithPath = "/sdcard/AttendanceData.atd";
        try {
            File FileToRead = new File(FileNameWithPath);
            FileInputStream FINS = new FileInputStream(FileToRead);
            BufferedReader bfrReader = new BufferedReader(new InputStreamReader(FINS));
            String AttendanceRecord = " ";
            String temp[], Roll;
            DateArray.clear();
            Divisions.clear();
            PresencyLine.clear();
            RollNos.clear();

            String strMonth=String.format("%02d",month);
            while ((AttendanceRecord = bfrReader.readLine()) != null)
            {    if(!AttendanceRecord.substring(3,5).equalsIgnoreCase(strMonth)) continue;
                //Msg.show(AttendanceRecord.substring(3,4));
                 if (AttendanceRecord.contains("#"+division+"#"))
                    attendanceLines.add(AttendanceRecord);
             }
        } catch (Exception e){

            Msg.show(e.getMessage());
            //    Toast.makeText(getBaseContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
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
                    if(ap=='P') PresentCount[k]++;
                    APchain.set(k,temp2+ap);

                }
                else
                    APchain.set(k,temp2+" ");

            }
        }

    }


    void PrintAttendanceReportPDF(String divi,int mon) throws DocumentException, IOException
    {  String[] monthnames = {" ","Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        LoadAttendanceLines(divi, mon);
       if(attendanceLines.size()<=0) { Msg.Show("No Attendance Record Found"); return;}
        if(strength>200) return;

        requiredtables=strength/35;
        if(strength%35!=0) requiredtables++;

        String filename="/sdcard/monthlyAttendance.pdf";
        Document document = new Document(PageSize.A4.rotate());
        document.setMargins(50, 10, 25, 25);
        PdfWriter.getInstance(document, new FileOutputStream(filename));
        document.open();

        //    com.itextpdf.text.Font NORMAL = new com.itextpdf.text.Font(FontFamily.TIMES_ROMAN, 12);

        FillRollArray();
        Check_ThirtyOneDays_And_Fill_APChain();

        AddTheHeader(document);
        AttendanceGrid(document);

//	        AddFooter(document);


        document.close();

        Msg.Show("Report For "+divi+"-"+monthnames[mon]+" Created");
    }



    void AddTheHeader(Document document) throws DocumentException, IOException
    {
        PdfPTable table = new PdfPTable(3);
        PdfPCell cell = new PdfPCell(new Phrase(" "));
        cell.setBorder(PdfPCell.NO_BORDER);
        table.setWidthPercentage(95);
        table.addCell(cell);


        cell = new PdfPCell(new Phrase("SIWS College"));cell.setBorder(PdfPCell.NO_BORDER);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        table.addCell(cell);


        cell = new PdfPCell(new Phrase(" "));cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Name of Teacher :"));cell.setBorder(PdfPCell.NO_BORDER);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        table.addCell(cell);

        System.out.println(ClassDiv);
        cell = new PdfPCell(new Phrase("Class-Div : "+ ClassDiv));cell.setBorder(PdfPCell.NO_BORDER);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        table.addCell(cell);


        cell = new PdfPCell(new Phrase("Subject :"));cell.setBorder(PdfPCell.NO_BORDER);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Date : 23/02/18"));cell.setBorder(PdfPCell.NO_BORDER);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
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

        document.add(table2);
    }


}  ////class ends


