package app.carhire.com.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import app.carhire.com.BuildConfig;
import app.carhire.com.R;

import static java.lang.Boolean.TRUE;

public class ViewCar extends AppCompatActivity {

    private ImageView displayImage;
    private FloatingActionButton bookCar;
    private ProgressBar loadCarDetails;
    private TextView bookerDetails;
    private ListView carDetailsList;
    private DatabaseReference rootDbRef;
    private String user_id,car_id, owner_id, image_url,booker_id;
    private ArrayList<String> carDetails;
    private ArrayAdapter<String> adapter;
    private SharedPreferences sharedPref;
    private String prefFile = BuildConfig.APPLICATION_ID + ".PREFERENCE_FILE_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_car);

        //get extras from intent
        car_id = getIntent().getExtras().getString("car_id");
        owner_id = getIntent().getExtras().getString("owner_id");
        image_url = getIntent().getExtras().getString("image_url");

        //get userId from SharedPreferences
        sharedPref = getApplicationContext().getSharedPreferences(prefFile, Context.MODE_PRIVATE);
        user_id = sharedPref.getString("UserId", null);

        //set up toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initialize variables
        displayImage = (ImageView) findViewById(R.id.display_image);
        loadCarDetails = (ProgressBar) findViewById(R.id.load_car_details);
        bookerDetails = (TextView)findViewById(R.id.booker_details);
        carDetailsList = (ListView) findViewById(R.id.car_details_list);
        bookCar = (FloatingActionButton) findViewById(R.id.book_car);
        rootDbRef = FirebaseDatabase.getInstance().getReference();
        carDetails = new ArrayList<String>();
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,carDetails);


        //handle item clicks
        bookCar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //check if car is available for booking
                //if available, book the car and set it to booked
                loadCarDetails.setVisibility(View.VISIBLE);
                rootDbRef.child("cars").child(car_id).child("booked").setValue(user_id).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loadCarDetails.setVisibility(View.GONE);
                        if (task.isSuccessful())
                        {
                            Toast.makeText(ViewCar.this, "Car successfully booked", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), ViewCars.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        }
                        else
                        {
                            Toast.makeText(ViewCar.this, "Could'nt book car.Try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        //method calls
        getCarDetails();

        //update ui

        if (!image_url.equals("No Image"))
        {
            Glide.with(this).load(image_url).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    displayImage.setImageResource(R.drawable.car_placeholder);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                    return false;
                }
            }).into(displayImage);
        }

        carDetailsList.setAdapter(adapter);

        if(user_id.equals(owner_id))
        {
            bookCar.setVisibility(View.GONE);
        }

    }

    private void getCarDetails() {

        rootDbRef.child("cars").child(car_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String bookedStatus = dataSnapshot.child("booked").getValue(String.class);

                carDetails.add(dataSnapshot.child("car_make").getValue(String.class) + " " + dataSnapshot.child("car_model").getValue(String.class));

                if (user_id.equals(owner_id))
                {
                    carDetails.add("You own this car");
                }
                else
                {
                    carDetails.add("Owner : " + dataSnapshot.child("car_owner").getValue(String.class));
                }

                carDetails.add("Engine : " + dataSnapshot.child("engine_size").getValue(String.class));
                carDetails.add("Fuel : " + dataSnapshot.child("fuel_type").getValue(String.class));
                carDetails.add("Transmission : " + dataSnapshot.child("car_transmission").getValue(String.class));
                carDetails.add("Capacity : " + dataSnapshot.child("car_capacity").getValue(String.class));
                carDetails.add("Rating : " + dataSnapshot.child("car_rating").getValue(String.class));
                carDetails.add("Accessories : " + dataSnapshot.child("car_accessories").getValue(String.class));
                carDetails.add("Hiring rate : " + dataSnapshot.child("hire_rate").getValue(String.class));

                if (bookedStatus != null)
                {
                    //check if the car is owned by current user
                    //and if it is booked to show booker details
                    if (user_id.equals(owner_id) && !bookedStatus.equals("available"))
                    {
                        booker_id = bookedStatus;
                        getBookerDetails();
                    }

                    //check if current user has booked the car
                    if (bookedStatus.equals(user_id))
                    {
                        bookCar.setVisibility(View.GONE);
                        carDetails.add("**You have booked this car**");
                    }

                    //check if the car is available for booking
                    if (bookedStatus.equals("available"))
                    {
                        carDetails.add("Booked Status: " + bookedStatus);
                    }
                }

                adapter.notifyDataSetChanged();

                loadCarDetails.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadCarDetails.setVisibility(View.GONE);
                Toast.makeText(ViewCar.this, "Couldn't fetch car details", Toast.LENGTH_LONG).show();
            }
        });
    }

    //get details of whoever has booked
    //the car to show to owner
    private void getBookerDetails() {

        if (booker_id != null)
        {
            rootDbRef.child("users").child(booker_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String booker_details = "This car was booked by : \n";
                    booker_details += "Username : " + dataSnapshot.child("username").getValue(String.class) + "\n";
                    booker_details += "Email : " + dataSnapshot.child("email").getValue(String.class);

                    bookerDetails.setVisibility(View.VISIBLE);
                    bookerDetails.setText(booker_details);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id  = item.getItemId();

        if (id == android.R.id.home)
        {
            finish();
        }

        return true;
    }
}
