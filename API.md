# CREATE
curl -i -X POST http://localhost:3000/api/sessions -H "Content-Type: application/json" -d '{"date":"2026-08-29","start":"16:00","end":"17:00","categoryId":"study","note":"x"}'

# READ 
curl -s http://localhost:3000/api/data //everything
curl -s http://localhost:3000/api/data

# UPDATE
curl -i -X PUT http://localhost:3000/api/sessions/<id> -H "Content-Type: application/json" -d '{"date":"2026-08-29","start":"16:00","end":"18:00","categoryId":"work","note":"x"}'

# DELETE
curl -i -X DELETE http://localhost:3000/api/sessions/<id>

# is an id still present?
curl -s http://localhost:3000/api/data | grep -c "<id>"

# get cookie and pass it ot a request
curl -c jar.txt -X POST http://localhost:3000/api/auth/login \
-H "Content-Type: application/json" \
-d '{"username":"test1","password":"1234"}'

curl -b jar.txt -i -X ...


curl -c jar.txt -X POST http://localhost:3000/api/auth/login \
-H "Content-Type: application/json" \
-d '{"username":"test1","password":"1234"}'

curl -b jar.txt -i -X POST http://localhost:3000/api/sessions \
-H "Content-Type: application/json" \
-d '{"date": "2026-09-09", "start": "09:00", "end": "02:00", "categoryName": "Study", "note": "invalid session"}'

curl -b jar.txt -i -X PUT http://localhost:3000/api/sessions/smty4jjpapjb3an \
-H "Content-Type: application/json" \
-d '{"date":"2026-09-09","start":"09:00","end":"02:00","categoryName":"Study","note":"put probe"}'

curl -b jar.txt -s http://localhost:3000/api/data

