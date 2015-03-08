package desai.com.raise;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ndesai on 3/4/15.
 */
public class BrandNameAdapter extends ArrayAdapter {

    private List<GiftCard> mGiftCards;
    private Context context;


    public BrandNameAdapter(Context context,int res, List<GiftCard> items) {
        super(context,res,items);
        this.context=context;
        this.mGiftCards=items;

    }


    @Override
    public int getCount() {
        return mGiftCards.size();
    }

    @Override
    public String getItem(int position) {
        return mGiftCards.get(position).getBrand();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return super.getView(position, convertView, parent);

    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.brandname_view, parent, false);

        TextView title = (TextView) convertView.findViewById(R.id.brand_name);
        title.setText(getItem(position));
        return convertView;
    }
}
