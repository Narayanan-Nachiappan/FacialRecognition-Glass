package com.glass.wis.picretriev;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.GzipCompressingEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.TextView;

import com.glass.wis.picretriev.stub.ImageRecogResponse;
import com.glass.wis.picretriev.stub.ImageUploadResponse;
import com.google.gson.Gson;
@SuppressWarnings("deprecation")
public class WisUtil {
	
	static String headers[]={"","Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:25.0) Gecko/20100101 Firefox/25.0",
					  "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1664.3 Safari/537.36",
					  "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/537.13+ (KHTML, like Gecko) Version/5.1.7 Safari/534.57.2",
					  "Opera/9.80 (Macintosh; Intel Mac OS X 10.6.8; U; fr) Presto/2.9.168 Version/11.52"};
	static int min = 1;
	static int max = 4;
	
	public static String get(String url){
		return null;
	}

	@SuppressWarnings("finally")
	public static String post(String url,File picFile){
		String str = null;
		
		HttpClient httpclient = new DefaultHttpClient();
	   // httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
	    double index = (min+(Math.random() * (max-min)));
	    System.out.println(headers[(int)(index)]);
	    httpclient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, headers[(int) index]);//"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:25.0) Gecko/20100101 Firefox/25.0");
	    httpclient.getParams().setParameter("Accept-Encoding","gzip, deflate");
	    httpclient.getParams().setParameter("Content-Type", "multipart/form-data");
	    HttpPost httppost = new HttpPost(url);

		MultipartEntity mpEntity = new MultipartEntity();
	   
		// ContentBody cbFile = new FileBody(picFile, "image/jpeg");
		//String outputFile = (picFile.getPath().split("\\.")[0]+"x.jpg");
		//compressIt(picFile.getPath(), outputFile);//(picFile.getAbsolutePath(), outputFile);
		FileBody pic = new FileBody((picFile));
	    
	    mpEntity.addPart("photo", pic);
	    httppost.setEntity(mpEntity);
	    System.out.println("executing request " + httppost.getRequestLine());
	    System.out.println(httppost.getEntity().getContentType());
	    try {
			System.out.println(httppost.getEntity());
		} catch (IllegalStateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
	    HttpResponse response;
		try {
			response = httpclient.execute(httppost);
			HttpEntity resEntity = response.getEntity();

		    System.out.println(response.getStatusLine());
		    if (resEntity != null) {
		      str = EntityUtils.toString(resEntity);
		   
		
		    }
		    if (resEntity != null) {
		      resEntity.consumeContent();
		    }

		    httpclient.getConnectionManager().shutdown();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("###################");
			System.out.println(e.toString());
			System.out.println("###################");
			System.out.println(e.getStackTrace());
			System.out.println("###################");
			e.printStackTrace();
			System.exit(-1);
		}
	    
		finally{
			return str;
		}
	}
	
	public static void compressIt(String inputFile, String outputFile){
		try {
				Bitmap bmp = BitmapFactory.decodeFile(inputFile);
		       FileOutputStream out = new FileOutputStream(new File(outputFile));
		       bmp.compress(Bitmap.CompressFormat.PNG, 50, out); //100-best quality
		       out.close();
		} catch (Exception e) {
		       e.printStackTrace();
		}
	}
	
	public static void gzipIt(String inputFile,String outputFile ){
		 
	     byte[] buffer = new byte[1024];
	 
	     try{
	 
	    	GZIPOutputStream gzos = 
	    		new GZIPOutputStream(new FileOutputStream(outputFile));
	 
	        FileInputStream in = 
	            new FileInputStream(inputFile);
	 
	        int len;
	        while ((len = in.read(buffer)) > 0) {
	        	gzos.write(buffer, 0, len);
	        }
	 
	        in.close();
	 
	    	gzos.finish();
	    	gzos.close();
	 
	    	System.out.println("Done");
	 
	    }catch(IOException ex){
	       ex.printStackTrace();   
	    }
	   }
	
