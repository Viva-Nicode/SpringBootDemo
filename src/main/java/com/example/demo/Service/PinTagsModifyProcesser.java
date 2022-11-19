package com.example.demo.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.example.demo.db.TagVO;

public class PinTagsModifyProcesser {
	public static Map<String, List<TagVO>> processingModification(Map<String, List<String>> modifiedPinTags,
			List<TagVO> beforeModifiedtagList) {

		/* 수정되기 이전의 key 핀이름 : value 핀에 붙어있는 태그들 */
		Map<String, HashSet<TagVO>> beforeModifiedProcesserMap = new HashMap<String, HashSet<TagVO>>();

		/* insert와 delete 결과 쿼리들을 담기위함 */
		Map<String, List<TagVO>> resultMap = new HashMap<>();
		resultMap.put("insertQueryList", new ArrayList<TagVO>());
		resultMap.put("deleteQueryList", new ArrayList<TagVO>());

		/* 수정되기 이전의 (핀 : 태그들)인 beforeModifiedtagList를 쓰기편하게 가공한다 */
		for (TagVO t : beforeModifiedtagList) {
			if (beforeModifiedProcesserMap.containsKey(t.getPinName())) {
				beforeModifiedProcesserMap.get(t.getPinName()).add(new TagVO(t.getTagName(), t.getTagid()));
			} else {
				beforeModifiedProcesserMap.put(t.getPinName(), new HashSet<TagVO>());
				beforeModifiedProcesserMap.get(t.getPinName()).add(new TagVO(t.getTagName(), t.getTagid()));
			}
		}

		List<String> afterModifiedcommonTagList = modifiedPinTags.get("commonTagList");
		System.out.println(afterModifiedcommonTagList);
		/*
		 * List<String> afterModifiedComplementTaglist =
		 * modifiedPinTags.get("complementTaglist");
		 * System.out.println(afterModifiedComplementTaglist);
		 */
		List<String> concatList = new ArrayList<>();
		concatList.addAll(modifiedPinTags.get("complementTaglist"));
		concatList.addAll(afterModifiedcommonTagList);

		for (String pinName : beforeModifiedProcesserMap.keySet()) {

			HashSet<TagVO> originalTagSet = beforeModifiedProcesserMap.get(pinName);/* pinName에 들어있는 태그set */

			for (String t : afterModifiedcommonTagList) {
				if (originalTagSet.add(new TagVO(pinName, t))) /* 새로 추가된 경우 */
					resultMap.get("insertQueryList").add(new TagVO(pinName, t));
			}

			for (TagVO t : originalTagSet) {
				if (!(concatList.contains(t.getTagName())))
					resultMap.get("deleteQueryList").add(t);
			}
		}
		return resultMap;
	}
}
