package ding.co.backendportfolio.chapter5._3_data_processing.loop;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ExternalApiClient {

    public List<SentimentAnalysisResult> analyzeSentiments(List<Post> posts) {
        return posts.stream()
                .map(post -> new SentimentAnalysisResult(post.getId(), Sentiment.getByRandom()))
                .collect(Collectors.toList());
    }
}
