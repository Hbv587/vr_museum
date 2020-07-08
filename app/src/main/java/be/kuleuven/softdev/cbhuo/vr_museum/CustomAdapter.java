package be.kuleuven.softdev.cbhuo.vr_museum;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends BaseAdapter {
    Context context;
    int logos[];
    List<ImagePiece> pieces;
    LayoutInflater inflter;
    public CustomAdapter(Context applicationContext, List<ImagePiece> pieces) {
        this.context = applicationContext;
        this.pieces = pieces;
        inflter = (LayoutInflater.from(applicationContext));
    }
    @Override
    public int getCount() {
        return pieces.size();
    }
    @Override
    public Object getItem(int i) {
        return null;
    }
    @Override
    public long getItemId(int i) {
        return 0;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.activity_gridview, null); // inflate the layout
        ImageView icon = (ImageView) view.findViewById(R.id.icon); // get the reference of ImageView
        icon.setImageBitmap(pieces.get(i).bitmap);
        return view;
    }
}
