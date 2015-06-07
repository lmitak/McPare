package hr.apps.cookies.mcpare.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import hr.apps.cookies.mcpare.R;
import hr.apps.cookies.mcpare.data.Zapis;
import hr.apps.cookies.mcpare.data.ZapisHelper;

/**
 * Created by Antonio on 23.5.2015..
 */
public class NumberPickerDialog extends DialogFragment {
    private NumberPicker numberPicker;
    private Button btnCancel,btnOK;

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        AlertDialog.Builder bilder = new AlertDialog.Builder(getActivity());
       /* Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.number_picker_dialog);*/
        final View view = getActivity().getLayoutInflater().inflate(R.layout.number_picker_dialog, null);

        final EditText editText = (EditText)view.findViewById(R.id.addPosition);
        final DatePicker dpStart = (DatePicker) view.findViewById(R.id.dateStart);
        final DatePicker dpEnd = (DatePicker) view.findViewById(R.id.dateEnd);

        bilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ZapisHelper zHelper = new ZapisHelper(getActivity().getApplicationContext());

                int dayStart = dpStart.getDayOfMonth();
                int monthStart = dpStart.getMonth();
                int yearStart = dpStart.getYear();

                int dayEnd = dpEnd.getDayOfMonth();
                int monthEnd = dpEnd.getMonth();
                int yearEnd = dpEnd.getYear();

                //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Calendar calendarStart = Calendar.getInstance();
                Calendar calendarEnd = Calendar.getInstance();

                calendarStart.set(yearStart,monthStart,dayStart);
                calendarEnd.set(yearEnd,monthEnd,dayEnd);

                java.util.Date dateStart, dateEnd;
                dateStart = calendarStart.getTime();
                dateEnd = calendarEnd.getTime();

                Zapis zapis = new Zapis(editText.getText().toString(),new Date(dateStart.getTime()),
                        new Date(dateEnd.getTime()),new Double(0),new Double(0));

                zHelper.insertZapis(zapis);
            }
        }).setNegativeButton("Odustani", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setTitle("Unos...");

        return bilder.show();

    }


}
