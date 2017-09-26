package com.anosi.asset.service;

import java.io.Serializable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BaseContentService<T, ID extends Serializable, OriginalBean> {

	/***
	 * 根据content查询
	 * 
	 * @param companyName
	 * @param content
	 * @param pageable
	 * @return
	 */
	public Page<T> findByContent(String companyName, String content, Pageable pageable);

	/***
	 * 重载
	 * 
	 * @param content
	 * @param pageable
	 * @return
	 */
	Page<T> findByContent(String content, Pageable pageable);
	
	/***
	 * 获取iotx中标记的内容
	 * 
	 * @param o
	 * @return
	 * @throws Exception
	 */
	String convertContent(OriginalBean o) throws Exception;
	
	/***
	 * 保存
	 * 
	 * @param o
	 * @return
	 * @throws Exception
	 */
	T save(OriginalBean o) throws Exception;

	/***
	 * 更新
	 * 
	 * @param t
	 * @param o
	 * @return
	 * @throws Exception
	 */
	T update(T t, OriginalBean o) throws Exception;

}
