package com.example.campuscrave;

public class DataClass {

    private boolean isVeg;
    private String dataName;
    private String dataDesc;
    private String dataAlgn;
    private String dataImage;
    private String key;
    private String foodType;

    private int vote;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDataName() {
        return dataName;
    }

    public String getDataDesc() {
        return dataDesc;
    }

    public String getDataAlgn() {
        return dataAlgn;
    }

    public String getDataImage() {
        return dataImage;
    }

    public String getFoodType() {
        return foodType;
    }

    public boolean isVeg() {return isVeg;}

    public void setVeg(boolean veg) {isVeg = veg;}

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    public int getVote() {
        return vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }

    public DataClass(String dataName, String dataDesc, String dataAlgn, String dataImage, String foodType, int vote) {
        this.dataName = dataName;
        this.dataDesc = dataDesc;
        this.dataAlgn = dataAlgn;
        this.dataImage = dataImage;
        this.foodType = foodType;
        this.isVeg = isVeg;
        this.vote = vote;
    }

    public DataClass() {
    }
}


//package com.example.campuscrave;
//
//public class DataClass {
//
//    private String dataName;
//    private String dataDesc;
//    private String dataAlgn;
//    private String dataImage;
//    private String key;
//    private String foodType;
//
//    public String getKey() {
//        return key;
//    }
//
//    public void setKey(String key) {
//        this.key = key;
//    }
//
//    public String getDataName() {
//        return dataName;
//    }
//
//    public String getDataDesc() {
//        return dataDesc;
//    }
//
//    public String getDataAlgn() {
//        return dataAlgn;
//    }
//
//    public String getDataImage() {
//        return dataImage;
//    }
//
//    public String getFoodType() {
//        return foodType;
//    }
//
//    public void setFoodType(String foodType) {
//        this.foodType = foodType;
//    }
//
//    public DataClass(String dataName, String dataDesc, String dataAlgn, String dataImage, String foodType) {
//        this.dataName = dataName;
//        this.dataDesc = dataDesc;
//        this.dataAlgn = dataAlgn;
//        this.dataImage = dataImage;
//        this.foodType = foodType;
//    }
//
//    public DataClass() {
//    }
//}


