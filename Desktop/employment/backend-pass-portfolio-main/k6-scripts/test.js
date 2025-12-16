import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '30s', target: 20 },
    { duration: '1m', target: 20 },
    { duration: '30s', target: 0 },
  ],
};

export default function () {
  // app은 docker-compose.yml의 서비스 이름
  const baseUrl = 'http://app:8080';
  
  // 피보나치
  const fibRes = http.get(`${baseUrl}/api/chapter2/test/fibonacci/45`);
  check(fibRes, { 'fibonacci status was 200': (r) => r.status === 200 });

  // 소수 찾기
  const primeRes = http.get(`${baseUrl}/api/chapter2/test/prime/10000`);
  check(primeRes, { 'prime status was 200': (r) => r.status === 200 });

  // 행렬 곱셈
  const matrixRes = http.get(`${baseUrl}/api/chapter2/test/matrix/100`);
  check(matrixRes, { 'matrix status was 200': (r) => r.status === 200 });

  // 정렬
  const sortRes = http.get(`${baseUrl}/api/chapter2/test/sort/100000`);
  check(sortRes, { 'sort status was 200': (r) => r.status === 200 });

  sleep(1);
} 