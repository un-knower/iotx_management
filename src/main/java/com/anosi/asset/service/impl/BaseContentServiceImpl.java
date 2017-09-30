package com.anosi.asset.service.impl;

import java.io.Serializable;
import java.lang.reflect.Field;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.anosi.asset.dao.elasticsearch.BaseContentDao;
import com.anosi.asset.model.elasticsearch.BaseContent;
import com.anosi.asset.model.elasticsearch.Content;
import com.anosi.asset.util.PropertyUtil;

public abstract class BaseContentServiceImpl<T extends BaseContent, ID extends Serializable, OriginalBean> {

	public abstract BaseContentDao<T, ID> getRepository();

	/****
	 * 通过提取对象中的@Content注解,获取到需要加入content的字段内容
	 * 
	 * @param o
	 * @return
	 */
	public String getContent(OriginalBean o) throws Exception {
		StringBuilder sb = new StringBuilder();

		Class<? extends Object> clazz = o.getClass();
		// 获取所有字段
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			// 判断是否有Content注解
			boolean fieldHasAnno = field.isAnnotationPresent(Content.class);
			if (fieldHasAnno) {
				Content fieldAnno = field.getAnnotation(Content.class);
				// 输出注解属性
				String[] extractFields = fieldAnno.extractFields();
				if (extractFields != null && extractFields.length != 0) {
					// 如果自定义了extractFields
					for (String extractField : extractFields) {
						sb.append(PropertyUtil.getNestedProperty(o, extractField) + "\t");
					}
				} else {
					// 如果使用默认
					sb.append(PropertyUtil.getNestedProperty(o, field.getName()) + "\t");
				}
			}
		}
		return sb.toString();
	}

	public Page<T> findByContent(String content, Pageable pageable) {
		return getRepository().findByContentContaining(StringUtils.deleteWhitespace(content), pageable);
	}

	public Page<T> findByContent(String companyName, String content, Pageable pageable) {
		return getRepository().findByCompanyNameEqualsAndContentContaining(companyName, StringUtils.deleteWhitespace(content), pageable);
	}

	public String convertContent(OriginalBean o) throws Exception {
		return getContent(o);
	}

	public T setCommonContent(T t, OriginalBean o) throws Exception {
		t.setContent(convertContent(o));
		return t;
	}

}
