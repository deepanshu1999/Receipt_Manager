package com.example.deepanshuaggarwal.receiptvault;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;



/**
 * Created by Deepanshu Aggarwal on 29-03-2018.
 */

public class ToFireBase extends AsyncTask<String,Void,String> {
    @Override
    protected String doInBackground(String... strings) {
        StorageReference storageReference= FirebaseStorage.getInstance().getReference();
        Uri file=Uri.parse(strings[0]);
        globals.check="False";

        String FolderName=MainActivity.Useremail;

        StorageReference riversRef = storageReference.child(FolderName+"/"+strings[1]);
        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        globals.downloadUri= taskSnapshot.getDownloadUrl();
                        globals.check="True";
                        Log.i("FTag",globals.downloadUri.toString());



                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.i("Tag","Failed");
                        globals.check="False";
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
        if(globals.check=="False")
      return "Failed";
        else
            return globals.downloadUri.toString();
    }
}
