package com.builtbymoby.anode;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;

import ch.boye.httpclientandroidlib.entity.mime.content.ContentBody;

import android.net.Uri;
import android.text.TextUtils;

public class AnodeFile implements ContentBody {
	private String urlString;
	private String filename;
	private Boolean loaded = false;
	private byte[] data;
	
	// TODO: file data storage
	
	public AnodeFile(String filename, byte[] data) {
		this.filename = filename;
		this.data = data;
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
	
	// TODO: remote asset loading and image representation
	
	public Boolean isLoaded() {
		return this.loaded;
	}
	
	public byte[] getData() {
		return this.data;
	}
	
	// Content Body
	
	public String getFilename() {
		return this.filename;
	}
	
	public void writeTo(OutputStream out) throws IOException {
		out.write(data);
	}

	@Override
	public String getCharset() {		
		return null;
	}

	@Override
	public long getContentLength() {
		return this.data.length;
	}
	
	// TODO: auto-detect supported types

	@Override
	public String getMediaType() {
		return "image";
	}

	@Override
	public String getMimeType() {
		return getMediaType() + "/" + getSubType(); 
	}

	@Override
	public String getSubType() {
		return "png";
	}

	@Override
	public String getTransferEncoding() {
		return "7bit";
	}	
}
