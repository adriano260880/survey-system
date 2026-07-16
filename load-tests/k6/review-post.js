import http from 'k6/http';
import { check } from 'k6';

export const options = {
    vus: 50,
    duration: '30s',
};

export default function () {

    const payload = JSON.stringify({
        orderId: Date.now() + __VU * 100000 + __ITER,
        restaurantId: 100,
        userId: __VU,
        rating: 5
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const res = http.post(
        'http://localhost:8080/reviews',
        payload,
        params
    );

    check(res, {
        'status is 201': (r) => r.status === 201,
    });
}