	public static String get(String Url, String imageId){
		String endpoint = Url+imageId;
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(endpoint);
		HttpResponse response = null;
		BufferedReader rd;
		StringBuffer textView = new StringBuffer();


		try {
			response = client.execute(request);
			rd = new BufferedReader
					  (new InputStreamReader(response.getEntity().getContent()));
			String line = "";
				while ((line = rd.readLine()) != null) {
				  textView.append(line);
				}
			
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Get the response
				    
		 
		return textView.toString();
	}
	
	public static void main(String a[]){
		
		ImageUploadResponse resp = UploadImage(PicRecognizer.END_POINT_PIC_UPLOAD,new File("/Users/nnarayan/Documents/vinay/vinay/pete.jpg"));
		String imageId = resp.getImageid();
		System.err.println("Image Uploaded and Id is :" + imageId);
		//if(Integer.getInteger(resp.getNfaces())>0){
			System.err.println("NUmber of identified " + resp.getNfaces());
			ImageRecogResponse imageRecogResponse = RecognizeImage(PicRecognizer.END_POINT_PIC_IDENTIFY,imageId);
			System.err.println(imageRecogResponse.getResult());
			
		//}else{
			//TODO handle 0 and more than one
		//}
		
		
		
	}

	public static ImageRecogResponse RecognizeImage(String endPointPicIdentify,
			String imageId) {
		// TODO Auto-generated method stub
		String resp = get(endPointPicIdentify,imageId);
		System.err.println("Image Identified and reponse is " + resp);
		return new Gson().fromJson(resp, ImageRecogResponse.class);
	}

	public static ImageUploadResponse UploadImage(String endpoint , File picFile) {
		// TODO Auto-generated method stub
		System.out.println(picFile.getAbsolutePath());
		String imageUploadResponse =  post(endpoint, picFile);
		System.err.println("Upload response " + imageUploadResponse);
		ImageUploadResponse resp = new Gson().fromJson(imageUploadResponse, ImageUploadResponse.class);
		return resp;
	}
	
	public static String UploadImageStr(String endpoint , File picFile) {
		// TODO Auto-generated method stub
		System.out.println(picFile.getAbsolutePath());
		String imageUploadResponse =  post(endpoint, picFile);
		System.err.println("Upload response " + imageUploadResponse);
		//ImageUploadResponse resp = new Gson().fromJson(imageUploadResponse, ImageUploadResponse.class);
		return imageUploadResponse;
	}
	
	public static File getImageFromUrl(String url,String name){
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		HttpResponse response = null;
		
		StringBuffer textView = new StringBuffer();

		File tempFile = null;
			try {
				response = client.execute(request);
				InputStream is =
						  ((response.getEntity().getContent()));
				tempFile = File.createTempFile(name, ".jpg");
				tempFile.deleteOnExit();
				FileOutputStream fout = new FileOutputStream(tempFile);
				IOUtils.copy(is, fout);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return tempFile;
		
	}
	
	public static void processAndIdentifyPic(String picturePath,File pictureFile) {
		// TODO Auto-generated method stub
		ImageUploadResponse resp = WisUtil.UploadImage(PicRecognizer.END_POINT_PIC_UPLOAD,pictureFile);
		System.out.println(resp);
		String imageId = resp.getImageid();
		//System.err.println("Image Uploaded and Id is :" + imageId);
		if(Integer.valueOf((resp.getNfaces()))>0){
			//System.err.println("NUmber of identified " + resp.getNfaces());
			ImageRecogResponse imageRecogResponse = WisUtil.RecognizeImage(PicRecognizer.END_POINT_PIC_IDENTIFY,imageId);
			//System.err.println(imageRecogResponse.getResult());
		//	createScrollCardsView(pictureFile,imageRecogResponse,resp);
		}
		else{
			//createSingleCardView(pictureFile);
		}
	}
}
