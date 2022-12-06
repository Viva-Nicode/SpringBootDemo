package com.example.demo.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.lang.System.out;
import com.example.demo.db.Sys_tagMapper;
import com.example.demo.db.Sys_tagVO;
import com.example.demo.db.TagMapper;
import com.example.demo.db.TagVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class Tagger {

	private Map<String, List<Sys_tagVO>> tagAndSystagListMap;
	@Autowired
	private final Sys_tagMapper systemTagMapper;

	@Autowired
	private final TagMapper tagMapper;

	public Map<String, List<Sys_tagVO>> getTagAndSystagListMap() {
		return this.tagAndSystagListMap;
	}

	public void setTagmap(Map<String, List<Sys_tagVO>> m) {
		this.tagAndSystagListMap = m;
	}

	public void bindingRepresentKeywordsAtSession(String user_id) {
		List<TagMatchPinListInnerClass> l = tagMapper.getTagPinNameList(user_id);
		Map<String, List<String>> tagAndPinNameListMap = new HashMap<String, List<String>>();
		Map<String, List<Sys_tagVO>> tagAndSystagListMap = new HashMap<String, List<Sys_tagVO>>();

		for (TagMatchPinListInnerClass elem : l)
			tagAndPinNameListMap.put(elem.getTagName(), Arrays.asList(elem.getPinListString().split(",")));

		for (Map.Entry<String, List<String>> elem : tagAndPinNameListMap.entrySet())
			tagAndSystagListMap.put(elem.getKey(), systemTagMapper.getSystagListByPinNames(elem.getValue()));

		tagAndSystagListMap.remove("none");

		this.tagAndSystagListMap = tagAndSystagListMap;

		/*
		 * out.println("최초 맵 : ");
		 * for (Map.Entry<String, List<Sys_tagVO>> elem :
		 * tagAndSystagListMap.entrySet()) {
		 * out.println(elem.getKey());
		 * for (Sys_tagVO t : elem.getValue())
		 * out.println(t.getPinName() + " " + t.getEngSysTag());
		 * }
		 */

	}

	public void doAutoTagging(List<String> appliedTagList, List<Sys_tagVO> systemTagList, String desPinName) {
		HashSet<String> tagSet = new HashSet<>();
		Map<String, Boolean> agreementCounterMap = new HashMap<>();

		for (int idx = 1; idx < appliedTagList.size() - 1; idx++)
			tagSet.add(appliedTagList.get(idx));

		for (Map.Entry<String, List<Sys_tagVO>> elem : tagAndSystagListMap.entrySet()) {
			agreementCounterMap.clear();

			for (Sys_tagVO t : elem.getValue()) {
				if (!agreementCounterMap.containsKey(t.getPinName()))
					agreementCounterMap.put(t.getPinName(), false);

				if (systemTagList.stream()
						.filter(t1 -> t1.getEngSysTag().equals(t.getEngSysTag())).findFirst().isPresent())
					agreementCounterMap.put(t.getPinName(), true);
			}
			out.println(elem.getKey() + "와의 일치율은 ");
			double truesize = ((double) agreementCounterMap.entrySet().stream().filter(o -> o.getValue()).count());
			double allsize = ((double) agreementCounterMap.size());
			out.println("true : " + truesize);
			out.println("all : " + allsize);
			out.println("ratio : " + (double) (truesize / allsize));

			if (((double) agreementCounterMap.entrySet().stream().filter(o -> o.getValue()).count())
					/ ((double) agreementCounterMap.size()) >= (double) 0.8) {
				tagSet.add(elem.getKey());
			}
		}

		if (tagSet.isEmpty()) {
			List<String> l = new ArrayList<>();
			l.add(desPinName);
			tagMapper.insertTagNone(l);
		} else {
			List<TagVO> l = new ArrayList<>();
			for (String tag : tagSet) {
				l.add(new TagVO(desPinName, tag));
				if (!tagAndSystagListMap.containsKey(tag))
					tagAndSystagListMap.put(tag, new ArrayList<Sys_tagVO>());
				tagAndSystagListMap.get(tag).addAll(systemTagList);
			}
			tagMapper.insertTag(l);
		}

	}

	public static class TagMatchPinListInnerClass {
		private String tagName;
		private String pinListString;

		TagMatchPinListInnerClass(String tagName, String pinListString) {
			this.tagName = tagName;
			this.pinListString = pinListString;
		}

		public String getTagName() {
			return this.tagName;
		}

		public String getPinListString() {
			return this.pinListString;
		}
	}
}
