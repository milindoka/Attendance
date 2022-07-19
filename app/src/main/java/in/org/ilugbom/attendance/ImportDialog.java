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
    private Button importButton;
    private Button cancelButton;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.import_dialog,null);
        builder.setView(view)
                .setTitle("Import Text");

importButton=view.findViewById(R.id.btn_import);
cancelButton=view.findViewById(R.id.btn_cancel);

cancelButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
    dismiss();
    }
});

        return builder.create();


    }



}
