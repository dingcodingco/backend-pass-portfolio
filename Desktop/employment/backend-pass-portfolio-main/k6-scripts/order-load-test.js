import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

const errorRate = new Rate('errors');
const BASE_URL = 'http://localhost:8080/api';

// 테스트 설정
export const options = {
  stages: [
    { duration: '1m', target: 50 },    // 1분동안 0->50 사용자
    { duration: '2m', target: 50 },    // 2분동안 50 사용자 유지
    { duration: '1m', target: 100 },   // 1분동안 50->100 사용자
    { duration: '3m', target: 100 },   // 3분동안 100 사용자 유지
    { duration: '1m', target: 0 },     // 1분동안 0으로 감소
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'], // 95%의 요청이 500ms 이내 처리
    errors: ['rate<0.1'],             // 에러율 10% 미만
  },
};

export default function () {
  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
  };

  // 1. 주문 목록 조회 (N+1 문제 발생 가능)
  const ordersResponse = http.get(
    `${BASE_URL}/orders?startDate=2024-01-01&endDate=2024-12-31&status=COMPLETED`,
    params
  );
  check(ordersResponse, {
    'orders list status is 200': (r) => r.status === 200,
    'orders response time < 200ms': (r) => r.timings.duration < 200,
  }) || errorRate.add(1);
  sleep(1);

  // 2. 주문번호로 검색 (인덱스 성능 테스트)
  const searchResponse = http.get(
    `${BASE_URL}/orders/search?orderNumber=ORD-2024`,
    params
  );
  check(searchResponse, {
    'search status is 200': (r) => r.status === 200,
    'search response time < 100ms': (r) => r.timings.duration < 100,
  }) || errorRate.add(1);
  sleep(1);

  // 3. 회원별 주문 통계 (집계 쿼리 성능)
  const statsResponse = http.get(
    `${BASE_URL}/orders/stats?minAmount=500000`,
    params
  );
  check(statsResponse, {
    'stats status is 200': (r) => r.status === 200,
    'stats response time < 300ms': (r) => r.timings.duration < 300,
  }) || errorRate.add(1);
  sleep(1);

  // 4. 복합 조건 검색 (인덱스 최적화 테스트)
  const complexSearchResponse = http.get(
    `${BASE_URL}/orders/complex-search?startDate=2024-01-01&status=COMPLETED&minAmount=100000`,
    params
  );
  check(complexSearchResponse, {
    'complex search status is 200': (r) => r.status === 200,
    'complex search response time < 400ms': (r) => r.timings.duration < 400,
  }) || errorRate.add(1);
  sleep(1);
}

// 결과 처리
export function handleSummary(data) {
  return {
    'order-test-summary.json': JSON.stringify(data),
  };
} 