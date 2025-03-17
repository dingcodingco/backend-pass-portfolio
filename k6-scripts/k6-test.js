import http from 'k6/http';
import {check, sleep} from 'k6';

// 부하 테스트 설정
export let options = {
    vus: 10, // 동시에 접속할 가상 유저 수
    duration: '10m', // 테스트 지속 시간
};

export default function () {
    let res = http.get('http://localhost:8080/api/chapter2/test/algorithm/string/concat?iterations=50000');
    check(res, {
        '응답 코드가 200인가?': (r) => r.status === 200,
    });
    sleep(1);
}
