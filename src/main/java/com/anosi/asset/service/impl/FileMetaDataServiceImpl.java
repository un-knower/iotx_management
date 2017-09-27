package com.anosi.asset.service.impl;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.Date;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anosi.asset.component.SessionUtil;
import com.anosi.asset.dao.mongo.FileMetaDataDao;
import com.anosi.asset.dao.mongo.GridFsDao;
import com.anosi.asset.model.mongo.FileMetaData;
import com.anosi.asset.service.FileMetaDataService;
import com.querydsl.core.types.Predicate;

@Service("fileMetaDataService")
@Transactional
public class FileMetaDataServiceImpl implements FileMetaDataService{
	
	private static final Logger logger = LoggerFactory.getLogger(FileMetaDataServiceImpl.class);

	@Autowired
	private FileMetaDataDao fileMetaDataDao;
	@Autowired
	private GridFsDao gridFsDao;
	
	@Override
	public FileMetaData saveFile(String identification, String fileName,InputStream is,Long fileSize) throws Exception{
		FileMetaData fileMetaData=new FileMetaData();
		fileMetaData.setIdentification(identification);
		fileMetaData.setUploader(SessionUtil.getCurrentUser()==null?identification:SessionUtil.getCurrentUser().getLoginId());
		fileMetaData.setUploadTime(new Date());
		fileMetaData.setFileName(fileName);
		fileMetaData.setFileSize(fileSize);
		
		return this.saveFileAndAttributes(fileMetaData, is);
	}
	
	private FileMetaData saveFileAndAttributes(FileMetaData fileMetaData,InputStream in) throws Exception {
		//gridfs存储文件
		logger.info("upload file to gridfs");
		Object id = gridFsDao.uploadFileToGridFS(in,fileMetaData.getFileName());
		fileMetaData.setObjectId(FileMetaData.ObjectIdToBigIntegerConverter((ObjectId) id));
		fileMetaDataDao.save(fileMetaData);
		return fileMetaData;
	}
	
	

	@Override
	public void deleteFile(FileMetaData fileMetaData) {
		ObjectId id=FileMetaData.BigIntegerToObjectIdConverter(fileMetaData.getObjectId());
		fileMetaDataDao.delete(fileMetaData);
		//gridfs删除文件
		logger.info("delete file from gridfs");
		gridFsDao.deleteFileFromGridFS(id);
	}

	@Override
	public Page<FileMetaData> findByIdentification(String identification,Pageable pageable) {
		return fileMetaDataDao.findByIdentification(identification,pageable);
	}
	
	@Override
	public Page<FileMetaData> findByUploader(String uploader,Pageable pageable) {
		return fileMetaDataDao.findByUploader(uploader,pageable);
	}

	@Override
	public FileMetaData findByObjectId(BigInteger objectId) {
		return fileMetaDataDao.findByObjectId(objectId);
	}

	@Override
	public InputStream getFileByObjectId(BigInteger objectId) {
		return gridFsDao.getFileFromGridFS(FileMetaData.BigIntegerToObjectIdConverter(objectId));
	}

	@Override
	public Page<FileMetaData> findAll(Predicate predicate, Pageable pageable) {
		return fileMetaDataDao.findAll(predicate, pageable);
	}
	
}
