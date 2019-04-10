package com.corroborator.rest.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;

import com.corroborator.rest.payload.UploadFileResponse;
import com.corroborator.rest.service.FileStorageService;


@RestController
@CrossOrigin(origins = {"*",}, maxAge = 4800, allowCredentials = "false")
public class CustomerController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
	
	private final GcsService gcsService = GcsServiceFactory.createGcsService(
											new RetryParams.Builder()
								            .initialRetryDelayMillis(10)
								            .retryMaxAttempts(10)
								            .totalRetryPeriodMillis(15000)
								            .build());
	
	private static final int BUFFER_SIZE = 2 * 1024 * 1024;
	private final String bucket = "corroborator-ms-file";

	private String storeImage(Part filePart) throws IOException {

		String filename = uploadedFilename(filePart); // Extract filename
		GcsFileOptions.Builder builder = new GcsFileOptions.Builder();

		builder.acl("public-read"); // Set the file to be publicly viewable
		GcsFileOptions instance = GcsFileOptions.getDefaultInstance();
		GcsOutputChannel outputChannel;
		GcsFilename gcsFile = new GcsFilename(bucket, filename);
		outputChannel = gcsService.createOrReplace(gcsFile, instance);
		copy(filePart.getInputStream(), Channels.newOutputStream(outputChannel));

		return filename; // Return the filename without GCS/bucket appendage
	}
	
	private String uploadedFilename(final Part part) {

		final String partHeader = part.getHeader("content-disposition");

		for (String content : part.getHeader("content-disposition").split(";")) {
			if (content.trim().startsWith("filename")) {
				// Append a date and time to the filename
				DateTimeFormatter dtf = DateTimeFormat.forPattern("-YYYY-MM-dd-HHmmssSSS");
				DateTime dt = DateTime.now(DateTimeZone.UTC);
				String dtString = dt.toString(dtf);
				final String fileName = dtString + content.substring(content.indexOf('=') + 1).trim().replace("\"", "");

				return fileName;
			}
		}
		return null;
	}
	
	private void copy(InputStream input, OutputStream output) throws IOException {
		try {
			byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead = input.read(buffer);
			while (bytesRead != -1) {
				output.write(buffer, 0, bytesRead);
				bytesRead = input.read(buffer);
			}
		} finally {
			input.close();
			output.close();
		}
	}
	
	

	@PostMapping("/storeFileToGCS")
	public UploadFileResponse storeFileToGCS(@RequestParam("file") Part filePart) {
		String fileName;
		try {
			fileName = storeImage(filePart);

			String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
					.path("/downloadFile/")
					.path(fileName).toUriString();

			return new UploadFileResponse(fileName, fileDownloadUri, filePart.getContentType(), filePart.getSize());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	
	
	
	
	// ----------------------------------------- OLDER METHOD BELOW ------------------------------------------
	@Autowired
    private FileStorageService fileStorageService;

	
	@RequestMapping(method = RequestMethod.GET, value="/customer/customers")
	@ResponseBody
	  public String getAllCustomers() {
	  return "You will get the list of customers shortly...!!";
	 }
	
	@PostMapping("/uploadFile")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();

        return new UploadFileResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize());
    }
	
	@GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
	
}