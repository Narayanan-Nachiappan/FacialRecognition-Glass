package com.glass.wis;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.glass.wis.picretriev.PicRecognizer;
import com.glass.wis.picretriev.WisUtil;
import com.glass.wis.picretriev.stub.ImageRecogResponse;
import com.glass.wis.picretriev.stub.ImageUploadResponse;
import com.google.android.glass.app.Card;
import com.google.android.glass.app.Card.ImageLayout;
import com.google.android.glass.timeline.TimelineManager;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;
import com.google.gson.Gson;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

public class WisActivity extends Activity {
	private static final int TAKE_PICTURE_REQUEST = 1;
	private List<Card> mCards;
	private CardScrollView mCardScrollView;
	public static ImageUploadResponse resp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		takePicture();
	}

	private void takePicture() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, TAKE_PICTURE_REQUEST);
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == TAKE_PICTURE_REQUEST && resultCode == RESULT_OK) {
			String picturePath = data.getStringExtra("picture_file_path");
			processPictureWhenReady(picturePath);
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void processPictureWhenReady(final String picturePath) {
		final File pictureFile = new File(picturePath);

		if (pictureFile.exists()) {
			// The picture is ready; process it.
			processAndIdentifyPic(picturePath,pictureFile);
			//createSingleCardView(pictureFile);
		} else {
			// The file does not exist yet. Before starting the file observer,
			// you
			// can update your UI to let the user know that the application is
			// waiting for the picture (for example, by displaying the thumbnail
			// image and a progress indicator).

			final File parentDirectory = pictureFile.getParentFile();
			FileObserver observer = new FileObserver(parentDirectory.getPath()) {
				// Protect against additional pending events after CLOSE_WRITE
				// is
				// handled.
				private boolean isFileWritten;

				@Override
				public void onEvent(int event, String path) {
					if (!isFileWritten) {
						// For safety, make sure that the file that was created
						// in
						// the directory is actually the one that we're
						// expecting.
						File affectedFile = new File(parentDirectory, path);
						isFileWritten = (event == FileObserver.CLOSE_WRITE && affectedFile
								.equals(pictureFile));

						if (isFileWritten) {
							stopWatching();

							// Now that the file is ready, recursively call
							// processPictureWhenReady again (on the UI thread).
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									processPictureWhenReady(picturePath);
								}
							});
						}
					}
				}
			};
			observer.startWatching();
		}
	}

	public void processAndIdentifyPic(String picturePath,File pictureFile) {
		// TODO Auto-generated method stub
		// android.os.Debug.waitForDebugger();

		 if (android.os.Build.VERSION.SDK_INT > 9) {
		      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		      StrictMode.setThreadPolicy(policy);
		    }
		 createInterCardView(pictureFile);

		 UploadPicTAsk task = new UploadPicTAsk(this);
		 task.doInBackground(new String[]{PicRecognizer.END_POINT_PIC_UPLOAD,pictureFile.getPath()});
//		 ImageUploadResponse resp = WisUtil.UploadImage(PicRecognizer.END_POINT_PIC_UPLOAD,pictureFile);
//		System.out.println(resp);
//		String imageId = resp.getImageid();
//		//System.err.println("Image Uploaded and Id is :" + imageId);
//		if(Integer.valueOf((resp.getNfaces()))>0){
//			//System.err.println("NUmber of identified " + resp.getNfaces());
//			ImageRecogResponse imageRecogResponse = WisUtil.RecognizeImage(PicRecognizer.END_POINT_PIC_IDENTIFY,imageId);
//			//System.err.println(imageRecogResponse.getResult());
//			createScrollCardsView(imageRecogResponse,resp);
//		}
//		else{
//			createSingleCardView();
//		}
	}
	
	public void processImageUploadResp(ImageUploadResponse resp){
		System.out.println("$$$Process Image Upload Resp$$$$");
		String imageId = resp.getImageid();
		//System.err.println("Image Uploaded and Id is :" + imageId);
		if(Integer.valueOf((resp.getNfaces()))>0){
			//System.err.println("NUmber of identified " + resp.getNfaces());
			ImageRecogResponse imageRecogResponse = WisUtil.RecognizeImage(PicRecognizer.END_POINT_PIC_IDENTIFY,imageId);
			//System.err.println(imageRecogResponse.getResult());
			createScrollCardsView(imageRecogResponse,resp);
		}
		else{
			createSingleCardView();
		}
	}

	private void createSingleCardView() {
		// TODO Auto-generated method stub
		// Create a card with a full-screen background image.
		
		Card card = new Card(WISApp.getAppContext());
		card.setText("Sorry!!!Coulnt detect faces from the picture. Please take a clearer pic");
		card.setFootnote("Couldnt find");
	//	card.setFullScreenImages(true);
		//card.addImage(Uri.fromFile(picFile));
		// Don't call this you're using TimelineManager
		View card2View = card.toView();
		setContentView(card2View);

	}
	
	private void createInterCardView(File picFile) {
		// TODO Auto-generated method stub
		// Create a card with a full-screen background image.
		Card card = new Card(WISApp.getAppContext());
		card.setText("Processing!!!!! YOur image is being processed");
		card.setFootnote("Please Wait....");
		card.setImageLayout(ImageLayout.LEFT);
		card.addImage(Uri.fromFile(picFile));
		// Don't call this you're using TimelineManager
		TimelineManager.from(getApplicationContext()).insert(card);
		setContentView(card.toView());
		mCards = new ArrayList<Card>();
		mCards.add(card);
		mCardScrollView = new CardScrollView(this);
		ExampleCardScrollAdapter adapter = new ExampleCardScrollAdapter();
		mCardScrollView.setAdapter(adapter);
		mCardScrollView.activate();
		setContentView(mCardScrollView);

	}


	private void createScrollCardsView(ImageRecogResponse imgRecogResp,ImageUploadResponse imgUploadResp) {
		mCards = new ArrayList<Card>();

		Card card;

		card = new Card(WISApp.getAppContext());
		String gender="Feamle";
		if(imgRecogResp.getGender().get(0).equals("M")){
			gender="Male";
		}
		card.setText("The Person is deteceted to be "+ gender + " with " +imgRecogResp.getGender().get(1) +" confidence and age is deteceted to be " + imgRecogResp.getAge() );
		card.setFootnote("Scroll through for people matching the picture");
		card.setImageLayout(ImageLayout.LEFT);
		card.addImage(Uri.fromFile(WisUtil.getImageFromUrl(PicRecognizer.END_POINT_THUMBNAIL+imgUploadResp.getImageid(),imgUploadResp.getImageid())));
		
		TimelineManager.from(getApplicationContext()).insert(card);
		mCards.add(card);
		Iterator<List<String>> iter = imgRecogResp.getAttrs().iterator();
		while(iter.hasNext()){
			List<String> person = iter.next();
			card = new Card(WISApp.getAppContext());
			card.setText(person.get(2));
			card.setFootnote("Confidence " + person.get(1));
			card.setImageLayout(ImageLayout.LEFT);			card.addImage(Uri.fromFile((WisUtil.getImageFromUrl(PicRecognizer.END_POINT_PIC_UPLOAD+person.get(3),person.get(3)))));
			TimelineManager.from(getApplicationContext()).insert(card);
			mCards.add(card);

		}
		mCardScrollView = new CardScrollView(this);
		ExampleCardScrollAdapter adapter = new ExampleCardScrollAdapter();
		mCardScrollView.setAdapter(adapter);
		mCardScrollView.activate();
		setContentView(mCardScrollView);
		

	}

	private class ExampleCardScrollAdapter extends CardScrollAdapter {
		@Override
		public int findIdPosition(Object id) {
			return -1;
		}

		@Override
		public int findItemPosition(Object item) {
			return mCards.indexOf(item);
		}

		@Override
		public int getCount() {
			return mCards.size();
		}

		@Override
		public Object getItem(int position) {
			return mCards.get(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return mCards.get(position).toView();
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_CAMERA) {
			// Stop the preview and release the camera.
			// Execute your logic as quickly as possible
			// so the capture happens quickly.

			// Return true where this activity will consume this event and your
			// app will not be
			// interrupted
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Re-acquire the camera and start the preview.
	}
	
	
	 class UploadPicTAsk extends AsyncTask<String, Void, String> {
	    WisActivity wisActivity;
		 public UploadPicTAsk(WisActivity wisActivity){
	    	this.wisActivity = wisActivity;
	    }
		 @Override
	    protected String doInBackground(String... urls) {
			 String response = WisUtil.UploadImageStr(urls[0],new File(urls[1]));
			 ImageUploadResponse resp = new Gson().fromJson(response,ImageUploadResponse.class);
		    	wisActivity.processImageUploadResp(resp);
			 return WisUtil.UploadImageStr(urls[0],new File(urls[1]));
			 //return new Gson().toJson(ImageUploadResponse.class);
	    }

	    @Override
	    protected void onPostExecute(String result) {
	    	ImageUploadResponse resp = new Gson().fromJson(result,ImageUploadResponse.class);
	    	wisActivity.processImageUploadResp(resp);
	    }
	  }
}
