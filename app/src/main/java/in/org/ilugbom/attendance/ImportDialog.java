package in.org.ilugbom.attendance;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatDialogFragment;

public class ImportDialog extends AppCompatDialogFragment {

    //CancelButton = (Button) findViewById(R.id.btn_cancel);
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.import_dialog,null);
        builder.setView(view)
                .setTitle("Import Text");


        return builder.create();


    }



}
