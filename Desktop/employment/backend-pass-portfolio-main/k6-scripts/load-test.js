import http from 'k6/http';
import { check, sleep } from 'k6';
import { SharedArray } from 'k6/data';
import { Rate } from 'k6/metrics';

// 에러율 메트릭 정의
const errorRate = new Rate('errors');

// 테스트 설정
export const options = {
  stages: [
    { duration: '2m', target: 100 }, // 2분동안 0->100 사용자로 증가
    { duration: '5m', target: 100 }, // 5분동안 100 사용자 유지
    { duration: '2m', target: 200 }, // 2분동안 100->200 사용자로 증가
    { duration: '5m', target: 200 }, // 5분동안 200 사용자 유지
    { duration: '2m', target: 300 }, // 2분동안 200->300 사용자로 증가
    { duration: '5m', target: 300 }, // 5분동안 300 사용자 유지
    { duration: '2m', target: 0 },   // 2분동안 0으로 감소
  ],
  thresholds: {
    http_req_duration: ['p(95)<2000'], // 95%의 요청이 2초 이내 완료
    'http_req_duration{type:search}': ['p(95)<3000'], // 검색은 3초
    errors: ['rate<0.1'], // 에러율 10% 미만
  },
};

// 랜덤 데이터 생성 함수
function getRandomInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

const BASE_URL = 'http://localhost:8080/api';

// 기본 헤더
const headers = {
  'Content-Type': 'application/json',
};

// 테스트 시나리오
export default function () {
  // 1. 로그인 (토큰 획득)
  const loginRes = http.post(`${BASE_URL}/auth/login`, JSON.stringify({
    email: `user${getRandomInt(1, 1000000)}@test.com`,
    password: 'password123',
  }), { headers });

  check(loginRes, {
    'login successful': (r) => r.status === 200,
  }) || errorRate.add(1);

  const token = loginRes.json('token');
  const authHeaders = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`,
  };

  // 2. 게시글 목록 조회 (페이징, 정렬)
  const listRes = http.get(`${BASE_URL}/boards?page=${getRandomInt(0, 1000)}&size=20&sort=createdAt,desc`, {
    headers: authHeaders,
    tags: { type: 'list' },
  });

  check(listRes, {
    'list successful': (r) => r.status === 200,
  }) || errorRate.add(1);

  // 3. 게시글 검색
  const searchRes = http.get(`${BASE_URL}/boards/search?keyword=Title${getRandomInt(1, 500000)}`, {
    headers: authHeaders,
    tags: { type: 'search' },
  });

  check(searchRes, {
    'search successful': (r) => r.status === 200,
  }) || errorRate.add(1);

  // 4. 게시글 상세 조회
  const detailRes = http.get(`${BASE_URL}/boards/${getRandomInt(1, 500000)}`, {
    headers: authHeaders,
    tags: { type: 'detail' },
  });

  check(detailRes, {
    'detail successful': (r) => r.status === 200,
  }) || errorRate.add(1);

  // 5. 태그별 게시글 조회
  const tagRes = http.get(`${BASE_URL}/boards/tags/tag${getRandomInt(1, 1000)}`, {
    headers: authHeaders,
    tags: { type: 'tag' },
  });

  check(tagRes, {
    'tag search successful': (r) => r.status === 200,
  }) || errorRate.add(1);

  // 6. 게시글 작성
  const createRes = http.post(`${BASE_URL}/boards`, JSON.stringify({
    title: `Load Test Title ${Date.now()}`,
    content: 'Load test content with some random text...',
    category: ['NOTICE', 'FREE', 'QUESTION', 'TECH'][getRandomInt(0, 3)],
    tagIds: [
      getRandomInt(1, 1000),
      getRandomInt(1, 1000),
    ],
  }), { headers: authHeaders, tags: { type: 'create' } });

  check(createRes, {
    'create successful': (r) => r.status === 201,
  }) || errorRate.add(1);

  // 7. 좋아요 토글
  const likeRes = http.post(
    `${BASE_URL}/boards/${getRandomInt(1, 500000)}/likes`,
    null,
    { headers: authHeaders, tags: { type: 'like' } }
  );

  check(likeRes, {
    'like successful': (r) => r.status === 200,
  }) || errorRate.add(1);

  // 8. 카테고리별 게시글 통계
  const statsRes = http.get(`${BASE_URL}/boards/stats`, {
    headers: authHeaders,
    tags: { type: 'stats' },
  });

  check(statsRes, {
    'stats successful': (r) => r.status === 200,
  }) || errorRate.add(1);

  sleep(1);
}

// 초기 체크 (테스트 시작 전 서버 상태 확인)
export function setup() {
  const res = http.get(`${BASE_URL}/health`);
  check(res, {
    'health check passed': (r) => r.status === 200,
  });
}

// 결과 처리
export function handleSummary(data) {
  return {
    'summary.json': JSON.stringify(data),
  };
} 