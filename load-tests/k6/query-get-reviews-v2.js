import http from 'k6/http';
import { check } from 'k6';

export const options = {
    scenarios: {
        query_reviews_v2: {
            executor: 'constant-arrival-rate',
            rate: 100,
            timeUnit: '1s',
            duration: '2m',
            preAllocatedVUs: 50,
            maxVUs: 300
        }
    },

    thresholds: {
        http_req_failed: ['rate<0.01'],
        http_req_duration: [
            'avg<50',
            'p(95)<100',
            'p(99)<200'
        ]
    }
};

const BASE_URL = 'http://localhost:8082';

export default function () {

    const restaurantId = 100;

    let cursor = null;

    // Percorre até 3 páginas simulando um usuário navegando
    for (let page = 0; page < 3; page++) {

        let url =
            `${BASE_URL}/restaurants/${restaurantId}/reviews/v2?size=20`;

        if (cursor) {
            url += `&lastCreatedAt=${encodeURIComponent(cursor)}`;
        }

        const res = http.get(url);

        check(res, {
            'status 200': (r) => r.status === 200,
        });

        const body = res.json();

        if (!body.hasNext) {
            break;
        }

        cursor = body.nextCursor;
    }
}