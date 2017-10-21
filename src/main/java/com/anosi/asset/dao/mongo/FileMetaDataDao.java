package com.anosi.asset.dao.mongo;

import java.math.BigInteger;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.anosi.asset.model.mongo.FileMetaData;

public interface FileMetaDataDao extends BaseMongoDao<FileMetaData> {

	public Page<FileMetaData> findByIdentification(String identification, Pageable pageable);

	public Page<FileMetaData> findByUploader(String uploader, Pageable pageable);

	public FileMetaData findByObjectId(BigInteger objectId);

}
