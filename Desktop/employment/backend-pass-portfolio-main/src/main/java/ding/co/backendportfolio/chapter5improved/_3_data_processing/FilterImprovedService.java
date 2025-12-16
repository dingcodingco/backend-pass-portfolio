package ding.co.backendportfolio.chapter5improved._3_data_processing;

import ding.co.backendportfolio.chapter5._3_data_processing.Post;
import ding.co.backendportfolio.chapter5._3_data_processing.SensitiveWordCheckApiClient;
import ding.co.backendportfolio.chapter5._3_data_processing.SentimentAnalysisApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FilterImprovedService {

    private final SentimentAnalysisApiClient sentimentAnalysisApiClient;
    private final SensitiveWordCheckApiClient sensitiveWordCheckApiClient;

    /**
     * 1. 포스트 타입 체크: 비용 1, 선택도 0.33 => FilterOverhead 0.33
     * 2. 부정적 단어 포함 체크: 비용 10, 선택도 0.33 => FilterOverhead 3.3
     * 3. 감정 분석: 비용 10, 선택도 0.5 => FilterOverhead 5
     */
    public void goodFilter(List<Post> posts) {
        posts.stream()
                .filter(post -> post.isVideoType())
                .filter(post -> sensitiveWordCheckApiClient.containsNegativeWords(post))
                .filter(post -> sentimentAnalysisApiClient.isPositiveSentiment(post))
                .collect(Collectors.toList());
    }
}
