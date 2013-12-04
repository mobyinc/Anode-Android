package com.builtbymoby.anode;

import java.net.URI;
import java.net.URL;

import android.net.Uri;
import android.text.TextUtils;

public class AnodeFile {
	private String urlString;
	private String fileName;
	private Boolean loaded = false;
	
	// TODO: file data storage
	
	public AnodeFile(String fileName, Object data) {
		
	}
	
	public AnodeFile(String urlString) {
		this.urlString = urlString;
	}
	
	public URL getUrl() {
		try {
			String uriString = urlString;
			
			URI uri = new URI(uriString);
			URI baseUri = new URI(Anode.getBaseUrl());
			
			if (TextUtils.isEmpty(uri.getHost())) {
				Uri.Builder builder = new Uri.Builder();
				builder.scheme(baseUri.getScheme()).authority(baseUri.getAuthority()).path(uri.getPath());
				uriString = builder.build().toString();
			}
			
			return new URL(uriString);
			
		} catch (Exception e) {
			throw new AnodeException(AnodeException.INVALID_BASE_URL, "invalid file url " + urlString);
		}
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public Boolean isLoaded() {
		return loaded;
	}
	
	// TODO: remote asset loading and image representation
}
