package com.cmlteam.video_reply_telegram_bot.services;

import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.cmlteam.video_reply_telegram_bot.util.FileName;
import com.cmlteam.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@Slf4j
public class AwsS3Service {

  private final AmazonS3 s3;

  @Autowired
  public AwsS3Service(AwsS3Props awsS3Props) {
    this.s3 =
        AmazonS3ClientBuilder.standard()
            .withEndpointConfiguration(
                new AwsClientBuilder.EndpointConfiguration(
                    awsS3Props.getEndpoint(), awsS3Props.getRegion()))
            .withCredentials(
                new AWSStaticCredentialsProvider(
                    new BasicAWSCredentials(awsS3Props.getAccessKey(), awsS3Props.getSecretKey())))
            .build();
  }

  /**
   * Generates unique key and uploads file with that key to corresponding bucket with private access
   *
   * @param file file to put to S3
   * @param bucket bucket name
   * @return generated S3 key for the file
   * @throws IOException when upload failed
   */
  public String uploadToS3(MultipartFile file, String bucket) throws IOException {
    Objects.requireNonNull(file);
    Objects.requireNonNull(bucket);

    String name = file.getOriginalFilename();
    log.info(
        "Uploading multipart file {} of size {}...", name, Util.renderFileSize(file.getSize()));
    String key = new FileName(Util.safeFilename(name), 100).getName("_" + UUID.randomUUID());

    try (InputStream inputStream = file.getInputStream()) {
      ObjectMetadata metadata = new ObjectMetadata();
      metadata.setContentType(file.getContentType());
      metadata.setContentLength(file.getSize());
      // TODO: this should be configurable
      metadata.setContentDisposition(
          "attachment;filename=" + URLEncoder.encode(name, UTF_8.toString()));
      s3.putObject(
          new PutObjectRequest(bucket, key, inputStream, metadata)
              .withCannedAcl(CannedAccessControlList.Private));
    }

    return key;
  }

  /**
   * Generates unique key and uploads file with that key to corresponding bucket with private access
   *
   * @param key file to delete from S3
   * @param bucket bucket name
   * @throws SdkClientException when deletion failed
   */
  public void removeFromS3(String key, String bucket) {
    try {
      s3.deleteObject(new DeleteObjectRequest(bucket, key));
    } catch (SdkClientException e) {
      log.warn(String.format("Error deleting s3 object %s from %s", key, bucket), e);
      throw e;
    }
  }

  /**
   * Download file from S3
   *
   * @param bucket bucket name
   * @param key requested S3 key for the file
   * @return InputStream for specified amazon bucket, and key
   */
  public InputStream getInputStream(String bucket, String key) {
    try {
      return s3.getObject(bucket, key).getObjectContent();
    } catch (AmazonS3Exception ex) {
      log.warn(String.format("Error getting s3 object %s from %s", key, bucket), ex);
      throw ex;
    }
  }

  public String generatePresignedUrl(String bucket, String objectKey) {
    // Set the presigned URL to expire after 12 hours. TODO make configurable
    Date expiration =
        Date.from(
            LocalDateTime.now()
                .plus(12, ChronoUnit.HOURS)
                .atZone(ZoneId.systemDefault())
                .toInstant());
    GeneratePresignedUrlRequest generatePresignedUrlRequest =
        new GeneratePresignedUrlRequest(bucket, objectKey)
            .withMethod(HttpMethod.GET)
            .withExpiration(expiration);
    URL url = s3.generatePresignedUrl(generatePresignedUrlRequest);
    return url.toString();
  }
}
