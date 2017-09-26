package com.anosi.asset.service.impl;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import java.util.List;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.dao.jpa.BaseJPADao;
import com.anosi.asset.dao.jpa.IotxDao;
import com.anosi.asset.model.jpa.District;
import com.anosi.asset.model.jpa.Iotx;
import com.anosi.asset.model.jpa.QIotx;
import com.anosi.asset.service.DistrictService;
import com.anosi.asset.service.IotxDataService;
import com.anosi.asset.service.IotxService;
import com.anosi.asset.util.MapUtil;
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

	@Override
	public BaseJPADao<Iotx> getRepository() {
		return iotxDao;
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
		JSONObject addressComponent = MapUtil.getAddressComponent(String.valueOf(iotx.getLongitude()),
				String.valueOf(iotx.getLatitude()));
		District district = districtService.findByNameAndCity(addressComponent.getString("district"),
				addressComponent.getString("city"));
		iotx.setDistrict(district);
		return iotxDao.save(iotx);
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

}
