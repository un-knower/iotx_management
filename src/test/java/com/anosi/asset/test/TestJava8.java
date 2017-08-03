package com.anosi.asset.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.Test;

import com.google.common.base.CaseFormat;
import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import com.google.common.collect.Multisets;
import com.google.common.collect.Ordering;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.Table;
import com.google.common.collect.TreeRangeSet;
import com.google.common.primitives.Ints;

public class TestJava8 {

	@Test
	public void testLambdaAndStream() {
		List<String> names = new ArrayList<String>();
		names.add("ABC");
		names.add("EFG");
		names.stream().map(name -> name.toLowerCase()).forEach(System.out::println);
	}

	@Test
	public void testStream(){
		List<Integer> nums = Lists.newArrayList(1,1,null,2,3,4,null,5,6,7,8,9,10);
		System.out.println("sum is:"+nums.stream().filter(num -> num != null).
		            distinct().mapToInt(num -> num * 2).
		            peek(System.out::println).skip(2).limit(4).sum());
	}
	
	@Test
	public void testOptional(){
		Optional<String> optional = Optional.of("hello");
		System.out.println(optional.get());
	}
	
	@Test
	public void testObjects(){
		System.out.println(MoreObjects.toStringHelper(this).add("x", 1).toString());
		System.out.println(ComparisonChain.start().compare("a", "b").result());
	}
	
	@Test
	public void testOrdering(){
		List<String> names = new ArrayList<String>();
		names.add("ABC");
		names.add("EFG");
		Ordering<String> orders = Ordering.natural().nullsFirst();
		names.sort(orders);
		System.out.println(names);
	}
	
	@Test
	public void TestMap(){
		Map<String, Integer> map = new HashMap<>();
		map.put("a", 1);
		map.put("b", 2);
		System.out.println(map);
	}
	
	@Test
	public void testMultiset(){
		ArrayList<Integer> intList = Lists.newArrayList(1,1,2,2,2,2,3,3,4,5,4,4);
		ArrayList<Integer> intList2 = Lists.newArrayList(1,1,1,1,2,2,2,3,3,3,4,5,5,4);
		
		Multiset<Integer> multiset = HashMultiset.create();
		multiset.addAll(intList);
		
		Multiset<Integer> multiset2 = HashMultiset.create();
		multiset2.addAll(intList2);
		
		System.out.println(Multisets.intersection(multiset, multiset2));
		Multisets.removeOccurrences(multiset, multiset2);
		System.out.println(multiset);
		
		System.out.println(multiset.size());
		System.out.println(multiset.count(1));
		System.out.println(multiset.count(0));
		multiset.stream().forEach(System.out::print);
		
		System.out.println();
		
		Set<Entry<Integer>> entrySet = multiset.entrySet();
		entrySet.stream().forEach(System.out::print);
		
		System.out.println();
		
		Set<Integer> elementSet = multiset.elementSet();
		elementSet.stream().forEach(System.out::print);
	}
	
	@Test
	public void testMultimap(){
		ArrayList<Integer> intList = Lists.newArrayList(1,1,2,2,2,2,3,3,4,5,4,4);
		Multimap<String, Integer> multimap = ArrayListMultimap.create();
		multimap.putAll("test1", intList);
		/*Map<String, Collection<Integer>> map = multimap.asMap();
		map.remove("test1");*/
		System.out.println(multimap);
		ImmutableMap<String,String> of = ImmutableMap.of("assignee", "abc");
		System.out.println(of.get("assignee"));
	}
	
	@Test
	public void testBiMap(){
		BiMap<String, Integer> userId = HashBiMap.create();
		userId.put("admin", 1);
		userId.put("hello", 2);
		System.out.println(userId);
		System.out.println(userId.inverse());
	}
	
	@Test
	public void testTable(){
		Table<String, String, Integer> table = HashBasedTable.create();
		table.put("a1", "b1", 1);
		table.put("a1", "b2", 2);
		table.put("a2", "b1", 1);
		table.put("a2", "b2", 2);
		System.out.println(table);
	}
	
	@Test
	public void testRangeSet(){
		RangeSet<Integer> rangeSet = TreeRangeSet.create();
		rangeSet.add(Range.closed(1, 10)); // {[1,10]}
		rangeSet.add(Range.closedOpen(11, 15));//不相连区间:{[1,10], [11,15)}
		rangeSet.add(Range.closedOpen(15, 20)); //相连区间; {[1,10], [11,20)}
		rangeSet.add(Range.openClosed(0, 0)); //空区间; {[1,10], [11,20)}
		rangeSet.remove(Range.open(5, 10)); //分割[1, 10]; {[1,5], [10,10], [11,20)}
		System.out.println(rangeSet);
		System.out.println(rangeSet.complement());
	}
	
	@Test
	public void testIterable(){
		Iterable<Integer> concatenated = Iterables.concat(
		        Ints.asList(1, 2, 3),
		        Ints.asList(4, 5, 6)); // concatenated包括元素 1, 2, 3, 4, 5, 6
		System.out.println(concatenated);
		System.out.println(Iterables.getLast(concatenated));
	}
	
	@Test
	public void testOnline(){
		long start=System.currentTimeMillis();
		List<String> serialNos=new ArrayList<>();
		List<String> existSerialNos=new ArrayList<>();
		for (int i=0;i<800000;i++) {
			String uuid = UUID.randomUUID().toString().replace("-", "");
			serialNos.add(uuid);
			if(i%1000==0){
				existSerialNos.add(uuid);
			}
			if(i%2000==0){
				existSerialNos.add(UUID.randomUUID().toString().replace("-", ""));
			}
		}
		
		List<String> result=new ArrayList<>();
		//比对
		existSerialNos.stream().filter(serialNo -> !serialNos.contains(serialNo)).forEach(result::add);
		long end=System.currentTimeMillis();
		System.out.println(result);
		System.out.printf("serialNos：%d;existSerialNos:%d;result:%d\n",serialNos.size(),existSerialNos.size(),result.size());
		System.out.printf("所用时间:%d秒",(end-start)/1000);
	}
	
	@Test
	public void testJoiner(){
		Joiner joiner = Joiner.on("; ").skipNulls();
		System.out.println(joiner.join("Harry", null, "Ron", "Hermione"));
		System.out.println(Joiner.on(",").useForNull("null").join("Harry", null, "Ron", "Hermione"));
	}
	
	@Test
	public void testSplite(){
		String a="foo,bar,,   qux";
		String[] strings = a.split(",");
		for (String string : strings) {
			System.out.print(string+" ");
		}
		System.out.println();
		Iterable<String> splits = Splitter.on(",").trimResults().omitEmptyStrings().split(a);
		splits.forEach(System.out::println);
	}
	
	@Test
	public void testCharMatcher(){
		String a="FirstName LastName +1 123 456 789 !@#$%^&*()_+|}{:\"?><";
		System.out.println(CharMatcher.javaDigit().retainFrom(a));
		System.out.println( CharMatcher.is('a').removeFrom("bazaar"));
	}
	
	@Test
	public void testCaseFormat(){
		System.out.println(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, "CONSTANT_NAME"));
		System.out.println(CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_CAMEL, "CONSTANT-NAME"));
	}
}
