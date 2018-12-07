package app.carhire.com.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import app.carhire.com.BuildConfig;
import app.carhire.com.R;

import app.carhire.com.R;

import static java.lang.Boolean.FALSE;

public class PostCar extends AppCompatActivity {

    private ImageView displayImage;
    private TextView pickImage;
    private EditText carModel, carMake, fuelType, engineSize, carTransmission, carCapacity, carAccessories, hireRate;
    private Uri imageUri;
    private String car_model, car_make, fuel_type, engine_size, car_transmission, car_capacity, car_accessories, hire_rate;
    private Button submit;
    private DatabaseReference rootDbRef;
    private StorageReference rootStgRef;
    private ProgressDialog loadPostCar;
    private String booked;
    private SharedPreferences sharedPreferences;
    private String prefFile = BuildConfig.APPLICATION_ID + ".PREFERENCE_FILE_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_car);

        //set up toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initialize variables
        displayImage = (ImageView)findViewById(R.id.display_image);
        pickImage = (TextView)findViewById(R.id.pick_image);
        carMake = (EditText)findViewById(R.id.car_make);
        carModel = (EditText)findViewById(R.id.car_model);
        fuelType = (EditText)findViewById(R.id.fuel_type);
        engineSize = (EditText)findViewById(R.id.engine_size);
        carTransmission = (EditText)findViewById(R.id.car_transmission);
        carCapacity = (EditText)findViewById(R.id.car_capacity);
        carAccessories = (EditText)findViewById(R.id.car_accessories);
        hireRate = (EditText)findViewById(R.id.hire_rate);
        submit = (Button)findViewById(R.id.submit);
        loadPostCar = new ProgressDialog(this);
        rootDbRef = FirebaseDatabase.getInstance().getReference();
        rootStgRef = FirebaseStorage.getInstance().getReference();
        sharedPreferences = getSharedPreferences(prefFile, Context.MODE_PRIVATE);

        //set booking information
        booked = "available";

        //handle item clicks

        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitCarImage();
            }
        });

        //update ui
        pickImage.setPaintFlags(pickImage.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

    }

    //post car image to firebase storage
    private void submitCarImage() {

        if (validate())
        {
            loadPostCar.setMessage("Uploading image...");
            loadPostCar.setCancelable(false);
            loadPostCar.show();

            final String car_id = getCarId();
            final String[] image_url = {""};

            final StorageReference imgStrgRef = rootStgRef.child("cars").child(car_id).child(imageUri.getLastPathSegment());
            UploadTask uploadTask = imgStrgRef.putFile(imageUri);

            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful())
                    {throw task.getException();}
                    return imgStrgRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful())
                    {
                        Uri uri = task.getResult();
                        image_url[0] = uri.toString();
                        postToDatabase(car_id, image_url[0]);
                    }
                    else
                    {
                        loadPostCar.dismiss();
                        showDialog(car_id);
                    }
                }
            });

        }

    }

    //show dialog to user to choose whether to
    //continue with an image or not
    private void showDialog(final String car_id) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Sorry, we couldn't upload your image\nProceed without one and add later?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               postToDatabase(car_id,"No Image");
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               submitCarImage();
            }
        }).show();
    }

    //post car details to firebase database
    private void postToDatabase(String car_id, String image_url) {
        loadPostCar.setMessage("Uploading car details...");
        if (!loadPostCar.isShowing())
        {
            loadPostCar.show();
        }

        Map<String,String> data = new HashMap<String,String>();

        data.put("car_id",car_id);
        data.put("image_url",image_url);
        data.put("car_make",car_make);
        data.put("car_model",car_model);
        data.put("owner_id",sharedPreferences.getString("UserId", ""));
        data.put("car_owner",sharedPreferences.getString("UserName",""));
        data.put("engine_size",engine_size);
        data.put("fuel_type",fuel_type);
        data.put("car_transmission",car_transmission);
        data.put("car_capacity",car_capacity);
        data.put("car_accessories",car_accessories);
        data.put("car_rating","No rating");
        data.put("hire_rate",hire_rate);
        data.put("booked", booked);

        rootDbRef.child("cars").child(car_id).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    loadPostCar.dismiss();
                    Toast.makeText(PostCar.this, "Successfully uploaded", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(PostCar.this,ViewCars.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                }
                else
                {
                    loadPostCar.dismiss();
                    Toast.makeText(PostCar.this, "Couldn't upload details.\nCheck internet connection and try later", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //push request to firebase database to get key to use as car id
    private String getCarId(){
        String key = "";
        key = rootDbRef.child("cars").push().getKey();
        return key;
    }

    //validate inputs
    private boolean validate(){

        car_model = carModel.getText().toString();
        car_make = carMake.getText().toString();
        fuel_type = fuelType.getText().toString();
        engine_size = engineSize.getText().toString();
        car_transmission = carTransmission.getText().toString();
        car_capacity = carCapacity.getText().toString();
        car_accessories = carAccessories.getText().toString();
        hire_rate = hireRate.getText().toString();

        if(imageUri == null)
        {
            Toast.makeText(this, "Pick an image for the car", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (car_model.isEmpty())
        {
            Toast.makeText(this, "Enter car model", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (car_make.isEmpty())
        {
            Toast.makeText(this, "Enter car make", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (fuel_type.isEmpty())
        {
            Toast.makeText(this, "Enter car fuel type", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (engine_size.isEmpty())
        {
            Toast.makeText(this, "Enter engine size", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (car_transmission.isEmpty())
        {
            Toast.makeText(this, "Enter car transmission", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (car_capacity.isEmpty())
        {
            Toast.makeText(this, "Enter car capacity", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (car_accessories.isEmpty())
        {
            Toast.makeText(this, "Enter car accessories", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (hire_rate.isEmpty())
        {
            Toast.makeText(this, "Enter hiring rate", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    //launch pick image intent
    private void pickImage(){

        int read_permission = ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_EXTERNAL_STORAGE);
        if (read_permission != PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(this, "Grant Permissions first", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
        }
        else
        {
            Intent pick = new Intent(Intent.ACTION_GET_CONTENT);
            pick.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
            startActivityForResult(Intent.createChooser(pick,"Proceed with : "),1);
        }

    }

    //get results after permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            pickImage();
        }
        else
        {
            Toast.makeText(this, "Grant Permissions first", Toast.LENGTH_SHORT).show();
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //get results after activity for result returns results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null)
        {
            imageUri = data.getData();
            pickImage.setVisibility(View.GONE);
            displayImage.setImageURI(imageUri);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    //handle item clicks on options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home)
        {
            finish();
        }

        return  true;
    }

}
