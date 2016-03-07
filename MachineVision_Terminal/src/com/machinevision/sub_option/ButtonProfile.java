package com.machinevision.sub_option;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;

import org.json.JSONException;
import org.json.JSONObject;

import com.machinevision.terminal.FileDirectory;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract.DeletedContacts;
import android.text.TextUtils;
import android.util.Log;

/**
 * 钮扣配置文件类
 * @author hanyu
 * 
 */
public class ButtonProfile {	
	private static String defaultPath = FileDirectory.getLeaningDirectory();
	private String id;
	private String filePath;

	public ButtonProfile(String _id) {
		// TODO Auto-generated constructor stub
		id = _id;
		filePath = defaultPath + id ;
	}

	public ButtonProfile(String _filepath, String _id) {
		// TODO Auto-generated constructor stub
		filePath = _filepath;
		id = _id;
	}
	
	public ButtonProfile(File file) {
		// TODO Auto-generated constructor stub
		id = file.getName();
		filePath = file.getAbsolutePath();
	}

	public File getFile() {
		return new File(filePath);
	}
	
	private String getImgPath() {
		return filePath + "/" + id + ".jpg";
	}

	private String getIniFilePath() {
		return filePath + "/" + id + ".ini";
	}

	/**
	 * 保存配置文件到sd卡
	 * 
	 * @param json
	 * @param ButtonID
	 */
	public void write2sd(JSONObject json) {
		try {				
			File file = new File(getIniFilePath());
			if (!file.getParentFile().exists())
			{
				file.getParentFile().mkdirs();
			}
			FileWriter writer = new FileWriter(file);
			SimpleDateFormat myFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date now = new Date(System.currentTimeMillis());
			try
			{
				json.put("time", myFmt.format(now).toString());
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			writer.write(json.toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	};

	/**
	 * 保存图片到sd卡上
	 * 
	 * @param mBitmap
	 */
	private void saveMyBitmap(Bitmap mBitmap) {
		File f = new File(getImgPath());
		if (!f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		}

		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveInfo(String ID, JSONObject json ,Bitmap mBitmap) 
	{
		ButtonProfile buttonProfile = new ButtonProfile(ID);
		buttonProfile.write2sd(json);
		buttonProfile.saveMyBitmap(mBitmap);
	}
	
	public String getID()
	{
		return id;
	}

	/**
	 * 从sd卡上获取配置文件，返回json对象
	 * 
	 * @return
	 */
	public JSONObject getJsonStr() 
	{
		JSONObject json = null;
		try {
			String str = null;
			StringBuffer strBuf = new StringBuffer();
			BufferedReader reader = new BufferedReader(new FileReader(new File(getIniFilePath())));
			while (!TextUtils.isEmpty(str = reader.readLine())) {
				strBuf.append(str);
			}
			json = new JSONObject(strBuf.toString());
		} catch (Exception e) {
			// TODO: handle exception
		}
		return json;
	}

	/**
	 * 从sd卡上获取图片文件
	 * @return
	 */
	public Bitmap getBitmap() {
		Bitmap bitmap = null;
		try {
			InputStream inputStream = new FileInputStream(new File(getImgPath()));
			bitmap = BitmapFactory.decodeStream(inputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return bitmap;
	}
	
	 /**
	  * id查询
	  * @param _id
	  * @return
	  */
	boolean hasName(String _id)
	{
		return id.contains(_id);		
	}
	
	/**
	 * 条件查询
	 * @param json
	 * @param keys
	 * @param values
	 * @return
	 * @throws JSONException 
	 */
	boolean checkCondition(String[] keys, StringBuilder[] values) throws JSONException {
		JSONObject json = getJsonStr();
		for (int i = 0; i < values.length; i++) 
		{
			if (json.has(keys[i])) 
			{
				if (!json.getString(keys[i]).equals(values[i].toString())) 
				{
					return false;
				}
			}
		}
		return true;
	}
	
	public void Delete()
	{
		FileManager_fileExplorer.deleteDirectory(new File(filePath + "/"));
	}
}
