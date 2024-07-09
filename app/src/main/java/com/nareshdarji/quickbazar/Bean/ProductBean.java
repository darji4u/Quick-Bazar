package com.nareshdarji.quickbazar.Bean;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class ProductBean implements Parcelable {

    String productUid;
    String dealerUid;
    String name;
    String description;
    String price;
    String category;
    String imgUrl;
    String availability;
    List<Integer> colors;
    List<Integer> sizes;

    public ProductBean(String productUid, String dealerUid, String name, String description, String price, String category, String imgUrl, List<Integer> colors, List<Integer> sizes) {
        this.productUid = productUid;
        this.dealerUid = dealerUid;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imgUrl = imgUrl;
        this.colors = colors;
        this.sizes = sizes;
    }

    public ProductBean() {
    }



    protected ProductBean(Parcel in) {
        productUid = in.readString();
        dealerUid = in.readString();
        name = in.readString();
        description = in.readString();
        price = in.readString();
        category = in.readString();
        imgUrl = in.readString();
        colors = in.readArrayList(Integer.class.getClassLoader());
        sizes = in.readArrayList(Integer.class.getClassLoader());
        availability = in.readString();
    }

    public static final Creator<ProductBean> CREATOR = new Creator<ProductBean>() {
        @Override
        public ProductBean createFromParcel(Parcel in) {
            return new ProductBean(in);
        }

        @Override
        public ProductBean[] newArray(int size) {
            return new ProductBean[size];
        }
    };

    public String getProductUid() {
        return productUid;
    }

    public void setProductUid(String productUid) {
        this.productUid = productUid;
    }

    public String getDealerUid() {
        return dealerUid;
    }

    public void setDealerUid(String dealerUid) {
        this.dealerUid = dealerUid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public List<Integer> getColors() {
        return colors;
    }

    public void setColors(List<Integer> colors) {
        this.colors = colors;
    }

    public List<Integer> getSizes() {
        return sizes;
    }

    public void setSizes(List<Integer> sizes) {
        this.sizes = sizes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(productUid);
        parcel.writeString(dealerUid);
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeString(price);
        parcel.writeString(category);
        parcel.writeString(imgUrl);
        parcel.writeList(colors);
        parcel.writeList(sizes);
        parcel.writeString(availability);
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }
}
