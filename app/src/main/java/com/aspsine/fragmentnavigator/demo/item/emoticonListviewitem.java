package com.aspsine.fragmentnavigator.demo.item;

import android.widget.ImageView;

public class emoticonListviewitem {
    private int icon1;
    private int icon2;
    private int icon3;
    private String icon1Tag;
    private String icon2Tag;
    private String icon3Tag;
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
    public String getIcon1Tag() { return icon1Tag; }
    public String getIcon2Tag() { return icon2Tag; }
    public String getIcon3Tag() { return icon3Tag; }
    public ImageView getImgview() { return imgview; }



    public emoticonListviewitem(int icon1,String icon1Tag, int icon2,String icon2Tag, int icon3,String icon3Tag, ImageView imgview) {
        this.icon1 = icon1;
        this.icon2 = icon2;
        this.icon3 = icon3;
        this.icon1Tag = icon1Tag;
        this.icon2Tag = icon2Tag;
        this.icon3Tag = icon3Tag;
        this.imgview = imgview;
    }
}
