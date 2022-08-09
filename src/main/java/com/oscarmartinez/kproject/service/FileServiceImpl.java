package com.oscarmartinez.kproject.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.annotation.processing.FilerException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FileServiceImpl implements IFileService {

	@Value("${user.dir.storage}")
	private String imageDirectory;

	public void createDirectoryIfNotExist() {
		File file = new File(imageDirectory);
		if (!file.exists())
			file.mkdir();
	}

	public String storeFile(final MultipartFile file, String name) throws Exception {
		log.info(imageDirectory);
		final Path fileStorageLocation = Paths.get(imageDirectory).toAbsolutePath().normalize();
		log.info(fileStorageLocation.toString());
		final String fileName = StringUtils.cleanPath(name.concat(file.getOriginalFilename()
				.substring(file.getOriginalFilename().indexOf("."), file.getOriginalFilename().length())));
		try {
			if (fileName.contains("..")) {
				throw new FilerException("Sorry! Filename contains invalid path sequence " + fileName);
			}
			final Path targetLocation = fileStorageLocation.resolve(fileName);
			log.info(targetLocation.toString());
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			return fileName;
		} catch (final IOException ex) {
			throw new Exception("No se pudo guardar el archivo " + fileName + ". Por favor intenta de nuevo.!");
		}
	}

	@Override
	public ResponseEntity<?> uploadImage(MultipartFile file, String name) throws Exception {
		final String METHOD_NAME = "uploadImage";
		createDirectoryIfNotExist();
		log.info("{} - File recieved: {}", METHOD_NAME, file.getOriginalFilename());
		storeFile(file, name);
		return ResponseEntity.ok(file.getOriginalFilename());
	}

	public Resource loadFileAsResource(final String fileName) throws FileNotFoundException {
		try {
			final Path filePath = Paths.get(imageDirectory).toAbsolutePath().normalize().resolve(fileName).normalize();
			log.info("download" + filePath.toString());
			final Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				throw new FileNotFoundException("Archivo no encontrado " + fileName);
			}
		} catch (final MalformedURLException ex) {
			throw new FileNotFoundException("Archivo no encontrado " + fileName);
		}
	}

	public Resource getFileImage(final String fileName) throws FileNotFoundException {
		return loadFileAsResource(fileName);
	}

}
