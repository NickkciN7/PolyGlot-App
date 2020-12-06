package com.example.polyglot;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;



public class imageFileMethods {

    //2 files "profilePictureBase64" and "partnerPicture"
    //serve to catch exceptions
    public void write(Context context, String imageData, String file){
        try {
            writeToInternalFile(context, imageData, file);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

    }
    public String read(Context context, String file){
        try {
            return readFromInternalFile(context, file);
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return "failedToRead";
        }
    }

    //from chapter 6 zybooks
    private void writeToInternalFile(Context context, String imageData, String file) throws IOException {
        FileOutputStream outputStream = context.openFileOutput(file, Context.MODE_PRIVATE);   ///openFileOuput needs context
        PrintWriter writer = new PrintWriter(outputStream);
        writer.println(imageData); //store base 64 image
        writer.close();
    }

    //testing that base64 image was stored
    private String readFromInternalFile(Context context, String file) throws IOException {
        FileInputStream inputStream = context.openFileInput(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line); //got rid of \n
            }
        }
        finally {
            reader.close();
        }
        return stringBuilder.toString();
    }


    public Bitmap decodeBase64(String imageData){
        //Log.d("in decode string", imageData);
        byte[] imageAsBytes = Base64.decode(imageData.getBytes(), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
        return bitmap;
    }

    public String imageToString(Bitmap bitmap){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, outputStream);
        byte[] byteholder = outputStream.toByteArray();
        String encodedImage = Base64.encodeToString(byteholder, Base64.DEFAULT);
        return encodedImage;
    }

}
