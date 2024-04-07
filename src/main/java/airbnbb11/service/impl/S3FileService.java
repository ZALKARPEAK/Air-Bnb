package airbnbb11.service.impl;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.InvalidMimeTypeException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3FileService {


    @Value("${aws_bucket_name}")
    private String bucketName;

    private final AmazonS3 s3Client;

    @Value("${prefix-for-file-link}")
    private String prefixForFileLink;



    public Map<String, String> uploadFile(MultipartFile file) {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.addUserMetadata("Content-Type", file.getContentType());
            metadata.addUserMetadata("Content-Length", String.valueOf(file.getSize()));
            s3Client.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Map.of("Link", prefixForFileLink + fileName);
    }



    public boolean deleteFile(String fileName) {
        try {
            s3Client.deleteObject("airbnb11", fileName);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public byte[] downloadFile(String fileName) {
        S3Object s3Object = s3Client.getObject(bucketName, fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try {
            return IOUtils.toByteArray(inputStream);
        } catch (InvalidMimeTypeException | IOException e) {
            log.error("При загрузке файла произошла ошибка: {}", fileName, e);
        }
        return new byte[0];
    }

    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            log.error("Ошибка преобразования multipartFile в файл", e);
        }
        return convertedFile;
    }
}