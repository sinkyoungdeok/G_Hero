package com.aspsine.fragmentnavigator.demo.item;

import android.widget.ImageView;

public class emoticonListviewitem {
    private int icon1;
    private int icon2;
    private int icon3;
    private ImageView imgview;
    public int getIcon1() {
        return icon1;
    }
    public int getIcon2() {
        return icon2;
    }
    public int getIcon3() {
        return icon3;
    }
    public ImageView getImgview() { return imgview; }



    public emoticonListviewitem(int icon1, int icon2, int icon3, ImageView imgview) {
        this.icon1 = icon1;
        this.icon2 = icon2;
        this.icon3 = icon3;
        this.imgview = imgview;
    }
}
