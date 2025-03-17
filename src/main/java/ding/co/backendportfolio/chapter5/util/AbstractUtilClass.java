package ding.co.backendportfolio.chapter5.util;

public abstract class AbstractUtilClass {

    // 유틸리티 클래스는 인스턴스화 방지
    protected AbstractUtilClass() {
        throw new UnsupportedOperationException("Cannot instantiate utility class");
    }
}
