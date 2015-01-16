package org.stofkat.battleround.server.uploader;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.BasicConfigurator;

public class ImageUploaderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String fileType = "png";
	private static final String mimeType = "image/png";
	
	public ImageUploaderServlet() {
		super();
		BasicConfigurator.configure();
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException {
		InputStream imageInputStream = null;
		ServletOutputStream outputStream = null;
		
		ServletFileUpload upload = new ServletFileUpload();
		
		try {
			FileItemIterator iterator = upload.getItemIterator(request);
			
			BufferedImage image = null;
			while (iterator.hasNext()) {
				FileItemStream item = iterator.next();
				
				String name = item.getFieldName();
				System.out.println(name);
				
				if (item.getContentType() != null && item.getContentType().equals(mimeType)) {
					imageInputStream = item.openStream();
					image = ImageIO.read(imageInputStream);
					break;
				}
	            
			} // We currently only support uploading one file at a time.
			
			if (image != null) {
//				response.setContentType(mimeType);
				outputStream = response.getOutputStream();
//				ImageIO.write(image, fileType, outputStream);
				outputStream.println(image.getHeight());
			}
		} catch (FileUploadException e) {
 			e.printStackTrace();
		} finally {
			if (imageInputStream != null) {
				imageInputStream.close();
			}
			if (outputStream != null) {
				outputStream.close();
			}
		}
	}
}
