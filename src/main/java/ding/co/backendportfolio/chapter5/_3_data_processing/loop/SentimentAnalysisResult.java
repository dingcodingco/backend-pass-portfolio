package ding.co.backendportfolio.chapter5._3_data_processing.loop;

import lombok.Getter;

@Getter
public class SentimentAnalysisResult {
    private final Long postId;
    private final Sentiment sentiment;

    public SentimentAnalysisResult(Long postId, Sentiment sentiment) {
        this.postId = postId;
        this.sentiment = sentiment;
    }
}
