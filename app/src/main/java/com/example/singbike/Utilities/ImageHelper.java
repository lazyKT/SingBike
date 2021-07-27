package com.example.singbike.Utilities;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

public class ImageHelper {

    /* get full file path from the image uri */
    public static String getFilePath (Context context, Uri imageUri) {

        Cursor cursor = null;
        Uri contentUri;
        final String documentID = DocumentsContract.getDocumentId (imageUri);
        final String type = documentID.split(":")[0];
        final String selection = "_id=?";
        final String[] selectionArgs = new String[] { documentID.split(":")[1] };
        final String column = "_data";
        final String[] projections = new String[] { column };

        if (type.equals("image")) {
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            try {
                cursor = context.getContentResolver().query (contentUri, projections, selection, selectionArgs, null);
                if (cursor != null && cursor.moveToFirst())
                    return cursor.getString (cursor.getColumnIndex(column));
            }
            finally {
                if (cursor != null)
                    cursor.close();
            }

        }

        return null;
    }

}
