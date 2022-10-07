package in.org.ilugbom.attendance;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class ImportDialog extends AppCompatDialogFragment {
    private Button importButton;
    private Button cancelButton;
    private EditText ImportedEditText;
    private String datafilepath;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.import_dialog,null);
        builder.setView(view)
                .setTitle("Import Text");

        importButton=view.findViewById(R.id.btn_import);
        cancelButton=view.findViewById(R.id.btn_cancel);
        ImportedEditText=view.findViewById(R.id.Imported_Text);

        cancelButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
    dismiss();
    }
        });

        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fromeditBox=ImportedEditText.getText().toString();
                //Msg.Show(fromeditBox);
                SaveImported(datafilepath,fromeditBox);
                dismiss();
            }
        });

        return builder.create();


    }

void SetFilePath(String pathfromMain) {datafilepath=pathfromMain; }

    void SaveImported(String FileNameWithPath,String txtData)
    {
        int i;

        try {
            File myFile = new File(FileNameWithPath);
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

            myOutWriter.write(txtData);
            myOutWriter.close();
            fOut.close();
            Msg.Show("Data Saved");


        } catch (Exception e)
        {
            Msg.show(e.getMessage());
        }
    }



}
