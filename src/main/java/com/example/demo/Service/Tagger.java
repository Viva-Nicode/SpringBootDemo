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
		/* 최종적으로 업로드된 핀에 삽입될 태그들이 담길 집합 */
		HashSet<String> tagSet = new HashSet<>();

		/* 업로드된 핀은 어떤핀과 일치한다, 일치하지않는다 정보를 담는 맵 */
		Map<String, Boolean> agreementCounterMap = new HashMap<>();

		/* 사용자가 직접 입력한 태그들을 태그셋에 담는다. 셋에 담기 때문에 오토태깅조건만족으로 동일한 태그가 삽입되더라도 중복은 제거된다. */
		for (int idx = 1; idx < appliedTagList.size() - 1; idx++)
			tagSet.add(appliedTagList.get(idx));

		for (Map.Entry<String, List<Sys_tagVO>> elem : tagAndSystagListMap.entrySet()) {
			agreementCounterMap.clear();

			/* 특정 태그가 몇개의 핀에 삽입되어 있는지 알아내기 위한 집합 */
			HashSet<String> pinCountSet = new HashSet<>();

			/* 핀 이름을 집합에 전부 넣어서 중복 제거 */
			elem.getValue().forEach((Sys_tagVO t) -> {
				pinCountSet.add(t.getEngSysTag());
			});

			/* 특정태그가 삽입된 핀의 갯수가 10개 이하라면 데이터가 너무 적다고 판단하고 해당 태그는 오토태깅의 대상이 되지 않는다. */
			if (pinCountSet.size() <= 10)
				continue;

			for (Sys_tagVO t : elem.getValue()) {
				/* 일단 false로 넣어주고 */
				if (!agreementCounterMap.containsKey(t.getPinName()))
					agreementCounterMap.put(t.getPinName(), false);

				/* 업로드된 핀의 시스템태그들과 비교하며 일치하는경우에만 true로 바꿔준다. 일치하지않으면 false로 유지된다. */
				if (systemTagList.stream()
						.filter(t1 -> t1.getEngSysTag().equals(t.getEngSysTag())).findFirst().isPresent())
					agreementCounterMap.put(t.getPinName(), true);
			}

			/* 일치해서 true가 되버린 핀의 비율을 계산해서 0.8이상이면 오토태강된다. */
			if (((double) agreementCounterMap.entrySet().stream().filter(o -> o.getValue()).count())
					/ ((double) agreementCounterMap.size()) >= (double) 0.8) {
				tagSet.add(elem.getKey());
			}
		}

		/* 삽입될 태그가 하나도 없으면 none삽입 */
		if (tagSet.isEmpty()) {
			List<String> l = new ArrayList<>();
			l.add(desPinName);
			tagMapper.insertTagNone(l);
		} else {
			/* 업로드된 핀에 태그들을 삽입해준다. */
			/* 동시에 전에없던 새로운 태그인경우 tagAndSystagListMap에 해당태그를 업데이트해준다. */
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
