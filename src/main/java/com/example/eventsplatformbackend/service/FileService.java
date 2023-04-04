package com.example.eventsplatformbackend.service;

import com.example.eventsplatformbackend.exception.UnsupportedExtensionException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class FileService {
    @Value("${server.files-destination-dir}")
    private String filesDirectory;
    public String saveUserAvatar(MultipartFile uploadedFile, Principal principal) throws FileUploadException, UnsupportedExtensionException {
        if(!checkExtension(uploadedFile, Arrays.asList("jpg", "png", "jpeg"))){
            throw new UnsupportedExtensionException(String.format("wrong extension of %s", uploadedFile.getOriginalFilename()));
        }

        File rootDir = new File(filesDirectory);
        if (rootDir.mkdir()){
            log.info("root dir created successfully in {}", rootDir.getPath());
        }

        File userDir = new File(rootDir.getPath() + File.separator + principal.getName());
        if (userDir.mkdir()){
            log.info("user dir created successfully in {}", userDir.getPath());
        }

        File fileToSave = new File(userDir.getPath() + File.separator + uploadedFile.getOriginalFilename());
        try (OutputStream os = new FileOutputStream(fileToSave)) {
            os.write(uploadedFile.getBytes());
            log.info("file successfully saved to {}", fileToSave.getPath());
            return fileToSave.getPath();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new FileUploadException(String.format("cannot save file %s to %s", fileToSave.getName(), fileToSave.getPath()));
        }
    }

    public void deleteFile(String path){
        try{
            File file = new File(path);
            file.delete();
            log.info("file {} deleted successfully", path);
        } catch (Exception e){
            log.error("cannot delete file {} with cause {}", path, e.getMessage());
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
