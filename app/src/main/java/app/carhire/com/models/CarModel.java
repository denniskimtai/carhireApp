package app.carhire.com.models;


public class CarModel {

    private String car_id;
    private String imageUrl;
    private String owner_id;
    private String carOwner;
    private String carMake;
    private String carModel;
    private String fuelType;
    private String engineSize;
    private String carTransmission;
    private String carCapacity;
    private String hireRate;
    private String carAccessories;
    private String carRating;
    private String booked;

    public CarModel() {

    }

    public CarModel(String car_id, String imageUrl, String owner_id, String carOwner, String carModel, String carMake,
                    String fuelType, String engineSize, String carTransmission, String carCapacity, String hireRate,
                    String carAccessories, String carRating) {
        this.car_id = car_id;
        this.imageUrl = imageUrl;
        this.owner_id = owner_id;
        this.carOwner = carOwner;
        this.carModel = carModel;
        this.carMake = carMake;
        this.fuelType = fuelType;
        this.engineSize = engineSize;
        this.carTransmission = carTransmission;
        this.carCapacity = carCapacity;
        this.hireRate = hireRate;
        this.carAccessories = carAccessories;
        this.carRating = carRating;
        this.setBooked("available");
    }


    public void setCarId(String car_id) {
        this.car_id = car_id;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setOwnerId(String owner_id) {
        this.owner_id = owner_id;
    }

    public void setCarOwner(String carOwner) {
        this.carOwner = carOwner;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public void setCarMake(String carMake) {
        this.carMake = carMake;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public void setEngineSize(String engineSize) {
        this.engineSize = engineSize;
    }

    public void setCarTransmission(String carTransmission) {
        this.carTransmission = carTransmission;
    }

    public void setCarCapacity(String carCapacity) {
        this.carCapacity = carCapacity;
    }

    public void setHireRate(String hireRate) {
        this.hireRate = hireRate;
    }

    public void setCarAccessories(String carAccessories) {
        this.carAccessories = carAccessories;
    }

    public void setBooked(String booked) { this.booked = booked; }

    public void setCarRating(String carRating) {
        this.carRating = carRating;
    }

    public String getCarId() {
        return car_id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getOwnerId() {
        return owner_id;
    }

    public String getCarOwner() {
        return carOwner;
    }

    public String getCarModel() {
        return carModel;
    }

    public String getCarMake() {
        return carMake;
    }

    public String getFuelType() {
        return fuelType;
    }

    public String getEngineSize() {
        return engineSize;
    }

    public String getCarTransmission() {
        return carTransmission;
    }

    public String getCarCapacity() {
        return carCapacity;
    }

    public String getHireRate() {
        return hireRate;
    }

    public String getCarAccessories() {
        return carAccessories;
    }

    public String getCarRating() {
        return carRating;
    }

    public String getBooked(){ return this.booked; }
}
