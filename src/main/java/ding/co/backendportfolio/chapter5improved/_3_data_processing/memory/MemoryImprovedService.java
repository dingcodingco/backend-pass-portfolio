package ding.co.backendportfolio.chapter5improved._3_data_processing.memory;

import ding.co.backendportfolio.chapter5.util.RandomStringUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemoryImprovedService {

    public List<String> executeImproved() {
        List<String> stringList1 = RandomStringUtil.generateRandomStringList(1000, 10000);
        List<String> stringList2 = RandomStringUtil.generateRandomStringList(1000, 10000);
        List<String> stringList3 = RandomStringUtil.generateRandomStringList(1000, 10000);
        List<String> stringList4 = RandomStringUtil.generateRandomStringList(1000, 10000);
        List<String> stringList5 = RandomStringUtil.generateRandomStringList(1000, 10000);

        return MemoryImprovedUtil.combineLists(stringList1, stringList2, stringList3, stringList4, stringList5);
    }
}
