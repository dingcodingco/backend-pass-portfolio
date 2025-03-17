package ding.co.backendportfolio.chapter5improved._3_data_processing.memory;

import ding.co.backendportfolio.chapter5.util.AbstractUtilClass;

import java.util.ArrayList;
import java.util.List;

public class MemoryImprovedUtil extends AbstractUtilClass {

    /**
     * 여러 리스트를 하나의 리스트로 결합합니다. 초기 용량을 최적화하여 성능을 향상시킵니다.
     *
     * @param <T>   리스트의 요소 타입
     * @param lists 결합할 리스트들 (가변 인자)
     * @return 입력 리스트의 모든 요소를 포함하는 새로운 리스트
     */
    @SafeVarargs
    public static <T> List<T> combineLists(List<T>... lists) {
        // 총 크기를 미리 계산하여 초기 용량을 설정
        int totalSize = 0;
        for (List<T> list : lists) {
            totalSize += list.size();
        }

        // 계산된 용량으로 새로운 리스트 생성
        List<T> combinedList = new ArrayList<>(totalSize);

        // 입력 리스트의 모든 요소를 추가
        for (List<T> list : lists) {
            combinedList.addAll(list);
        }

        return combinedList;
    }
}
