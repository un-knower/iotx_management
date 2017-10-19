package com.anosi.asset.service.impl;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.component.I18nComponent;
import com.anosi.asset.dao.jpa.BaseJPADao;
import com.anosi.asset.dao.jpa.IotxDao;
import com.anosi.asset.exception.CustomRunTimeException;
import com.anosi.asset.model.elasticsearch.IotxContent;
import com.anosi.asset.model.jpa.Account;
import com.anosi.asset.model.jpa.Company;
import com.anosi.asset.model.jpa.District;
import com.anosi.asset.model.jpa.Iotx;
import com.anosi.asset.model.jpa.QIotx;
import com.anosi.asset.mqtt.MqttServer;
import com.anosi.asset.service.CompanyService;
import com.anosi.asset.service.DistrictService;
import com.anosi.asset.service.IotxContentService;
import com.anosi.asset.service.IotxDataService;
import com.anosi.asset.service.IotxService;
import com.anosi.asset.util.MapUtil;
import com.google.common.collect.ImmutableMap;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathInits;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Service("iotxService")
@Transactional
public class IotxServiceImpl extends BaseServiceImpl<Iotx> implements IotxService {

	private static final Logger logger = LoggerFactory.getLogger(IotxServiceImpl.class);

	@Autowired
	private IotxDao iotxDao;
	@Autowired
	private IotxDataService iotxDataService;
	@Autowired
	private DistrictService districtService;
	@Autowired
	private EntityManager entityManager;
	@Autowired
	private IotxContentService iotxContentService;
	@Autowired
	private I18nComponent i18nComponent;
	@Autowired
	private MqttServer mqttServer;
	@Autowired
	private CompanyService companyService;

	@Override
	public BaseJPADao<Iotx> getRepository() {
		return iotxDao;
	}

	/***
	 * 重写save,保存iotx的同时，将@Content标记的字段内容提取，存储到iotxContent中
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Iotx save(Iotx iotx) {
		// 根据经纬度获取位置,并保存
		setIotxDistrict(iotx);
		iotx.setCompany(companyService.findByName(i18nComponent.getMessage("goaland")));
		iotxDao.save(iotx);

		try {
			iotxContentService.save(iotx);
		} catch (Exception e) {
			throw new CustomRunTimeException(e.getMessage());
		}
		return iotx;
	}

	/***
	 * 重写批量添加
	 */
	@Override
	public <S extends Iotx> Iterable<S> save(Iterable<S> iotxs) {
		List<Iotx> iotxsWithDistrict = new ArrayList<>();
		for (Iotx iotx : iotxs) {
			setIotxDistrict(iotx);
		}
		iotxsWithDistrict = iotxDao.save(iotxsWithDistrict);

		try {
			iotxContentService.save(iotxsWithDistrict);
		} catch (Exception e) {
			throw new CustomRunTimeException(e.getMessage());
		}

		return iotxs;
	}

	@Override
	public Page<Iotx> findAll(Predicate predicate, Pageable pageable) {
		logger.debug("finAll by predicate:{}", predicate == null ? "is null" : predicate.toString());
		Page<Iotx> iotxPage = iotxDao.findAll(predicate, pageable);
		// 查询每个iotx的告警数量
		for (Iotx iotx : iotxPage) {
			iotx.setAlarmQuantity(iotxDataService.countByiotxSN(iotx.getSerialNo()));
		}
		return iotxPage;
	}

	@Override
	public Iotx setIotxDistrict(Iotx iotx) {
		Double longitude = iotx.getLongitude();
		Double latitude = iotx.getLatitude();
		if (longitude == null) {
			throw new RuntimeException(i18nComponent.getMessage("iotx.longitude.cannot.null"));
		}
		if (latitude == null) {
			throw new RuntimeException(i18nComponent.getMessage("iotx.latitude.cannot.null"));
		}
		JSONObject addressComponent = MapUtil.getAddressComponent(String.valueOf(longitude), String.valueOf(latitude));
		District district = districtService.findByNameAndCity(addressComponent.getString("district"),
				addressComponent.getString("city"));
		iotx.setDistrict(district);
		// 获取转换的百度坐标
		JSONObject convertLocation = MapUtil.convertLocation(String.valueOf(longitude), String.valueOf(latitude));
		iotx.setBaiduLongitude(
				Double.parseDouble(new String(Base64.getDecoder().decode(convertLocation.getString("x")))));
		iotx.setBaiduLatitude(
				Double.parseDouble(new String(Base64.getDecoder().decode(convertLocation.getString("y")))));
		return iotx;
	}

