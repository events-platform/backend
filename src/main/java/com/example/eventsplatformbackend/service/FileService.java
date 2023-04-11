package com.example.eventsplatformbackend.service;

import com.example.eventsplatformbackend.exception.UnsupportedExtensionException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class FileService {
    @Value("${server.files-destination-dir}")
    private String filesDirectory;
    public String saveUserAvatar(MultipartFile uploadedFile, String username) throws FileUploadException, UnsupportedExtensionException {
        if(!checkExtension(uploadedFile, Arrays.asList("jpg", "png", "jpeg"))){
            throw new UnsupportedExtensionException(String.format("wrong extension of %s", uploadedFile.getOriginalFilename()));
        }

        File userDir = new File(filesDirectory + File.separator + username);
        if (!userDir.exists() && userDir.mkdirs()){
            log.info("user dir created successfully in {}", userDir.getPath());
        }

        File fileToSave = new File(userDir.getPath() + File.separator + uploadedFile.getOriginalFilename());
        if (fileToSave.exists()){
            log.info("file {} already exists", uploadedFile.getOriginalFilename());
            return fileToSave.getPath();
        }
        try (OutputStream os = new FileOutputStream(fileToSave)) {
            os.write(uploadedFile.getBytes());
            log.info("file successfully saved to {}", fileToSave.getPath());
            return fileToSave.getPath();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new FileUploadException(String.format("cannot save file %s to %s", fileToSave.getName(), fileToSave.getPath()));
        }
    }

    public InputStream getFile(String path) throws FileNotFoundException {
        try{
            File file = ResourceUtils.getFile(path);
            InputStream fileInputStream = new FileInputStream(file);
            if(fileInputStream != null){
                return fileInputStream;
            } else {
                throw new FileNotFoundException();
            }
        } catch (Exception e){
            log.error("cannot read {}\n{}", path, e.getMessage());
            throw new FileNotFoundException(String.format("cannot read file %s", path));
        }
    }

    @Async
    public void deleteFile(String path){
        try{
            File file = new File(path);
            Files.delete(file.toPath());
            log.info("{} deleted successfully", path);
        } catch (Exception e){
            log.error("cannot delete {} with cause {}", path, e.getMessage());
        }
    }

    private boolean checkExtension(MultipartFile file, List<String> extensions){
        String filename = file.getOriginalFilename();
        Optional<String> fileExtension = Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));

        return fileExtension.isPresent() && extensions.contains(fileExtension.get());
    }
}
