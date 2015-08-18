package ndhunju.com.folderpicker.library;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * A simple adapter that takes in directories in string format
 */
public class DirListAdapter<String> extends ArrayAdapter<String> {

    public DirListAdapter(Context context, List<String> directories) {
        super(context,android.R.layout.select_dialog_item, android.R.id.text1, directories);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        if (v instanceof TextView) {
            //ellipsize long directores
            TextView tv = (TextView) v;
            tv.setBackgroundColor(Color.LTGRAY);
            tv.setSingleLine();
            tv.setEllipsize(TextUtils.TruncateAt.END);
        }
        return v;
    }
}
