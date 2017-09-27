package com.anosi.asset.test;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.highlight.HighlightField;
import org.elasticsearch.search.highlight.HighlightBuilder.Field;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.anosi.asset.IotxManagementApplication;
import com.anosi.asset.model.elasticsearch.IotxContent;
import com.anosi.asset.model.jpa.Iotx;
import com.anosi.asset.model.jpa.Iotx.NetworkCategory;
import com.anosi.asset.model.jpa.Iotx.Status;
import com.anosi.asset.service.CompanyService;
import com.anosi.asset.service.IotxContentService;
import com.anosi.asset.service.IotxService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = IotxManagementApplication.class)
@Transactional
public class TestIotxContent {

	@Autowired
	private IotxService iotxService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private IotxContentService iotxContentService;
	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;
	
	@Test
	@Rollback(false)
	public void initContent(){
		Iotx iotx = new Iotx();
		iotx.setCompany(companyService.findByName("北京安诺信通信科技有限公司"));
		iotx.setSerialNo("abc123efg");
		iotx.setLongitude(116.4136103013);
		iotx.setLatitude(39.9110666857);
		iotx.setNetworkCategory(NetworkCategory.WIFI);
		iotx.setStatus(Status.ONLINE);
		iotx.setOpenTime(new Date());
		iotxService.save(iotx);
	}
	
	@Test
	public void testContent(){
		Page<IotxContent> iotxContents = iotxContentService.findByContent("北京安诺信通信科技有限公司", "abc无锡", new PageRequest(0, 10));
		for (IotxContent iotxContent : iotxContents) {
			System.out.println(iotxContent.getId());
		}
	}
	
	@Test
	public void testHighLight(){
		NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder().withPageable(new PageRequest(0, 20));

		Field fieldConent = new HighlightBuilder.Field("content");
		fieldConent.preTags("<font color='#FF0000'>");
		fieldConent.postTags("</font>");
		
		queryBuilder.withQuery(matchQuery("content", "无锡"));
		
		SearchQuery searchQuery = queryBuilder.withHighlightFields(fieldConent).build();

		AggregatedPage<IotxContent> queryForPage = elasticsearchTemplate.queryForPage(searchQuery,
				IotxContent.class, new SearchResultMapper() {

					@SuppressWarnings("unchecked")
					@Override
					public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz,
							Pageable pageable) {
						List<IotxContent> chunk = new ArrayList<>();
						for (SearchHit searchHit : response.getHits()) {
							if (response.getHits().getHits().length <= 0) {
								return null;
							}
							IotxContent iotxContent = new IotxContent();

							// 高亮内容
							HighlightField content = searchHit.getHighlightFields().get("content");
							iotxContent.setContent(content.toString());
							chunk.add(iotxContent);
						}
						return new AggregatedPageImpl<>((List<T>) chunk, pageable, response.getHits().getTotalHits());
					}
				});
		for (IotxContent iotxContent : queryForPage) {
			System.out.println(iotxContent.getContent());
		}
		
	}
	
}
