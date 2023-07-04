package com.example.eventsplatformbackend.adapter.web.controller;

import com.amazonaws.services.s3.model.S3Object;
import com.example.eventsplatformbackend.service.user.UserFileService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Работает с файлами пользователей
 */
@RestController
@RequestMapping(path = "user/files")
@RequiredArgsConstructor
@Slf4j
public class UserFileController {
    private final UserFileService userFileService;

    /**
     * Загружает файл и устанавливает его аватаркой пользователя, возвращает ссылку на загруженны файл.
     * @param uploadedFile Файл, который должен быть загружен
     * @param principal Авторизовавшийся пользователь
     * @return Ссылка на файл
     */
    @PostMapping(path = "/avatar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = "application/json; charset=utf-8")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String uploadImage(@RequestParam("file") MultipartFile uploadedFile, Principal principal){
        return userFileService.setUserAvatarAndGetLink(uploadedFile, principal);
    }
    @SneakyThrows
    @ResponseBody
    @GetMapping(path = "/download/{username}", produces="application/zip")
    public void downloadUserFiles(@PathVariable String username,
                                    HttpServletResponse response){
        //setting headers
        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader(
                "Content-Disposition", String.format("attachment; filename=\"%s.zip\"", username));

        ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());

        // create a list to add files to be zipped
        List<S3Object> files = userFileService.getUserFiles(username);

        // package files
        for (S3Object file : files) {
            //new zip entry and copying inputstream with file to zipOutputStream, after all closing streams
            var filename = file.getKey().split("/")[(int) Arrays.stream(file.getKey().split("/")).count() -1];
            zipOutputStream.putNextEntry(new ZipEntry(filename));
            InputStream fileInputStream = new ByteArrayInputStream(file.getObjectContent().getDelegateStream().readAllBytes());

            IOUtils.copy(fileInputStream, zipOutputStream);

            fileInputStream.close();
            zipOutputStream.closeEntry();
        }

        zipOutputStream.close();
    }
}
