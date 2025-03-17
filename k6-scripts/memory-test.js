import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '30s', target: 10 },
    { duration: '1m', target: 10 },
    { duration: '30s', target: 0 },
  ],
};

export default function () {
  const baseUrl = 'http://app:8080';
  
  // 큰 객체 생성
  const largeObjectRes = http.get(`${baseUrl}/api/chapter2/test/memory/large-object/50`);
  check(largeObjectRes, { 'large object status was 200': (r) => r.status === 200 });

  // 메모리 누수 시뮬레이션
  const leakRes = http.get(`${baseUrl}/api/chapter2/test/memory/leak/10`);
  check(leakRes, { 'memory leak status was 200': (r) => r.status === 200 });

  // 많은 객체 생성
  const objectsRes = http.get(`${baseUrl}/api/chapter2/test/memory/objects/10000`);
  check(objectsRes, { 'objects status was 200': (r) => r.status === 200 });

  // 큰 문자열 생성
  const stringRes = http.get(`${baseUrl}/api/chapter2/test/memory/string/1000`);
  check(stringRes, { 'string status was 200': (r) => r.status === 200 });

  sleep(1);
} 