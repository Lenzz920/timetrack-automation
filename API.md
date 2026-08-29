# CREATE
curl -i -X POST http://localhost:3000/api/sessions -H "Content-Type: application/json" -d '{"date":"2026-08-29","start":"16:00","end":"17:00","categoryId":"study","note":"x"}'

# READ (everything)
curl -s http://localhost:3000/api/data

# UPDATE
curl -i -X PUT http://localhost:3000/api/sessions/<id> -H "Content-Type: application/json" -d '{"date":"2026-08-29","start":"16:00","end":"18:00","categoryId":"work","note":"x"}'

# DELETE
curl -i -X DELETE http://localhost:3000/api/sessions/<id>

# is an id still present?
curl -s http://localhost:3000/api/data | grep -c "<id>"