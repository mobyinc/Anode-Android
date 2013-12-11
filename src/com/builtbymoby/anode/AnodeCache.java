package com.builtbymoby.anode;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.builtbymoby.anode.utility.Utils;
import com.jakewharton.disklrucache.DiskLruCache;

public class AnodeCache {
	private static final String TAG = "AnodeCache";
	private static final int CACHE_VERSION = 1;
	private static final int VALUE_COUNT = 1;
	private static final int IO_BUFFER_SIZE = 8 * 1024;
	
	private static AnodeCache instance = null;
	
	private DiskLruCache diskCache = null;
	
	public static void initialize(Context context, String uniqueName, long maxSizeBytes) {
		try {
			AnodeCache.instance = new AnodeCache(context, uniqueName, maxSizeBytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static AnodeCache getInstance() {
		if (AnodeCache.instance == null) {
			throw new AnodeException(AnodeException.CACHE_NOT_INITIALIZED, "Attepted to use cache before it was initialized");
		}
		
		return AnodeCache.instance;
	}
	
	public boolean putObject(String key, Serializable object) {
		DiskLruCache.Editor editor = null;
		
		try {
			editor = this.diskCache.edit(key);
			
			if (editor == null) {
				return false;
			}
			
			if (writeObjectToFile(object, editor)) {
				this.diskCache.flush();
				editor.commit();
			} else {
				editor.abort();
				return false;
			}
		} catch (Exception e) {
			Log.e(TAG, "Failed to add object to cache", e);
			try {
                if (editor != null) {
                    editor.abort();
                }
            } catch (IOException ignored) {} 
			
			return false;
		}
		
		return true;
	}
	
	public Object getObject(String key) {
		DiskLruCache.Snapshot snapshot = null;
		
		try {
			snapshot = this.diskCache.get(key);
			return readObjectFromFile(snapshot);
		} catch (Exception e) {
			Log.e(TAG, "Failed to get object from cache", e);
			return null;
		} finally {
			if (snapshot != null) {
				snapshot.close();
			}
		}		
	}
	
	public void clearCache() {
		try {
			this.diskCache.delete();
		} catch (IOException ignored) {}
	}
	
	/*
	 * Private
	 */
	
	private AnodeCache(Context context, String uniqueName, long maxSizeBytes) throws IOException {
		final File cacheDirectory = getDiskCacheDir(context, uniqueName);
		this.diskCache = DiskLruCache.open(cacheDirectory, CACHE_VERSION, VALUE_COUNT, maxSizeBytes);
	}
	
	private File getDiskCacheDir(Context context, String uniqueName) {
		final String cachePath =
				Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
	            !Utils.isExternalStorageRemovable() ?
	            Utils.getExternalCacheDir(context).getPath() :
	            	context.getCacheDir().getPath();

	    return new File(cachePath + File.separator + uniqueName);
	}
	
	private boolean writeObjectToFile(Serializable object, DiskLruCache.Editor editor) {
		boolean success = true;
		OutputStream buffer = null;
        ObjectOutputStream objectStream = null;        
        
        try {        	
        	buffer = new BufferedOutputStream(editor.newOutputStream(0), IO_BUFFER_SIZE);
            objectStream = new ObjectOutputStream(buffer);
            objectStream.writeObject(object);
        } catch (Exception e) {
        	Log.e(TAG, "Failed to write cache object", e);
        	success = false;
        } finally {
        	if (buffer != null) {
            	try {
					buffer.close();
				} catch (IOException e) {}
            }
        	
        	if (objectStream != null) {
            	try {
            		objectStream.close();
				} catch (IOException ignored) {}
            }
        }
        
        return success;
    }
	
	private Object readObjectFromFile(DiskLruCache.Snapshot snapshot) {
		if (snapshot == null) {
			return null;
		}
		
		try {
			final BufferedInputStream buffer = new BufferedInputStream(snapshot.getInputStream(0), IO_BUFFER_SIZE);
			ObjectInputStream objectStream = new ObjectInputStream(buffer);
			return objectStream.readObject();
		} catch (Exception e) {
			Log.e(TAG, "Failed to read object snapshot from cache", e);
			return null;
		}
	}
}




