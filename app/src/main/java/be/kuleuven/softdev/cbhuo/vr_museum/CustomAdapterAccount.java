package be.kuleuven.softdev.cbhuo.vr_museum;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CustomAdapterAccount extends BaseAdapter {
    Context context;
    List<ImagePiece> pictures;
    List<String> pictureInfos;
    LayoutInflater inflter;
    public CustomAdapterAccount(Context applicationContext, List<ImagePiece> pictures, List<String> pictureInfos) {
        this.context = applicationContext;
        this.pictures = pictures;
        this.pictureInfos = pictureInfos;
        inflter = (LayoutInflater.from(applicationContext));
    }
    @Override
    public int getCount() {
        return pictures.size();
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
        view = inflter.inflate(R.layout.activity_gridview_account, null); // inflate the layout
        ImageView icon = (ImageView) view.findViewById(R.id.icon); // get the reference of ImageView
        icon.setImageBitmap(pictures.get(i).bitmap); // set logo images

        TextView pictureInfo = (TextView)view.findViewById(R.id.pictureInfoText);
        pictureInfo.setText(pictureInfos.get(i));
        return view;
    }
}
