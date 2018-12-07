package app.carhire.com.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import app.carhire.com.BuildConfig;
import app.carhire.com.R;
import app.carhire.com.adapters.ViewCarsAdapter;
import app.carhire.com.models.CarModel;


public class ViewCars extends AppCompatActivity {

    private TextView welcome, noAvailableCars, here;
    private ListView carList;
    private FloatingActionButton postCar;
    private ProgressBar loadCars;
    private ViewCarsAdapter carsAdapter;
    private ArrayList<CarModel> availableCars;
    private DatabaseReference rootDbRef;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPrefEditor;
    private String userId,userType,userName;
    private String prefFile = BuildConfig.APPLICATION_ID + ".PREFERENCE_FILE_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cars);
        //check if user is logged in
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() == null){
            //redirect to login
            startActivity(new Intent(ViewCars.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        }

        //set up toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initialize variables
        welcome = (TextView)findViewById(R.id.welcome);
        here = (TextView)findViewById(R.id.here_is_a_list);
        noAvailableCars = (TextView)findViewById(R.id.no_available_cars);
        carList = (ListView)findViewById(R.id.car_list);
        postCar = (FloatingActionButton)findViewById(R.id.post_car);
        loadCars = (ProgressBar)findViewById(R.id.load_available_vehicles);
        availableCars = new ArrayList<>();
        carsAdapter = new ViewCarsAdapter(this,availableCars);
        rootDbRef = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = getSharedPreferences(prefFile,Context.MODE_PRIVATE);
        sharedPrefEditor = sharedPreferences.edit();

        //get users details from shared pref
        userId = sharedPreferences.getString("UserId", "");
        userType = sharedPreferences.getString("UserType", "");
        userName = sharedPreferences.getString("UserName", "");

        //handle item clicks
        carList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String car_id = availableCars.get(i).getCarId();
                String owner_id = availableCars.get(i).getOwnerId();
                String image_url = availableCars.get(i).getImageUrl();

                Intent viewCar = new Intent(ViewCars.this, ViewCar.class);
                viewCar.putExtra("car_id",car_id);
                viewCar.putExtra("owner_id",owner_id);
                viewCar.putExtra("image_url",image_url);
                startActivity(viewCar);

            }
        });

        postCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(new Intent(ViewCars.this,PostCar.class)));
            }
        });

        //update ui
        carList.setAdapter(carsAdapter);
        welcome.setText("Welcome " + userName);
        if (userType.equals("Client"))
        {
            postCar.setVisibility(View.GONE);
        }

        //method call
        getAvailableCars();

    }

    //method to get available cars on firebase database
    private void getAvailableCars(){

        rootDbRef.child("cars").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                availableCars.clear();

                if (userType.equals("Client"))
                {
                    for (DataSnapshot ds : dataSnapshot.getChildren())
                    {
                        CarModel carModel = new CarModel();
                        carModel.setCarId(ds.child("car_id").getValue(String.class));
                        carModel.setImageUrl(ds.child("image_url").getValue(String.class));
                        carModel.setCarMake(ds.child("car_make").getValue(String.class));
                        carModel.setCarModel(ds.child("car_model").getValue(String.class));
                        carModel.setCarOwner(ds.child("car_owner").getValue(String.class));
                        carModel.setEngineSize(ds.child("engine_size").getValue(String.class));
                        carModel.setCarCapacity(ds.child("car_capacity").getValue(String.class));
                        carModel.setCarRating(ds.child("car_rating").getValue(String.class));
                        carModel.setBooked(ds.child("booked").getValue(String.class));
                        //[START] select only cars that are not booked
                        if (carModel.getBooked().equals(userId))
                        {
                            carModel.setHireRate("You have booked this car");
                            availableCars.add(carModel);
                        }
                        else if(carModel.getBooked().equals("available"))
                        {
                            carModel.setHireRate(ds.child("hire_rate").getValue(String.class));
                            availableCars.add(carModel);
                        }
                        //[END] select only cars that are not booked
                    }
                }
                else if (userType.equals("Owner"))
                {
                    for (DataSnapshot ds : dataSnapshot.getChildren())
                    {
                        CarModel carModel = new CarModel();
                        carModel.setCarId(ds.child("car_id").getValue(String.class));
                        carModel.setImageUrl(ds.child("image_url").getValue(String.class));
                        carModel.setCarMake(ds.child("car_make").getValue(String.class));
                        carModel.setCarModel(ds.child("car_model").getValue(String.class));
                        carModel.setOwnerId(ds.child("owner_id").getValue(String.class));
                        carModel.setCarOwner(ds.child("car_owner").getValue(String.class));
                        carModel.setEngineSize(ds.child("engine_size").getValue(String.class));
                        carModel.setCarCapacity(ds.child("car_capacity").getValue(String.class));
                        carModel.setCarRating(ds.child("car_rating").getValue(String.class));
                        carModel.setHireRate(ds.child("hire_rate").getValue(String.class));
                        carModel.setBooked(ds.child("booked").getValue(String.class));
                        //[START] select only cars that belong to logged in user
                        if(carModel.getOwnerId().equals(userId)){
                            availableCars.add(carModel);
                        }
                        //[END] select only cars that belong to logged in user
                    }
                    here.setText("Here is a list of your cars");
                }

                carsAdapter.notifyDataSetChanged();
                loadCars.setVisibility(View.GONE);

                if (availableCars.isEmpty())
                {
                    noAvailableCars.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadCars.setVisibility(View.GONE);
                Toast.makeText(ViewCars.this, "Couldn't fetch available cars", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_cars_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.log_out)
        {
            mAuth.signOut();
            sharedPrefEditor.clear();
            sharedPrefEditor.commit();
            startActivity(new Intent(new Intent(ViewCars.this,LoginActivity.class)).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        }
        else if (id == R.id.exit)
        {
            finish();
        }
        return true;
    }
}
