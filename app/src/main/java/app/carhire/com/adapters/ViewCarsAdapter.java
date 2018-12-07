package app.carhire.com.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import app.carhire.com.R;
import app.carhire.com.models.CarModel;

public class ViewCarsAdapter extends BaseAdapter {

    Context context;
    ArrayList<CarModel> availableCars;
    LayoutInflater inflater;

    public ViewCarsAdapter(Context context, ArrayList<CarModel> availableCars) {
        this.context = context;
        this.availableCars = availableCars;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return availableCars.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.car_list_item,viewGroup,false);
        ViewHolder holder = new ViewHolder();
        holder.displayImage = (ImageView) view.findViewById(R.id.display_image);
        holder.makeAndModel = (TextView) view.findViewById(R.id.car_make_and_model);
        holder.carDetails = (TextView) view.findViewById(R.id.car_details);
        holder.carRating = (TextView) view.findViewById(R.id.car_rating);
        view.setTag(holder);

        String car_details = "";
        car_details = "Owner : " + availableCars.get(i).getCarOwner() + "\n";
        car_details += "Engine : " + availableCars.get(i).getEngineSize() + "\n";
        car_details += "Capacity : " + availableCars.get(i).getCarCapacity() + "\n";

        if (availableCars.get(i).getHireRate().equals("You have booked this car"))
        {
            car_details += "**" + availableCars.get(i).getHireRate() + "**";
        }
        else
        {
            car_details += "Hire rate : " + availableCars.get(i).getHireRate();
        }

        String image_url = availableCars.get(i).getImageUrl();
        String rating = availableCars.get(i).getCarRating();

        if (!image_url.equals("No Image"))
        {
            Glide.with(context).load(image_url).into(holder.displayImage);
        }

        holder.makeAndModel.setText(availableCars.get(i).getCarMake() + " " + availableCars.get(i).getCarModel());
        holder.carDetails.setText(car_details);

        if (!rating.equals("No rating"))
        {
            holder.carRating.setText(rating);
        }
        else
        {
            holder.carRating.setVisibility(View.GONE);
        }

        return view;
    }

    private class ViewHolder{
        ImageView displayImage;
        TextView makeAndModel;
        TextView carDetails;
        TextView carRating;
    }
}
