package com.oscarmartinez.kproject.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.oscarmartinez.kproject.service.FileServiceImpl;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/file")
public class FileController {

	@Autowired
	private FileServiceImpl fileService;

	@PostMapping
	public ResponseEntity<?> saveImage(@RequestParam("fileImage") MultipartFile file, @RequestParam String name)
			throws Exception {
		return fileService.uploadImage(file, name);
	}

	@GetMapping("/{fileName}")
	public ResponseEntity<?> downloadFile(@PathVariable final String fileName, final HttpServletRequest request)
			throws IOException {
		final Resource resource = fileService.getFileImage(fileName);
		String contentType = null;
		contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		if (contentType == null) {
			contentType = "application/octet-stream";
		}
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

}
