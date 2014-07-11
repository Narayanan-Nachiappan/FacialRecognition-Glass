package com.glass.wis.picretriev;

import java.io.File;

public class PicRecognizer {

	public static final String END_POINT_PIC_UPLOAD = "http://www.pictriev.com/facedbj.php?findface&image=post";

	public static final String END_POINT_PIC_IDENTIFY = "http://www.pictriev.com/facedbj.php?whoissim&faceid=0&lang=en&imageid=";
	
	public static final String END_POINT_PIC_DOWNLOAD = "http://www.pictriev.com/imgj.php?facex=";
	
	public static final String END_POINT_THUMBNAIL = "http://www.pictriev.com/imgj.php?facethumbnail&faceid=0&imageid=";

	public void Recognize(File picFile) {
		String picId = getPicIdAfterUpload(picFile);
	}

	public String getPicIdAfterUpload(File picFile) {
		return null;
	}
}
