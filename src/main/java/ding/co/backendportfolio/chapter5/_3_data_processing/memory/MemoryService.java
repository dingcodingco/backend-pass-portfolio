package ding.co.backendportfolio.chapter5._3_data_processing.memory;

import ding.co.backendportfolio.chapter5.util.RandomStringUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MemoryService {

    public List<String> execute() {
        List<String> stringList1 = RandomStringUtil.generateRandomStringList(1000, 10000);
        List<String> stringList2 = RandomStringUtil.generateRandomStringList(1000, 10000);
        List<String> stringList3 = RandomStringUtil.generateRandomStringList(1000, 10000);
        List<String> stringList4 = RandomStringUtil.generateRandomStringList(1000, 10000);
        List<String> stringList5 = RandomStringUtil.generateRandomStringList(1000, 10000);

        // 초기 사이즈 지정 X
        List<String> result = new ArrayList<>();
        result.addAll(stringList1);
        result.addAll(stringList2);
        result.addAll(stringList3);
        result.addAll(stringList4);
        result.addAll(stringList5);

        return result;
    }
}
