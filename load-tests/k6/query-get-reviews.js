import http from 'k6/http';
import { check } from 'k6';

export const options = {
    scenarios: {
        reviews_read: {
            executor: 'constant-arrival-rate',
            rate: 500,
            timeUnit: '1s',
            duration: '2m',
            preAllocatedVUs: 100,
            maxVUs: 500
        }
    },

    thresholds: {
        http_req_failed: ['rate<0.01'],
        http_req_duration: [
            'p(95)<80',
            'p(99)<150'
        ]
    }
};

const BASE_URL = 'http://localhost:8082';

export default function () {

    const restaurantId = 100 + Math.floor(Math.random() * 10);

    const res = http.get(
        `${BASE_URL}/restaurants/${restaurantId}/reviews?page=0&size=20`
    );

    check(res, {
        'status is 200': (r) => r.status === 200,
    });
}