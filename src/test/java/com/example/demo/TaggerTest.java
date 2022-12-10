package com.example.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.Set;
import java.util.function.Predicate;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Service.Tagger;
import com.example.demo.db.PinMapper;
import com.example.demo.db.PinVO;
import com.example.demo.db.Sys_tagVO;

import lombok.Getter;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TaggerTest {

	private final static String pinUuid = "testPinName.jpeg";

	@Autowired
	private Tagger tagger;

	@Autowired
	private PinMapper pinMapper;

	private static HashSet<String> expectedSet;
	private static int index = 0;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private boolean isSameSet(Set<String> lhs, Set<String> rhs) {
		Predicate<String> capStartFilter = (p) -> (rhs.contains(p));
		boolean result = lhs.stream().allMatch(capStartFilter);
		if (lhs.size() == rhs.size() && result)
			return true;
		else
			return false;
	}

	/*
	 * @BeforeAll
	 * public void setup() {
	 * pinMapper.insertPin(new PinVO(pinUuid, "nicodes9912", 1, true));
	 * tagger.bindingRepresentKeywordsAtSession("nicodes9912");
	 * }
	 */

	@BeforeEach
	public void beforeEachMethod() {
		List<Set<String>> expectedSetList = new ArrayList<Set<String>>(
				Arrays.asList(new HashSet<String>(Set.of("dog")), new HashSet<String>(Set.of("dog", "flower"))));
		expectedSet = (HashSet<String>) expectedSetList.get(index++);
	}

	@ParameterizedTest
	@Transactional
	@MethodSource(value = { "getAppliedTagList" })
	public void doTest(List<String> appliedTagList, List<Sys_tagVO> systemTagList, String desPinName) {

		tagger.bindingRepresentKeywordsAtSession("nicodes9912");
		pinMapper.insertPin(new PinVO(pinUuid, "nicodes9912", 1, true));
		System.out.println("expected Value : " + expectedSet);
		/*
		 * Set<String> countingset = new HashSet<>();
		 * for (Sys_tagVO t : tagger.getTagAndSystagListMap().get("flower")) {
		 * countingset.add(t.getEngSysTag());
		 * }
		 * 
		 * for (String s : countingset) {
		 * System.out.println(s);
		 * System.out.println(
		 * Collections.frequency(tagger.getTagAndSystagListMap().get("flower"), new
		 * Sys_tagVO(s, .6f)));
		 * }
		 */

		boolean result = this.isSameSet(
				tagger.doAutoTagging(appliedTagList, systemTagList, desPinName),
				expectedSet);

		logger.info("test Reulst : " + result);
		assertTrue(result);
	}

	public static Stream<Arguments> getAppliedTagList() {

		TaggerTest.TaggerTestCaseContainer testcaseContainer = new TaggerTestCaseContainer();
		List<Arguments> argumentsList = new ArrayList<>();

		for (int idx = 0; idx < testcaseContainer.getFirstTestcaseArgumets().size(); idx++) {
			argumentsList.add(Arguments.of(testcaseContainer.getFirstTestcaseArgumets().get(idx),
					testcaseContainer.getSecondTestcaseArgumets().get(idx), pinUuid));
		}
		return argumentsList.stream();
	}

	@Getter
	static class TaggerTestCaseContainer {
		private List<List<String>> firstTestcaseArgumets;
		private List<List<Sys_tagVO>> secondTestcaseArgumets;

		public TaggerTestCaseContainer() {

			this.firstTestcaseArgumets = new ArrayList<List<String>>();
			this.secondTestcaseArgumets = new ArrayList<List<Sys_tagVO>>();

			this.firstTestcaseArgumets.add(
					new ArrayList<String>(Arrays.asList("$$empty$$", "dog", "false")));
			this.secondTestcaseArgumets.add(
					new ArrayList<Sys_tagVO>(Arrays.asList(new Sys_tagVO("apple", .2f), new Sys_tagVO("melon", .3f),
							new Sys_tagVO("banana", .5f))));

			this.firstTestcaseArgumets.add(
					new ArrayList<String>(Arrays.asList("$$empty$$", "dog", "flower", "false")));
			this.secondTestcaseArgumets.add(
					new ArrayList<Sys_tagVO>(Arrays.asList(new Sys_tagVO("apple", .2f), new Sys_tagVO("melon", .3f),
							new Sys_tagVO("banana", .5f), new Sys_tagVO("plant", .3f), new Sys_tagVO("flower", .3f))));
		}
	}

	@Getter
	static class TaggerBeforeTestCaseContainer {
		private List<Map<String, List<Sys_tagVO>>> tagAndSystagListMapList;

		public TaggerBeforeTestCaseContainer() {
			this.tagAndSystagListMapList = new ArrayList<>();
			this.tagAndSystagListMapList.add(this.getTagAndSystagListMap1());
			this.tagAndSystagListMapList.add(this.getTagAndSystagListMap2());
		}

		private Map<String, List<Sys_tagVO>> getTagAndSystagListMap1() {
			return new HashMap<String, List<Sys_tagVO>>(Map.of("dog", new ArrayList<Sys_tagVO>(Arrays.asList(
					new Sys_tagVO("pinName1", "systag1", null, .8f),
					new Sys_tagVO("pinName1", "systag2", null, .7f), new Sys_tagVO("pinName1", "systag3", null, .6f),
					new Sys_tagVO("pinName1", "systag4", null, .5f),
					new Sys_tagVO("pinName1", "systag5", null, .4f)))));
		}

		private Map<String, List<Sys_tagVO>> getTagAndSystagListMap2() {
			return new HashMap<String, List<Sys_tagVO>>(Map.of("cat",
					new ArrayList<Sys_tagVO>(Arrays.asList(new Sys_tagVO("pinName6", "cat", null, .8f),
							new Sys_tagVO("pinName7", "systag7", null, .7f),
							new Sys_tagVO("pinName8", "systag8", null, .6f),
							new Sys_tagVO("pinName9", "systag9", null, .5f),
							new Sys_tagVO("pinName10", "systag10", null, .4f)))));
		}
	}
}
