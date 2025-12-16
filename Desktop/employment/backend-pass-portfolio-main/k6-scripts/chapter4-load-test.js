import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

const errorRate = new Rate('errors');
const BASE_URL = 'http://app:8080/api';

export const options = {
  stages: [
    { duration: '1m', target: 50 },    // 1분동안 0->50 사용자
    { duration: '2m', target: 50 },    // 2분동안 50 사용자 유지
    { duration: '1m', target: 100 },   // 1분동안 50->100 사용자
    { duration: '3m', target: 100 },   // 3분동안 100 사용자 유지
    { duration: '1m', target: 0 },     // 1분동안 100->0 사용자
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'], // 95%의 요청이 500ms 이내 처리
    'http_req_duration{type:order-search}': ['p(95)<400'],
    'http_req_duration{type:product-search}': ['p(95)<300'],
    errors: ['rate<0.1'], // 에러율 10% 미만
  },
};

const PRODUCTS_PER_PAGE = 10;
const ORDERS_PER_PAGE = 20;

export default function () {
  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
    tags: { type: 'order-search' },
  };

  // 1. 주문 목록 조회 (페이징)
  const pageNum = Math.floor(Math.random() * 50); // 0-49 페이지
  const ordersResponse = http.get(
    `${BASE_URL}/orders?page=${pageNum}&size=${ORDERS_PER_PAGE}`,
    params
  );
  check(ordersResponse, {
    'orders status is 200': (r) => r.status === 200,
    'orders response time < 500ms': (r) => r.timings.duration < 500,
  }) || errorRate.add(1);
  sleep(1);

  // 2. 주문 상세 조회
  const orderId = 1 + Math.floor(Math.random() * 1000000); // 1-1000000
  const orderDetailResponse = http.get(
    `${BASE_URL}/orders/${orderId}`,
    params
  );
  check(orderDetailResponse, {
    'order detail status is 200': (r) => r.status === 200,
    'order detail response time < 400ms': (r) => r.timings.duration < 400,
  }) || errorRate.add(1);
  sleep(1);

  // 3. 상품 검색 (카테고리별)
  const categories = ['ELECTRONICS', 'CLOTHING', 'BOOKS', 'FOOD', 'OTHERS'];
  const randomCategory = categories[Math.floor(Math.random() * categories.length)];
  params.tags.type = 'product-search';
  
  const productsResponse = http.get(
    `${BASE_URL}/products?category=${randomCategory}&page=${Math.floor(Math.random() * 50)}&size=${PRODUCTS_PER_PAGE}`,
    params
  );
  check(productsResponse, {
    'products status is 200': (r) => r.status === 200,
    'products response time < 300ms': (r) => r.timings.duration < 300,
  }) || errorRate.add(1);
  sleep(1);

  // 4. 주문 상태별 검색
  const statuses = ['COMPLETED', 'PROCESSING', 'CANCELLED', 'PENDING'];
  const randomStatus = statuses[Math.floor(Math.random() * statuses.length)];
  params.tags.type = 'order-search';
  
  const ordersByStatusResponse = http.get(
    `${BASE_URL}/orders/status/${randomStatus}?page=${Math.floor(Math.random() * 50)}&size=${ORDERS_PER_PAGE}`,
    params
  );
  check(ordersByStatusResponse, {
    'orders by status is 200': (r) => r.status === 200,
    'orders by status response time < 400ms': (r) => r.timings.duration < 400,
  }) || errorRate.add(1);
  sleep(1);

  // 5. 기간별 주문 검색
  const startDate = '2023-01-01';
  const endDate = '2024-12-31';
  const ordersByDateResponse = http.get(
    `${BASE_URL}/orders/period?startDate=${startDate}&endDate=${endDate}&page=${Math.floor(Math.random() * 50)}&size=${ORDERS_PER_PAGE}`,
    params
  );
  check(ordersByDateResponse, {
    'orders by date status is 200': (r) => r.status === 200,
    'orders by date response time < 400ms': (r) => r.timings.duration < 400,
  }) || errorRate.add(1);
  sleep(1);
} 