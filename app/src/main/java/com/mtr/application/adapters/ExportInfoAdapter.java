package com.mtr.application.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mtr.application.shared.ExportArticle;

import application.R;

public class ExportInfoAdapter extends ArrayAdapter<ExportArticle> {

    private final Context context;
    private final ExportArticle[] data;


    public ExportInfoAdapter(Context context, ExportArticle[] data) {
        super(context, R.layout.export_info_adapter, data);
        this.context = context;
        this.data = data;
    }




    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.export_info_adapter, null, true);
        TextView textView = (TextView) rowView.findViewById(R.id.adapterTextView);

        if ("vratky".equalsIgnoreCase(data[position].getName()) || "objednávky".equalsIgnoreCase(data[position].getName())) {
            textView.setTypeface(null, Typeface.BOLD);
            textView.setText(data[position].getName());
        } else {
            textView.setText(String.format("%s\n%s, Množství: %s", data[position].getName(), data[position].getEan(), data[position].getExportAmount()));
        }


        return rowView;
    }
}
