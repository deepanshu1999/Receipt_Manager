package com.example.deepanshuaggarwal.receiptvault;

/**
 * Created by Deepanshu Aggarwal on 29-03-2018.
 */

public class Receipt {
    String rno;
    String category,date,seller,comment,imagepath,imagename;
    int warranty;
    String imagelink;

    public Receipt(String rno, String category, String date, String seller, String comment, int warranty,String imagepath,String imagename,String imagelink) {
        this.rno = rno;
        this.category = category;
        this.date = date;
        this.seller = seller;
        this.comment = comment;
        this.warranty = warranty;
        this.imagename=imagename;
        this.imagepath=imagepath;
        this.imagelink=imagelink;
    }
}
