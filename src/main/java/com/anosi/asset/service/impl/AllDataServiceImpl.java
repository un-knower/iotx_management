package com.anosi.asset.service.impl;

import com.anosi.asset.dao.mongo.AllDataDao;
import com.anosi.asset.dao.mongo.BaseMongoDao;
import com.anosi.asset.model.mongo.AllData;
import com.anosi.asset.service.AllDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * java类简单作用描述
 *
 * @ProjectName: iotx_management
 * @Package: com.anosi.asset.service.impl
 * @Description:
 * @Author: jinyao
 * @CreateDate: 2018/1/25 14:09
 * @UpdateUser: jinyao
 * @UpdateDate: 2018/1/25 14:09
 * @UpdateRemark: The modified content
 * @Version: 1.0
 */
@Service("allDataService")
public class AllDataServiceImpl extends BaseMongoServiceImpl<AllData> implements AllDataService{
    @Autowired
    private AllDataDao allDataDao;

    @Override
    public BaseMongoDao<AllData> getRepository() {
        return allDataDao;
    }
}
