package com.lakeba.filechooser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Test extends Activity implements OnItemClickListener {
    Cursor cursor;
    String thumbpath;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_list);

        GridView listView = (GridView) this.findViewById(R.id.ListView);

        String[] thumbColumns = {MediaStore.Video.Thumbnails.DATA,
                MediaStore.Video.Thumbnails.VIDEO_ID};

        String[] mediaColumns = {MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA, MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.MIME_TYPE,MediaStore.Video.Media.DURATION};

        cursor = managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                mediaColumns, null, null, null);

        ArrayList<VideoViewInfo> videoRows = new ArrayList<VideoViewInfo>();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {

                    VideoViewInfo newVVI = new VideoViewInfo();
                    int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                    Cursor thumbCursor = managedQuery(
                            MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                            thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID
                                    + "=" + id, null, null);
                    if (thumbCursor.moveToFirst()) {
                        newVVI.thumbPath = thumbCursor.getString(thumbCursor
                                .getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                        thumbpath = newVVI.thumbPath;
                        Log.d("Thum_path", newVVI.thumbPath);
                    }

                    newVVI.filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));



                    newVVI.title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                    Log.v("", newVVI.title);
                    newVVI.mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
                    newVVI.duration=cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                    int sec = newVVI.duration/1000%60;
                    if(sec<10) { sec = Integer.parseInt("0"+sec);
                    }
                    int min = newVVI.duration/(60 * 1000) % 60;
                    int hour =newVVI.duration/ (60 * 60 * 1000) %24;
                    newVVI.time_duration = hour +":"+min+":"+sec;
                    Log.v("", newVVI.mimeType);
                    videoRows.add(newVVI);
                } while (cursor.moveToNext());
            }
            listView.setAdapter(new VideoGalleryAdapter(this, videoRows));
            listView.setOnItemClickListener(this);
        }else {

            Toast.makeText(Test.this,"NO ITEM FOUND",Toast.LENGTH_SHORT).show();
        }
    }
    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        if (cursor.moveToPosition(position)) {
            int fileColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            int mimeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE);
            String videoFilePath = cursor.getString(fileColumn);
            String mimeType = cursor.getString(mimeColumn);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            File newFile = new File(videoFilePath);

//            String thumbpath_P =cursor.getString(cursor
//                    .getColumnIndex(MediaStore.Video.Thumbnails.DATA));
            Log.d("VIDEO_PATH", videoFilePath);


            intent.setDataAndType(Uri.fromFile(newFile), mimeType);

//            intent.putExtra("FILE_PATH", videoFilePath);
//            intent.putExtra("THUMB_PATH",thumbpath);
//            Log.d("THUMB_PATH2_CLICK", thumbpath_P);
            startActivity(intent);
        }
    }
}

class VideoViewInfo {
    String filePath;
    String mimeType;
    String thumbPath;
    String title;
    int duration;
    String time_duration;
}

class VideoGalleryAdapter extends BaseAdapter {
    private Context context;
    private List<VideoViewInfo> videoItems;

    LayoutInflater inflater;

    public VideoGalleryAdapter(Context _context,
                               ArrayList<VideoViewInfo> _items) {
        context = _context;
        videoItems = _items;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return videoItems.size();
    }

    public Object getItem(int position) {
        return videoItems.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View videoRow = inflater.inflate(R.layout.row, null);



        ImageView videoThumb = (ImageView) videoRow
                .findViewById(R.id.ImageView);
//        if (videoItems.get(position).thumbPath != null) {
//            videoThumb.setImageURI(Uri
//                    .parse(videoItems.get(position).thumbPath));
//        }

        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(videoItems.get(position).filePath, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
        videoThumb.setImageBitmap(bitmap);

        TextView videoTitle = (TextView) videoRow
                .findViewById(R.id.TextView);
        videoTitle.setText(videoItems.get(position).title);

        TextView time_duration =(TextView)videoRow.findViewById(R.id.duration);
        time_duration.setText(videoItems.get(position).time_duration);
        return videoRow;
    }
}