import http from 'k6/http';

export const options = {
    vus: 1,
    iterations: 30
};

export default function () {

    const id = __ITER + 1;

    const payload = JSON.stringify({
        orderId: id,
        restaurantId: 100 + (id % 3),   // restaurantes 100,101,102
        userId: 1000 + id,
        rating: (id % 5) + 1            // notas de 1 a 5
    });

    http.post(
        'http://localhost:8080/reviews',
        payload,
        {
            headers: {
                'Content-Type': 'application/json'
            }
        }
    );
}