	/***
	 * 基本思路是count groupBy province,city 查看结果是否为1,为1表示都是同一个行政区划的,那么找下一级
	 * 
	 * @param predicate
	 */
	@Override
	public JSONArray ascertainArea(Predicate predicate) {
		// 使用querydsl
		QIotx qIotx = QIotx.iotx;
		JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
		List<Tuple> iotxTuples = null;

		QIotx qIotxCustom = createQIotxCustom();
		long countProvince = queryFactory.from(qIotxCustom).where(predicate).groupBy(qIotxCustom.district.city.province)
				.fetchCount();
		if (countProvince == 1) {
			// 如果都是一个省，那么查看是否都是一个市
			long countCity = queryFactory.from(qIotx).where(predicate).groupBy(qIotx.district.city).fetchCount();
			if (countCity == 1) {
				// 如果都是一个市，那么就按照区来统计数据
				iotxTuples = queryFactory.select(qIotx.district.city.name, qIotx.count()).from(qIotx).where(predicate)
						.groupBy(qIotx.district.city).fetch();
			} else {
				// 如果是多个市，就按照市统计
				iotxTuples = queryFactory.select(qIotx.district.name, qIotx.count()).from(qIotx).where(predicate)
						.groupBy(qIotx.district).fetch();
			}
		} else {
			// 如果是多个省，就按照省统计
			iotxTuples = queryFactory.select(qIotxCustom.district.city.province.name, qIotx.count()).from(qIotx)
					.where(predicate).groupBy(qIotxCustom.district.city.province).fetch();
		}

		JSONArray jsonArray = new JSONArray();
		for (Tuple tuple : iotxTuples) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("name", tuple.get(0, String.class));
			jsonObject.put("amount", tuple.get(1, Long.class));
			jsonArray.add(jsonObject);
		}
		return jsonArray;
	}

	/****
	 * 貌似遇到个bug，对象链超过三个后就为null了 比如qIotx.district.city.province结果为null
	 * 查看源码后，应该需要自己调用最后一个构造方法，定制一个对象，不能用编译类提供的对象了
	 */
	private QIotx createQIotxCustom() {
		/***
		 * 主要在于构造PathInits 需要有如下结构: map:{district:{city:{province:.....}}}
		 */
		PathInits inits = new PathInits("district.city.province");
		QIotx iotx = new QIotx(Iotx.class, forVariable("iotx"), inits);
		return iotx;
	}

	@Override
	public Page<Iotx> findByContentSearch(String content, Predicate predicate, Pageable pageable) {
		Account account = sessionComponent.getCurrentUser();
		Page<IotxContent> iotxContents;
		// 防止sort报错，只获取pageable的页数和size
		logger.debug("page:{},size:{}", pageable.getPageNumber(), pageable.getPageSize());
		Pageable contentPage = new PageRequest(pageable.getPageNumber(), pageable.getPageSize());
		if (account.isAdmin()) {
			iotxContents = iotxContentService.findByContent(content, contentPage);
		} else {
			iotxContents = iotxContentService.findByContent(account.getCompany().getName(), content, contentPage);
		}
		List<Long> ids = iotxContents.getContent().stream().map(c -> Long.parseLong(c.getId()))
				.collect(Collectors.toList());
		return findAll(QIotx.iotx.id.in(ids).and(predicate), pageable);
	}

	@Override
	public Iotx findBySerialNo(String serialNo) {
		return iotxDao.findBySerialNo(serialNo);
	}

	@Override
	public void remoteUpdate(Iotx iotx, Company company) {
		JSONObject bodyJson = new JSONObject();
		if (company != null && !Objects.equals(iotx.getCompany(), company)) {
			bodyJson.put("companyName", company.getName());
		}
		if (!bodyJson.isEmpty()) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("header", new JSONObject(ImmutableMap.of("uniqueId",UUID.randomUUID().toString(),"type", "iotx", "serialNo", iotx.getSerialNo())));
			jsonObject.put("body", bodyJson);
			MqttMessage message = new MqttMessage();
			message.setQos(2);
			message.setRetained(true);
			message.setPayload(jsonObject.toString().getBytes());
			try {
				mqttServer.publish("/configure/" + iotx.getSerialNo(), message);
			} catch (MqttException e) {
				e.printStackTrace();
				throw new CustomRunTimeException(i18nComponent.getMessage("mqtt.message.send.fail"));
			}
		}
	}

}
