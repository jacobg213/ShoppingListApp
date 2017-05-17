package org.projects.shoppinglist;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
    String name;
    int quantity;

    public Product() {}

    public Product(String name, int quantity)
    {
        this.name = name;
        this.quantity = quantity;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

        @Override
    public String toString() {
        if(quantity == 0)
        {
            return name;
        }
        else
        {
            return name+" x"+quantity;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(quantity);
    }
    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public Product(Parcel in) {
        name = in.readString();
        quantity = in.readInt();
    }
}
