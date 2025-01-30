package com.example.ecommerce.service;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    public String uploadFile(MultipartFile file) {
        log.info("S3Service::uploadFile execution started.");
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .build();

            s3Client.putObject(
                putObjectRequest,
                software.amazon.awssdk.core.sync.RequestBody.fromInputStream(
                    file.getInputStream(),
                    file.getSize()
                )
            );

            log.info("S3Service::uploadFile execution ended.");

            return fileName;
        } catch (IOException ex) {
            throw new IllegalStateException("파일 업로드 실패", ex);
        }
    }

    public String getPresignedUrl(String fileKey) {
        log.info("S3Service::getPresignedUrl execution started.");

        try {
            S3Presigner presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(
                    StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();

            String presignedUrl = presigner.presignGetObject(
                b -> b.signatureDuration(Duration.ofMinutes(60))
                    .getObjectRequest(r -> r.bucket(bucketName).key(fileKey))).url().toString();

            log.info("S3Service::getPresignedUrl execution successfully ended.");

            return presignedUrl;
        } catch (Exception ex) {
            throw new IllegalStateException("파일 변환 살패", ex);
        }

    }

}
