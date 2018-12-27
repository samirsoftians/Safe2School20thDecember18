package sushant.com.Safe2School;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by twtech on 13/8/18.
 */

public class CustomLeaderAdapter extends RecyclerView.Adapter<CustomLeaderAdapter.MyViewHolder> {

    List<String> vehicleNumber;
    List<String> vdate;
    List<String> vtime;
    List<String> vspeed;
    List<String> vlat;
    List<String> vlng;
    List<String> vCode;
    String location,updatedDate;
    Context context;

    public CustomLeaderAdapter(Context context, List<String> vehicleNumber, List<String> date, List<String> time,List<String> lat,List<String> lng,List<String> vcode,List<String> speed) {
        this.context = context;
        this.vehicleNumber = vehicleNumber;
        this.vdate = date;
        this.vtime = time;
        this.vspeed=speed;
        this.vlat=lat;
        this.vlng=lng;
        this.vCode=vcode;
        Log.e("inside Constructor","Constructor called");
        Log.e("list1", String.valueOf(vehicleNumber));
        Log.e("list2", String.valueOf(date));
        Log.e("list3", String.valueOf(time));
        Log.e("list4", String.valueOf(lat));
        Log.e("list5", String.valueOf(speed));
        Log.e("list6", String.valueOf(lng));
        Log.e("list7", String.valueOf(vcode));
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
// infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_navigation_drawer, parent, false);
// set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

    public void onBindViewHolder(MyViewHolder holder, final int position) {
// set the data in items
        holder.vehicleNo.setText(vehicleNumber.get(position));
        SimpleDateFormat spf=new SimpleDateFormat("yyyy-MM-dd ");
        Date newDate = null;
        try {
            newDate = spf.parse(vdate.get(position));
            spf = new SimpleDateFormat("dd MMM yyyy");
            updatedDate = spf.format(newDate);
            holder.date.setText("Date: "+updatedDate);
            Log.e("onBindView","Method called");
            location=getAddress(context,Double.valueOf(vlat.get(position)),Double.valueOf(vlng.get(position)));
            holder.time.setText(vtime.get(position));
            holder.location.setText("Location: "+location);
            holder.speed.setText("Speed: "+vspeed.get(position)+"KMPH");
        }catch (Exception e){
           Log.e("Excep",e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        Log.e("Custom size", String.valueOf(vehicleNumber.size()));
        return vehicleNumber.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // init the item view's
        TextView vehicleNo;
        TextView date,time,location,speed;
        ImageView imageView;

        public MyViewHolder(final View itemView) {
            super(itemView);
            context = itemView.getContext();

// get the reference of item view's
            Log.e("MYViewHolder","inside class");
            vehicleNo = (TextView) itemView.findViewById(R.id.vName);
            date = (TextView) itemView.findViewById(R.id.vDate);
            time = (TextView) itemView.findViewById(R.id.vTime);
            location= (TextView) itemView.findViewById(R.id.vLocation);
            speed= (TextView) itemView.findViewById(R.id.vSpeed);
            imageView=(ImageView)itemView.findViewById(R.id.google);
//            loc=getAddress(context,Double.valueOf(vlat.get(getPosition())),Double.valueOf(vlng.get(getPosition())));
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(context,NavigationDrawerActivity.class);
                    i.putExtra("vcode",vCode.get(getPosition()));
                    i.putExtra("vnum",vehicleNumber.get(getPosition()));
                    i.putExtra("vdate",vdate.get(getPosition()));
                    i.putExtra("vlat",vlat.get(getPosition()));
                    i.putExtra("vlng",vlng.get(getPosition()));
                    i.putExtra("vtime",vtime.get(getPosition()));
                    i.putExtra("vspeed",vspeed.get(getPosition()));
                    context.startActivity(i);
                }
            });
        }

    }

    public String getAddress(Context context, double LATITUDE, double LONGITUDE) {
        String address=" ";
        //Set Address
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null && addresses.size() > 0) {



                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
            }
        } catch (IOException e) {
            Log.e("ss",e.getMessage());
            e.printStackTrace();
        }
        return address;
    }
}