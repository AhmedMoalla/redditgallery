package com.novencia.bltech.posts.storage;

import com.novencia.bltech.posts.storage.exception.StorageException;

import java.io.InputStream;

public interface StorageService {
    String uploadMediaToStorage(InputStream mediaData, String fileName, String bucketName, String contentType) throws StorageException;
    String getPresignedObjectUrl(String objectId, String bucketName) throws StorageException;
    String createBucketIfNotExists(String bucket) throws StorageException;
}
