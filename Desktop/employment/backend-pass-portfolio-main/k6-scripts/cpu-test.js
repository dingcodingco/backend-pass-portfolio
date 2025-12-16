import http from 'k6/http';
import { check, sleep } from 'k6';

// 환경변수에서 IP 주소를 가져옴 (기본값 설정)
const HOST = __ENV.HOST || 'localhost';
const BASE_URL = `http://${HOST}:8080`;

export const options = {
  stages: [
    { duration: '10m', target: 50 },  // 동시 사용자 수 감소
    { duration: '10m', target: 10 },  // 부하 유지
    { duration: '10m', target: 0 },   // 정리
  ]
};

export default function () {
  // 요청별 타임아웃 설정
  const params = {
    timeout: '30s',  // 30초 타임아웃
  };
  
  // CPU 집약적인 작업 실행 (파라미터 감소)
  const heavyRes = http.get(`${BASE_URL}/api/chapter2/test/cpu/heavy/2`, params);
  check(heavyRes, { 'heavy CPU task status was 200': (r) => r.status === 200 });

  // 피보나치 수 감소
  const fibRes = http.get(`${BASE_URL}/api/chapter2/test/fibonacci/40`, params);
  check(fibRes, { 'fibonacci status was 200': (r) => r.status === 200 });

  // 소수 범위 감소
  const primeRes = http.get(`${BASE_URL}/api/chapter2/test/prime/50000`, params);
  check(primeRes, { 'prime status was 200': (r) => r.status === 200 });

  sleep(2); // 대기 시간 증가
} 